package org.mmbase.applications.config;
/**
 * @author Kees Jongenbuger
 * @version $Id: RelationManagerConfiguration.java,v 1.3 2002-06-27 19:20:31 kees Exp $
 **/
public interface RelationManagerConfiguration{
    /**
     * @return the name/role of the relation manager
     **/
    public String getName();
    public String getSourceNodeManagerName();
    public String getDestinationNodeManagerName();
    public String getDirectionality();
    
    /**
     * the node manager of the relation manager is the nodemanager where
     * the relation manager stores it's data. The node manager(insrel/posrel) of the relation
     * manager can store the data for more relation managers
     * @return the nodeManager where the relation manager should store it's data the nodemanger should extend insrel
     **/
    public String getNodeManagerName();
}
