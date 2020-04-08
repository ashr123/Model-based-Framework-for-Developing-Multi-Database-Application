package dataLayer.crud.filters;

import dataLayer.configReader.Entity;
import dataLayer.crud.dbAdapters.DatabaseAdapter;
import org.bson.conversions.Bson;

import java.util.Set;
import java.util.stream.Stream;

public class Lt extends SimpleFilter
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
	public Stream<Entity> accept(DatabaseAdapter databaseAdapter)
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

//	@Override
//	public IClause[] generateFromNeo4j()
//	{
//		JcNode node = new JcNode(getEntityName().toLowerCase());
//		return new IClause[]{
//				MATCH.node(node).label(getEntityName()),
//				WHERE.valueOf(node.property(getFieldName())).LT(getValue()),
//				RETURN.value(node)
//		};
//	}
}
