/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

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
 * @author Daniel Ockeloen
 * @version 12 Mar 1997
 */
public class MultiRelations extends MMObjectBuilder {
	
	final static boolean debug=true;

	public MultiRelations(MMBase m) {
		this.mmb=m;
		this.tableName="multirelations";
		this.description="";
		//init();
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


	public String getDBType(String fieldName) {
		// oke oke we expect a '.' in the name
		int pos=fieldName.indexOf('.');
		if (pos!=-1) {
			String bulname=fieldName.substring(0,pos);
			if (getTableNumber(bulname)>=0) bulname=bulname.substring(0,bulname.length()-1);
			MMObjectBuilder bul=mmb.getMMObject(bulname);
			String tmp=fieldName.substring(pos+1);
			String tmp2=bul.getDBType(tmp);
			if (tmp2!=null) return(tmp2);
			return(null);
		}
		return(null);
	}

	String getSelectString(Vector fields,String a,String b,String c,String d,String e) {
		String result="";
		for (Enumeration r=fields.elements();r.hasMoreElements();) {
			String val=(String)r.nextElement();
    		val= Strip.DoubleQuote(val,Strip.BOTH);
			int pos=val.indexOf('.');
			if (pos!=-1) {
				String val2=val.substring(0,pos);
				String val3=val.substring(pos+1);
				if (!result.equals("")) result+=", ";
				if (val2.equals(a)) {
					result+="a."+val3;	
				} else if (val2.equals(b)) {
					result+="b."+val3;	
				} else if (val2.equals(c)) {
					result+="c."+val3;	
				} else if (val2.equals(d)) {
					result+="d."+val3;	
				} else if (val2.equals(e)) {
					result+="e."+val3;	
				}
			}	
		}
		return(result);
	}


	String getOrderString(Vector orders,String a,String b,String c,String d,String e) {
		String result="";
		if (orders==null) return(result);
		for (Enumeration r=orders.elements();r.hasMoreElements();) {
			String val=(String)r.nextElement();
    		val= Strip.DoubleQuote(val,Strip.BOTH);
			int pos=val.indexOf('.');
			if (pos!=-1) {
				String val2=val.substring(0,pos);
				String val3=val.substring(pos+1);
				if (!result.equals("")) {
					result+=", ";
				} else {
					result=" ORDER BY ";
				}
				if (val2.equals(a)) {
					result+="a."+val3;	
				} else if (val2.equals(b)) {
					result+="b."+val3;	
				} else if (val2.equals(c)) {
					result+="c."+val3;	
				} else if (val2.equals(d)) {
					result+="d."+val3;	
				} else if (val2.equals(e)) {
					result+="e."+val3;	
				}
			}	
		}
		return(result);
	}


	String getWhereConvert(String where,String a,String b,String c,String d,String e) {
		StringObject result=new StringObject(where);
		result.replace(a+".","a.");
		result.replace(b+".","b.");
		result.replace(c+".","c.");
		result.replace(d+".","d.");
		result.replace(e+".","e.");
		return(result.toString());
	}


	// Is used by MultiLevel;
	public Vector getSelectTypes(Vector rfields) {
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
		}
		return(null);
	}



	Vector getFields(String a,String b,String c,String d,String e) {
		Vector result=new Vector();
		// map out a
		MMObjectBuilder bul=mmb.getMMObject(a);
		for (Enumeration r=bul.getFieldNames().elements();r.hasMoreElements();) {
			result.addElement(a+"."+(String)r.nextElement());
		}
		// map out b
		bul=mmb.getMMObject(b);
		for (Enumeration r=bul.getFieldNames().elements();r.hasMoreElements();) {
			result.addElement(b+"."+(String)r.nextElement());
		}
		// map out c
		bul=mmb.getMMObject(c);
		for (Enumeration r=bul.getFieldNames().elements();r.hasMoreElements();) {
			result.addElement(c+"."+(String)r.nextElement());
		}
		// map out d
		bul=mmb.getMMObject(d);
		for (Enumeration r=bul.getFieldNames().elements();r.hasMoreElements();) {
			result.addElement(d+"."+(String)r.nextElement());
		}
		// map out e
		bul=mmb.getMMObject(e);
		for (Enumeration r=bul.getFieldNames().elements();r.hasMoreElements();) {
			result.addElement(e+"."+(String)r.nextElement());
		}
		return(result);
	}


