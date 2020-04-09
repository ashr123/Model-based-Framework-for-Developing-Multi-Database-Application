package dataLayer.crud.dbAdapters;

import dataLayer.configReader.Conf;
import dataLayer.configReader.Entity;
import dataLayer.crud.filters.CreateMany;
import iot.jcypher.database.DBAccessFactory;
import iot.jcypher.database.DBProperties;
import iot.jcypher.database.DBType;
import iot.jcypher.database.IDBAccess;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.v1.AuthTokens;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

class Neo4jAdapterTest
{
	private final Entity
			roy = Entity.of("Person",
			Map.of("name", "Roy",
					"age", 27,
					"phoneNumber", "0546815181",
					"emailAddress", "ashr@post.bgu.ac.il")),
			yossi = Entity.of("Person",
					Map.of("name", "Yossi",
							"age", 22,
							"phoneNumber", "0587158627",
							"emailAddress", "yossilan@post.bgu.ac.il")),
			karin = Entity.of("Person",
					Map.of("name", "Karin",
							"age", 26,
							"phoneNumber", "0504563434",
							"emailAddress", "davidz@post.bgu.ac.il"));
//	private Set<Entity> removeId(Set<Entity> input)
//	{
//		input.forEach(map -> map.remove("_id"));
//		return input;
//	}

	@BeforeAll
	void setUp() throws IOException
	{
		Conf.loadConfiguration(MongoDBAdapterTest.class.getResource("/configurations/configurationNeo4j.json"));
//		try (MongoClient mongoClient = MongoClients.create())
//		{
//			mongoClient.getDatabase("TestDB").drop();
//			mongoClient.getDatabase("mydb").drop();
////					.insertMany(asList(new Document("name", "Roy")
////									.append("age", 27)
////									.append("phoneNumber", "0546815181")
////									.append("emailAddress", "ashr@post.bgu.ac.il"),
////							new Document("name", "Yossi")
////									.append("age", 22)
////									.append("phoneNumber", "0587158627")
////									.append("emailAddress", "yossilan@post.bgu.ac.il"),
////							new Document("name", "Karin")
////									.append("age", 26)
////									.append("phoneNumber", "0504563434")
////									.append("emailAddress", "davidz@post.bgu.ac.il")));
//		}
		Neo4jAdapter neo4jAdapter = new Neo4jAdapter();
		neo4jAdapter.revealQuery(CreateMany.createMany(roy, yossi, karin));
	}

	@AfterAll
	void tearDown()
	{
		Properties props = new Properties();
		props.setProperty(DBProperties.SERVER_ROOT_URI, "bolt://localhost:7687");
		IDBAccess dbAccess = DBAccessFactory.createDBAccess(DBType.REMOTE, props, AuthTokens.basic("neo4j", "neo4j1"));
		dbAccess.clearDatabase();
		dbAccess.close();

		props = new Properties();
		props.setProperty(DBProperties.SERVER_ROOT_URI, "bolt://localhost:11008");
		dbAccess = DBAccessFactory.createDBAccess(DBType.REMOTE, props, AuthTokens.basic("neo4j", "neo4j1"));
		dbAccess.clearDatabase();
		dbAccess.close();
	}

	@Test
	void revealQuery()
	{
	}

	@Test
	void testRevealQuery()
	{
	}

	@Test
	void execute()
	{
	}

	@Test
	void testExecute()
	{
	}

	@Test
	void executeCreate()
	{
	}

	@Test
	void testExecuteCreate()
	{
	}

	@Test
	void testExecute1()
	{
	}

	@Test
	void testExecute2()
	{
	}

	@Test
	void testExecute3()
	{
	}

	@Test
	void testExecute4()
	{
	}

	@Test
	void testExecute5()
	{
	}

	@Test
	void testExecute6()
	{
	}
}
