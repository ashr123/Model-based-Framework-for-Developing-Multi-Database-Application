package dataLayer.crud.dbAdapters;

import dataLayer.crud.filters.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Element
 */
public interface DatabaseAdapter
{
	void revealQuery(VoidFilter voidFilter);

	Map<String, Set<Map<String, Object>>> revealQuery(Filter filter);

	void executeCreate(CreateSingle createSingle);

	void executeCreate(CreateMany createMany);

	Map<String, Set<Map<String, Object>>> execute(Eq eq);

	Map<String, Set<Map<String, Object>>> execute(Ne ne);

	Map<String, Set<Map<String, Object>>> execute(Gt gt);

	Map<String, Set<Map<String, Object>>> execute(Lt lt);

	Map<String, Set<Map<String, Object>>> execute(Gte gte);

	Map<String, Set<Map<String, Object>>> execute(Lte lte);

	Map<String, Set<Map<String, Object>>> execute(And and);

	Map<String, Set<Map<String, Object>>> execute(Or or);

	Map<String, Set<Map<String, Object>>> execute(All all);
}
