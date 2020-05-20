package dataLayer.readers.schemaReader;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RelationType
{
	@JsonProperty("composition")
	COMPOSITION,
	@JsonProperty("aggregation")
	AGGREGATION
}
