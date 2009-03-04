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
 * act as a a node field, but actually is a related node.
 *
 * In case just one related node is desired, this makes it easy to just create a drop-down for it.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.8.7
 * @version $Id: Related.java,v 1.6 2009-03-04 11:32:09 michiel Exp $
 */

public class Related {

    private static final Logger log = Logging.getLoggerInstance(Related.class);

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

            if (value != null) {
                Cloud cloud = node.getCloud();
                Node dest = Casting.toNode(value, cloud);
                NodeList nl = node.getRelatedNodes(type, role, searchDir);
                if (nl.contains(dest)) {
                    // nothing changed
                } else {
                    for (Relation r : rl) {
                        r.delete();
                    }
                    RelationManager rel = cloud.getRelationManager(node.getNodeManager(),
                                                                   cloud.getNodeManager(type),
                                                                   role);
                    if (node.isNew()) node.commit(); // Silly, but you cannot make relations to new nodes.
                    Relation newrel = node.createRelation(dest, rel);
                    newrel.commit();
                }
                return dest;
            } else {
                for (Relation r : rl) {
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
                log.info("The node is new, returning " + field.getDataType().getDefaultValue());
                return field.getDataType().getDefaultValue();
            }
            NodeList nl = node.getRelatedNodes(type, role, searchDir);
            if (nl.size() == 0) {
                return null;
            } else {
                return nl.getNode(0);
            }
        }
    }

}
