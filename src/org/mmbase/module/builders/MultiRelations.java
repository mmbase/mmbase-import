/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
/*
	$Id: MultiRelations.java,v 1.14 2000-11-16 18:04:07 vpro Exp $

	$Log: not supported by cvs2svn $
	Revision 1.13  2000/11/09 12:16:49  eduard
	Eduard : searchMultiLevelVector now supports OAliases
	
	Revision 1.12  2000/07/15 19:18:09  daniel
	Fixed a bug with new DBType
	
	Revision 1.11  2000/07/15 10:14:42  daniel
	Changed getDBType to int
	
	Revision 1.10  2000/03/30 13:11:32  wwwtech
	Rico: added license
	
	Revision 1.9  2000/03/29 10:59:23  wwwtech
	Rob: Licenses changed
	
	Revision 1.8  2000/03/24 14:33:59  wwwtech
	Rico: total recompile
	
	Revision 1.7  2000/03/21 15:46:13  wwwtech
	Removed private debug method and private field classname, using inherited instead
	
	Revision 1.6  2000/03/20 13:17:30  wwwtech
	Rico: added super.getValue for global function support
	
	Revision 1.5  2000/03/09 10:07:14  wwwtech
	Rico: Fixed multirelations so where clauses can contain similar tablenames ie like program and subprogram. This would go wrong with the old version
	
	Revision 1.4  2000/03/08 14:20:26  wwwtech
	Rico: zapped several old unused methods
	
	Revision 1.3  2000/03/08 14:16:46  wwwtech
	Rico: fixed the scope of several methods, plus added fix against similar table names going wrong in where clause
	
	Revision 1.2  2000/02/24 14:33:49  wwwtech
	Rico: changed out.println by debug
	
*/
package org.mmbase.module.builders;

import java.util.*;
import java.sql.*;
import org.mmbase.module.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.corebuilders.InsRel;
import org.mmbase.module.database.*;
import org.mmbase.util.*;

/**
 * @author Rico Jansen
 * @version $Id: MultiRelations.java,v 1.14 2000-11-16 18:04:07 vpro Exp $
 */
public class MultiRelations extends MMObjectBuilder {
	
	final static boolean debug=false;

	public MultiRelations(MMBase m) {
		this.mmb=m;
		this.tableName="multirelations";
		this.description="";
		m.mmobjs.put(tableName,this);
	}

	
	/**
	* insert a new object, normally not used (only subtables are used)
	*/
	public boolean create() {
		// no create needed this is a virtual builder that will query over
		// multiple other builders.
		return(true);
	}


	/**
	* insert a new object, normally not used (only subtables are used)
	*/
	public int insert(String owner,MMObjectNode node) {
		// no insert allowed on this builder so signal -1
		return(-1);
	}

	 public String getGUIIndicator(MMObjectNode node) {
        String str=node.getStringValue("name");
        if (str.length()>15) {
            return(str.substring(0,12)+"...");
        } else {
            return(str);
        }
    }


	public String getGUIIndicator(String field,MMObjectNode node) {
		return(null);
	}


	public int getDBType(String fieldName) {
		// oke oke we expect a '.' in the name
		int pos=fieldName.indexOf('.');
		if (pos!=-1) {
			String bulname=fieldName.substring(0,pos);
			if (getTableNumber(bulname)>=0) bulname=bulname.substring(0,bulname.length()-1);
			MMObjectBuilder bul=mmb.getMMObject(bulname);
			String tmp=fieldName.substring(pos+1);
			int tmp2=bul.getDBType(tmp);
			return(tmp2);
		}
		return(-1);
	}

	private Vector getSelectTypes(Vector rfields) {
		Vector result=new Vector();
		String val;
		int pos;
		for (Enumeration e=rfields.elements();e.hasMoreElements();) {
			val=(String)e.nextElement();
    		val=Strip.DoubleQuote(val,Strip.BOTH);
			pos=val.indexOf('.');
			if (pos!=-1) {
				String val2=val.substring(0,pos);
				result.addElement(val2);
			}	
		}
		return(result);
	}


