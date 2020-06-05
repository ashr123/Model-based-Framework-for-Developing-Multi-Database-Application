package dataLayer.crud;

import dataLayer.crud.dbAdapters.DatabaseAdapter;
import dataLayer.readers.configReader.Conf;

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
		Objects.requireNonNull(friend);
	}

	Entity(String uuid, String entityType, Map<String, Object> fieldsValues)
	{
		this(UUID.fromString(uuid), entityType, fieldsValues);
	}

	public Entity(UUID uuid, String entityType, Map<String, Object> fieldsValues, DatabaseAdapter.Friend friend)
	{
		this(uuid, entityType, fieldsValues);
		Objects.requireNonNull(friend);
	}

	Entity(UUID uuid, String entityType, Map<String, Object> fieldsValues)
	{
		this.uuid = uuid;
		this.entityType = entityType;
		this.fieldsValues = Objects.requireNonNull(fieldsValues);
	}

	/**
	 * Adds a new field to this entity
	 * @param field the about field name
	 * @param value its value
	 */
	public void putField(String field, Object value)
	{
		fieldsValues.put(field, value);
	}

	/**
	 *
	 * @param entityType the type of the about entity
	 * @param fieldsValues its values
	 * @return a new entity of type {@code entityType} and the specified fields and values
	 */
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

	/**
	 *
	 * @return this entity's {@link UUID}
	 */
	public UUID getUuid()
	{
		return uuid;
	}

	/**
	 *
	 * @return this entity's type
	 */
	public String getEntityType()
	{
		return entityType;
	}

	/**
	 *
	 * @param field the name of the requested field
	 * @return this entity's field's value, if the field doesn't exist, it returns {@code null}
	 * @see Map#get(Object)
	 */
	public Object get(String field)
	{
		return fieldsValues.get(field);
	}

	Map<String, Object> getFieldsValues()
	{
		return fieldsValues;
	}

	public Map<String, Object> getFieldsValues(DatabaseAdapter.Friend friend)
	{
		Objects.requireNonNull(friend);
		return getFieldsValues();
	}

	public Map<String, Object> getFieldsValues(Conf.Friend friend)
	{
		Objects.requireNonNull(friend);
		return getFieldsValues();
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
