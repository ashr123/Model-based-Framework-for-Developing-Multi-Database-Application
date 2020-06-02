//import org.jooq.DSLContext;
//import org.jooq.impl.SQLDataType;
//
//import java.util.UUID;
//
//import static java.util.stream.Collectors.toList;
//import static java.util.stream.Stream.generate;
//import static org.jooq.impl.DSL.*;
//
//public class Main
//{
//	public static void main(String... args)
//	{
////		Reader.loadConfAndSchema("src/test/resources/configurations/configurationSQL.json",
////				"src/test/resources/schemas/Schema.json");
//		try (DSLContext connection = using("jdbc:mysql://localhost:3306/testDatabase", "root", ""))
//		{
//			connection.createTableIfNotExists("testTable")
//					.column("uuid", SQLDataType.UUID.nullable(false))
//					.column("testString", SQLDataType.VARCHAR)
//					.constraint(primaryKey("uuid"))
//					.execute();
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
//		System.out.println(
//				deleteFrom(table("Person"))
//						.where(field("uuid").in(
//								generate(UUID::randomUUID).parallel()
//										.limit(5)
//										.collect(toList()))
//						));
//	}
//}
