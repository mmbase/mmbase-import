package org.mmbase.applications.vprowizards.spring.cache.template;


public class QueryTemplate extends Template{
    
    private String sourceType;
    private String relationRole;
    private String destinationType;
    
    public QueryTemplate(String template) {
        super(template);
        parseQueryPath();
    }

    private void parseQueryPath() {
        String[] parts = template.split("\\.");
        if(parts.length != 3){
            throw new IllegalStateException(String.format("can not construct a QueryTemplate instance from %s", template));
        }
        init(parts[0],parts[1], parts[2]);
    }

    private void init(String sourceType, String relationrole, String destinationType) {
        this.sourceType = sourceType;
        this.relationRole = relationrole;
        this.destinationType = destinationType;
    }


    

    public String getSourceType() {
        return sourceType;
    }

    public String getRelationRole() {
        return relationRole;
    }

    public String getDestinationType() {
        return destinationType;
    }
}
