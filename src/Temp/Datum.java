package Temp;

import com.fasterxml.jackson.annotation.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
		"class",
		"prop"
})
public class Datum {

	@JsonProperty("class")
	private String _class;
	@JsonProperty("prop")
	private String prop;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	/**
	 * No args constructor for use in serialization
	 *
	 */
	public Datum() {
	}

	/**
	 *
	 * @param prop
	 * @param _class
	 */
	public Datum(String _class, String prop) {
		super();
		this._class = _class;
		this.prop = prop;
	}

	@JsonProperty("class")
	public String getClass_() {
		return _class;
	}

	@JsonProperty("class")
	public void setClass_(String _class) {
		this._class = _class;
	}

	@JsonProperty("prop")
	public String getProp() {
		return prop;
	}

	@JsonProperty("prop")
	public void setProp(String prop) {
		this.prop = prop;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("_class", _class).append("prop", prop).append("additionalProperties", additionalProperties).toString();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(_class).append(additionalProperties).append(prop).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if (!(other instanceof Datum)) {
			return false;
		}
		Datum rhs = ((Datum) other);
		return new EqualsBuilder().append(_class, rhs._class).append(additionalProperties, rhs.additionalProperties).append(prop, rhs.prop).isEquals();
	}

}
