package org.mmbase.applications.vprowizards.spring.action;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import nl.vpro.redactie.ResultContainer;

import org.apache.commons.lang.StringUtils;
import org.mmbase.applications.vprowizards.spring.cache.CacheFlushHint;
import org.mmbase.applications.vprowizards.spring.util.PathBuilder;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NotFoundException;
import org.mmbase.bridge.Query;
import org.mmbase.bridge.Relation;
import org.mmbase.bridge.RelationManager;
import org.mmbase.bridge.Transaction;
import org.mmbase.bridge.util.Queries;
import org.mmbase.security.Action;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * this action creates a relation. source and destiation nodes can be set either
 * as nodennr or as nodenr reference, which meens they are looked up in the id
 * map of the {@link ResultContainer}.
 * 
 * @author Ernst Bunders
 * 
 */
public class CreateRelationAction extends AbstractNodeAction {
	
	public static final String SORT_POSITION_BEGIN = "begin";

	public static final String SORT_POSITION_END = "end";

	private static final Logger log = Logging.getLoggerInstance(CreateRelationAction.class);

	private Node sourceNode = null;
	private Node destinationNode = null;
	
	private String sourceNodeNumber;
	private String destinationNodeNumber;

	private String sourceNodeRef;
	private String destinationNodeRef;

	private String role;

	private String sortPosition = CreateRelationAction.SORT_POSITION_END;
	private String sortField ;
	
	@Override
	protected void createCacheFlushHints() {
		CacheFlushHint hint = new CacheFlushHint(CacheFlushHint.TYPE_RELATION);
		hint.setRelationNumber(getNode().getNumber());
		hint.setSourceNodeNumber(sourceNode.getNumber());
		hint.setDestinationNodeNumber(destinationNode.getNumber());
		addCachFlushHint(hint);
		
	}
	@Override
	protected Node createNode(Transaction transaction, Map<String,Node> idMap, HttpServletRequest request) {
		if(StringUtils.isBlank(role)){
			addGlobalError("error.property.required", new String[]{"role", this.getClass().getName()});
			addGlobalError("error.create.relation");
			return null;
		}else{
			RelationManager relationManager = transaction.getRelationManager(role);
			if(relationManager != null){
				if(resolveSourceAndDestination(transaction, idMap )){
					//create the relation node.
					if(checkAuthorization(relationManager) && checkTypeRel(relationManager)){
						Relation rel = relationManager.createRelation(sourceNode, destinationNode);
						return rel;
					}else{
						return null;
					}
				}
			}else{
				addGlobalError("error.illegal.relationmanager", new String[]{"role"});
				addGlobalError("error.create.relation");
				return null;
			}
		}
		return null;
	}
	
	/**
	 * When sortField is set, and it is a valid field, the sort position is calculated and set on the new relation.
	 * @see org.mmbase.applications.vprowizards.spring.action.AbstractNodeAction#processNode(org.mmbase.bridge.Transaction)
	 */
	@Override
	protected void processNode(Transaction transaction) {
		if(!StringUtils.isBlank(sortField)){
			//check if the sortField exists
			try{
				getNode().getNodeManager().getField(sortField);
				
				//set the position if we need to do that
				Integer sortPositionValue = resolvePosition(transaction);
				if(sortPositionValue != null){
					getNode().setIntValue(sortField, sortPositionValue);
				}
			}catch(NotFoundException e){
				addGlobalError("error.field.unknown", 
						new String[]{sortField, this.getClass().getName(), getNode().getNodeManager().getName()});
			}
		}
	}
	