	public Object getValue(MMObjectNode node,String fieldName) {
		// oke oke we expect a '.' in the name
		int pos=fieldName.indexOf('.');
		if (pos!=-1) {
			String bulname=fieldName.substring(0,pos);
			int pos2=fieldName.indexOf('(');
			if (pos2==-1) {
				MMObjectBuilder bul=mmb.getMMObject(bulname);
				node.prefix=bulname+".";
				Object o=bul.getValue(node,fieldName.substring(pos+1));
				node.prefix="";
				return(o);
			} else {
				bulname=bulname.substring(pos2+1);
				MMObjectBuilder bul=mmb.getMMObject(bulname);
				node.prefix=bulname+".";
				Object o=bul.getValue(node,fieldName.substring(0,pos2)+"("+fieldName.substring(pos+1));
				node.prefix="";
				return(o);
			}
		} else {
			super.getValue(node,fieldName);
		}
		return(null);
	}

	/**
	* Enumerate all the objects that match the searchkeys
	*/
	public Vector searchMultiLevelVector(int snode,Vector fields,String pdistinct,Vector tables,String where, Vector orderVec,Vector direction) {
		Vector v=new Vector();
		v.addElement(""+snode);
		return(searchMultiLevelVector(v,fields,pdistinct,tables,where,orderVec,direction));
	}

	public Vector searchMultiLevelVector(Vector snodes,Vector fields,String pdistinct,Vector tables,String where, Vector orderVec,Vector direction) {
		String stables,relstring,select,order,basenodestring,distinct;
		Vector rfields,alltables,selectTypes;
		MMObjectNode basenode;
		int snode;

		// Get all the fieldnames
		rfields=fields;
		if (pdistinct!=null && pdistinct.equals("YES")) {
			distinct="distinct";
		}  else {
			distinct="";
		}

		// Get ALL tables (including missing reltables)
		alltables=getAllTables(tables); 

		// Get the destination select string;
		select=getSelectString(alltables,rfields);

		// Get the tables names corresponding to the fields (for the mapping)
		selectTypes=getSelectTypes(rfields);

		// create the order parts
		order=getOrderString(alltables,orderVec,direction);

		// get all the table names 
		stables=getTableString(alltables);

		// get the relation string
		relstring=getRelationString(alltables);

		// Supporting more then 1 source node or no source node at all
		// Note that node number -1 is seen as no source node
		if (snodes.size()>0) {
			String str;
			snode = -1;
			
			// go trough the whole list and verify that it are all integers
			// from last to first,,... since we want snode to be the one that contains the first..
			for (int i=snodes.size() - 1 ; i >= 0 ; i--) {
				str = Strip.DoubleQuote((String)snodes.elementAt(i),Strip.BOTH);
				try {			
					snode=Integer.parseInt(str);
				}
				catch(NumberFormatException e) {
					// maybe it was not an integer, hmm lets look in OAlias table then
					snode = mmb.OAlias.getNumber(str);
				}
				snodes.setElementAt(""+snode, i);
      		}

			int sidx;			
			StringBuffer bb=new StringBuffer();			
			
			if (snode>0) {
				basenode=getNode(""+snode);
				sidx=alltables.indexOf(basenode.parent.tableName);
				if (sidx<0) sidx=0;
				str=idx2char(sidx);
				bb.append(" (");
				bb.append(getNodeString(str,snodes));
				// Check if we got a relation to ourself
				bb.append(") AND ");
				basenodestring=bb.toString();
			} else {
				basenodestring="";
			}
		} else {
			basenodestring="";
		}

		// create the extra where parts
		if (where!=null && !where.equals("")) {
			where=QueryConvertor.altaVista2SQL(where).substring(5);
			if (relstring.length()>1) {
				where="AND ("+getWhereConvert(alltables,where,tables)+")";
			} else {
				where=" ("+getWhereConvert(alltables,where,tables)+")";
			}
		} else {
			where="";
		}

		try {
			MultiConnection con=mmb.getConnection();
			Statement stmt=con.createStatement();
			String query="select "+distinct+" "+select+" from "+stables+" where "+basenodestring+" "+relstring +" "+where+" "+order;
			if (debug) debug("Query "+query);

			ResultSet rs=stmt.executeQuery(query);
			MMObjectNode node;
			Vector results=new Vector();
			Integer number;
			String tmp,prefix;
			while(rs.next()) {
				// create a new object and add it to the result vector
				node=new MMObjectNode(this);
				ResultSetMetaData rd=rs.getMetaData();
				String fieldname;String fieldtype;
				for (int i=1;i<=rd.getColumnCount();i++) {
					prefix=selectTypes.elementAt(i-1)+".";
					fieldname=rd.getColumnName(i);	
					//fieldtype=rd.getColumnTypeName(i);	
					node=database.decodeDBnodeField(node,fieldname,rs,i,prefix);
					if (debug) debug("Node="+node);
				}
				// clear the changed signal
				//node.clearChanged(); // huh ?
				results.addElement(node);
				// huge trick to fill the caches does it make sense ?
				number=new Integer(node.getIntValue("number"));
			}	
			stmt.close();
			con.close();
			// return the results
			return(results);
		} catch (SQLException ee) {
			// something went wrong print it to the logs
			debug("searchMultiLevelVector(): ERROR: ");
			ee.printStackTrace();
			return(null);
		}
	}