	// NEW multi level search code RICO

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
			String str,bstr;
			StringBuffer bb=new StringBuffer();
			int i=0,sidx;
			str=Strip.DoubleQuote((String)snodes.elementAt(0),Strip.BOTH);
			snode=Integer.parseInt(str);
			if (snode>0) {
				basenode=getNode(""+snode);
				sidx=alltables.indexOf(basenode.parent.tableName);
				if (sidx<0) sidx=0;
				bstr=idx2char(sidx);
				bb.append(" (");
				bb.append(getNodeString(bstr,snodes));
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
			if (debug) System.out.println("MultiLevel "+query);

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
					fieldtype=rd.getColumnTypeName(i);	
					node=database.decodeDBnodeField(node,fieldtype,fieldname,rs,i,prefix);
					//if (debug) System.out.println("Node="+node);
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
			ee.printStackTrace();
			return(null);
		}
	}

	public Vector getAllTables(Vector tables) {
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
					System.out.println("MultiRelations error , two reltables "+curtable);
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

	public Vector getFields(Vector tables) {
		Vector v=new Vector();
		for (Enumeration e=tables.elements();e.hasMoreElements();) {
			getFields(v,Strip.DoubleQuote((String)e.nextElement(),Strip.BOTH));
		}
		return(v);
	}

	public Vector getFields(String table) {
		Vector result=new Vector();
		return(getFields(result,table));
	}

	Vector getFields(Vector v,String table) {
		if (v==null) v=new Vector();

		MMObjectBuilder bul=mmb.getMMObject(table);
		for (Enumeration r=bul.getFieldNames().elements();r.hasMoreElements();) {
			v.addElement(table+"."+(String)r.nextElement());
		}
		return(v);
	}

	String getSelectString(Vector alltables,Vector rfields) {
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
//		System.out.println("getSelectString="+result);
		return(result);
	}

	String getOrderString(Vector alltables,Vector orders,Vector direction) {
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

	// Adapt for numbers
	String getWhereConvert(Vector alltables,String where,Vector tables) {
		StringObject result=new StringObject(where);
		String atable,table;
		int i=0,x,y,idx;
/*
		for (Enumeration e=alltables.elements();e.hasMoreElements();) {
			atable=(String)e.nextElement();
			result.replace(atable+".",""+idx2char(i)+".");
			i++;
		}
*/
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
			result.replace(atable+".",""+idx2char(idx)+".");
		}
		return(result.toString());
	}

	String getTableString(Vector alltables) {
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

	public String getRelationString(Vector alltables) {
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
			src=(String)alltables.elementAt(i);
			rel=(String)alltables.elementAt(i+1);
			dst=(String)alltables.elementAt(i+2);
			so=typedef.getIntValue(src);
			ro=typedef.getIntValue(dst);

			rnum=typerel.getAllowedRelationType(so,ro);

			if (!result.toString().equals("")) result.append(" AND ");
			if (insrel.reldefCorrect(so,ro,rnum)) {
				result.append(idx2char(i)+".number="+idx2char(i+1)+".snumber AND "+idx2char(i+2)+".number="+idx2char(i+1)+".dnumber");
			} else {
				result.append(idx2char(i)+".number="+idx2char(i+1)+".dnumber AND "+idx2char(i+2)+".number="+idx2char(i+1)+".snumber");

			}
		}
		return(result.toString());
	}

	public String idx2char(int idx) {
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
			// System.out.println("SELECT "+fname+" FROM "+mmb.baseName+"_"+tname+" where number="+number);
			ResultSet rs=stmt.executeQuery("SELECT "+fname+" FROM "+mmb.baseName+"_"+tname+" where number="+number);
			if (rs.next()) {
				result=getDBText(rs,1);
			}
			stmt.close();
			con.close();
			return(result);
		} catch (Exception e) {
			System.out.println("MultiRelations : trying to load text");
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
			System.out.println("MultiRelations : trying to load bytes");
			e.printStackTrace();
		}
		return(null);
	}
}
