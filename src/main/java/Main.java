import dataLayer.readers.Reader;
import org.jooq.impl.DSL;

import java.io.IOException;

public class Main
{
	public static void main(String... args) throws IOException, NoSuchMethodException
	{
		Reader.loadConfAndSchema(Main.class.getResource("/configurations/configurationMongoDB.json"),
				Main.class.getResource("/schemas/Schema.json"));
		System.out.println(DSL.select(DSL.field("name")).from("Person").getSQL());
	}
}
