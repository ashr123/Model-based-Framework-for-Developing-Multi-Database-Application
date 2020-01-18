package dataLayer.queryAdapters.crud;

import dataLayer.queryAdapters.dbAdapters.DatabaseAdapter;
import org.bson.conversions.Bson;

import java.util.List;
import java.util.Map;

public class Lt extends SimpleQuery
{
	private Lt(String entityName, String fieldName, Object value)
	{
		super(entityName, fieldName, value);
	}

	public static Lt lt(String entityName, String fieldName, Object value)
	{
		return new Lt(entityName, fieldName, value);
	}

	@Override
	public List<Map<String, Object>> accept(DatabaseAdapter databaseAdapter)
	{
		return databaseAdapter.execute(this);
	}

	@Override
	public String toString()
	{
		return "Lt{" + super.toString() + '}';
	}

	public Bson generateFromMongoDB()
	{
		return com.mongodb.client.model.Filters.lt(getFieldName(), getValue());
	}
}
