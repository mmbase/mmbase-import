/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.module.builders;

import java.util.*;
import java.io.*;
import java.sql.*;
import java.sql.Types;

import javax.servlet.http.*;

import org.mmbase.util.*;
import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;

/**
 * @author Daniel Ockeloen
 * @author Rico Jansen
 * @version $Revision: 1.17 $ $Date: 2001-05-04 16:19:44 $ 
 * V2
 */
public class Teasers extends MMObjectBuilder {

    private static Logger log = Logging.getLoggerInstance(Teasers.class.getName());
	
	public String getGUIIndicator(String field,MMObjectNode node) {
		if (field.equals("state")) {
			int val=node.getIntValue("state");
			if (val==1) {
				return("Prive");
			} else if (val==2) {
				return("VPRO");
			} else  if (val==3) {
				return("WebServer");
			} else  if (val==4) {
				return("WebServer");
			}
			
		}
		return(null);
	}
	
	/**
	* Generate a list of values from a command to the processor
	*/
	public Vector getList(scanpage sp, StringTagger tagger, StringTokenizer tok) throws org.mmbase.module.ParseException {
		Vector results=new Vector();
		if (tok.hasMoreTokens()) {
			String cmd=tok.nextToken();
			if (cmd.equals("KEYWORDS")) {
				results=getKeyWords(tagger);
			}
		}
		return(results);
	}

	private Vector getKeyWords(StringTagger tagger)
	{
		String nodeNr = tagger.Value("NODE");
		MMObjectNode node = getNode(nodeNr);
		if (node==null)
			return new Vector();
		String s = node.getStringValue("title")+" "+node.getStringValue("body");
		return Keywords.createKeywords(s);
	}

	public Object getValue(MMObjectNode node,String field) {
		if (field.equals("image")) {
			Enumeration e=mmb.getInsRel().getRelated(node.getIntValue("number"),6);
			if (e.hasMoreElements()) {
				MMObjectNode node2=(MMObjectNode)e.nextElement();
				return "<IMG SRC=\"/img.db?"+node2.getIntValue("number")+"+s(118x3000)\" ALIGN=\"BOTTOM\">";
			}
			return null;
		}
        return super.getValue( node, field ); 
	}

	public String getDefaultUrl(int src) {

		log.debug("getDefaultUrl("+src+")");
	
		MMObjectNode node=getNode(src);
		String url=getUrl(src); // returns the url related to this teaser or null if none

		if (url!=null) 
			return(url);

		// No url related to teaser, now walk through all related objects
		// and ask their builders for a url through builder.getDefaultUrl(relatedNodeNr)
		Enumeration e=node.getRelations();
		while (e.hasMoreElements()) {
			MMObjectNode node2=(MMObjectNode)e.nextElement();
			int other=node2.getIntValue("dnumber");
			if (other==src) {
				other=node2.getIntValue("snumber");
			}

			log.debug("getDefaultUrl("+src+"): other("+other+")");

			MMObjectNode node3=getNode(other);
			int otype=node3.getIntValue("otype");

			log.debug("getDefaultUrl("+src+"): found otype("+otype+")");

			MMObjectBuilder bul=mmb.getMMObject(mmb.getTypeDef().getValue(otype));

			int numbie = node3.getIntValue("number");
			log.debug("getDefaultUrl("+src+"): number found("+numbie+"), getting defaultUrl");

			url=bul.getDefaultUrl(numbie);

			if (url!=null) 
				return(url);
		}
		return(null);
	}

	/** @return the url related to this teaser or null if none
	 */
	private String getUrl(int src) {
		Enumeration e=mmb.getInsRel().getRelated(src,8); // 8 = objectNr URL
		if (e.hasMoreElements()) {
			MMObjectNode node=(MMObjectNode)e.nextElement();
			return(node.getStringValue("url"));
		}
		return(null);
	}

