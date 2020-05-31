package dataLayer.readers.schemaReader;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("ConstantConditions")
public class EntityClassData
{
	@JsonProperty("primaryKey")
	private final Collection<String> primaryKey = null;
	@JsonProperty("properties")
	private final Map<String/*property name*/, EntityPropertyData> properties = null;

	private EntityClassData()
	{
	}

	void checkValidity()
	{
		if (!properties.keySet().containsAll(primaryKey))
			throw new InputMismatchException("Not all fields in primary key exists in class's properties.");
		properties.values()
				.forEach(EntityPropertyData::checkValidity);
	}

//	public Map<String/*property name*/, String /*related class name*/> getRelatedClasses()
//	{
//		return properties.entrySet().stream()
//				.filter(property -> property.getValue().isRelationProperty() && property.getValue().getRelation() != null)
//				.collect(Collectors.toMap(Map.Entry::getKey, property -> property.getValue().getRelatedClassName()));
//	}

	public EntityPropertyData getPropertyData(String propertyName)
	{
		return properties.get(propertyName);
	}

//	public Collection<EntityPropertyData> getClassPropertiesData()
//	{
//		return properties.values();
//	}

	public Collection<String> getClassPropertiesNames()
	{
		return properties.keySet();
	}

	public Collection<String> getPrimaryKey()
	{
		return primaryKey;
	}

	@Override
	public boolean equals(Object o)
	{
		return this == o || o instanceof EntityClassData && properties.equals(((EntityClassData) o).properties);
//		if (this == o)
//			return true;
//		if (!(o instanceof EntityClassData))
//			return false;
//		return properties.equals(((EntityClassData) o).properties);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(properties);
	}

	@Override
	public String toString()
	{
		return "EntityClassData{" +
		       "primaryKey=" + primaryKey +
		       ", properties=" + properties +
		       '}';
	}
}
