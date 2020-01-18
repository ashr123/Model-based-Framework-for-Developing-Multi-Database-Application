package dataLayer.queryAdapters.dbAdapters;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import dataLayer.configReader.Conf;
import dataLayer.queryAdapters.crud.Eq;
import org.bson.Document;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MongoDBAdapterTest
{

	final MongoDBAdapter mongoDBAdapter = new MongoDBAdapter();

	@BeforeAll
	void setUp() throws IOException
	{
		Conf.loadConfiguration(MongoDBAdapterTest.class.getResource("/configuration.json"));
		try (MongoClient mongoClient = MongoClients.create())
		{
			mongoClient.getDatabase("TestDB")
					.getCollection("Person")
					.insertMany(asList(new Document("name", "Roy")
									.append("age", 27)
									.append("phoneNumber", "0546815181")
									.append("emailAddress", "ashr@post.bgu.ac.il"),
							new Document("name", "Yossi")
									.append("age", 22)
									.append("phoneNumber", "0587158627")
									.append("emailAddress", "Alice@post.bgu.ac.il"),
							new Document("name", "Karin")
									.append("age", 26)
									.append("phoneNumber", "0504563434")
									.append("emailAddress", "davidz@post.bgu.ac.il")));
		}
	}

	@AfterAll
	void tearDown()
	{
		try (MongoClient mongoClient = MongoClients.create())
		{
			mongoClient.getDatabase("TestDB").drop();
		}
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
	void executeCreate()
	{
	}

	@Test
	void testExecuteCreate()
	{
	}

	@Test
	void testExecuteEq()
	{
		List<Map<String, Object>> result = mongoDBAdapter.revealQuery(Eq.eq("Person", "name", "Roy"));
		System.out.println(result);
	}

	@Test
	void testExecuteNe()
	{
	}

	@Test
	void testExecuteGt()
	{
	}

	@Test
	void testExecuteLt()
	{
	}

	@Test
	void testExecuteGte()
	{
	}

	@Test
	void testExecuteLte()
	{
	}

	@Test
	void testExecuteAnd()
	{
	}
}
