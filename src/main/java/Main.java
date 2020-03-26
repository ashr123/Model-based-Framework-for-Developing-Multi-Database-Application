//import com.mongodb.client.MongoClient;
//import com.mongodb.client.MongoClients;
//import dataLayer.configReader.Conf;
//import org.bson.Document;
//import org.neo4j.dbms.api.DatabaseManagementService;
//import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
//import org.neo4j.graphdb.GraphDatabaseService;
//
//import java.io.IOException;
//import java.util.Arrays;
//
//import static org.neo4j.configuration.GraphDatabaseSettings.DEFAULT_DATABASE_NAME;
//
//public class Main
//{
//	public static void main(String[] args) throws IOException
//	{
//		Conf.loadConfiguration(Main.class.getResource("/configuration.json"));
////		try (MongoClient mongoClient = MongoClients.create())
////		{
////			mongoClient.getDatabase("myDB").drop();
////			mongoClient.getDatabase("myDB")
////					.getCollection("Person")
////					.insertOne(new Document("name", "Alice")
////							.append("age", 18)
////							.append("phoneNumber", "0504563434")
////							.append("emailAddress", "Alice@Bob.com"));
////		}
//
//		GraphDatabaseService managementService = new DatabaseManagementServiceBuilder(databaseDirectory).build();
//		GragraphDb = managementService..databaseName(DEFAULT_DATABASE_NAME);
//		​registerShutdownHook(managementService);
//
//		System.out.println(Arrays.stream(new int[]{1, 2, 3})
//				.reduce((acc, b) ->
//				{
//					System.out.println("acc: " + acc + ", b: " + b);
//					return acc + b;
//				})
//				.getAsInt());
//
////		new MongoDBAdapter()
////				.revealQuery(createMany()
////						.add(entity("Person")
////								.append("name", "Karin")
////								.append("age", 26)
////								.append("phoneNumber", "496351")
////								.append("emailAddress", "karin@gmail.com"))
////						.add(entity("Person")
////								.append("name", "Yossi")
////								.append("age", 21)
////								.append("phoneNumber", "0587158627")
////								.append("emailAddress", "yossi@gmail.com")));
//	}
//
//	private static void registerShutdownHook(final DatabaseManagementService managementService)
//	{
//		// Registers a shutdown hook for the Neo4j instance so that it
//		// shuts down nicely when the VM exits (even if you "Ctrl-C" the
//		// running application).
//		Runtime.getRuntime().addShutdownHook(new Thread(managementService::shutdown));
//	}
//
//}

import iot.jcypher.database.DBAccessFactory;
import iot.jcypher.database.DBProperties;
import iot.jcypher.database.DBType;
import iot.jcypher.database.IDBAccess;
import iot.jcypher.database.util.QParamsUtil;
import iot.jcypher.graph.GrNode;
import iot.jcypher.graph.GrProperty;
import iot.jcypher.graph.GrPropertyContainer;
import iot.jcypher.graph.Graph;
import iot.jcypher.query.JcQuery;
import iot.jcypher.query.JcQueryResult;
import iot.jcypher.query.factories.clause.*;
import iot.jcypher.query.values.JcNode;
import iot.jcypher.query.values.JcNumber;
import iot.jcypher.query.values.JcRelation;
import iot.jcypher.query.values.JcString;
import iot.jcypher.query.writer.CypherWriter;
import iot.jcypher.query.writer.QueryParam;
import iot.jcypher.query.writer.WriterContext;
import org.neo4j.driver.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

//import static org.neo4j.driver.Values.parameters;

public class Main
{
	public static void main(String... args)
	{
//		try (HelloWorldExample greeter = new HelloWorldExample("bolt://localhost:7687", "neo4j", "neo4j1"))
//		{
//			greeter.printGreeting("hello, world");
//		}
		try (HelloWorldExample2 greeter = new HelloWorldExample2("bolt://localhost:7687", "neo4j", "neo4j1"))
		{
			greeter.query();
		}
	}

