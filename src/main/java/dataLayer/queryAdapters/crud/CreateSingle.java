package dataLayer.queryAdapters.crud;

import dataLayer.configReader.Entity;
import dataLayer.queryAdapters.dbAdapters.DatabaseAdapter;

public class CreateSingle implements VoidQuery
{
	private final Entity entity;

	private CreateSingle(Entity entity)
	{
		this.entity = entity;
	}

	public static CreateSingle createSingle(Entity entity)
	{
		return new CreateSingle(entity);
	}

	public Entity getEntity()
	{
		return entity;
	}

	public void accept(DatabaseAdapter databaseAdapter)
	{
		databaseAdapter.executeCreate(this);
	}
}
