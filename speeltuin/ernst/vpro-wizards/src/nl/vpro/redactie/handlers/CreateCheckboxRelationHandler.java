package nl.vpro.redactie.handlers;

import java.util.Map;
import java.util.Map.Entry;

import nl.vpro.redactie.FieldError;
import nl.vpro.redactie.ResultContainer;
import nl.vpro.redactie.actions.CreateCheckboxRelationAction;
import nl.vpro.redactie.cache.CacheFlushHint;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logging;


/**
 * is the same as CreateRelationHandler.
 * @author ebunders
 *
 */
public class CreateCheckboxRelationHandler extends Handler<CreateCheckboxRelationAction> {
	private static org.mmbase.util.logging.Logger log = Logging.getLoggerInstance(CreateCheckboxRelationHandler.class);

	public CreateCheckboxRelationHandler(Transaction transactionalCloud, ResultContainer resultContainer, Map<String, Node> idMap) {
		super(transactionalCloud, resultContainer, idMap);
	}

	void process() {
        if (isValid()) {
            if (log.isDebugEnabled()) {
                log.debug("CreateCheckboxRelationHandler " + action);
            }
            if (!action.getRelate()) {
                log.debug("No need to create relation");
                return;
            }
            Node source = getSource();
            if (source == null) {
                resultContainer.getErrors().add(
                        new FieldError("source,referSource", "Kon geen source node vinden met waardes: source=" + action.getSource()
                                + " en referSource=" + action.getReferSource()));
                return;
            }
            Node destination = getDestination();
            if (destination == null) {
                resultContainer.getErrors().add(
                        new FieldError("destination,referDestination", "Kon geen destination node vinden met waardes: source="
                                + action.getDestination() + " en referDestination=" + action.getReferDestination()));
                return;
            }
            String role = getRole();
            createRelation(source, destination, role);
        }
	}

    /**
     * checks for the fields that are needed to allow the creation of a relation
     * @return
     */
    private boolean isValid() {
        boolean result = true;
       if(action.getDestination().isEmpty() && action.getReferDestination().isEmpty()){
           resultContainer.getErrors().add(new FieldError("destination", "Als je een relatie maakt moet je of destination of referDestination opgeven"));
           result = false;
       }

       if(action.getSource().isEmpty() && action.getReferSource().isEmpty()){
           resultContainer.getErrors().add(new FieldError("source", "Als je een relatie maakt moet je of source of referSource opgeven"));
           result = false;
       }
        return result;
    }

	/**
     * find the sourcenode for the relation
	 * @return the source node or null if there is no source node yet.
	 */
	Node getSource() {
		Node source = null;
		if(action.getSource().isEmpty()&&action.getReferSource().isEmpty()) {
			log.error("Must specify a 'source' or 'referSource' value "+action);
		}
		if(action.getSource().isEmpty()) {
			String sourceNumber = action.getReferSource().toArray()[0].toString();
			source = idMap.get(sourceNumber);
		} else {
			String sourceNumber = action.getSource().toArray()[0].toString();
			source = transactionalCloud.getNode(sourceNumber);
		}
		if(log.isDebugEnabled()){
			log.debug("source "+source);
		}
		return source;
	}

	/**
     * find the destination node for the relation
	 * @return the destination node or null if there is no destination node yet.
	 */
	Node getDestination() {
		Node destination = null;
		if(action.getDestination().isEmpty()&&action.getReferDestination().isEmpty()) {
			log.error("Must specify a 'destination' or 'referDestination' value "+action);
		}
		if(action.getDestination().isEmpty()) {
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

		if(log.isDebugEnabled()){
			log.debug("destination "+destination);
		}

		return destination;
	}

	/**
     * find the role for the relation we want to create
	 * @return
	 */
	String getRole() {
		String role = null;
		if(action.getRole().isEmpty()) {
			if(log.isDebugEnabled()){
				log.debug("using role related for "+action);
			}
			role = "related"; // use default the related relation.
		} else {
			role = action.getRole().toArray()[0].toString();
		}

		if(log.isDebugEnabled()){
			log.debug("role "+role);
		}

		return role;
	}

	/**
     * create the relation node
	 * @param source
	 * @param destination
	 * @param role
	 */
	void createRelation(Node source, Node destination, String role) {
		RelationManager relationManager = transactionalCloud.getRelationManager(role);
		Relation relation = relationManager.createRelation(source,destination);

		// Zet ook de velden van de relatie, b.v. pos voor posrel.
		for (Entry<String,String> entry : action.getFields().entrySet()) {
			try {
				relation.setStringValue(entry.getKey(),entry.getValue());
			} catch (Exception e) {
				FieldError fielderror = new FieldError(entry.getKey(),e.toString());
				log.warn(fielderror);
				resultContainer.getErrors().add(fielderror);
			}
		}

        //create a cache flush hint
        log.debug("createing new cache flush hint type 'relation'");
        CacheFlushHint hint = new CacheFlushHint(CacheFlushHint.TYPE_RELATION);
        hint.setRelationNumber(relation.getNumber());
        hint.setSourceNodeNumber(source.getNumber());
        hint.setDestinationNodeNumber(destination.getNumber());
	}
}