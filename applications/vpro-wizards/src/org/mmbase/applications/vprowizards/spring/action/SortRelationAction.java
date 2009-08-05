/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/ 
package org.mmbase.applications.vprowizards.spring.action;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.mmbase.applications.vprowizards.spring.cache.CacheFlushHint;
import org.mmbase.applications.vprowizards.spring.util.PathBuilder;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * FIXME: voor deze actie is het niet direct nodig om een source en destination node the hebben?
 * @author Ernst Bunders
 * 
 */
public class SortRelationAction extends AbstractRelationAction {

	public static final String DIRECTION_UP = "up";
	public static final String DIRECTION_DOWN = "down";
	
	public static final Logger log = Logging.getLoggerInstance(SortRelationAction.class);
	/**
	 * What direction to sort in?
	 */
	private String direction;
	private String sortField;
	
	private boolean setCacheflushHint = false;

	@Override
	protected void createCacheFlushHints() {
		if(setCacheflushHint){
			log.debug("create cache flush hint of type 'relation'");
			CacheFlushHint hint = new CacheFlushHint(
					CacheFlushHint.TYPE_RELATION);
			hint.setRelationNumber(getNode().getNumber());
			hint.setSourceNodeNumber(sourceNode.getNumber());
			hint.setDestinationNodeNumber(destinationNode.getNumber());
			addCachFlushHint(hint);
		}
	}

	/**
	 * Try to find the relation node that is of type given by the role field, 
	 * and exists between the source and destination node.
	 */
	@Override
	protected Node doCreateNode(Transaction transaction, Map<String, Node> idMap, HttpServletRequest request) {
		//preconditions
		if (!DIRECTION_UP.equals(direction) && !DIRECTION_DOWN.equals(direction)){
			addGlobalError("error.property.illegal.sortdirection", new String[]{direction, this.getClass().getName(), DIRECTION_UP, DIRECTION_DOWN});
		}
		if(!relationManager.hasField(sortField)){
			addGlobalError("error.property.illegal.sortfield", new String[]{sortField, this.getClass().getName(), role});
		}
		log.debug(String.format("preconditions met for action %s. errors found: %s", this.getClass().getName(), hasErrors()));
		
		if(!hasErrors()){
			PathBuilder pathBuilder = createPathBuilder();
			String constraint = String.format("%s.number = %s AND %s.number = %s", 
					pathBuilder.getStep(0),
					sourceNode.getNumber(),
					pathBuilder.getStep(2),
					destinationNode.getNumber());
			Query q = Queries.createQuery(transaction, 
					null, 
					pathBuilder.getPath(), 
					pathBuilder.getStep(1) + ".number", /*the role step*/ 
					constraint, 
					null, 
					null, 
					null, 
					true);
			NodeList nl = transaction.getList(q);
			if(nl.size() == 0){
				addGlobalError("error.relation.notfound", 
						new String[]{role, "" + sourceNode.getNumber(), "" + destinationNode.getNumber()});
				return null;
			}
			Node result = transaction.getNode(nl.getNode(0).getStringValue(pathBuilder.getStep(1) + ".number"));
			log.debug("node found for SortRelationAction: " + result);
			return result; 
			
		}
		return null;
	}
	
	
	

