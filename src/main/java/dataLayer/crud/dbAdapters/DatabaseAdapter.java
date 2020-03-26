package dataLayer.crud.dbAdapters;

import dataLayer.crud.filters.*;

import java.util.List;
import java.util.Map;

/**
 * Element
 */
public interface DatabaseAdapter
{
	void revealQuery(VoidFilter voidFilter);

	Map<String, List<Map<String, Object>>> revealQuery(Filter filter);

	void executeCreate(CreateSingle createSingle);

	void executeCreate(CreateMany createMany);

	Map<String, List<Map<String, Object>>> execute(Eq eq);

	Map<String, List<Map<String, Object>>> execute(Ne ne);

	Map<String, List<Map<String, Object>>> execute(Gt gt);

	Map<String, List<Map<String, Object>>> execute(Lt lt);

	Map<String, List<Map<String, Object>>> execute(Gte gte);

	Map<String, List<Map<String, Object>>> execute(Lte lte);

	Map<String, List<Map<String, Object>>> execute(And and);

	Map<String, List<Map<String, Object>>> execute(Or or);

	Map<String, List<Map<String, Object>>> execute(All all);
}
