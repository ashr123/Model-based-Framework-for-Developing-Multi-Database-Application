import dataLayer.readers.Reader;
import org.jooq.DSLContext;
import org.jooq.impl.SQLDataType;

import java.io.IOException;

import static org.jooq.impl.DSL.primaryKey;
import static org.jooq.impl.DSL.using;

public class Main
{
	public static void main(String... args) throws IOException
	{
		Reader.loadConfAndSchema("resourcesTemp/configurations/configurationMongoDB.json",
				"resourcesTemp/schemas/Schema.json");

//		try (DSLContext connection = using("jdbc:sqlite:src/main/resources/sqliteDBs/test.db"))
//		{
//			connection.createTableIfNotExists("testTable")
//					.column("uuid", SQLDataType.UUID)
//					.constraint(primaryKey("uuid"))
//					.execute();
//		}
//		System.out.println(
//				deleteFrom(table("Person"))
//						.where(field("uuid")
//								.in(Stream.generate(UUID::randomUUID)
//										.limit(5)
//										.collect(Collectors.toList())))
//						.getSQL());
//		SQLDataType.UUID
	}
}
