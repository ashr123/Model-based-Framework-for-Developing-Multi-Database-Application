package Temp;

import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedList;
import java.util.Objects;

public class ConfigurationLine
{
	final String hostType;
	final String connectionString;
	final String username;
	final String password;
	final LinkedList<Pair<String, String>> data;

	public ConfigurationLine(String hostType, String connectionString, String username, String password, LinkedList<Pair<String, String>> data)
	{
		this.hostType = hostType;
		this.connectionString = connectionString;
		this.username = username;
		this.password = password;
		this.data = data;
	}

	public String getHostType()
	{
		return hostType;
	}

	public String getConnectionString()
	{
		return connectionString;
	}

	public String getUsername()
	{
		return username;
	}

	public String getPassword()
	{
		return password;
	}

	public LinkedList<Pair<String, String>> getData()
	{
		return data;
	}

	@Override
	public String toString()
	{
		return "Temp.ConfigurationLine{" +
				"hostType='" + hostType + '\'' +
				", connectionString='" + connectionString + '\'' +
				", username='" + username + '\'' +
				", password='" + password + '\'' +
				", data=" + data +
				'}';
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ConfigurationLine that = (ConfigurationLine) o;
		return connectionString.equals(that.connectionString);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(connectionString);
	}
}