	private Vector getAllTables(Vector tables) {
		Vector alltables=new Vector();
		boolean lastrel=false,isRel;
		boolean first=true;
		String curtable;

		for (Enumeration e=tables.elements();e.hasMoreElements();) {
			curtable=(String)e.nextElement();
    		curtable= Strip.DoubleQuote(curtable,Strip.BOTH);
			if (getTableNumber(curtable)>=0) curtable=curtable.substring(0,curtable.length()-1);
			isRel=mmb.getTypeDef().isRelationTable(curtable);
			if (lastrel) {
				if (isRel) {
					// rel, rel
					debug("Error , two reltables "+curtable);
					lastrel=true;
				} else {
					// rel, nonrel
					alltables.addElement(curtable);
					lastrel=false;
				}
			} else {
				if (isRel) {
					// nonrel, rel
					alltables.addElement(curtable);
					lastrel=true;
				} else {
					// nonrel, nonrel
					if (!first) {
						 alltables.addElement("insrel");
					}
					alltables.addElement(curtable);
					lastrel=false;
				}
			}
			first=false;
		}
		return(alltables);
	}

	private int getTableNumber(String table) {
		char ch;
		int i;

		ch=table.charAt(table.length()-1);
		if (Character.isDigit(ch)) {
			i=Integer.parseInt(""+ch);
		} else {
			i=-1;
		}
		return(i);
	}

	private String getTableName(String table) {
		char ch;
		String str;

		ch=table.charAt(table.length()-1);
		if (Character.isDigit(ch)) {
			str=table.substring(0,table.length()-1);
		} else {
			str=table;
		}
		return(str);
	}

	private Vector getFields(Vector tables) {
		Vector v=new Vector();
		for (Enumeration e=tables.elements();e.hasMoreElements();) {
			getFields(v,Strip.DoubleQuote((String)e.nextElement(),Strip.BOTH));
		}
		return(v);
	}

	private Vector getFields(String table) {
		Vector result=new Vector();
		return(getFields(result,table));
	}

	private Vector getFields(Vector v,String table) {
		if (v==null) v=new Vector();

		MMObjectBuilder bul=mmb.getMMObject(table);
		for (Enumeration r=bul.getFieldNames().elements();r.hasMoreElements();) {
			v.addElement(table+"."+(String)r.nextElement());
		}
		return(v);
	}

	protected String getSelectString(Vector alltables,Vector rfields) {
		String result="";
		String val,table,field;
		int pos,idx,x,y;

		for (Enumeration r=rfields.elements();r.hasMoreElements();) {
			val=(String)r.nextElement();
    		val=Strip.DoubleQuote(val,Strip.BOTH);
			pos=val.indexOf('.');
			if (pos!=-1) {
				table=val.substring(0,pos); // the table
				x=getTableNumber(table);
				if (x<0) {
					idx=alltables.indexOf(table);
				} else {
					table=val.substring(0,pos-1); // the table
					y=0;
					idx=-1;
					do {
						idx=alltables.indexOf(table,idx+1);
						y++;
					} while(y<x);
				}
				if (idx>=0) {
					field=val.substring(pos+1); // the field
					if (!result.equals("")) result+=", ";
					result+=""+idx2char(idx)+"."+field;	
				}
			}	
		}
//		debug("getSelectString="+result);
		return(result);
	}

