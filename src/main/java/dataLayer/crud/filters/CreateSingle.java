package dataLayer.crud.filters;

import dataLayer.configReader.Entity;
import dataLayer.crud.dbAdapters.DatabaseAdapter;

public class CreateSingle implements VoidFilter
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

	@Override
	public String toString()
	{
		return "CreateSingle{" +
				"entity=" + entity +
				'}';
	}
}
