/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.corebuilders;

import java.util.*;
import org.mmbase.util.*;
import org.mmbase.module.core.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * TypeRel defines the allowed relations between two object types.
 *
 * @author Daniel Ockeloen
 * @author Pierre van Rooden
 * @version 2 jan 2001
 */
public class TypeRel extends MMObjectBuilder {

    private static Logger log = Logging.getLoggerInstance(TypeRel.class.getName());

    /**
     * Maximum size of the Allowed Relation Type (ART) Cache
     * @bad-constant should be configurable, possibly using builder properties?
     */
    private static final int ART_CACHE_SIZE = 128;
    /**
     * Default size of the relation definition cache
     * @bad-constant should be configurable, possibly using builder properties?
     */
    private static final int RELDEF_CACHE_SIZE = 128;

    /**
     * Allowed Relation Type (ART) Cache, holds the last 128 verified type-relation nodes
     * @duplicate similar to the {@link #relDefCorrectCache}
     */
    private LRUHashtable artCache=new LRUHashtable(ART_CACHE_SIZE);

    /**
     * Cache table that holds yes/no if a relation direction
     * question was correct or not.
     * This is needed to make sure that a relation is correctly inserted.
     * @duplicated cache mechanisms should be implemented in org.mmbase.cache
     */
    private Hashtable relDefCorrectCache=new Hashtable(RELDEF_CACHE_SIZE);

    /**
     * Constructor for the TypeRel builder
     */
    public TypeRel() {
    }

    /**
     * Insert a new object (content provided) in the cloud, including an entry for the object alias (if provided).
     * This method indirectly calls {@link #preCommit}.
     * @param owner The administrator creating the node
     * @param node The object to insert. The object need be of the same type as the current builder.
     * @return An <code>int</code> value which is the new object's unique number, -1 if the insert failed.
     */
    public int insert(String owner, MMObjectNode node) {
        int snumber=node.getIntValue("snumber");
        int dnumber=node.getIntValue("dnumber");
        int result=super.insert(owner,node);
        // remove from cache, to catch multiple relations between types
        artCache.remove(""+snumber+" "+dnumber);
        return result;
    }

    /**
     * Remove a node from the cloud.
     * @param node The node to remove.
     */
    public void removeNode(MMObjectNode node) {
        int snumber=node.getIntValue("snumber");
        int dnumber=node.getIntValue("dnumber");
        int rnumber=node.getIntValue("rnumber");
        super.removeNode(node);
        relDefCorrectCache.remove(""+snumber+" "+dnumber+" "+rnumber);
        artCache.remove(""+snumber+" "+dnumber);
    }

    /**
     *  Retrieves all relations which are 'allowed' for a specified node, that is,
     *  where the node is either allowed to be the source, or to be the destination (but where the
     *  corresponing relation definition is bidirectional). The allowed relations are determined by
     *  the type of the node
     *  @param mmnode The node to retrieve the allowed relations of.
     *  @return An <code>Enumeration</code> of nodes containing the typerel relation data
     */
    public Enumeration getAllowedRelations(MMObjectNode mmnode) {
        int number=mmnode.getOType();
        return getAllowedRelations(number);
    }

    /**
     * Removes all invalid relation type nodes from a list.
     * This removes all relation types where the requesting node is actually the destination, and where
     * the directionality is unidirectional.
     * @param e the original list of relation types
     * @param number the numbe rof the requesting node
     * @return a 'clean' enumeration of relation types
     */
    private Enumeration clearDirectedRelations(Enumeration e, int number) {
        // only check when directionality field exist?
        // -> makes sure older code works the same, maybe remove later
        if (!InsRel.usesdir) return e;

        Vector result= new Vector();
        while (e.hasMoreElements()) {
            MMObjectNode node=(MMObjectNode)e.nextElement();
            if (number==node.getIntValue("snumber")) { // requesting node is the source, add
                result.add(node);
             } else {
                int reldefnr=node.getIntValue("rnumber");
                MMObjectNode reldefnode = getNode(reldefnr); // obtain reldefnode
                if ((reldefnode!=null) && (reldefnode.getIntValue("dir")!=1)) {  // relation is bidirectional, add
                    result.add(node);
               }
            }
        }
        return(result.elements());
    }

