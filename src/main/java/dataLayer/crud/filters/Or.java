package dataLayer.crud.filters;

import dataLayer.configReader.Entity;
import dataLayer.crud.dbAdapters.DatabaseAdapter;
import iot.jcypher.query.api.IClause;
import iot.jcypher.query.api.pattern.Element;
import iot.jcypher.query.api.predicate.Concatenator;
import iot.jcypher.query.api.returns.RSortable;
import iot.jcypher.query.factories.clause.UNION;
import org.bson.conversions.Bson;

import java.util.*;

public class Or extends ComplexFilter
{
	private Or(Filter... queries)
	{
		super(queries);
	}

	public static Or or(Filter... queries)
	{
		return new Or(queries);
	}

	@Override
	public Set<Entity> accept(DatabaseAdapter databaseAdapter)
	{
		return databaseAdapter.execute(this);
	}

	@Override
	public String toString()
	{
		return "Or{" + super.toString() + '}';
	}

//	public Bson generateFromMongoDB()
//	{
//		return com.mongodb.client.model.Filters.or((Bson[]) Arrays.stream(getComplexQuery())
//				.map(Filter::generateFromMongoDB)
//				.toArray());
//	}
//
//	@Override
//	public IClause[] generateFromNeo4j()
//	{
//		List<Element<?>> matches = new LinkedList<>();
//		List<Concatenator> wheres = new LinkedList<>();
//		List<RSortable> returns = new LinkedList<>();
////		WHERE.BR_OPEN().valueOf()..EQUALS().
//		return new IClause[]{
//				UNION.all()
//		};
//	}
}
