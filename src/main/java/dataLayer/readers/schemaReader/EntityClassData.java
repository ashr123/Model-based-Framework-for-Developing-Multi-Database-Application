package dataLayer.readers.schemaReader;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class EntityClassData
{
	@JsonProperty("properties")
	private final Map<String/*property name*/, EntityPropertyData> properties;
	@JsonProperty("extends")
	private final Set<String> extend;

	public EntityClassData()
	{
		properties = null;
		extend = null;
	}

	public EntityClassData(Map<String, EntityPropertyData> properties, Set<String> extend)
	{
		this.properties = properties;
		this.extend = extend;
	}

	void checkValidity()
	{
		if (extend != null)
			Schema.containsAllClasses(extend);
		properties.values()
				.forEach(EntityPropertyData::checkValidity);
	}

	public Collection<String> getClassProperties()
	{
		assert properties != null;
		return properties.keySet();
	}

	@Override
	public String toString()
	{
		return "EntityClass{" +
				"properties=" + properties +
				", extend=" + extend +
				'}';
	}
}
