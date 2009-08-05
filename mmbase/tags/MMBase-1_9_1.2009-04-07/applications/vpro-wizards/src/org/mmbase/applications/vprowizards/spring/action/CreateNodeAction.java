/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/ 
package org.mmbase.applications.vprowizards.spring.action;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.mmbase.applications.vprowizards.spring.cache.CacheFlushHint;
import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This action will create a node of the type that you set in the field
 * 'nodemanager'
 * 
 * @author Ernst Bunders
 * 
 */
public class CreateNodeAction extends AbstractNodeAction {
	private static final Logger log = Logging.getLoggerInstance(CreateNodeAction.class);
 	private String nodeType;
 	
 	private String nodeAsParam = ""; 

	public final void setNodeType(String nodemanger) {
		this.nodeType = nodemanger;
	}
	
	public final void setNodeAsParam(String paramName){
	    nodeAsParam = paramName;
	}
	

	public final String getNodenType() {
		return this.nodeType;
	}

	@Override
	protected final void createCacheFlushHints() {
		CacheFlushHint hint = new CacheFlushHint(CacheFlushHint.TYPE_NODE);
		hint.setNodeType(nodeType);
		addCachFlushHint(hint);
	}

	@Override
	protected final Node createNode(Transaction transaction, Map<String,Node>idMap, HttpServletRequest request) {
		NodeManager nodeManager = resolveNodemanager(transaction);
		if (nodeManager == null) {
			return null;
		} else {
			if(mayCreate(nodeManager)){
				log.info(String.format("Creating new node of type '%s'", nodeManager.getName()));
				return nodeManager.createNode();
			}
			return null;
		}
	}

	@Override
    protected void processNode(Transaction transaction) {
	    if(getNode() != null && ! StringUtils.isBlank(nodeAsParam)){
	        addNodeParam(nodeAsParam);
	    }
    }

    /**
	 * This is the default implementation for creating new nodes. Override this
	 * if the node manager has to be derived in a different way.
	 * 
	 * @return the node manager used to create a new node with
	 */
	protected NodeManager resolveNodemanager(Transaction transaction) {
		if(transaction == null){
			throw new IllegalStateException("transaction should not be null!");
		}
		if (StringUtils.isBlank(nodeType)) {
			log.debug("Can not create node manager instance: name field is empty");
			addGlobalError("error.property.required", new String[] { "nodeType", CreateNodeAction.class.getName() });
			return null;
		} else if (transaction.hasNodeManager(nodeType)) {
			NodeManager nodeManager = transaction.getNodeManager(nodeType);
			log.debug(String.format("Returning node manager of type '%s'", nodeManager.getName()));
			return nodeManager;
		} else {
			log.debug(String.format("Node manager of type '%s' does not exist: can not get instance.", nodeType));
			addGlobalError("error.illegal.nodemanager", new String[] { nodeType });
			return null;
		}
	}

}