	/**
	 * Check if a relation is possible from the given source to the given destination with
	 * the given relation manager.
	 * @param relationManager
	 * @return
	 */
	private boolean checkTypeRel(RelationManager relationManager) {
		String constraints = String.format(
				"snumber=%s AND dnumber=%s and rnumber=%s", 
				"" + sourceNode.getNodeManager().getNumber(), 
				"" + destinationNode.getNodeManager().getNumber(),
				"" + relationManager.getNumber());
		NodeList nl = relationManager.getList(constraints, null, null);
		if(nl.size() == 0){
			addGlobalError(
					"error.create.relation.typerel", 
					new String[]{sourceNode.getNodeManager().getName(), destinationNode.getNodeManager().getName(), role});
			addGlobalError("error.create.relation");
			return false;
		}
		return true;
	}
	/**
	 * can the current owner create a node of this type?
	 * set glabal error when fale.
	 * @param relationManager
	 * @return true when allowed.
	 */
	private boolean checkAuthorization(RelationManager relationManager) {
		boolean mayCreate = relationManager.mayCreateNode();
		if(!mayCreate){
			addGlobalError("error.create.authorization", new String[]{relationManager.getName()});
		}
		return mayCreate;
	}
	/**
	 * (try to) resolve the source and destination nodes for this relation.
	 * Set global errors when fail. 
	 * @return true when source and destination nodes are found
	 */
	private boolean resolveSourceAndDestination(Transaction transaction, Map<String,Node> idMap) {
		sourceNode = resolveNode("error.create.relation.nosource", sourceNodeRef, sourceNodeNumber, idMap, transaction); 
		destinationNode = resolveNode("error.create.relation.nodestination", destinationNodeRef, destinationNodeNumber, idMap, transaction);
		return (sourceNode != null && destinationNode != null);
	}
	
	/**
	 * Try to resolve a node, by trying either the nodenr or node ref. If there are problems, relevant global errors are created. 
	 * @param refNotFoundErrorKey
	 * @param nodeRef
	 * @param nodenr
	 * @param idMap
	 * @param transaction
	 * @return the node
	 */
	private final Node resolveNode(String refNotFoundErrorKey, String nodeRef, String nodenr, Map<String,Node>idMap, Transaction transaction){
		Node result = null;
		if(nodenr == null){
			if(nodeRef == null){
				addGlobalError(refNotFoundErrorKey);
				addGlobalError("error.create.relation");
			}else{
				if(idMap.get(nodeRef) == null){
					addGlobalError("error.node.notfound.idmap", new String[]{nodeRef});
					addGlobalError("error.create.relation");
				}else{
					result = idMap.get(nodeRef);
				}
			}
		}else{
			//try to load the node
			try{
				result = transaction.getNode(nodenr);
			}catch(NotFoundException e){
				addGlobalError("error.node.notfound", new String[]{nodenr});
				addGlobalError("error.create.relation");
			}
		}
		return result;
	}
	
	/**
	 * Derive the position field value, if sortField is set. If something goes wrong a global error is set. The sort
	 * position value will be, depending on the sortPostion field, either the lowest current sort position value minus
	 * 1, or the highest current sort position value plus one.
	 * 
	 * @param sortField
	 * @param sortPosition
	 * @param source
	 * @param destination
	 * @param role
	 * @return the new sort position or null, if something went wrong.
	 */
	private Integer resolvePosition(Transaction transaction) {
        if (StringUtils.isBlank(sortField)) {
            return null;
        }
        int position = 1;

        Query q = null;
        try {
            // find the lowest or highest relation number
        	
        	//it is unlikely that the path matches duplicate builder names here, but who knows? 
        	PathBuilder pathBuilder = new PathBuilder(new String[]{
        			sourceNode.getNodeManager().getName(),
        			role,
        			destinationNode.getNodeManager().getName()
        	});
            q = Queries.createQuery(
            		transaction, 
            		sourceNode.getNumber() + "", 
            		pathBuilder.getPath(), 
            		pathBuilder.getStep(1) + "." + sortField, 
            		null, 
            		pathBuilder.getStep(1) + "." + sortField, 
            		(sortPosition.equals("begin") ? "up" : "down"), 
            		null, 
            		false);
            q.setMaxNumber(1);
            NodeList nl = transaction.getList(q);
            if (nl.size() > 0) {
                position = nl.getNode(0).getIntValue(role + "." + sortField);
                position = (sortPosition.equals("begin") ? position - 1 : position + 1);
            }

            return new Integer(position);
        } catch (RuntimeException e) {
            addGlobalError("error.unexpected", new String[]{e.getMessage()});
            log.error("something went wrong running a query to find out the position of a new relation. query: ["+q.toString()+"]", e);
        }
        return null;
    }

}
