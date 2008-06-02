package nl.vpro.redactie.handlers;

import java.util.Map;

import nl.vpro.redactie.FieldError;
import nl.vpro.redactie.ResultContainer;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logging;

/**
 *
 * @author ebunders
 */
public class UpdateCheckboxRelationHandler extends CreateCheckboxRelationHandler {
    private static org.mmbase.util.logging.Logger log = Logging.getLoggerInstance(UpdateCheckboxRelationHandler.class);

    public UpdateCheckboxRelationHandler(Transaction transactionalCloud, ResultContainer resultContainer, Map<String, Node> idMap) {
        super(transactionalCloud, resultContainer, idMap);
    }

    void process() {

        if (log.isDebugEnabled()) {
            log.debug("UpdateCheckboxRelationHandler " + action);
        }

        if (isValid()) {
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
            boolean existsRelation = existsRelation(source, destination, role);
            if (action.getRelate()) {
                // 1) Als relatie al bestaat doe niets.
                // 2) Als relatie nog niet bestaat leg hem aan.
                if (!existsRelation) {
                    createRelation(source, destination, role);
                }
            } else {
                // 1) Als relatie al bestaat verwijder hem.
                // 2) Als relatie nog niet bestaat doe niets.
                if (existsRelation) {
                    deleteRelation(source, destination, role);
                }
            }
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
                    new FieldError("destination", "Als je een relatie maakt moet je of destination of referDestination opgeven"));
            result = false;
        }

        if (action.getSource().isEmpty() && action.getReferSource().isEmpty()) {
            resultContainer.getErrors().add(new FieldError("source", "Als je een relatie maakt moet je of source of referSource opgeven"));
            result = false;
        }
        return result;
    }

    boolean existsRelation(Node source, Node destination, String role) {
        //let's assume that if a node is not committed yet (new in the transaction) it's number is negative.
        if(source.getNumber() < 0 || destination.getNumber() < 1){
            return false;
        }
        String path = source.getNodeManager().getName() + "," + role + "," + destination.getNodeManager().getName();
        String constriantField = destination.getNodeManager().getName() + ".number";
        NodeList nodeList = transactionalCloud.getList(source.getNumber()+"", path, null,  constriantField + "=" +destination.getNumber(), null, null, null, false);
        if (log.isDebugEnabled()) {
            log.debug(String.format("size %d", nodeList.size()));
        }

        return !nodeList.isEmpty();
    }

    void deleteRelation(Node source, Node destination, String role) {
        NodeList nodes = transactionalCloud.getList("" + source.getNumber(), source.getNodeManager().getName() + "," + role + ","
                + destination.getNodeManager().getName(), null, destination.getNodeManager().getName() + ".number="
                + destination.getNumber(), null, null, null, false);
        Node node = nodes.getNode(0);
        String relationNumber = node.getStringValue(role + ".number");

        if (log.isDebugEnabled()) {
            log.debug("deleting relation " + relationNumber);
        }

        Node relation = transactionalCloud.getNode(relationNumber);
        relation.delete();
    }
}