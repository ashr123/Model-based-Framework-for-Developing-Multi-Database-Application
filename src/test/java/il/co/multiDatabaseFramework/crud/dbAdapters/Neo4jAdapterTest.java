package il.co.multiDatabaseFramework.crud.dbAdapters;

import il.co.multiDatabaseFramework.readers.Reader;
import iot.jcypher.database.DBAccessFactory;
import iot.jcypher.database.DBProperties;
import iot.jcypher.database.DBType;
import iot.jcypher.database.IDBAccess;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.neo4j.driver.v1.AuthTokens;

import java.io.IOException;
import java.util.Properties;

import static il.co.multiDatabaseFramework.crud.Query.create;

class Neo4jAdapterTest extends DatabaseTest
{
	@BeforeEach
	@Override
	protected void setUp() throws IOException
	{
		Reader.loadConfAndSchema("src/test/resources/configurations/configurationNeo4j.json",
				"src/test/resources/schemas/Schema.json");
		create(roy, yossi, karin, arnon);
	}

	@AfterEach
	@Override
	protected void tearDown()
	{
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
	}
}
