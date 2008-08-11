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

	private String nodemanager;

	public final void setNodemanger(String nodemanger) {
		this.nodemanager = nodemanger;
	}

	@Override
	protected final void createCacheFlushHints() {
		CacheFlushHint hint = new CacheFlushHint(CacheFlushHint.TYPE_NODE);
		hint.setNodeType(nodemanager);
		addCachFlushHint(hint);
	}

	@Override
	protected final Node createNode(Transaction transaction, Map<String,Node>idMap, HttpServletRequest request) {
		NodeManager nodeManager = resolveNodemanager(transaction);
		if (nodeManager == null) {
			return null;
		} else {
			if(nodeManager.mayCreateNode()){
				return nodeManager.createNode();
			}else{
				addGlobalError("error.create.authorization", new String[]{nodemanager});
				addGlobalError("error.create.node");
				return null;
			}
		}
	}

	/**
	 * This is the default implementation for creating new nodes. Override this
	 * if the node manager has to be derived in a different way.
	 * 
	 * @return the node manager used to create a new node with
	 */
	protected NodeManager resolveNodemanager(Transaction transaction) {
		if (StringUtils.isBlank(nodemanager)) {
			addGlobalError("error.property.required", new String[] { "nodemanager", CreateNodeAction.class.getName() });
			addGlobalError("error.create.node");
			return null;
		} else if (transaction.hasNodeManager(nodemanager)) {
			return transaction.getNodeManager(nodemanager);
		} else {
			addGlobalError("error.illegal.nodemanager", new String[] { nodemanager });
			addGlobalError("error.create.node");
			return null;
		}
	}

	protected final String getNodenmanager() {
		return this.nodemanager;
	}

}
