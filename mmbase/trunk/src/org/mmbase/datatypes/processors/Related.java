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
 * act as 'related'.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.8.7
 * @version $Id: Related.java,v 1.2 2008-11-11 13:24:46 michiel Exp $
 */

public class Related {

    private static final Logger log = Logging.getLoggerInstance(Related.class);

    private static abstract class  AbstractProcessor implements Processor {

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
            if (node.getChanged().contains(field.getName())) {
                log.info("Setting "  + value);
                RelationList nl = node.getRelations(role, node.getCloud().getNodeManager(type), searchDir);
                RelationIterator ni = nl.relationIterator();
                while (ni.hasNext()) {
                    Relation r = ni.nextRelation();
                    r.delete();
                }
                if (value != null) {
                    Cloud cloud = node.getCloud();
                    RelationManager rel = cloud.getRelationManager(node.getNodeManager(),
                                                                   cloud.getNodeManager(type),
                                                                   role);
                    Node dest = Casting.toNode(value, node.getCloud());
                    node.createRelation(dest, rel);
                    return dest;
                } else {
                    return null;
                }
            } else {
                return value;
            }
        }
    }

    public static class Getter extends AbstractProcessor {
        private static final long serialVersionUID = 1L;

        public Object process(Node node, Field field, Object value) {
            NodeList nl = node.getRelatedNodes(type, role, searchDir);
            if (nl.size() == 0) {
                return null;
            } else {
                return nl.getNode(0);
            }
        }
    }

}
