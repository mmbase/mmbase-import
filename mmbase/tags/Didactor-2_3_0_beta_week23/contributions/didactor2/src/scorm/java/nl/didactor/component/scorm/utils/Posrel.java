package nl.didactor.component.scorm.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.mmbase.bridge.*;

/**
 * @javadoc
 */

public class Posrel {
    public static List getRelatedOrderedMetaStandarts(Cloud cloud, Node node) {
        //Gives a list of metastandarts ordered by posrel.pos
        NodeList nlVirtualMetaStandarts = cloud.getList("" + node.getNumber(), node.getNodeManager().getName() + ",posrel,metastandard2", "metastandard2.number,posrel.pos", null, "posrel.pos", null, "destination", false);
        List listResult = new ArrayList();
        
        for(Iterator it = nlVirtualMetaStandarts.iterator(); it.hasNext();) {
            Node nodeMetaStandart = (Node) it.next();
            listResult.add(cloud.getNode(nodeMetaStandart.getStringValue("metastandard2.number")));
        }
        
        return listResult;
    }
    



    public static List getRelatedOrderedMetaDefinitions(Cloud cloud, Node node) {
        //Gives a list of metadefinitons ordered by posrel.pos
        NodeList nlVirtualMetaDefinitions = cloud.getList("" + node.getNumber(), node.getNodeManager().getName() + ",posrel,metadefinition2", "metadefinition2.number,posrel.pos", null, "posrel.pos", null, "destination", false);
        List listResult = new ArrayList();
        
        for(Iterator it = nlVirtualMetaDefinitions.iterator(); it.hasNext();) {
            Node nodeMetaDefinition = (Node) it.next();
            listResult.add(cloud.getNode(nodeMetaDefinition.getStringValue("metadefinition2.number")));
        }
        
        return listResult;
    }
    
    

    
    
    public Node getPosrelNode(Cloud cloud, String sSource, String sDestination) {
        NodeManager nmPosrel = cloud.getNodeManager("posrel");
        NodeList nlPosrel = nmPosrel.getList("snumber='" + sSource + "' AND dnumber='" + sDestination + "'", null, null);
        return (Node) nlPosrel.get(0);
    }
    
    public Node getPosrelNode(Cloud cloud, int sSource, int sDestination) {
        return this.getPosrelNode(cloud, "" + sSource, "" + sDestination);
    }
    
}
