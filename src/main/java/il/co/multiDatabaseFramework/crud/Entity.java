package il.co.multiDatabaseFramework.crud;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import il.co.multiDatabaseFramework.crud.dbAdapters.DatabaseAdapter;
import il.co.multiDatabaseFramework.readers.Reader;
import il.co.multiDatabaseFramework.readers.configReader.Conf;

import java.io.IOException;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * Represents the core element of this framework.
 *
 * @implNote if {@link Reader#isCyclic()}{@code ==true} then {@link Entity#fieldsValues} won't be evaluated in {@link Entity#equals(Object)}
 * and in {@link Entity#hashCode()}, {@link Entity#toString()} won't unfold dipper entities.
 */
public class Entity
{
	private static final String REPLACEMENT = "<cyclic>";
	private static final Random RANDOM = new Random();
	private final UUID uuid;
	private final String entityType;
	@JsonSerialize(contentUsing = ValueSerializer.class)
	private final Map<String, Object> fieldsValues;

	private int seed;

	Entity(Map<String, Object> fieldsValues)
	{
		this((UUID) null, null, new HashMap<>(fieldsValues));
		if (Reader.isCyclic())
			seed = RANDOM.nextInt();
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

	private static Object prepareValue(Object o)
	{
//		return o instanceof Entity ? // 1
//		       Reader.isCyclic() ? // 1.a
//		       REPLACEMENT : // 1.a.true
//		       o : // 1.a.false
//		       o instanceof Collection<?> ? // 2
//		       ((Collection<?>) o).stream().allMatch(Entity.class::isInstance) ? // 2.a
//		       Reader.isCyclic() ? // 2.a.i
//		       ((Collection<?>) o).stream()
//				       .map(entity -> REPLACEMENT)
//				       .collect(toList()) : // 2.a.i.true
//		       o : // 2.a.i.false
//		       ((Collection<?>) o).stream()
//				       .map(Entity::prepareObject)
//				       .collect(toList()) : // 2.a.false
//
//		       prepareObject(o); // 3 (2.false)

		if (o instanceof Entity)
			return Reader.isCyclic() ?
			       REPLACEMENT :
			       o /*Entity*/;
		if (o instanceof Collection<?>)
			if (((Collection<?>) o).stream().allMatch(Entity.class::isInstance))
				return Reader.isCyclic() ?
				       ((Collection<?>) o).stream()
						       .map(entity -> REPLACEMENT)
						       .collect(toList()) :
				       o /*Collection of Entities*/;
			else
				return ((Collection<?>) o).stream()
						.map(Entity::prepareObject)
						.collect(toList()) /*Collection of Objects*/;
		return prepareObject(o) /*A single Object*/;
	}

	private static Object prepareObject(Object o)
	{
		return o instanceof Number || o instanceof Boolean ? o : o.toString();
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

	public Object getAndTransform(String field)
	{
		return Reader.unDecodeValue(entityType, field, get(field));
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
		       (Reader.isCyclic() ? seed == entity.seed : fieldsValues.equals(entity.fieldsValues));
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(uuid, entityType, Reader.isCyclic() ? seed : fieldsValues);
	}

	@Override
	public String toString()
	{
		return "Entity{" +
		       "uuid=" + uuid +
		       ", entityType='" + entityType + '\'' +
		       ", fieldsValues=" + fieldsValues.entrySet().stream()
				       .map(fieldAndValue -> Map.entry(fieldAndValue.getKey(), prepareValue(fieldAndValue.getValue())))
				       .collect(toMap(Map.Entry::getKey, Map.Entry::getValue)) +
		       '}';
	}

	private static class ValueSerializer extends JsonSerializer<Object>
	{
		@Override
		public void serialize(final Object value,
		                      final JsonGenerator gen,
		                      final SerializerProvider serializers) throws IOException
		{
			gen.writeObject(prepareValue(value));
		}
	}
}
