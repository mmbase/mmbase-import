package nl.vpro.redactie.handlers;

import java.util.Map;
import java.util.Map.Entry;

import nl.vpro.redactie.FieldError;
import nl.vpro.redactie.ResultContainer;
import nl.vpro.redactie.actions.CreateRelationAction;
import nl.vpro.redactie.cache.CacheFlushHint;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.util.logging.Logging;

/**
 * creates a new relation from a given source node to a given destination node with a given role. source and destination
 * can be given as either a specific node number of as a reference to a previously mapped node. all the values in the
 * fields map will be set on the new node. date or file fields are not supported. Errors caused by setting these values
 * result in a FieldError instance in the resultContainer.Errors map. <br/> With sortfield you can set a sorting value
 * on the relation (think: posrel: pos). When the role is posrel this fields defaults to pos. for other relations
 * (specializations of posrel) you can set the field to use. the field must be of type number. With sortPosition you can
 * determine if the new relation should be added to the begin of the sorted list or the end.
 * 
 * @author ebunders
 * 
 */
public class CreateRelationHandler extends Handler<CreateRelationAction> {
	private static org.mmbase.util.logging.Logger log = Logging.getLoggerInstance(CreateRelationHandler.class);

	public CreateRelationHandler(Transaction transactionalCloud, ResultContainer resultContainer,
			Map<String, Node> idMap) {
		super(transactionalCloud, resultContainer, idMap);
	}

	void process() {
		if (isValid()) {

			if (log.isDebugEnabled()) {
				log.debug("CreateRelationHandler " + action);
			}

			Node source = getSource();
			if (source == null) {
				resultContainer.getErrors().add(
						new FieldError("source,referSource", "Kon geen source node vinden met waardes: source="
								+ action.getSource() + " en referSource=" + action.getReferSource()));
				return;
			}
			Node destination = getDestination();
			if (destination == null) {
				resultContainer.getErrors().add(
						new FieldError("destination,referDestination",
								"Kon geen destination node vinden met waardes: source=" + action.getDestination()
										+ " en referDestination=" + action.getReferDestination()));
				return;
			}
			String role = getRole();
			String sortField = (action.getSortField().isEmpty() ? "" : action.getSortField().toArray()[0].toString());
			if (sortField == null || "".equals(sortField)) {
				if (role.equals("posrel")) {
					sortField = "pos";
				} else {
					// perhaps the relation is of type posrel
					RelationManager rmmm = transactionalCloud.getRelationManager(role);
					int builderNumber = rmmm.getIntValue("builder");
					NodeManager nodeManager = (NodeManager) transactionalCloud.getNode(builderNumber);
					if (nodeManager.getName().equals("posrel")) {
						sortField = "pos";
					} else {

						// perhaps the relation manager is a descendant of posrel
						NodeManager rm = transactionalCloud.getNodeManager("posrel");
						if (rm != null) {
							NodeManagerList nml = rm.getDescendants();
							for (NodeManagerIterator i = nml.nodeManagerIterator(); i.hasNext();) {
								NodeManager nextNodeManager = i.nextNodeManager();
								String name = nextNodeManager.getName();
								if (name.equals(role)) {
									sortField = "pos";
									break;
								}
							}
						}
						// TODO: we could also check if the relation is using one of the Descendants as builder.
					}
				}
			}
			String sortPosition = (action.getSortPosition().isEmpty() ? "" : action.getSortPosition().toArray()[0]
					.toString());
			Integer position = getPosition(sortField, sortPosition, source, destination, role);
			createRelation(source, destination, role, sortField, position);
		}
	}

	/**
	 * checks for the fields that are needed to allow the creation of a relation
	 * 
	 * @return
	 */
	private boolean isValid() {
		boolean result = true;
		if (action.getDestination().isEmpty() && action.getReferDestination().isEmpty()) {
			resultContainer.getErrors().add(
					new FieldError("destination",
							"Als je een relatie maakt moet je of destination of referDestination opgeven"));
			result = false;
		}

		if (action.getSource().isEmpty() && action.getReferSource().isEmpty()) {
			resultContainer.getErrors().add(
					new FieldError("source", "Als je een relatie maakt moet je of source of referSource opgeven"));
			result = false;
		}
		return result;
	}

