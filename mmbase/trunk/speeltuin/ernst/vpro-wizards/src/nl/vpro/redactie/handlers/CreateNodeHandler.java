package nl.vpro.redactie.handlers;

import java.text.ParseException;
import java.util.Map;
import java.util.Map.Entry;

import nl.vpro.redactie.FieldError;
import nl.vpro.redactie.ResultContainer;
import nl.vpro.redactie.actions.CreateNodeAction;
import nl.vpro.redactie.actions.DateTime;
import nl.vpro.redactie.cache.CacheFlushHint;

import org.mmbase.bridge.Node;
import org.mmbase.bridge.Transaction;
import org.mmbase.util.logging.Logging;

/**
 * this handler creates a node of given type. first all the fields are set on it, than all the datefields. currently one file can be
 * uploaded for each create node action, so nodes with more file fields than one have a problem. for each exception thrown by the setting of
 * a value on a field of the node, an error message is mapped to the field name in the resultcontainer.Errors map. if an id is given than
 * the newly created node is mapped to it in the idMap that is accessible for subsequent actions. they usualy allow pointing to this map
 * with attributes like 'sourceRef=[id]'
 *
 * @author ebunders
 *
 */
public class CreateNodeHandler extends Handler<CreateNodeAction> {
    private static org.mmbase.util.logging.Logger log = Logging.getLoggerInstance(CreateNodeHandler.class);

    public CreateNodeHandler(Transaction transactionalCloud, ResultContainer resultContainer, Map<String, Node> idMap) {
        super(transactionalCloud, resultContainer, idMap);
    }

    void process() {
        Node node;
        String nodetype = getType();
        if(nodetype == null){
            resultContainer.getErrors().add(new FieldError("type","voor createNodeActions moet je een node type opgeven"));
        } else {
            node = transactionalCloud.getNodeManager(nodetype).createNode();

            for (Entry<String, String> entry : action.getFields().entrySet()) {
                try {
                    node.setStringValue(entry.getKey(), entry.getValue());
                } catch (Exception e) {
                    FieldError fielderror = new FieldError(entry.getKey(), e.toString());
                    log.warn(fielderror);
                    resultContainer.getErrors().add(fielderror);
                }
            }

            for (Entry<String, DateTime> entry : action.getDateFields().entrySet()) {
                log.debug("field: " + entry.getKey() + ", date: " + entry.getValue().getDate() + ", time:" + entry.getValue().getTime());
                try {
                    node.setStringValue(entry.getKey(), entry.getValue().getDateInSeconds());
                } catch (ParseException e) {
                    FieldError fielderror = new FieldError(entry.getKey(), entry.getValue().getDate() + " of " + entry.getValue().getTime()
                            + " is ongeldig");
                    log.warn(fielderror);
                    resultContainer.getErrors().add(fielderror);
                }
            }

            // set the file field
            setHandlerField(resultContainer, node, false);

            // create cache flush hint
            log.debug("creating a cache flush hint of type 'node'");
            CacheFlushHint hint = new CacheFlushHint(CacheFlushHint.TYPE_NODE);
            hint.setNodeNumber(node.getNumber());
            resultContainer.addCacheFlushHint(hint);

            // add the node to the id map
            setId(node);
        }
    }
}