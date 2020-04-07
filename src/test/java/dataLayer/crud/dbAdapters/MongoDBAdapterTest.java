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
import static dataLayer.crud.filters.Gt.gt;
import static dataLayer.crud.filters.Gte.gte;
import static dataLayer.crud.filters.Lt.lt;
import static dataLayer.crud.filters.Lte.lte;
import static dataLayer.crud.filters.Ne.ne;
import static dataLayer.crud.filters.Or.or;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MongoDBAdapterTest
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
		Conf.loadConfiguration(MongoDBAdapterTest.class.getResource("/configurations/configuration.json"));
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
		MongoDBAdapter mongoDBAdapter = new MongoDBAdapter();
		mongoDBAdapter.revealQuery(CreateMany.createMany(roy, yossi, karin));
	}

	@AfterAll
	void tearDown()
	{
		try (MongoClient mongoClient = MongoClients.create())
		{
			mongoClient.getDatabase("TestDB").drop();
			mongoClient.getDatabase("myDB").drop();
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
				read(eq("Person", "name", "Roy")),
				"Should return person named Roy.");

		assertEquals(Set.of(),
				read(eq("Person", "name", "Nobody")),
				"There is no person named Nobody");
	}

	@Test
	void testExecuteNe()
	{
		assertEquals(Set.of(yossi, karin),
				read(ne("Person", "name", "Roy")),
				"Should return everyone except Roy.");

		assertEquals(Set.of(roy, yossi, karin),
				read(ne("Person", "name", "Nobody")),
				"Should return everyone since everyone are not named Nobody.");
	}

	@Test
	void testExecuteGt()
	{
		assertEquals(Set.of(roy, yossi, karin),
				read(gt("Person", "age", 18)),
				"Should return everyone since all are above the age of 18.");

		assertEquals(Set.of(),
				read(gt("Person", "age", 30)),
				"Result should be empty all of the people ages are <= 30.");
	}

	@Test
	void testExecuteLt()
	{
		assertEquals(Set.of(roy, yossi, karin),
				read(lt("Person", "age", 30)),
				"Should return everyone since all are under the age of 30.");

		assertEquals(Set.of(),
				read(lt("Person", "age", 18)),
				"Result should be empty all of the people ages are >= 18.");
	}

	@Test
	void testExecuteGte()
	{
		assertEquals(Set.of(roy, yossi, karin),
				read(gte("Person", "age", 18)),
				"Should return everyone since all are over the age of 18.");

		assertEquals(Set.of(roy, karin),
				read(gte("Person", "age", 26)),
				"Only Roy and Karin are above/equal to 26.");

		assertEquals(Set.of(),
				read(gte("Person", "age", 30)),
				"Result should be empty all of the people ages are < 30.");
	}

	@Test
	void testExecuteLte()
	{
		assertEquals(Set.of(roy, yossi, karin),
				read(lte("Person", "age", 30)),
				"Should return everyone since all are under the age of 30.");

		assertEquals(Set.of(yossi, karin),
				read(lte("Person", "age", 26)),
				"Only Yossi and Karin are under/equal to 26.");

		assertEquals(Set.of(),
				read(lte("Person", "age", 18)),
				"Result should be empty all of the people ages are > 18.");
	}

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
		assertEquals(
				Set.of(roy, yossi, karin),
				(read(
						or(
								or(
										lte("Person", "age", 26),
										gte("Person", "age", 18)),
								or(
										eq("Person", "phoneNumber", "0587158627"),
										eq("Person", "name", "Yossi"))))));
	}
}