	/**
	* Create or update an entry in the TeaserSearchTable for teaser with objectnr where
	* or for all if where = -1
	* Layout tsearchcache:
	* number		integer			btree index	primary key equals teaser number
	* tnumber		integer			object related to teaser
	* totype		integer			1: gids artikel 2: cds, 3: video tape, 4: publicaties 5: tv program
									6: radio program, 7: web program
	* value			integer         value field from teaser
	* image			integer			related image
	* title			varchar(255)	title from teaser
	* teaserbody	char(512)		body from teaser
	* body			char(29000)		eetx index composed of lowercase title and body
	*/
	private synchronized boolean fillTeaserSearchTable(int where) {
		MMObjectNode node2,other;
		int image;
		int i=0;
		String tmp;
		try {
			MultiConnection con=mmb.getConnection();
			Statement stmt=con.createStatement();
			ResultSet rs;
			if (where==-1) {		
				rs=stmt.executeQuery("SELECT number FROM "+mmb.baseName+"_teasers;");
			} else {
				rs=stmt.executeQuery("SELECT number FROM "+mmb.baseName+"_teasers where number="+where+";");
			}
			int number,ttarget,sort,othertype;
			Vector numbers=new Vector();
			while (rs.next()) {
			 	numbers.addElement(new Integer(rs.getInt(1)));
			}
			stmt.close();
			con.close();

			Enumeration list=numbers.elements();
			while (list.hasMoreElements()) {
				number=((Integer)list.nextElement()).intValue();
				delTeaserSearchElement(number);
				log.info("Adding teaser "+number+" to teaser search cache");
				ttarget=-1;
				sort=-1;
				MMObjectNode node=getNode(number);
				if (node!=null) {
					// get teaser info
					int value=node.getIntValue("value");
					int state=node.getIntValue("state");

					if (state<3) continue; // Skip unpublished ones

					String title=node.getStringValue("title");
					String tbody=node.getStringValue("body");
					String body=title+" "+tbody;

					// find out if it has a image
					Enumeration t=mmb.getInsRel().getRelated(node.getIntValue("number"),6); // Images otype ?
					if (t.hasMoreElements()) {
						MMObjectNode inode=(MMObjectNode)t.nextElement();
						image=inode.getIntValue("number");
					} else {
						image=-1;
					}
					// get its relations
					Enumeration e=node.getRelations();
					while (e.hasMoreElements()) {
						node2=(MMObjectNode)e.nextElement();
						if (node2.getIntValue("snumber")==number) {
							other=getNode(node2.getIntValue("dnumber"));
						} else {
							other=getNode(node2.getIntValue("snumber"));
						}
						othertype=other.getIntValue("otype");
						ttarget=other.getIntValue("number");

						tmp=other.getStringValue("intro");
						body+=" "+tmp;
						tmp=other.getStringValue("title");
						body+=" "+tmp;
						tmp=other.getStringValue("subtitle");
						body+=" "+tmp;
						tmp=other.getStringValue("body");
						if (tmp!=null) body+=" "+tmp;

						switch(othertype) {
						case 1139: // cds
							sort=2; // signal this as a cd from the shop
							break;
						case 1792: // video tape
							sort=3; // signal this as a video tape
							break;
						case 910: // publicaties
							sort=4; // signal this as a publication
							break;
						case 873: // program
							int medium=other.getIntValue("medium");
							if (medium==1) {
								sort=5; // signal this as a tv program
							} else if (medium==2) {
								sort=6; // signal this as a radio program
							} else if (medium==3) {
								sort=7; // signal this as a web program
							}
							break;
						case 1319: // guidearticle
							sort=1; // signal this as a guide article
							break;
						default:
							break;
						}
					}
					body=body.toLowerCase();
					if (state>2) { 
						try {
							addTeaserSearchElementInformix(number,ttarget,sort,value,image,title,tbody,body);
						} catch (Exception r) {
						}
					} else {
						log.error("fillTeaserSearchTable("+where+"): ERROR: Not Insert");
					}
					
				}
			}	
			return(true);
		} catch (SQLException e) {
			log.error("fillTeaserSearchTable("+where+"): ERROR: ");
			e.printStackTrace();
			return(false);
		}
	}

	private boolean addTeaserSearchElementInformix(int number,int ttarget,int ttype, int value, int image,String title,String tbody,String body) {
		byte[] isochars=null;
		PreparedStatement statement;
		log.info("Teasers -> Adding teaser search element "+number+" -> "+ttarget);

			if (body!=null && body.length()>29000) {
				body=body.substring(0,28999);
			}

			// got some issues with this routine.
			// seems users are able to trigger a 'java.sql.SQLException: Unique constraint' each second
			// print out what teaser its doing it

			try {
				MultiConnection con=mmb.getConnection();
				statement=con.prepareStatement("insert into "+mmb.baseName+"_tsearchcache values(?,?,?,?,?,?,?,?)");
				statement.setInt(1,number);
				statement.setInt(2,ttarget);
				statement.setInt(3,ttype);
				statement.setInt(4,value);
				statement.setInt(5,image);
				statement.setString(6,title);
				statement.setString(7,tbody);
				statement.setString(8,body);
				statement.executeUpdate();
				statement.close();
				con.close();
			} catch (SQLException e) {
				log.error("addTeaserSearchElementInformix("+number+","+ttarget+","+ttype+","+value+","+image+","+title+"): Exception: ");
				log.error("addTeaserSearchElementInformix(): ERROR: SQL processing error: " + e);
				e.printStackTrace();
			}
		return(true);
	}