    /**
     *  Retrieves all relations which are 'allowed' for a specified node, that is,
     *  where the node is either allowed to be the source, or to be the destination (but where the
     *  corresponding relation definition is bidirectional). The allowed relations are determined by
     *  the type of the node
     *  @param number The number of the node to retrieve the allowed relations of.
     *  @return An <code>Enumeration</code> of nodes containing the typerel relation data
     */
    public Enumeration getAllowedRelations(int number) {
        Enumeration e = search("WHERE snumber="+number+" OR dnumber="+number);
        return clearDirectedRelations(e, number);
    }

    /**
     *  Retrieves all relations which are 'allowed' between two specified nodes.
     *  @param n1 The first objectnode (the source)
     *  @param n2 The second objectnode (the destination)
     *  @return An <code>Enumeration</code> of nodes containing the typerel relation data
     */
    public Enumeration getAllowedRelations(int snum, int dnum) {
        Enumeration e = search("WHERE (snumber="+snum+" AND dnumber="+dnum+") OR (dnumber="+snum+" AND snumber="+dnum+")");
        return clearDirectedRelations(e, snum);
    }

    /**
     *  Retrieves the identifying number of the relation definition that is 'allowed' between two specified node types.
     *  The results are dependent on there being only one type of relation between two node types (not enforced, thus unpredictable).
     *  Makes use of a cache.
     *  @param snum The first objectnode type (the source)
     *  @param dnum The second objectnode type (the destination)
     *  @return the number of the found relation, or -1 if either no relation was found, or more than one was found.
     */
    public int getAllowedRelationType(int snum,int dnum) {
        // putting a cache here is silly but makes editor faster !
        Integer i=(Integer)artCache.get(""+snum+" "+dnum);
        if (i!=null) return i.intValue();
        Enumeration e=getAllowedRelationTypes(snum,dnum).elements();
        if (e.hasMoreElements()) {
            Integer j=(Integer)e.nextElement();
            if (e.hasMoreElements()) {
                j=new Integer(-1);
            }
            artCache.put(""+snum+" "+dnum,j);
            return j.intValue();
        }
        return -1;
    }

    /**
     *  Retrieves all reldef node numbers for relations which are 'allowed' between two specified nodes.
     *  @param n1 The number of the first objectnode (the source)
     *  @param n2 The number of the second objectnode (the destination)
     *  @return A <code>Vector</code> of Integers containing the reldef object node numbers
     */
    public Vector getAllowedRelationTypes(int snum,int dnum) {
        Vector result= new Vector();
        for(Enumeration e=getAllowedRelations(snum,dnum); e.hasMoreElements();) {
            MMObjectNode node=(MMObjectNode)e.nextElement();
            int j=node.getIntValue("rnumber");
            result.addElement( new Integer(j) );
        }
        return(result);
    }

    /**
     *  For use with MultiRelations
     *  Retrieves all reldef nodes for relations which are 'allowed' between two specified nodes.
     *  @param n1 The number of the first objectnode (the source)
     *  @param n2 The number of the second objectnode (the destination)
     *  @return A <code>Vector</code> of Integers containing the reldef object node numbers
     */
    public Vector getAllowedRelationsTypes(int snum,int dnum) {
        Vector result= new Vector();
        for(Enumeration e=getAllowedRelations(snum,dnum); e.hasMoreElements();) {
            MMObjectNode node=(MMObjectNode)e.nextElement();
            int rnumber=node.getIntValue("rnumber");
            MMObjectNode snode=getNode(rnumber);
            result.addElement(snode);
        }
        return(result);
    }

    /**
     *  Retrieves all relations which are 'allowed' between two specified nodes.
     *  @param n1 The first objectnode (the source)
     *  @param n2 The second objectnode (the destination)
     *  @return An <code>Enumeration</code> of nodes containing the typerel relation data
     */
    public Enumeration getAllowedRelations(MMObjectNode n1,MMObjectNode n2) {
        int snum=n1.getOType();
        int dnum=n2.getOType();
        return getAllowedRelations(snum,dnum);
    }

