package dataLayer.crud.dbAdapters;

import dataLayer.configReader.Entity;
import dataLayer.configReader.FieldsMapping;
import dataLayer.crud.Read;
import dataLayer.crud.filters.*;

import java.util.Optional;
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
				.collect(Collectors.groupingBy(Entity::getUuid, Collectors.reducing(Entity::merge)))
				.values().stream()
				.map(Optional::get);
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

	public Stream<Entity> execute(Or or)
	{
		return groupEntities(defragEntities(or)
				.flatMap(Function.identity()));

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
//		return Stream.of(or.getComplexQuery())
//				.flatMap(filter -> revealQuery(filter).stream())
//				.collect(Collectors.toSet());
	}
}
