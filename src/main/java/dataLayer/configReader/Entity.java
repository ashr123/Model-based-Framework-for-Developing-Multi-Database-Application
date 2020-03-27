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
	private final Map<String, String> fieldsLocations;

	@JsonIgnore
	private final Map<String, Object> fieldsValues;

	private Entity()
	{
		entityType = null;
		fieldsValues = null;
		fieldsLocations = new LinkedHashMap<>();
	}

	public Entity(String entityType, Map<String, Object> fieldsValues)
	{
		fieldsLocations = null;
		this.entityType = entityType;
		this.fieldsValues = fieldsValues;
	}

	private Entity(String entityType)
	{
		fieldsLocations = null;
		this.entityType = entityType;
		this.fieldsValues = new LinkedHashMap<>();
	}

	private static Entity entity(String entityName, Map<String, Object> fieldsValues)
	{
		return new Entity(entityName, fieldsValues);
	}

	public static Entity entity(String entityName)
	{
		return new Entity(entityName);
	}

	public Entity append(String field, Object value)
	{
		assert fieldsValues != null;
		fieldsValues.put(field, value);
		return this;
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
