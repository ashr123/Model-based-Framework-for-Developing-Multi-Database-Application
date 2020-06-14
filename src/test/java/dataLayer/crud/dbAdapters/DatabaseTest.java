package dataLayer.crud.dbAdapters;

import dataLayer.crud.Entity;
import dataLayer.crud.EntityHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
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

public abstract class DatabaseTest
{
	protected final Entity
			roy = Entity.of("Person")
			.putField("name", "Roy")
			.putField("age", 27)
			.putField("phoneNumber", "0546815181")
			.putField("emailAddress", "ashr@post.bgu.ac.il"),
			yossi = Entity.of("Person")
					.putField("name", "Yossi")
					.putField("age", 22)
					.putField("phoneNumber", "0587158627")
					.putField("emailAddress", "yossilan@post.bgu.ac.il"),
			karin = Entity.of("Person")
					.putField("name", "Karin")
					.putField("age", 26)
					.putField("phoneNumber", "0504563434")
					.putField("emailAddress", "davidz@post.bgu.ac.il"),
			arnon = Entity.of("Professor")
					.putField("name", "Arnon")
					.putField("age", 50)
					.putField("phoneNumber", "0501234567")
					.putField("emailAddress", "strum@post.bgu.ac.il")
					.putField("students", Set.of(roy, yossi));

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
		Entity luke = Entity.of("Person")
				.putField("name", "Luke Skywalker")
				.putField("age", 24)
				.putField("phoneNumber", "0503451221")
				.putField("emailAddress", "luke@JediOrder.com");

