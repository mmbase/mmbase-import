/*
 * ApplicationConfiguration.java
 *
 * Created on June 13, 2002, 11:00 PM
 */

package org.mmbase.applications.config;

/**
 *
 * @author  mmbase
 */
public interface ApplicationConfiguration {
    public NodeManagerConfigurations getNodeManagerConfigurations();
    public RelationManagerConfigurations getRelationManagerConfigurations();
    
}
