package dataLayer.configReader;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({"ConstantConditions", "unused"})
public class Conf
{
	@JsonIgnore
	private static Conf configuration;

	@JsonProperty("dataStores")
	private final Map<String, DataStore> dataStores = null;
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

	public DataStore getDataStore(String key)
	{
		return dataStores.get(key);
	}

	public Entity getEntity(String key)
	{
		return entities.get(key);
	}

	public DataStore getDataStoreFromEntityField(String entity, String field)
	{
		return dataStores.get(entities.get(entity).getFieldDataStoreName(field));
	}

	public Conf checkValidity()
	{
		final Set<String> keySet = dataStores.keySet();
		entities.values()
				.forEach(entity -> entity.validate(keySet));
		return this;
	}

	@Override
	public String toString()
	{
		return "Conf{" +
				"dataStores=" + dataStores +
				", entities=" + entities +
				'}';
	}
}
