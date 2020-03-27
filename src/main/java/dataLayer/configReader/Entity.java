package dataLayer.configReader;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class Entity
{
	@JsonIgnore
	private final String entityType;

	@JsonIgnore
	private final UUID uuid;

	@JsonIgnore
	private final Map<String, String> fieldsLocations;

	@JsonIgnore
	private final Map<String, Object> fieldsValues;

	private Entity()
	{
		entityType = null;
		fieldsValues = null;
		uuid = null;
		fieldsLocations = new LinkedHashMap<>();
	}

	public Entity(String entityType, Map<String, Object> fieldsValues)
	{
		fieldsLocations = null;
		uuid = UUID.randomUUID();
		this.entityType = entityType;
		this.fieldsValues = fieldsValues;
	}

//	private Entity(String entityType)
//	{
//		fieldsLocations = null;
//		uuid = UUID.randomUUID();
//		this.entityType = entityType;
//		this.fieldsValues = new LinkedHashMap<>();
//	}

	public static Entity entity(String entityType, Map<String, Object> fieldsValues)
	{
		return new Entity(entityType, fieldsValues);
	}

//	public static Entity entity(String entityType)
//	{
//		return new Entity(entityType);
//	}

	public Entity append(String field, Object value)
	{
		assert fieldsValues != null;
		fieldsValues.put(field, value);
		return this;
	}

	public UUID getUuid()
	{
		return uuid;
	}

	public String getEntityType()
	{
		return entityType;
	}

	public Map<String, Object> getFieldsValues()
	{
		return fieldsValues;
	}

	@JsonAnyGetter
	private Map<String, String> getFieldsLocations()
	{
		return fieldsLocations;
	}

	@JsonAnySetter
	private void addFieldLocation(String key, String value)
	{
		fieldsLocations.put(key, value);
	}

	public String getFieldFieldsMappingName(String field)
	{
		return fieldsLocations.get(field);
	}

	public Entity merge(Entity entity)
	{
		entity.fieldsValues
				.forEach((field, value) -> fieldsValues.computeIfAbsent(field, field1 -> fieldsValues.put(field1, value)));
		return this;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Entity entity = (Entity) o;
		return entityType.equals(entity.entityType) &&
				Objects.equals(fieldsValues, entity.fieldsValues);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(entityType, fieldsValues);
	}

	@Override
	public String toString()
	{
		return "Entity{" +
				"entityType='" + entityType + '\'' +
				", fieldsLocations=" + fieldsLocations +
				", fieldsValues=" + fieldsValues +
				'}';
	}

	void validate(Set<String> keySet)
	{
		if (!keySet.containsAll(fieldsLocations.values()))
			throw new InputMismatchException("Not all fieldsLocations locations exists as FieldsMapping!!");
	}
}
