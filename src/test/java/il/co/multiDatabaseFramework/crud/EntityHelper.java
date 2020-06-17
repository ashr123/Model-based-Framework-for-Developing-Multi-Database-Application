package il.co.multiDatabaseFramework.crud;

import java.util.Map;
import java.util.UUID;

public class EntityHelper
{
	private EntityHelper()
	{
	}

	public static Entity entityBuilder(UUID uuid, String entityType, Map<String, Object> fieldsValues)
	{
		return new Entity(uuid, entityType, fieldsValues);
	}

	public static Map<String, Object> getEntityFieldsAndValues(Entity entity)
	{
		return entity.getFieldsValues();
	}
}
