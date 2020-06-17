package il.co.multiDatabaseFramework.readers.configReader;

import com.fasterxml.jackson.annotation.JsonProperty;
import il.co.multiDatabaseFramework.crud.dbAdapters.DBType;

import java.util.InputMismatchException;
import java.util.Objects;

/**
 * Represents a mapping from one or more fields of one or more entities to certain database.
 */
@SuppressWarnings({"ConstantConditions", "FieldMayBeStatic"})
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

	private FieldsMapping()
	{
	}

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

	void checkValidity()
	{
		if (type == null)
			throw new InputMismatchException("Type can't be " + null);
		if (connStr == null)
			throw new InputMismatchException("Connection string can't be " + null);
		if (username != null && password == null)
			throw new InputMismatchException("If username not null, so must the password");
		else if (password != null && username == null)
			throw new InputMismatchException("If password not null, so must the username");
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (!(o instanceof FieldsMapping))
			return false;
		FieldsMapping that = (FieldsMapping) o;
		return type == that.type &&
		       connStr.equals(that.connStr) &&
		       Objects.equals(location, that.location) &&
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
