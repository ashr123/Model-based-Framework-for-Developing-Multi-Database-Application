package dataLayer.crud;

import dataLayer.crud.dbAdapters.DatabaseAdapter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class Entity
{
	private final UUID uuid;
	private final String entityType;
	private final Map<String, Object> fieldsValues;

	Entity(Map<String, Object> fieldsValues)
	{
		this((UUID) null, null, new HashMap<>(fieldsValues));
	}

	@SuppressWarnings("CopyConstructorMissesField")
	public Entity(Entity entity)
	{
		this(entity.uuid, entity.entityType, new HashMap<>(entity.fieldsValues));
	}

	public Entity(String uuid, String entityType, Map<String, Object> fieldsValues, DatabaseAdapter.Friend friend)
	{
		this(uuid, entityType, fieldsValues);
	}

	Entity(String uuid, String entityType, Map<String, Object> fieldsValues)
	{
		this(UUID.fromString(uuid), entityType, fieldsValues);
	}

	public Entity(UUID uuid, String entityType, Map<String, Object> fieldsValues, DatabaseAdapter.Friend friend)
	{
		this(uuid, entityType, fieldsValues);
	}

	Entity(UUID uuid, String entityType, Map<String, Object> fieldsValues)
	{
		this.uuid = uuid;
		this.entityType = entityType;
		this.fieldsValues = Objects.requireNonNull(fieldsValues);
	}

	public static Entity of(String entityType, Map<String, Object> fieldsValues)
	{
//		fieldsValues.entrySet().stream()
//				.filter(fieldAndValue -> fieldAndValue.getValue() instanceof Number && !(fieldAndValue.getValue() instanceof Long || fieldAndValue.getValue() instanceof Double))
//				.map(fieldAndValue -> Map.entry(fieldAndValue.getKey(), ((Number) fieldAndValue.getValue()).longValue()));
		return new Entity(UUID.randomUUID(), entityType, fieldsValues);
	}

//	public Entity append(String field, Object value)
//	{
//		assert fieldsValues != null;
//		fieldsValues.put(field, value);
//		return this;
//	}

	public UUID getUuid()
	{
		return uuid;
	}

	public String getEntityType()
	{
		return entityType;
	}

	public Object get(String field)
	{
		return fieldsValues.get(field);
	}

	public Map<String, Object> getFieldsValues()
	{
		return fieldsValues;
	}

	public Entity merge(Entity entity)
	{
		entity.fieldsValues
				.forEach((field, value) -> fieldsValues.merge(field, value, (value1, value2) -> value2));
		return this;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (!(o instanceof Entity))
			return false;
		Entity entity = (Entity) o;
		return Objects.equals(uuid, entity.uuid) &&
		       Objects.equals(entityType, entity.entityType) &&
		       fieldsValues.equals(entity.fieldsValues);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(uuid, entityType, fieldsValues);
	}

	@Override
	public String toString()
	{
		return "Entity{" +
		       "uuid=" + uuid +
		       ", entityType='" + entityType + '\'' +
		       ", fieldsValues=" + fieldsValues +
		       '}';
	}
}
