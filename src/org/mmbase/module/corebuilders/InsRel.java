/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.corebuilders;

import java.util.*;

import org.mmbase.core.CoreField;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.cache.Cache;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;

/**
 *
 * InsRel, the main relation object, holds relations, and methods to
 * handle them. An insrel defines a relation between two objects.
 * <p>
 * This class can be extended to create insrels that can also hold
 * extra values and custom methods (named relations for example).
 * </p>
 *
 * @author Daniel Ockeloen
 * @author Pierre van Rooden
 * @version $Id$
 */
public class InsRel extends MMObjectBuilder {

    /** Base 'insrel' builder name */
    public static final String INSREL_BUILDER    = "insrel";

    /** Name of the field containing the source object number */
    public static final String FIELD_SNUMBER     = "snumber";
    /** Name of the field containing the source object number */
    public static final String FIELD_SOURCE      = FIELD_SNUMBER;
    /** Name of the field containing the destination object number */
    public static final String FIELD_DNUMBER     = "dnumber";
    /** Name of the field containing the destination object number */
    public static final String FIELD_DESTINATION = FIELD_DNUMBER;
    /** Name of the field containing the role (reldef) object number */
    public static final String FIELD_RNUMBER     = "rnumber";
    /** Name of the field containing the role (reldef) object number */
    public static final String FIELD_ROLE        = FIELD_RNUMBER;
    /** Name of the field containing the directionality */
    public static final String FIELD_DIR            = "dir";
    /** Name of the field containing the directionality */
    public static final String FIELD_DIRECTIONALITY = FIELD_DIR;

    private static final Logger log = Logging.getLoggerInstance(InsRel.class);

    /**
     * Indicates whether the relations use the 'dir' field (that is, whether the
     * field has been defined in the xml file). Used for backward compatibility.
     * The default is <code>true</code> - the value is set to <code>false</code> if any of the
     * relation builders does not contain a <code>dir</code> field (a warning is issued).
     */
    public static boolean usesdir = true;

    /**
     * Hold the relnumber to use when creating a node of this builder.
     */
    public int relnumber = -1;

    /**
     *  Cache system, holds the relations from the 25
     *  most used relations
     * @todo Is this cache still used?
     */

    private Cache relatedCache = new Cache(25) {
        public String getName()        { return "RelatedCache"; }
        public String getDescription() { return "Cache for Related Nodes"; }
        };


    /* perhaps this would be nice?
    private Cache relationsCache = new Cache(25) {
        public String getName()        { return "RelationsCache"; }
        public String getDescription() { return "Cache for Relations"; }
        };
    */


    /**
     * needed for autoload
     */
    public InsRel() {
        relatedCache.putCache();
        // relationsCache.putCache();
    }

    /**
     * Initializes the builder. Determines whether the <code>dir</code> field is defined (and thus whether directionality is supported).
     * If the field cannot be found, a <em>"Warning: No dir field. Directionality support turned off."</em> warning message is issued.
     * @return A <code>boolean</code> value, always success (<code>true</code>), as any exceptions are
     *         caught and logged.
     * @see #usesdir
     */
    public boolean init() {
        CoreField dirField = getField(FIELD_DIRECTIONALITY);
        boolean hasDirField = dirField != null && dirField.inStorage();
        if (!created()) {
            // check whether directionality is in use, and whether a dir field is present.
            // if a non-dir supporting builder is attempted to be used, a fatal error is logged.
            // the table is not created. MMbase continues, but anny atept to use this builder will fail
            // (one way or the other).
            // If the builder to be created is insrel (the basic builder), the system ignores the error
            // and continues without directionality (backward compatibility).
            //
            if (usesdir && !hasDirField && (!getTableName().equals(INSREL_BUILDER))) {
                log.fatal("FATAL ERROR: Builder "+getTableName()+" has no dir field but directionality support was turned on.");
                log.fatal("Table for "+getTableName()+" was NOT created.");
                log.fatal("MMBase continues, but use of the "+getTableName()+" builder will fail.");
                return false;
            }
        }
        boolean res=super.init();
        checkAddTmpField("_dnumber");
        checkAddTmpField("_snumber");
        if (res && usesdir && !hasDirField) {
            log.warn("No dir field. Directionality support turned off.");
            usesdir = false;
        }
        return res;
    }

