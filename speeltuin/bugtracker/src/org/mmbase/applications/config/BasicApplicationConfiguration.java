package org.mmbase.applications.config;

/**
 * implementation of ApplicationConfiguration
 * @author Kees Jongenburger
 */
public class BasicApplicationConfiguration implements org.mmbase.applications.config.ApplicationConfiguration {
    String name ="Default";
    NodeManagerConfigurations nodeManagerConfigurations;
    RelationManagerConfigurations relationManagerConfigurations;
    /** Creates a new instance of BasicApplicationConfiguration */
    public BasicApplicationConfiguration() {
        nodeManagerConfigurations = new NodeManagerConfigurations();
        relationManagerConfigurations = new RelationManagerConfigurations();
    }
    
    /**
     * set the name of the application
     * @param name the name of the application
     **/
    public void setName(String name){
	    this.name = name;
    }
    
    public String getName(){
	    return name;
    }
    
    /**
     * add a nodemanager configuration to the application
     * @param nodeManagerConfiguration the nodemanagerconfiguration to add
     **/
    public void addNodeManagerConfiguration(NodeManagerConfiguration nodeManagerConfiguration){
        nodeManagerConfigurations.add(nodeManagerConfiguration);
    }
    
    /**
     * add a relation manager configuration to the application
     * @param relationManagerConfiguration the relation manager configuration to add
     *
     **/
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
