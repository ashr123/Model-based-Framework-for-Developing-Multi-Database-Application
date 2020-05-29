package dataLayer.crud.dbAdapters;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import dataLayer.readers.Reader;
import iot.jcypher.database.DBAccessFactory;
import iot.jcypher.database.DBProperties;
import iot.jcypher.database.DBType;
import iot.jcypher.database.IDBAccess;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.neo4j.driver.v1.AuthTokens;

import java.io.IOException;
import java.util.Properties;

import static dataLayer.crud.Query.create;

class MongoDBAdapterTest extends DatabaseTest
{
	@BeforeAll
	@Override
	protected void setUp() throws IOException
	{
		Reader.loadConfAndSchema("resourcesTemp/configurations/configurationMongoDB.json",
				"resourcesTemp/schemas/Schema.json");
		create(roy, yossi, karin);
	}

	@AfterAll
	@Override
	protected void tearDown()
	{
		try (MongoClient mongoClient = MongoClients.create())
		{
			mongoClient.getDatabase("TestDB").drop();
			mongoClient.getDatabase("myDB").drop();
		}

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
