package org.mmbase.applications.config;

public interface RelationManagerConfiguration extends NodeManagerConfiguration{
	public String getSourceNodeManagerName();
	public String getDestinationNodeManagerName();
        public String getDirectionality();
}