    /**
     * Fixes a relation node.  Determines the source and destination numbers, and checks the object
     * types against the types specified in the relation definition ( {@link TypeRel} ).  If the
     * types differ, the source and destination are likely mis-aligned, and if the relation in the
     * other direction is indeed allowed, then they are swapped to produce a correct relation node.
     *
     * @param node The node to fix
     * @return     The node again
     */
    private MMObjectNode alignRelNode(MMObjectNode node) {
        int source = getNodeType(node.getIntValue(FIELD_SOURCE));
        int destination = getNodeType(node.getIntValue(FIELD_DESTINATION));
        int role = node.getIntValue(FIELD_ROLE);
        TypeRel typeRel = mmb.getTypeRel();
        if (!typeRel.reldefCorrect(source, destination, role) && typeRel.reldefCorrect(destination, source, role)) {
            destination = node.getIntValue(FIELD_SOURCE);
            node.setValue(FIELD_SOURCE, node.getIntValue(FIELD_DESTINATION));
            node.setValue(FIELD_DESTINATION, destination);
        }
        return node;
    }


    /**
     * Insert a new Instance Relation.
     * @param owner Administrator
     * @param source Identifying number of the source object
     * @param destination Identifying number of the destination object
     * @param role Identifying number of the relation defintition
     * @return A <code>integer</code> value identifying the newly inserted relation
     * @deprecated Use insert(String, MMObjectNode) instead.
     */
    public int insert(String owner, int source, int destination, int role) {
        int result = -1;
        MMObjectNode node = getNewNode(owner);
        if( node != null ) {
            node.setValue(FIELD_SOURCE, source);
            node.setValue(FIELD_DESTINATION, destination);
            node.setValue(FIELD_ROLE, role);
            result = insert(owner, node);
        } else {
            log.error("insert(" + owner + "," + source + "," + destination + "," + role + "): Cannot create new node(" + node + ")!");
        }
        return result;
    }


    /**
     * Insert a new Instance Relation.
     * @param owner Administrator
     * @param node Relation node to add
     * @return A <code>integer</code> value identifying the newly inserted relation
     */
    public int insert(String owner, MMObjectNode node) {
        int result = -1;
        int source = node.getIntValue(FIELD_SOURCE);
        if( source >= 0 ) {
            int destination = node.getIntValue(FIELD_DESTINATION);
            if( destination >= 0 ) {
                int role = node.getIntValue(FIELD_ROLE);
                if( role > 0 ) {
                    if (usesdir) {
                        MMObjectNode reldef = getNode(role);
                        int dir = reldef.getIntValue(FIELD_DIRECTIONALITY);
                        if (dir <= 0) dir = 2;
                        node.setValue(FIELD_DIRECTIONALITY,dir);
                    }
                    node=alignRelNode(node);
                    if (log.isDebugEnabled()) {
                        log.debug("insert(" + owner + ","  + node + ")");
                    }
                    result = super.insert(owner,node);
                    // remove cache for these nodes (enforce update)
                    deleteRelationCache(source);
                    deleteRelationCache(destination);
                } else {
                    log.error("insert("+owner+","+node+"): rnumber("+ role +") is not greater than 0! (something is seriously wrong)");
                }
            } else {
                log.error("insert("+owner+","+node+"): dnumber("+ destination +" is not greater than 0! (something is seriously wrong)");
            }
        } else {
            log.error("insert("+owner+","+node+"): snumber(" + source + ") is not greater than 0! (something is seriously wrong)");
        }
        return result;
    }

    /**
     * Remove a node from the cloud.
     * @param node The node to remove.
     */
    public void removeNode(MMObjectNode node) {
        int source = node.getIntValue(FIELD_SOURCE);
        int destination = node.getIntValue(FIELD_DESTINATION);
        super.removeNode(node);
        deleteRelationCache(source);
        deleteRelationCache(destination);
    }

    /**
     * Get relation(s) for a MMObjectNode
     * @param source Identifying number of the object to find the relations of.
     * @return If succesful, an <code>Enumeration</code> listing the relations.
     *         If no relations exist, the method returns <code>null</code>.
     * @see #getRelationsVector(int)
     */
    public Enumeration getRelations(int source) {
        return getRelations(source,-1);
    }

