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
 * acts as a field of a related node.
 *
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.9.1
 * @version $Id: Related.java 34900 2009-05-01 16:29:42Z michiel $
 */

public class RelatedField {

    private static final Logger log = Logging.getLoggerInstance(RelatedField.class);


    public abstract static class  AbstractProcessor extends Related.AbstractProcessor {


        protected String otherField = null;

        public void setField(String f) {
            otherField  = f;
        }


    }



    /**
     * This get-processor can be used to implictely create the wanted related node too.
     */
    public static class Creator extends AbstractProcessor {
        private static final long serialVersionUID = 1L;
        public Object process(final Node node, final Field field, final Object value) {
            Node relatedNode = getRelatedNode(node, field);
            if (relatedNode == null) {
                NodeQuery related = getRelatedQuery(node);
                log.service("No related node of type " + getRelatedType(node) + " for node " + node.getNumber() + ". Implicitely creating now.");
                Cloud cloud = node.getCloud();
                Node newNode = getRelatedType(node).createNode();
                newNode.commit();
                RelationManager rel = getRelationManager(node);
                Relation newrel = node.createRelation(newNode, rel);
                for (Map.Entry<String, String> entry : relationConstraints.entrySet()) {
                    newrel.setStringValue(entry.getKey(), entry.getValue());
                }
                log.service("Created " + newrel);
                newrel.commit();
                if (node.getCloud() instanceof Transaction) {
                    node.getCloud().setProperty(getKey(node, field),  newNode);
                }
                log.info("" + node.getCloud().getProperties());
            }
            return value;
        }
    }


    public static class Setter extends AbstractProcessor {

        private static final long serialVersionUID = 1L;
        public Object process(final Node node, final Field field, final Object value) {
            if (log.isDebugEnabled()) {
                log.debug("Setting "  + value);
            }

            Node otherNode = getRelatedNode(node, field);
            if (otherNode != null) {
                String fieldName = otherField == null ? field.getName() : otherField;
                otherNode.setValue(fieldName, value);
                otherNode.commit();
                return value;
            } else {
                log.warn("No related node");
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
            Node otherNode = getRelatedNode(node, field);
            if (otherNode != null) {
                String fieldName = otherField == null ? field.getName() : otherField;
                return otherNode.getValue(fieldName);
            } else {
                log.debug("No related node");
                return null;
            }
        }
    }

}
