package dataLayer.crud.filters;

import dataLayer.configReader.Entity;
import dataLayer.crud.dbAdapters.DatabaseAdapter;
import iot.jcypher.query.api.IClause;
import iot.jcypher.query.factories.clause.MATCH;
import iot.jcypher.query.factories.clause.RETURN;
import iot.jcypher.query.values.JcNode;
import org.bson.conversions.Bson;

import java.util.Set;

public class Eq extends SimpleFilter
{
	private Eq(String entityName, String fieldName, Object value)
	{
		super(entityName, fieldName, value);
	}

	public static Eq eq(String entityName, String fieldName, Object value)
	{
		return new Eq(entityName, fieldName, value);
	}

	@Override
	public Set<Entity> accept(DatabaseAdapter databaseAdapter)
	{
		return databaseAdapter.execute(this);
	}

	@Override
	public String toString()
	{
		return "Eq{" + super.toString() + '}';
	}

	public Bson generateFromMongoDB()
	{
		return com.mongodb.client.model.Filters.eq(getFieldName(), getValue());
	}

	@Override
	public IClause[] generateFromNeo4j()
	{
		JcNode node = new JcNode(getEntityName().toLowerCase());
		return new IClause[]{
				MATCH.node(node).label(getEntityName()).property(getFieldName()).value(getValue()),
				RETURN.value(node)
		};
	}
}
