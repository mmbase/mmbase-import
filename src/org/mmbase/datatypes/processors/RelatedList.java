/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.processors;

import org.mmbase.bridge.*;
import org.mmbase.datatypes.*;
import org.mmbase.util.*;
import java.util.*;
import org.mmbase.util.logging.*;

/**
 * The set- and get- processors implemented in this file can be used to make a virtual field which
 * act as a list (or 'set')  field, but actually represent related nodes.
 *
 * In case just a selection of a limited number of other nodes is desired, this makes it easy to
 * just create a multiple select for it.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.9.1
 * @version $Id$
 */

public class RelatedList {

    private static final Logger log = Logging.getLoggerInstance(RelatedList.class);

    public abstract static class  AbstractProcessor implements Processor {

        protected String role = "related";
        protected String type = "object";
        protected String searchDir = "destination";
        public void setRole(String r) {
            role = r;
        }
        public void setType(String t) {
            type = t;
        }
        public void setSearchDir(String d) {
            searchDir = d;
        }
    }

    public static class Setter extends AbstractProcessor {

        private static final long serialVersionUID = 1L;
        public Object process(Node node, Field field, Object value) {
            if (log.isDebugEnabled()) {
                log.debug("Setting "  + value);
            }
            RelationList rl = node.getRelations(role, node.getCloud().getNodeManager(type), searchDir);

            Cloud cloud = node.getCloud();
            NodeList otherNodes   = cloud.getCloudContext().createNodeList();
            NodeList relatedNodes = cloud.getCloudContext().createNodeList();
            for (Object v : Casting.toList(value)) {
                otherNodes.add(Casting.toNode(v, cloud));
            }
            relatedNodes.addAll(otherNodes);


            for (Relation r : rl) {
                Node source      = r.getSource();
                Node destination = r.getDestination();
                Node otherNode = source.equals(node) ? destination : source;
                if (! relatedNodes.contains(otherNode)) {
                    r.delete();
                }
                while(relatedNodes.remove(otherNode));
            }
            RelationManager rel = "destination".equals(searchDir) ?
                cloud.getRelationManager(node.getNodeManager(),
                                         cloud.getNodeManager(type),
                                         role) :
                cloud.getRelationManager(cloud.getNodeManager(type),
                                         node.getNodeManager(),
                                         role);

            for (Node otherNode : relatedNodes) {
                if (node.isNew()) {
                    node.commit(); // Silly, but you cannot make relations to new nodes.
                }
                Relation newrel = searchDir.equals("destination") ?
                    node.createRelation(otherNode, rel) :
                    otherNode.createRelation(node, rel);
                newrel.commit();
            }
            return otherNodes;
        }
    }

    public static class Getter extends AbstractProcessor {
        private static final long serialVersionUID = 1L;

        public Object process(Node node, Field field, Object value) {
            if (log.isDebugEnabled()) {
                log.debug("getting "  + node);
            }
            NodeList nl = node.getRelatedNodes(type, role, searchDir);
            if (nl.size() == 0) {
                return null;
            } else {
                return nl;
            }
        }
    }

}