		create(luke);
		assertEquals(Set.of(luke),
				read(eq("Person", "name", "Luke Skywalker")),
				"Should return Luke Skywalker.");
	}

	@Test
	void testExecuteRepetitiveCreate()
	{
		Entity luke = Entity.of("Person")
				.putField("name", "Luke Skywalker")
				.putField("age", 24)
				.putField("phoneNumber", "0503451221")
				.putField("emailAddress", "luke@JediOrder.com"),
				cloneLuke = Entity.of("Person")
						.putField("name", "Luke Skywalker")
						.putField("name", "Luke Skywalker")
						.putField("age", 24)
						.putField("phoneNumber", "0503451221")
						.putField("emailAddress", "luke@SithEmpire.com");

		assertThrows(IllegalStateException.class, () -> create(luke, cloneLuke));
	}

	@Test
	void testExecuteUpdate()
	{
		final Entity royForUpdate = Entity.of("Person")
				.putField("name", "RoyForUpdate")
				.putField("age", 27)
				.putField("phoneNumber", "0546815181")
				.putField("emailAddress", "ashr@post.bgu.ac.il");

		final Set<Entity> updates = Set.of(
				Entity.of("Person")
						.putField("age", 18)
						.putField("phoneNumber", "12345"));

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
		Entity anakinSkywalker = Entity.of("Person")
				.putField("name", "Anakin Skywalker")
				.putField("age", 24)
				.putField("phoneNumber", "0503451111")
				.putField("emailAddress", "anakinSkywalker@SithEmpire.com");

		Set<Entity> nonPrimaryUpdates = Set.of(
				Entity.of("Person")
						.putField("age", 25)
						.putField("phoneNumber", "0501111111")
						.putField("emailAddress", "updated@SithEmpire.com")),
				primaryUpdates = Set.of(
						Entity.of("Person")
								.putField("name", "Darth Vader")),
				existingPrimaryUpdates = Set.of(
						Entity.of("Person")
								.putField("name", "Yossi"));

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
		final Entity royForDelete = Entity.of("Person")
				.putField("name", "RoyForDelete")
				.putField("age", 27)
				.putField("phoneNumber", "0546815181")
				.putField("emailAddress", "ashr@post.bgu.ac.il");

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
		Entity nestedEntity = Entity.of("Person")
				.putField("name", "Oscar")
				.putField("age", 12)
				.putField("phoneNumber", "0521212121")
				.putField("emailAddress", "Elmo@post.bgu.ac.il")
				.putField("livesAt", Entity.of("Address")
						.putField("street", "Sesame street")
						.putField("state", "New York")
						.putField("city", Entity.of("City")
								.putField("name", "California")
								.putField("mayor", "Arnold"))
						.putField("postalCode", "777777")
						.putField("country", "United States"));

		create(nestedEntity);
		System.out.println(nestedEntity);
		assertEquals(Set.of(nestedEntity),
				read(eq("Person", "name", "Oscar")),
				"Should return person named Oscar.");
	}

	@Test
	void testJoin()
	{
		Entity city = Entity.of("City")
				.putField("name", "Beersheba")
				.putField("mayor", "Rubik Danilovich");
		Entity address = Entity.of("Address")
				.putField("street", "Rager 4")
				.putField("state", "Israel")
				.putField("city", city)
				.putField("postalCode", "432212")
				.putField("country", "Israel");
		create(city, address);

		Map<String, Object> expectedAddress = Map.of("Address.street", "Rager 4",
				"Address.state", "Israel",
				"Address.city", city,
				"Address.postalCode", "432212",
				"Address.country", "Israel");

		Map<String, Object> expectedCity = Map.of("City.name", "Beersheba",
				"City.mayor", "Rubik Danilovich");

		Map<String, Object> expected1 = new HashMap<>();
		expected1.put("Person.name", "Roy");
		expected1.put("Person.age", 27L);
		expected1.put("Person.phoneNumber", "0546815181");
		expected1.put("Person.emailAddress", "ashr@post.bgu.ac.il");

		expected1.putAll(expectedCity);
		Entity expectedEntity1 = EntityHelper.entityBuilder(null, null, expected1);

		Map<String, Object> expected2 = new HashMap<>();
		expected2.put("Person.name", "Yossi");
		expected2.put("Person.age", 22L);
		expected2.put("Person.phoneNumber", "0587158627");
		expected2.put("Person.emailAddress", "yossilan@post.bgu.ac.il");

		expected2.putAll(expectedCity);
		Entity expectedEntity2 = EntityHelper.entityBuilder(null, null, expected2);

		Map<String, Object> expected3 = new HashMap<>();
		expected3.put("Person.name", "Karin");
		expected3.put("Person.age", 26L);
		expected3.put("Person.phoneNumber", "0504563434");
		expected3.put("Person.emailAddress", "davidz@post.bgu.ac.il");

		expected3.putAll(expectedCity);
		Entity expectedEntity3 = EntityHelper.entityBuilder(null, null, expected3);

		Set<Entity> expectedResult = Set.of(expectedEntity1, expectedEntity2, expectedEntity3);
		Set<Entity> joinResult = join(or(gte("Person", "age", 18), eq("City", "name", "Beersheba")), entity -> true);

		assertEquals(expectedResult, joinResult, "Should return a set of joined Person entity and City entity together, preform join between people over 18 and cities named Beersheba");

		expected1.putAll(expectedAddress);
		expectedEntity1 = EntityHelper.entityBuilder(null, null, expected1);

		expected2.putAll(expectedAddress);
		expectedEntity2 = EntityHelper.entityBuilder(null, null, expected2);

		expected3.putAll(expectedAddress);
		expectedEntity3 = EntityHelper.entityBuilder(null, null, expected3);

		expectedResult = Set.of(expectedEntity1, expectedEntity2, expectedEntity3);
		joinResult = join(or(gte("Person", "age", 12), eq("Address", "state", "Israel"), eq("City", "name", "Beersheba")), entity -> true);

		assertEquals(expectedResult, joinResult, "Should return a set of joined Person entity, Address entity and City entity together, preform join between people over 18, Addresses in Israel and cities named Beersheba");
	}


	@Test
	void testComplexJoin()
	{
		Entity city = Entity.of("City")
				.putField("name", "Newark")
				.putField("mayor", "Mayor West.");

		Entity city2 = Entity.of("City")
				.putField("name", "Unknown")
				.putField("mayor", "Some magical wizard");

		Entity address1 = Entity.of("Address")
				.putField("street", "Sesame street")
				.putField("state", "New York")
				.putField("city", city)
				.putField("postalCode", "757212")
				.putField("country", "United States");

		Entity address2 = Entity.of("Address")
				.putField("street", "Hobbit Street")
				.putField("state", "Mordor")
				.putField("city", city2)
				.putField("postalCode", "123212")
				.putField("country", "Australia");

		Entity address3 = Entity.of("Address")
				.putField("street", "Hobbit Street")
				.putField("state", "Mordor")
				.putField("city", city2)
				.putField("postalCode", "432212")
				.putField("country", "Australia");

		Entity nestedEntity1 = Entity.of("Person")
				.putField("name", "Elmo")
				.putField("age", 12)
				.putField("phoneNumber", "0521212121")
				.putField("emailAddress", "Elmo@post.bgu.ac.il")
				.putField("livesAt", address1);

		Entity nestedEntity2 = Entity.of("Person")
				.putField("name", "Bilbo")
				.putField("age", 16)
				.putField("phoneNumber", "0531313131")
				.putField("emailAddress", "Baggins@post.bgu.ac.il")
				.putField("livesAt", address2);

		Entity nestedEntity3 = Entity.of("Person")
				.putField("name", "Frodo")
				.putField("age", 18)
				.putField("phoneNumber", "0541414141")
				.putField("emailAddress", "Frodo@post.bgu.ac.il")
				.putField("livesAt", address3);

		Map<String, Object> expectedCity = Map.of("City.name", "Unknown",
				"City.mayor", "Some magical wizard");

		Map<String, Object> expected1 = new HashMap<>();
		expected1.put("Person.name", "Bilbo");
		expected1.put("Person.age", 16L);
		expected1.put("Person.phoneNumber", "0531313131");
		expected1.put("Person.emailAddress", "Baggins@post.bgu.ac.il");
		expected1.put("Person.livesAt", address2);

		expected1.putAll(expectedCity);
		Entity expectedEntity1 = EntityHelper.entityBuilder(null, null, expected1);

		Map<String, Object> expected2 = new HashMap<>();
		expected2.put("Person.name", "Frodo");
		expected2.put("Person.age", 18L);
		expected2.put("Person.phoneNumber", "0541414141");
		expected2.put("Person.emailAddress", "Frodo@post.bgu.ac.il");
		expected2.put("Person.livesAt", address3);

		expected2.putAll(expectedCity);
		Entity expectedEntity2 = EntityHelper.entityBuilder(null, null, expected2);

		create(city, city2, address1, address2, address3, nestedEntity1, nestedEntity2, nestedEntity3);

		Set<Entity> expectedResult = Set.of(expectedEntity1, expectedEntity2);
		Set<Entity> joinResult = join(or(lte("Person", "age", 18), eq("City", "name", "Unknown")),
				entity -> ((Entity) ((Entity) entity.get("Person.livesAt")).get("city")).get("name").equals(entity.get("City.name")));

		assertEquals(expectedResult, joinResult, "Should return a set of joined Person entity and City entity together, preform natural join between people under 19 who live in city named Unknown");
	}
}
