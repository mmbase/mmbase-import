package nl.vpro.redactie.handlers;

import java.text.ParseException;
import java.util.Map;
import java.util.Map.Entry;

import nl.vpro.redactie.FieldError;
import nl.vpro.redactie.ResultContainer;
import nl.vpro.redactie.actions.DateTime;
import nl.vpro.redactie.actions.UpdateNodeAction;
import nl.vpro.redactie.cache.CacheFlushHint;

import org.mmbase.bridge.Node;
import org.mmbase.bridge.Transaction;
import org.mmbase.util.logging.Logging;

/**
 * updates a given node.
Errors caused by setting these values result in a FieldError instance in the resultContainer.Errors map.
When id is set the node is set in the idMap
 * @author ebunders
 *
 */
public class UpdateNodeHandler extends Handler<UpdateNodeAction> {
    private static org.mmbase.util.logging.Logger log = Logging.getLoggerInstance(UpdateNodeHandler.class);

    public UpdateNodeHandler(Transaction transactionalCloud, ResultContainer resultContainer, Map<String, Node> idMap) {
        super(transactionalCloud, resultContainer, idMap);
    }

    void process() {
        if(log.isDebugEnabled()){
            log.debug("processing action "+action);
        }

        Node node = transactionalCloud.getNode(getNumber());
        boolean changed = false;

        for (Entry<String,String> entry : action.getFields().entrySet()) {
            try {
                node.setStringValue(entry.getKey(),entry.getValue());
                changed = true;
            } catch (Exception e) {
                FieldError fielderror = new FieldError(entry.getKey(),e.toString());
                log.warn(fielderror);
                resultContainer.getErrors().add(fielderror);
            }
        }
        for(Entry<String,DateTime> entry : action.getDateFields().entrySet()) {
            try {
                changed = true;
                node.setStringValue(entry.getKey(),entry.getValue().getDateInSeconds());
            } catch (ParseException e) {
                FieldError fielderror = new FieldError(entry.getKey(),entry.getValue().getDate()+" of "+entry.getValue().getTime()+" is ongeldig");
                log.warn(fielderror);
                resultContainer.getErrors().add(fielderror);
            }
        }

        //set the file field
        changed = setHandlerField(resultContainer, node, changed);

        if (changed) {
            CacheFlushHint hint = new CacheFlushHint(CacheFlushHint.TYPE_NODE);
            //TODO: dous it make sense to set the number of a new node?
            hint.setNodeNumber(node.getNumber());
            resultContainer.addCacheFlushHint(hint);
        }

        setId(node);
        // OscacheFlush.flushNodeNumber(resultContainer.getRequest(), "" + node.getNumber());
    }
}