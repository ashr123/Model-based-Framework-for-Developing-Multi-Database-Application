package queryAdapters.dbAdapters;

import queryAdapters.crud.*;

import java.util.List;
import java.util.Map;

/**
 * Element
 */
public interface DatabaseAdapter
{
	void revealQuery(Query query);

	void execute(CreateQuery createQuery);

	void execute(ReadQuery readQuery);

	void execute(UpdateQuery updateQuery);

	void execute(DeleteQuery deleteQuery);

	void execute(CreateSingle createSingle);

	void execute(CreateMany createMany);

	List<Map<String, Object>> execute(Eq eq);

	List<Map<String, Object>> execute(Ne ne);

	List<Map<String, Object>> execute(Gt gt);

	List<Map<String, Object>> execute(Lt lt);

	List<Map<String, Object>> execute(Gte gte);

	List<Map<String, Object>> execute(Lte lte);
}
