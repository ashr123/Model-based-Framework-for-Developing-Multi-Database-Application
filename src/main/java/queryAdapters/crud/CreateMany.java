package queryAdapters.crud;

import dataLayer.configReader.Entity;
import queryAdapters.dbAdapters.DatabaseAdapter;

import java.util.LinkedList;
import java.util.List;

public class CreateMany implements Query
{
	private final List<Entity> entities;

	public CreateMany()
	{
		entities = new LinkedList<>();
	}

	private CreateMany(List<Entity> entities)
	{
		this.entities = entities;
	}

	public List<Entity> getEntities()
	{
		return entities;
	}

	public CreateMany add(Entity entity)
	{
		entities.add(entity);
		return this;
	}

	public static CreateMany createMany(List<Entity> entities)
	{
		return new CreateMany(entities);
	}

	public static CreateMany createMany()
	{
		return new CreateMany();
	}

	public void accept(DatabaseAdapter databaseAdapter)
	{
		databaseAdapter.execute(this);
	}
}
