package dataLayer.configReader;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class Reader
{
	private final static ObjectMapper objectMapper = new ObjectMapper();

	public static Conf read(String file) throws IOException
	{
		return objectMapper.readValue(Reader.class.getResource(file), Conf.class).checkValidity();
	}

	public static String toJson(Object o) throws JsonProcessingException
	{
		return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
	}
}
