package dataLayer.configReader;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Set;

/**
 * The main class for loading the configuration file.
 */
@SuppressWarnings({"ConstantConditions", "unused"})
public class Conf
{
	@JsonIgnore
	private static Conf configuration;

	@JsonProperty("fieldsMappings")
	private final Map<String, FieldsMapping> fieldsMappings = null;
	@JsonProperty("entities")
	private final Map<String, Entity> entities = null;

	private Conf()
	{
	}

	public static Conf getConfiguration()
	{
		if (configuration == null)
			throw new NullPointerException("No configuration file loaded");
		return configuration;
	}

	public static void loadConfiguration(URL url) throws IOException
	{
		configuration = Reader.read(url);
	}

	public FieldsMapping getFieldsMapping(String key)
	{
		return fieldsMappings.get(key);
	}

	public Entity getEntity(String key)
	{
		return entities.get(key);
	}

	public FieldsMapping getFieldsMappingFromEntityField(String entity, String field)
	{
		return fieldsMappings.get(entities.get(entity).getFieldFieldsMappingName(field));
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
