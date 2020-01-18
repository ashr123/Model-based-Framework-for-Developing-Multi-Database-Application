package Temp;

//-----------------------------------config.ConfigObj.java-----------------------------------

import com.fasterxml.jackson.annotation.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
		"type",
		"connString",
		"username",
		"password",
		"data"
})
public class ConfigObj
{

	@JsonProperty("type")
	private String type;
	@JsonProperty("connString")
	private String connString;
	@JsonProperty("username")
	private String username;
	@JsonProperty("password")
	private String password;
	@JsonProperty("data")
	private List<DB> data;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	/**
	 * No args constructor for use in serialization
	 */
	public ConfigObj()
	{
	}

	/**
	 * @param password
	 * @param data
	 * @param connString
	 * @param type
	 * @param username
	 */
	public ConfigObj(String type, String connString, String username, String password, List<DB> data)
	{
		super();
		this.type = type;
		this.connString = connString;
		this.username = username;
		this.password = password;
		this.data = data;
	}

	@JsonProperty("type")
	public String getType()
	{
		return type;
	}

	@JsonProperty("type")
	public void setType(String type)
	{
		this.type = type;
	}

	@JsonProperty("connString")
	public String getConnString()
	{
		return connString;
	}

	@JsonProperty("connString")
	public void setConnString(String connString)
	{
		this.connString = connString;
	}

	@JsonProperty("username")
	public String getUsername()
	{
		return username;
	}

	@JsonProperty("username")
	public void setUsername(String username)
	{
		this.username = username;
	}

	@JsonProperty("password")
	public String getPassword()
	{
		return password;
	}

	@JsonProperty("password")
	public void setPassword(String password)
	{
		this.password = password;
	}

	@JsonProperty("data")
	public List<DB> getData()
	{
		return data;
	}

	@JsonProperty("data")
	public void setData(List<DB> data)
	{
		this.data = data;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties()
	{
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value)
	{
		this.additionalProperties.put(name, value);
	}

	@Override
	public String toString()
	{
		return new ToStringBuilder(this).append("type", type).append("connString", connString).append("username", username).append("password", password).append("data", data).append("additionalProperties", additionalProperties).toString();
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder().append(password).append(data).append(connString).append(additionalProperties).append(type).append(username).toHashCode();
	}

	@Override
	public boolean equals(Object other)
	{
		if (other == this)
		{
			return true;
		}
		if (!(other instanceof ConfigObj))
		{
			return false;
		}
		ConfigObj rhs = ((ConfigObj) other);
		return new EqualsBuilder().append(password, rhs.password).append(data, rhs.data).append(connString, rhs.connString).append(additionalProperties, rhs.additionalProperties).append(type, rhs.type).append(username, rhs.username).isEquals();
	}

}

