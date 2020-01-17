package queryAdapters.crud;

import java.util.List;

public abstract class ComplexQuery implements Query {
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
