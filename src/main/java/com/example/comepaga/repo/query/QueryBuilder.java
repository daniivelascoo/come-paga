package com.example.comepaga.repo.query;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Map;
import java.util.Objects;

/**
 * The type Query builder.
 */
@Slf4j
public class QueryBuilder {

    /**
     * The Filter.
     */
    protected final Map<String, Object> filter;

    /**
     * Instantiates a new Query builder.
     *
     * @param filter the filter
     */
    public QueryBuilder(Map<String, Object> filter) {
        this.filter = filter;
    }


    /**
     * Build query.
     *
     * @return the query
     */
    public Query build() {
        if (this.filter.isEmpty()) return null;

        var query = new Query();
        Criteria criteria = new Criteria();

        for (Map.Entry<String, Object> f : filter.entrySet()) {
            if (Objects.isNull(f.getValue())) {
                criteria.and(f.getKey()).exists(false);
            } else {
                criteria.and(f.getKey()).is(f.getValue());
            }
        }

        query.addCriteria(criteria);
        return query;
    }
}
