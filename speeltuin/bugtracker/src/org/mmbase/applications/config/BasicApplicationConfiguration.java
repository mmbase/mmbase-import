package org.mmbase.applications.config;

/**
 *
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
