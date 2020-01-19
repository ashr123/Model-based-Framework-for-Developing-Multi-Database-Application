package dataLayer.configReader;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Represents one or many {@link Entity}'s fields storage
 */
public class FieldsMapping
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
		FieldsMapping fieldsMapping = (FieldsMapping) o;
		return type == fieldsMapping.type &&
				connStr.equals(fieldsMapping.connStr) &&
				location.equals(fieldsMapping.location);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(type, connStr, location);
	}

	@Override
	public String toString()
	{
		return "FieldsMapping{" +
				"type='" + type + '\'' +
				", connStr='" + connStr + '\'' +
				", location='" + location + '\'' +
				'}';
	}
}
