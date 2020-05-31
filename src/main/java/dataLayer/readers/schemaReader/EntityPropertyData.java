package dataLayer.readers.schemaReader;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Objects;

@SuppressWarnings("ConstantConditions")
public class EntityPropertyData
{
	@JsonProperty("type")
	private final PropertyType type = null;
	@SuppressWarnings("FieldMayBeStatic")
	@JsonProperty("javaType")
	private final String javaType = null;
	@JsonProperty("items")
	private final EntityPropertyData items = null;
//	@JsonProperty("relation")
//	private final RelationType relation = null;

	private EntityPropertyData()
	{
	}

	public PropertyType getType()
	{
		return type;
	}

	public String getJavaType()
	{
		return javaType;
	}

	public EntityPropertyData getItems()
	{
		return items;
	}

//	public RelationType getRelation()
//	{
//		return relation;
//	}

	void checkValidity()
	{
		switch (type)
		{
			case ARRAY -> {
				if (items == null)
					throw new InputMismatchException("when type is '" + type + "' \"items\" must be defined!");
				items.checkValidity();
			}
			case OBJECT -> {
				if (javaType == null)
					throw new InputMismatchException("when type is '" + type + "' \"javaType\" must be defined!");
				Schema.containsClass(javaType);
			}
		}
	}

	public String getRelatedClassName()
	{
		if (type.equals(PropertyType.OBJECT))
			return javaType;
		if (type.equals(PropertyType.ARRAY) && !items.type.equals(PropertyType.ARRAY)) // To prevent deep arrays
			return items.getRelatedClassName();
		throw new NoSuchElementException("The property does not represent a relation.");
	}

	public boolean isRelationProperty()
	{
		return type.equals(PropertyType.OBJECT) || (type.equals(PropertyType.ARRAY) && items.isRelationProperty());
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (!(o instanceof EntityPropertyData))
			return false;
		EntityPropertyData that = (EntityPropertyData) o;
		return type == that.type &&
//		       relation == that.relation &&
		       Objects.equals(javaType, that.javaType) &&
		       Objects.equals(items, that.items);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(type, javaType, items/*, relation*/);
	}

	@Override
	public String toString()
	{
		return "EntityPropertyData{" +
		       "type=" + type +
		       ", javaType='" + javaType + '\'' +
		       ", items=" + items +
//		       ", relation=" + relation +
		       '}';
	}
}
