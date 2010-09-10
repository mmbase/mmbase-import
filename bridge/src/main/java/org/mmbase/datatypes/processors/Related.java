/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.processors;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.*;
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
        private String type = null;
        private String typeProperty = null;
        private String createType = null;
        private String createTypeProperty = null;
        protected String searchDir = "destination";
        protected boolean relateDefaultIfNull = false;


        protected final Map<String, String> relationConstraints = new HashMap<String, String>();
        public void setRole(String r) {
            role = r;
        }
        public void setType(String t) {
            type = t;
        }
        public void setTypeProperty(String tp) {
            typeProperty = tp;
        }

        public void setCreateType(String t) {
            createType = t;
        }
        public void setCreateTypeProperty(String tp) {
            createTypeProperty = tp;
        }

        public void setSearchDir(String d) {
            searchDir = d;
        }
        public void setRelationConstraints(Map<String, String> map) {
            relationConstraints.putAll(map);
        }

        public void setRelateDefaultIfNull(boolean b) {
            relateDefaultIfNull = b;
        }


        protected NodeManager getRelatedType(Node node) {
            Cloud cloud = node.getCloud();
            if(typeProperty != null) {
                return cloud.getNodeManager(node.getNodeManager().getProperty(typeProperty));
            } else if (type != null) {
                return cloud.getNodeManager(type);
            } else if(createTypeProperty != null) {
                return cloud.getNodeManager(node.getNodeManager().getProperty(createTypeProperty));
            } else if (createType != null) {
                return cloud.getNodeManager(createType);
            } else {
                return cloud.getNodeManager("object");
            }
        }

        protected NodeManager getRelatedCreateType(Node node) {
            Cloud cloud = node.getCloud();
            if(createTypeProperty != null) {
                return cloud.getNodeManager(node.getNodeManager().getProperty(createTypeProperty));
            } else if (createType != null) {
                return cloud.getNodeManager(createType);
            } else {
                return getRelatedType(node);
            }
        }

        protected RelationManager getRelationManager(Node node) {
            Cloud cloud = node.getCloud();
            if (searchDir.equals("source")) {
                return cloud.getRelationManager(getRelatedType(node),
                                                node.getNodeManager(),
                                                role);

            } else {
                return cloud.getRelationManager(node.getNodeManager(),
                                                getRelatedType(node),
                                                role);
            }
        }

        protected NodeQuery getRelationsQuery(Node node) {
            NodeQuery nq = Queries.createRelationNodesQuery(node, getRelatedType(node), role, searchDir);
            Step relationStep = nq.getSteps().get(1);
            for (Map.Entry<String, String> entry : relationConstraints.entrySet()) {
                String key = entry.getKey();
                if (key.indexOf(".") > 0) {
                    Queries.addConstraint(nq, Queries.createConstraint(nq, key, FieldCompareConstraint.EQUAL, entry.getValue()));
                } else {
                    NodeManager relationNodeManager = node.getCloud().getNodeManager(relationStep.getTableName());
                    StepField sf = nq.createStepField(relationStep, relationNodeManager.getField(key));
                    Queries.addConstraint(nq, nq.createConstraint(sf, FieldCompareConstraint.EQUAL, entry.getValue()));
                }
            }
            return nq;
        }

        protected NodeQuery getRelatedQuery(Node node) {
            Cloud cloud = node.getCloud();
            NodeQuery nq = Queries.createRelatedNodesQuery(node, getRelatedType(node), role, searchDir);
            for (Map.Entry<String, String> entry : relationConstraints.entrySet()) {
                Queries.addConstraint(nq, Queries.createConstraint(nq, entry.getKey(), FieldCompareConstraint.EQUAL, entry.getValue()));
            }
            return nq;
        }

        protected String getKey(Node node, Field field) {
            return Related.class.getName() + node.getValue("_number") + "." + role + "." + getRelatedType(node) + "." + searchDir;
        }

        protected Node getRelatedNode(final Node node, final Field field) {
            String key = getKey(node, field);
            Node relatedNode = (Node) node.getCloud().getProperty(key);
            if (relatedNode != null) {
                log.debug("Found a node in " + key);
                return relatedNode;
            } else {
                NodeQuery related = getRelatedQuery(node);
                NodeList rl = related.getNodeManager().getList(related);
                if (rl.size() > 0) {
                    relatedNode = rl.getNode(0);
                    if (node.getCloud() instanceof Transaction) {
                        node.getCloud().setProperty(key,  relatedNode);
                    }
                    log.debug("Found relatedNode " + relatedNode);
                } else {
                    log.debug("Not related");
                }
                return relatedNode;

            }
        }

        protected void createRelation(final Node node, final Node dest, final NodeList rl) {
            boolean related = false;
            if (rl.size() == 1) {
                Relation r = rl.getNode(0).toRelation();
                try {
                    if (r.getDestination().getNumber() == dest.getNumber() ||
                        r.getSource().getNumber() == dest.getNumber()) {
                        related = true;
                    }
                } catch (NotFoundException nfe) {
                    log.warn(nfe.getMessage(), nfe);
                    log.warn("Inconsistent db?, Deleting this relation");
                    r.delete();
                }
            } else if (rl.size() > 1) {
                log.warn("More than one correct relations between " + node + " and " + getRelatedType(node) + " " + rl + ". Will fix this now");
            }
            if (related) {
                log.debug("" + dest + " already correctly related");
                // nothing changed
            } else {
                log.debug("" + dest + " not correctly related");
                RelationManager rel = getRelationManager(node);
                if (node.isNew()) {
                    log.debug("Cannot make relations to new nodes");
                    node.commit(); // Silly, but you cannot make relations to new nodes.
                } else {
                    log.debug("Deleting " + rl.size() + " existing relations");
                    for (Node r : rl) {
                        log.debug("Deleting " + r);
                        r.delete();
                    }
                }
                log.debug("Creating new relation ");
                Relation newrel = node.createRelation(dest, rel);

                try {
                    for (Map.Entry<String, String> entry : relationConstraints.entrySet()) {
                        log.debug("Setting " + entry);
                        String key = entry.getKey();
                        int dot = key.indexOf(".");
                        if (dot >= 0) {
                            key = key.substring(dot + 1, key.length());
                        }
                        newrel.setStringValue(key, entry.getValue());
                    }
                } catch (Throwable e) {
                    log.warn(e.getMessage(), e);
                }
                log.debug("Created " + newrel);
                newrel.commit();
            }

        }
    }

    /**
     * This get-processor can be used to implicitly create the wanted related node too.
     */
    public static class Creator extends AbstractProcessor {
        private static final long serialVersionUID = 1L;
        public Object process(final Node node, final Field field, final Object value) {
            Node relatedNode = getRelatedNode(node, field);
            if (relatedNode == null) {
                log.service("No related node of type " + getRelatedCreateType(node) + " for node " + node.getNumber() + ". Implicitely creating now.");
                Node newNode = getRelatedCreateType(node).createNode();
                newNode.commit();
                RelationManager rel = getRelationManager(node);
                Relation newrel = node.createRelation(newNode, rel);
                for (Map.Entry<String, String> entry : relationConstraints.entrySet()) {
                    newrel.setStringValue(entry.getKey(), entry.getValue());
                }

                newrel.commit();
                if (node.getCloud() instanceof Transaction) {
                    node.getCloud().setProperty(getKey(node, field),  newNode);
                }
                if (log.isDebugEnabled()) {
                    log.debug("Created " + newrel);
                    log.debug("Cloud: : " + node.getCloud().getProperties());

                }
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

            NodeQuery relations = getRelationsQuery(node);
            NodeList rl = relations.getNodeManager().getList(relations);
            log.debug("Found " + rl.size() + " existing relations");
            if (value != null) {
                Cloud cloud = node.getCloud();
                Node dest = BridgeCaster.toNode(value, cloud);
                createRelation(node, dest, rl);
                return dest;
            } else {
                log.debug("value is null, deleting existing relations");
                for (Node r : rl) {
                    r.delete();
                }
                return null;
            }

        }
    }

    public static class Getter extends AbstractProcessor {
        private static final long serialVersionUID = 1L;


        public Object process(final Node node, final Field field, final Object value) {
            if (log.isDebugEnabled()) {
                log.debug("getting "  + node);
            }
            if (node == null) {
                // null cannot be related to anything
                return null;
            }
            Node result = getRelatedNode(node, field);
            if (relateDefaultIfNull && result == null && field != null) {
                Object defaultValue = field.getDataType().getDefaultValue(node.getCloud().getLocale(), node.getCloud(), field);
                if (defaultValue != null) {
                    Node defaultNode = BridgeCaster.toNode(defaultValue, node.getCloud());
                    createRelation(node, defaultNode, BridgeCollections.EMPTY_NODELIST);
                }
            }
            return result;
        }
    }

}
