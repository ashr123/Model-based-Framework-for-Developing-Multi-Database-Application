package queryAdapters.crud;

public abstract class ComplexQuery implements Query
{
	private final SimpleQuery[] simpleQueries;

	public ComplexQuery(SimpleQuery... simpleQueries)
	{
		this.simpleQueries = simpleQueries;
	}

	public SimpleQuery[] getComplexQuery()
	{
		return simpleQueries;
	}
}
