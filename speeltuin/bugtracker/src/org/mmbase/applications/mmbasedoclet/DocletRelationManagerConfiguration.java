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
    String parentRelationManager ="insrel";
    FieldConfigurations fieldConfigurations;
    String sourceNodeManagerName;
    String destinationNodeManagerName;
    
    String relationManagerName;
    String nodeManagerName;
    
    String directionality="bidirectional";
    
    /** Creates a new instance of DocletRelationManagerConfiguration */
    public DocletRelationManagerConfiguration() {
        fieldConfigurations = new FieldConfigurations();
    }
    
    public String getExtends(){
        return parentRelationManager;
    }
    
    public FieldConfigurations getFieldConfigurations(){
        return fieldConfigurations;
    }
    
    public String getDestinationNodeManagerName() {
        return destinationNodeManagerName;
    }
        
    public String getNodeManagerName() {
        return nodeManagerName;
        
    }
    
    public String getSourceNodeManagerName() {
        return sourceNodeManagerName;
    }
    
    public String getDirectionality() {
        return directionality;
    }
    
}