	/** Remove teaser number from tsearchcache
	* 
	*/
	private boolean delTeaserSearchElement(int number) {
		log.info("Removing teaser "+number+" from teaser search cache");
		try {
			MultiConnection con=mmb.getConnection();
			Statement stmt=con.createStatement();
			stmt.executeUpdate("delete from "+mmb.baseName+"_tsearchcache where number="+number);
			stmt.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return(true);	
	}

	public boolean nodeLocalChanged(String machine, String number,String builder,String ctype) {
		super.nodeLocalChanged(machine, number,builder,ctype);
		log.info("Teasers -> change detected in "+number+" "+builder+" "+ctype);
		if (builder.equals(tableName)) {
			int nr = Integer.parseInt(number);
			if (ctype.equals("c") || ctype.equals("n") || ctype.equals("r")) {
				MMObjectNode node=getNode(number);
				if (node!=null) fillTeaserSearchTable(nr);
			}
			else if (ctype.equals("d")) delTeaserSearchElement(nr);
			log.info("Removing "+number+" from jumper cache");
			Jumpers jumpers = (Jumpers)mmb.getMMObject("jumpers");
			if (jumpers==null) { 
				log.error("ERROR: Could not get Jumper builder");
			} else {
				jumpers.delJumpCache(number);
			}
		}
		return true;
	}

	// Temp Temp for searchhack
	// total table holds nodes !!
	// Hashtable ts_cache_total=new Hashtable();
	// Hashtable ts_image_cache=new Hashtable();

	/* Currently dead code
	private boolean createTeaser(String number) {
		MMObjectNode node=getNode(number);
		if (node!=null) {
			fillTeaserSearchTable(node.getIntValue("number"));
			fillTeaserUrl(node.getIntValue("number"));
		}
		return(true);
	}
	*/

	/**
	* Return a vector of teasers with the realtions flattened into the result
	* Called by Pool builder
	*/
	public Vector insertRelations(Vector nodes) {
		// do the query on the database
		try {
			MultiConnection con=mmb.getConnection();
			Statement stmt = con.createStatement();
			ResultSet rs;
			Vector results = new Vector();
			String numberSet = buildSet(nodes,"number");
			Enumeration enum;
			MMObjectNode node;
			int currentNumber;

			// Get all the images linked to the requested Teasers
			rs = stmt.executeQuery("SELECT a.snumber, b.number FROM "
								   +mmb.baseName+"_"+mmb.getMMObject("insrel").tableName+" a, "
								   +mmb.baseName+"_"+mmb.getMMObject("images").tableName+" b"								   
								   +" WHERE a.snumber in "+numberSet+" AND a.dnumber = b.number ORDER BY a.snumber");

			enum = nodes.elements();
			while (rs.next()) {
				boolean found = false;
				
				while(!found) {
					if(enum.hasMoreElements()) {
						node = (MMObjectNode)enum.nextElement();
						currentNumber = node.getIntValue("number");
						if (currentNumber == rs.getInt(1)) {
							node.setValue("handle",rs.getString(2));
							found = true;
						}
					}
					else found = true;
				}
			}
			stmt.close();
			con.close();

			enum = nodes.elements();
			while (enum.hasMoreElements()) {
				node = (MMObjectNode)enum.nextElement();
				node.setValue("url",node.getValue("number"));
			}

			return (results);
		} catch (SQLException e) {
			// something went wrong print it to the logs
			e.printStackTrace();
			return(null);
		}
	}

	/* Currently dead code II
	public boolean createTeaserSearchTable() {
		// create the main object table
		try {
			MultiConnection con=mmb.getConnection();
			Statement stmt=con.createStatement();
			stmt.executeUpdate("create table "+mmb.baseName+"_tsearchcache  ( number integer not null "
										+", tnumber integer not null"
										+", totype integer not null"
										+", value integer not null"
										+", image integer not null"
										+", title varchar(255) not null"
										+", teaserbody char(512) not null"
										//+", body large_text not null"
										+", body char(29000) not null"
										+", primary key(number))");
			stmt.close();
			con.close();
			return(true);
		} catch (SQLException e) {
			log.error("createTeaserSearchTable(): ERROR: can't create teaser search table ");
			//e.printStackTrace();
		}
		return(false);
	}
	*/

	/* Currently dead code II
	public boolean createTeaserUrlTable() {
		// create the main object table
		try {
			MultiConnection con=mmb.getConnection();
			Statement stmt=con.createStatement();
			stmt.executeUpdate("create table "+mmb.baseName+"_turlcache  ( number integer not null "
										+", url varchar(255) not null"
										+", primary key(number))");
			stmt.close();
			con.close();
			return(true);
		} catch (SQLException e) {
			log.error("createTeaserUrlTable(): can't create teaser url table ");
		}
		return(false);
	}
	*/

	/** Currently dead code II
	public boolean fillTeaserSearchTable() {
		return(fillTeaserSearchTable(-1));
	}
	*/
	
	/**
	* Currently dead code
	*
	private boolean delUrlSearchElement(int number) {
		try {
			MultiConnection con=mmb.getConnection();
			Statement stmt=con.createStatement();
			stmt.executeUpdate("delete from "+mmb.baseName+"_turlcache where number="+number);
			stmt.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Jumpers bul=(Jumpers)mmb.getMMObject("jumpers");
		bul.delJumpCache(""+number);
		return(true);	
	}
	*/

	/** Currently dead code
	* 
	*
	private Vector globalsearch(String key) {
		key=key.toLowerCase();
		Vector results=new Vector();
		try {
			MultiConnection con=mmb.getConnection();
			Statement stmt=con.createStatement();
			// ResultSet rs=stmt.executeQuery("SELECT number,totype FROM "+mmb.baseName+"_tsearchcache WHERE body like '%"+key+"%'");
			ResultSet rs=stmt.executeQuery("SELECT number,totype FROM "+mmb.baseName+"_tsearchcache WHERE Contains(body,'"+key+"')");
			while(rs.next()) {
				results.addElement(new Integer(rs.getInt(1)));
				results.addElement(new Integer(rs.getInt(2)));
			}
			stmt.close();
			con.close();
			return(results);
		} catch (SQLException e) {
			return(results);
		}
	}
	*/

	/* Currently dead code II
	public boolean fillTeaserUrlTable() {
		MMObjectNode node2,other;
		int i=0;
		String number;
		try {
			MultiConnection con=mmb.getConnection();
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("SELECT number FROM "+mmb.baseName+"_teasers;");
			Vector numbers=new Vector();
			while (rs.next()) {
			 	numbers.addElement(""+rs.getInt(1));
			}
			stmt.close();
			con.close();
			Jumpers bul=(Jumpers)mmb.getMMObject("jumpers");
			Enumeration list=numbers.elements();
			while (list.hasMoreElements()) {
				number=(String)list.nextElement();
				String url=bul.getJump(""+number);
				if (url!=null) {

				 String query="insert into "+mmb.baseName+"_turlcache values("+number+",'"+url+"')";
				boolean oke=false;
				while (!oke) {
				try {
				MultiConnection con2=mmb.getConnection();
				Statement stmt2=con2.createStatement();
				stmt2.executeUpdate(query);
				stmt2.close();
				stmt2=null;
				con2.close();
				con2=null;
				oke=true;
				} catch (SQLException f) {
					f.printStackTrace();
					log.error("Teasers -> Database error doing a wait");
					try {Thread.sleep(1000);} catch (InterruptedException e){}
					//return(false);
				}
				}
			
				}
			}
		} catch (SQLException e) {
			// something went wrong print it to the logs
			e.printStackTrace();
			return(false);
		}
		return(false);
	}
	*/

	/**
	* Currently dead code
	*
	private boolean fillTeaserUrl(int number) {
		MMObjectNode node2,other;
		int i=0;
		delUrlSearchElement(number);
		Jumpers bul=(Jumpers)mmb.getMMObject("jumpers");
		String url=bul.getJump(""+number);
		if (url!=null) {

			String query="insert into "+mmb.baseName+"_turlcache values("+number+",'"+url+"')";
			try {
				MultiConnection con=mmb.getConnection();
				Statement stmt2=con.createStatement();
				stmt2.executeUpdate(query);
				con.close();
			} catch (SQLException f) {
				f.printStackTrace();
				return(false);
			}
		}
		return(false);
	}
	*/

	/* Currently dead code II
	public boolean hasRelatedTeaser(String number) {
		boolean b=false;
		try {
			MultiConnection con=mmb.getConnection();
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("select a.number from vpro4_teasers a, vpro4_insrel b where b.snumber=a.number and b.dnumber="+number+" UNION select a.number from vpro4_teasers a, vpro4_insrel b where b.dnumber=a.number and b.snumber="+number);
			while (rs.next()) {
				b=true;
			}
			stmt.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return(b);
	}
	*/
	
	/** Currently dead code II
	* generates a teaser and creates a relation between this new Teaser
	* and the given node (id).
	public int autoGenerateTeaser(String id) {
		MMObjectNode node=getNode(id);
		MMObjectBuilder bul=mmb.getMMObject(mmb.getTypeDef().getValue(node.getIntValue("otype")));
		if (bul!=null) {
			MMObjectNode tnode=getNewNode("system");
			tnode=bul.getDefaultTeaser(node,tnode);
			int tid=tnode.insert("system");
			if (tid!=-1) {
				try {
					log.debug("Teasers -> Adding relation between "+id+"  and "+tid);
					int oit=Integer.parseInt(id);
					mmb.getInsRel().insert("system",oit,tid,14);
				} catch(Exception e) {}
			}
			createTeaser(""+tid);
			return(tid);
		} else {
			log.warning("Teasers -> Can't autogenerate teaser, unknown builder on "+id);
		}
		return(-1);
	}
	*/

	/*	Nothing as we would get dup's otherwise II
	public boolean nodeRemoteChanged(String number,String builder,String ctype) {
        super.nodeRemoteChanged(number,builder,ctype);
		if (builder.equals(tableName)) {
			// Nothing as we would get dup's otherwise
		}
		return(true);
	}
	*/

	/** Currently dead code
	 * temp search 
	 *
	public Vector doTSearch(scanpage sp, StringTagger tagger) {
		Object tmp;
		int wantedlist;
		Enumeration e;
		String result=null;
		MMObjectNode node;
		Vector results=new Vector(); 
		String where=tagger.Value("WHERE");
		String dbsort=tagger.Value("DBSORT");
		long begin=(long)System.currentTimeMillis();
		try {
			wantedlist=Integer.parseInt(tagger.Value("WANTEDLIST"));
		} catch (Exception ee) {
			wantedlist=0;
		}

		// start of search
		Vector se=(Vector)ts_cache_total.get(where+dbsort);
		if (se==null) {
			se=globalsearch(where);
			ts_cache_total.put(where+dbsort,se);
			e=se.elements();
		} else {
			e=se.elements();
		}
		// end of search

		int i,totype;
		for (;e.hasMoreElements();) {
			i=((Integer)e.nextElement()).intValue();
			totype=((Integer)e.nextElement()).intValue();
			if (wantedlist==0 || wantedlist==totype) {
				node=getNode(i);
				if (node!=null) {
					Enumeration f=tagger.Values("FIELDS").elements();
					for (;f.hasMoreElements();) {
						// hack hack this is way silly Strip needs to be fixed
						String fieldname=Strip.DoubleQuote((String)f.nextElement(),Strip.BOTH);
						String type2=node.getDBType(fieldname);

						if (type2==null) {
							if (fieldname.equals("image")) {
								result=(String)ts_image_cache.get("image"+node.getIntValue("number"));
								// allways correct !!!
								result="<IMG SRC=\"/img.db?1123+s(118x3000)\">"; 
							}
							if (result==null) {
								result=""+node.getValue(fieldname);
								if (fieldname.equals("image")) {
									ts_image_cache.put("image"+node.getIntValue("number"),result);
								}
							}
							if (result!=null && !result.equals("null")) {
								results.addElement(result); 
							} else {
								results.addElement(""); 
							}
						} else results.addElement( node.getValueAsString( fieldname ) );
					}// for
				}// if (node!=null)
			}// if (wantedlist==0 ...)
		}// for
		long end=(long)System.currentTimeMillis();
		tagger.setValue("ITEMS",""+tagger.Values("FIELDS").size());
		return(results);
	}
	*/


	/** Currently dead code
	 * temp search 
	 *
	public int doTSearchSize(scanpage sp, StringTokenizer tok) {
			// this is a hack^2
			String type=null,key=null;
			if (tok.hasMoreTokens()) {
				type=tok.nextToken();
				if (tok.hasMoreTokens()) {
					key=tok.nextToken();
				}
			}
			if (key==null) key="";
			StringTagger tagger=new StringTagger("");
			tagger.setValue("WANTEDLIST",type);
			tagger.setValue("WHERE",key);
			tagger.setValue("FIELDS","number");
			Vector result=doTSearch(sp,tagger);
			if (result!=null) {
				return(result.size());
			} else {
				return(0);
			}
	}
	*/
}
