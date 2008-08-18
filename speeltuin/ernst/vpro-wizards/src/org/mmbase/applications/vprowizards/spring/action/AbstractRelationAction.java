package org.mmbase.applications.vprowizards.spring.action;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NotFoundException;
import org.mmbase.bridge.Relation;
import org.mmbase.bridge.RelationManager;
import org.mmbase.bridge.Transaction;

/**
 * @author Ernst Bunders
 *
 */
public abstract class AbstractRelationAction extends AbstractNodeAction {

	protected Node sourceNode = null;
	protected Node destinationNode = null;
	protected String sourceNodeNumber;
	protected String destinationNodeNumber;
	protected String sourceNodeRef;
	protected String destinationNodeRef;
	protected String role;
	protected RelationManager relationManager;

	@Override
	protected final Node createNode(Transaction transaction, Map<String, Node> idMap, HttpServletRequest request) {
		if (StringUtils.isBlank(role)) {
			addGlobalError("error.property.required", new String[] { "role", this.getClass().getName() });
			return null;
		} else {
			relationManager = transaction.getRelationManager(role);
			if (relationManager != null) {
				if (resolveSourceAndDestination(transaction, idMap)) {
					// create the relation node.
					if (mayCreate(relationManager)
							&& checkTypeRel(relationManager, sourceNode, destinationNode)) {
						//all preliminary checks ok.
						//call the callback method
						return doCreateNode(transaction, idMap, request);
					} else {
						return null;
					}
				}
			} else {
				addGlobalError("error.illegal.relationmanager", new String[] { "role" });
				return null;
			}
		}
		return null;
	}
	
	/**
	 * This callback method should be implemented by concrete implementations of 
	 * this class. It is called by {@link AbstractRelationAction#createNode(Transaction, Map, HttpServletRequest)} after all
	 * the preliminary checks. 
	 * @param transaction
	 * @param idMap
	 * @param request
	 * @return
	 */
	protected abstract Node doCreateNode(Transaction transaction, Map<String, Node> idMap, HttpServletRequest request);
	
	
	/**
	 * Try to resolve a node, by trying either the nodenr or node ref. If there are problems, relevant global errors are
	 * created.
	 * 
	 * @param refNotFoundErrorKey
	 * @param nodeRef
	 * @param nodenr
	 * @param idMap
	 * @param transaction
	 * @return the node
	 */
	protected final Node resolveNode(String refNotFoundErrorKey, String nodeRef, String nodenr,
			Map<String, Node> idMap, Transaction transaction) {
		Node result = null;
		if (nodenr == null) {
			if (nodeRef == null) {
				addGlobalError(refNotFoundErrorKey);
			} else {
				if (idMap.get(nodeRef) == null) {
					addGlobalError("error.node.notfound.idmap", new String[] { nodeRef });
				} else {
					result = idMap.get(nodeRef);
				}
			}
		} else {
			// try to load the node
			try {
				result = transaction.getNode(nodenr);
			} catch (NotFoundException e) {
				addGlobalError("error.node.notfound", new String[] { nodenr });
			}
		}
		return result;
	}

	/**
	 * (try to) resolve the source and destination nodes for this relation. Set global errors when fail.
	 * 
	 * @return true when source and destination nodes are found
	 */
	protected final boolean resolveSourceAndDestination(Transaction transaction, Map<String, Node> idMap) {
		sourceNode = resolveNode("error.create.relation.nosource", sourceNodeRef, sourceNodeNumber, idMap, transaction);
		destinationNode = resolveNode("error.create.relation.nodestination", destinationNodeRef, destinationNodeNumber,
				idMap, transaction);
		return (sourceNode != null && destinationNode != null);
	}

	public void setSourceNodeNumber(String sourceNodeNumber) {
		this.sourceNodeNumber = sourceNodeNumber;
	}

	public String getDestinationNodeNumber() {
		return destinationNodeNumber;
	}

	public void setDestinationNodeNumber(String destinationNodeNumber) {
		this.destinationNodeNumber = destinationNodeNumber;
	}

	public String getSourceNodeRef() {
		return sourceNodeRef;
	}

	public void setSourceNodeRef(String sourceNodeRef) {
		this.sourceNodeRef = sourceNodeRef;
	}

	public String getDestinationNodeRef() {
		return destinationNodeRef;
	}

	public void setDestinationNodeRef(String destinationNodeRef) {
		this.destinationNodeRef = destinationNodeRef;
	}

	public String getSourceNodeNumber() {
		return sourceNodeNumber;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

}
