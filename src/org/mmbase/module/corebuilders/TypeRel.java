/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.corebuilders;

import java.util.*;
import java.sql.*;
import org.mmbase.util.*;
import org.mmbase.module.core.*;
import org.mmbase.module.database.*;
import org.mmbase.module.ParseException;

/**
 * TypeRel, one of the meta stucture nodes it is used to define the
 * allowed relations between two object types this is used by editors
 * and other software to see/enforce the wanted structure
 *
 * @author Daniel Ockeloen
 * @version 12 Mar 1997
 */
public class TypeRel extends MMObjectBuilder {

	// Cache, holds the last 128 verified type-relation nodes
	
	LRUHashtable artCache			=new LRUHashtable(128);

	/**
	* Constructor for the TypeRel builder
	*/
	public TypeRel() {
	}

	/**
	*  Retrieves all relations which are 'allowed' for a specified node, that is,
	*  where the node is either allowed to be the source, or to be the destination (but where the
	*  corresponing relation definition is bi-directional). The allowed relations are determined by
	*  the type of the node
	*  @param mmnode The node to retrieve the allowed relations of.
	*  @return An <code>Enumeration</code> of nodes containing the typerel relation data
	*/
	public Enumeration getAllowedRelations(MMObjectNode mmnode) {
		int number=mmnode.getIntValue("otype");
	    return getAllowedRelations(number);
	}

	/**
	*  Retrieves all relations which are 'allowed' for a specified node, that is,
	*  where the node is either allowed to be the source, or to be the destination (but where the
	*  corresponing relation definition is bi-directional). The allowed relations are determined by
	*  the type of the node
	*  @param number The number of the node to retrieve the allowed relations of.
	*  @return An <code>Enumeration</code> of nodes containing the typerel relation data
	*/
	public Enumeration getAllowedRelations(int number) {
	    // XXX add directionality (and caching ?)
	    // note: adding dir means we cannot use search!
	    return search("WHERE snumber="+number+" OR dnumber="+number);
	}

    /**
    *  Retrieves all relations which are 'allowed' between two specified nodes.
    *  @param n1 The first objectnode (the source)
    *  @param n2 The second objectnode (the destination)
    *  @return An <code>Enumeration</code> of nodes containing the typerel relation data
    */
    public Enumeration getAllowedRelations(int snum, int dnum) {
	    // XXX add directionality (and caching ?)
	    // note: adding dir means we cannot use search!
        return search("WHERE (snumber="+snum+" AND dnumber="+dnum+") OR (dnumber="+snum+" AND snumber="+dnum+")");
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
	*  Retrieves all reldef node numbers for relations which are 'allowed' between two specified nodes.
	*  @param n1 The number of the first objectnode (the source)
	*  @param n2 The number of the second objectnode (the destination)
	*  @return A <code>Vector</code> of Integers containing the reldef object node numbers
	*/
	public Vector getAllowedRelationsTypes(int snum,int dnum) {
	    Vector result= new Vector();
		for(Enumeration e=getAllowedRelations(snum,dnum); e.hasMoreElements();) {
		    MMObjectNode node=(MMObjectNode)e.nextElement();
		    int rnumber=node.getIntValue("rnumber");
			MMObjectNode snode=mmb.getRelDef().getNode(rnumber);
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
		int snum=n1.getIntValue("otype");
		int dnum=n2.getIntValue("otype");
		return getAllowedRelations(snum,dnum);
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
    *  Processes the BUILDER-typerel-ALLOWEDRELATIONSNAMES in the LIST command, and (possibly) returns a Vector containing
    *  requested data (based on the content of TYPE and NODE, which can be retrieved through tagger).
    */
    public Vector getList(scanpage sp, StringTagger tagger, StringTokenizer tok) throws ParseException {
        if (tok.hasMoreTokens()) {
            String cmd=tok.nextToken();	//Retrieving command.
            if (cmd.equals("ALLOWEDRELATIONSNAMES")) {
                try {
                    String tmp=tagger.Value("TYPE");
                    int number1=mmb.getTypeDef().getIntValue(tmp);
                    tmp=tagger.Value("NODE");
                    int number2=Integer.parseInt(tmp);
                    MMObjectNode node=getNode(number2);
                    return getAllowedRelationsNames(number1,node.getIntValue("otype"));
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
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
