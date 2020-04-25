package dataLayer.readers.schemaReader;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PropertyType
{
	@JsonProperty("string")
	STRING,
	@JsonProperty("number")
	NUMBER,
	@JsonProperty("boolean")
	BOOLEAN,
	@JsonProperty("array")
	ARRAY,
	@JsonProperty("object")
	OBJECT
}