	/**
	 * @param sortField
	 * @param sortPosition
	 * @param source
	 * @param destination
	 * @param role
	 * @return a number for the new relation sort field or null if there is no sorting.
	 */
	Integer getPosition(String sortField, String sortPosition, Node source, Node destination, String role) {
		if (sortField == null || sortField.equals("")) {
			return null;
		}
		int position = 1;

		try {
			// find the lowest or highest relation number
			Query q = Queries.createQuery(transactionalCloud, source.getNumber() + "", source.getNodeManager()
					.getName()
					+ "," + role + "," + destination.getNodeManager().getName(), role + "." + sortField, null, role
					+ "." + sortField, (sortPosition.equals("begin") ? "up" : "down"), null, false);
			q.setMaxNumber(1);
			NodeList nl = transactionalCloud.getList(q);
			if (nl.size() > 0) {
				position = nl.getNode(0).getIntValue(role + "." + sortField);
				position = (sortPosition.equals("begin") ? position - 1 : position + 1);
			}

			return new Integer(position);
		} catch (RuntimeException e) {
			log.error(e);
		}
		return null;
	}

	/**
	 * find the sourcenode for the relation
	 * 
	 * @return
	 */
	Node getSource() {
		Node source = null;
		if (action.getSource().isEmpty() && action.getReferSource().isEmpty()) {
			log.error("Must specify a 'source' or 'referSource' value " + action);
		} else {

			if (action.getSource().isEmpty()) {
				String sourceNumber = action.getReferSource().toArray()[0].toString();
				source = idMap.get(sourceNumber);
			} else {
				String sourceNumber = action.getSource().toArray()[0].toString();
				source = transactionalCloud.getNode(sourceNumber);
			}
			if (log.isDebugEnabled()) {
				log.debug("source " + source);
			}
		}
		return source;
	}

	/**
	 * find the destination node for the relation
	 * 
	 * @return
	 */
	Node getDestination() {
		Node destination = null;
		if (action.getDestination().isEmpty() && action.getReferDestination().isEmpty()) {
			log.error("Must specify a 'destination' or 'referDestination' value " + action);
		}
		if (action.getDestination().isEmpty()) {
			try {
				String destinationNumber = action.getReferDestination().toArray()[0].toString();
				destination = idMap.get(destinationNumber);
			} catch (Exception e) {
				log.error("Cannot find referDestination in createrelation");
				log.error(e);
			}
		} else {
			String destinationNumber = action.getDestination().toArray()[0].toString();
			destination = transactionalCloud.getNode(destinationNumber);
		}

		if (log.isDebugEnabled()) {
			log.debug("destination " + destination);
		}

		return destination;
	}

	/**
	 * find the role for the relation we want to create
	 * 
	 * @return
	 */
	String getRole() {
		String role = null;
		if (action.getRole().isEmpty()) {
			if (log.isDebugEnabled()) {
				log.debug("using role related for " + action);
			}
			role = "related"; // use default the related relation.
		} else {
			role = action.getRole().toArray()[0].toString();
		}

		if (log.isDebugEnabled()) {
			log.debug("role " + role);
		}

		return role;
	}

	/**
	 * create the relation node
	 * 
	 * @param source
	 * @param destination
	 * @param role
	 * @param position
	 * @param sortField
	 */
	void createRelation(Node source, Node destination, String role, String sortField, Integer position) {
		RelationManager relationManager = transactionalCloud.getRelationManager(role);
		Relation relation = relationManager.createRelation(source, destination);

		// set the sort position
		if (position != null) {
			relation.setIntValue(sortField, position);
		}

		// Zet ook de velden van de relatie, b.v. pos voor posrel.
		for (Entry<String, String> entry : action.getFields().entrySet()) {
			try {
				relation.setStringValue(entry.getKey(), entry.getValue());
			} catch (Exception e) {
				FieldError fielderror = new FieldError(entry.getKey(), e.toString());
				log.warn(fielderror);
				resultContainer.getErrors().add(fielderror);
			}
		}

		// create a cache flush hint
		log.debug("createing new cache flush hint type 'relation'");
		CacheFlushHint hint = new CacheFlushHint(CacheFlushHint.TYPE_RELATION);
		hint.setRelationNumber(relation.getNumber());
		hint.setSourceNodeNumber(source.getNumber());
		hint.setDestinationNodeNumber(destination.getNumber());
	}
}