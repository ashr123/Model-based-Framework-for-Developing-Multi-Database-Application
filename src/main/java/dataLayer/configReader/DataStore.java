package dataLayer.configReader;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DataStore
{
	@JsonProperty("type")
	private DBType type;
	@JsonProperty("connStr")
	private String connStr;
	@JsonProperty("location")
	private String location;

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
