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
 * @version $Id: TypeRel.java,v 1.25 2002-04-09 07:02:43 kees Exp $
 */
public class TypeRel extends MMObjectBuilder implements MMBaseObserver {

    private static Logger log = Logging.getLoggerInstance(TypeRel.class.getName());

    /**
     * Maximum size of the Allowed Relation Type (ART) Cache
     */
    private int art_Cache_Size = 128;

    /**
     * Default size of the relation definition cache
     */
    private int reldef_Cache_Size = 128;

    /**
     * Initial size of the relationTypes hashtable adjust appropiate to your
     * installation
     */
    private int reltype_Cache_Size = 128;

    /**
     * Allowed Relation Type (ART) Cache, holds the last 128 verified type-relation nodes
     * @duplicate similar to the {@link #relDefCorrectCache}
     */
    private LRUHashtable artCache;

    /**
     * Cache table that holds yes/no if a relation direction
     * question was correct or not.
     * This is needed to make sure that a relation is correctly inserted.
     * @duplicated cache mechanisms should be implemented in org.mmbase.cache
     */
    private Hashtable relDefCorrectCache;

    /**
     * Cache table that holds all relation types
     * This avoids having to go to the database every time this is
     * requested through <code>getAllowedRelations</code>
     * @duplicated This might need to move to org.mmbase.cache but this might be to difficult due
     * @duplicated to the nature of the structure (Vector in Hashtable in Hashtable)
     */
    private Hashtable relationTypes;

    /**
     * Enable memory based TypeRel querying or not.
	 * default on, can be switched to off in typerel.xml
     */
    private boolean memTableActive=true;

    /**
     * Tracks if the memorytable has been initialized
     */
    private boolean memTableDone=false;

    /**
     * Count how many times init is called, we must used second call because otherwise InsRel isn't ready
     * and doesn't know its directionality value
     */
    private int memTableCount=0;

    /**
     * Constructor for the TypeRel builder
     */
    public TypeRel() {
    }

    /**
     * Initialize this builder and load the caches
     */
    public boolean init() {
        log.debug("Init of TypeRel");
        super.init();

        // Read parameters set in XML file
        int val;
        String str;
        str=getInitParameter("art_cache_size");
        if (str!=null && str.length()>0) {
            val=Integer.parseInt(str);
            art_Cache_Size=val;
        }
		artCache=new LRUHashtable(art_Cache_Size);

        str=getInitParameter("reldef_cache_size");
        if (str!=null && str.length()>0) {
            val=Integer.parseInt(str);
            reldef_Cache_Size=val;
        }
		relDefCorrectCache=new Hashtable(reldef_Cache_Size);

        str=getInitParameter("reltype_cache_size");
        if (str!=null && str.length()>0) {
            val=Integer.parseInt(str);
            reltype_Cache_Size=val;
        }
		relationTypes=new Hashtable(reltype_Cache_Size);

        str=getInitParameter("reltype_cache_active");
        if (str!=null && str.length()>0) {
            if (str.toUpperCase().equals("TRUE") || str.toUpperCase().equals("YES")) memTableActive=true;
            else memTableActive=false;
        }
        log.info("Memory Table usage for getAllowedRelations is "+memTableActive);

        if (memTableActive && relationTypes.size()==0 && memTableCount==1) {
            readRelationTypes();
        }
        memTableCount++;
        return(true);
    }

    /**
     * Load the relationTypes table
     * The structure created here is a Hashtable keyed on source tabletype (otype)
     * containing a Hashtable keyed on the destination type. This hashtable contains
     * a Vector containing the typerels.
     * This way you can retrieve all typerels for a type with 1 hashtable lookup
     * and specific typerels with 2 lookups. The Vector is needed for duplicate relations
     * between the same types
     */
    private void readRelationTypes() {
        Enumeration alltypes;
        MMObjectNode reltype;
        
        log.debug("Reading in relation types");
        // Find all typerel nodes
        alltypes=search("WHERE 1=1");
        while(alltypes.hasMoreElements()) {
            // For every reltype node :
            reltype=(MMObjectNode)alltypes.nextElement();
            addRelationType(reltype);
        }
        log.debug("Done reading in relation types");
        if (log.isDebugEnabled()) {
            Enumeration x;
            Hashtable h;
            Integer i;
            StringBuffer b=new StringBuffer();

            b.append("[\n");
            for (x=relationTypes.keys();x.hasMoreElements();) {
                i=(Integer)x.nextElement();
                h=(Hashtable)relationTypes.get(i);
                b.append("("+i+"="+h+")\n");
            }
            b.append("]");
            log.trace("TypeRel : Hashtable "+b.toString());
        }
        memTableDone=true;
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
     * @param number the number of the requesting node
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
        Enumeration typerelEnum;

        if (memTableActive && memTableDone) {
            typerelEnum = getAllowedRelationsTable(number);
        } else {
            Enumeration e = search("WHERE snumber="+number+" OR dnumber="+number);
            typerelEnum=clearDirectedRelations(e, number);
        }
        return typerelEnum;
    }

