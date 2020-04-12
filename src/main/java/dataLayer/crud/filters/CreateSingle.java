package dataLayer.crud.filters;

import dataLayer.crud.Entity;
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

	@Override
	public void executeAt(DatabaseAdapter databaseAdapter)
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
