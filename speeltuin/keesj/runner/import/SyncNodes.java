
import org.mmbase.bridge.*;
import java.util.*;
/**
 * implementation of the mmbase syncnode on top of the
 * mmci. Syncnodes are used to keep track of imported nodes
 * that belong to a other systems. syncnodes can be used in later
 * stage to "reimport" nodes or create new relations
 * @author keesj
 * @version $Id: SyncNodes.java,v 1.1.1.1 2004-07-08 10:32:15 keesj Exp $
 */
public class SyncNodes{
    private Cloud cloud ;
    private NodeManager syncNodesNodeManager;

    /**
     * @param cloud the cloud in wich we import and where the syncnodes builder is
     * set to actice
     **/
    public SyncNodes(Cloud cloud){
	this.cloud = cloud;
	this.syncNodesNodeManager  = cloud.getNodeManager("syncnodes");
    }

    /**
     * create an entry in the syncnodes table
     * @param exportSourceName identifier for the source
     * @param exportSourceNumber number/identifier of the external entity
     * @param localNumber node number of the local(imported) node
     * @return true if a new synnode was created or false if the node already existed
     **/
    public boolean add(String exportSourceName,int exportSourceNumber,int localNumber){
	if (isImported(exportSourceName,exportSourceNumber) ==-1){
	    Node syncNode = syncNodesNodeManager.createNode();
	    syncNode.setIntValue("timestamp",(int)System.currentTimeMillis()/ 1000);
	    syncNode.setStringValue("exportsource",exportSourceName);
	    syncNode.setIntValue("exportnumber",exportSourceNumber);
	    syncNode.setIntValue("localnumber",localNumber);
	    syncNode.commit();
	    return true;
	}
	return false;
    }
    
    /**
     * @param exportSourceName
     * @param exportSourceNumber
     * @return -1 if the node was not imported else it returns the new object number
     **/
    public int isImported(String exportSourceName,int exportSourceNumber){
	NodeList nodeList = syncNodesNodeManager.getList("exportsource='"+exportSourceName +"' AND exportnumber="+exportSourceNumber +"",null,null);
	if (nodeList.size() != 1){
	    return -1;
	}
	return nodeList.getNode(0).getIntValue("localnumber");
    }

    public void delete(String exportSourceName,int exportSourceNumber){
	NodeList nodeList = syncNodesNodeManager.getList("exportsource='"+exportSourceName +"' AND exportnumber="+exportSourceNumber +"",null,null);
	if (nodeList.size() != 1){
	    return ;
	}
	Node node = nodeList.getNode(0);
	node.delete();
    }
}
