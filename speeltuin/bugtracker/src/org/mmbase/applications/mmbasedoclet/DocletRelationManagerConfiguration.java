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
    
    String maintainer = "mmbase.org";
    String version = "1";
    
    String classFile ="InsRel";
    String searchAge ="360";
    String description;
    
    public void setMaintainer(String maintainer){
        this.maintainer = maintainer;
    }
    public String getMaintainer(){
        return maintainer;
    }
    
    public void setVersion(String version){
        this.version = version;
    }
    public String getVersion(){
        return version;
    }
    public void setDescription(String description){
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
    
    public void setClassFile(String classFile){
        this.classFile = classFile;
    }
    
    public String getClassFile(){
        return classFile;
    }
    
    public void setSearchAge(String searchAge){
        this.searchAge = searchAge;
    }
    
    public String getSearchAge(){
        return searchAge;
    }
    
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
