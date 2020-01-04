package dataLayer.configReader;

import com.fasterxml.jackson.annotation.JsonProperty;

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
