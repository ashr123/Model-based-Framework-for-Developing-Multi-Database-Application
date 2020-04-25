package dataLayer.readers.schemaReader;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;
import java.util.Map;

@SuppressWarnings("ConstantConditions")
public class EntityClassData
{
	@JsonProperty("properties")
	private final Map<String/*property name*/, EntityPropertyData> properties = null;

	private EntityClassData()
	{
	}

	void checkValidity()
	{
		properties.values()
				.forEach(EntityPropertyData::checkValidity);
	}

	public Collection<String> getClassProperties()
	{
		return properties.keySet();
	}

	@Override
	public String toString()
	{
		return "EntityClass{" +
				"properties=" + properties +
				'}';
	}
}
