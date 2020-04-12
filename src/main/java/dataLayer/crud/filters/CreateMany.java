package dataLayer.crud.filters;

import dataLayer.crud.Entity;
import dataLayer.crud.dbAdapters.DatabaseAdapter;

import java.util.Arrays;

public class CreateMany implements VoidFilter
{
	private final Entity[] entities;

	private CreateMany(Entity... entities)
	{
		this.entities = entities;
	}

	public static CreateMany createMany(Entity... entities)
	{
		return new CreateMany(entities);
	}

	public static CreateMany createMany()
	{
		return new CreateMany();
	}

	public Entity[] getEntities()
	{
		return entities;
	}

	@Override
	public void executeAt(DatabaseAdapter databaseAdapter)
	{
		databaseAdapter.executeCreate(this);
	}

	@Override
	public String toString()
	{
		return "CreateMany{" +
				"entities=" + Arrays.toString(entities) +
				'}';
	}
}
