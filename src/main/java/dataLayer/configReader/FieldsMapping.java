package dataLayer.configReader;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Represents a mapping from one or more fields of one or more entities to certain database.
 */
@SuppressWarnings("ConstantConditions")
public class FieldsMapping
{
	@JsonProperty("type")
	private final DBType type = null;
	@JsonProperty("connStr")
	private final String connStr = null;
	@JsonProperty("location")
	private final String location = null;
	@JsonProperty("username")
	private final String username = null;
	@JsonProperty("password")
	private final String password = null;

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

	public String getUsername()
	{
		return username;
	}

	public String getPassword()
	{
		return password;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		FieldsMapping that = (FieldsMapping) o;
		//noinspection ConstantConditions
		return type == that.type &&
				connStr.equals(that.connStr) &&
				location.equals(that.location) &&
				Objects.equals(username, that.username) &&
				Objects.equals(password, that.password);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(type, connStr, location, username, password);
	}

	@Override
	public String toString()
	{
		return "FieldsMapping{" +
				"type=" + type +
				", connStr='" + connStr + '\'' +
				", location='" + location + '\'' +
				", username='" + username + '\'' +
				", password='" + password + '\'' +
				'}';
	}
}
