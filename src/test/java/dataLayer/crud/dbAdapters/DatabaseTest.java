package dataLayer.crud.dbAdapters;

import dataLayer.crud.Entity;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static dataLayer.crud.Query.*;
import static dataLayer.crud.Query.read;
import static dataLayer.crud.filters.And.and;
import static dataLayer.crud.filters.Eq.eq;
import static dataLayer.crud.filters.Gt.gt;
import static dataLayer.crud.filters.Gte.gte;
import static dataLayer.crud.filters.Lt.lt;
import static dataLayer.crud.filters.Lte.lte;
import static dataLayer.crud.filters.Ne.ne;
import static dataLayer.crud.filters.Or.or;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class DatabaseTest
{

	protected final Entity
			roy = Entity.of("Person",
			Map.of("name", "Roy",
					"age", 27L,
					"phoneNumber", "0546815181",
					"emailAddress", "ashr@post.bgu.ac.il")),
			yossi = Entity.of("Person",
					Map.of("name", "Yossi",
							"age", 22L,
							"phoneNumber", "0587158627",
							"emailAddress", "yossilan@post.bgu.ac.il")),
			karin = Entity.of("Person",
					Map.of("name", "Karin",
							"age", 26L,
							"phoneNumber", "0504563434",
							"emailAddress", "davidz@post.bgu.ac.il"));

	@AfterAll
	abstract protected void tearDown();

	@BeforeAll
	abstract protected void setUp() throws IOException;

	@Test
	void testExecuteCreate()
	{
	}

	@Test
	void testExecuteUpdate()
	{
		final Entity royForUpdate = Entity.of("Person",
				Map.of("name", "RoyForUpdate",
						"age", 27L,
						"phoneNumber", "0546815181",
						"emailAddress", "ashr@post.bgu.ac.il"));

		final Set<Entity> updates = Set.of(
				Entity.of("Person",
						Map.of("age", 18L,
								"phoneNumber", "12345")));

		//createSingle(royForUpdate).executeAt(DBType.MONGODB.getDatabaseAdapter());
		create(royForUpdate);

		assertEquals(Set.of(royForUpdate),
				read(eq("Person", "name", "RoyForUpdate")),
				"Should return person named Roy.");

		update(eq("Person", "name", "RoyForUpdate"), updates);

		Entity updatedRoy = read(eq("Person", "name", "RoyForUpdate")).toArray(Entity[]::new)[0];

		assertEquals(updatedRoy.get("age"), 18L, "Age should be updated to 18.");

		assertEquals(updatedRoy.get("phoneNumber"), "12345", "Age should be updated to 12345.");

		delete(eq("Person", "name", "RoyForUpdate"));
	}

	@Test
	void testExecuteDelete()
	{
		final Entity royForDelete = Entity.of("Person",
				Map.of("name", "RoyForDelete",
						"age", 27L,
						"phoneNumber", "0546815181",
						"emailAddress", "ashr@post.bgu.ac.il"));

		//createSingle(royForDelete).executeAt(DBType.MONGODB.getDatabaseAdapter());
		create(royForDelete);

		assertEquals(Set.of(royForDelete),
				read(eq("Person", "name", "RoyForDelete")),
				"Should return person named Roy.");

		delete(eq("Person", "name", "RoyForDelete"));

		assertEquals(Set.of(),
				read(eq("Person", "name", "RoyForDelete")),
				"RoyForDelete should have been removed!");
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

	@Test
	void testNestedCreate()
	{
		Entity city = Entity.of("City", Map.of("name", "Newark",
				"mayor", "Mayor West."));
		Entity nestedEntity = Entity.of("Person",
				Map.of("name", "Elmo",
						"age", 12L,
						"phoneNumber", "0521212121",
						"emailAddress", "Elmo@post.bgu.ac.il",
						"livesAt", Entity.of("Address",
								Map.of("street", "Sesame street",
										"state", "New York",
										"city", city,
										"postal-code", "757212",
										"country", "United States"))));

		create(nestedEntity);
		System.out.println(nestedEntity.toString());
		assertEquals(Set.of(nestedEntity),
				read(eq("Person", "name", "Elmo")),
				"Should return person named Elmo.");
	}
}
