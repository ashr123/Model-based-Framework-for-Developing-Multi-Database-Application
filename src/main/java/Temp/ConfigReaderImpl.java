package Temp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class ConfigReaderImpl
{
	@JsonProperty("data")
	List<ConfigObj> lines;

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass())
			return false;
		ConfigReaderImpl that = (ConfigReaderImpl) o;
		return lines.equals(that.lines);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(lines);
	}

	@Override
	public String toString()
	{
		return "ConfigReaderImpl{" +
				"lines=" + lines +
				'}';
	}
}
