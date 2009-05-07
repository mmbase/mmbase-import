/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.processors;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.*;
import org.mmbase.datatypes.*;
import org.mmbase.util.*;
import org.mmbase.storage.search.*;
import java.util.*;
import org.mmbase.util.logging.*;

/**
 * The set- and get- processors implemented in this file can be used to make a virtual field which
 * act as a a node field, but actually is a related node.
 *
 * In case just one related node is desired, this makes it easy to just create a drop-down for it.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.8.7
 * @version $Id$
 */

public class Related {

    private static final Logger log = Logging.getLoggerInstance(Related.class);

    public abstract static class  AbstractProcessor implements Processor {

        protected String role = "related";
        protected String type = "object";
        protected String searchDir = "destination";
        protected final Map<String, String> relationConstraints = new HashMap<String, String>();
        public void setRole(String r) {
            role = r;
        }
        public void setType(String t) {
            type = t;
        }
        public void setSearchDir(String d) {
            searchDir = d;
        }
        public void setRelationConstraints(Map<String, String> map) {
            relationConstraints.putAll(map);
        }

        protected RelationManager getRelationManager(Node node) {
            Cloud cloud = node.getCloud();
            if (searchDir.equals("source")) {
                return cloud.getRelationManager(cloud.getNodeManager(type),
                                                node.getNodeManager(),
                                                role);

            } else {
                return cloud.getRelationManager(node.getNodeManager(),
                                                cloud.getNodeManager(type),
                                                role);
            }
        }

        protected NodeQuery getRelationsQuery(Node node) {
            Cloud cloud = node.getCloud();
            NodeQuery nq = Queries.createRelationNodesQuery(node, cloud.getNodeManager(type), role, searchDir);
            for (Map.Entry<String, String> entry : relationConstraints.entrySet()) {
                Queries.addConstraint(nq, Queries.createConstraint(nq, entry.getKey(), FieldCompareConstraint.EQUAL, entry.getValue()));
            }
            return nq;
        }
    }

    public static class Setter extends AbstractProcessor {

        private static final long serialVersionUID = 1L;
        public Object process(Node node, Field field, Object value) {
            if (log.isDebugEnabled()) {
                log.debug("Setting "  + value);
            }

            NodeQuery relations = getRelationsQuery(node);
            NodeList rl = relations.getNodeManager().getList(relations);
            if (value != null) {
                Cloud cloud = node.getCloud();
                Node dest = Casting.toNode(value, cloud);

                boolean related = false;
                if (rl.size() == 1) {
                    Relation r = rl.getNode(0).toRelation();
                    if (r.getDestination().getNumber() == dest.getNumber() ||
                        r.getSource().getNumber() == dest.getNumber()) {
                        related = true;
                    }
                } else if (rl.size() > 1) {
                    log.warn("More than one correct relations between " + node + " and " + type + " " + rl + ". Will fix this now");
                }
                if (related) {
                    log.debug("" + dest + " already correctly related");
                    // nothing changed
                } else {
                    for (Node r : rl) {
                        log.debug("Deleting " + r);
                        r.delete();
                    }
                    RelationManager rel = getRelationManager(node);
                    if (node.isNew()) node.commit(); // Silly, but you cannot make relations to new nodes.
                    Relation newrel = node.createRelation(dest, rel);
                    for (Map.Entry<String, String> entry : relationConstraints.entrySet()) {
                        newrel.setStringValue(entry.getKey(), entry.getValue());
                    }
                    log.debug("Created " + newrel);
                    newrel.commit();
                }
                return dest;
            } else {
                for (Node r : rl) {
                    r.delete();
                }
                return null;
            }

        }
    }

    public static class Getter extends AbstractProcessor {
        private static final long serialVersionUID = 1L;

        public Object process(Node node, Field field, Object value) {
            if (log.isDebugEnabled()) {
                log.debug("getting "  + node);
            }
            if (node.isNew()) {
                log.debug("The node is new, returning " + field.getDataType().getDefaultValue());
                return field.getDataType().getDefaultValue();
            }
            NodeQuery relations = getRelationsQuery(node);
            NodeList rl = relations.getNodeManager().getList(relations);
            if (rl.size() == 0) {
                return null;
            } else {
                Relation relation = rl.getNode(0).toRelation();
                if (relation.getSource().getNumber() == node.getNumber()) {
                    return relation.getDestination();
                } else {
                    return relation.getSource();
                }
            }
        }
    }

}
