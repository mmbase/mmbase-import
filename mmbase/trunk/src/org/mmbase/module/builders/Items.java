/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.module.builders;

import java.util.*;
import java.sql.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;
import org.mmbase.hitlisted.*;

/**
 * @author Arjan Houtman
 */
public class Items extends MMObjectBuilder {

	Hashtable items_cache = new Hashtable ();
	LRUHashtable itemsInfoCache = new LRUHashtable (10);
	int lastprg;
	Vector checklist;

	public String getGUIIndicator (String field, MMObjectNode node) {
		if (field.equals ("medium")) {
			int val = node.getIntValue ("medium");
			if (val == 1) {
				return ("Televisie");
			} else if (val == 2) {
				return ("Radio");
			}
		} else if (field.equals ("format")) {
			int val = node.getIntValue ("format");
			return (POPconst.getFormat (val));
		}
		return (null);
	}

	public Object getValue (MMObjectNode node, String field) {
		if (field.indexOf ("html_") == 0) {
			String val = node.getStringValue (field.substring (5));
			val = getHTML (val);
			return (val);
		} else if (field.equals ("next")) {
			int str = node.getIntValue ("itemNr");
			// hunt the relations with programs !!! to find programNr
			Enumeration e = mmb.getInsRel ().getRelated (node.getIntValue ("number"), 873);
			if (e.hasMoreElements ()) {
				MMObjectNode prgnode = (MMObjectNode)e.nextElement ();  
				if (prgnode != null) {
					int prgnr = prgnode.getIntValue ("number");
					Vector se = (Vector)items_cache.get ("" + prgnr);
					if (se == null || se.size () == 0) {
						se = mmb.getInsRel ().getRelatedVector (prgnr, 889);
						items_cache.put ("" + prgnr, se);
						System.out.println ("Items -> Cache miss");
					}
					e = se.elements ();
					for (;e.hasMoreElements ();) {
						MMObjectNode itemnode = (MMObjectNode)e.nextElement ();
						if (itemnode.getIntValue ("itemNr") == (str + 1)) {
							return("" + itemnode.getIntValue ("number"));
						}
					}	
				}
			} 
			return ("0");
		} else if (field.equals ("prev")) {
			int str = node.getIntValue ("itemNr");
			// hunt the relations with programs !!! to find programNr
			Enumeration e = mmb.getInsRel ().getRelated (node.getIntValue ("number"), 873);
			if (e.hasMoreElements ()) { 
				MMObjectNode prgnode = (MMObjectNode)e.nextElement ();
				if (prgnode != null) {
					int prgnr = prgnode.getIntValue ("number");
					Vector se = (Vector)items_cache.get ("" + prgnr);
					if (se == null || se.size () == 0) {
						se = mmb.getInsRel ().getRelatedVector (prgnr, 889);
						items_cache.put ("" + prgnr, se);
						System.out.println ("Items -> Cache miss");
					} 
					e = se.elements ();
					for (;e.hasMoreElements ();) {
						MMObjectNode itemnode = (MMObjectNode)e.nextElement ();
						if (itemnode.getIntValue ("itemNr") == (str - 1)) {
							return("" + itemnode.getIntValue ("number"));
						}
					}	
				}
			} 
			return ("0");
		} else if (field.equals ("events")) {
			// Not Called ever ?????????
			System.out.println("getItems ????");
					int prgnr = -1;
					// hairy hack is last program the one with me (the item)
					// as one of its relations
					if (checklist != null && checklist.contains (node.getIntegerValue ("number"))) {
							// it was in this list so same prgnr
							 prgnr = lastprg;
					} else {
						// make new checklist
						Enumeration t = mmb.getInsRel ().getRelated (node.getIntValue ("number"), 873);
						//Enumeration t = node.getRelations (873);
						if (t.hasMoreElements ()) {
							MMObjectNode prgnode = (MMObjectNode)t.nextElement ();
							prgnr = prgnode.getIntValue ("number");
							lastprg = prgnr;
							// fill the new checklist
							Enumeration t3 = mmb.getInsRel ().getRelated (prgnode.getIntValue ("number"), 889);
							//Enumeration t3 = prgnode.getRelations (889);
							checklist = new Vector();
							while (t3.hasMoreElements ()) {
								MMObjectNode inode = (MMObjectNode)t3.nextElement ();
								checklist.addElement (inode.getIntegerValue ("number"));
							}
						}
					}

					//int prgnr = 8706;
					SortedVector items = getItems (prgnr);
					Enumeration t2 = items.elements ();
					int itemnr = node.getIntValue ("number");
					while (t2.hasMoreElements ()) {
						MMObjectNode itemnode = (MMObjectNode)t2.nextElement ();
						if (itemnode.getIntValue ("number") == itemnr) {
							int start = itemnode.getIntValue ("start");
							int end = (itemnode.getIntValue ("stop"));
							String result = DateStrings.Dutch_longdays[DateSupport.getWeekDayInt (start)] + " ";
							result += "" + DateSupport.getDayInt (start) + " ";
							result += "" + DateStrings.Dutch_months[DateSupport.getMonthInt (start)] + " ";
							result += "" + DateSupport.getTime (start)+" tot ";
							result += "" + DateSupport.getTime (end);
							return (result);
						}
					}
			// end test
			return (null);
		} else if (field.equals ("medium")) {
			Enumeration e = mmb.getInsRel ().getRelated (node.getIntValue ("number"), 873);
			if (e.hasMoreElements ()) {
				MMObjectNode prgnode = (MMObjectNode)e.nextElement ();
				int sort = prgnode.getIntValue ("medium");
				if (sort == 1) return ("tv");
				if (sort == 2) return ("radio");
				if (sort == 3) return ("web");
			}
			return ("");	
		}
		return (null);
	}

