package dataLayer.crud.filters;

import dataLayer.crud.dbAdapters.DatabaseAdapter;
import org.bson.conversions.Bson;

import java.util.List;
import java.util.Map;

public class Gte extends SimpleFilter
{
	private Gte(String entityName, String fieldName, Object value)
	{
		super(entityName, fieldName, value);
	}

	public static Gte gte(String entityName, String fieldName, Object value)
	{
		return new Gte(entityName, fieldName, value);
	}

	@Override
	public List<Map<String, Object>> accept(DatabaseAdapter databaseAdapter)
	{
		return databaseAdapter.execute(this);
	}

	@Override
	public String toString()
	{
		return "Gte{" + super.toString() + '}';
	}

	public Bson generateFromMongoDB()
	{
		return com.mongodb.client.model.Filters.gte(getFieldName(), getValue());
	}
}
