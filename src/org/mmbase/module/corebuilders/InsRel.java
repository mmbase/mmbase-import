/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.corebuilders;

import java.util.*;
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
 * @version $Id: InsRel.java,v 1.39 2003-11-03 13:12:53 michiel Exp $
 */
public class InsRel extends MMObjectBuilder {

    /*
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

    private static Logger log = Logging.getLoggerInstance(InsRel.class);

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
        if (!created()) {
            // check whether directionality is in use, and whether a dir field is present.
            // if a non-dir supporting builder is attempted to be used, a fatal error is logged.
            // the table is not created. MMbase continues, but anny atept to use this builder will fail
            // (one way or the other).
            // If the builder to be created is insrel (the basic builder), the system ignores the error
            // and continues without directionality (backward compatibility).
            //
            if (usesdir && (getField("dir")==null) && (!getTableName().equals("insrel"))) {
                log.fatal("FATAL ERROR: Builder "+getTableName()+" has no dir field but directionality support was turned on.");
                log.fatal("Table for "+getTableName()+" was NOT created.");
                log.fatal("MMBase continues, but use of the "+getTableName()+" builder will fail.");
                return false;
            }
        }
        boolean res=super.init();
        checkAddTmpField("_dnumber");
        checkAddTmpField("_snumber");
        if (res && usesdir && (getField("dir")==null)) {
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
        int snumber=getNodeType(node.getIntValue("snumber"));
        int dnumber=getNodeType(node.getIntValue("dnumber"));
        int rnumber = node.getIntValue("rnumber");
        TypeRel typeRel = mmb.getTypeRel();
        if (  (!typeRel.reldefCorrect(snumber, dnumber, rnumber))
            && typeRel.reldefCorrect(dnumber, snumber, rnumber) 
              ) {
            dnumber= node.getIntValue("snumber");
            node.setValue("snumber", node.getIntValue("dnumber"));
            node.setValue("dnumber", dnumber);
        }
        return node;
    }


    /**
     * Insert a new Instance Relation.
     * @param owner Administrator
     * @param snumber Identifying number of the source object
     * @param dnumber Identifying number of the destination object
     * @param rnumber Identifying number of the relation defintition
     * @return A <code>integer</code> value identifying the newly inserted relation
     * @deprecated Use insert(String, MMObjectNode) instead.
     */
    public int insert(String owner, int snumber, int dnumber, int rnumber) {
        int result = -1;
        MMObjectNode node = getNewNode(owner);
        if( node != null ) {
            node.setValue("snumber",snumber);
            node.setValue("dnumber",dnumber);
            node.setValue("rnumber",rnumber);
            result = insert(owner,node);
        } else {
            log.error("insert("+owner+","+snumber+","+dnumber+","+rnumber+"): Cannot create new node("+node+")!");
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
        int snumber=node.getIntValue("snumber");
        if( snumber >= 0 ) {
            int dnumber=node.getIntValue("dnumber");
            if( dnumber >= 0 ) {
                int rnumber=node.getIntValue("rnumber");
                if( rnumber > 0 ) {
                    if (usesdir) {
                        MMObjectNode reldef=getNode(rnumber);
                        int dir= reldef.getIntValue("dir");
                        if (dir<=0) dir =2;
                        node.setValue("dir",dir);
                    }
                    node=alignRelNode(node);
                    if (log.isDebugEnabled()) {
                        log.debug("insert(" + owner + ","  + node + ")");
                    }
                    result=super.insert(owner,node);
                    // remove cache for these nodes (enforce update)
                    deleteRelationCache(snumber);
                    deleteRelationCache(dnumber);
                            // Gerard: temporary removed here, should be removed from databaselayer!!!!
                            /*
                            MMObjectNode n1=getNode(snumber);
                            MMObjectNode n2=getNode(dnumber);

                            mmb.mmc.changedNode(n1.getNumber(),n1.getTableName(),"r");
                            mmb.mmc.changedNode(n2.getNumber(),n2.getTableName(),"r");
                                  */
                } else {
                    log.error("insert("+owner+","+node+"): rnumber("+rnumber+") is not greater than 0! (something is seriously wrong)");
                }
            } else {
                log.error("insert("+owner+","+node+"): dnumber("+dnumber+" is not greater than 0! (something is seriously wrong)");
            }
        } else {
            log.error("insert("+owner+","+node+"): snumber("+snumber+") is not greater than 0! (something is seriously wrong)");
        }
        return result;
    }

    /**
     * Remove a node from the cloud.
     * @param node The node to remove.
     */
    public void removeNode(MMObjectNode node) {
        int snumber=node.getIntValue("snumber");
        int dnumber=node.getIntValue("dnumber");
        super.removeNode(node);
        deleteRelationCache(snumber);
        deleteRelationCache(dnumber);
    }

    /**
     * Get relation(s) for a MMObjectNode
     * @param src Identifying number of the object to find the relations of.
     * @return If succesful, an <code>Enumeration</code> listing the relations.
     *         If no relations exist, the method returns <code>null</code>.
     * @see #getRelationsVector(int)
     */
    public Enumeration getRelations(int src) {
        return getRelations(src,-1);
    }

    /**
     * Get relation(s) for a MMObjectNode, using a specified role (reldef) as a filter
     * @param src Identifying number of the object to find the relations of.
     * @param rnumber The number of the relation definition (role) to filter on
     * @return an <code>Enumeration</code> listing the relations.
     * @see #getRelationsVector(int,int)
     */
    public Enumeration getRelations(int src, int rnumber) {
        return getRelationsVector(src,rnumber).elements();
    }

    /**
     * Get relations for a specified MMObjectNode
     * @param src this is the number of the MMObjectNode requesting the relations
     * @param otype the object type of the nodes you want to have. -1 means any node.
     * @param rnumber Identifying number of the role (reldef)
     * @return An <code>Enumeration</code> whose enumeration consists of <code>MMObjectNode</code> object related to the source
     *   according to the specified filter(s).
     */
    public Enumeration getRelations(int src,int otype, int rnumber) {
        return getRelations(src,otype,rnumber,true);
    }

    /**
     * Gets relations for a specified MMObjectNode
     * @param src this is the number of the MMObjectNode requesting the relations
     * @param otype the object type of the nodes you want to have. -1 means any node.
     * @param rnumber Identifying number of the role (reldef)
     * @param usedirectionality if <code>true</code> teh result si filtered on unidirectional relations.
     *                          specify <code>false</code> if you want to show unidoerctional relations
     *                          from destination to source.
     * @return An <code>Enumeration</code> whose enumeration consists of <code>MMObjectNode</code> object related to the source
     *   according to the specified filter(s).
     */
    public Enumeration getRelations(int src,int otype, int rnumber, boolean usedirectionality) {
        Vector re;
        if (usedirectionality) {
             re = getRelationsVector(src,rnumber);
        } else {
             re = getAllRelationsVector(src,rnumber);
        }
        if (otype==-1) {
            return re.elements();
        } else {
            TypeDef typedef = mmb.getTypeDef();
            MMObjectBuilder wantedBuilder = mmb.getBuilder(typedef.getValue(otype));
            Vector list = new Vector();
            for(Enumeration e = re.elements(); e.hasMoreElements(); ) {
                MMObjectNode node = (MMObjectNode) e.nextElement();
                int nodenr = node.getIntValue("snumber");
                if (nodenr == src) {
                    nodenr = node.getIntValue("dnumber");
                }
                String tableName = typedef.getValue(getNodeType(nodenr));
                if (tableName != null) {
                    MMObjectBuilder nodeBuilder = mmb.getBuilder(tableName);
                    if (nodeBuilder != null && (nodeBuilder.equals(wantedBuilder) || nodeBuilder.isExtensionOf(wantedBuilder))) {
                        list.add(node);
                    }
                }
            }
            return list.elements();
        }
    }

    /**
     * Checks whether any relations exist for a MMObjectNode.
     * This includes unidirection relations which would otherwise not be counted.
     * If the query fails to execute, the system will assume that relations exists.
     * @param src Identifying number of the object to find the relations of.
     * @return <code>true</code> if any relations exist, <code>false</code> otherwise.
     */
    public boolean hasRelations(int src) {
        return count("WHERE snumber=" + src + " OR dnumber=" + src) != 0;
    }

    /**
     * Get all relation(s) for a MMObjectNode
     * This function returns all relations in which the node is either a source or
     * the destination (even if the direction is unidirectional).
     * @param src Identifying number of the object to find the relations of.
     * @return If succesful, a <code>Vector</code> containing the relations.
     *       Each element in the vector's enumeration is a node object retrieved from the
     *       associated table (i.e. 'insrel'), containing the node's fields.
     *       If no relations exist (or a database exception occurs), the method returns <code>null</code>.
     */
    public Vector getAllRelationsVector(int src) {
        return searchVector("WHERE snumber="+src+" OR dnumber="+src);
    }

    /**
     * Get all relation(s) for a MMObjectNode
     * This function returns all relations in which the node is either a source or
     * the destination (even if the direction is unidirectional).
     * @param src Identifying number of the object to find the relations of.
     * @param rnumber The number of the relation definition (role) to filter on
     * @return If succesful, a <code>Vector</code> containing the relations.
     *       Each element in the vector's enumeration is a node object retrieved from the
     *       associated table (i.e. 'insrel'), containing the node's fields.
     *       If no relations exist (or a database exception occurs), the method returns <code>null</code>.
     */
    public Vector getAllRelationsVector(int src, int rnumber) {
        if (rnumber==-1) {
            return getAllRelationsVector(src);
        } else {
            // assures that retrieved nodes are correct
            MMObjectBuilder builder=mmb.getRelDef().getBuilder(rnumber);
            return builder.searchVector("WHERE (snumber="+src+" OR dnumber="+src+") AND rnumber="+rnumber);
        }
    }

    /**
     * Get relation(s) for a MMObjectNode
     * This function returns all relations in which the node is either a source, or where the node is
     * the destination, but the direction is bidirectional.
     * @param src Identifying number of the object to find the relations of.
     * @return If succesful, a <code>Vector</code> containing the relations.
     *       Each element in the vector's enumeration is a node object retrieved from the
     *       associated table (i.e. 'insrel'), containing the node's fields.
     *       If no relations exist (or a database exception occurs), the method returns <code>null</code>.
     */
    public Vector getRelationsVector(int src) {
        if (usesdir) {
            return searchVector("WHERE snumber="+src+" OR (dnumber="+src+" and dir<>1)");
        } else {
            return searchVector("WHERE snumber="+src+" OR dnumber="+src);
        }
    }

    /**
     * Get relation(s) for a MMObjectNode, using a specified role (reldef) as a filter
     * This function returns all relations based on this role in which the node is either a source, or where the node is
     * the destination, but the direction is bidirectional.
     * @param src Identifying number of the object to find the relations of.
     * @param rnumber The number of the relation definition (role) to filter on
     * @return If succesful, a <code>Vector</code> containing the relations.
     *       Each element in the vector's enumeration is a node object retrieved from the
     *       associated table (i.e. 'insrel'), containing the node's fields.
     *       If no relations exist (or a database exception occurs), the method returns <code>null</code>.
     */
    public Vector getRelationsVector(int src, int rnumber) {
        if (rnumber==-1) {
            return getRelationsVector(src);
        } else {
            // assures that retrieved nodes are correct
            MMObjectBuilder builder=mmb.getRelDef().getBuilder(rnumber);
            if (usesdir) {
                return builder.searchVector("WHERE (snumber="+src+" OR (dnumber="+src+" and dir<>1)) AND rnumber="+rnumber);
            } else {
                return builder.searchVector("WHERE (snumber="+src+" OR dnumber="+src+") AND rnumber="+rnumber);
            }
        }
    }

    /**
     * Test whether a relation exists and returns the corresponding node.
     * Note that this test is strict: it determines whether a relation exists from a source to a destination
     * with a specific role. If only a role-relation exists where source and destination are reversed, this method
     * assumed that this is a different relation type, and it returns <code>null</code>.
     * @param snumber Identifying number of the source object
     * @param dnumber Identifying number of the destination object
     * @param rnumber Identifying number of the role (reldef)
     * @return The corresponding <code>MMObjectNode</code> if the exact relation exists,<code>null</code> otherwise
     */
    public MMObjectNode getRelation(int snumber, int dnumber, int rnumber) {
        Enumeration e=search("WHERE snumber="+snumber+" AND dnumber="+dnumber+" and rnumber="+rnumber);
        if (e.hasMoreElements()) {
            return (MMObjectNode)e.nextElement();
        }
        return null;
    }

    /**
    * get MMObjectNodes related to a specified MMObjectNode
    * @param sourceNode this is the source MMObjectNode
    * @param wtype Specifies the type of the nodes you want to have e.g. wtype="pools"
    */
    public Enumeration getRelated(String sourceNode,String wtype) {
        try {
            int src=Integer.parseInt(sourceNode);
            int otype=mmb.TypeDef.getIntValue(wtype);
            return getRelated(src,otype);
        } catch(Exception e) {}
        return null;
    }


    /**
    * get MMObjectNodes related to a specified MMObjectNode
    * @param src this is the number of the source MMObjectNode
    * @param wtype Specifies the type of the nodes you want to have e.g. wtype="pools"
    */
    public Enumeration getRelated(int src,String wtype) {
        try {
            int otype=-1;
            if (wtype!=null) {
                otype=mmb.TypeDef.getIntValue(wtype);
            }
            return getRelated(src,otype);
        } catch(Exception e) {}
        return null;
    }

    /**
    * Get MMObjectNodes of a specified type related to a specified MMObjectNode
    * @param src this is the number of the source MMObjectNode
    * @param otype the object type of the nodes you want to have
    * @return An <code>Enumeration</code> of <code>MMObjectNode</code> object related to the source
    */
    public Enumeration getRelated(int src,int otype) {
        Vector se=getRelatedVector(src,otype);
        if (se!=null) return se.elements();
        return null;
    }

    /**
    * get MMObjectNodes related to a specified MMObjectNode
    * @param sourceNode this is the number of the source MMObjectNode (in string format)
    * @param wtype Specifies the type of the nodes you want to have e.g. wtype="pools"
    * @param role the role of teh relation (name in reldef)
    */
    public Enumeration getRelated(String sourceNode,String wtype, String role) {
        try {
            int src=Integer.parseInt(sourceNode);
            int otype=mmb.getTypeDef().getIntValue(wtype);
            int rnumber=mmb.getRelDef().getNumberByName(role);
            return getRelated(src,otype,rnumber);
        } catch(Exception e) {}
        return null;
    }

    /**
    * get MMObjectNodes related to a specified MMObjectNode
    * @param src this is the number of the source MMObjectNode
    * @param wtype Specifies the type of the nodes you want to have e.g. wtype="pools"
    * @param role the role of teh relation (name in reldef)
    */
    public Enumeration getRelated(int src,String wtype, String role) {
        try {
            int otype=mmb.getTypeDef().getIntValue(wtype);
            int rnumber=mmb.getRelDef().getNumberByName(role);
            return getRelated(src,otype,rnumber);
        } catch(Exception e) {}
        return null;
    }

    /**
    * Get MMObjectNodes of a specified type related to a specified MMObjectNode
    * @param src this is the number of the source MMObjectNode
    * @param otype the object type of the nodes you want to have
    * @param rnumber Identifying number of the role (reldef)
    * @return An <code>Enumeration</code> of <code>MMObjectNode</code> object related to the source
    */
    public Enumeration getRelated(int src,int otype, int rnumber) {
        Vector se=getRelatedVector(src,otype,rnumber);
        if (se!=null) return se.elements();
        return null;
    }

    /**
    * Get MMObjectNodes related to a specified MMObjectNode
    * @param src this is the number of the MMObjectNode requesting the relations
    * @param otype the object type of the nodes you want to have. -1 means any node.
    * @return A <code>Vector</code> whose enumeration consists of <code>MMObjectNode</code> object related to the source
    *   according to the specified filter(s).
    * @deprecated
    **/
    public Vector getRelatedVector(int src,int otype) {
        return getRelatedVector(src,otype,-1);
    }

    /**
     * Get MMObjectNodes related to a specified MMObjectNode
     * @param src this is the number of the MMObjectNode requesting the relations
     * @param otype the object type of the nodes you want to have. -1 means any node.
     * @param rnumber Identifying number of the role (reldef)
     * @return A <code>Vector</code> whose enumeration consists of <code>MMObjectNode</code> object related to the source
     *   according to the specified filter(s).
    * @deprecated
     */
    public Vector getRelatedVector(int src,int otype, int rnumber) {
        Vector list=null;
        if (rnumber==-1) {
            list=(Vector)relatedCache.get(new Integer(src));
        }
        if (list==null) {
            list=new Vector();
            MMObjectNode node,node2;
            int nodenr = -1;
            for(Enumeration e=getRelations(src,rnumber); e.hasMoreElements(); ) {
                    node=(MMObjectNode)e.nextElement();
                    nodenr=node.getIntValue("snumber");
                    if (nodenr==src) {
                        nodenr=node.getIntValue("dnumber");
                    }
                    node2=getNode(nodenr);
                    if(node2!=null) {
                        list.addElement(node2);
                    }
            }
            if (rnumber==-1) {
                relatedCache.put(new Integer(src),list);
            }
        }
        // oke got the Vector now lets get the correct otypes

        Vector results = null;
        if (otype==-1) {
            results=new Vector(list);
        } else {
            results=new Vector();
            for (Enumeration e=list.elements();e.hasMoreElements();) {
                MMObjectNode node=(MMObjectNode)e.nextElement();
                if (node.getOType()==otype) {
                    results.addElement(node);
                }
            }
        }
        return results;
    }
    public String getGUIIndicator(MMObjectNode node) {
        return node.getStringValue("snumber") + "->" + node.getStringValue("dnumber");
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
            if (field.equals("dir")) {
                int dir=node.getIntValue("dir");
                if (dir == 2) {
                    return "bidirectional";
                } else if (dir==1) {
                    return "unidirectional";
                } else {
                    return "unknown";
                }
            } else if (field.equals("snumber")) {
                MMObjectNode node2 = getNode(node.getIntValue("snumber"));
                String ty = "=" + mmb.getTypeDef().getValue(node2.getOType());
                if (node2 != null) {
                    return "" + node.getIntValue("snumber") + ty + "(" + node2.getGUIIndicator()+")";
                }
            } else if (field.equals("dnumber")) {
                MMObjectNode node2 = getNode(node.getIntValue("dnumber"));
                String ty = "=" + mmb.getTypeDef().getValue(node2.getOType());
                if (node2 != null) {
                    return "" + node.getIntValue("dnumber") + ty + "(" + node2.getGUIIndicator() + ")";
                }
            } else if (field.equals("rnumber")) {
                MMObjectNode node2 = mmb.getRelDef().getNode(node.getIntValue("rnumber"));
                return ""+node.getIntValue("rnumber")+"="+node2.getGUIIndicator();
            }
        } catch (Exception e) {
        }
        return null;
    }

