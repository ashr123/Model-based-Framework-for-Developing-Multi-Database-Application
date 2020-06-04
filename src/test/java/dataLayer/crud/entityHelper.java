package dataLayer.crud;

import dataLayer.crud.dbAdapters.DatabaseAdapter;

import java.util.Map;
import java.util.UUID;

public class entityHelper
{
	private entityHelper()
	{
	}

	public static Entity entityBuilder(UUID uuid, String entityType, Map<String, Object> fieldsValues)
	{
		return new Entity(uuid, entityType, fieldsValues);
	}
}
