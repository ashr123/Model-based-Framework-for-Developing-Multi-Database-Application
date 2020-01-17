package dataLayer.configReader;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.InputMismatchException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Entity
{
	@JsonIgnore
	private final String entityName;

	@JsonIgnore
	private final Map<String, String> fieldsLocations;

	@JsonIgnore
	private final Map<String, Object> fieldsValues;

	private Entity() {
		entityName = null;
		fieldsValues = null;
		fieldsLocations = new LinkedHashMap<>();
	}

	public Entity(String entityName, Map<String, Object> fieldsValues) {
		fieldsLocations = null;
		this.entityName = entityName;
		this.fieldsValues = fieldsValues;
	}

	public Entity append(String field, Object value)
	{
		assert fieldsValues != null;
		fieldsValues.put(field, value);
		return this;
	}

	public String getEntityName()
	{
		return entityName;
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

	public String getFieldDataStoreName(String field)
	{
		return fieldsLocations.get(field);
	}

	@Override
	public String toString()
	{
		return "Entity{" +
				"fieldsLocations=" + fieldsLocations +
				'}';
	}

	public void validate(Set<String> keySet)
	{
		if (!keySet.containsAll(fieldsLocations.values()))
			throw new InputMismatchException("Not all fieldsLocations locations exists as DataStores!!");
	}
}
