package org.mmbase.applications.vprowizards.spring.cache.template;

public class MockTemplateQueryRunner implements TemplateQueryRunner{
    private String _nodenr;
    
    public MockTemplateQueryRunner(String nodenr) {
        this._nodenr = nodenr;
    }

    public String runQuery(String nodeNumber, QueryTemplate queryTemplate) {
        return _nodenr;
    }
    
}
