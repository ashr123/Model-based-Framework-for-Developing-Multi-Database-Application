package dataLayer.readers.schemaReader;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("ConstantConditions")
public class EntityClassData
{
	@JsonProperty("properties")
	private final Map<String/*property name*/, EntityPropertyData> properties = null;
	@JsonProperty("extends")
	private final Set<String> extend = null;

	private EntityClassData()
	{
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
