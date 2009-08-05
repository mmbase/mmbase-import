package org.mmbase.applications.vprowizards.spring.cache.template;


public interface TemplateQueryRunner {
    /**
     * @return the number that is the result of the query.
     */
    public String runQuery(String nodeNumber, QueryTemplate queryTemplate);
    
}
