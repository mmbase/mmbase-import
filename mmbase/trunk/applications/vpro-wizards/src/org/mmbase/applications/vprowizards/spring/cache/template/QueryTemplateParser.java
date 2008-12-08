package org.mmbase.applications.vprowizards.spring.cache.template;


public class QueryTemplateParser extends AbstractTemplateParser implements TemplateParser {
    public static final String QUERY_TEMPLATE_REGEXP = "\\w+(\\.\\w+){2}(:[0-9]+)?";
    
    private QueryTemplate queryTemplate;
    private TemplateQueryRunner templateQueryRunner = new DummyTemplateQueryRunner();

    public QueryTemplateParser(String nodeType, String nodeNumber, String template, QueryTemplate queryTemplate, TemplateQueryRunner templateQueryRunner){
        super(nodeType, nodeNumber, template);
        this.queryTemplate = queryTemplate;
        this.templateQueryRunner = templateQueryRunner;
    }

    public void insertNodeNumber() {
        removeNodeNumber();
        if(nodeType.equals(((QueryTemplate)template).getSourceType())){
            template.setNodeNumber(templateQueryRunner.runQuery(nodeNumber, queryTemplate));
        }
    }

    public void setTemplateQueryRunner(TemplateQueryRunner templateQueryRunner) {
        this.templateQueryRunner = templateQueryRunner;
    }
    public static boolean isTemplate(String template){
        return template.matches("^"+QUERY_TEMPLATE_REGEXP+"$");
    }

    @Override
    protected boolean matches(String template) {
        return QueryTemplateParser.isTemplate(template);
    }
    
    private static class DummyTemplateQueryRunner implements TemplateQueryRunner{
        public String runQuery(String nodeNumber, QueryTemplate queryTemplate) {
            throw new RuntimeException("OOps, you forgot to set a TemplateQueryRunner instance on the QueryTemplateParser");
        }
        
    }

    @Override
    protected Template instantiateTemplate(String templateStr) {
        return new QueryTemplate(templateStr);
    }
}
