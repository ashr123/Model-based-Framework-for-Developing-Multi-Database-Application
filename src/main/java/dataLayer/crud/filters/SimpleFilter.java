package dataLayer.crud.filters;

import java.util.Objects;

/**
 * This class represents an atomic logical filter.
 *
 * @author Roy Ash
 * @author Yossi Landa.
 */
public abstract class SimpleFilter implements Filter
{
	private final String entityType, fieldName;
	private final Object value;

	/**
	 * Constructor function for a simple filter.
	 * @param entityType The entity type on which we wish to filter.
	 * @param fieldName The entity field name which we wish to filter by.
	 * @param value The entity field value which we wish to filter by.
	 */
	protected SimpleFilter(String entityType, String fieldName, Object value)
	{
		this.entityType = entityType;
		this.fieldName = fieldName;
		this.value = value;
	}

	/**
	 * Getter for the Entity type involved in the filter
	 * @return The filter's result entity type.
	 */
	public String getEntityType()
	{
		return entityType;
	}

	/**
	 * Getter for the field (name) in which we filter by.
	 * @return name of field we filter by.
	 */
	public String getFieldName()
	{
		return fieldName;
	}

	/**
	 * Getter for the field (value) in which we filter by.
	 * @return value of field we filter by.
	 */
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

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (!(o instanceof SimpleFilter))
			return false;
		SimpleFilter that = (SimpleFilter) o;
		return entityType.equals(that.entityType) &&
		       fieldName.equals(that.fieldName) &&
		       value.equals(that.value);
	}
}
