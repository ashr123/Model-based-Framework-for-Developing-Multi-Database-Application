package dataLayer.readers.configReader;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dataLayer.crud.Entity;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The main class for loading the configuration file.
 */
@SuppressWarnings({"ConstantConditions", "unused"})
public class Conf
{
	private final static ObjectMapper objectMapper = new ObjectMapper();

	private static Conf configuration;

	/**
	 * Example: {"Mongo1" -> FieldsMapping1, "Mongo2" -> FieldsMapping2, ...}
	 */
	@JsonProperty("fieldsMappings")
	private final Map<String, FieldsMapping> fieldsMappings = null;
	/**
	 * Example: {"Person" -> {"name" -> "mongo1", "age" -> "mongo1", ...}, "Address" -> {"city" -> "Be'er Sheva", ...}, ...}
	 */
	@JsonProperty("entities")
	private final Map<String, Map<String, String>> entities = null;
	private Map<FieldsMapping, String> fieldsMappingsReverse;

	private Conf()
	{
	}

//	public static Conf getConfiguration()
//	{
//		return Objects.requireNonNull(configuration, "No configuration file loaded");
//	}

	public static void loadConfiguration(URL url) throws IOException
	{
		(configuration = objectMapper.readValue(url, Conf.class)).checkValidity();
	}

	public static String toJson(Object o) throws JsonProcessingException
	{
		return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
	}

//	public FieldsMapping getFieldsMapping(String locationName)
//	{
//		return fieldsMappings.get(locationName);
//	}
//
//	public Map<String, String> getEntity(String key)
//	{
//		return entities.get(key);
//	}

	public static boolean isEntityComplete(Entity entityFrag)
	{
		return configuration.entities.get(entityFrag.getEntityType()).keySet().equals(entityFrag.getFieldsValues().keySet());
	}

	public static Set<FieldsMapping> getMissingFields(Entity entityFrag)
	{
		return configuration.entities.get(entityFrag.getEntityType()).keySet().stream()
				.filter(field -> !entityFrag.getFieldsValues().containsKey(field))
				.map(field -> getFieldsMappingFromEntityField(entityFrag.getEntityType(), field))
				.collect(Collectors.toSet());
	}

	public static Stream<FieldsMapping> getFieldsMappingForEntity(String entityType)
	{
		return configuration.entities.get(entityType).values().stream()
				.map(configuration.fieldsMappings::get);
	}

	public static Stream<FieldsMapping> getFieldsMappingForEntity(Entity entity)
	{
		return getFieldsMappingForEntity(entity.getEntityType());
	}

	public static FieldsMapping getFieldsMappingFromEntityField(String entityType, String field)
	{
		FieldsMapping output = configuration.fieldsMappings.get(configuration.entities.get(entityType).get(field));
		if (output == null)
			throw new NullPointerException("Field " + field + " doesn't exist in entity " + entityType);
		return output;
	}

	public static Set<String> getFieldsFromTypeAndMapping(String entityType, FieldsMapping fieldsMapping)
	{
		if (configuration.fieldsMappingsReverse == null)
			configuration.fieldsMappingsReverse = configuration.fieldsMappings.entrySet().stream()
					.collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
		String nickname = configuration.fieldsMappingsReverse.get(fieldsMapping);
		return configuration.entities.get(entityType).entrySet().stream()
				.filter(mapping -> mapping.getValue().equals(nickname))
				.map(Map.Entry::getKey)
				.collect(Collectors.toSet());
	}

	private void checkValidity()
	{
		final Set<String> keySet = fieldsMappings.keySet();
		if (!entities.values().stream()
				.allMatch(entityLocations -> keySet.containsAll(entityLocations.values())))
			throw new InputMismatchException("Not all fieldsLocations locations exists as FieldsMapping!!");
//		entities.values()
//				.forEach(entityLocations ->
//				{
//					if (!keySet.containsAll(entityLocations.values()))
//						throw new InputMismatchException("Not all fieldsLocations locations exists as FieldsMapping!!");
//				});
//		return this;
	}

	@Override
	public String toString()
	{
		return "Conf{" +
		       "fieldsMappings=" + fieldsMappings +
		       ", entities=" + entities +
		       '}';
	}

	@JsonIgnore
	public static Collection<String> getEntitiesType()
	{
		return configuration.entities.keySet();
	}

	@JsonIgnore
	public static Collection<String> getEntityProperties(String entityName)
	{
		return configuration.entities.get(entityName).keySet();
	}
}
