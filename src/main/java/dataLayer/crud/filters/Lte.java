package dataLayer.crud.filters;

import dataLayer.configReader.Entity;
import dataLayer.crud.dbAdapters.DatabaseAdapter;
import org.bson.conversions.Bson;

import java.util.Set;

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
	public Set<Entity> accept(DatabaseAdapter databaseAdapter)
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

//	@Override
//	public IClause[] generateFromNeo4j()
//	{
//		JcNode node = new JcNode(getEntityName().toLowerCase());
//		return new IClause[]{
//				MATCH.node(node).label(getEntityName()),
//				WHERE.valueOf(node.property(getFieldName())).LTE(getValue()),
//				RETURN.value(node)
//		};
//	}
}