    /**
     * Get relation(s) for a MMObjectNode, using a specified role (reldef) as a filter
     * @param source Identifying number of the object to find the relations of.
     * @param role The number of the relation definition (role) to filter on
     * @return an <code>Enumeration</code> listing the relations.
     * @see #getRelationsVector(int, int)
     */
    public Enumeration getRelations(int source, int role) {
        return getRelationsVector(source, role).elements();
    }

    /**
     * Get relations for a specified MMObjectNode
     * @param source this is the number of the MMObjectNode requesting the relations
     * @param otype the object type of the nodes you want to have. -1 means any node.
     * @param role Identifying number of the role (reldef)
     * @return An <code>Enumeration</code> whose enumeration consists of <code>MMObjectNode</code> object related to the source
     *   according to the specified filter(s).
     */
    public Enumeration getRelations(int source, int otype, int role) {
        return getRelations(source, otype, role, true);
    }

    /**
     * Gets relations for a specified MMObjectNode
     * @param source this is the number of the MMObjectNode requesting the relations
     * @param otype the object type of the nodes you want to have. -1 means any node.
     * @param role Identifying number of the role (reldef)
     * @param usedirectionality if <code>true</code> teh result si filtered on unidirectional relations.
     *                          specify <code>false</code> if you want to show unidoerctional relations
     *                          from destination to source.
     * @return An <code>Enumeration</code> whose enumeration consists of <code>MMObjectNode</code> object related to the source
     *   according to the specified filter(s).
     */
    public Enumeration getRelations(int source, int otype, int role, boolean usedirectionality) {
        List re;
        if (usedirectionality) {
             re = getRelationsVector(source, role);
        } else {
             re = getAllRelationsVector(source, role);
        }
        if (otype==-1) {
            return Collections.enumeration(re);
        } else {
            TypeDef typedef = mmb.getTypeDef();
            MMObjectBuilder wantedBuilder = mmb.getBuilder(typedef.getValue(otype));
            List list = new ArrayList();
            for(Iterator e = re.iterator(); e.hasNext(); ) {
                MMObjectNode node = (MMObjectNode) e.next();
                int nodenr = node.getIntValue(FIELD_SOURCE);
                if (nodenr == source) {
                    nodenr = node.getIntValue(FIELD_DESTINATION);
                }
                String tableName = typedef.getValue(getNodeType(nodenr));
                if (tableName != null) {
                    MMObjectBuilder nodeBuilder = mmb.getBuilder(tableName);
                    if (nodeBuilder != null && (nodeBuilder.equals(wantedBuilder) || nodeBuilder.isExtensionOf(wantedBuilder))) {
                        list.add(node);
                    }
                }
            }
            return Collections.enumeration(list);
        }
    }

    /**
     * Checks whether any relations exist for a MMObjectNode.
     * This includes unidirection relations which would otherwise not be counted.
     * If the query fails to execute, the system will assume that relations exists.
     * @param source Identifying number of the object to find the relations of.
     * @return <code>true</code> if any relations exist, <code>false</code> otherwise.
     */
    public boolean hasRelations(int source) {
        try {
            NodeSearchQuery query = new NodeSearchQuery(this);
            BasicCompositeConstraint constraint = new BasicCompositeConstraint(CompositeConstraint.LOGICAL_OR);
            constraint.addChild(getNumberConstraint(query,FIELD_SOURCE, source));
            constraint.addChild(getNumberConstraint(query,FIELD_DESTINATION, source));
            query.setConstraint(constraint);
            return count(query) != 0;
        } catch (SearchQueryException sqe) {
            log.error(sqe.getMessage()); // should not happen
            return true; // perhaps yes?
        }
    }

    // creates a constraint for a numeric field on a query
    private BasicFieldValueConstraint getNumberConstraint(NodeSearchQuery query, String fieldName, int value) {
        return new BasicFieldValueConstraint(query.getField(query.getBuilder().getField(fieldName)), new Integer(value));
    }

    /**
     * Get relation(s) for an MMObjectNode, using a specified role (reldef) as a filter.
     * This function returns all relations based on this role in which the node is either a source, or where the node is
     * the destination, but the direction is bidirectional.
     * @param source Identifying number of the object to find the relations of.
     * @return A <code>List</code> containing the relation nodes.
     * @throws SearchQueryException if a storage error occurs
     */
    public List getRelationNodes(int source) throws SearchQueryException {
        return getRelationNodes(source, -1, usesdir);
    }