	private static class HelloWorldExample2 implements AutoCloseable
	{
		private final IDBAccess r_dbAccess;

		public HelloWorldExample2(String uri, String user, String password)
		{

			Properties props = new Properties();
			props.setProperty(DBProperties.SERVER_ROOT_URI, uri);
			r_dbAccess = DBAccessFactory.createDBAccess(DBType.REMOTE, props, AuthTokens.basic(user, password));
		}

		private static void printQuery(JcQuery query)
		{
			WriterContext context = new WriterContext();
			QueryParam.setExtractParams(query.isExtractParams(), context);
			CypherWriter.toCypherExpression(query, context);
			System.out.println("{\n\tquery: " + context.buffer.toString() + "\n\tparameters: " + QParamsUtil.createQueryParams(context) + "\n}");
		}

		public void query()
		{
			JcNode
					people = new JcNode("people"),
					m = new JcNode("m");
			JcRelation relatedTo = new JcRelation("relatedTo");
			JcString ty = new JcString("ty"), ty2 = new JcString("ty2"), dummy = new JcString("dummy");
			JcQuery query = new JcQuery(
					MATCH.node(people).label("Person").relation(relatedTo).node(m).label("Movie"),
					WHERE.valueOf(m.property("title")).EQUALS("Apollo 13"),
					RETURN.value(people.property("name")),
					RETURN.value(relatedTo.type()).AS(ty)/*,
					RETURN.value(relatedTo)*/);
			printQuery(query);

			query = new JcQuery(
					MATCH.node(people).label("Person"),
//					WHERE.valueOf(m.property("title")).EQUALS("Apollo 13"),
					RETURN.value(people.property("name")).AS(ty),
					UNION.distinct(),
					MATCH.node(m).label("Movie"),
					RETURN.value(m.id()).AS(ty)/*,
					RETURN.value(relatedTo)*/);
			query = new JcQuery(
					MATCH.node(people).label("Person"),
					WITH.value(people.property("name")).AS(ty),
					MATCH.node(m).label("Movie"),
					RETURN.value(ty),
					RETURN.value(m.id()));
			query = new JcQuery(
					MATCH.node(people).label("Person"),
					WITH.value(new JcNumber("1")).AS(dummy),
					MATCH.node(m).label("Movie"),
					WHERE.valueOf(m.property("title")).EQUALS("Apollo 13"),
					RETURN.value(m.property("title")),
					RETURN.value(people).AS(ty));

			JcNode person = new JcNode("person");
			JcString name = new JcString("name");
			JcString born = new JcString("born");
			query = new JcQuery(
					MATCH.node(person).label("Person").relation().out().type("ACTED_IN").node(),
					RETURN.DISTINCT().value(person.property("name")).AS(name),
					RETURN.value(person.property("born")).AS(born));
			printQuery(query);
			final JcQueryResult result = r_dbAccess.execute(query);
			System.out.println("DB errors: " + result.getDBErrors() +
					"\nGeneral errors: " + result.getGeneralErrors() /*+
					"\nResult: " + (*//*result.resultOf(relatedTo).size()==*//*result.resultOf(ty)*//*.size()*//*)*/);

			List<?> people1 = result.resultOf(born);
			Graph graph = result.getGraph();

			Object keanu = people1.get(0);
			System.out.println(keanu);
//			System.out.println(keanu.getProperty("name").getValue());
		}

//		protected String printJSON(JcQuery query, Format pretty)
//		{
//			WriterContext context = new WriterContext();
//			context.cypherFormat = pretty;
//			JSONWriter.toJSON(query, context);
////			if (this.print)
////			{
////				System.out.println("");
////				System.out.println(context.buffer.toString());
////			}
//			return context.buffer.toString();
//		}

		@Override
		public void close()
		{
			r_dbAccess.close();
		}

		private static Map<String, Object> getElementsForGr(GrPropertyContainer propertyContainer)
		{
			return propertyContainer.getProperties().stream()
					.collect(Collectors.toMap(GrProperty::getName, GrProperty::getValue, (a, b) -> b));
		}

//		private static Map<String, List<Map<String, Object>>> getAllEntities(Graph graph)
//		{
//			graph.
//		}
	}

