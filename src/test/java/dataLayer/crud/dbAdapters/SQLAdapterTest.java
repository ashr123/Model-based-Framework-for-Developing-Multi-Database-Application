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

class SQLAdapterTest extends DatabaseTest
{
	@BeforeEach
	@Override
	protected void setUp() throws IOException
	{
		Reader.loadConfAndSchema("resourcesTemp/configurations/configurationSQL.json",
				"resourcesTemp/schemas/Schema.json");

		//Setting all the SQL databases.
		try (DSLContext connection = using("jdbc:sqlite:resourcesTemp/sqliteDBs/test.db"))
		{
			connection.createTableIfNotExists("City")
					.column("uuid", SQLDataType.UUID)
					.column("mayor", SQLDataType.VARCHAR)
					.constraint(primaryKey("uuid"))
					.execute();
			connection.createTableIfNotExists("Address")
					.column("uuid", SQLDataType.UUID)
					.column("state", SQLDataType.VARCHAR)
					.column("postalCode", SQLDataType.VARCHAR)
					.constraint(primaryKey("uuid"))
					.execute();
			connection.createTableIfNotExists("Person")
					.column("uuid", SQLDataType.UUID)
					.column("name", SQLDataType.VARCHAR)
					.column("phoneNumber", SQLDataType.VARCHAR)
					.column("livesAt", SQLDataType.UUID)
					.constraint(primaryKey("uuid"))
					.execute();
			connection.createTableIfNotExists("Professor")
					.column("uuid", SQLDataType.UUID)
					.column("age", SQLDataType.BIGINT)
					.column("emailAddress", SQLDataType.VARCHAR)
					.column("students", SQLDataType.BLOB)
					.constraint(primaryKey("uuid"))
					.execute();
			connection.createTableIfNotExists("Student")
					.column("uuid", SQLDataType.UUID)
					.column("age", SQLDataType.BIGINT)
					.column("emailAddress", SQLDataType.VARCHAR)
					.constraint(primaryKey("uuid"))
					.execute();
		}

		try (DSLContext connection = using("jdbc:sqlite:resourcesTemp/sqliteDBs/test2.db"))
		{
			connection.createTableIfNotExists("City")
					.column("uuid", SQLDataType.UUID)
					.column("name", SQLDataType.VARCHAR)
					.constraint(primaryKey("uuid"))
					.execute();
			connection.createTableIfNotExists("Address")
					.column("uuid", SQLDataType.UUID)
					.column("street", SQLDataType.VARCHAR)
					.column("city", SQLDataType.UUID)
					.column("country", SQLDataType.VARCHAR)
					.constraint(primaryKey("uuid"))
					.execute();
			connection.createTableIfNotExists("Person")
					.column("uuid", SQLDataType.UUID)
					.column("age", SQLDataType.BIGINT)
					.column("emailAddress", SQLDataType.VARCHAR)
					.constraint(primaryKey("uuid"))
					.execute();
			connection.createTableIfNotExists("Professor")
					.column("uuid", SQLDataType.UUID)
					.column("name", SQLDataType.VARCHAR)
					.column("phoneNumber", SQLDataType.VARCHAR)
					.column("livesAt", SQLDataType.UUID)
					.constraint(primaryKey("uuid"))
					.execute();
			connection.createTableIfNotExists("Student")
					.column("uuid", SQLDataType.UUID)
					.column("name", SQLDataType.VARCHAR)
					.column("phoneNumber", SQLDataType.VARCHAR)
					.column("livesAt", SQLDataType.UUID)
					.constraint(primaryKey("uuid"))
					.execute();
		}

		create(roy, yossi, karin);
	}

	@AfterEach
	@Override
	protected void tearDown()
	{
		//Dropping all SQL databases.
		try (DSLContext connection = using("jdbc:sqlite:resourcesTemp/sqliteDBs/test.db"))
		{
			connection.dropTable("City").execute();
			connection.dropTable("Address").execute();
			connection.dropTable("Person").execute();
			connection.dropTable("Professor").execute();
			connection.dropTable("Student").execute();
		}

		try (DSLContext connection = using("jdbc:sqlite:resourcesTemp/sqliteDBs/test2.db"))
		{
			connection.dropTable("City").execute();
			connection.dropTable("Address").execute();
			connection.dropTable("Person").execute();
			connection.dropTable("Professor").execute();
			connection.dropTable("Student").execute();
		}
	}
}