    /**
     * Get relation(s) for a MMObjectNode.
     * @deprecated use {@link #getRelationNodes(int)}
     */
    public Vector getRelationsVector(int source) {
        try {
            return new Vector(getRelationNodes(source, -1, usesdir));
        } catch (SearchQueryException  sqe) {
            log.error(sqe.getMessage()); // should not happen
            return new Vector(); //
        }
    }

    /**
     * Get relation(s) for an MMObjectNode, using a specified role (reldef) as a filter.
     * This function returns all relations based on this role in which the node is either a source, or where the node is
     * the destination, but the direction is bidirectional.
     * @param source Identifying number of the object to find the relations of.
     * @param role The number of the relation definition (role) to filter on, <code>-1</code> means any role
     * @return A <code>List</code> containing the relation nodes.
     * @throws SearchQueryException if a storage error occurs
     */
    public List getRelationNodes(int source, int role) throws SearchQueryException {
        return getRelationNodes(source, role, usesdir);
    }

    /**
     * Get relation(s) for a MMObjectNode, using a specified role.
     * @deprecated use {@link #getRelationNodes(int, int, boolean)}
     */
    public Vector getRelationsVector(int source, int role) {
        try {
            return new Vector(getRelationNodes(source, role, usesdir));
        } catch (SearchQueryException  sqe) {
            log.error(sqe.getMessage()); // should not happen
            return new Vector(); //
        }
    }

    /**
     * Get all relation(s) for an MMObjectNode.
     * This function returns all relations in which the node is either a source or
     * the destination.
     * @param source Identifying number of the object to find the relations of.
     * @param useDirectionality if <code>truie</code>, take directionality into account.
     *       If <code>false</code>, returns all relations, even if the direction is unidirectional.
     * @return A <code>List</code> containing the relation nodes.
     * @throws SearchQueryException if a storage error occurs
     */
    public List getRelationNodes(int source, boolean useDirectionality) throws SearchQueryException {
        return getRelationNodes(source, -1, useDirectionality);
    }

    /**
     * Get all relation(s) for a MMObjectNode.
     * @deprecated use {@link #getRelationNodes(int, boolean)}
     */
    public Vector getAllRelationsVector(int source) {
        try {
            return new Vector(getRelationNodes(source, -1, false));
        } catch (SearchQueryException  sqe) {
            log.error(sqe.getMessage()); // should not happen
            return new Vector(); //
        }
    }

    /**
     * Get all relation(s) for a MMObjectNode
     * This function returns all relations in which the node is either a source or
     * the destination.
     * @param source Identifying number of the object to find the relations of.
     * @param role The number of the relation definition (role) to filter on, <code>-1</code> means any role
     * @param useDirectionality if <code>truie</code>, take directionality into account.
     *       If <code>false</code>, returns all relations, even if the direction is unidirectional.
     * @return A <code>List</code> containing the relation nodes.
     * @throws SearchQueryException if a storage error occurs
     */
    public List getRelationNodes(int source, int role, boolean useDirectionality) throws SearchQueryException {
        MMObjectBuilder builder = this;
        if (role != -1) {
            builder = mmb.getRelDef().getBuilder(role);
        }
        NodeSearchQuery query1 = new NodeSearchQuery(builder);
        {
            Constraint constraint = (getNumberConstraint(query1, FIELD_SOURCE, source));
            if (role != -1) {
                BasicCompositeConstraint roleConstraint = new BasicCompositeConstraint(CompositeConstraint.LOGICAL_AND);
                roleConstraint.addChild(constraint);
                roleConstraint.addChild(getNumberConstraint(query1, FIELD_ROLE, role));
                constraint = roleConstraint;
            }
            query1.setConstraint(constraint);
        }
        NodeSearchQuery query2 = new NodeSearchQuery(builder);
        {
            BasicCompositeConstraint constraint = new BasicCompositeConstraint(CompositeConstraint.LOGICAL_AND);
            constraint.addChild(getNumberConstraint(query2, FIELD_DESTINATION, source));
            BasicFieldValueConstraint sourceConstraint = getNumberConstraint(query2, FIELD_SOURCE, source);
            sourceConstraint.setOperator(FieldCompareConstraint.NOT_EQUAL);
            constraint.addChild(sourceConstraint);
            if (useDirectionality) {
                BasicFieldValueConstraint dirConstraint = getNumberConstraint(query2, FIELD_DIRECTIONALITY, 1);
                dirConstraint.setOperator(FieldCompareConstraint.NOT_EQUAL);
                constraint.addChild(dirConstraint);
            }
            if (role != -1) {
                constraint.addChild(getNumberConstraint(query2, FIELD_ROLE, role));
            }
            query2.setConstraint(constraint);
        }
        org.mmbase.util.ChainedList nodes =  new org.mmbase.util.ChainedList();
        nodes.addList(builder.getNodes(query1));
        nodes.addList(builder.getNodes(query2));
        return nodes;
    }

