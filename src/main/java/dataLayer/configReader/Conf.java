package dataLayer.configReader;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class Conf
{
	@JsonIgnore
	private static Conf configuration;

	static
	{
		try
		{
			configuration = Reader.read("/configuration.json");
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@JsonProperty("dataStores")
	private Map<String, DataStore> dataStores;
	@JsonProperty("entities")
	private Map<String, Entity> entities;

	private Conf()
	{
	}

	public static Conf getConfiguration()
	{
		return configuration;
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
		entities.values().forEach(entity -> entity.validate(keySet));
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
