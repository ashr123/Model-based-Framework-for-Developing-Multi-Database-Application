package dataLayer.crud.dbAdapters;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import dataLayer.configReader.Conf;
import dataLayer.configReader.Entity;
import dataLayer.configReader.FieldsMapping;
import dataLayer.crud.filters.*;
import iot.jcypher.database.DBAccessFactory;
import iot.jcypher.database.DBProperties;
import iot.jcypher.database.DBType;
import iot.jcypher.database.IDBAccess;
import iot.jcypher.graph.GrNode;
import iot.jcypher.graph.Graph;
import org.bson.Document;
import org.neo4j.driver.v1.AuthTokens;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Stream;

import static dataLayer.crud.filters.CreateSingle.createSingle;

/**
 * Concrete element
 */
public class Neo4jAdapter implements DatabaseAdapter
{

	@Override
	public void revealQuery(VoidFilter voidFilter) { voidFilter.accept(this); }

	@Override
	public Set<Entity> revealQuery(Filter filter) { return filter.accept(this); }

	@Override
	public void executeCreate(CreateSingle createSingle)
	{
		Entity entity = createSingle.getEntity();

		final FieldsMapping fieldMappingFromEntityFields = Conf.getConfiguration().getFieldsMappingFromEntityField(entity.getEntityType(), entity.getFieldsValues().keySet().iterator().next());
		Properties props = new Properties();
		IDBAccess dbAccess = DBAccessFactory.createDBAccess(DBType.REMOTE, props, AuthTokens.basic(fieldMappingFromEntityFields.getUsername(), fieldMappingFromEntityFields.getPassword()));
		props.setProperty(DBProperties.SERVER_ROOT_URI, fieldMappingFromEntityFields.getConnStr());
		Graph graph = Graph.create(dbAccess);
		GrNode node = graph.createNode();
		node.addLabel(entity.getEntityType());
		entity.getFieldsValues()
				.forEach(node::addProperty);
		graph.store();
	}

	@Override
	public void executeCreate(CreateMany createMany)
	{
		Stream.of(createMany.getEntities())
				.forEach(entity -> executeCreate(createSingle(entity)));
	}

	@Override
	public Set<Entity> execute(Eq eq)
	{
		return null;
	}

	@Override
	public Set<Entity> execute(Ne ne)
	{
		return null;
	}

	@Override
	public Set<Entity> execute(Gt gt)
	{
		return null;
	}

	@Override
	public Set<Entity> execute(Lt lt)
	{
		return null;
	}

	@Override
	public Set<Entity> execute(Gte gte)
	{
		return null;
	}

	@Override
	public Set<Entity> execute(Lte lte)
	{
		return null;
	}

	@Override
	public Set<Entity> execute(And and)
	{
		return null;
	}

	@Override
	public Set<Entity> execute(Or or)
	{
		return null;
	}

	@Override
	public Set<Entity> execute(All all)
	{
		return null;
	}
}
