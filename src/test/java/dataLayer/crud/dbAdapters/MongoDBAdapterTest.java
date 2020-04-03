package dataLayer.crud.dbAdapters;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import dataLayer.configReader.Conf;
import dataLayer.configReader.Entity;
import dataLayer.crud.filters.CreateMany;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static dataLayer.crud.Read.read;
import static dataLayer.crud.filters.And.and;
import static dataLayer.crud.filters.Eq.eq;
import static dataLayer.crud.filters.Gte.gte;
import static dataLayer.crud.filters.Lte.lte;
import static dataLayer.crud.filters.Or.or;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MongoDBAdapterTest
{
	Entity roy = new Entity("Person",
			Map.of("name", "Roy",
					"age", 27,
					"phoneNumber", "0546815181",
					"emailAddress", "ashr@post.bgu.ac.il"));
	Entity yossi = new Entity("Person",
			Map.of("name", "Yossi",
					"age", 22,
					"phoneNumber", "0587158627",
					"emailAddress", "yossilan@post.bgu.ac.il"));
	Entity karin = new Entity("Person",
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
		Conf.loadConfiguration(MongoDBAdapterTest.class.getResource("/configuration.json"));
		try (MongoClient mongoClient = MongoClients.create())
		{
			mongoClient.getDatabase("TestDB").drop();
//					.insertMany(asList(new Document("name", "Roy")
//									.append("age", 27)
//									.append("phoneNumber", "0546815181")
//									.append("emailAddress", "ashr@post.bgu.ac.il"),
//							new Document("name", "Yossi")
//									.append("age", 22)
//									.append("phoneNumber", "0587158627")
//									.append("emailAddress", "yossilan@post.bgu.ac.il"),
//							new Document("name", "Karin")
//									.append("age", 26)
//									.append("phoneNumber", "0504563434")
//									.append("emailAddress", "davidz@post.bgu.ac.il")));
		}

		MongoDBAdapter mongoDBAdapter = new MongoDBAdapter();
		mongoDBAdapter.revealQuery(CreateMany.createMany(roy, yossi, karin));
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
		//mongoDBAdapter.revealQuery(CreateSingle.createSingle());
	}

	@Test
	void testExecuteCreate()
	{
	}

	@Test
	void testExecuteEq()
	{
		assertEquals(Set.of(roy),
				read(eq("Person", "name", "Roy")));

		assertTrue(read(eq("Person", "name", "Nobody")).isEmpty(), "Should be empty!");
	}

//	@Test
//	void testExecuteNe()
//	{
//		List<Map<String, Object>> result = read(Ne.ne("Person", "name", "Roy"));
//		System.out.println(result);
//		boolean hasYossi = result.get(0).get("name").equals("Yossi") &&
//				result.get(0).get("age").equals(22) &&
//				result.get(0).get("phoneNumber").equals("0587158627") &&
//				result.get(0).get("emailAddress").equals("yossilan@post.bgu.ac.il");
//		assertTrue(hasYossi, "Yossi's name != Roy.");
//		boolean hasKarin = result.get(1).get("name").equals("Karin") &&
//				result.get(1).get("age").equals(26) &&
//				result.get(1).get("phoneNumber").equals("0504563434") &&
//				result.get(1).get("emailAddress").equals("davidz@post.bgu.ac.il");
//		assertTrue(hasKarin, "Karin's name != Roy.");
//
//		result = read(Ne.ne("Person", "name", "Nobody"));
//		boolean hasRoy = result.get(0).get("name").equals("Roy") &&
//				result.get(0).get("age").equals(27) &&
//				result.get(0).get("phoneNumber").equals("0546815181") &&
//				result.get(0).get("emailAddress").equals("ashr@post.bgu.ac.il");
//		assertTrue(hasRoy, "Roy's name != Nobody.");
//		hasYossi = result.get(1).get("name").equals("Yossi") &&
//				result.get(1).get("age").equals(22) &&
//				result.get(1).get("phoneNumber").equals("0587158627") &&
//				result.get(1).get("emailAddress").equals("yossilan@post.bgu.ac.il");
//		assertTrue(hasYossi, "Yossi's name != Nobody.");
//		hasKarin = result.get(2).get("name").equals("Karin") &&
//				result.get(2).get("age").equals(26) &&
//				result.get(2).get("phoneNumber").equals("0504563434") &&
//				result.get(2).get("emailAddress").equals("davidz@post.bgu.ac.il");
//		assertTrue(hasKarin, "Karin's name != Nobody.");
//	}
//
//	@Test
//	void testExecuteGt()
//	{
//		List<Map<String, Object>> result = read(gt("Person", "age", 18));
//		boolean hasRoy = result.get(0).get("name").equals("Roy") &&
//				result.get(0).get("age").equals(27) &&
//				result.get(0).get("phoneNumber").equals("0546815181") &&
//				result.get(0).get("emailAddress").equals("ashr@post.bgu.ac.il");
//		assertTrue(hasRoy, "Roy's age is > 18.");
//		boolean hasYossi = result.get(1).get("name").equals("Yossi") &&
//				result.get(1).get("age").equals(22) &&
//				result.get(1).get("phoneNumber").equals("0587158627") &&
//				result.get(1).get("emailAddress").equals("yossilan@post.bgu.ac.il");
//		assertTrue(hasYossi, "Yossi's age is > 18.");
//		boolean hasKarin = result.get(2).get("name").equals("Karin") &&
//				result.get(2).get("age").equals(26) &&
//				result.get(2).get("phoneNumber").equals("0504563434") &&
//				result.get(2).get("emailAddress").equals("davidz@post.bgu.ac.il");
//		assertTrue(hasKarin, "Karin's age is > 18.");
//
//		result = read(gt("Person", "age", 30));
//		assertTrue(result.isEmpty(), "Result should be empty all of the people ages are <= 30.");
//	}
//
//	@Test
//	void testExecuteLt()
//	{
//		List<Map<String, Object>> result = read(Lt.lt("Person", "age", 30));
//		boolean hasRoy = result.get(0).get("name").equals("Roy") &&
//				result.get(0).get("age").equals(27) &&
//				result.get(0).get("phoneNumber").equals("0546815181") &&
//				result.get(0).get("emailAddress").equals("ashr@post.bgu.ac.il");
//		assertTrue(hasRoy, "Roy's age is < 30.");
//		boolean hasYossi = result.get(1).get("name").equals("Yossi") &&
//				result.get(1).get("age").equals(22) &&
//				result.get(1).get("phoneNumber").equals("0587158627") &&
//				result.get(1).get("emailAddress").equals("yossilan@post.bgu.ac.il");
//		assertTrue(hasYossi, "Yossi's age is < 30.");
//		boolean hasKarin = result.get(2).get("name").equals("Karin") &&
//				result.get(2).get("age").equals(26) &&
//				result.get(2).get("phoneNumber").equals("0504563434") &&
//				result.get(2).get("emailAddress").equals("davidz@post.bgu.ac.il");
//		assertTrue(hasKarin, "Karin's age is < 30.");
//
//		result = read(Lt.lt("Person", "age", 18));
//		assertTrue(result.isEmpty(), "Result should be empty all of the people ages are >= 18.");
//	}
//
//	@Test
//	void testExecuteGte()
//	{
//		List<Map<String, Object>> result = read(gte("Person", "age", 18));
//		boolean hasRoy = result.get(0).get("name").equals("Roy") &&
//				result.get(0).get("age").equals(27) &&
//				result.get(0).get("phoneNumber").equals("0546815181") &&
//				result.get(0).get("emailAddress").equals("ashr@post.bgu.ac.il");
//		assertTrue(hasRoy, "Roy's age is >= 18.");
//		boolean hasYossi = result.get(1).get("name").equals("Yossi") &&
//				result.get(1).get("age").equals(22) &&
//				result.get(1).get("phoneNumber").equals("0587158627") &&
//				result.get(1).get("emailAddress").equals("yossilan@post.bgu.ac.il");
//		assertTrue(hasYossi, "Yossi's age is >= 18.");
//		boolean hasKarin = result.get(2).get("name").equals("Karin") &&
//				result.get(2).get("age").equals(26) &&
//				result.get(2).get("phoneNumber").equals("0504563434") &&
//				result.get(2).get("emailAddress").equals("davidz@post.bgu.ac.il");
//		assertTrue(hasKarin, "Karin's age is >= 18.");
//
//
//		result = read(gte("Person", "age", 26));
//		hasRoy = result.get(0).get("name").equals("Roy") &&
//				result.get(0).get("age").equals(27) &&
//				result.get(0).get("phoneNumber").equals("0546815181") &&
//				result.get(0).get("emailAddress").equals("ashr@post.bgu.ac.il");
//		assertTrue(hasRoy, "Roy's age is >= 26.");
//		hasKarin = result.get(1).get("name").equals("Karin") &&
//				result.get(1).get("age").equals(26) &&
//				result.get(1).get("phoneNumber").equals("0504563434") &&
//				result.get(1).get("emailAddress").equals("davidz@post.bgu.ac.il");
//		assertTrue(hasKarin, "Karin's age is >= 26.");
//
//		result = read(gte("Person", "age", 30));
//		assertTrue(result.isEmpty(), "Result should be empty all of the people ages are < 30.");
//	}
//
//	@Test
//	void testExecuteLte()
//	{
//		List<Map<String, Object>> result = read(lte("Person", "age", 30));
//		boolean hasRoy = result.get(0).get("name").equals("Roy") &&
//				result.get(0).get("age").equals(27) &&
//				result.get(0).get("phoneNumber").equals("0546815181") &&
//				result.get(0).get("emailAddress").equals("ashr@post.bgu.ac.il");
//		assertTrue(hasRoy, "Roy's age is <= 30.");
//		boolean hasYossi = result.get(1).get("name").equals("Yossi") &&
//				result.get(1).get("age").equals(22) &&
//				result.get(1).get("phoneNumber").equals("0587158627") &&
//				result.get(1).get("emailAddress").equals("yossilan@post.bgu.ac.il");
//		assertTrue(hasYossi, "Yossi's age is <= 30.");
//		boolean hasKarin = result.get(2).get("name").equals("Karin") &&
//				result.get(2).get("age").equals(26) &&
//				result.get(2).get("phoneNumber").equals("0504563434") &&
//				result.get(2).get("emailAddress").equals("davidz@post.bgu.ac.il");
//		assertTrue(hasKarin, "Karin's age is <= 30.");
//
//		result = read(lte("Person", "age", 26));
//		hasYossi = result.get(0).get("name").equals("Yossi") &&
//				result.get(0).get("age").equals(22) &&
//				result.get(0).get("phoneNumber").equals("0587158627") &&
//				result.get(0).get("emailAddress").equals("yossilan@post.bgu.ac.il");
//		assertTrue(hasYossi, "Yossi's age is <= 26.");
//		hasKarin = result.get(1).get("name").equals("Karin") &&
//				result.get(1).get("age").equals(26) &&
//				result.get(1).get("phoneNumber").equals("0504563434") &&
//				result.get(1).get("emailAddress").equals("davidz@post.bgu.ac.il");
//		assertTrue(hasKarin, "Karin's age is <= 26.");
//
//		result = read(lte("Person", "age", 18));
//		assertTrue(result.isEmpty(), "Result should be empty all of the people ages are > 18.");
//	}

	@Test
	void testExecuteAnd()
	{
		assertEquals(
				Set.of(yossi),
				(read(
						and(
								and(
										lte("Person", "age", 26),
										gte("Person", "age", 18)),
								and(
										eq("Person", "phoneNumber", "0587158627"),
										eq("Person", "name", "Yossi"))))));
	}

	@Test
	void testExecuteOr()
	{
//		assertEquals(
//				List.of(
//						Map.of("name", "Roy",
//								"age", 27,
//								"phoneNumber", "0546815181",
//								"emailAddress", "ashr@post.bgu.ac.il"),
//						Map.of("name", "Yossi",
//								"age", 22,
//								"phoneNumber", "0587158627",
//								"emailAddress", "yossilan@post.bgu.ac.il")),
//				removeId(read(
//						or(
//								eq("Person", "age", 27),
//								eq("Person", "age", 22)))));
		assertEquals(
				Set.of(yossi,karin),
				(read(
						or(
								or(
										lte("Person", "age", 26),
										gte("Person", "age", 18)),
								or(
										eq("Person", "phoneNumber", "0587158627"),
										eq("Person", "name", "Yossi"))))));
	}

//	@Test
//	void testExecuteTest()
//	{
//		System.out.println(read(and(
//				gt("Person", "age", 30),
//				eq("Person", "name", "Yossi"))));
//	}
}
