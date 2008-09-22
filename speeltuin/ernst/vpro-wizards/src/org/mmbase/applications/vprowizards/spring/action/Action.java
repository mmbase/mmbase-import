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
