package dataLayer.crud.filters;

import dataLayer.crud.dbAdapters.DatabaseAdapter;
import org.bson.conversions.Bson;

import java.util.List;
import java.util.Map;

public class Ne extends SimpleFilter
{
	private Ne(String entityName, String fieldName, Object value)
	{
		super(entityName, fieldName, value);
	}

	public static Ne ne(String entityName, String fieldName, Object value)
	{
		return new Ne(entityName, fieldName, value);
	}

	@Override
	public List<Map<String, Object>> accept(DatabaseAdapter databaseAdapter)
	{
		return databaseAdapter.execute(this);
	}

	@Override
	public String toString()
	{
		return "Ne{" + super.toString() + '}';
	}

	public Bson generateFromMongoDB()
	{
		return com.mongodb.client.model.Filters.ne(getFieldName(), getValue());
	}
}
