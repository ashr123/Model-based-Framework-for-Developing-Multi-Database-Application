package dataLayer.crud.dbAdapters;

import dataLayer.configReader.Conf;
import dataLayer.configReader.Entity;
import dataLayer.crud.Read;
import dataLayer.crud.filters.*;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dataLayer.crud.filters.Eq.eq;

/**
 * Element
 */
public abstract class DatabaseAdapter
{
	public void revealQuery(VoidFilter voidFilter)
	{
		voidFilter.accept(this);
	}

	public Set<Entity> revealQuery(Filter filter)
	{
		return filter.accept(this);
	}

	public abstract void executeCreate(CreateSingle createSingle);

	public abstract void executeCreate(CreateMany createMany);

	public abstract Set<Entity> execute(Eq eq);

	public abstract Set<Entity> execute(Ne ne);

	public abstract Set<Entity> execute(Gt gt);

	public abstract Set<Entity> execute(Lt lt);

	public abstract Set<Entity> execute(Gte gte);

	public abstract Set<Entity> execute(Lte lte);

//	abstract Set<Entity> execute(All all);

	protected Entity completeEntity(Entity entityFrag)
	{
		if (!Conf.getConfiguration().isEntityComplete(entityFrag))
			Read.read(eq(entityFrag.getEntityType(), "uuid", entityFrag.getUuid()))
					.forEach(entityFrag::merge);
		return entityFrag;
	}

	private Set<Entity> groupEntities(Stream<Entity> entities)
	{
		//noinspection OptionalGetWithoutIsPresent
		return entities
				.collect(Collectors.groupingBy(Entity::getUuid))
				.values().stream()
				.map(pojoFragments -> pojoFragments.stream()
						.reduce(Entity::merge)
						.get())
				.collect(Collectors.toSet());
	}

	/**
	 * given:<br>
	 * Entity{"entityType": "person", "fieldsValues": {"uuid": {"value": 1}, "name": "Moshe", "phone": 0546815181}}<br>
	 * Entity{"entityType": "Person", "fieldsValues": {"uuid": {"value": 1}, "livesAt": {"value": 999}}}
	 *
	 * @param complexFilter Filter that can get results from multiple filters
	 * @return Entity{"entityType": "person", "fieldsValues": {"uuid": {"value": 1}, "name": "Moshe", "phone": 0546815181, "livesAt": {"value": 999}}}
	 */
	private Stream<Set<Entity>> defragEntities(ComplexFilter complexFilter)
	{
		return Stream.of(complexFilter.getComplexQuery())
				.map(filter -> groupEntities(revealQuery(filter)
						.stream()));
	}

	private boolean isEntityInSet(Collection<Entity> entities, Entity entityFrag)
	{
		return entities.stream()
				.map(Entity::getUuid)
				.anyMatch(entityFrag.getUuid()::equals);
	}

	public Set<Entity> execute(And and)
	{
		return defragEntities(and)
				.reduce((set1, set2) ->
						groupEntities(Stream.concat(set1.stream(), set2.stream())
								.filter(entityFrag ->
										isEntityInSet(set1, entityFrag) &&
												isEntityInSet(set2, entityFrag))))
				.orElse(Set.of());
//		Set<Entity> result = new HashSet<>(resultSets.get(0));
//		resultSets.subList(1, resultSets.size()).forEach(result::retainAll);
//		return result;

//		return defragEntities(and)
//				.map(answerSet -> );


//		return Stream.of(and.getComplexQuery())
//				.map(this::revealQuery)
//				.reduce((map1, map2) ->
//						merge(map1, map2, (set1, set2) ->
//								Stream.concat(set1.stream(), set2.stream())
//										.collect(Collectors.toSet())))
//				.orElse(new HashMap<>());
	}

	public Set<Entity> execute(Or or)
	{
		return groupEntities(defragEntities(or)
				.flatMap(Collection::stream));

//		Map<String, Set<Map<String, Object>>> output = new HashMap<>();
//		List<Map<String, Set<Map<String, Object>>>> temp = Stream.of(or.getComplexQuery())
//				.map(this::revealQuery)
//				.collect(Collectors.toList());


		// See: https://www.baeldung.com/java-merge-maps#concat
//		return Stream.of(or.getComplexQuery())
//				.flatMap(filter -> revealQuery(filter).entrySet().stream())
//				.collect(Collectors.toMap(
//						Map.Entry::getKey,
//						Map.Entry::getValue,
//						(set1, set2) ->
//								Stream.concat(set1.stream(), set2.stream())
//										.collect(Collectors.toSet())));


//		return Stream.of(or.getComplexQuery())
//				.map(this::revealQuery)
//				.reduce((stringSetMap, stringSetMap2) ->
//				{
//					stringSetMap.forEach((type, entitiesSet) -> stringSetMap2.merge(type, entitiesSet, (maps, maps2) ->
//					{
//						HashSet<Map<String, Object>> set = new HashSet<>(maps);
//						set.addAll(maps2);
//						return set;
//					}));
//					return stringSetMap2;
//				})
//				.orElse(new HashMap<>());
//		Stream.of(or.getComplexQuery())
//				.map(this::revealQuery)
//				.forEach(typeMap -> typeMap
//						.forEach((type, entitiesSet) ->
//						{
//							// if type doesn't exist in output-create it
//							output.computeIfAbsent(type, typeIgnored -> new HashSet<>())
//									.addAll(entitiesSet); // merge existing entities in the type with remaining entities
//						}));
//		return output;

//		tempList.forEach(typeMap ->
//				tempList.stream()
//						.filter(typeMap2 -> !typeMap2.equals(typeMap))
//						.forEach(typeMap2 -> typeMap
//								.forEach((type, value) ->
//								{
//									Set<Map<String, Object>> lst = typeMap2.get(type);
//									if (lst != null)
//									{
//										output.computeIfAbsent(type, typeIgnored -> new LinkedList<>());
//										lst.removeAll(output.get(type));
//										output.get(type).addAll(lst);
//									}
//								})));
//		return output;

//		tempList.stream()
//				.map(typeMap -> typeMap.entrySet().stream()
//						.forEach(stringListEntry -> tempList.stream().))
//		tempList.stream()
//				.map(typeMap -> tempList.stream()
//						.flatMap(map -> map.entrySet().stream())
//						.filter(stringListEntry -> stringListEntry.getKey().equals())
//						.map(stringListEntry ->);
//
		// Entity(“person”, {“UUID”: {“value”: 1}, “name”: “Moshe, “phone”: 0546815181})
		// Entity(“Person”, {“UUID”: {“value”: 1}, “livesAt”: {“value”: 999}})↴
		// Entity(“person”, {“UUID”: {“value”: 1}, “name”: “Moshe, “phone”: 0546815181, “livesAt”: {“value”: 999}})
//		return Stream.of(or.getComplexQuery())
//				.flatMap(filter -> revealQuery(filter).stream())
//				.collect(Collectors.toSet());
	}

	public abstract Set<Entity> execute(UUIDEq uuidEq);
}
