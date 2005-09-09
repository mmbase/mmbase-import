/*
 * Created on 6-sep-2005
 */
package org.mmbase.core.event;

/**
 * This is the listener interface for relation events
 * @author Ernst Bunders
 * @since MMBase-1.8
 */
public interface RelationEventListener extends EventListener {
	public void fire(RelationEvent event); 
}
