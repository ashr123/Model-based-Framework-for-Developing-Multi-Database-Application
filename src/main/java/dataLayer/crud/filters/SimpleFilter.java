package dataLayer.crud.filters;

public abstract class SimpleFilter implements Filter
{
	private final String entityName, fieldName;
	private final Object value;

	public SimpleFilter(String entityName, String fieldName, Object value)
	{
		this.entityName = entityName;
		this.fieldName = fieldName;
		this.value = value;
	}

	public String getEntityName()
	{
		return entityName;
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
		return "entityName='" + entityName + '\'' +
				", fieldName='" + fieldName + '\'' +
				", value=" + value;
	}
}