	private String getOrderString(Vector alltables,Vector orders,Vector direction) {
		StringBuffer result=new StringBuffer();
		String val,table,field,dir;
		int pos,idx,opos;
		// UP = ASC, DOWN = DESC

		if (orders==null) return(result.toString());
		// Convert direction table
		for (pos=0;pos<direction.size();pos++) {
			val=(String)direction.elementAt(pos);
    		val=Strip.DoubleQuote(val,Strip.BOTH);
			if (val.equalsIgnoreCase("DOWN")) {
				direction.setElementAt("DESC",pos);
			} else {
				direction.setElementAt("ASC",pos);
			}
		}

		opos=0;
		for (Enumeration r=orders.elements();r.hasMoreElements();opos++) {
			val=(String)r.nextElement();
    		val=Strip.DoubleQuote(val,Strip.BOTH);
			pos=val.indexOf('.');
			if (pos!=-1) {
				table=val.substring(0,pos); // table
				field=val.substring(pos+1); // field
				if (result.length()>0) {
					result.append(", ");
				} else {
					result.append(" ORDER BY ");
				}
				idx=alltables.indexOf(table);
				if (opos<direction.size()) {
					dir=(String)direction.elementAt(opos);
				} else {
					dir=(String)direction.elementAt(0);
				}
				if (idx>=0) result.append(idx2char(idx)+"."+field+" "+dir);	
			}	
		}
		return(result.toString());
	}

	private String getWhereConvert(Vector alltables,String where,Vector tables) {
		String atable,table,pre,post,result2=where;
		int i=0,x,y,idx,cx,px;
		char ch;

		for (Enumeration e=tables.elements();e.hasMoreElements();) {
			atable=Strip.DoubleQuote((String)e.nextElement(),Strip.BOTH);
			x=getTableNumber(atable);
			if (x<0) {
				idx=alltables.indexOf(atable);
			} else {
				table=atable.substring(0,atable.length()-1); // the table
				y=0;
				idx=-1;
				do {
					idx=alltables.indexOf(table,idx+1);
					y++;
				} while(y<x);
			}
			// not 100% safe
			if (idx<0) idx=0;

			// This translates the long tablename to the short one , the
			// database expects
			// ie people.account to a.account
			cx=result2.indexOf(atable+".",0);
			while (cx!=-1) {
				if (cx>0) ch=result2.charAt(cx-1);
				else ch=0;
				if (!isTableNameChar(ch)) {
					pre=result2.substring(0,cx);
					post=result2.substring(cx+atable.length());
					result2=pre+idx2char(idx)+post;
				}
				cx=result2.indexOf(atable+".",cx+1);
			}
			if (debug) debug("getWhereConvert for table "+atable+"|"+result2+"|");
		}
		return(result2.toString());
	}

	/**
	 * This method defines what is 'allowed' in tablenames
	 * Multilevel uses this to find out what is a tablename and what not
	 */
	private boolean isTableNameChar(char ch) {
		boolean rtn=false;

		if (ch=='_' || Character.isLetterOrDigit(ch)) rtn=true;
		return(rtn);
	}

	protected String getTableString(Vector alltables) {
		StringBuffer result=new StringBuffer("");
		String val;
		int idx=0;

		for (Enumeration r=alltables.elements();r.hasMoreElements();) {
			val=(String)r.nextElement();
			if (!result.toString().equals("")) result.append(", ");
			result.append(mmb.baseName+"_"+val);
			result.append(" "+idx2char(idx));	
			idx++;
		}
		return(result.toString());
	}

