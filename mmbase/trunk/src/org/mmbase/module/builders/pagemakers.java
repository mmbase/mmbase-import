/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.module.builders;

import java.util.*;
import java.sql.*;
import java.io.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;

/**
 * @author Daniel Ockeloen
 * @author David V van Zeventer
 * @version 21 Dec 1998
 */
public class pagemakers extends MMObjectBuilder implements MMBaseObserver {

	public final static String buildername = "pagemakers";
	public static java.util.Properties driveprops= null;

	
	/**
	* insert a new object, normally not used (only subtables are used)
	*/
	/*
	public boolean create() {
		// create the main object table
		try {
			MultiConnection con=mmb.getConnection();
			Statement stmt=con.createStatement();
			stmt.executeUpdate("create row type "+mmb.baseName+"_"+tableName+"_t (name varchar(20) not null"
				+", pagemakertype char(32)"
				+", state char(32)"
				+", info char(2048) not null) under "+mmb.baseName+"_object_t");
			System.out.println("Created "+tableName);
			stmt.close();
			con.close();
		} catch (SQLException e) {
			System.out.println("can't create type "+tableName);
			e.printStackTrace();
		}
		try {
			MultiConnection con=mmb.getConnection();
			Statement stmt=con.createStatement();
			stmt.executeUpdate("create table "+mmb.baseName+"_"+tableName+" of type "+mmb.baseName+"_"+tableName+"_t (unique (name)"
				+", primary key(number)) under "+mmb.baseName+"_object");
			stmt.close();
			con.close();
		} catch (SQLException e) {
			System.out.println("can't create table "+tableName);
			e.printStackTrace();
		}
		return(false);
	}
	*/

	/**
	* insert a new object, normally not used (only subtables are used)
	*/
	/*
	public int insert(String owner,MMObjectNode node) {
		String name=node.getStringValue("name");
		String pagemakertype=node.getStringValue("pagemakertype");
		String state=node.getStringValue("state");
		String info=node.getStringValue("info");
		
		if (state==null) state="";
		if (info==null) info="";

		int number=getDBKey();
		if (number==-1) return(-1);
		try {
			MultiConnection con=mmb.getConnection();
			PreparedStatement stmt=con.prepareStatement("insert into "+mmb.baseName+"_"+tableName+" values(?,?,?,?,?,?,?)");
				stmt.setInt(1,number);
				stmt.setInt(2,oType);
				stmt.setString(3,owner);
				stmt.setString(4,name);
				stmt.setString(5,pagemakertype);
				stmt.setString(6,state);
				stmt.setString(7,info);
				stmt.executeUpdate();
				stmt.close();
				con.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Error on : "+number+" "+owner+" fake");
			return(-1);
		}
		return(number);	
	}
	*/


	/*
	public String getGUIIndicator (MMObjectNode node) {
		String str = node.getStringValue ("name");
		if (str.length () > 15) {
			return (str.substring (0,12) + "...");
		} else {
			return (str);
		}
	}
	*/

	/*
	public boolean nodeRemoteChanged(String number,String builder,String ctype) {
		super.nodeRemoteChanged(number,builder,ctype);
		return(nodeChanged(number,builder,ctype));
	}
	*/

	/*
	public boolean nodeLocalChanged(String number,String builder,String ctype) {
		super.nodeLocalChanged(number,builder,ctype);
		return(nodeChanged(number,builder,ctype));
	}
	*/

	/*
	public boolean nodeChanged(String number,String builder,String ctype) {
		MMObjectNode node=getNode(number);
		if (node!=null) {
			System.out.println("pagemakers node="+node.toString());
			// is if for me ?, enum related mmservers to check
			Enumeration e=mmb.getInsRel().getRelated(number,"mmservers");
			while  (e.hasMoreElements()) {
				MMObjectNode node2=(MMObjectNode)e.nextElement();
				String wantedServer=node2.getStringValue("name");
				if (wantedServer.equals(mmb.getMachineName())) {
					doAction(node);
				}
			}
		}
		return(true);
	}
	*/

	/**
	* handles commands for pagemakers found in this machine
	*/
	private void doAction(MMObjectNode node) {
		String name=node.getStringValue("name");
		String cdtype=node.getStringValue("pagemakertype");
		String state=node.getStringValue("state");
		String info=node.getStringValue("info");

		if (!state.equals("busy")) {
			System.out.println("Action called on pagemaker : "+name);
			// start a thread to handle command
			new pagemakerProbe(this,node);
		} else {
			System.out.println("Problem action called on pagemaker: "+name+" while it was busy");
		}
	}

  	public void calcPage(String url) { 
		scanmoduleInterface m=(scanmoduleInterface)mmb.getBaseModule("scanmodule");		
		if (m!=null) {
			HttpServletRequest req=(HttpServletRequest)(new EmptyHttpServletRequest());
			System.out.println(m.calcPage(url,req,0));
		}
	}


  	public void newPage(String line) { 
		StringTagger tagger=new StringTagger(line);
		String url=tagger.Value("URL");
		String expires=tagger.Value("EXPIRE");
		int expire=-1;
		try {
			expire=Integer.parseInt(expires);
		} catch(Exception e) {
		}
		newUrlCache(url,expire);
	}