    /**
     * Memory version of getAllowedRelations
     */
    public Enumeration getAllowedRelationsTable(int number) {
        Vector res,v;
        Hashtable h;

        res=new Vector();
        h=(Hashtable)relationTypes.get(new Integer(number));
        if (h!=null) {
            for (Enumeration e=h.elements();e.hasMoreElements();) {
                v=(Vector)e.nextElement();
                res.addAll(v);
            }
        }
        log.trace("Result for "+number+" is "+res);
        return(res.elements());
    }

    /**
     *  Retrieves all relations which are 'allowed' between two specified nodes.
     *  @param n1 The first objectnode (the source)
     *  @param n2 The second objectnode (the destination)
     *  @return An <code>Enumeration</code> of nodes containing the typerel relation data
     */
    public Enumeration getAllowedRelations(int snum, int dnum) {
        Enumeration e,f;
        long l1,l2;

        l1=System.currentTimeMillis();
        if (memTableActive && memTableDone) {
            f = getAllowedRelationsTable(snum,dnum);
        } else {
            e = search("WHERE (snumber="+snum+" AND dnumber="+dnum+") OR (dnumber="+snum+" AND snumber="+dnum+")");
               f=clearDirectedRelations(e, snum);
            if (log.isDebugEnabled()) f=printEnum(f);
        }
        l2=System.currentTimeMillis();
        log.info("Time : "+(l2-l1));
        return(f);
    }

    /**
     * Memory version of getAllowedRelations
     */
    public Enumeration getAllowedRelationsTable(int snum,int dnum) {
        Vector res,lev2;
        Hashtable h;

        res=new Vector();
        h=(Hashtable)relationTypes.get(new Integer(snum));
        if (h!=null) {
            lev2=(Vector)h.get(new Integer(dnum));
            if (lev2!=null) {
                res.addAll(lev2);
            }
        }
        log.trace("Result for "+snum+":"+dnum+" is "+res);
        return(res.elements());
    }

