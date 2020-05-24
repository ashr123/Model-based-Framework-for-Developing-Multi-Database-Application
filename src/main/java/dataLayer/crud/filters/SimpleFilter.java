package dataLayer.crud.filters;

import java.util.Objects;

public abstract class SimpleFilter implements Filter
{
	private final String entityType, fieldName;
	private final Object value;

	protected SimpleFilter(String entityType, String fieldName, Object value)
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
	public int hashCode()
	{
		return Objects.hash(entityType, fieldName, value);
	}

	@Override
	public String toString()
	{
		return "entityType='" + entityType + '\'' +
		       ", fieldName='" + fieldName + '\'' +
		       ", value=" + value;
	}
}