    /**
    * Checks whether a specific relation exists.
    * Maintains a cache containing the last checked relations
    *
    * Note that this routine returns false both when a snumber/dnumber are swapped, and when a typecombo
    * does not exist -  it is not possible to derive whether one or the other has occurred.
    *
    * @param n1 Number of the source node
    * @param n2 Number of the destination node
    * @param r  Number of the relation definition
    * @return A <code>boolean</code> indicating success when the relation exists, failure if it does not.
    * @deprecated Use {@link TypeRel#reldefCorrect} instead
    */
    public boolean reldefCorrect(int n1,int n2, int r) {
        return mmb.getTypeRel().reldefCorrect(n1,n2,r);
    }

    /**
    * Deletes the Relation cache.
    * This is to be called if caching gives problems.
    * Make sure that you can't use the deleteRelationCache(int src) instead.
    **/
    public void deleteRelationCache() {
        relatedCache.clear();
    }

    /**
    * Delete a specified relation from the relationCache
    * @param src the number of the relation to remove from the cache
    **/
    public void deleteRelationCache(int src) {
        relatedCache.remove(new Integer(src));
    }

    /**
    * Search the relation definition table for the identifying number of
    * a relation, by name.
    * Success is dependent on the uniqueness of the relation's name (not enforced, so unpredictable).
    * @param name The name on which to search for the relation
    * @return A <code>int</code> value indicating the relation's object number, or -1 if not found.
    **/
    public int getGuessedNumber(String name) {
        RelDef reldef=mmb.getRelDef();
        if (reldef!=null) {
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
        if (tableName.equals("insrel")) return;

        if (relnumber==-1) {
            MMObjectNode n=mmb.getRelDef().getDefaultForBuilder(this);
            if (n==null) {
                log.warn("Can not determine default reldef for ("+getTableName()+")");
            } else {
                relnumber=n.getNumber();
            }
        }
        node.setValue("rnumber",relnumber);
    }
}
