package dataLayer.crud.dbAdapters;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import dataLayer.readers.Reader;
import iot.jcypher.database.DBAccessFactory;
import iot.jcypher.database.DBProperties;
import iot.jcypher.database.DBType;
import iot.jcypher.database.IDBAccess;
import org.jooq.DSLContext;
import org.jooq.impl.SQLDataType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.neo4j.driver.v1.AuthTokens;

import java.io.IOException;
import java.util.Properties;

import static dataLayer.crud.Query.create;
import static org.jooq.impl.DSL.*;

class SQLAdapterTest extends DatabaseTest
{
	@BeforeAll
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
					.column("country", SQLDataType.VARCHAR)
					.constraint(primaryKey("uuid"))
					.execute();
			connection.createTableIfNotExists("Person")
					.column("uuid", SQLDataType.UUID)
					.column("name", SQLDataType.VARCHAR)
					.column("age", SQLDataType.BIGINT)
					.column("livesAt", SQLDataType.UUID)
					.constraint(primaryKey("uuid"))
					.execute();
			connection.createTableIfNotExists("Professor")
					.column("uuid", SQLDataType.UUID)
					.column("phoneNumber", SQLDataType.VARCHAR)
					.column( "emailAddress", SQLDataType.VARCHAR)
					.constraint(primaryKey("uuid"))
					.execute();
			connection.createTableIfNotExists("Student")
					.column("uuid", SQLDataType.UUID)
					.column("name", SQLDataType.VARCHAR)
					.column("age", SQLDataType.BIGINT)
					.constraint(primaryKey("uuid"))
					.execute();
		}

		create(roy, yossi, karin);
	}

	@AfterAll
	@Override
	protected void tearDown()
	{
		//Dropping all MongoDB databases.
		try (MongoClient mongoClient = MongoClients.create())
		{
			mongoClient.getDatabase("TestDB").drop();
			mongoClient.getDatabase("myDB").drop();
		}

		//Dropping all Neo4j databases.
		Properties props = new Properties();
		props.setProperty(DBProperties.SERVER_ROOT_URI, "bolt://localhost:7687");
		IDBAccess dbAccess = DBAccessFactory.createDBAccess(iot.jcypher.database.DBType.REMOTE, props, AuthTokens.basic("neo4j", "neo4j1"));
		try
		{
			dbAccess.clearDatabase();
		}
		finally
		{
			dbAccess.close();
		}

		props = new Properties();
		props.setProperty(DBProperties.SERVER_ROOT_URI, "bolt://localhost:11008");
		dbAccess = DBAccessFactory.createDBAccess(DBType.REMOTE, props, AuthTokens.basic("neo4j", "neo4j1"));
		try
		{
			dbAccess.clearDatabase();
		}
		finally
		{
			dbAccess.close();
		}

		//Dropping all SQL databases.
		try (DSLContext connection = using("jdbc:sqlite:resourcesTemp/sqliteDBs/test.db"))
		{
			connection.dropTable("City").execute();
			connection.dropTable("Address").execute();
			connection.dropTable("Person").execute();
			connection.dropTable("Professor").execute();
			connection.dropTable("Student").execute();
		}
	}
}

