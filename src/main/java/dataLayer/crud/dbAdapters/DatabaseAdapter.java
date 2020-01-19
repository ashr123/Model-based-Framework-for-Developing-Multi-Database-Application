package dataLayer.crud.dbAdapters;

import dataLayer.crud.filters.*;

import java.util.List;
import java.util.Map;

/**
 * Element
 */
public interface DatabaseAdapter
{
	void revealQuery(VoidQuery voidQuery);

	List<Map<String, Object>> revealQuery(Query query);

	void executeCreate(CreateSingle createSingle);

	void executeCreate(CreateMany createMany);

	List<Map<String, Object>> execute(Eq eq);

	List<Map<String, Object>> execute(Ne ne);

	List<Map<String, Object>> execute(Gt gt);

	List<Map<String, Object>> execute(Lt lt);

	List<Map<String, Object>> execute(Gte gte);

	List<Map<String, Object>> execute(Lte lte);

	List<Map<String, Object>> execute(And and);

	List<Map<String, Object>> execute(Or or);

	List<Map<String, Object>> execute(All all);
}
