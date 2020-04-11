package dataLayer.crud.dbAdapters;

import dataLayer.configReader.FieldsMapping;
import dataLayer.crud.Entity;
import dataLayer.crud.Read;
import dataLayer.crud.filters.*;

import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class DatabaseAdapter
{
	private static Stream<Entity> groupEntities(Stream<Entity> entities)
	{
		return entities
				.collect(Collectors.toMap(Entity::getUuid, Function.identity(), Entity::merge))
				.values().stream();
	}

	/**
	 * given:<br>
	 * Entity{"entityType": "person", "fieldsValues": {"uuid": {"value": 1}, "name": "Moshe", "phone": 0546815181}}<br>
	 * Entity{"entityType": "Person", "fieldsValues": {"uuid": {"value": 1}, "livesAt": {"value": 999}}}
	 *
	 * @param complexFilter Filter that can get results from multiple filters
	 * @return Entity{"entityType": "person", "fieldsValues": {"uuid": {"value": 1}, "name": "Moshe", "phone": 0546815181, "livesAt": {"value": 999}}}
	 */
	private static Stream<Stream<Entity>> defragEntities(ComplexFilter complexFilter)
	{
		return Stream.of(complexFilter.getComplexQuery())
				.map(filter -> groupEntities(Read.simpleRead(filter)));
	}

	private static boolean isEntityInSet(Set<Entity> entities, Entity entityFrag)
	{
		return entities.stream()
				.map(Entity::getUuid)
				.anyMatch(entityFrag.getUuid()::equals);
	}

//	public void revealQuery(VoidFilter voidFilter)
//	{
//		voidFilter.accept(this);
//	}

//	public Stream<Entity> revealQuery(Filter filter)
//	{
//		return filter.accept(this);
//	}

	public abstract void executeCreate(CreateSingle createSingle);

	public abstract void executeCreate(CreateMany createMany);

	public abstract Stream<Entity> execute(Eq eq);

	public abstract Stream<Entity> execute(Ne ne);

	public abstract Stream<Entity> execute(Gt gt);

//	abstract Set<Entity> execute(All all);

	public abstract Stream<Entity> execute(Lt lt);

	public abstract Stream<Entity> execute(Gte gte);

	public abstract Stream<Entity> execute(Lte lte);

	public abstract Stream<Entity> execute(String entityType, UUID uuid, FieldsMapping fieldsMapping);

	public Stream<Entity> execute(And and)
	{
		return defragEntities(and)
				.reduce((set1, set2) ->
				{
					final Set<Entity>
							collected1 = set1.collect(Collectors.toSet()),
							collected2 = set2.collect(Collectors.toSet());
					return groupEntities(Stream.concat(collected1.stream(), collected2.stream())
							.filter(entityFrag ->
									isEntityInSet(collected1, entityFrag) &&
											isEntityInSet(collected2, entityFrag)));
				})
				.orElse(Stream.of());
	}

	public Stream<Entity> execute(Or or)
	{
		return groupEntities(defragEntities(or)
				.flatMap(Function.identity()));
	}
}
