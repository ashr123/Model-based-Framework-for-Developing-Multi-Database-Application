package dataLayer.readers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dataLayer.crud.Pair;
import dataLayer.readers.configReader.Conf;
import dataLayer.readers.schemaReader.Schema;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.function.Function;

public class Reader
{
	private final static ObjectMapper objectMapper = new ObjectMapper();
	private static boolean cyclic;

	private static Map<String, Map<String, Pair<Function<Object, Object>, Function<Object, Object>>>> valuesMappers;

	private Reader()
	{
	}

	public static Object unDecodeValue(String entityType, String entityField, Object value)
	{
		Map<String, Pair<Function<Object, Object>, Function<Object, Object>>> fieldsMap;
		Pair<Function<Object, Object>, Function<Object, Object>> function;
		return valuesMappers == null || (fieldsMap = valuesMappers.get(entityType)) == null || (function = fieldsMap.get(entityField)) == null || function.getFirst() == null ?
		       value :
		       function.getFirst().apply(value);
	}

	public static Object decodeValue(String entityType, String entityField, Object value)
	{
		Map<String, Pair<Function<Object, Object>, Function<Object, Object>>> fieldsMap;
		Pair<Function<Object, Object>, Function<Object, Object>> function;
		return valuesMappers == null || (fieldsMap = valuesMappers.get(entityType)) == null || (function = fieldsMap.get(entityField)) == null || function.getSecond() == null ?
		       value :
		       function.getSecond().apply(value);
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
		//noinspection AssignmentToNull
		valuesMappers = null;
		Reader.cyclic = isCyclic;
	}

	public static void loadConfAndSchema(String confPath, String schemaPath, Map<String, Map<String, Pair<Function<Object, Object>, Function<Object, Object>>>> valuesMappers, boolean isCyclic) throws IOException
	{
		Conf.loadConfiguration(confPath, objectMapper);
		Schema.loadSchema(schemaPath, objectMapper);
		checkValidity();
		Reader.valuesMappers = valuesMappers;
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
