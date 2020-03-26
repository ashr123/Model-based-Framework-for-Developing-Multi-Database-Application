import iot.jcypher.database.DBAccessFactory;
import iot.jcypher.database.DBProperties;
import iot.jcypher.database.DBType;
import iot.jcypher.database.IDBAccess;
import iot.jcypher.database.internal.PlannerStrategy;
import iot.jcypher.database.util.QParamsUtil;
import iot.jcypher.graph.GrNode;
import iot.jcypher.graph.GrRelation;
import iot.jcypher.graph.Graph;
import iot.jcypher.query.JcQuery;
import iot.jcypher.query.JcQueryResult;
import iot.jcypher.query.api.IClause;
import iot.jcypher.query.factories.clause.MATCH;
import iot.jcypher.query.factories.clause.RETURN;
import iot.jcypher.query.result.JcError;
import iot.jcypher.query.values.JcNode;
import iot.jcypher.query.values.JcString;
import iot.jcypher.query.writer.CypherWriter;
import iot.jcypher.query.writer.QueryParam;
import iot.jcypher.query.writer.WriterContext;
import org.neo4j.driver.v1.AuthTokens;

import java.util.List;
import java.util.Properties;

public class Temp
{
	public static void main(String... args)
	{
		Properties props = new Properties();
		props.setProperty(DBProperties.SERVER_ROOT_URI, "bolt://localhost:7687");
		IDBAccess dbAccess = DBAccessFactory.createDBAccess(DBType.REMOTE, props, AuthTokens.basic("neo4j", "neo4j1"));

		// create a new graph model
		Graph g = Graph.create(dbAccess);

		// create a node
		GrNode m = g.createNode();
		// add a label
		m.addLabel("Movie");
		// add properties
		m.addProperty("title", "The Matrix");
		m.addProperty("year", "1999-03-31");

		// create another node
		GrNode k = g.createNode();
		k.addLabel("Actor");
		k.addProperty("name", "Keanu Reeves");

		// create a relation
		GrRelation rel = g.createRelation("ACTS_IN", k, m);
		rel.addProperty("role", "Neo");

		// store the graph
		List<JcError> errors = g.store();
		System.out.println("Errors of store method: " + errors);

		JcNode actor = new JcNode("actor");
		JcNode movie = new JcNode("movie");
//		JcString name = new JcString("name");
		JcQuery query = new JcQuery(PlannerStrategy.DEFAULT);
		query.setClauses(new IClause[]{
				MATCH.node(actor).relation().out().type("ACTS_IN").node(movie),
				RETURN.value(actor),
				RETURN.value(movie)
		});

		printQuery(query);
		JcQueryResult result = dbAccess.execute(query);

		System.out.println("DB errors: " + result.getDBErrors() + "\nGeneral errors: " + result.getGeneralErrors());
		System.out.println(result.resultOf(actor));
		System.out.println(result.resultOf(actor.property("name")));

		List<GrNode> actors = result.resultOf(actor);
		List<GrNode> movies = result.resultOf(movie);

//		Graph graph = result.getGraph();

		GrNode keanu = actors.get(0);
		System.out.println(keanu.getProperty("name").getValue());
		GrNode matrix = movies.get(0);
		System.out.println(matrix.getProperty("title").getValue());
	}

	private static void printQuery(JcQuery query)
	{
		WriterContext context = new WriterContext();
		QueryParam.setExtractParams(query.isExtractParams(), context);
		CypherWriter.toCypherExpression(query, context);
		System.out.println("{\n\tquery: " + context.buffer.toString() + "\n\tparameters: " + QParamsUtil.createQueryParams(context) + "\n}");
	}

//    private static Map<String, List<Map<String, Object>>> getElementFromGr(GrPropertyContainer grPropertyContainer) {
//        HashMap<String, Object> element =
//    }
}
