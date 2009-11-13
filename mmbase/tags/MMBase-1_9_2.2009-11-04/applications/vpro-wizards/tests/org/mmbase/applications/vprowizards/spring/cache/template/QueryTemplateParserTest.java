package org.mmbase.applications.vprowizards.spring.cache.template;

import java.util.ArrayList;
import java.util.List;

public class QueryTemplateParserTest extends AbstractTemplateParserTest {

    @Override
    protected void createInstanceAndInsertNodenr(String nodeType, String nodeNumber, String template) {
        QueryTemplate qt = new QueryTemplate(template);
        QueryTemplateParser queryTemplateParser = new QueryTemplateParser(nodeType, nodeNumber, template, qt, new MockTemplateQueryRunner(nodeNumber));
        templateParser = queryTemplateParser;
        templateParser.insertNodeNumber();
    }

    @Override
    protected List<String> getIllegalPatterns() {
        List<String> l = new ArrayList<String>();
        l.add("stap");
        l.add("stap:90");
        l.add("stap.stap");
        l.add("stap.stap:90");
        l.add("stap.stap.stap.stap");
        l.add("stap.stap.stap.stap:90");
        l.add("[stap.stap.stap");
        l.add("stap.stap.stap]");
        l.add("stap.999.stap]");
        return l;
    }

    @Override
    protected List<String> getLegalPatterns() {
        List<String> l = new ArrayList<String>();
        l.add("een.twee.drie");
        l.add("een.twee.drie:90");
        l.add("een.twee_ja.drie:90");
        return l;
    }

    @Override
    protected String getMatchingTemplate() {
        return "user.een.twee";
    }

    @Override
    protected String getMatchingTemplateResult() {
        return "user.een.twee:100";
    }

    @Override
    protected String getMatchingTemplateWithNodenr() {
        return "user.een.twee:50";
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
        return "banaan.een.twee";
    }

    @Override
    protected String getNonMatchingTemplateWithNodenr() {
        return "banaan.een.twee:50";
    }

    @Override
    protected Class<? extends TemplateParser> getTemplateParserClass() {
        return QueryTemplateParser.class;
    }

    @Override
    protected String getMatchingTemplateWithTemplateRemoved() {
        return getNodeNumber();
    }
}
