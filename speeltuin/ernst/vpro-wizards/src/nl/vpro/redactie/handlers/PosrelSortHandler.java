package nl.vpro.redactie.handlers;

import java.util.*;

import nl.vpro.redactie.FieldError;
import nl.vpro.redactie.ResultContainer;
import nl.vpro.redactie.actions.PosrelSortAction;
import nl.vpro.redactie.cache.CacheFlushHint;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

public class PosrelSortHandler extends Handler<PosrelSortAction> {

	private static final Logger log = Logging
			.getLoggerInstance(PosrelSortHandler.class);

	@SuppressWarnings("unchecked")
	public PosrelSortHandler(Transaction transactionalCloud,
			ResultContainer resultContainer, Map idMap) {
		super(transactionalCloud, resultContainer, idMap);
	}
	
	@Override
	void process() {
		if (!action.directionValid()) {
			log.error("direction invalid: " + action.getDirection());
			return;
		}

		if (action.getContainerNode() == null
				|| "".equals(action.getContainerNode())) {
			log.error("containerNode not defined");
			return;
		}

		if (action.getNumber() == null || "".equals(action.getNumber())) {
			log.error("destination node  not defined");
			return;
		}

		log.debug("** preconditions met");

		// get the nodes
		Node containerNode, targetNode;
		try {
			containerNode = transactionalCloud.getNode(action.getContainerNode());
			targetNode = transactionalCloud.getNode(getNumber());

			String containerType = containerNode.getNodeManager().getName();
			String targetType = targetNode.getNodeManager().getName();
			
			//now make sure the pos fields are valid.
			checkPos(targetType, containerType, containerNode);
			
			//now do the sorting
			NodeList nl = getList(targetType, containerType, containerNode);
			

			for (int i = 0; i < nl.size(); i++) {
				String thisNodeNumber = nl.getNode(i).getNodeValue(targetType).getStringValue("number");
				
				if (log.isDebugEnabled()) {
					log.debug(i + ": number: " + thisNodeNumber + "  posrel: "
							+ nl.getNode(i).getStringValue(action.getRole() + ".pos"));
				}
				if (thisNodeNumber.equals(""+targetNode.getNumber())) {
					log.debug("posrel node found at position "+i+" with pos value: "+nl.getNode(i).getIntValue(action.getRole() + ".pos"));
					// node found. now find the posrel node that we are going to
					// have to swap places with
					Node otherNode = null;
					Node posrelNode = nl.getNode(i).getNodeValue(action.getRole());

					if (action.getDirection().equals(
							PosrelSortAction.DIRECTION_UP)) {
						if (i > 0) {
							otherNode = nl.getNode(i - 1).getNodeValue(action.getRole());
						} else {
							log.error("you want to go up, but you are at the top of the list. Abort!");
							return;
						}
					} else if (action.getDirection().equals(
							PosrelSortAction.DIRECTION_DOWN)) {
						if (i < nl.size()) {
							otherNode = nl.getNode(i + 1).getNodeValue(action.getRole());
						} else {
							log.error("you want to go down, but you are at the end of the list. Abort!");
							return;
						}
					}

					// this should not happen
					if (otherNode == null) {
						log.error("other node is null! can not beeee");
						return;
					}

					// now let the two nodes swap position
					int p1 = posrelNode.getIntValue("pos");
					int p2 = otherNode.getIntValue("pos");
					if (p1 == p2) {
						log.error("can not sort! both posrel nodes have the same position");
						p1 = (action.getDirection() == PosrelSortAction.DIRECTION_UP ? p2 - 1 : p2 + 1);
					}

					posrelNode.setIntValue("pos", p2);
					otherNode.setIntValue("pos", p1);

					// create cache flush hint
					log.debug("create cache flush hint of type 'relation'");
					CacheFlushHint hint = new CacheFlushHint(
							CacheFlushHint.TYPE_RELATION);
					hint.setRelationNumber(posrelNode.getNumber());
					hint.setSourceNodeNumber(containerNode.getNumber());
					hint.setDestinationNodeNumber(targetNode.getNumber());
					resultContainer.addCacheFlushHint(hint);

					// we can stop iterating
					return;
				}
			}
		} catch (NotFoundException e) {
			resultContainer.getErrors().add(new FieldError("node","Container Node or Target Node not found: "+e.getMessage()));
			log.error("something went wrong creating one of the nodes: "
					+ e.getMessage());
		}
	}

	/**
	 * This method checks if there are no multiple occurrences of posrel values.
	 * if this is the case  then the posrels will be renumbered starting at the lowest value that was found.
	 * @param targetType
	 * @param containerType
	 * @param containerNode
	 */
	private void checkPos(String targetType, String containerType, Node containerNode) {
		log.debug("** starting posrel resort checking");
		NodeList nl = getList(targetType, containerType,  containerNode);
		Integer lowest = null;
		boolean resort = false;
		Set<Integer> values = new HashSet<Integer>(); 
		for (NodeIterator i = nl.nodeIterator(); i.hasNext();) {
			Node posrelNode = i.nextNode().getNodeValue(action.getRole());
			int posValue = posrelNode.getIntValue("pos");
			
			//check lowest
			if(lowest == null || lowest > posValue){
				lowest = posValue;
			}
			
			//check for double value occurence
			if(values.contains(posValue)){
				log.debug("** posrel value "+posValue+" is double. we must resort");
				resort = true;
				break;
			}else{
				values.add(posValue);
			}
		}
		
		//should we resort?
		if(resort){
			log.debug("** resorting code");
			for (NodeIterator i = nl.nodeIterator(); i.hasNext();) {
				Node posrelNode = i.nextNode().getNodeValue(action.getRole());
				int oldValue = posrelNode.getIntValue("pos");
				if(oldValue != lowest){
					log.debug("setting node "+posrelNode.getNumber()+" to pos "+lowest);
					posrelNode.setIntValue("pos", lowest);
					posrelNode.commit();
				}else{
					log.debug("node "+posrelNode.getNumber()+" has the right pos value: "+lowest+". skip it");
					
				}
				lowest ++;
			}
		}else{
			log.debug("** no resorting nesecary");
		}
	}

	/**
	 * creates a list of of virtual posrel nodes that exist between the container node and nodes of 
	 * the given target type 
	 * @param targetType
	 * @param containerType
	 * @param containerNode
	 * @return
	 */
	private NodeList getList(String targetType, String containerType,  Node containerNode) {
		
		// it can happen that container and target share the same type.
		// handle that eventuality
		if (targetType.equals(containerType)) {
			targetType = targetType + "1";
		}
	
		// create a list of all the posrel nodes between the container and
		// the target.
		NodeList nl = transactionalCloud.getList(
				"" + containerNode.getNumber(), 
				containerType + "," + action.getRole() + "," + targetType, 
				action.getRole() + ".pos", 
				null, 
				action.getRole() + ".pos", 
				"up", 
				null,
				false);
	
		log.debug("nodes in list: " + nl.size());
		return nl;
	}
}
