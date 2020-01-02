package dataLayer.configReader;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;
import java.util.Set;

public class Conf
{
	@JsonProperty("dataStores")
	private Map<String, DataStore> dataStores;
	@JsonProperty("entities")
	private Map<String, Entity> entities;

	public Object getDataStore(String key)
	{
		return dataStores.get(key);
	}

	public Object getEntity(String key)
	{
		return entities.get(key);
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
