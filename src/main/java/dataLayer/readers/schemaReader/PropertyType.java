package dataLayer.readers.schemaReader;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PropertyType
{
	@JsonProperty("string")
	STRING,
	@JsonProperty("array")
	ARRAY,
	@JsonProperty("number")
	NUMBER,
	@JsonProperty("object")
	OBJECT
}
