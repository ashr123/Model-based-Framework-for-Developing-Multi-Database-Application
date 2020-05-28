package dataLayer.crud.dbAdapters;

import dataLayer.crud.Entity;
import dataLayer.crud.Pair;
import dataLayer.crud.Query;
import dataLayer.crud.filters.*;
import dataLayer.readers.configReader.Conf;
import dataLayer.readers.configReader.FieldsMapping;
import org.jooq.*;
import org.jooq.Record;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;
import static org.jooq.impl.DSL.using;

/**
 * @author Roy Ash
 */
public class SQLAdapter extends DatabaseAdapter
{
	SQLAdapter()
	{
	}

	private static Stream<Entity> getEntityFromResult(String entityType, Result<Record> result)
	{
		return result.stream()
				.map(Record::intoMap)
				.map(map -> new Entity((String) map.remove("uuid"), entityType, map, FRIEND));
	}

	@Override
	protected void executeCreate(FieldsMapping fieldsMapping, String entityType, Map<String, Object> fieldsAndValues)
	{
		try (DSLContext connection = using(fieldsMapping.getConnStr()))
		{
			connection.insertInto(table(entityType)).set(fieldsAndValues);
		}
	}

	private static Stream<Entity> makeEntities(FieldsMapping fieldsMapping, String entityType, Condition filter)
	{
		try (DSLContext connection = using(fieldsMapping.getConnStr()))
		{
			return getEntityFromResult(entityType, connection.selectFrom(entityType)
					.where(filter)
					.fetch());
		}
	}

	@Override
	protected Stream<Entity> makeEntities(FieldsMapping fieldsMapping, String entityType)
	{
		try (DSLContext connection = using(fieldsMapping.getConnStr()))
		{
			return getEntityFromResult(entityType, connection.selectFrom(entityType).fetch());
		}
	}

	private static Stream<Entity> queryRead(SimpleFilter simpleFilter, Condition filter)
	{
		return makeEntities(Conf.getFieldsMappingFromEntityField(simpleFilter.getEntityType(), simpleFilter.getFieldName()), simpleFilter.getEntityType(), filter);
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
					connection.deleteFrom(table(entityType)).where(field("uuid").in(uuids)));
		}
	}

	@Override
	public void executeUpdate(FieldsMapping fieldsMapping, Map<String, Pair<Collection<UUID>, Map<String, Object>>> updates, Query.Friend friend)
	{

	}
}
