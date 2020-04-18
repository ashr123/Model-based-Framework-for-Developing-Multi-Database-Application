package dataLayer.readers;

import dataLayer.readers.configReader.Conf;
import dataLayer.readers.schemaReader.Schema;

import java.io.IOException;
import java.net.URL;
import java.util.InputMismatchException;

public class Reader
{
	private Reader()
	{
	}

	public static void loadConfAndSchema(URL confURL, URL schemaURL) throws IOException
	{
		Conf.loadConfiguration(confURL);
		Schema.loadSchema(schemaURL);
//		checkValidity();
	}

	// TODO check also coresponding fields in every class/type
	private static void checkValidity()
	{
		if (!Conf.getConfiguration().getEntitiesType().equals(Schema.getClassesName()))
			throw new InputMismatchException("Classes in Conf and Schema don't equate!");
	}
}
