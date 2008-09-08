package org.mmbase.applications.vprowizards.spring.action;

import java.util.Map;

import org.mmbase.applications.vprowizards.spring.ResultContainer;
import org.mmbase.bridge.Node;

/**
 * This should be extended by all actions that can be registered to an Command instance.
 * 
 * @author Ernst Bunders
 *
 */
public abstract class Action {
	public abstract void process(ResultContainer resultContainer);
}
