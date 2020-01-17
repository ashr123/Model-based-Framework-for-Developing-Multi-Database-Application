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
	private final Map<String, String> fields;

	public Entity() {
		entityName = null;
		fields = new LinkedHashMap<>();
	}

	public Entity(String entityName, Map<String, String> fields) {
		this.entityName = entityName;
		this.fields = fields;
	}

	@JsonAnyGetter
	private Map<String, String> getFields()
	{
		return fields;
	}

	@JsonAnySetter
	private void addField(String key, String value)
	{
		fields.put(key, value);
	}

	public String getFieldDataStoreName(String field)
	{
		return fields.get(field);
	}

	@Override
	public String toString()
	{
		return "Entity{" +
				"fields=" + fields +
				'}';
	}

	public void validate(Set<String> keySet)
	{
		if (!keySet.containsAll(fields.values()))
			throw new InputMismatchException("Not all fields locations exists as DataStores!!");
	}
}
