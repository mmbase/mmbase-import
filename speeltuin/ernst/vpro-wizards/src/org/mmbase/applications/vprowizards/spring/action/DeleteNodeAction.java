package org.mmbase.applications.vprowizards.spring.action;

import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.mmbase.applications.vprowizards.spring.GlobalError;
import org.mmbase.applications.vprowizards.spring.ResultContainer;
import org.mmbase.bridge.Node;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This acton can delete a node. you have to set the node number (or alias).
 * The node can not be retreved from the idMap.
 * @author Ernst Bunders
 *
 */
public class DeleteNodeAction extends Action {
	private static final Logger log = Logging.getLoggerInstance(DeleteNodeAction.class);
	
	private String nodenr;

	public String getNodenr() {
		return nodenr;
	}

	public void setNodenr(String nodenr) {
		this.nodenr = nodenr;
	}

	@Override
	public void process(Map<String, Node> nodeMap, ResultContainer resultContainer) {
		if(StringUtils.isBlank(nodenr)){
			resultContainer.getGlobalErrors().add(
					new GlobalError(
							"error.property.required", 
							new String[]{"nodenr", this.getClass().getName()}, 
							resultContainer.getLocale()
					)
			);
		}else{
			log.debug("deleting node with number "+nodenr);
			resultContainer.getTransaction().getNode(nodenr).delete(true);
		}
	}

}
