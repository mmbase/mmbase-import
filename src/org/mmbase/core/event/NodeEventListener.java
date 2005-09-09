/*
 * Created on 6-sep-2005
 *
 */
package org.mmbase.core.event;

/**
 * This is the listener interface for node events
 * @author Ernst Bunders
 * @since MMBase-1.8
 */
public interface NodeEventListener extends EventListener {
	public void fire(NodeEvent event);
}
