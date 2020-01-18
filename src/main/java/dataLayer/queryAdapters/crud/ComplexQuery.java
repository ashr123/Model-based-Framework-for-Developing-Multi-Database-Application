package dataLayer.queryAdapters.crud;

public abstract class ComplexQuery implements Query
{
	private final Query[] simpleQueries;

	ComplexQuery(Query... queries)
	{
		this.simpleQueries = queries;
	}

	public Query[] getComplexQuery()
	{
		return simpleQueries;
	}
}
