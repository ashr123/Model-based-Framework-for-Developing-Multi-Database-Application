//-----------------------------------Temp.DB.java-----------------------------------

package Temp;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
		"name",
		"data"
})
public class DB
{

	@JsonProperty("name")
	private String name;
	@JsonProperty("data")
	private List<Datum> data = null;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	/**
	 * No args constructor for use in serialization
	 */
	public DB()
	{
	}

	/**
	 * @param data
	 * @param name
	 */
	public DB(String name, List<Datum> data)
	{
		super();
		this.name = name;
		this.data = data;
	}

	@JsonProperty("name")
	public String getName()
	{
		return name;
	}

	@JsonProperty("name")
	public void setName(String name)
	{
		this.name = name;
	}

	@JsonProperty("data")
	public List<Datum> getData()
	{
		return data;
	}

	@JsonProperty("data")
	public void setData(List<Datum> data)
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
		return new ToStringBuilder(this).append("name", name).append("data", data).append("additionalProperties", additionalProperties).toString();
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder().append(name).append(additionalProperties).append(data).toHashCode();
	}

	@Override
	public boolean equals(Object other)
	{
		if (other == this)
		{
			return true;
		}
		if (!(other instanceof DB))
		{
			return false;
		}
		DB rhs = ((DB) other);
		return new EqualsBuilder().append(name, rhs.name).append(additionalProperties, rhs.additionalProperties).append(data, rhs.data).isEquals();
	}

}
