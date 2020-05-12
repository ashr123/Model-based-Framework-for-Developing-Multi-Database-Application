package dataLayer.readers.schemaReader;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

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

	public Map<String/*property name*/, String /*related class name*/> getRelatedClasses()
	{
		return properties.entrySet().stream()
				.filter(property -> property.getValue().isRelationProperty() && property.getValue().getRelation() != null)
				.collect(Collectors.toMap(Map.Entry::getKey, property -> property.getValue().getRelatedClassName()));
	}

	public EntityPropertyData getPropertyData(String propertyName)
	{
		return properties.get(propertyName);
	}

	public Collection<EntityPropertyData> getClassPropertiesData()
	{
		return properties.values();
	}

	public Collection<String> getClassPropertiesNames()
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
