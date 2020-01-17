package queryAdapters.crud;

public abstract class SimpleQuery implements Query
{
	private final String entityName, fieldName;
	private final Object value;

	public SimpleQuery(String entityName, String fieldName, Object value)
	{
		this.entityName = entityName;
		this.fieldName = fieldName;
		this.value = value;
	}

	public static Eq eq(String entityName, String fieldName, Object value)
	{
		return new Eq(entityName, fieldName, value);
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
}