    private Enumeration printEnum(Enumeration e) {
        Vector v=new Vector();
        MMObjectNode node;
        StringBuffer b=new StringBuffer();
        b.append("[");
        while (e.hasMoreElements()) {
            node=(MMObjectNode)e.nextElement();
            v.addElement(node);
            b.append("+"+node);
        }
        b.append("]");
        log.trace("Enumeration "+b.toString());
        return(v.elements());
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
            String cmd=tok.nextToken();    //Retrieving command.
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

    public boolean nodeRemoteChanged(String machine,String number,String builder,String ctype) {
        super.nodeRemoteChanged(machine,number,builder,ctype);
        return(nodeChanged(machine,number,builder,ctype));
    }

    public boolean nodeLocalChanged(String machine,String number,String builder,String ctype) {
        super.nodeLocalChanged(machine,number,builder,ctype);
        return(nodeChanged(machine,number,builder,ctype));
    }

    /**
     * Watch for changes on relation types and adjust our memory table accordingly
	 * @todo Should update artCache en relDefCorrectCache as wel 
     */
    public boolean nodeChanged(String machine,String number,String builder,String ctype) {
        log.debug("Seeing change on "+number+" : "+ctype);
        if (builder.equals(getTableName())) {
            if (ctype.equals("d")) {
                removeRelationType(Integer.parseInt(number));
            } else if (ctype.equals("r")) {
                // do nothing
            } else if (ctype.equals("c")) {
                updateRelationType(Integer.parseInt(number));
            } else if (ctype.equals("n")) {
                addRelationType(Integer.parseInt(number));
            } else {
                log.info("Unknown type received "+ctype);
            }
        }
        return(true);
    }

    /**
     * Remove a relation type from the internal table 
     */
    private void removeRelationType(int number) {
        Hashtable level2;
        Vector v1;
        MMObjectNode node;
        int idx=0;
        boolean remove=false;

        for (Enumeration lev1=relationTypes.elements();lev1.hasMoreElements();) {
            level2=(Hashtable)lev1.nextElement();
            for (Enumeration lev2=level2.elements();lev2.hasMoreElements();) {
                v1=(Vector)lev2.nextElement();
                log.debug("Looking at Vector "+v1);
                // Node is already can't use getNode so we use the stupid way;
                remove=false;
                for (idx=0;idx<v1.size();idx++) {
                    node=(MMObjectNode)v1.elementAt(idx);
                    if (node.getNumber()==number) {
                        remove=true;
                        break;
                    }
                }
                log.debug("Done looking at Vector "+idx+" - "+remove);
                if (remove) {
                    v1.removeElementAt(idx);
                }
            }
        }
    }

    /**
     * Update a relation type in our internal table
     */
    private void updateRelationType(int number) {
        Hashtable level2;
        Vector v1;
        MMObjectNode node;
        int idx=0;
        boolean update=false;

        for (Enumeration lev1=relationTypes.elements();lev1.hasMoreElements();) {
            level2=(Hashtable)lev1.nextElement();
            for (Enumeration lev2=level2.elements();lev2.hasMoreElements();) {
                v1=(Vector)lev2.nextElement();
                log.debug("Looking at Vector "+v1);
                update=false;
                for (idx=0;idx<v1.size();idx++) {
                    node=(MMObjectNode)v1.elementAt(idx);
                    if (node.getNumber()==number) {
                        update=true;
                        break;
                    }
                }
                log.debug("Done looking at Vector "+idx+" - "+update);
                if (update) {
                    node=getNode(number);
                    v1.setElementAt(node,idx);
                }
            }
        }
    }

    /**
     * Add a relation type to our internal table
     * This is a callthrough to the real function 
     */
    private void addRelationType(int number) {
        MMObjectNode node;
        node=getNode(number);
        if (node!=null) addRelationType(node);
        else log.info("Node "+number+" doesn't exist");
    }

    /**
     * Add a relation type to our internal table
     * This is the one used to fill the internal table.
     */
    private void addRelationType(MMObjectNode reltype) {
        Hashtable level2;
        Vector reltypes;
        MMObjectNode reldefnode;
        Integer snumber,dnumber;
        boolean doubledirection;
        int reldefnr;

        snumber=reltype.getIntegerValue("snumber");
        dnumber=reltype.getIntegerValue("dnumber");

        log.trace("Processing "+reltype);

        // Add to table indexed on source type
        log.trace("Doing forward "+snumber);
        addType(snumber,dnumber,reltype);

        // Check if we are running in directional mode.
        if (InsRel.usesdir) {
            reldefnr=reltype.getIntValue("rnumber");
            reldefnode = getNode(reldefnr); // obtain reldefnode
            // Only add to reverse when the reltype is bidirectional
                if ((reldefnode!=null) && (reldefnode.getIntValue("dir")!=1)) {
                doubledirection=true;
            } else {
                doubledirection=false;
            }
        } else {
            doubledirection=true;
        }

        // Only add reversewhen both directions are allowed
        if (doubledirection) {
            log.trace("Doing reverse "+dnumber);
            addType(dnumber,snumber,reltype);
        }
    }

    /**
     * Add a reltype in our Hashtable,Hashtable,Vector structure
     */
    private void addType(Integer lev1,Integer lev2,MMObjectNode reltype) {
        Hashtable level2;
        Vector reltypes;

        // Find 2nd level table 
        level2=(Hashtable)relationTypes.get(lev1);
        if (level2==null) {
            level2=new Hashtable();
            reltypes=new Vector();
            level2.put(lev2,reltypes);
            relationTypes.put(lev1,level2);
        } else {
            // Fetch 2nd level vector
            reltypes=(Vector)level2.get(lev2);
            if (reltypes==null) {
                reltypes=new Vector();
                level2.put(lev2,reltypes);
            }
        }
        // Only add if it is not in the list
        if (!reltypes.contains(reltype)) reltypes.addElement(reltype);
    }

}