	/**
	 * Here we do the actual sorting.
	 */
	@Override
	protected void processNode(Transaction transaction) {
		//first check  all the relations of type 'role' between source node and destination type.
		//when double values occur, resort them first them.
		checkSortfieldValues(transaction);
		
		//now we now the sort values are ok, we can resort the given relation.
		//now do the sorting
		NodeList nl = getList(transaction);
		
		PathBuilder pb = createPathBuilder();
		
		boolean relationFound = false;
		for (int i = 0; i < nl.size(); i++) {
			//String thisDestinationNodeNumber = nl.getNode(i).getNodeValue(pb.getStep(2)).getStringValue("number");
			
//			if (log.isDebugEnabled()) {
//				log.debug(i + ": number: " + thisNodeNumber + "  posrel: "
//						+ nl.getNode(i).getStringValue(action.getRole() + ".pos"));
//			}
//			log.debug(String.format("testing [%s] with [%s]",nl.getNode(i).getNodeValue(pb.getStep(1)).getNumber(), getNode().getNumber()));
			if (getNode().getNumber() == nl.getNode(i).getNodeValue(pb.getStep(1)).getNumber()) {
				relationFound = true;
				log.debug("relation node found at list position "+i+" with sort field value: "+nl.getNode(i).getIntValue(pb.getStep(1) + "." + sortField));
				// node found. now find the relation node node that we are going to
				// have to swap places with
				Node nodeToSwapWith = null;
				int nodeToSwapIndex = -1;
				
//				Node relationNode = nl.getNode(i).getNodeValue(pb.getStep(1));

				if (direction.equals(
						DIRECTION_UP)) {
					if (i > 0) {
						log.debug("direction is up and list index is "+i);
						nodeToSwapWith = nl.getNode(i - 1).getNodeValue(pb.getStep(1));
						nodeToSwapIndex = i-1;
//						log.debug("step to extract from virtual node: "+pb.getStep(1));
//						log.debug("node to swap(nl): "+nl.getNode(i - 1));
//						log.debug("node to swap: "+nodeToSwapWith);
					} else {
						log.error("you want to go up, but you are at the top of the list. Abort!");
						return;
					}
				} else if (direction.equals(
						DIRECTION_DOWN)) {
					if (i < ( nl.size() - 1 )) {
						log.debug("direction is down and list index is "+i+" and list size = "+nl.size());
						nodeToSwapWith = nl.getNode(i + 1).getNodeValue(pb.getStep(1));
						nodeToSwapIndex = i + 1;
					} else {
						log.error("you want to go down, but you are at the end of the list. Abort!");
						return;
					}
				}else{
					log.fatal(String.format("attribute 'direction has illegal value [%s], while this value should have been checked. Bug!", direction));
				}

				// this should not happen
				if (nodeToSwapIndex == -1) {
					log.fatal("Next or preveaus relation node to swap position with is not found. this is a BUG!");
					return;
				}
				
				nodeToSwapWith = transaction.getRelation(nl.getNode(nodeToSwapIndex).getStringValue(pb.getStep(1) + ".number"));
				log.debug(String.format(
						"Swap relation found at list position %s with sort field value %s",
						nodeToSwapIndex, 
						nodeToSwapWith.getIntValue(sortField)));
				
				log.debug(String.format("node number is %s and swap node number is %s", getNode().getNumber(), nodeToSwapWith.getNumber()));
				log.debug(String.format("node pos is %s and swap pos is %s", getNode().getStringValue("pos"), nodeToSwapWith.getStringValue("pos")));

				// now let the two nodes swap position
				int p1 = getNode().getIntValue(sortField);
				int p2 = nodeToSwapWith.getIntValue(sortField);

				log.debug("swapping the position values");
				getNode().setIntValue(sortField, p2);
				nodeToSwapWith.setIntValue(sortField, p1);
				log.debug(String.format("after: node pos is %s and swap pos is %s", getNode().getStringValue("pos"), nodeToSwapWith.getStringValue("pos")));
				
				// a cacheflush hint needs to be created for this action.
				doSetCachflushHint();

				// we can stop iterating
				return;
			}
			
		}
		if(!relationFound){
			log.fatal(String.format(
					"relation node with number %s not found in nodelist [%s]. This is a BUG!", 
					getNode().getNumber(), nl));
		}
	}
		

	private void doSetCachflushHint() {
		setCacheflushHint = true;
		
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getSortField() {
		return sortField;
	}

	public void setSortField(String sortField) {
		this.sortField = sortField;
	}
	
	/**
	 * This method checks if there are no multiple occurrences of sort field  values.
	 * if this is the case  then the sort fields will be renumbered starting at the lowest value that was found.
	 * @param targetType
	 * @param containerType
	 * @param containerNode
	 */
	private final void checkSortfieldValues(Transaction transaction) {
		log.debug("Checking the sort fields of all relations between source node and destination type for double entries");
		NodeList nl = getList(transaction);
		Integer lowest = null;
		boolean resort = false;
		Set<Integer> values = new HashSet<Integer>(); 
		for (NodeIterator i = nl.nodeIterator(); i.hasNext();) {
			Node relationNode = i.nextNode().getNodeValue(role);
			int sortFieldValue = relationNode.getIntValue(sortField);
			
			//check lowest
			if(lowest == null || lowest > sortFieldValue){
				lowest = sortFieldValue;
			}
			
			//check for double value occurrence
			if(values.contains(sortFieldValue)){
				log.debug("Sort field value "+sortFieldValue+" is double. we must resort");
				resort = true;
				break;
			}else{
				values.add(sortFieldValue);
			}
		}
		
		//should we resort?
		if(resort){
			log.debug("Resorting code");
			for (NodeIterator i = nl.nodeIterator(); i.hasNext();) {
				Node relationNode = i.nextNode().getNodeValue(role);
				int oldValue = relationNode.getIntValue("pos");
				if(oldValue != lowest){
					log.debug("setting node "+relationNode.getNumber()+" to value "+lowest);
					relationNode.setIntValue(sortField, lowest);
				}else{
					log.debug("node "+relationNode.getNumber()+" has the right pos value: "+lowest+". skip it");
					
				}
				lowest ++;
			}
		}else{
			log.debug("No resorting nesecary");
		}
	}
	
	/**
	 * creates a list of of virtual nodes nodes that exist between the 
	 * source node and destination node type, with the given relation role.
	 * @param transaction
	 * @return a virtual node list containing the sortfield values.
	 */
	private final NodeList getList(Transaction transaction) {
		
		PathBuilder pb = createPathBuilder();
	
		// create a list of all the posrel nodes between the container and
		// the target.
		NodeList nl = transaction.getList(
				"" + sourceNode.getNumber(), 
				pb.getPath(), 
				pb.getStep(1) + "." + sortField, 
				null, 
				pb.getStep(1) + "." + sortField, 
				"up", 
				null,
				false);
	
		log.debug("nodes in list: " + nl.size());
		return nl;
	}

	private PathBuilder createPathBuilder() {
		PathBuilder pb = new PathBuilder(new String[]{
				sourceNode.getNodeManager().getName(),
				role,
				destinationNode.getNodeManager().getName()});
		return pb;
	}

}
