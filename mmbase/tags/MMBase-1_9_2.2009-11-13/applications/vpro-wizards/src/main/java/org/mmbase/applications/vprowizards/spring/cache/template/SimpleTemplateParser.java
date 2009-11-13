package org.mmbase.applications.vprowizards.spring.cache.template;

public class SimpleTemplateParser extends AbstractTemplateParser implements TemplateParser {
    public static final String SIMPLE_TEMPLATE_REGEXP = "\\w+(:[0-9]+)?";

    public SimpleTemplateParser(String nodeType, String nodeNumber, String template) {
        super(nodeType, nodeNumber, template);
    }

    public void insertNodeNumber() {
        removeNodeNumber();
        if (nodeType.equals(template.getTemplate())) {
            template.setNodeNumber(nodeNumber);
        }
    }

    public static boolean isTemplate(String template) {
        return template.matches("^" + SIMPLE_TEMPLATE_REGEXP + "$");
    }

    @Override
    protected boolean matches(String template) {
        return SimpleTemplateParser.isTemplate(template);
    }

    @Override
    protected Template instantiateTemplate(String templateStr) {
        return new Template(templateStr);
    }

}
