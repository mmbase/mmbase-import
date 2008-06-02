package nl.vpro.redactie.handlers;

import java.util.Map;

import nl.vpro.redactie.ResultContainer;
import nl.vpro.redactie.actions.DeleteNodeAction;
import nl.vpro.redactie.cache.CacheFlushHint;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * simple handler that deletes a node. When the node's nodemanager extends
 * the 'insrel' nodemanager then a 'relation' type cache flush hint is created, and
 * otherwise an 'node' type cache flush hint.
 *
 * @author ebunders
 *
 */
public class DeleteNodeHandler extends Handler<DeleteNodeAction> {
    private static Logger log = Logging.getLoggerInstance(DeleteNodeHandler.class);

    public DeleteNodeHandler(Transaction transactionalCloud, ResultContainer resultContainer, Map<String, Node> idMap) {
        super(transactionalCloud, resultContainer, idMap);
    }

    @Override
    void process() {
        try {
            Node node = transactionalCloud.getNode(getNumber());
            log.debug("deleting node :"+node.getNumber());
            node.delete(true);

            //lets find out if this node is a relation. based on that we create hour
            //cacheflush hint.
            NodeManager insrelManager = transactionalCloud.getNodeManager("insrel");
            NodeManagerList parents = insrelManager.getDescendants();
            boolean isRelation = false;
            for(NodeManagerIterator i = parents.nodeManagerIterator(); i.hasNext(); ){
                if(i.nextNodeManager().getName().equals(node.getNodeManager().getName())){
                    isRelation = true;
                }
            }

            CacheFlushHint cacheFlushHint;
            if(isRelation){
                cacheFlushHint = new CacheFlushHint(CacheFlushHint.TYPE_RELATION);
                log.debug("create cache flush hint of type 'relation'");
            }else{
                cacheFlushHint = new CacheFlushHint(CacheFlushHint.TYPE_NODE);
                log.debug("create cache flush hint of type 'node'");
            }

            cacheFlushHint.setNodeNumber(node.getNumber());
            resultContainer.addCacheFlushHint(cacheFlushHint);


        } catch (NotFoundException e) {
            log.error("node with number '" + getNumber() + "' can not be found, so it can not be deleted");
        }
    }

}
