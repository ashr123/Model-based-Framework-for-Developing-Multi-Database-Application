//import il.co.multiDatabaseFramework.crud.Entity;
//import il.co.multiDatabaseFramework.crud.Query;
//import il.co.multiDatabaseFramework.readers.Reader;
//import org.jooq.DSLContext;
//import org.jooq.impl.SQLDataType;
//
//import java.io.IOException;
//import java.util.Set;
//
//import static il.co.multiDatabaseFramework.crud.filters.Eq.eq;
//import static java.util.stream.Collectors.toList;
//import static org.jooq.impl.DSL.primaryKey;
//import static org.jooq.impl.DSL.using;
//
//public class Main
//{
//	public static void main(String... args) throws IOException
//	{
//		Reader.loadConfAndSchema("src/test/resources/configurations/configurationCircular.json",
//				"src/test/resources/schemas/SchemaCircular.json");
//
//		try (DSLContext connection = using("jdbc:mysql://localhost:3306/testDatabase", "root", "mysql123"))
//		{
//			connection.dropTableIfExists("City").execute();
//			connection.dropTableIfExists("Person").execute();
//
//			connection.createTableIfNotExists("City")
//					.column("uuid", SQLDataType.UUID.nullable(false))
//					.column("name", SQLDataType.VARCHAR)
//					.column("mayor", SQLDataType.UUID)
//					.constraint(primaryKey("uuid"))
//					.execute();
//			connection.createTableIfNotExists("Person")
//					.column("uuid", SQLDataType.UUID.nullable(false))
//					.column("name", SQLDataType.VARCHAR)
//					.column("phoneNumber", SQLDataType.VARCHAR)
//					.column("livesAt", SQLDataType.UUID)
//					.constraint(primaryKey("uuid"))
//					.execute();
//
//			Entity city = Entity.of("City")
//					.putField("name", "BSH");
//			Entity person = Entity.of("Person")
//					.putField("name", "Roy")
//					.putField("livesAt", city);
//			city.putField("mayor", person);
//
//			Query.create(city, person);
//
//			Set<Entity> entities = Query.read(eq("Person", "name", "Roy"));
//			System.out.println(entities.stream().map(entity -> entity.get("name")).collect(toList()));
//
//			connection.dropTableIfExists("City").execute();
//			connection.dropTableIfExists("Person").execute();
//		}
//
////		Map<String, Object> fieldsAndValues = new HashMap<>();
////		fieldsAndValues.put("uuid", randomUUID());
////		fieldsAndValues.put("testString", "Roy");
////
////		try (DSLContext connection = using("jdbc:sqlite:resourcesTemp/sqliteDBs/test.db"))
////		{
////			connection.insertInto(table("testTable"), fieldsAndValues.keySet().stream().map(DSL::field).collect(toList()))
////					.values(fieldsAndValues.values())
////					.execute();
////		}
////
////		Map<String, Object> updates = new HashMap<>();
////		updates.put("uuid", randomUUID());
////		updates.put("testString", "Wohoo");
////
////		try (DSLContext connection = using("jdbc:sqlite:resourcesTemp/sqliteDBs/test.db"))
////		{
////			connection.update(table("testTable"))
////					.set(row(updates.keySet().stream().map(DSL::field).collect(toList())), row(updates.values()))
////					.execute();
////		}
//
////		System.out.println(
////				deleteFrom(table("Person"))
////						.where(field("uuid").in(
////								generate(UUID::randomUUID).parallel()
////										.limit(5)
////										.collect(toList()))
////						));
//	}
//}
