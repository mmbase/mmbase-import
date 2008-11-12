/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/ 
package org.mmbase.applications.vprowizards.spring.action;

import org.mmbase.applications.vprowizards.spring.ResultContainer;

/**
 * This should be extended by all actions that can be registered to an Command instance.
 * 
 * @author Ernst Bunders
 *
 */
public abstract class Action {
	public abstract void process(ResultContainer resultContainer);
}
