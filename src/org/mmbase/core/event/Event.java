/*
 * Created on 6-sep-2005
 *
 */
package org.mmbase.core.event;

/**
 * This class is the base class for all mmbase events
 * @author Ernst Bunders
 * @since MMBase-1.8
 */
public abstract class Event {

	protected String name;
	
	


	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

}
