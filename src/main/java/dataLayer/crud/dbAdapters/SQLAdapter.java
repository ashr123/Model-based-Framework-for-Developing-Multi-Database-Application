package dataLayer.crud.dbAdapters;

import dataLayer.crud.Entity;
import dataLayer.crud.Pair;
import dataLayer.crud.Query;
import dataLayer.crud.filters.*;
import dataLayer.readers.configReader.Conf;
import dataLayer.readers.configReader.FieldsMapping;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;

import java.io.*;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.jooq.impl.DSL.*;

/**
 * This class deals with CRUD operations on relational DBs
 *
 * @author Roy Ash
 */
@SuppressWarnings({"JavadocReference", "SimplifyStreamApiCallChains"})
public class SQLAdapter extends DatabaseAdapter
{
	SQLAdapter()
	{
	}

	private static byte[] objectToBytes(final Serializable obj) throws IOException
	{
		try (final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		     final ObjectOutputStream oos = new ObjectOutputStream(bos))
		{
			oos.writeObject(obj);
			return bos.toByteArray();
		}
	}

	private static Object bytesToObject(final byte[] data) throws ClassNotFoundException, IOException
	{
		try (final ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(data)))
		{
			return objectInputStream.readObject();
		}
	}

	/**
	 * Traverse on the result given by the relevant relational DB driver and transforms each result to {@link Map} of fields and values to be inserted into {@link Entity}
	 *
	 * @param entityType the type of the created entities
	 * @param result     the result given by MongoDB driver
	 * @return {@link Stream} of {@link Entity}s
	 * @see SQLAdapter#makeEntities(FieldsMapping, String)
	 * @see SQLAdapter#makeEntities(FieldsMapping, String, Condition)
	 */
	private static Stream<Entity> getEntityFromResult(String entityType, Result<Record> result)
	{
		return result.stream()
				.map(record ->
				{
					final Map<String, Object> fieldsAndValues = record.intoMap().entrySet().stream()
							.filter(fieldAndValue -> fieldAndValue.getValue() != null)
							.peek(fieldAndValue ->
							{
								if (fieldAndValue.getValue() instanceof byte[])
									try
									{
										fieldAndValue.setValue(bytesToObject((byte[]) fieldAndValue.getValue()));
									}
									catch (ClassNotFoundException | IOException e) // Doesn't suppose to happen
									{
										e.printStackTrace();
									}
							})
							.collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
					final Object uuid = fieldsAndValues.remove("uuid");
					return /*uuid instanceof String ?*/
					       new Entity((String) uuid, entityType, fieldsAndValues, FRIEND)/* :
					       new Entity((UUID) uuid, entityType, fieldsAndValues, FRIEND)*/;
				});
	}

	/**
	 * Queries relational DB with given {@link Entity#entityType} as collection name
	 *
	 * @param fieldsMapping gives the necessary details about the connection such as {@link FieldsMapping#connStr} and {@link FieldsMapping#location}
	 * @param entityType    is practically {@link Entity#entityType}
	 * @param filter        upon which MongoDB returns the relevant results
	 * @return flat, partial entities according to the given parameters
	 * @see SQLAdapter#queryRead(SimpleFilter, Condition)
	 */
	private static Stream<Entity> makeEntities(FieldsMapping fieldsMapping, String entityType, Condition filter)
	{
		try (DSLContext connection = getConnection(fieldsMapping))
		{
			return getEntityFromResult(entityType,
					connection.selectFrom(entityType)
							.where(filter)
							.fetch());
		}
	}

	private static DSLContext getConnection(FieldsMapping fieldsMapping)
	{
		return fieldsMapping.getUsername() == null ?
		       using(fieldsMapping.getConnStr()) :
		       using(fieldsMapping.getConnStr(), fieldsMapping.getUsername(), fieldsMapping.getPassword());
	}

	/**
	 * General adapter for all {@link SimpleFilter}s
	 *
	 * @param simpleFilter which gives us the relevant {@link FieldsMapping} for the given {@link Entity#entityType} and it's field
	 * @param filter       the filter upon MongoDB will filter its result
	 * @return flat, partial entities according to the given parameters
	 * @see SQLAdapter#executeRead(Eq, Query.Friend)
	 * @see SQLAdapter#executeRead(Gt, Query.Friend)
	 * @see SQLAdapter#executeRead(Gte, Query.Friend)
	 * @see SQLAdapter#executeRead(Lt, Query.Friend)
	 * @see SQLAdapter#executeRead(Ne, Query.Friend)
	 * @see SQLAdapter#executeRead(Lte, Query.Friend)
	 */
	private static Stream<Entity> queryRead(SimpleFilter simpleFilter, Condition filter)
	{
		return makeEntities(Conf.getFieldsMappingFromEntityField(simpleFilter.getEntityType(), simpleFilter.getFieldName()), simpleFilter.getEntityType(), filter);
	}

	@Override
	protected void executeCreate(FieldsMapping fieldsMapping, String entityType, Map<String, Object> fieldsAndValues)
	{
		try (DSLContext connection = getConnection(fieldsMapping))
		{
			connection.insertInto(table(entityType),
					fieldsAndValues.entrySet().stream()
							.map(fieldAndValue -> field(fieldAndValue.getKey()))
							.collect(toList()))
					.values(fieldsAndValues.entrySet().stream()
							.map(fieldAndValue -> serializeIfNeeded(fieldAndValue.getValue()))
							.collect(toList()))
					.execute();
//			connection.insertInto(getTable(connection, entityType))
//					.set(fieldsAndValues.entrySet().stream()
//							.peek(fieldAndValue ->
//							{
//								if (fieldAndValue.getValue() instanceof Collection<?>)
//									try
//									{
//										fieldAndValue.setValue(objectToBytes((Serializable) fieldAndValue.getValue()));
//									}
//									catch (IOException e) // Doesn't suppose to happen
//									{
//										e.printStackTrace();
//									}
//							})
//							.collect(toMap(Map.Entry::getKey, Map.Entry::getValue)))
//					.execute();
		}
	}

	private static Object serializeIfNeeded(Object value)
	{
		if (value instanceof Set<?> && value instanceof Serializable)
			try
			{
				return objectToBytes((Serializable) value);
			}
			catch (IOException e) // Doesn't suppose to happen
			{
				e.printStackTrace();
				return null;
			}
		return value;
	}

	@Override
	protected Stream<Entity> makeEntities(FieldsMapping fieldsMapping, String entityType)
	{
		try (DSLContext connection = getConnection(fieldsMapping))
		{
			return getEntityFromResult(entityType,
					connection.selectFrom(entityType)
							.fetch());
		}
	}

	@Override
	public Stream<Entity> executeRead(Eq eq, Query.Friend friend)
	{
		return queryRead(eq, field(eq.getFieldName()).eq(eq.getValue()));
	}

	@Override
	public Stream<Entity> executeRead(Ne ne, Query.Friend friend)
	{
		return queryRead(ne, field(ne.getFieldName()).ne(ne.getValue()));
	}

	@Override
	public Stream<Entity> executeRead(Gt gt, Query.Friend friend)
	{
		return queryRead(gt, field(gt.getFieldName()).gt(gt.getValue()));
	}

	@Override
	public Stream<Entity> executeRead(Lt lt, Query.Friend friend)
	{
		return queryRead(lt, field(lt.getFieldName()).lt(lt.getValue()));
	}

	@Override
	public Stream<Entity> executeRead(Gte gte, Query.Friend friend)
	{
		return queryRead(gte, field(gte.getFieldName()).ge(gte.getValue()));
	}

	@Override
	public Stream<Entity> executeRead(Lte lte, Query.Friend friend)
	{
		return queryRead(lte, field(lte.getFieldName()).le(lte.getValue()));
	}

	@Override
	protected Stream<Entity> executeRead(FieldsMapping fieldsMapping, UUID uuid, String entityType)
	{
		return makeEntities(fieldsMapping, entityType, field("uuid", UUID.class).eq(uuid));
	}

	@Override
	public void executeDelete(FieldsMapping fieldsMapping, Map<String, Collection<UUID>> typesAndUuids, Query.Friend friend)
	{
		try (DSLContext connection = getConnection(fieldsMapping))
		{
			typesAndUuids.forEach((entityType, uuids) ->
					connection.deleteFrom(table(entityType))
							.where(field("uuid").in(uuids))
							.execute());
		}
	}

	@Override
	public void executeUpdate(FieldsMapping fieldsMapping, Map<String, Pair<Collection<UUID>, Map<String, Object>>> updates, Query.Friend friend)
	{
		try (DSLContext connection = getConnection(fieldsMapping))
		{
			updates.forEach((entityType, uuidsAndUpdates) ->
			{
				if (!uuidsAndUpdates.getSecond().isEmpty())
				{
					connection.update(table(entityType))
							.set(row(uuidsAndUpdates.getSecond().entrySet().stream()
											.map(entry -> field(entry.getKey()))
											.collect(toList())),
									row(uuidsAndUpdates.getSecond().entrySet().stream()
											.map(fieldAndValue -> serializeIfNeeded(validateAndTransformEntity(entityType, fieldAndValue.getKey(), fieldAndValue.getValue())))
											.collect(toList())))
							.where(field("uuid").in(uuidsAndUpdates.getFirst()))
							.execute();

//					connection.update(getTable(connection, entityType))
//							.set(uuidsAndUpdates.getSecond().entrySet().stream()
//									.peek(fieldAndValue -> fieldAndValue.setValue(validateAndTransformEntity(entityType, fieldAndValue.getKey(), fieldAndValue.getValue()))) // need to be fixed with SerializeIfNeededTo
//									.collect(toMap(Map.Entry::getKey, Map.Entry::getValue)))
//							.where(field("uuid").in(uuidsAndUpdates.getFirst()))
//							.execute();
				}
			});
		}
	}
}
