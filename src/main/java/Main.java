import dataLayer.readers.Reader;
import org.jooq.DSLContext;
import org.jooq.Row1;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.UUID.randomUUID;
import static java.util.stream.Stream.generate;
import static org.jooq.impl.DSL.*;

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
					.column("testString", SQLDataType.VARCHAR)
					.constraint(primaryKey("uuid"))
					.execute();
		}

		Map<String, Object> fieldsAndValues = new HashMap<>();
		fieldsAndValues.put("uuid", randomUUID());
		fieldsAndValues.put("testString", "Roy");

		try (DSLContext connection = using("jdbc:sqlite:resourcesTemp/sqliteDBs/test.db"))
		{
			connection.insertInto(table("testTable"), fieldsAndValues.keySet().stream().map(DSL::field).collect(Collectors.toSet()))
					.values(fieldsAndValues.values())
					.execute();
		}

		Map<String, Object> updates = new HashMap<>();
		updates.put("uuid", randomUUID());
		updates.put("testString", "Wohoo");

		try (DSLContext connection = using("jdbc:sqlite:resourcesTemp/sqliteDBs/test.db"))
		{
			connection.update(table("testTable"))
					.set((Row1) row(updates.keySet().stream().map(DSL::field).collect(Collectors.toSet())), (Row1) row(updates.values()))
					.execute();
		}

		System.out.println(
				deleteFrom(table("Person"))
						.where(field("uuid").in(
								generate(UUID::randomUUID).parallel()
										.limit(5)
										.collect(Collectors.toList()))
						));
	}
}
