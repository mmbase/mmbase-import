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
import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @author Ernst Bunders
 *
 */
public abstract class AbstractRelationAction extends AbstractNodeAction {
	
	private static final Logger log = Logging.getLoggerInstance(AbstractRelationAction.class);

	protected Node sourceNode = null;
	protected Node destinationNode = null;
	protected String sourceNodeNumber;
	protected String destinationNodeNumber;
	protected String sourceNodeRef;
	protected String destinationNodeRef;
	protected String role;
	protected RelationManager relationManager;

	/**
	 * This template method implementation handles all the preconditions for relation actions. When
	 * these are met, a new template method is called, {@link this#doCreateNode(Transaction, Map, HttpServletRequest)}
	 * When you create specific relation actions, you extend this class and implement that method.
	 */
	@Override
	protected final Node createNode(Transaction transaction, Map<String, Node> idMap, HttpServletRequest request) {
		if (StringUtils.isBlank(role)) {
			addGlobalError("error.property.required", new String[] { "role", this.getClass().getName() });
			return null;
		} else {
			try{
			relationManager = transaction.getRelationManager(role);
				if (resolveSourceAndDestination(transaction, idMap)) {
					log.debug("source and destination successfully resolved");
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
			} catch(NotFoundException e) {
				log.error("relation manager "+ role+" does not exist");
				addGlobalError("error.illegal.relationmanager", new String[] { "role" });
				return null;
			}
		}
		return null;
	}
	
	/**
	 * This template method should be implemented by concrete implementations of 
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
		if (StringUtils.isBlank(nodenr)) {
			if (StringUtils.isBlank(nodeRef)) {
				log.debug("nodenr is empty and noderef is empty too. this stinks!");
				addGlobalError(refNotFoundErrorKey);
			} else {
				log.debug(String.format("trying to find node with id %s in the idmap", nodeRef));
				if (idMap.get(nodeRef) == null) {
					log.warn(String.format("oops, could not find node in idMap with id %s", nodeRef));
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
				log.warn(String.format("could not find node with number %s", nodenr));
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
