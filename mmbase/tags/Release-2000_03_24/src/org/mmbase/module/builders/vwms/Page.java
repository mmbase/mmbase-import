/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.module.builders.vwms;

import java.util.*;
import java.sql.*;

import javax.servlet.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;
import org.mmbase.module.builders.*;

/**
 * @author Daniel Ockeloen
 */

public class Page extends Vwm implements MMBaseObserver {
	sessionsInterface sessions;
	Vector	changedNodes=new Vector();
	Vector	changedUrls=new Vector();
	PageUrlProbe urlprobe;

	public Page() {
		System.out.println("Page ready for action");
	}


	public boolean probeCall() {
		// this is silly need to be so that init allows this !1
		System.out.println("Page probe");

		// HACK HACK REMOVED BECAUSE OF LOOP PROBLEMS
		// RICO
		boolean rtn=false;
		if (rtn) return(true);
		// HACK HACK

		Vwms.mmb.addRemoteObserver("teasers",this);
		Vwms.mmb.addLocalObserver("teasers",this);

		/*
		sessions=(sessionsInterface)Vwms.mmb.getBaseModule("SESSION");		
		createUIRelPageCache();
		createIdPageCache();
	    createUrlPageCache();
	    createTimePageCache();
		*/
		if (urlprobe==null) urlprobe=new PageUrlProbe(this);
		handleExpired();
		return(true);
	}