    /**
     *  Returns the display string for this node
     *  It returns a commbination of objecttypes and rolename : "source->destination (role)".
     *  @param node Node from which to retrieve the data
     *  @return A <code>String</code> describing the content of the node
     */
    public String getGUIIndicator(MMObjectNode node) {
        try {
            return mmb.getTypeDef().getValue(node.getIntValue("snumber"))+
                   "->"+mmb.getTypeDef().getValue(node.getIntValue("dnumber"))+
                   " ("+mmb.getRelDef().getNode(node.getIntValue("rnumber")).getGUIIndicator()+")";
        } catch (Exception e) {}
        return null;
    }

    /**
     *  Returns the display string for a specified field.
     *  Returns, for snumber and dnumber, the name of the objecttype they represent, and for
     *  rnumber the display (GUI) string for the indicated relation definition.
     *  @param field The name of the field to retrieve
     *  @param node Node from which to retrieve the data
     *  @return A <code>String</code> describing the content of the field
     */
    public String getGUIIndicator(String field, MMObjectNode node) {
        try {
            if (field.equals("snumber")) {
                return mmb.getTypeDef().getValue(node.getIntValue("snumber"));
            } else if (field.equals("dnumber")) {
                return mmb.getTypeDef().getValue(node.getIntValue("dnumber"));
            } else if (field.equals("rnumber")) {
                MMObjectNode node2=mmb.getRelDef().getNode(node.getIntValue("rnumber"));
                return node2.getGUIIndicator();
            }
        } catch (Exception e) {}
        return null;
    }

    /**
     * Processes the BUILDER-typerel-ALLOWEDRELATIONSNAMES in the LIST command, and (possibly) returns a Vector containing
     * requested data (based on the content of TYPE and NODE, which can be retrieved through tagger).
     * @javadoc parameters
     */
    public Vector getList(scanpage sp, StringTagger tagger, StringTokenizer tok) {
        if (tok.hasMoreTokens()) {
            String cmd=tok.nextToken();	//Retrieving command.
            if (cmd.equals("ALLOWEDRELATIONSNAMES")) {
                try {
                    String tmp=tagger.Value("TYPE");
                    int number1=mmb.getTypeDef().getIntValue(tmp);
                    tmp=tagger.Value("NODE");
                    int number2=Integer.parseInt(tmp);
                    MMObjectNode node=getNode(number2);
                    return getAllowedRelationsNames(number1,node.getOType());
                } catch(Exception e) {
                    log.error(Logging.stackTrace(e));
                }
            }
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
     */
    public boolean reldefCorrect(int n1,int n2, int r) {
        // do the query on the database
        Boolean b=(Boolean)relDefCorrectCache.get(""+n1+" "+n2+" "+r);
        if (b!=null) {
            return b.booleanValue();
        } else {
            Vector v=searchNumbers("WHERE snumber="+n1+" AND dnumber="+n2+" AND rnumber="+r);
            if (v.size()>0) {
                relDefCorrectCache.put(""+n1+" "+n2+" "+r,new Boolean(true));
                return true;
            } else {
                v=searchNumbers("WHERE dnumber="+n1+" AND snumber="+n2+" AND rnumber="+r);
                if (v.size()>0) {
                    relDefCorrectCache.put(""+n1+" "+n2+" "+r,new Boolean(false));
                }
            }
            return false;
        }
    }

    /**
     *  Retrieves all relations which are 'allowed' between two specified nodes.
     *  @param snum The first objectnode type (the source)
     *  @param dnum The second objectnode type (the destination)
     *  @return An <code>Enumeration</code> of nodes containing the reldef (not typerel!) sname field
     */
    public Vector getAllowedRelationsNames(int number1,int number2) {
        Vector results=new Vector();
        for(Enumeration e=getAllowedRelations(number1, number2); e.hasMoreElements();) {
            MMObjectNode node=(MMObjectNode)e.nextElement();
            int rnumber=node.getIntValue("rnumber");
            MMObjectNode snode=mmb.getRelDef().getNode(rnumber);
            results.addElement(snode.getStringValue("sname"));
        }
        return results;
    }
}