    /**
     * Get all relation(s) for a MMObjectNode.
     * @deprecated use {@link #getRelationNodes(int, int, boolean)}
     */
    public Vector getAllRelationsVector(int source, int role) {
        try {
            return new Vector(getRelationNodes(source, role, false));
        } catch (SearchQueryException  sqe) {
            log.error(sqe.getMessage()); // should not happen
            return new Vector(); //
        }
    }

    /**
     * Test whether a relation exists and returns the corresponding node.
     * Note that this test is strict: it determines whether a relation exists from a source to a destination
     * with a specific role. If only a role-relation exists where source and destination are reversed, this method
     * assumed that this is a different relation type, and it returns <code>null</code>.
     * @param source Identifying number of the source object
     * @param destination Identifying number of the destination object
     * @param role Identifying number of the role (reldef)
     * @throws SearchQueryException if a storage error occurs
     * @return The corresponding <code>MMObjectNode</code> if the exact relation exists,<code>null</code> otherwise
     */
    public MMObjectNode getRelationNode(int source, int destination, int role) throws SearchQueryException {
        MMObjectNode result = null;
        MMObjectBuilder builder = mmb.getRelDef().getBuilder(role);
        NodeSearchQuery query = new NodeSearchQuery(builder);
        BasicCompositeConstraint constraint = new BasicCompositeConstraint(CompositeConstraint.LOGICAL_AND);
        constraint.addChild(getNumberConstraint(query,FIELD_SOURCE, source));
        constraint.addChild(getNumberConstraint(query,FIELD_DESTINATION, destination));
        constraint.addChild(getNumberConstraint(query,FIELD_ROLE, role));
        query.setConstraint(constraint);
        Iterator i = builder.getNodes(query).iterator();
        if (i.hasNext()) {
            result = (MMObjectNode)i.next();
        }
        return result;
    }

    /**
     * Test whether a relation exists and returns the corresponding node.
     * Note that this test is strict: it determines whether a relation exists from a source to a destination
     * with a specific role. If only a role-relation exists where source and destination are reversed, this method
     * assumed that this is a different relation type, and it returns <code>null</code>.
     * @param source Identifying number of the source object
     * @param destination Identifying number of the destination object
     * @param role Identifying number of the role (reldef)
     * @return The corresponding <code>MMObjectNode</code> if the exact relation exists,<code>null</code> otherwise
     */
    public MMObjectNode getRelation(int source, int destination, int role) {
        try {
            return getRelationNode(source, destination, role);
        } catch (SearchQueryException  sqe) {
            log.error(sqe.getMessage()); // should not happen
            return null;
        }
    }

    /**
    * get MMObjectNodes related to a specified MMObjectNode
    * @param sourceNode this is the source MMObjectNode
    * @param nodeType Specifies the type of the nodes you want to have e.g. "pools"
    */
    public Enumeration getRelated(String sourceNode, String nodeType) {
        try {
            int source = Integer.parseInt(sourceNode);
            int otype = mmb.getTypeDef().getIntValue(nodeType);
            return getRelated(source, otype);
        } catch(Exception e) {
            // why is this silentely catched ?
        }
        return null;
    }

    /**
    * get MMObjectNodes related to a specified MMObjectNode
    * @param source this is the number of the source MMObjectNode
    * @param nodeType Specifies the type of the nodes you want to have e.g. "pools"
    */
    public Enumeration getRelated(int source, String nodeType) {
        try {
            int otype = -1;
            if (nodeType != null) {
                otype = mmb.getTypeDef().getIntValue(nodeType);
            }
            return getRelated(source, otype);
        } catch(Exception e) {
            // why is this silentely catched ?
        }
        return null;
    }