	private static class HelloWorldExample implements AutoCloseable
	{
		private final Driver driver;

		public HelloWorldExample(String uri, String user, String password)
		{
			driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
		}

		private static Result execute(JcQuery query, Transaction tx)
		{
			WriterContext context = new WriterContext();
			QueryParam.setExtractParams(query.isExtractParams(), context);
			CypherWriter.toCypherExpression(query, context);
			String cypher = context.buffer.toString();
			Map<String, Object> paramsMap = QParamsUtil.createQueryParams(context);
			System.out.println("{\n\tquery: " + cypher + "\n\tparameters: " + paramsMap + "\n}");
			return paramsMap != null ? tx.run(cypher, paramsMap) : tx.run(cypher);
		}

		@Override
		public void close()
		{
			driver.close();
		}

		public void printGreeting(final String message)
		{
			try (Session session = driver.session())
			{
				session.readTransaction(tx ->
				{
					JcNode movie = new JcNode("movie");
					execute(new JcQuery(
									MATCH.node(movie).label("Movie"),
									WHERE.valueOf(movie.property("title")).EQUALS("The Polar Express"),
									RETURN.value(movie.property("title"))),
							tx).stream()
							.forEach(record -> System.out.println(record.get("movie.title")));
					return null;
				});

				session.readTransaction(tx ->
				{
					tx.run("MATCH (people:Person)-[relatedTo]-(m:Movie) " +
							"WHERE m.title = \"Cloud Atlas\" " +
							"RETURN people.name, Type(relatedTo), relatedTo").stream()
							.forEach(record -> System.out.println(record.get("people.name") + ", " + record.get("Type(relatedTo)") + ", " + record.get("relatedTo").asRelationship().asMap()));
					return null;
				});

				session.readTransaction(tx ->
				{
					JcNode
							people = new JcNode("people"),
							m = new JcNode("m");
					JcRelation relatedTo = new JcRelation("relatedTo");
					execute(new JcQuery(
									MATCH.node(people).label("Person").relation(relatedTo).node(m).label("Movie"),
									WHERE.valueOf(m.property("title")).EQUALS("Cloud Atlas"),
									RETURN.value(people.property("name")), RETURN.value(relatedTo.type()), RETURN.value(relatedTo)),
							tx).stream()
							.forEach(record -> System.out.println(record.get("people.name") + ", " + record.get("type(relatedTo)") + ", " + record.get("relatedTo").asRelationship().asMap()));
					return null;
				});

				session.readTransaction(tx ->
				{
					tx.run("MATCH (people:Person)-[relatedTo]-(:Movie {title: \"Cloud Atlas\"}) " +
							"RETURN people.name, Type(relatedTo), relatedTo").stream()
							.forEach(record -> System.out.println(record.get("people.name") + ", " + record.get("Type(relatedTo)") + ", " + record.get("relatedTo").asRelationship().asMap()));
					return null;
				});

//				session.run("MATCH (people:Person)-[relatedTo]-(:Movie {title: \"Cloud Atlas\"}) " +
//						"RETURN people.name, Type(relatedTo), relatedTo", TransactionConfig.builder().build())
				session.readTransaction(tx ->
				{
					JcNode people = new JcNode("people");
					JcRelation relatedTo = new JcRelation("relatedTo");
					execute(new JcQuery(
									MATCH.node(people).label("Person").relation(relatedTo).node().label("Movie").property("title").value("Cloud Atlas"),
									RETURN.value(people.property("name")), RETURN.value(relatedTo.type()), RETURN.value(relatedTo)),
							tx).stream()
							.forEach(record -> System.out.println(record.get("people.name") + ", " + record.get("type(relatedTo)") + ", " + record.get("relatedTo").asRelationship().asMap()));
					return null;
				});
//			System.out.println(result.list());
//			result.stream()
//					.forEach(record -> System.out.println(record.get("title")));
			}
		}
	}
}
