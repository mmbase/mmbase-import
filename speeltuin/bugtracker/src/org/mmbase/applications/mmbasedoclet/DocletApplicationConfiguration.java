/*
 * DocletApplicationConfiguration.java
 *
 * Created on June 13, 2002, 11:03 PM
 */

package org.mmbase.applications.mmbasedoclet;

import org.mmbase.applications.config.*;
/**
 *
 * @author  mmbase
 */
public class DocletApplicationConfiguration implements org.mmbase.applications.config.ApplicationConfiguration {
    String name ="Default";
    NodeManagerConfigurations nodeManagerConfigurations;
    RelationManagerConfigurations relationManagerConfigurations;
    /** Creates a new instance of DocletApplicationConfiguration */
    public DocletApplicationConfiguration() {
        nodeManagerConfigurations = new NodeManagerConfigurations();
        relationManagerConfigurations = new RelationManagerConfigurations();
    }
    public void setName(String name){
	    this.name = name;
    }
    public String getName(){
	    return name;
    }
    
    public void addNodeManagerConfiguration(NodeManagerConfiguration nodeManagerConfiguration){
        nodeManagerConfigurations.add(nodeManagerConfiguration);
    }
    
    public void addRelationManagerConfiguration(RelationManagerConfiguration relationManagerConfiguration){
        relationManagerConfigurations.add(relationManagerConfiguration);
    }
    
    public NodeManagerConfigurations getNodeManagerConfigurations() {
        return nodeManagerConfigurations;
    }
    
    public RelationManagerConfigurations getRelationManagerConfigurations() {
        return relationManagerConfigurations;
    }
    
}
