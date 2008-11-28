package org.mmbase.applications.vprowizards.spring.cache;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;

import junit.framework.TestCase;
import static org.easymock.EasyMock.*;

public class FlushNameTemplateBeanTest extends TestCase {

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

        bean.setTemplate("template");
        tryForIllegalStateOnProcessAndGet();

        Cloud cloud = createMock(Cloud.class);
        bean.setCloud(cloud);
        try {
            bean.processAndGetTemplate();
        } catch (Exception e) {
            fail("illegal state exception should not occur");
        }
    }

    public void test_simple_template_without_nodenumber() {
        bean = createConfiguredBean();
        bean.setTemplate("een:[user]");
        assertEquals("with matching nodetype number should be inserted", "een:[user:100]", bean.processAndGetTemplate());
        bean.setTemplate("een:[person]");
        assertEquals("Non matching node type should be ignored","een:[person]", bean.processAndGetTemplate());
    }



    public void test_simple_template_that_dousnt_match() {
        bean = createConfiguredBean();
        bean.setTemplate("een:[disco:600]");
        assertEquals("Node type dousn't match. template should be ignored but number should be stripped", 
                "een:[disco]", bean.processAndGetTemplate());
        bean.setTemplate("een:[disco]");
        assertEquals("Node type dousn't match. template should be ignored", "een:[disco]", bean.processAndGetTemplate());
    }
    
    public void test_simple_template_with_several_subtemplates(){
        bean = createConfiguredBean();
        bean.setTemplate("een:[disco], twee:[user]");
        assertEquals("een:[disco], twee:[user:100]", bean.processAndGetTemplate());
        bean.setTemplate("een:[disco:5], twee:[user:5]");
        String result = bean.processAndGetTemplate();
        assertEquals("een:[disco], twee:[user:100]", result );
        
        //different node type
        bean.setNodeType("disco");
        bean.setTemplate("een:[disco], twee:[user]");
        assertEquals("een:[disco:100], twee:[user]", bean.processAndGetTemplate());
        bean.setTemplate("een:[disco:5], twee:[user:5]");
        assertEquals("een:[disco:100], twee:[user]", bean.processAndGetTemplate());
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
    
    public void test_query_templates(){
        bean = createConfiguredBean();
//        bean.setTemplate("een:[disco.posrel.thing]");
//        assertEquals("nodetype doesn't match", "een:[disco.posrel.thing]", bean.processAndGetTemplate());
//        
//        bean.setTemplate("een:[disco.posrel.thing:12]");
//        assertEquals("nodenr should be cleaned out", "een:[disco.posrel.thing]", bean.processAndGetTemplate());
        
        bean.setCloud(createMockCloudForQueryTemplates("posrel", "thing", 100, 50));
        bean.setTemplate("een:[disco.posrel.thing]");
        bean.setNodeNumber(""+100);
        bean.setNodeType("disco");
        assertEquals("template should match", "een:[disco.posrel.thing:50]", bean.processAndGetTemplate());
        verifyMockObjects();
        
        bean.setCloud(createMockCloudForQueryTemplates("posrel", "thing", 100, 50));
        bean.setTemplate("een:[disco.posrel.thing:300]");
        assertEquals("old node number must be deleted", "een:[disco.posrel.thing:50]", bean.processAndGetTemplate());
        verifyMockObjects();
        
        bean.setCloud(createMockCloudForQueryTemplates("posrel", "thing", 100, 50));
        bean.setNodeType("thing");
        bean.setTemplate("een:[disco.posrel.thing:300]");
        assertEquals("node type dousn't match.", "een:[disco.posrel.thing]", bean.processAndGetTemplate());
        try {
            verifyMockObjects();
            fail("The nodetype dous not match the template, so the query is not executed.");
        } catch (AssertionError e) {
            //ignore
        }
        
        bean.setCloud(createMockCloudForQueryTemplates("posrel", "thing", 100, 50));
        bean.setTemplate("een:[disco.posrel.thing:25]");
        assertEquals("old nodenumber must be deleted.", "een:[disco.posrel.thing]", bean.processAndGetTemplate());
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
        
        expect(mockCloud.getNode(""+sourceNodeNumber)).andReturn(mockSourceNode);
        expect(mockSourceNode.getRelatedNodes(destinationType, relationRole, "both")).andReturn(mockNodeList);
        
        expect(mockNodeList.size()).andReturn(1);
        expect(mockNodeList.getNode(0)).andReturn(mockDestinationNode);
        
        expect(mockDestinationNode.getNumber()).andReturn(destinationNodeNumber);
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
        assertEquals(pattern + " should be ignored",pattern, bean.processAndGetTemplate());
    }
                                                    
    



    private void tryForIllegalStateOnProcessAndGet() {
        try {
            bean.processAndGetTemplate();
            fail("illegal state exception should be thrown");
        } catch (IllegalStateException e) {/* ignore */
        }
    }

}
