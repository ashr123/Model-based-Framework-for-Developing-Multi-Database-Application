package dataLayer.crud.dbAdapters;

import dataLayer.configReader.Conf;
import dataLayer.configReader.Entity;
import dataLayer.configReader.FieldsMapping;
import dataLayer.crud.filters.*;
import iot.jcypher.database.DBAccessFactory;
import iot.jcypher.database.DBProperties;
import iot.jcypher.database.DBType;
import iot.jcypher.database.IDBAccess;
import iot.jcypher.graph.GrNode;
import iot.jcypher.graph.GrProperty;
import iot.jcypher.graph.Graph;
import iot.jcypher.query.JcQuery;
import iot.jcypher.query.JcQueryResult;
import iot.jcypher.query.api.IClause;
import iot.jcypher.query.factories.clause.MATCH;
import iot.jcypher.query.factories.clause.RETURN;
import iot.jcypher.query.factories.clause.WHERE;
import iot.jcypher.query.values.JcNode;
import org.neo4j.driver.v1.AuthTokens;

import java.util.*;
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
					Properties props = new Properties(1);
					props.setProperty(DBProperties.SERVER_ROOT_URI, fieldsMapping.getConnStr() + '/' + fieldsMapping.getLocation());
					final IDBAccess dbAccess = DBAccessFactory.createDBAccess(DBType.REMOTE, props, AuthTokens.basic(fieldsMapping.getUsername(), fieldsMapping.getPassword()));
					Graph graph = Graph.create(dbAccess);
					GrNode node = graph.createNode();
					node.addLabel(createSingle.getEntity().getEntityType());
					fields.forEach(node::addProperty);
					graph.store();
					dbAccess.close();
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
		List<GrProperty> grProperties = grNode.getProperties();
		String grLabel = grNode.getLabels().get(0).getName();
		Map<String, Object> fieldsMap = new HashMap<>();
		grProperties.forEach(grProperty -> fieldsMap.put(grProperty.getName(), grProperty.getValue()));
		return new Entity((UUID) fieldsMap.remove("uuid"), grLabel, fieldsMap);
	}

	private Set<Entity> query(SimpleFilter simpleFilter, JcQuery jcQuery, JcNode jcNode)
	{
		final FieldsMapping fieldsMapping = Conf.getConfiguration().getFieldsMappingFromEntityField(simpleFilter.getEntityName(), simpleFilter.getFieldName());
		Properties props = new Properties();
		IDBAccess dbAccess = DBAccessFactory.createDBAccess(DBType.REMOTE, props, AuthTokens.basic(fieldsMapping.getUsername(), fieldsMapping.getPassword()));
		props.setProperty(DBProperties.SERVER_ROOT_URI, fieldsMapping.getConnStr());
		JcQueryResult jcQueryResult = dbAccess.execute(jcQuery);
		List<GrNode> grNodes = jcQueryResult.resultOf(jcNode);
		Set<Entity> result = new HashSet<>();
		grNodes.forEach(grNode -> result.add(getEntityFromNode(grNode)));
		dbAccess.close();
		return result;
	}

	@Override
	public Set<Entity> execute(Eq eq)
	{
		JcNode jcNode = new JcNode(eq.getEntityName());
		JcQuery jcQuery = new JcQuery();
		jcQuery.setClauses(new IClause[]{
				MATCH.node(jcNode).label(eq.getEntityName()),
				WHERE.valueOf(jcNode.property(eq.getFieldName())).EQUALS(eq.getValue()),
				RETURN.value(jcNode)
		});
		return query(eq, jcQuery, jcNode);
	}

	@Override
	public Set<Entity> execute(Ne ne)
	{
		JcNode jcNode = new JcNode(ne.getEntityName());
		JcQuery jcQuery = new JcQuery();
		jcQuery.setClauses(new IClause[]{
				MATCH.node(jcNode).label(ne.getEntityName()),
				WHERE.valueOf(jcNode.property(ne.getFieldName())).NOT_EQUALS(ne.getValue()),
				RETURN.value(jcNode)
		});
		return query(ne, jcQuery, jcNode);
	}

	@Override
	public Set<Entity> execute(Gt gt)
	{
		JcNode jcNode = new JcNode(gt.getEntityName());
		JcQuery jcQuery = new JcQuery();
		jcQuery.setClauses(new IClause[]{
				MATCH.node(jcNode).label(gt.getEntityName()),
				WHERE.valueOf(jcNode.property(gt.getFieldName())).GT(gt.getValue()),
				RETURN.value(jcNode)
		});
		return query(gt, jcQuery, jcNode);
	}

	@Override
	public Set<Entity> execute(Lt lt)
	{
		JcNode jcNode = new JcNode(lt.getEntityName());
		JcQuery jcQuery = new JcQuery();
		jcQuery.setClauses(new IClause[]{
				MATCH.node(jcNode).label(lt.getEntityName()),
				WHERE.valueOf(jcNode.property(lt.getFieldName())).LT(lt.getValue()),
				RETURN.value(jcNode)
		});
		return query(lt, jcQuery, jcNode);
	}

	@Override
	public Set<Entity> execute(Gte gte)
	{
		JcNode jcNode = new JcNode(gte.getEntityName());
		JcQuery jcQuery = new JcQuery();
		jcQuery.setClauses(new IClause[]{
				MATCH.node(jcNode).label(gte.getEntityName()),
				WHERE.valueOf(jcNode.property(gte.getFieldName())).GTE(gte.getValue()),
				RETURN.value(jcNode)
		});
		return query(gte, jcQuery, jcNode);
	}

	@Override
	public Set<Entity> execute(Lte lte)
	{
		JcNode jcNode = new JcNode(lte.getEntityName());
		JcQuery jcQuery = new JcQuery();
		jcQuery.setClauses(new IClause[]{
				MATCH.node(jcNode).label(lte.getEntityName()),
				WHERE.valueOf(jcNode.property(lte.getFieldName())).LTE(lte.getValue()),
				RETURN.value(jcNode)
		});
		return query(lte, jcQuery, jcNode);
	}

	@Override
	public Set<Entity> execute(UUIDEq uuidEq)
	{
		return null;
	}
}
