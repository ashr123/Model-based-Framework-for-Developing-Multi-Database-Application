package dataLayer.crud.dbAdapters;

import dataLayer.crud.Entity;
import dataLayer.crud.Pair;
import dataLayer.crud.Query;
import dataLayer.crud.filters.*;
import dataLayer.readers.configReader.Conf;
import dataLayer.readers.configReader.FieldsMapping;
import iot.jcypher.database.DBAccessFactory;
import iot.jcypher.database.DBProperties;
import iot.jcypher.database.DBType;
import iot.jcypher.database.IDBAccess;
import iot.jcypher.graph.GrNode;
import iot.jcypher.graph.GrProperty;
import iot.jcypher.graph.Graph;
import iot.jcypher.query.JcQuery;
import iot.jcypher.query.api.IClause;
import iot.jcypher.query.factories.clause.DO;
import iot.jcypher.query.factories.clause.MATCH;
import iot.jcypher.query.factories.clause.RETURN;
import iot.jcypher.query.factories.clause.WHERE;
import iot.jcypher.query.values.JcNode;
import org.neo4j.driver.v1.AuthTokens;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Yossi Landa
 */
public class Neo4jAdapter extends DatabaseAdapter
{
	Neo4jAdapter()
	{
	}

	private static IDBAccess getDBAccess(FieldsMapping fieldsMapping)
	{
		Properties props = new Properties();
		props.setProperty(DBProperties.SERVER_ROOT_URI, fieldsMapping.getConnStr());
		return DBAccessFactory.createDBAccess(DBType.REMOTE, props, AuthTokens.basic(fieldsMapping.getUsername(), fieldsMapping.getPassword()));
	}

	private static Entity getEntityFromNode(GrNode grNode)
	{
		return new Entity((String) grNode.getProperty("uuid").getValue(),
				grNode.getLabels().get(0).getName(),
				grNode.getProperties().stream()
						.filter(grProperty -> !(grProperty.getName().equals("_c_version_") || grProperty.getName().equals("uuid")))
						.collect(Collectors.toMap(GrProperty::getName, GrProperty::getValue, (a, b) -> b)),
				FRIEND);
	}

	private static Stream<Entity> query(SimpleFilter simpleFilter, JcQuery jcQuery, JcNode jcNode)
	{
		FieldsMapping fieldsMapping = Conf.getConfiguration().getFieldsMappingFromEntityField(simpleFilter.getEntityType(), simpleFilter.getFieldName());
		IDBAccess idbAccess = getDBAccess(fieldsMapping);
		try
		{
			return idbAccess.execute(jcQuery)
					.resultOf(jcNode).stream()
					.map(Neo4jAdapter::getEntityFromNode);
		}
		finally
		{
			idbAccess.close();
		}
	}

	private static Stream<Entity> query(String entityType, UUID uuid, FieldsMapping fieldsMapping)
	{
		IDBAccess idbAccess = getDBAccess(fieldsMapping);
		JcNode jcNode = new JcNode(entityType);
		JcQuery jcQuery = new JcQuery();
		jcQuery.setClauses(new IClause[]{
				MATCH.node(jcNode).label(entityType),
				WHERE.valueOf(jcNode.property("uuid")).EQUALS(uuid),
				RETURN.value(jcNode)
		});
		try
		{
			return idbAccess.execute(jcQuery)
					.resultOf(jcNode).stream()
					.map(Neo4jAdapter::getEntityFromNode);
		}
		finally
		{
			idbAccess.close();
		}
	}

	@Override
	protected Stream<Entity> makeEntities(FieldsMapping fieldsMapping, String entityType)
	{
		IDBAccess idbAccess = getDBAccess(fieldsMapping);
		JcNode jcNode = new JcNode(entityType);
		JcQuery jcQuery = new JcQuery();
		jcQuery.setClauses(new IClause[]{
				MATCH.node(jcNode).label(entityType),
				RETURN.value(jcNode)
		});
		try
		{
			return idbAccess.execute(jcQuery)
					.resultOf(jcNode).stream()
					.map(Neo4jAdapter::getEntityFromNode);
		}
		finally
		{
			idbAccess.close();
		}
	}

	@Override
	public void executeCreate(Entity entity, Query.Friend friend)
	{
		groupFieldsByFieldsMapping(entity, dataLayer.crud.dbAdapters.DBType.NEO4J)
				.forEach((fieldsMapping, fields) ->
				{
					final IDBAccess idbAccess = getDBAccess(fieldsMapping);
					try
					{
						Graph graph = Graph.create(idbAccess);
						GrNode node = graph.createNode();
						node.addLabel(entity.getEntityType());
						fields.forEach(node::addProperty);
						graph.store();
					}
					finally
					{
						idbAccess.close();
					}
				});
	}

	@Override
	public Stream<Entity> executeRead(Eq eq, Query.Friend friend)
	{
		JcNode jcNode = new JcNode(eq.getEntityType());
		JcQuery jcQuery = new JcQuery();
		jcQuery.setClauses(new IClause[]{
				MATCH.node(jcNode).label(eq.getEntityType()),
				WHERE.valueOf(jcNode.property(eq.getFieldName())).EQUALS(eq.getValue()),
				RETURN.value(jcNode)
		});
		return query(eq, jcQuery, jcNode);
	}