	/**
	*/
	public UrlPageCacheNode getUrlPageCacheNode(String url) {
		try {
			MultiConnection con=mmb.getConnection();
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("select * from "+mmb.baseName+"_urlpagecache where url='"+url+"'");
			if (rs.next()) {
				UrlPageCacheNode n=new UrlPageCacheNode();
				n.number=rs.getInt(1);
				n.url=rs.getString(2);
				n.time_c=rs.getInt(3);
				n.state=rs.getInt(4);
				stmt.close();
				con.close();
				return(n);
			}
			stmt.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return(null);
	}

	/**
	* inserts a url and calcs the relations to id's
	* and inserts those.
	*/
	/*
	public boolean newUrlCache(String url,int expire) {
		scanmoduleInterface m=(scanmoduleInterface)mmb.getBaseModule("scanmodule");		
		sessionsInterface sessions=(sessionsInterface)mmb.getBaseModule("SESSION");		
		HttpServletRequest req=(HttpServletRequest)(new EmptyHttpServletRequest());
		System.out.println(m.calcPage(url,req,0)); 
		Vector numbers=new Vector();
		if (sessions!=null) {
			sessionInfo session=sessions.getSession(req,req.getSessionName());
			String cachetype=session.getValue("CACHE");
			String line=session.getSetString("PAGECACHENODES");
			System.out.println("PAGELINE="+line);
			if (line==null) line="";
			StringTokenizer tok=new StringTokenizer(line,",\n\r\t",true);
			while(tok.hasMoreTokens()) {
				String t=tok.nextToken();
				System.out.println("PAGECACHENODE="+t);
				numbers.addElement(new Integer(t));	
			}
		}
		// end temp code

		// insert the url
		int urlnumber=insertUrl(url);	
		if (urlnumber!=-1) {
			createUrlIdRelations(urlnumber,numbers);
			if (expire!=-1) createUrlTimeRelations(urlnumber,expire);
		}
		return(true);
	}
	*/

	/*
	public void createUrlIdRelations(int urlnumber,Vector numbers) {
		Enumeration t=numbers.elements();
		while (t.hasMoreElements()) {
			Integer i=(Integer)t.nextElement();
			int nodeid=i.intValue();
			// node allready in the database ? if no create it
			if (getIdPageCacheNode(nodeid)==null) {
				insertID(nodeid);		
			}
			// create the relation between the page and the id
			insertUI(urlnumber,nodeid);
		}
	}
	*/

	/*
	public void createUrlTimeRelations(int urlnumber,int expire) {
		try {
			MultiConnection con=mmb.getConnection();
			PreparedStatement stmt=con.prepareStatement("insert into "+mmb.baseName+"_timpagecache values(?,?,?)");
				int time=(int)((DateSupport.currentTimeMillis()/1000)); 
				stmt.setInt(1,urlnumber);
				stmt.setInt(2,time+expire);
				stmt.setInt(3,expire);
				stmt.executeUpdate();
				stmt.close();
				con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	
	/**
	*/
	public int insertUrl(String url) {
		int number=-1;
		try {
			MultiConnection con=mmb.getConnection();
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("select max(number) from "+mmb.baseName+"_urlpagecache");
			if (rs.next()) {
			 	number=rs.getInt(1);
				number++;
			} else {
				number=1;
			}
			stmt.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			MultiConnection con=mmb.getConnection();
			PreparedStatement stmt=con.prepareStatement("insert into "+mmb.baseName+"_urlpagecache values(?,?,?,?)");
				stmt.setInt(1,number);
				stmt.setString(2,url);
				stmt.setInt(3,0);
				stmt.setInt(4,0);
				stmt.executeUpdate();
				stmt.close();
				con.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return(-1);
		}
		return(number);	
	}


	/**
	*/
	public void insertUI(int snumber,int dnumber) {
		try {
			MultiConnection con=mmb.getConnection();
			PreparedStatement stmt=con.prepareStatement("insert into "+mmb.baseName+"_uipagecache values(?,?)");
				stmt.setInt(1,snumber);
				stmt.setInt(2,dnumber);
				stmt.executeUpdate();
				stmt.close();
				con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	/**
	*/
	public int insertID(int number) {
		try {
			MultiConnection con=mmb.getConnection();
			PreparedStatement stmt=con.prepareStatement("insert into "+mmb.baseName+"_idpagecache values(?,?)");
				stmt.setInt(1,number);
				stmt.setInt(2,0);
				stmt.executeUpdate();
				stmt.close();
				con.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return(-1);
		}
		return(number);	
	}

	/**
	*/
	public IdPageCacheNode getIdPageCacheNode(int id) {
		try {
			MultiConnection con=mmb.getConnection();
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("select * from "+mmb.baseName+"_idpagecache where number='"+id+"'");
			if (rs.next()) {
				IdPageCacheNode n=new IdPageCacheNode();
				n.number=rs.getInt(1);
				n.state=rs.getInt(2);
				stmt.close();
				con.close();
				return(n);
			}
			stmt.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return(null);
	}
}
