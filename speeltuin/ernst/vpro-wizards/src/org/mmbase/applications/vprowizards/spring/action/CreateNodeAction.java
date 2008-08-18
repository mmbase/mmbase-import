package org.mmbase.applications.vprowizards.spring.action;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.mmbase.applications.vprowizards.spring.GlobalError;
import org.mmbase.applications.vprowizards.spring.ResultContainer;
import org.mmbase.applications.vprowizards.spring.cache.CacheFlushHint;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.Transaction;

/**
 * This action will create a node of the type that you set in the field
 * 'nodemanager'
 * 
 * @author Ernst Bunders
 * 
 */
public class CreateNodeAction extends AbstractNodeAction {

	private String nodeType;

	public final void setNodeType(String nodemanger) {
		this.nodeType = nodemanger;
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
				return nodeManager.createNode();
			}
			return null;
		}
	}

	/**
	 * This is the default implementation for creating new nodes. Override this
	 * if the node manager has to be derived in a different way.
	 * 
	 * @return the node manager used to create a new node with
	 */
	protected NodeManager resolveNodemanager(Transaction transaction) {
		if (StringUtils.isBlank(nodeType)) {
			addGlobalError("error.property.required", new String[] { "nodemanager", CreateNodeAction.class.getName() });
			return null;
		} else if (transaction.hasNodeManager(nodeType)) {
			return transaction.getNodeManager(nodeType);
		} else {
			addGlobalError("error.illegal.nodemanager", new String[] { nodeType });
			return null;
		}
	}

}
