import dataLayer.readers.Reader;
import org.jooq.DSLContext;
import org.jooq.impl.SQLDataType;

import java.io.IOException;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.jooq.impl.DSL.deleteFrom;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.primaryKey;
import static org.jooq.impl.DSL.table;
import static org.jooq.impl.DSL.using;

public class Main
{
	public static void main(String... args) throws IOException
	{
		Reader.loadConfAndSchema("resourcesTemp/configurations/configurationMongoDB.json",
				"resourcesTemp/schemas/Schema.json");

		try (DSLContext connection = using("jdbc:sqlite:resourcesTemp/sqliteDBs/test.db"))
		{
			connection.createTableIfNotExists("testTable")
					.column("uuid", SQLDataType.UUID)
					.constraint(primaryKey("uuid"))
					.execute();
		}
		System.out.println(
				deleteFrom(table("Person"))
						.where(field("uuid").in(
								Stream.generate(UUID::randomUUID).parallel()
										.limit(5)
										.collect(Collectors.toList()))
						));
	}
}
