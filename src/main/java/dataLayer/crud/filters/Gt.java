package dataLayer.crud.filters;

import dataLayer.configReader.Entity;
import dataLayer.crud.dbAdapters.DatabaseAdapter;
import org.bson.conversions.Bson;

import java.util.Set;
import java.util.stream.Stream;

public class Gt extends SimpleFilter
{
	private Gt(String entityName, String fieldName, Object value)
	{
		super(entityName, fieldName, value);
	}

	public static Gt gt(String entityName, String fieldName, Object value)
	{
		return new Gt(entityName, fieldName, value);
	}

	@Override
	public Stream<Entity> accept(DatabaseAdapter databaseAdapter)
	{
		return databaseAdapter.execute(this);
	}

	@Override
	public String toString()
	{
		return "Gt{" + super.toString() + '}';
	}

	public Bson generateFromMongoDB()
	{
		return com.mongodb.client.model.Filters.gt(getFieldName(), getValue());
	}

//	@Override
//	public IClause[] generateFromNeo4j()
//	{
//		JcNode node = new JcNode(getEntityName().toLowerCase());
//		return new IClause[]{
//				MATCH.node(node).label(getEntityName()),
//				WHERE.valueOf(node.property(getFieldName())).GT(getValue()),
//				RETURN.value(node)
//		};
//	}
}
