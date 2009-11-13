package org.mmbase.applications.vprowizards.spring.cache;

import static org.easymock.EasyMock.*;
import junit.framework.TestCase;

import org.mmbase.bridge.*;

public class FlushNameTemplateBeanIntegrationTest extends TestCase {

    private FlushNameTemplateBean bean;
    private Cloud mockCloud;
    private Node mockSourceNode;
    private Node mockDestinationNode;
    private NodeList mockNodeList;

    protected FlushNameTemplateBean createConfiguredBean() {
        bean = new FlushNameTemplateBean();
        bean.setNodeNumber("100");
        bean.setNodeType("user");
        Cloud coud = createMock(Cloud.class);
        bean.setCloud(coud);
        return bean;
    }

    public void test_illigalStateErrors_on_processAndGet() {
        bean = new FlushNameTemplateBean();
        tryForIllegalStateOnProcessAndGet();

        bean.setNodeNumber("100");
        tryForIllegalStateOnProcessAndGet();

        bean.setNodeType("user");
        tryForIllegalStateOnProcessAndGet();

        bean.setTemplate("[template]");
        tryForIllegalStateOnProcessAndGet();

        Cloud cloud = createMock(Cloud.class);
        bean.setCloud(cloud);
        try {
            bean.getTemplate();
        } catch (Exception e) {
            fail("illegal state exception should not occur but is: "+e.getMessage());
        }
    }

    public void test_simple_template_with_matching_nodetype_without_nodenumber_has_number_inserted() {
        bean = createConfiguredBean();
        bean.setTemplate("een:[user]");
        assertEquals("een:[user:100]", bean.getTemplate());
    }
    
    public void test_simple_template_without_matching_nodetype_is_ignored() {
        bean = createConfiguredBean();
        bean.setTemplate("een:[person]");
        assertEquals("een:[person]", bean.getTemplate());
    }

    public void test_simple_template_if_nodetype_dousnt_match_template_must_be_cleaned_of_nodenrs() {
        bean = createConfiguredBean();
        bean.setTemplate("een:[disco:600]");
        assertEquals("een:[disco]", bean.getTemplate());
    }
    
    public void test_multi_template_nodenr_should_be_inserted_where_nodetype_matches(){
        bean = createConfiguredBean();
        bean.setTemplate("een:[disco], twee:[user]");
        assertEquals("een:[disco], twee:[user:100]", bean.getTemplate());
        
        bean = createConfiguredBean();
        bean.setTemplate("een:[user], twee:[user]");
        assertEquals("een:[user:100], twee:[user:100]", bean.getTemplate());
        
        bean = createConfiguredBean();
        bean.setCloud(createMockCloudForQueryTemplates("test", "nogwat", 100, 50));
        bean.setTemplate("een:[user.test.nogwat], twee:[nogwat.user.test]");
        assertEquals("een:[user.test.nogwat:50], twee:[nogwat.user.test]", bean.getTemplate());
        verifyMockObjects();
    }
    
    public void test_multi_template_nodenumbers_should_be_stripped_for_all_subtemplates(){
        bean = createConfiguredBean();
        bean.setTemplate("een:[disco:5], twee:[user:5]");
        assertEquals("een:[disco], twee:[user:100]", bean.getTemplate() );
    }
    
    public void test_malfomed_templates_are_ignored(){
        testIgnoredPattern("een:(user)");
        testIgnoredPattern("een:[user)");
        testIgnoredPattern("een:[user");
        testIgnoredPattern("een:user]");
        testIgnoredPattern("een:[[user]");
        testIgnoredPattern("een:[user::90]");
        testIgnoredPattern("een:[us[er:90]");
        testIgnoredPattern("een:[een.twee:90]");
        testIgnoredPattern("een:[een.twee.drie.vier:90]");
        testIgnoredPattern("een:[een.:90]");
        
    }
    
    public void test_query_should_not_be_run_bet_nodenr_deleted_when_querytemplate_dousnt_match(){
        bean = createConfiguredBean();
        
        bean.setCloud(createMockCloudForQueryTemplates("posrel", "thing", 100, 50));
        bean.setNodeType("thing");
        bean.setTemplate("een:[disco.posrel.thing:300]");
        assertEquals("node type dousn't match.", "een:[disco.posrel.thing]", bean.getTemplate());
        try {
            verifyMockObjects();
            fail("The nodetype dous not match the template, so the query is not executed.");
        } catch (AssertionError e) {
            //ignore
        }
    }

    private Cloud createMockCloudForQueryTemplates(String relationRole, String destinationType, int sourceNodeNumber, int destinationNodeNumber) {
        mockCloud = createMock(Cloud.class);
        mockSourceNode = createMock(Node.class);
        mockDestinationNode = createMock(Node.class);
        mockNodeList = createMock(NodeList.class);
        
        expect(mockCloud.getNode(""+sourceNodeNumber)).andReturn(mockSourceNode).atLeastOnce();
        expect(mockSourceNode.getRelatedNodes(destinationType, relationRole, "both")).andReturn(mockNodeList).atLeastOnce();
        
        expect(mockNodeList.size()).andReturn(1).atLeastOnce();
        expect(mockNodeList.getNode(0)).andReturn(mockDestinationNode);
        
        expect(mockDestinationNode.getNumber()).andReturn(destinationNodeNumber).atLeastOnce();
        replay(mockCloud);
        replay(mockSourceNode);
        replay(mockDestinationNode);
        replay(mockNodeList);
        
        return mockCloud;
    }
    
    private void verifyMockObjects(){
        verify(mockCloud);
        verify(mockSourceNode);
        verify(mockDestinationNode);
        verify(mockNodeList);
    }
    
    private void testIgnoredPattern(String pattern){
        bean = createConfiguredBean();
        bean.setTemplate(pattern);
        assertEquals(pattern + " should be ignored",pattern, bean.getTemplate());
    }
                                                    
    



    private void tryForIllegalStateOnProcessAndGet() {
        try {
            bean.getTemplate();
            fail("illegal state exception should be thrown");
        } catch (IllegalStateException e) {/* ignore */
        }
    }

}
