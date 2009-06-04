package org.mmbase.applications.vprowizards.spring.cache.template;

public abstract class AbstractTemplateParser implements TemplateParser {
    
    protected Template template;
    protected String nodeNumber;
    protected String nodeType;
    
    public AbstractTemplateParser(String nodeType, String nodeNumber, String template) {
        validateTempalate(template);
        this.template = instantiateTemplate(template);
        this.nodeNumber = nodeNumber;
        this.nodeType = nodeType;
    }
    public String getTemplate() {
        return template.getTemplate();
    }
    public void removeNodeNumber() {
        template.removeNodenr();
    }
   
    
    private void validateTempalate(String template){
        if(! matches(template)){
            throw new IllegalStateException(String.format("Template %s can not be parsed by parser %s", template, this.getClass().getName()));
        }
    }
    
    public void stripTemplateLeaveNodenr() {
        template.removeTemplate();
    }
    
    //TODO: this is all a bit weird. Better solution for static isTemplate() methods 
    //(should be in interface, but you don't want to instantiate a parser to see if it can parse a template...
    protected abstract boolean matches(String template);
    protected abstract Template instantiateTemplate(String templateStr);
    
}
