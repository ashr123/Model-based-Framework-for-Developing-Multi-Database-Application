package dataLayer.crud.dbAdapters;

import dataLayer.configReader.Conf;
import dataLayer.configReader.FieldsMapping;
import dataLayer.crud.Entity;
import dataLayer.crud.filters.*;
import iot.jcypher.database.DBAccessFactory;
import iot.jcypher.database.DBProperties;
import iot.jcypher.database.DBType;
import iot.jcypher.database.IDBAccess;
import iot.jcypher.graph.GrNode;
import iot.jcypher.graph.GrProperty;
import iot.jcypher.graph.Graph;
import iot.jcypher.query.JcQuery;
import iot.jcypher.query.api.IClause;
import iot.jcypher.query.factories.clause.*;
import iot.jcypher.query.values.JcNode;
import org.neo4j.driver.v1.AuthTokens;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dataLayer.crud.filters.CreateSingle.createSingle;

/**
 * Concrete element
 */
public class Neo4jAdapter extends DatabaseAdapter
{
	private Map<FieldsMapping, Map<String, Object>> groupFieldsByFieldsMapping(Entity entity)
	{
		Map<FieldsMapping, Map<String, Object>> result = new HashMap<>();
		entity.getFieldsValues()
				.forEach((field, value) ->
				{
					final FieldsMapping fieldMappingFromEntityFields = Conf.getConfiguration().getFieldsMappingFromEntityField(entity.getEntityType(), field);
					if (fieldMappingFromEntityFields != null)
						result.computeIfAbsent(fieldMappingFromEntityFields, fieldsMapping -> new HashMap<>()).put(field, value);
					else
						throw new NullPointerException("Field " + field + " doesn't exist in entity " + entity.getEntityType());
				});
		return result;
	}

	@Override
	public void executeCreate(CreateSingle createSingle)
	{
		groupFieldsByFieldsMapping(createSingle.getEntity())
				.forEach((fieldsMapping, fields) ->
				{
					final IDBAccess idbAccess = getDBAccess(fieldsMapping);
					try
					{
						Graph graph = Graph.create(idbAccess);
						GrNode node = graph.createNode();
						node.addLabel(createSingle.getEntity().getEntityType());
						node.addProperty("uuid", createSingle.getEntity().getUuid());
						fields.forEach(node::addProperty);
						graph.store();
					} finally
					{
						idbAccess.close();
					}
				});
	}

	@Override
	public void executeCreate(CreateMany createMany)
	{
		Stream.of(createMany.getEntities())
				.forEach(entity -> executeCreate(createSingle(entity)));
	}

	private Entity getEntityFromNode(GrNode grNode)
	{
		return new Entity((String) grNode.getProperty("uuid").getValue(),
				grNode.getLabels().get(0).getName(),
				grNode.getProperties().stream()
						.filter(grProperty -> !(grProperty.getName().equals("_c_version_") || grProperty.getName().equals("uuid")))
						.collect(Collectors.toMap(GrProperty::getName, GrProperty::getValue, (a, b) -> b)));
	}

	private Stream<Entity> query(SimpleFilter simpleFilter, JcQuery jcQuery, JcNode jcNode)
	{
		FieldsMapping fieldsMapping = Conf.getConfiguration().getFieldsMappingFromEntityField(simpleFilter.getEntityType(), simpleFilter.getFieldName());
		IDBAccess idbAccess = getDBAccess(fieldsMapping);
		try
		{
			return idbAccess.execute(jcQuery)
					.resultOf(jcNode).stream()
					.map(this::getEntityFromNode);
		} finally
		{
			idbAccess.close();
		}
	}

	private Stream<Entity> query(String entityType, UUID uuid, FieldsMapping fieldsMapping)
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
					.map(this::getEntityFromNode);
		} finally
		{
			idbAccess.close();
		}
	}

	private void queryDelete(SimpleFilter simpleFilter, JcQuery jcQuery, JcNode jcNode)
	{
		FieldsMapping fieldsMapping = Conf.getConfiguration().getFieldsMappingFromEntityField(simpleFilter.getEntityType(), simpleFilter.getFieldName());
		IDBAccess idbAccess = getDBAccess(fieldsMapping);
		try
		{
			idbAccess.execute(jcQuery);
		} finally
		{
			idbAccess.close();
		}
	}

	private void queryDelete(String entityType, UUID uuid, FieldsMapping fieldsMapping)
	{
		IDBAccess idbAccess = getDBAccess(fieldsMapping);
		JcNode jcNode = new JcNode(entityType);
		JcQuery jcQuery = new JcQuery();
		jcQuery.setClauses(new IClause[]{
				MATCH.node(jcNode).label(entityType).property("uuid").value(uuid), // TODO Maybe we need to map uuid to String?
				DO.DETACH_DELETE(jcNode)
		});
		try
		{
			idbAccess.execute(jcQuery);
		}
		finally
		{
			idbAccess.close();
		}
	}

	@Override
	public Stream<Entity> executeRead(Eq eq)
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
	public Stream<Entity> executeRead(Ne ne)
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
	public Stream<Entity> executeRead(Gt gt)
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
	public Stream<Entity> executeRead(Lt lt)
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
	public Stream<Entity> executeRead(Gte gte)
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
	public Stream<Entity> executeRead(Lte lte)
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
	public Stream<Entity> executeRead(String entityType, UUID uuid, FieldsMapping fieldsMapping)
	{
		return query(entityType, uuid, fieldsMapping);
	}

