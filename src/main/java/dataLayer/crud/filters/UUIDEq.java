//package dataLayer.crud.filters;
//
//import dataLayer.configReader.Entity;
//import dataLayer.configReader.FieldsMapping;
//import dataLayer.crud.dbAdapters.DatabaseAdapter;
//
//import java.util.Set;
//import java.util.UUID;
//import java.util.stream.Stream;
//
//public class UUIDEq implements Filter
//{
//	private final String type;
//	private final UUID uuid;
//	private final FieldsMapping fieldsMapping;
//
//	public UUIDEq(String type, String uuid, FieldsMapping fieldsMapping)
//	{
//		this(type, UUID.fromString(uuid), fieldsMapping);
//	}
//
//	public UUIDEq(String type, UUID uuid, FieldsMapping fieldsMapping)
//	{
//		this.type = type;
//		this.uuid = uuid;
//		this.fieldsMapping = fieldsMapping;
//	}
//
//	public String getType()
//	{
//		return type;
//	}
//
//	public UUID getUuid()
//	{
//		return uuid;
//	}
//
//	public FieldsMapping getFieldsMapping()
//	{
//		return fieldsMapping;
//	}
//
//	@Override
//	public Stream<Entity> accept(DatabaseAdapter databaseAdapter)
//	{
//		return databaseAdapter.execute(this);
//	}
//
//	@Override
//	public String toString()
//	{
//		return "UUIDEq{" +
//				"type='" + type + '\'' +
//				", uuid=" + uuid +
//				", fieldsMapping=" + fieldsMapping +
//				'}';
//	}
//}
