package dataLayer.crud;

import com.fasterxml.jackson.annotation.JsonProperty;
import dataLayer.crud.dbAdapters.DatabaseAdapter;
import dataLayer.readers.Reader;
import dataLayer.readers.configReader.Conf;

import java.util.*;

/**
 * Represents the core element of this framework.
 *
 * @implNote if {@link Reader#isCyclic()}{@code ==true} then {@link Entity#fieldsValues} won't be evaluated in {@link Entity#equals(Object)}
 * and in {@link Entity#hashCode()}, {@link Entity#toString()} will cause {@link StackOverflowError}.
 */
public class Entity
{
	private static final Random random = new Random();
	private final UUID uuid;
	private final String entityType;
	@JsonProperty
	private final Map<String, Object> fieldsValues;

	private int seed = 0;

	Entity(Map<String, Object> fieldsValues)
	{
		this((UUID) null, null, new HashMap<>(fieldsValues));
		seed = random.nextInt();
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
	 * @param entityType the type of the about entity
	 * @return a new empty entity of type {@code entityType}
	 */
	public static Entity of(String entityType)
	{
		return new Entity(UUID.randomUUID(), entityType, new HashMap<>());
	}

	/**
	 * Adds a new field to this entity
	 *
	 * @param field the about field name
	 * @param value its value
	 * @return this entity
	 */
	public Entity putField(String field, Object value)
	{
		fieldsValues.put(field, value instanceof Integer ? Long.valueOf((Integer) value) : value);
		return this;
	}

	/**
	 * @return this entity's {@link UUID}
	 */
	public UUID getUuid()
	{
		return uuid;
	}

	/**
	 * @return this entity's type
	 */
	public String getEntityType()
	{
		return entityType;
	}

	/**
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
		       (Reader.isCyclic() && seed == entity.seed || fieldsValues.equals(entity.fieldsValues));
	}

	@Override
	public int hashCode()
	{
		return Reader.isCyclic() ?
		       Objects.hash(seed, uuid, entityType) :
		       Objects.hash(uuid, entityType, fieldsValues);
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
