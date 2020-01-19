package dataLayer.crud.filters;

import dataLayer.configReader.Entity;
import dataLayer.crud.dbAdapters.DatabaseAdapter;

public class CreateMany implements VoidFilter
{
	private final Entity[] entities;

	private CreateMany(Entity... entities)
	{
		this.entities = entities;
	}

//	private CreateMany(List<Entity> entities)
//	{
//		this.entities = entities;
//	}

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

//	public CreateMany add(Entity entity)
//	{
//		entities.add(entity);
//		return this;
//	}

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
