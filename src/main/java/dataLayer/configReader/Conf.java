package dataLayer.configReader;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The main class for loading the configuration file.
 */
@SuppressWarnings({"ConstantConditions", "unused"})
public class Conf
{
	@JsonIgnore
	private static Conf configuration;

	/**
	 * Example: {"Mongo1" -> FieldsMapping1, "Mongo2" -> FieldsMapping2, ...}
	 */
	@JsonProperty("fieldsMappings")
	private final Map<String, FieldsMapping> fieldsMappings = null;

	/**
	 * Example: {"Person" -> Entity1, "Address" -> Entity2, ...}
	 */
	@JsonProperty("entities")
	private final Map<String, Entity> entities = null;

	private Conf()
	{
	}

	public static Conf getConfiguration()
	{
		return Objects.requireNonNull(configuration, "No configuration file loaded");
	}

	public static void loadConfiguration(URL url) throws IOException
	{
		configuration = Reader.read(url);
	}

	public FieldsMapping getFieldsMapping(String locationName)
	{
		return fieldsMappings.get(locationName);
	}

	public Entity getEntity(String key)
	{
		return entities.get(key);
	}

	public boolean isEntityComplete(Entity entityFrag)
	{
		return entities.get(entityFrag.getEntityType()).getFieldsLocations().keySet().equals(entityFrag.getFieldsValues().keySet());
	}

	public Set<FieldsMapping> getMissingFields(Entity entityFrag)
	{
		return entities.get(entityFrag.getEntityType()).getFieldsLocations().keySet().stream()
				.filter(field -> !entityFrag.getFieldsValues().containsKey(field))
				.map(field -> getFieldsMappingFromEntityField(entityFrag.getEntityType(), field))
				.collect(Collectors.toSet());
	}

	public FieldsMapping getFieldsMappingFromEntityField(String entityType, String field)
	{
		return fieldsMappings.get(entities.get(entityType).getFieldFieldsMappingName(field));
	}

	public Conf checkValidity()
	{
		final Set<String> keySet = fieldsMappings.keySet();
		entities.values()
				.forEach(entity -> entity.validate(keySet));
		return this;
	}

	@Override
	public String toString()
	{
		return "Conf{" +
				"fieldsMappings=" + fieldsMappings +
				", entities=" + entities +
				'}';
	}
}
