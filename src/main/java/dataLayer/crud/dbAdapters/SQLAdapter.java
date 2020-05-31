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
import org.jooq.impl.DSL;

import java.util.Collection;
import java.util.List;
import java.util.Map;
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
@SuppressWarnings("JavadocReference")
public class SQLAdapter extends DatabaseAdapter
{
	SQLAdapter()
	{
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
							.collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
					final Object uuid = fieldsAndValues.remove("uuid");
					return uuid instanceof String ?
					       new Entity((String) uuid, entityType, fieldsAndValues, FRIEND) :
					       new Entity((UUID) uuid, entityType, fieldsAndValues, FRIEND);
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
		try (DSLContext connection = using(fieldsMapping.getConnStr()))
		{
			return getEntityFromResult(entityType,
					connection.selectFrom(entityType)
							.where(filter)
							.fetch());
		}
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
		try (DSLContext connection = using(fieldsMapping.getConnStr()))
		{
			connection.insertInto(table(entityType), fieldsAndValues.keySet().stream().map(DSL::field).collect(toList()))
					.values(fieldsAndValues.values())
					.execute();
		}
	}

	@Override
	protected Stream<Entity> makeEntities(FieldsMapping fieldsMapping, String entityType)
	{
		try (DSLContext connection = using(fieldsMapping.getConnStr()))
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
		return makeEntities(fieldsMapping, entityType, field("uuid").eq(uuid));
	}

	@Override
	public void executeDelete(FieldsMapping fieldsMapping, Map<String, Collection<UUID>> typesAndUuids, Query.Friend friend)
	{
		try (DSLContext connection = using(fieldsMapping.getConnStr()))
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
		try (DSLContext connection = using(fieldsMapping.getConnStr()))
		{
			updates.forEach((entityType, uuidsAndUpdates) ->
			{
				if (!uuidsAndUpdates.getSecond().isEmpty())
				{
					final List<Map.Entry<String, Object>> toUpdate = uuidsAndUpdates.getSecond().entrySet().stream()
							.peek(fieldAndValue -> fieldAndValue.setValue(validateAndTransformEntity(entityType, fieldAndValue.getKey(), fieldAndValue.getValue())))
							.collect(toList());
					connection.update(table(entityType))
							.set(row(toUpdate.stream()
											.map(entry -> field(entry.getKey()))
											.collect(toList())),
									row(toUpdate.stream()
											.map(Map.Entry::getValue)
											.collect(toList())))
							.where(field("uuid").in(uuidsAndUpdates.getFirst()))
							.execute();
				}
			});
		}
	}
}
