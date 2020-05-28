import dataLayer.readers.Reader;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import java.io.IOException;

public class Main
{
	public static void main(String... args) throws IOException
	{
		Reader.loadConfAndSchema(Main.class.getResource("/configurations/configurationMongoDB.json"),
				Main.class.getResource("/schemas/Schema.json"));
		try (DSLContext connection = DSL.using("jdbc:sqlite:src/main/resources/sqliteDBs/test.db"))
		{
			connection.createTableIfNotExists("testTable").column("ts", SQLDataType.INTEGER).execute();
		}
//		System.out.println(DSL.select(DSL.field("name")).from("Person").getSQL());
	}
}
