package dataLayer.crud.filters;

import dataLayer.configReader.Entity;
import dataLayer.crud.dbAdapters.DatabaseAdapter;

import java.util.LinkedList;
import java.util.List;

public class CreateMany implements VoidQuery
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

	public static CreateMany createMany(List<Entity> entities)
	{
		return new CreateMany(entities);
	}

	public static CreateMany createMany()
	{
		return new CreateMany();
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

	public void accept(DatabaseAdapter databaseAdapter)
	{
		databaseAdapter.executeCreate(this);
	}

	@Override
	public String toString()
	{
		return "CreateMany{" +
				"entities=" + entities +
				'}';
	}
}
