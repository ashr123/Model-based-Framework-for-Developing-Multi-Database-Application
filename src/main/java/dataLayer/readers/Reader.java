package dataLayer.readers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dataLayer.readers.configReader.Conf;
import dataLayer.readers.schemaReader.Schema;

import java.io.IOException;
import java.net.URL;
import java.util.InputMismatchException;

public class Reader
{
	private final static ObjectMapper objectMapper = new ObjectMapper();

	private Reader()
	{
	}

	public static void loadConfAndSchema(String confURL, String schemaURL) throws IOException
	{
		Conf.loadConfiguration(confURL, objectMapper);
		Schema.loadSchema(schemaURL, objectMapper);
		checkValidity();
	}

	private static void checkValidity()
	{
		if (!Conf.getEntitiesType().equals(Schema.getClassesName()))
			throw new InputMismatchException("Classes in Conf and Schema don't equate!");

		Schema.getClassesName().forEach(className ->
		{
			if (!Conf.getEntityProperties(className).equals(Schema.getClassesFields(className)))
				throw new InputMismatchException(className + "'s fields in Conf and Schema don't equate!");
		});
	}

	public static String toJson(Object o) throws JsonProcessingException
	{
		return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
	}
}