	@Override
	public Stream<Entity> executeRead(Ne ne, Query.Friend friend)
	{
		JcNode jcNode = new JcNode(ne.getEntityType());
		JcQuery jcQuery = new JcQuery();
		jcQuery.setClauses(new IClause[]{
				MATCH.node(jcNode).label(ne.getEntityType()),
				WHERE.valueOf(jcNode.property(ne.getFieldName())).NOT_EQUALS(ne.getValue()),
				RETURN.value(jcNode)
		});
		return query(ne, jcQuery, jcNode);
	}

	@Override
	public Stream<Entity> executeRead(Gt gt, Query.Friend friend)
	{
		JcNode jcNode = new JcNode(gt.getEntityType());
		JcQuery jcQuery = new JcQuery();
		jcQuery.setClauses(new IClause[]{
				MATCH.node(jcNode).label(gt.getEntityType()),
				WHERE.valueOf(jcNode.property(gt.getFieldName())).GT(gt.getValue()),
				RETURN.value(jcNode)
		});
		return query(gt, jcQuery, jcNode);
	}

	@Override
	public Stream<Entity> executeRead(Lt lt, Query.Friend friend)
	{
		JcNode jcNode = new JcNode(lt.getEntityType());
		JcQuery jcQuery = new JcQuery();
		jcQuery.setClauses(new IClause[]{
				MATCH.node(jcNode).label(lt.getEntityType()),
				WHERE.valueOf(jcNode.property(lt.getFieldName())).LT(lt.getValue()),
				RETURN.value(jcNode)
		});
		return query(lt, jcQuery, jcNode);
	}

	@Override
	public Stream<Entity> executeRead(Gte gte, Query.Friend friend)
	{
		JcNode jcNode = new JcNode(gte.getEntityType());
		JcQuery jcQuery = new JcQuery();
		jcQuery.setClauses(new IClause[]{
				MATCH.node(jcNode).label(gte.getEntityType()),
				WHERE.valueOf(jcNode.property(gte.getFieldName())).GTE(gte.getValue()),
				RETURN.value(jcNode)
		});
		return query(gte, jcQuery, jcNode);
	}

	@Override
	public Stream<Entity> executeRead(Lte lte, Query.Friend friend)
	{
		JcNode jcNode = new JcNode(lte.getEntityType());
		JcQuery jcQuery = new JcQuery();
		jcQuery.setClauses(new IClause[]{
				MATCH.node(jcNode).label(lte.getEntityType()),
				WHERE.valueOf(jcNode.property(lte.getFieldName())).LTE(lte.getValue()),
				RETURN.value(jcNode)
		});
		return query(lte, jcQuery, jcNode);
	}

	@Override
	protected Stream<Entity> executeRead(FieldsMapping fieldsMapping, String entityType, UUID uuid)
	{
		return query(entityType, uuid, fieldsMapping);
	}

	@Override
	public void executeDelete(FieldsMapping fieldsMapping, Map<String, Collection<UUID>> typesAndUuids, Query.Friend friend)
	{
		IDBAccess idbAccess = getDBAccess(fieldsMapping);
		try
		{
			JcNode jcNode = new JcNode("n");
			JcQuery jcQuery = new JcQuery();
			jcQuery.setClauses(new IClause[]{
					MATCH.node(jcNode),
					WHERE.valueOf(jcNode.property("uuid")).IN_list(typesAndUuids.values().stream()
							.flatMap(Collection::stream)
							.toArray()),
					DO.DETACH_DELETE(jcNode)
			});

			idbAccess.execute(jcQuery);
		}
		finally
		{
			idbAccess.close();
		}
	}

	@Override
	public void executeUpdate(FieldsMapping fieldsMapping, Map<String, Pair<Collection<UUID>, Map<String, Object>>> updates, Query.Friend friend)
	{
		IDBAccess idbAccess = getDBAccess(fieldsMapping);
		try
		{
			updates.forEach((entityType, uuidsAndUpdates) ->
			{
				if (!uuidsAndUpdates.getSecond().isEmpty())
				{
					editFieldValueMap(entityType, uuidsAndUpdates.getSecond());
					JcNode jcNode = new JcNode(entityType);
					JcQuery jcQuery = new JcQuery();
					List<IClause> clauses = new ArrayList<>(2 + uuidsAndUpdates.getSecond().size());
					clauses.add(MATCH.node(jcNode).label(entityType));
					clauses.add(WHERE.valueOf(jcNode.property("uuid")).IN_list(uuidsAndUpdates.getFirst()));
					uuidsAndUpdates.getSecond()
							.forEach((field, value) ->
									clauses.add(DO.SET(jcNode.property(field)).to(value)));
					jcQuery.setClauses(clauses.toArray(IClause[]::new));
					idbAccess.execute(jcQuery);
				}
			});
		}
		finally
		{
			idbAccess.close();
		}
	}
}

