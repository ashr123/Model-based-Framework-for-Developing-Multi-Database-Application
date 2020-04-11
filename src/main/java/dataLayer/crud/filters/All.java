//package dataLayer.crud.filters;
//
//import dataLayer.crud.Entity;
//import dataLayer.crud.dbAdapters.DatabaseAdapter;
//import iot.jcypher.query.api.IClause;
//import org.bson.conversions.Bson;
//
//import java.util.Set;
//
//public class All extends SimpleFilter
//{
//	private All(String entityName, Object value)
//	{
//		super(entityName, null, value);
//	}
//
//	public static All all(String entityName, Object value)
//	{
//		return new All(entityName, value);
//	}
//
//	@Override
//	public Stream<Entity> accept(DatabaseAdapter databaseAdapter)
//	{
//		return databaseAdapter.execute(this);
//	}
//
//	@Override
//	public String toString()
//	{
//		return "All{" +
//				"entityName='" + getEntityType() + '\'' +
//				", value=" + getValue()
//				+ '}';
//	}
//
//	public Bson generateFromMongoDB()
//	{
//		throw new UnsupportedOperationException("Not implemented yet.");
//	}
//
//	@Override
//	public IClause[] generateFromNeo4j()
//	{
//		return new IClause[0];
//	}
//}
