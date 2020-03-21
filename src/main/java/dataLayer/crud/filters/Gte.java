package dataLayer.crud.filters;

import dataLayer.crud.dbAdapters.DatabaseAdapter;
import iot.jcypher.query.api.IClause;
import iot.jcypher.query.factories.clause.MATCH;
import iot.jcypher.query.factories.clause.RETURN;
import iot.jcypher.query.factories.clause.WHERE;
import iot.jcypher.query.values.JcNode;
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

	@Override
	public IClause[] generateFromNeo4j()
	{
		JcNode node = new JcNode(getEntityName().toLowerCase());
		return new IClause[]{
				MATCH.node(node).label(getEntityName()),
				WHERE.valueOf(node.property(getFieldName())).GTE(getValue()),
				RETURN.value(node)
		};
	}
}
