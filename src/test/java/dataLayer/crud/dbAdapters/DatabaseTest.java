package dataLayer.crud.dbAdapters;

import com.fasterxml.jackson.core.JsonProcessingException;
import dataLayer.crud.Entity;
import dataLayer.readers.Reader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static dataLayer.crud.Query.*;
import static dataLayer.crud.filters.All.all;
import static dataLayer.crud.filters.And.and;
import static dataLayer.crud.filters.Eq.eq;
import static dataLayer.crud.filters.Gt.gt;
import static dataLayer.crud.filters.Gte.gte;
import static dataLayer.crud.filters.Lt.lt;
import static dataLayer.crud.filters.Lte.lte;
import static dataLayer.crud.filters.Ne.ne;
import static dataLayer.crud.filters.Or.or;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

//TODO insert an array
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
							"emailAddress", "davidz@post.bgu.ac.il")),

			arnon = Entity.of("Professor",
					Map.of("name", "Arnon",
					"age", 50L,
					"phoneNumber", "0501234567",
					"emailAddress", "strum@post.bgu.ac.il",
					"students", List.of(roy, yossi)));

	@AfterEach
	abstract protected void tearDown();

	@BeforeEach
	abstract protected void setUp() throws IOException;

	@Test
	void testInsertArray()
	{
		assertEquals(Set.of(arnon),
				read(eq("Professor", "name", "Arnon")),
				"Should return person named Arnon.");
	}

	@Test
	void testExecuteCreate()
	{
		Entity luke = Entity.of("Person",
				Map.of("name", "Luke Skywalker",
						"age", 24L,
						"phoneNumber", "0503451221",
						"emailAddress", "luke@JediOrder.com"));

		create(luke);
		assertEquals(Set.of(luke),
				read(eq("Person", "name", "Luke Skywalker")),
				"Should return Luke Skywalker.");
	}

	@Test
	void testExecuteRepetitiveCreate()
	{
		Entity luke = Entity.of("Person",
				Map.of("name", "Luke Skywalker",
						"age", 24L,
						"phoneNumber", "0503451221",
						"emailAddress", "luke@JediOrder.com"));
		Entity cloneLuke = Entity.of("Person",
				Map.of("name", "Luke Skywalker",
						"age", 24L,
						"phoneNumber", "0503451221",
						"emailAddress", "luke@SithEmpire.com"));

		assertThrows(IllegalStateException.class, () -> create(luke, cloneLuke));
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

		create(royForUpdate);

		assertEquals(Set.of(royForUpdate),
				read(eq("Person", "name", "RoyForUpdate")),
				"Should return person named Roy.");

		update(eq("Person", "name", "RoyForUpdate"), updates);

		//noinspection OptionalGetWithoutIsPresent
		Entity updatedRoy = read(eq("Person", "name", "RoyForUpdate")).stream()
				.findFirst()
				.get();

		assertEquals(18L, updatedRoy.get("age"), "Age should be updated to 18.");

		assertEquals("12345", updatedRoy.get("phoneNumber"), "Age should be updated to 12345.");
	}

	@Test
	void testRepetitiveUpdate()
	{
		Entity anakinSkywalker = Entity.of("Person",
				Map.of("name", "Anakin Skywalker",
						"age", 24L,
						"phoneNumber", "0503451111",
						"emailAddress", "anakinSkywalker@SithEmpire.com"));

		Set<Entity> nonPrimaryUpdates = Set.of(
				Entity.of("Person",
						Map.of("age", 25L,
								"phoneNumber", "0501111111",
								"emailAddress", "updated@SithEmpire.com")));

		Set<Entity> primaryUpdates = Set.of(
				Entity.of("Person",
						Map.of("name", "Darth Vader")));

		Set<Entity> existingPrimaryUpdates = Set.of(
				Entity.of("Person",
						Map.of("name", "Yossi")));

		create(anakinSkywalker);

		assertEquals(Set.of(anakinSkywalker),
				read(eq("Person", "name", "Anakin Skywalker")),
				"Should return Anakin Skywalker.");

		update(Set.of(anakinSkywalker), nonPrimaryUpdates);

		//noinspection OptionalGetWithoutIsPresent
		Entity updatedAnakin = read(eq("Person", "name", "Anakin Skywalker")).stream()
				.findFirst()
				.get();

		assertEquals(25L, updatedAnakin.get("age"), "Age should be updated to 25.");

		assertEquals("0501111111", updatedAnakin.get("phoneNumber"), "Phone number should be updated to 0501111111.");

		assertEquals("updated@SithEmpire.com", updatedAnakin.get("emailAddress"), "E-mail address should be updated to updated@SithEmpire.com.");

		update(Set.of(updatedAnakin), primaryUpdates);

		//noinspection OptionalGetWithoutIsPresent
		Entity darthVader = read(eq("Person", "name", "Darth Vader")).stream()
				.findFirst()
				.get();

		assertEquals("Darth Vader", darthVader.get("name"), "Name should be updated to Darth Vader.");

		assertThrows(IllegalStateException.class, () -> update(Set.of(darthVader), existingPrimaryUpdates));
	}

	@Test
	void testExecuteDelete()
	{
		final Entity royForDelete = Entity.of("Person",
				Map.of("name", "RoyForDelete",
						"age", 27L,
						"phoneNumber", "0546815181",
						"emailAddress", "ashr@post.bgu.ac.il"));

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
	void testExecuteAll()
	{
		assertEquals(Set.of(roy, yossi, karin),
				read(all("Person")),
				"Should return Roy, Yossi and Karin.");
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
										eq("Person", "name", "Yossi"))))),
				"Should be the same!!!");
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
										eq("Person", "name", "Yossi"))))),
				"Should be the same!!!");
	}

	@Test
	void testNestedCreate()
	{
		Entity nestedEntity = Entity.of("Person",
				Map.of("name", "Oscar",
						"age", 12L,
						"phoneNumber", "0521212121",
						"emailAddress", "Elmo@post.bgu.ac.il",
						"livesAt", Entity.of("Address",
								Map.of("street", "Sesame street",
										"state", "New York",
										"city", Entity.of("City", Map.of("name", "California",
												"mayor", "Arnold")),
										"postalCode", "777777",
										"country", "United States"))));

		create(nestedEntity);
		System.out.println(nestedEntity);
		assertEquals(Set.of(nestedEntity),
				read(eq("Person", "name", "Oscar")),
				"Should return person named Oscar.");
	}


	@Test
	void testJoin() throws JsonProcessingException
	{
		//TODO complete testJoin
		System.out.println(Reader.toJson(join(gt("Person", "age", 18), entity -> true)));
	}

	@Test
	void testComplexJoin() throws JsonProcessingException
	{
		//TODO complete testComplexJoin
		Entity city = Entity.of("City", Map.of("name", "Newark",
				"mayor", "Mayor West."));
		Entity nestedEntity1 = Entity.of("Person",
				Map.of("name", "Elmo",
						"age", 12L,
						"phoneNumber", "0521212121",
						"emailAddress", "Elmo@post.bgu.ac.il",
						"livesAt", Entity.of("Address",
								Map.of("street", "Sesame street",
										"state", "New York",
										"city", city,
										"postalCode", "757212",
										"country", "United States"))));
		Entity city2 = Entity.of("City", Map.of("name", "Unknown",
				"mayor", "Some magical wizard"));
		Entity nestedEntity2 = Entity.of("Person",
				Map.of("name", "Bilbo",
						"age", 16L,
						"phoneNumber", "0531313131",
						"emailAddress", "Baggins@post.bgu.ac.il",
						"livesAt", Entity.of("Address",
								Map.of("street", "Hobbit Street",
										"state", "Mordor",
										"city", city2,
										"postalCode", "123212",
										"country", "Australia"))));
		Entity nestedEntity3 = Entity.of("Person",
				Map.of("name", "Frodo",
						"age", 18L,
						"phoneNumber", "0541414141",
						"emailAddress", "Frodo@post.bgu.ac.il",
						"livesAt", Entity.of("Address",
								Map.of("street", "Hobbit Street",
										"state", "Mordor",
										"city", city2,
										"postalCode", "432212",
										"country", "Australia"))));
		create(nestedEntity1, nestedEntity2, nestedEntity3);
		System.out.println(Reader.toJson(join(or(gte("Person", "age", 12), eq("City", "name", "Unknown")), entity -> true)));
//		delete(nestedEntity1, nestedEntity2, nestedEntity3);
	}
}
