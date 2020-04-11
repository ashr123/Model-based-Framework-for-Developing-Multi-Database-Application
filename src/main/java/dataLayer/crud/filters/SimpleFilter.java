package dataLayer.crud.filters;

public abstract class SimpleFilter implements Filter
{
	private final String entityType, fieldName;
	private final Object value;

	public SimpleFilter(String entityType, String fieldName, Object value)
	{
		this.entityType = entityType;
		this.fieldName = fieldName;
		this.value = value;
	}

	public String getEntityType()
	{
		return entityType;
	}

	public String getFieldName()
	{
		return fieldName;
	}

	public Object getValue()
	{
		return value;
	}

	@Override
	public String toString()
	{
		return "entityType='" + entityType + '\'' +
				", fieldName='" + fieldName + '\'' +
				", value=" + value;
	}
}
