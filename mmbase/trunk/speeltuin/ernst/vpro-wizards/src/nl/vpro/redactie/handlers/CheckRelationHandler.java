package nl.vpro.redactie.handlers;

import java.util.Map;

import nl.vpro.redactie.ResultContainer;
import nl.vpro.redactie.actions.CheckRelationAction;
import nl.vpro.redactie.cache.CacheFlushHint;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logging;

/**
 *creates a new relation of given type. If olddestination is set, than the old relation node is deleted.
 * the following fields can be set (* = mandatory).
 * With this action it is not possible to set fields on the created relation!
 * @author ebunders
 */
public class CheckRelationHandler extends Handler<CheckRelationAction> {
	private static org.mmbase.util.logging.Logger log = Logging.getLoggerInstance(CheckRelationHandler.class);

	public CheckRelationHandler(Transaction transactionalCloud, ResultContainer resultContainer, Map<String, Node> idMap) {
		super(transactionalCloud, resultContainer, idMap);
	}

	void process() {
		if(log.isDebugEnabled()){
			log.debug(String.format("processing action %s",action.toString()));
		}
		String olddestnumber = action.getFields().get("olddestination");
		String newdestnumber = action.getFields().get("newdestination");
		String relationtype = action.getFields().get("relationtype");
		String sourceNumber = action.getFields().get("source");

		if(olddestnumber!=null && olddestnumber.equals(newdestnumber)) {
			if(log.isDebugEnabled()){
				log.debug("Relation is not changed ");
			}
			return;
		}

        if(relationtype == null){
            log.error("no relation type given. can not create relation");
            return;
        }

        if(relationtype == null){
            log.error("no source number given. can not create relation");
            return;
        }

		if(log.isDebugEnabled()){
			log.debug("Updating relations");
		}

        Node source = transactionalCloud.getNode(sourceNumber);
		Node newdestination = transactionalCloud.getNode(newdestnumber);

		// Als er geen olddestnumber is dan is er ook geen relatie die verwijderd moet worden.
		if(olddestnumber!=null) {
			String sourceType = source.getNodeManager().getName();
			Node olddestination = transactionalCloud.getNode(action.getFields().get("olddestination"));
			String destinationType = olddestination.getNodeManager().getName();

			NodeList nodes = transactionalCloud.getList(""+source.getNumber(),sourceType+",insrel,"+destinationType,"insrel.number,"+destinationType+".number",destinationType+".number="+olddestination.getNumber(),null,null,null,false);
			NodeIterator iterator = nodes.nodeIterator();
			while(iterator.hasNext()) {
				Node node = (Node)iterator.next();

				String relationnumber = node.getStringValue("insrel.number");
				Relation relation = transactionalCloud.getRelation(relationnumber);
				if(log.isDebugEnabled()){
					log.debug("Deleting node "+node);
				}
				relation.delete();

				// flush old pages
//				OscacheFlush.flushRelation(resultContainer.getRequest(), relation);
                CacheFlushHint hint = new CacheFlushHint(CacheFlushHint.TYPE_RELATION);
                hint.setProperty("relation", relation);
                resultContainer.addCacheFlushHint(hint);
			}
		}

		// creating relation
		Relation relation = source.createRelation(newdestination,transactionalCloud.getRelationManager(relationtype));

		// flush pagina's
        log.debug("createing new cache flush hint type 'relation'");
        CacheFlushHint hint = new CacheFlushHint(CacheFlushHint.TYPE_RELATION);
        hint.setRelationNumber(relation.getNumber());
        hint.setSourceNodeNumber(source.getNumber());
        hint.setDestinationNodeNumber(newdestination.getNumber());

        resultContainer.addCacheFlushHint(hint);

		log.debug("Creating node "+relation);
	}
}