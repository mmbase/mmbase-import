/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/ 
package org.mmbase.applications.vprowizards.spring.action;

import org.apache.commons.lang.StringUtils;
import org.mmbase.applications.vprowizards.spring.GlobalError;
import org.mmbase.applications.vprowizards.spring.ResultContainer;
import org.mmbase.bridge.NotFoundException;
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
	public void process(ResultContainer resultContainer) {
		if(StringUtils.isBlank(nodenr)){
			//this can't really happen, becouse the only way of instantiating a delete node action, is by setting
			//this property. there are no others!
			resultContainer.getGlobalErrors().add(
					new GlobalError(
							"error.property.required", 
							new String[]{"nodenr", this.getClass().getName()}, 
							resultContainer.getLocale()
					)
			);
		}else{
			log.debug("deleting node with number "+nodenr);
			try{
				resultContainer.getTransaction().getNode(nodenr).delete(true);
			}catch (NotFoundException e){
				resultContainer.addGlobalError(new GlobalError("error.node.notfound", new String[]{""+nodenr}, resultContainer.getLocale()));
			}
		}
	}

}
