package dataLayer.configReader;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class DataStore
{
	@JsonProperty("type")
	private final DBType type = null;
	@JsonProperty("connStr")
	private final String connStr = null;
	@JsonProperty("location")
	private final String location = null;

	public DBType getType()
	{
		return type;
	}

	public String getConnStr()
	{
		return connStr;
	}

	public String getLocation()
	{
		return location;
	}

	@SuppressWarnings("ConstantConditions")
	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		DataStore dataStore = (DataStore) o;
		return type == dataStore.type &&
				connStr.equals(dataStore.connStr) &&
				location.equals(dataStore.location);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(type, connStr, location);
	}

	@Override
	public String toString()
	{
		return "DataStore{" +
				"type='" + type + '\'' +
				", connStr='" + connStr + '\'' +
				", location='" + location + '\'' +
				'}';
	}
}
