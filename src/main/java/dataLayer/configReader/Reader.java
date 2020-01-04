package dataLayer.configReader;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class Reader
{
	private final static ObjectMapper objectMapper = new ObjectMapper();

	private Reader()
	{
	}

	static Conf read() throws IOException
	{
		return objectMapper.readValue(Reader.class.getResource("/configuration.json"), Conf.class).checkValidity();
	}

	public static String toJson(Object o) throws JsonProcessingException
	{
		return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
	}
}