    /**
    * Get MMObjectNodes of a specified type related to a specified MMObjectNode
    * @param source this is the number of the source MMObjectNode
    * @param otype the object type of the nodes you want to have
    * @return An <code>Enumeration</code> of <code>MMObjectNode</code> object related to the source
    */
    public Enumeration getRelated(int source, int otype) {
        Vector se = getRelatedVector(source,otype);
        if (se != null) return se.elements();
        return null;
    }

    /**
    * get MMObjectNodes related to a specified MMObjectNode
    * @param sourceNode this is the number of the source MMObjectNode (in string format)
    * @param nodeType Specifies the type of the nodes you want to have e.g. "pools"
    * @param roleName the role of teh relation (name in reldef)
    */
    public Enumeration getRelated(String sourceNode, String nodeType, String roleName) {
        try {
            int source = Integer.parseInt(sourceNode);
            int otype = mmb.getTypeDef().getIntValue(nodeType);
            int role = mmb.getRelDef().getNumberByName(roleName);
            return getRelated(source, otype, role);
        } catch(Exception e) {}
        return null;
    }

    /**
    * get MMObjectNodes related to a specified MMObjectNode
    * @param source this is the number of the source MMObjectNode
    * @param nodeType Specifies the type of the nodes you want to have e.g. "pools"
    * @param roleName the name of the role of the relation (name in reldef)
    */
    public Enumeration getRelated(int source, String nodeType, String roleName) {
        try {
            int otype = mmb.getTypeDef().getIntValue(nodeType);
            int role = mmb.getRelDef().getNumberByName(roleName);
            return getRelated(source, otype, role);
        } catch(Exception e) {}
        return null;
    }

    /**
    * Get MMObjectNodes of a specified type related to a specified MMObjectNode
    * @param source this is the number of the source MMObjectNode
    * @param otype the object type of the nodes you want to have
    * @param role Identifying number of the role (reldef)
    * @return An <code>Enumeration</code> of <code>MMObjectNode</code> object related to the source
    */
    public Enumeration getRelated(int source, int otype, int role) {
        Vector se = getRelatedVector(source, otype, role);
        if (se != null) return se.elements();
        return null;
    }

    /**
    * Get MMObjectNodes related to a specified MMObjectNode
    * @param source this is the number of the MMObjectNode requesting the relations
    * @param otype the object type of the nodes you want to have. -1 means any node.
    * @return A <code>Vector</code> whose enumeration consists of <code>MMObjectNode</code> object related to the source
    *   according to the specified filter(s).
    * @deprecated
    **/
    public Vector getRelatedVector(int source, int otype) {
        return getRelatedVector(source,otype,-1);
    }

    /**
     * Get MMObjectNodes related to a specified MMObjectNode
     * @param source this is the number of the MMObjectNode requesting the relations
     * @param otype the object type of the nodes you want to have. -1 means any node.
     * @param role Identifying number of the role (reldef)
     * @return A <code>Vector</code> whose enumeration consists of <code>MMObjectNode</code> object related to the source
     *   according to the specified filter(s).
    * @deprecated
     */
    public Vector getRelatedVector(int source, int otype, int role) {
        Vector list = null;
        if (role == -1) {
            list = (Vector)relatedCache.get(new Integer(source));
        }
        if (list == null) {
            list = new Vector();
            MMObjectNode node,node2;
            int nodenr = -1;
            for(Enumeration e = getRelations(source, role); e.hasMoreElements(); ) {
                    node = (MMObjectNode)e.nextElement();
                    nodenr = node.getIntValue(FIELD_SOURCE);
                    if (nodenr == source) {
                        nodenr = node.getIntValue(FIELD_DESTINATION);
                    }
                    node2=getNode(nodenr);
                    if(node2 != null) {
                        list.addElement(node2);
                    }
            }
            if (role == -1) {
                relatedCache.put(new Integer(source),list);
            }
        }
        // oke got the Vector now lets get the correct otypes

        Vector results = null;
        if (otype == -1) {
            results = new Vector(list);
        } else {
            results = new Vector();
            for (Enumeration e = list.elements(); e.hasMoreElements();) {
                MMObjectNode node = (MMObjectNode)e.nextElement();
                if (node.getOType() == otype) {
                    results.addElement(node);
                }
            }
        }
        return results;
    }

