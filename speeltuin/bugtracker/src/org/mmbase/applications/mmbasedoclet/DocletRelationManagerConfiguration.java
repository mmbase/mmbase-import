/*
 * DocletRelationManagerConfiguration.java
 *
 * Created on June 13, 2002, 4:01 PM
 */

package org.mmbase.applications.mmbasedoclet;

import org.mmbase.applications.config.*;
/**
 *
 * @author  mmbase
 */
public class DocletRelationManagerConfiguration  implements RelationManagerConfiguration{
    String name;
    String sourceNodeManagerName;
    String destinationNodeManagerName;
    String directionality="bidirectional";
    String nodeManagerName;
    
    /** Creates a new instance of DocletRelationManagerConfiguration */
    public DocletRelationManagerConfiguration() {};
    
    public void setName(String name){
        this.name = name;
    }
    public String getName() {
        return name;
    }    

    public void setDestinationNodeManagerName(String destinationNodeManagerName){
        this.destinationNodeManagerName = destinationNodeManagerName;
    }
    
    public String getDestinationNodeManagerName() {
        return destinationNodeManagerName;
    }
    
    public void setNodeManagerName(String nodeManagerName){
        this.nodeManagerName = nodeManagerName;
    }
    
    public String getNodeManagerName() {
        return nodeManagerName;
        
    }
    
    public void setSourceNodeManagerName(String sourceNodeManagerName){
        this.sourceNodeManagerName = sourceNodeManagerName;
    }
    
    
    public String getSourceNodeManagerName() {
        return sourceNodeManagerName;
    }
    
    public void setDirectionality(String directionality){
            this.directionality = directionality;
    }
    
    public String getDirectionality() {
        return directionality;
    }
    
    
}
