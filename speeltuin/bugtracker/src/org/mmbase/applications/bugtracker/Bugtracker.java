/*
 * BugReport.java
 *
 * Created on June 7, 2002, 9:01 AM
 */

package org.mmbase.applications.bugtracker;

import org.mmbase.bridge.*;
import java.util.*;


/**
 * Bugtracker is the mail class for the bugtracker
 * it contains the root bugtracker category and information
 * about the maintainer of the bugtracker
 * @mmbase-application-name Bugtracker
 *
 * @mmbase-nodemanager-name bugtracker
 * @mmbase-nodemanager-field name string 50
 *
 * @mmbase-relationmanager-name maintainerrel
 * @mmbase-relationmanager-source bugtracker
 * @mmbase-relationmanager-destination bugtrackeruser
 *
 * @mmbase-relationmanager-name subcategoryrel
 * @mmbase-relationmanager-nodemanager catrel
 * @mmbase-relationmanager-directionality unidirectional
 * @mmbase-relationmanager-source bugtracker
 * @mmbase-relationmanager-destination bugcategory
 */
public class Bugtracker {
    
    
    private Cloud cloud;
    private Node bugtrackerNode = null;
    /** Creates a new instance of BugReport */
    public Bugtracker(Cloud cloud) {
        this.cloud = cloud;
        checkSetup();
        bugtrackerNode = cloud.getNode("bugtracker.start");
        
    }
    
    private void checkSetup(){
        try {
            Node node = cloud.getNode("bugtracker.start");
        } catch (BridgeException e){
            Node node = cloud.getNodeManager("bugtracker").createNode();
            node.setStringValue("name","MMBase bugtracker");
            node.commit();
            node.createAlias("bugtracker.start");
            System.out.println("created a bugtracker node with alias bugtracker.start");
        }
    }
    public String getName(){
	    return bugtrackerNode.getStringValue("name");
    }
    
    public void setName(String name){
            bugtrackerNode.setStringValue("name",name);
            bugtrackerNode.commit();
    }
    /**
     * bugreports are stored in a hirachical scruture
     * this method returns an empty root category with the
     * containing the sub categories
     **/
    public BugCategories getRootBugCategories(){
        BugCategories retval = new BugCategories();
        RelationIterator ri  =bugtrackerNode.getRelations("subcategoryrel","bugcategory").relationIterator();
        while(ri.hasNext()){
            Relation rel = ri.nextRelation();
            Node destNode = rel.getDestination();
            retval.add( new BugCategory(rel,destNode));
        }
        //sort on pos
        Collections.sort(retval);
    
        return retval;
    }
    
    public BugCategory createBugCategory(String name){
        Node node = cloud.getNodeManager("bugcategory").createNode();
        node.setStringValue("name",name);
        node.commit();
        return new BugCategory(null,node);
    }
    
    public void addRootBugCategory(int pos,BugCategory bugCategory){
        RelationManager relman = cloud.getRelationManager("bugtracker","bugcategory","subcategoryrel");
        Relation rel =relman.createRelation(bugtrackerNode,bugCategory.getBugCategoryNode());
        rel.setIntValue("pos",pos);
        rel.commit();
    }
    
    
    /**
     * @return the list of maintainers of the bugtracker
     **/
    public BugtrackerUsers getBugtrackerMaintainers(){
        return new BugtrackerUsers();
    }
    
    public String getVersion(){
        return "$Id: Bugtracker.java,v 1.5 2002-06-28 16:20:19 kees Exp $";
    }
    
    public static void main(String argv[]) throws Exception{
        CloudContext  cloudContext = ContextProvider.getCloudContext("rmi://24.132.250.86:1111/remotecontext");
        HashMap user = new HashMap();
        user.put("username", "admin");
        user.put("password", "admin2k");
        Cloud cloud = cloudContext.getCloud("mmbase","name/password",user);
        Bugtracker bugtracker = new Bugtracker(cloud);
        BugCategories rootCats = bugtracker.getRootBugCategories();
        for (int x =0 ; x < rootCats.size(); x++){
            BugCategory cat = rootCats.getBugCategory(x);
            System.err.println(cat.getName());
        }
        //bugtracker.addRootBugCategory(10,bugtracker.createBugCategory("org"));
        //bugtracker.addRootBugCategory(20,bugtracker.createBugCategory("nl"));
        //bugtracker.addRootBugCategory(5,bugtracker.createBugCategory("com"));
        
        
    }
}
