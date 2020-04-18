package dataLayer.readers.schemaReader;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.InputMismatchException;
import java.util.Objects;

public class EntityPropertyData
{
	@JsonProperty("type")
	private final PropertyType type;
	@JsonProperty("javaType")
	private final String javaType;
	@JsonProperty("items")
	private final EntityPropertyData items;
	@JsonProperty("relation")
	private final RelationType relation;

	public EntityPropertyData()
	{
		type = null;
		javaType = null;
		items = null;
		relation = null;
	}

	public EntityPropertyData(PropertyType type, String javaType, EntityPropertyData items, RelationType relation)
	{
		this.type = type;
		this.javaType = javaType;
		this.items = items;
		this.relation = relation;
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

	public RelationType getRelation()
	{
		return relation;
	}

	void checkValidity()
	{
		switch (type)
		{
			case ARRAY:
				if (items == null)
					throw new InputMismatchException("when type is '"+ type + "' \"items\" must be defined!");
				else
					items.checkValidity();
				break;
			case OBJECT:
				if (javaType == null)
					throw new InputMismatchException("when type is '"+ type + "' \"javaType\" must be defined!");
				else
					Schema.containsClass(javaType);
		}
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
				Objects.equals(javaType, that.javaType) &&
				Objects.equals(items, that.items) &&
				relation == that.relation;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(type, javaType, items, relation);
	}

	@Override
	public String toString()
	{
		return "EntityPropertyData{" +
				"type=" + type +
				", javaType='" + javaType + '\'' +
				", items=" + items +
				", relation=" + relation +
				'}';
	}
}
