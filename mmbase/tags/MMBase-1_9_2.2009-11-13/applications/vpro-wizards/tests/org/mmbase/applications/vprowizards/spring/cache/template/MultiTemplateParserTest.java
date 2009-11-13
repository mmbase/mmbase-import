package org.mmbase.applications.vprowizards.spring.cache.template;

import java.util.ArrayList;
import java.util.List;

public class MultiTemplateParserTest extends AbstractTemplateParserTest {

    @Override
    protected void createInstanceAndInsertNodenr(String nodeType, String nodeNumber, String template) {
        MockTemplateQueryRunner mockQueryRunner = new MockTemplateQueryRunner(getNodeNumber());
        templateParser = new MultiTemplateParser(nodeType, nodeNumber, template, mockQueryRunner);
        templateParser.insertNodeNumber();
    }

    @Override
    protected List<String> getIllegalPatterns() {
        List<String> l = new ArrayList<String>();
        l.add("e[u[e]");
        l.add("hallo");
        l.add("hallo[simple");
        l.add("hallo[simple:89");
        l.add("hallo[simple.step:89]");
        l.add("hallo[query.template.too.long:89]");
        l.add("hallo[simple]and[smethign&**&&]");
        return l;
    }

    @Override
    protected List<String> getLegalPatterns() {
        List<String> l = new ArrayList<String>();
        l.add("\\[[j]");
        
        l.add("hi[simple]");
        l.add("[simple]ho");
        l.add("hi[simple]ho");
        
        l.add("hi[simple:10]");
        l.add("[simple:10]ho");
        
        l.add("hi[query.template.one]");
        l.add("[query.template.one]ho");
        l.add("hi[query.template.one]ho");
        
        l.add("hi[dit.is.een:90]");
        l.add("[dit.is.een]ho:90");
        l.add("hi[dit.is.een:90]ho");
        
        l.add("hi[simple]en[nogeensimple]");
        l.add("hi[simple:90]en[nogeensimple]");
        
        l.add("hi[simple]en[query.template.one]");
        l.add("hi[simple:90]en[query.template.one:8]");
        return l;
    }

    @Override
    protected String getMatchingTemplate() {
        return "pre[user]and[user.and.something]post";
    }

    @Override
    protected String getMatchingTemplateResult() {
        return "pre[user:100]and[user.and.something:100]post";
    }

    @Override
    protected String getMatchingTemplateWithNodenr() {
        return "pre[user:50]and[user.and.something:50]post";
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
        return "pre[job]and[job.and.something]post";
    }

    @Override
    protected String getNonMatchingTemplateWithNodenr() {
        return "pre[job:150]and[job.and.something:50]post";
    }

    @Override
    protected Class<? extends TemplateParser> getTemplateParserClass() {
        return MultiTemplateParser.class;
    }

    @Override
    protected String getMatchingTemplateWithTemplateRemoved() {
        return "pre100and100post";
    }

}
