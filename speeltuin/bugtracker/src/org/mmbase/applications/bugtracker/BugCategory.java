
package org.mmbase.applications.bugtracker;

import org.mmbase.bridge.*;
import java.util.*;
/**
 *
 * @author  mmbase
 * @mmbase-nodemanager-name bugcategory
 * @mmbase-nodemanager-field name
 *
 * @mmbase-nodemanager-name catrel
 * @mmbase-nodemanager-extends insrel
 * @mmbase-nodemanager-field pos INTEGER
 *
 * @mmbase-relationmanager-name subcategoryrel
 * @mmbase-relationmanager-directionality unidirectional
 * @mmbase-relationmanager-source bugcategory
 * @mmbase-relationmanager-destination bugcategory
 * @mmbase-relationmanager-nodemanager catrel
 */
public class BugCategory implements Comparable{
    Relation parentRelation = null;
    Node bugCategoryNode = null;
    /** Creates a new instance of BugCategory */
    public BugCategory(Relation parentRelation,Node bugCategoryNode) {
        this.parentRelation = parentRelation;
        this.bugCategoryNode = bugCategoryNode;
    }
    
    public int getPos(){
        if (parentRelation != null){
            return parentRelation.getIntValue("pos");
        } else {
            return -1;
        }
    }
    
    public void setPos(int pos){
        if (parentRelation != null){
            parentRelation.setIntValue("pos",pos);
            parentRelation.commit();
        }
    }
    
    public BugCategories getBugSubCategories(){
        BugCategories retval = new BugCategories();
        RelationIterator ri  =bugCategoryNode.getRelations("subcategoryrel","bugcategory").relationIterator();
        while(ri.hasNext()){
            Relation rel = ri.nextRelation();
            Node destNode = rel.getDestination();
            retval.add( new BugCategory(rel,destNode));
        }
        //sort on pos
        Collections.sort(retval);
        
        return retval;
    }
    
    public void deleteBugCategory(){
        if (parentRelation!= null){
            //get the parent category if avaiable
            Node parentNode = parentRelation.getSource();
            if (parentNode.getNodeManager().getName().equals("bugcategory")){
                BugCategory parentCategory = new BugCategory(null,parentNode);
                
                //delete the current relation of the parent category
                parentRelation.delete();
                
                //get a list of sub categories
                BugCategories cats = getBugSubCategories();
                
                //and add them to the parent category
                for (int x =0 ; x < cats.size(); x++){
                    BugCategory currentSubCat = cats.getBugCategory(x);
                    parentCategory.addBugSubCategory(currentSubCat.getPos(),currentSubCat);
                }
            }
        }
        bugCategoryNode.delete(true);
    }
    
    public String getName(){
        return bugCategoryNode.getStringValue("name");
    }
    
    public void setName(String name){
        bugCategoryNode.setStringValue("name",name);
        bugCategoryNode.commit();
    }
    
    public BugCategories getSubCategories(){
        return new BugCategories();
    }
    
    public Node getBugCategoryNode(){
        return bugCategoryNode;
    }
    public int compareTo(Object obj) {
        BugCategory otherCat = (BugCategory)obj;
        return getPos() - otherCat.getPos();
    }
    
    public void addBugSubCategory(int pos,BugCategory bugCategory){
        RelationManager relman = bugCategoryNode.getCloud().getRelationManager("bugcategory","bugcategory","subcategoryrel");
        Relation rel =relman.createRelation(bugCategoryNode,bugCategory.getBugCategoryNode());
        rel.setIntValue("pos",pos);
        rel.commit();
    }
}
