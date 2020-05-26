//package dataLayer.crud.dbAdapters;
//
//import dataLayer.crud.Entity;
//import dataLayer.crud.Pair;
//import dataLayer.crud.Query;
//import dataLayer.crud.filters.*;
//import dataLayer.readers.configReader.FieldsMapping;
//
//import java.util.Collection;
//import java.util.Map;
//import java.util.UUID;
//import java.util.stream.Stream;
//
//public class MySQLAdapter extends DatabaseAdapter
//{
//	@Override
//	protected void executeCreate(FieldsMapping fieldsMapping, String entityType, Map<String, Object> fieldsAndValues)
//	{
//
//	}
//
//	@Override
//	protected Stream<Entity> makeEntities(FieldsMapping fieldsMapping, String entityType)
//	{
//		return null;
//	}
//
//	@Override
//	public Stream<Entity> executeRead(Eq eq, Query.Friend friend)
//	{
//		return null;
//	}
//
//	@Override
//	public Stream<Entity> executeRead(Ne ne, Query.Friend friend)
//	{
//		return null;
//	}
//
//	@Override
//	public Stream<Entity> executeRead(Gt gt, Query.Friend friend)
//	{
//		return null;
//	}
//
//	@Override
//	public Stream<Entity> executeRead(Lt lt, Query.Friend friend)
//	{
//		return null;
//	}
//
//	@Override
//	public Stream<Entity> executeRead(Gte gte, Query.Friend friend)
//	{
//		return null;
//	}
//
//	@Override
//	public Stream<Entity> executeRead(Lte lte, Query.Friend friend)
//	{
//		return null;
//	}
//
//	@Override
//	protected Stream<Entity> executeRead(FieldsMapping fieldsMapping, UUID uuid, String entityType)
//	{
//		return null;
//	}
//
//	@Override
//	public void executeDelete(FieldsMapping fieldsMapping, Map<String, Collection<UUID>> typesAndUuids, Query.Friend friend)
//	{
//
//	}
//
//	@Override
//	public void executeUpdate(FieldsMapping fieldsMapping, Map<String, Pair<Collection<UUID>, Map<String, Object>>> updates, Query.Friend friend)
//	{
//
//	}
//}
