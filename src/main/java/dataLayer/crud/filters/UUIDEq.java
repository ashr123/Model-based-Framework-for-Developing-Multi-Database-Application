package dataLayer.crud.filters;

import dataLayer.configReader.Entity;
import dataLayer.crud.dbAdapters.DatabaseAdapter;

import java.util.Set;
import java.util.UUID;

public class UUIDEq implements Filter
{
	private final String type;
	private final UUID uuid;

	public UUIDEq(String type, String uuid)
	{
		this(type, UUID.fromString(uuid));
	}

	public UUIDEq(String type, UUID uuid)
	{
		this.type = type;
		this.uuid = uuid;
	}

	public String getType()
	{
		return type;
	}

	public UUID getUuid()
	{
		return uuid;
	}

	@Override
	public Set<Entity> accept(DatabaseAdapter databaseAdapter)
	{
		return databaseAdapter.execute(this);
	}

	@Override
	public String toString()
	{
		return "uuidEq{" +
				"type='" + type + '\'' +
				", uuid='" + uuid + '\'' +
				'}';
	}
}
