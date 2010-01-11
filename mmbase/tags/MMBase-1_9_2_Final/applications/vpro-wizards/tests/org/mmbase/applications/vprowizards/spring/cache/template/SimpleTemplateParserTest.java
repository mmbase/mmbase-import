package org.mmbase.applications.vprowizards.spring.cache.template;

import java.util.ArrayList;
import java.util.List;


public class SimpleTemplateParserTest extends AbstractTemplateParserTest{

    @Override
    protected final void createInstanceAndInsertNodenr(String nodeType, String nodeNumber, String template) {
        templateParser = new SimpleTemplateParser (nodeType, nodeNumber, template);
        templateParser.insertNodeNumber();
    }

    @Override
    protected List<String> getIllegalPatterns() {
        List<String> l = new ArrayList<String>();
        l.add("hallo_daar:12a");
        l.add("hallo_daar:a");
        l.add("hallo_daar:");
        l.add("hal&$%lo_daar:");
        l.add("[hi");
        l.add("hi]");
        return l;
    }

    @Override
    protected List<String> getLegalPatterns() {
        List<String> l = new ArrayList<String>();
        l.add("hallo_daar");
        l.add("hallo_daar:12");
        return l;
    }

    @Override
    protected String getMatchingTemplate() {
        return "user";
    }

    @Override
    protected String getMatchingTemplateResult() {
        return "user:100";
    }

    @Override
    protected String getMatchingTemplateWithNodenr() {
        return "user:50";
    }

    @Override
    protected String getNodeNumber() {
        return "100";
    }

    @Override
    protected String getNodeType() {
        return "user";
    }

    @Override
    protected String getNonMatchingTemplate() {
        return "banaan";
    }

    @Override
    protected String getNonMatchingTemplateWithNodenr() {
        return "banaan:50";
    }

    @Override
    protected Class<? extends TemplateParser> getTemplateParserClass() {
        return SimpleTemplateParser.class;
    }

    @Override
    protected String getMatchingTemplateWithTemplateRemoved() {
        return getNodeNumber();
    }

    
}