	public boolean createUrlPageCache() {
		// create the main object table
		try {
			MultiConnection con=Vwms.mmb.getConnection();
			Statement stmt=con.createStatement();
			stmt.executeUpdate("create table "+Vwms.mmb.baseName+"_urlpagecache  ( number integer not null "
										+", url char(128) not null"
										+", calc_t integer not null"
										+", state integer not null"
										+", primary key(number))");
			stmt.close();
			con.close();
			return(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return(false);
	}

	public boolean createIdPageCache() {
		// create the main object table
		try {
			MultiConnection con=Vwms.mmb.getConnection();
			Statement stmt=con.createStatement();
			stmt.executeUpdate("create table "+Vwms.mmb.baseName+"_idpagecache  ( number integer not null "
										+", state integer not null"
										+", primary key(number))");
			stmt.close();
			con.close();
			return(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return(false);
	}


	public boolean createTimePageCache() {
		// create the main object table
		try {
			MultiConnection con=Vwms.mmb.getConnection();
			Statement stmt=con.createStatement();
			stmt.executeUpdate("create table "+Vwms.mmb.baseName+"_timpagecache  ( number integer not null "
										+", expire integer not null"
										+", interval integer not null"
										+", primary key(number))");
			stmt.close();
			con.close();
			return(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return(false);
	}


	public boolean createUIRelPageCache() {
		// create the main object table
		try {
			MultiConnection con=Vwms.mmb.getConnection();
			Statement stmt=con.createStatement();
			stmt.executeUpdate("create table "+Vwms.mmb.baseName+"_uipagecache  ( snumber integer not null "
										+", dnumber integer not null"
										+")");
			stmt.close();
			con.close();
			return(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return(false);
	}


	public boolean createUTRelPageCache() {
		// create the main object table
		try {
			MultiConnection con=Vwms.mmb.getConnection();
			Statement stmt=con.createStatement();
			stmt.executeUpdate("create table "+Vwms.mmb.baseName+"_utpagecache  ( snumber integer not null "
										+", dnumber integer not null"
										+")");
			stmt.close();
			con.close();
			return(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return(false);
	}

	/**
	* inserts a url and calcs the relations to id's
	* and inserts those.
	*/
	public boolean insertUrlPageCache(String url) {
		scanmoduleInterface m=(scanmoduleInterface)Vwms.mmb.getBaseModule("scanmodule");		
		HttpServletRequest req=(HttpServletRequest)(new EmptyHttpServletRequest());
		System.out.println(m.calcPage(url,req,0)); 
		Vector numbers=new Vector();
		if (sessions!=null) {
			sessionInfo session=sessions.getSession(req,req.getSessionName());
			String cachetype=session.getValue("CACHE");
			String line=session.getSetString("PAGECACHENODES");
			StringTokenizer tok=new StringTokenizer(line,",\n\r\t",true);
			while(tok.hasMoreTokens()) {
				String t=tok.nextToken();
				System.out.println("Page -> PAGECACHENODE="+t);
				numbers.addElement(new Integer(t));	
			}
		}
		// end temp code

		// insert the url
		int urlnumber=insertUrl(url);	
		if (urlnumber!=-1) {
			createUrlIdRelations(urlnumber,numbers);
			createUrlTimeRelations(urlnumber,180);
		}
		return(true);
	}

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


	public void createUrlTimeRelations(int urlnumber,int expire) {
		try {
			MultiConnection con=Vwms.mmb.getConnection();
			PreparedStatement stmt=con.prepareStatement("insert into "+Vwms.mmb.baseName+"_timpagecache values(?,?,?)");
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
			MultiConnection con=Vwms.mmb.getConnection();
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("select max(number) from "+Vwms.mmb.baseName+"_urlpagecache");
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
			MultiConnection con=Vwms.mmb.getConnection();
			PreparedStatement stmt=con.prepareStatement("insert into "+Vwms.mmb.baseName+"_urlpagecache values(?,?,?,?)");
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
			MultiConnection con=Vwms.mmb.getConnection();
			PreparedStatement stmt=con.prepareStatement("insert into "+Vwms.mmb.baseName+"_uipagecache values(?,?)");
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
			MultiConnection con=Vwms.mmb.getConnection();
			PreparedStatement stmt=con.prepareStatement("insert into "+Vwms.mmb.baseName+"_idpagecache values(?,?)");
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
	public UrlPageCacheNode getUrlPageCacheNode(String url) {
		try {
			MultiConnection con=Vwms.mmb.getConnection();
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("select * from "+Vwms.mmb.baseName+"_urlpagecache where url='"+url+"'");
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
	*/
	public IdPageCacheNode getIdPageCacheNode(int id) {
		try {
			MultiConnection con=Vwms.mmb.getConnection();
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("select * from "+Vwms.mmb.baseName+"_idpagecache where number='"+id+"'");
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


	public boolean nodeRemoteChanged(String number,String builder,String ctype) {
		return(nodeChanged(number,builder,ctype));
	}

	public boolean nodeLocalChanged(String number,String builder,String ctype) {
		return(nodeChanged(number,builder,ctype));
	}

	public boolean nodeChanged(String number,String builder, String ctype) {
		System.out.println("Page sees that : "+number+" has changed type="+ctype);
		pagemakers bul=(pagemakers)Vwms.mmb.getMMObject("pagemakers");
		if (bul==null) { 
			System.out.println("Page can't use pagemakers");
			return(false);
		}
		if (ctype.equals("c")) {
			if (!changedNodes.contains(number)) {
				changedNodes.addElement(number);		
				System.out.println("Page -> Changed="+changedNodes);
				// next is allways >0 but hell its only a demo
				if (changedNodes.size()>0) handleChange();
			}
		}
		return(true);
	}

	public boolean handleChange() {
		Vector vals=changedNodes;
		changedNodes=new Vector();

		// create the needed ( number, number ) line.
		String line=null;
		Enumeration t=vals.elements();
		while (t.hasMoreElements()) {
			String part=(String)t.nextElement();
			if (line==null) {
				line=part;
			} else {
				line+=","+part;
			}
		}
		try {
			MultiConnection con=Vwms.mmb.getConnection();
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("select url from "+Vwms.mmb.baseName+"_urlpagecache a, "+Vwms.mmb.baseName+"_uipagecache b, "+Vwms.mmb.baseName+"_idpagecache c where a.number=b.snumber and b.dnumber=c.number and c.number in ("+line+")");
			while (rs.next()) {
			 	String url=rs.getString(1);
				if (!changedUrls.contains(url)) {
					changedUrls.addElement(url);		
					if (urlprobe!=null) urlprobe.newUrl();
				}
			}
			stmt.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return(true);
	}


	public boolean handleExpired() {
		try {
			int time=(int)((DateSupport.currentTimeMillis()/1000)); 
			MultiConnection con=Vwms.mmb.getConnection();
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("select a.url,b.number,b.expire,b.interval from "+Vwms.mmb.baseName+"_urlpagecache a, "+Vwms.mmb.baseName+"_timpagecache b where a.number=b.number and b.expire<"+time);
			while (rs.next()) {
			 	String url=rs.getString(1);
			 	int number=rs.getInt(2);
			 	int expire=rs.getInt(3);
			 	int interval=rs.getInt(4);
				if (!changedUrls.contains(url)) {
					changedUrls.addElement(url);		
					if (urlprobe!=null) urlprobe.newUrl();
					System.out.println("Page -> TIME NOW="+time+" DATABASE="+expire);
					setNewExpire(number,time,expire,interval);
				}
			}
			stmt.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return(true);
	}


	public void setNewExpire(int urlnumber,int time,int expire, int interval) {
		try {
			MultiConnection con=Vwms.mmb.getConnection();
			PreparedStatement stmt=con.prepareStatement("update "+Vwms.mmb.baseName+"_timpagecache set expire="+(time+interval)+" where number="+urlnumber);
			stmt.executeUpdate();
			stmt.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean checkDirtyUrls() {
		System.out.println("Page -> URLSIZE="+changedUrls.size());
		pagemakers bul=(pagemakers)Vwms.mmb.getMMObject("pagemakers");
		if (bul!=null) { 
		  while (changedUrls.size()>0) {
				MMObjectNode node=bul.getNode(1452549);
				if (node!=null) {
					String state=node.getStringValue("state");
					System.out.println("Page -> PAGE STATE="+state);
					if (state!=null && state.equals("waiting")) {
						node.setValue("info",(String)changedUrls.elementAt(0));
						changedUrls.removeElementAt(0);
						node.setValue("state","calcpage");
						node.commit();

						boolean changed=false;
						MMObjectNode newnode=null;
						while (!changed) {	
							System.out.println("Page -> PAGE WAIT");
							bul.waitUntilNodeChanged(node);
							newnode=bul.getNode(node.getIntValue("number"));
							state=newnode.getStringValue("state");
							System.out.println("Page -> PAGE WAIT RESULT="+state);
							if (state.equals("waiting")||state.equals("error")) changed=true;
						}
					} else {
						try {Thread.sleep(30000);} catch (InterruptedException e) {}
					}
				} else {
					System.out.println("Darn Page can't find pagemaker");
				}
			}
		} else {
			System.out.println("Page can't use pagemakers");
		}
		return(true);
	}

}