    public String getGUIIndicator(MMObjectNode node) {
        return node.getStringValue(FIELD_SOURCE) + "->" + node.getStringValue(FIELD_DESTINATION);
    }


    /**
    * Get the display string for a given field of this node.
    * Returns for 'snumber' the name of the source object,
    * for 'dnumber' the name of the destination object, and
    * for 'rnumber' the name of the relation definition.
    * @param field name of the field to describe.
    * @param node Node containing the field data.
    * @return A <code>String</code> describing the requested field's content
    **/
    public String getGUIIndicator(String field, MMObjectNode node) {
        try {
            if (field.equals(FIELD_DIRECTIONALITY)) {
                int dir=node.getIntValue(FIELD_DIRECTIONALITY);
                if (dir == 2) {
                    return "bidirectional";
                } else if (dir==1) {
                    return "unidirectional";
                } else {
                    return "unknown";
                }
            } else if (field.equals(FIELD_SOURCE)) {
                MMObjectNode node2 = getNode(node.getIntValue(FIELD_SOURCE));
                String ty = "=" + mmb.getTypeDef().getValue(node2.getOType());
                if (node2 != null) {
                    return "" + node.getIntValue(FIELD_SOURCE) + ty + "(" + node2.getGUIIndicator()+")";
                }
            } else if (field.equals(FIELD_DESTINATION)) {
                MMObjectNode node2 = getNode(node.getIntValue(FIELD_DESTINATION));
                String ty = "=" + mmb.getTypeDef().getValue(node2.getOType());
                if (node2 != null) {
                    return "" + node.getIntValue(FIELD_DESTINATION) + ty + "(" + node2.getGUIIndicator() + ")";
                }
            } else if (field.equals(FIELD_ROLE)) {
                MMObjectNode node2 = mmb.getRelDef().getNode(node.getIntValue(FIELD_ROLE));
                return "" + node.getIntValue(FIELD_ROLE) + "=" + node2.getGUIIndicator();
            }
        } catch (Exception e) {
        }
        return null;
    }

    /**
    * Checks whether a specific relation exists.
    * Maintains a cache containing the last checked relations
    *
    * Note that this routine returns false both when a source/destination are swapped, and when a typecombo
    * does not exist -  it is not possible to derive whether one or the other has occurred.
    *
    * @param source Number of the source node
    * @param destination Number of the destination node
    * @param role  Number of the relation definition
    * @return A <code>boolean</code> indicating success when the relation exists, failure if it does not.
    * @deprecated Use {@link TypeRel#reldefCorrect} instead
    */
    public boolean reldefCorrect(int source, int destination, int role) {
        return mmb.getTypeRel().reldefCorrect(source, destination, role);
    }

    /**
    * Deletes the Relation cache.
    * This is to be called if caching gives problems.
    * Make sure that you can't use the deleteRelationCache(int source) instead.
    **/
    public void deleteRelationCache() {
        relatedCache.clear();
    }

    /**
    * Delete a specified relation from the relationCache
    * @param source the number of the relation to remove from the cache
    **/
    public void deleteRelationCache(int source) {
        relatedCache.remove(new Integer(source));
    }

    /**
    * Search the relation definition table for the identifying number of
    * a relation, by name.
    * Success is dependent on the uniqueness of the relation's name (not enforced, so unpredictable).
    * @param name The name on which to search for the relation
    * @return A <code>int</code> value indicating the relation's object number, or -1 if not found.
    **/
    public int getGuessedNumber(String name) {
        RelDef reldef = mmb.getRelDef();
        if (reldef != null) {
            return reldef.getNumberByName(name);
        }
        return -1;
    }

    /**
    * Set defaults for a node.
    * Tries to determine a default for 'relnumber' by searching the RelDef table for an occurrence of the node's builder.
    * Uses the table-mapping system, and should be replaced.
    * @param node The node whose defaults to set.
    */
    public void setDefaults(MMObjectNode node) {
        super.setDefaults(node);
        if (tableName.equals(INSREL_BUILDER)) return;

        if (relnumber == -1) {
            MMObjectNode n = mmb.getRelDef().getDefaultForBuilder(this);
            if (n == null) {
                log.warn("Can not determine default reldef for ("+getTableName()+")");
            } else {
                relnumber = n.getNumber();
            }
        }
        node.setValue(FIELD_ROLE,relnumber);
    }
}
