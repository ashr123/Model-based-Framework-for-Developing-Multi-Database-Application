package dataLayer.crud.dbAdapters;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import dataLayer.readers.Reader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;

import static dataLayer.crud.Query.create;

class MongoDBAdapterTest extends DatabaseTest
{
	@BeforeEach
	@Override
	protected void setUp() throws IOException
	{
		Reader.loadConfAndSchema("resourcesTemp/configurations/configurationMongoDB.json",
				"resourcesTemp/schemas/Schema.json");
		create(roy, yossi, karin, arnon);
	}

	@AfterEach
	@Override
	protected void tearDown()
	{
		//Dropping all MongoDB databases.
		try (MongoClient mongoClient = MongoClients.create())
		{
			mongoClient.getDatabase("TestDB").drop();
			mongoClient.getDatabase("myDB").drop();
		}
	}
}
