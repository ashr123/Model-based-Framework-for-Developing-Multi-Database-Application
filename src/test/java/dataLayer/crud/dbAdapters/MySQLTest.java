package dataLayer.crud.dbAdapters;

import dataLayer.readers.Reader;
import org.jooq.DSLContext;
import org.jooq.impl.SQLDataType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;

import static dataLayer.crud.Query.create;
import static org.jooq.impl.DSL.primaryKey;
import static org.jooq.impl.DSL.using;

class MySQLTest extends DatabaseTest
{
	@BeforeEach
	@Override
	protected void setUp() throws IOException
	{
		Reader.loadConfAndSchema("src/test/resources/configurations/configurationMySQL.json",
				"src/test/resources/schemas/Schema.json");

		//Setting all the SQL databases.
		try (DSLContext connection = using("jdbc:mysql://localhost:3306/test", "root", ""))
		{
			connection.createTableIfNotExists("City")
					.column("uuid", SQLDataType.UUID.nullable(false))
					.column("mayor", SQLDataType.VARCHAR)
					.constraint(primaryKey("uuid"))
					.execute();
			connection.createTableIfNotExists("Address")
					.column("uuid", SQLDataType.UUID.nullable(false))
					.column("state", SQLDataType.VARCHAR)
					.column("postalCode", SQLDataType.VARCHAR)
					.constraint(primaryKey("uuid"))
					.execute();
			connection.createTableIfNotExists("Person")
					.column("uuid", SQLDataType.UUID.nullable(false))
					.column("name", SQLDataType.VARCHAR)
					.column("phoneNumber", SQLDataType.VARCHAR)
					.column("livesAt", SQLDataType.UUID)
					.constraint(primaryKey("uuid"))
					.execute();
			connection.createTableIfNotExists("Professor")
					.column("uuid", SQLDataType.UUID.nullable(false))
					.column("name", SQLDataType.VARCHAR)
					.column("age", SQLDataType.BIGINT)
					.column("emailAddress", SQLDataType.VARCHAR)
					.column("students", SQLDataType.BLOB)
					.constraint(primaryKey("uuid"))
					.execute();
		}

		try (DSLContext connection = using("jdbc:mysql://localhost:3306/test2", "root", ""))
		{
			connection.createTableIfNotExists("City")
					.column("uuid", SQLDataType.UUID.nullable(false))
					.column("name", SQLDataType.VARCHAR)
					.constraint(primaryKey("uuid"))
					.execute();
			connection.createTableIfNotExists("Address")
					.column("uuid", SQLDataType.UUID.nullable(false))
					.column("street", SQLDataType.VARCHAR)
					.column("city", SQLDataType.UUID)
					.column("country", SQLDataType.VARCHAR)
					.constraint(primaryKey("uuid"))
					.execute();
			connection.createTableIfNotExists("Person")
					.column("uuid", SQLDataType.UUID.nullable(false))
					.column("age", SQLDataType.BIGINT)
					.column("emailAddress", SQLDataType.VARCHAR)
					.constraint(primaryKey("uuid"))
					.execute();
			connection.createTableIfNotExists("Professor")
					.column("uuid", SQLDataType.UUID.nullable(false))
					.column("name", SQLDataType.VARCHAR)
					.column("phoneNumber", SQLDataType.VARCHAR)
					.column("livesAt", SQLDataType.UUID)
					.constraint(primaryKey("uuid"))
					.execute();
		}

		create(roy, yossi, karin, arnon);
	}

	@AfterEach
	@Override
	protected void tearDown()
	{
		//Dropping all SQL databases.
		try (DSLContext connection = using("jdbc:mysql://localhost:3306/test", "root", ""))
		{
			connection.dropTableIfExists("City").execute();
			connection.dropTableIfExists("Address").execute();
			connection.dropTableIfExists("Person").execute();
			connection.dropTableIfExists("Professor").execute();
		}

		try (DSLContext connection = using("jdbc:mysql://localhost:3306/test2", "root", ""))
		{
			connection.dropTableIfExists("City").execute();
			connection.dropTableIfExists("Address").execute();
			connection.dropTableIfExists("Person").execute();
			connection.dropTableIfExists("Professor").execute();
		}
	}
}

