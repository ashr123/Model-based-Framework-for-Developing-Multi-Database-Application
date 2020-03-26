package dataLayer.crud.filters;

import dataLayer.crud.dbAdapters.DatabaseAdapter;
import iot.jcypher.query.api.IClause;
import org.bson.conversions.Bson;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class And extends ComplexFilter
{

	private And(Filter... queries)
	{
		super(queries);
	}

	public static And and(Filter... queries)
	{
		return new And(queries);
	}

	@Override
	public Map<String, List<Map<String, Object>>> accept(DatabaseAdapter databaseAdapter)
	{
		return databaseAdapter.execute(this);
	}

	@Override
	public String toString()
	{
		return "And{" + super.toString() + '}';
	}

	public Bson generateFromMongoDB()
	{
		return com.mongodb.client.model.Filters.and((Bson[]) Arrays.stream(getComplexQuery())
				.map(Filter::generateFromMongoDB)
				.toArray());
	}

	@Override
	public IClause[] generateFromNeo4j()
	{
//		return new IClause[]{
//				WHERE.valueOf(new JcNode())
//		};
		return null;
	}
}
