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