//	@Override
//	public void executeDelete(Eq eq)
//	{
//		JcNode jcNode = new JcNode(eq.getEntityType());
//		JcQuery jcQuery = new JcQuery();
//		jcQuery.setClauses(new IClause[]{
//				MATCH.node(jcNode).label(eq.getEntityType()),
//				WHERE.valueOf(jcNode.property(eq.getFieldName())).EQUALS(eq.getValue()),
//				DO.DETACH_DELETE(jcNode)
//		});
//		queryDelete(eq, jcQuery, jcNode);
//	}
//
//	@Override
//	public void executeDelete(Ne ne)
//	{
//		JcNode jcNode = new JcNode(ne.getEntityType());
//		JcQuery jcQuery = new JcQuery();
//		jcQuery.setClauses(new IClause[]{
//				MATCH.node(jcNode).label(ne.getEntityType()),
//				WHERE.valueOf(jcNode.property(ne.getFieldName())).NOT_EQUALS(ne.getValue()),
//				DO.DETACH_DELETE(jcNode)
//		});
//		queryDelete(ne, jcQuery, jcNode);
//	}
//
//	@Override
//	public void executeDelete(Gt gt)
//	{
//		JcNode jcNode = new JcNode(gt.getEntityType());
//		JcQuery jcQuery = new JcQuery();
//		jcQuery.setClauses(new IClause[]{
//				MATCH.node(jcNode).label(gt.getEntityType()),
//				WHERE.valueOf(jcNode.property(gt.getFieldName())).GT(gt.getValue()),
//				DO.DETACH_DELETE(jcNode)
//		});
//		queryDelete(gt, jcQuery, jcNode);
//	}
//
//	@Override
//	public void executeDelete(Lt lt)
//	{
//		JcNode jcNode = new JcNode(lt.getEntityType());
//		JcQuery jcQuery = new JcQuery();
//		jcQuery.setClauses(new IClause[]{
//				MATCH.node(jcNode).label(lt.getEntityType()),
//				WHERE.valueOf(jcNode.property(lt.getFieldName())).LT(lt.getValue()),
//				DO.DETACH_DELETE(jcNode)
//		});
//		queryDelete(lt, jcQuery, jcNode);
//	}
//
//	@Override
//	public void executeDelete(Gte gte)
//	{
//		JcNode jcNode = new JcNode(gte.getEntityType());
//		JcQuery jcQuery = new JcQuery();
//		jcQuery.setClauses(new IClause[]{
//				MATCH.node(jcNode).label(gte.getEntityType()),
//				WHERE.valueOf(jcNode.property(gte.getFieldName())).GTE(gte.getValue()),
//				DO.DETACH_DELETE(jcNode)
//		});
//		queryDelete(gte, jcQuery, jcNode);
//	}
//
//	@Override
//	public void executeDelete(Lte lte)
//	{
//		JcNode jcNode = new JcNode(lte.getEntityType());
//		JcQuery jcQuery = new JcQuery();
//		jcQuery.setClauses(new IClause[]{
//				MATCH.node(jcNode).label(lte.getEntityType()),
//				WHERE.valueOf(jcNode.property(lte.getFieldName())).LTE(lte.getValue()),
//				DO.DETACH_DELETE(jcNode)
//		});
//		queryDelete(lte, jcQuery, jcNode);
//	}

//	@Override
//	public void executeDelete(String entityType, UUID uuid, FieldsMapping fieldsMapping)
//	{
//		queryDelete(entityType, uuid, fieldsMapping);
//	}

	@Override
	public void executeDelete(FieldsMapping fieldsMapping, Map<String, Collection<UUID>> typesAndUuids)
	{
		IDBAccess idbAccess = getDBAccess(fieldsMapping);
		try
		{
			typesAndUuids.forEach((entityType, uuids) ->
			{
				JcNode jcNode = new JcNode(entityType);
				JcQuery jcQuery = new JcQuery();
				jcQuery.setClauses(new IClause[]{
						MATCH.node(jcNode).label(entityType),
						WHERE.valueOf(jcNode.property("uuid")).IN_list(uuids/*.stream().map(UUID::toString).toArray()*/), // TODO Maybe we need to map uuids to String?
						DO.DETACH_DELETE(jcNode)
				});

				idbAccess.execute(jcQuery);
			});
		}
		finally
		{
			idbAccess.close();
		}
	}

	@Override
	public void executeUpdate(FieldsMapping fieldsMapping, Map<String, Map<Collection<UUID>, Map<String, Object>>> updates)
	{

	}

	public IDBAccess getDBAccess(FieldsMapping fieldsMapping){
		Properties props = new Properties();
		props.setProperty(DBProperties.SERVER_ROOT_URI, fieldsMapping.getConnStr());
		return DBAccessFactory.createDBAccess(DBType.REMOTE, props, AuthTokens.basic(fieldsMapping.getUsername(), fieldsMapping.getPassword()));
	}
}

