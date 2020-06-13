package dataLayer.readers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dataLayer.readers.configReader.Conf;
import dataLayer.readers.schemaReader.Schema;

import java.io.IOException;
import java.util.InputMismatchException;

public class Reader
{
	private final static ObjectMapper objectMapper = new ObjectMapper();
	private static boolean cyclic;

	private Reader()
	{
	}

	/**
	 * Loads <b>non-cyclic</b> configuration and schema files from given paths.
	 *
	 * @param confPath   path of configuration file
	 * @param schemaPath path of schema file
	 * @throws IOException if a low-level I/O problem (unexpected end-of-input, network error) occurs
	 */
	public static void loadConfAndSchema(String confPath, String schemaPath) throws IOException
	{
		loadConfAndSchema(confPath, schemaPath, false);
	}

	/**
	 * Loads configuration and schema files from given paths.
	 *
	 * @param confPath   path of configuration file
	 * @param schemaPath path of schema file
	 * @param isCyclic   states if the schema contains cyclic relationships
	 * @throws IOException if a low-level I/O problem (unexpected end-of-input, network error) occurs
	 */
	public static void loadConfAndSchema(String confPath, String schemaPath, boolean isCyclic) throws IOException
	{
		Conf.loadConfiguration(confPath, objectMapper);
		Schema.loadSchema(schemaPath, objectMapper);
		checkValidity();
		Reader.cyclic = isCyclic;
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

	public static boolean isCyclic()
	{
		return cyclic;
	}
}