	protected String getRelationString(Vector alltables) {
		StringBuffer result=new StringBuffer("");
		int siz;
		String src,rel,dst;
		int so,ro,rnum;
		TypeDef typedef;
		TypeRel typerel;
		InsRel insrel;

		typedef=mmb.getTypeDef();
		typerel=mmb.getTypeRel();
		insrel=mmb.getInsRel();
		siz=alltables.size()-2;
		for (int i=0;i<siz;i+=2) {
			src=(String)alltables.elementAt(i);							// name of table (eg. image)
			rel=(String)alltables.elementAt(i+1);						// relation type (eg. insrel)
			dst=(String)alltables.elementAt(i+2);						// name of table (eq. audiopart)

			so=typedef.getIntValue(src);								// get the number of image
			ro=typedef.getIntValue(dst);								// get the number of audiopart

			MMObjectNode nodes = mmb.getMMObject(src).getNode(so);		// transform them to MMObjectNodes
			MMObjectNode noded = mmb.getMMObject(dst).getNode(ro);
			Vector types = typerel.getAllowedRelationsTypes(so, ro);	// get the allowed relations

			Enumeration e= types.elements();
			MMObjectNode reltypeNode;
			int x = 0, y=0;
			rnum = -1;
			if( e.hasMoreElements() ) { 
																	// check if specified relation is a valid one
				// if specified reltype is insrel, check all 

				if( rel.equals("insrel") ) {
					while( e.hasMoreElements() ) { 
						reltypeNode = (MMObjectNode)e.nextElement();	
						if( debug ) debug("getRelationString(): reltypeNode("+reltypeNode+")");
						rnum = reltypeNode.getIntValue("number");
						if( debug ) debug("getRelationString(): rnum("+rnum+")");
						if (insrel.reldefCorrect(so,ro,rnum)) {				// relations all in same directions?
							x++;
						} else {
							y++;
						}
					}
					if( x==0 || y==0 ) {									// allowed when all in same directions
						if( debug ) 
							debug("getRelationString(): x("+x+") y("+y+")");
					} else {
						debug("getRelationString(): ERROR: src("+src+") has NO unidirectional relations with dst("+dst+")!");
					}
				} else {
					reltypeNode = (MMObjectNode)e.nextElement();	
					rnum = reltypeNode.getIntValue("number");
					if( debug ) debug("getRelationString(): one entry found, rnum("+rnum+")");
				}
			} else {
				debug("getRelationString(): ERROR: src("+src+") -> rel("+rel+") -> dst("+dst+") not allowed!");
			}

			if (!result.toString().equals("")) result.append(" AND ");
			if (insrel.reldefCorrect(so,ro,rnum)) {
				result.append(idx2char(i)+".number="+idx2char(i+1)+".snumber AND "+idx2char(i+2)+".number="+idx2char(i+1)+".dnumber");
			} else {
				result.append(idx2char(i)+".number="+idx2char(i+1)+".dnumber AND "+idx2char(i+2)+".number="+idx2char(i+1)+".snumber");

			}
		}
		return(result.toString());
	}

	protected String idx2char(int idx) {
		return(""+new Character((char)('a'+idx)));
	}

	private String getNodeString(String bstr,Vector snodes) {
		String snode,str;
		StringBuffer bb=new StringBuffer();

		snode=Strip.DoubleQuote((String)snodes.elementAt(0),Strip.BOTH);
		if (snodes.size()>1) {
			bb.append(bstr+".number in ("+snode);
			for (int i=1;i<snodes.size();i++) {
				str=Strip.DoubleQuote((String)snodes.elementAt(i),Strip.BOTH);
				bb.append(","+str);
			}
			bb.append(")");
		} else {
			bb.append(bstr+".number="+snode);
		}
		return(bb.toString());
	}

	/**
	*/
	public String getShortedText(String fieldname,int number) {
		try {
			String result=null;
			String tname,fname;
			int pos=fieldname.indexOf('.');
			if (pos!=-1) {
				tname=getTableName(fieldname.substring(0,pos));
				fname=fieldname.substring(pos+1);
			} else {
				tname="object";
				fname=fieldname;
			}
			
			MultiConnection con=mmb.getConnection();
			Statement stmt=con.createStatement();
			// debug("getShortedText(): SELECT "+fname+" FROM "+mmb.baseName+"_"+tname+" where number="+number);
			ResultSet rs=stmt.executeQuery("SELECT "+fname+" FROM "+mmb.baseName+"_"+tname+" where number="+number);
			if (rs.next()) {
				result=getDBText(rs,1);
			}
			stmt.close();
			con.close();
			return(result);
		} catch (Exception e) {
			debug("getShortedText(): Error while trying to load text");
			e.printStackTrace();
		}
		return(null);
	}


	/**
	*/
	public byte[] getShortedByte(String fieldname,int number) {
		try {
			byte[] result=null;
			String tname,fname;
			int pos=fieldname.indexOf('.');
			if (pos!=-1) {
				tname=getTableName(fieldname.substring(0,pos));
				fname=fieldname.substring(pos+1);
			} else {
				tname="object";
				fname=fieldname;
			}
			MultiConnection con=mmb.getConnection();
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("SELECT "+fname+" FROM "+mmb.baseName+"_"+tname+" where number="+number);
			if (rs.next()) {
				result=getDBByte(rs,1);
			}
			stmt.close();
			con.close();
			return(result);
		} catch (Exception e) {
			debug("getShortedByte(): Error while trying to load bytes");
			e.printStackTrace();
		}
		return(null);
	}

}