	public SortedVector getItems (int programId) {
		// added a LRUCache
		SortedVector items2 = (SortedVector)itemsInfoCache.get (new Integer (programId));
		if (items2 != null) {
			return (items2);
		}

		SortedVector items = new SortedVector (new MMObjectCompare ("itemNr"));
		try {
			MultiConnection con = mmb.getConnection ();
			Statement stmt = con.createStatement ();
			// UNION
			ResultSet rs = stmt.executeQuery ("SELECT a.* from vpro4_items a,(select b.dnumber from vpro4_insrel b where b.snumber="+programId+" union select b.snumber from vpro4_insrel b where b.dnumber="+programId+") c where a.number=c");
			
			(new hitlisted()).convertResultSet (rs, items);				   

			stmt.close ();
			con.close ();
 
			long currentTime = (long)DateSupport.currentTimeMillis () / 1000;
			//insertTimes (items, currentTime);
			items = insertTimes (items);
			itemsInfoCache.put (new Integer (programId), items);
			System.out.println ("Items -> Not Cached getting items=" + programId);
		} catch (SQLException e) {
			// something went wrong print it to the logs
			e.printStackTrace ();
			return (null);
		}				

		return (items);
	}

	public SortedVector insertTimes (SortedVector items) {
		insertTimes (items, 0);
		return (items);
	}

	public SortedVector insertTimes (SortedVector items, long startTime) {
		String itemSet = buildSet (items, "number");

		Enumeration enum;
		MMObjectNode node;
		int currentNumber;

		try {
			MultiConnection con = mmb.getConnection ();
			Statement stmt = con.createStatement ();
			ResultSet rs = stmt.executeQuery ("SELECT b.snumber, a.start, a.stop, a.playtime from vpro4_mmevents a, vpro4_broadcastRel b where a.number = b.dnumber and b.snumber in "+itemSet+" order by b.snumber");
			
			stmt.close ();
			con.close ();

			enum = items.elements ();
			while (enum.hasMoreElements ()) {
				node = (MMObjectNode)enum.nextElement ();
				node.setValue ("start", new Integer (Integer.MAX_VALUE));
				node.setValue ("stop", new Integer (Integer.MAX_VALUE));
				node.setValue ("playtime", new Integer (Integer.MAX_VALUE));
			}

			while (rs.next ()) {
				enum = items.elements ();
				while (enum.hasMoreElements ()) {
					node = (MMObjectNode)enum.nextElement ();
					currentNumber = node.getIntValue ("number");
					if (currentNumber == rs.getInt (1)) {
						node.setValue ("start", new Integer (rs.getInt (2)));
						node.setValue ("stop", new Integer (rs.getInt (3)));
						node.setValue ("playtime", new Integer (rs.getInt (4)));
					}
				}
			}

			/* If we are missing events/items this probably must be enabled, although the relation is one way
			con = mmb.getConnection ();
			stmt = con.createStatement ();
			rs = stmt.executeQuery ("SELECT b.dnumber, a.start, a.stop, a.playtime from vpro4_mmevents a, vpro4_broadcastRel b where a.number = b.snumber and b.dnumber in "+itemSet+" and start >"+startTime+" order by b.dnumber");
			
			stmt.close ();
			con.close ();

			enum = items.elements ();
			while (enum.hasMoreElements ()) {
				node = (MMObjectNode)enum.nextElement ();
				node.setValue ("start", new Integer (Integer.MAX_VALUE));
				node.setValue ("stop", new Integer (Integer.MAX_VALUE));
				node.setValue ("playtime", new Integer (Integer.MAX_VALUE));
			}

			enum = items.elements ();
			while (rs.next ()) {
				boolean found = false;

				while(!found) {
					if(enum.hasMoreElements ()) {
						node = (MMObjectNode)enum.nextElement ();
						currentNumber = node.getIntValue ("number");
						if (currentNumber == rs.getInt (1)) {
							node.setValue ("start", new Integer (rs.getInt (2)));
							node.setValue ("stop", new Integer (rs.getInt (3)));
							node.setValue ("playtime", new Integer (rs.getInt (4)));
							found = true;
						}
					}
					else found = true;
				}
			}
		*/
		   						
		} catch (SQLException e) {
			// something went wrong print it to the logs
			e.printStackTrace ();
			return (null);
		}		


		return (items);
	}



}
