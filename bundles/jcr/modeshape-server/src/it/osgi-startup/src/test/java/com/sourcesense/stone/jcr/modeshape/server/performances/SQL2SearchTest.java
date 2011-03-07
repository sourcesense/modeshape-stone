package com.sourcesense.stone.jcr.modeshape.server.performances;

import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;

public class SQL2SearchTest extends SimpleSearchTest {

    protected Query createQuery(QueryManager manager, int i)
            throws RepositoryException {
        return manager.createQuery(
                "SELECT * FROM [nt:base] WHERE testcount=" + i,
                "JCR-SQL2");
    }

}
