/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/ 
package org.mmbase.applications.vprowizards.spring;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mmbase.applications.vprowizards.spring.action.Action;



/**
 * This interface should be implemented by all classes that should be able to be used for
 * binding action html forms from the vpro-wizards.
 * @author ebunders
 *
 */
public interface Command {
	/**
	 * 
	 * @return the map that have all the Action instances mapped to arbitrary keys.
	 * This method is used in the databinding process, The way actions are mapped is like this:
	 * actions[<specific action mapping>]
	 */
	public Map<String, Map<String, Action>> getActions();
	
	/**
	 * This method should process all the action objects after the data binding was done.
	 * The class implementing this interface should make shure the actions are executed in
	 * the proper order (i.e. node create actions first and so on).
	 * The method is called after the data binding process is completed.
	 * @param transaction an mmbase transaction in which all datamanipulation is done
	 * @param request
	 * @param response
	 * @return
	 */
	public void processActions(HttpServletRequest request, HttpServletResponse response, ResultContainer resultContainer);
}
