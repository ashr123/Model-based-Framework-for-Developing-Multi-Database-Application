package dataLayer.crud.filters;

import dataLayer.crud.dbAdapters.DatabaseAdapter;
import org.bson.conversions.Bson;

import java.util.List;
import java.util.Map;

public class Lte extends SimpleFilter
{
	private Lte(String entityName, String fieldName, Object value)
	{
		super(entityName, fieldName, value);
	}

	public static Lte lte(String entityName, String fieldName, Object value)
	{
		return new Lte(entityName, fieldName, value);
	}

	@Override
	public List<Map<String, Object>> accept(DatabaseAdapter databaseAdapter)
	{
		return databaseAdapter.execute(this);
	}

	@Override
	public String toString()
	{
		return "Lte{" + super.toString() + '}';
	}

	public Bson generateFromMongoDB()
	{
		return com.mongodb.client.model.Filters.lte(getFieldName(), getValue());
	}
}
