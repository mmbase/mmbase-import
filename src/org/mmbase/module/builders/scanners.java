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

import javax.servlet.http.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;

/**
 * @author Daniel Ockeloen
 * @author David V van Zeventer
 * @version 21 Dec 1998
 */

/**
 * class is imcomplete and isn't used yet.
 */
public class scanners extends MMObjectBuilder implements MMBaseObserver {

	public final static String buildername = "scanners";
	public static java.util.Properties driveprops= null;

	public scanners() {
	}

	/*
	public scanners(MMBase m) {
		this.mmb=m;
		this.tableName="scanners";
		this.description="Scanners";
		this.dutchSName="Scanners";
		init();
		m.mmobjs.put(tableName,this);
		//mmb.addRemoteObserver("scanners",this);
		//mmb.addLocalObserver("scanners",this);
	}
	*/

	
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
				+", scannertype char(32)"
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
		String scannertype=node.getStringValue("scannertype");
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
				stmt.setString(5,scannertype);
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


	public boolean commit(MMObjectNode node) {
		boolean res=super.commit(node);
		return(res);
	}
	public String getGUIIndicator(MMObjectNode node) {
		return(null);
	}
	public String getGUIIndicator(String field,MMObjectNode node) {
		return(null);
	}
	public String getDefaultUrl(int src) {
		return(null);
	}

	public boolean nodeRemoteChanged(String number,String builder,String ctype) {
		super.nodeRemoteChanged(number,builder,ctype);
		return(nodeChanged(number,builder,ctype));
	}

	public boolean nodeLocalChanged(String number,String builder,String ctype) {
		super.nodeLocalChanged(number,builder,ctype);
		return(nodeChanged(number,builder,ctype));
	}

	public boolean nodeChanged(String number,String builder,String ctype) {
		MMObjectNode node=getNode(number);
		if (node!=null) {
			System.out.println("scanners node="+node.toString());
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

	/**
	* handles commands for scanners found in this machine
	*/
	private void doAction(MMObjectNode node) {
		String name=node.getStringValue("name");
		String cdtype=node.getStringValue("scannertype");
		String state=node.getStringValue("state");
		String info=node.getStringValue("info");

		if (!state.equals("busy")) {
			System.out.println("Action called on scanner : "+name);
			// start a thread to handle command
			new scannersProbe(this,node);
		} else {
			System.out.println("Problem action called on scanner: "+name+" while it was busy");
		}
	}
     	
        public Vector getList(HttpServletRequest req, StringTagger tagger, StringTokenizer tok) {
                if (tok.hasMoreTokens()) {
                        String cmd=tok.nextToken();	//Retrieving command.
                        if (cmd.equals("getdir")) return(getHTMLDir(tagger,tok));
                }
                return(null);
        }
	
	/**
	 * getHTMLDir: This method transforms the listing sothat it looks 'nice' inside a browser.
	 */
        public Vector getHTMLDir(StringTagger tagger, StringTokenizer tok) {

                Vector result=new Vector();                		
		String id=tagger.Value("NODE");
                MMObjectNode node=getNode(id);	//Get the scannernodenr.
                if (node!=null) {
			String filepath = tok.nextToken();	//Path where the scanserver drops its images. 
			node.setValue("info","droppath="+filepath);	//Store filepath in snodes' info field.

		        String info=(String)getValue(node,"getdir(info)"); //Call getValue() to retrieve list.
			StringTokenizer info_st = new StringTokenizer(info,"\n\r");
			while (info_st.hasMoreTokens()){
				result.addElement(info_st.nextToken());
			}
                }
                tagger.setValue("ITEMS","2");
                return(result);
        }

	/**
         * getDir: Returns a string containing all the files from the scanner.
         */
	public String getDir(MMObjectNode snode) {
	        String methodname = "getDir";
		String list= new String();
		String path=null;
		Vector entries = null;

		System.out.println(buildername+": "+methodname+": Executing getDir method.");
		String scannername = snode.getStringValue("name");
		System.out.println(buildername+": "+methodname+": getDir called for scanner: "+scannername);

                String info = snode.getStringValue("info");	//Retrieve path from snodes' info field.
		StringTokenizer info_tok = new StringTokenizer(info,"=");
		while (info_tok.hasMoreTokens()){
			path = info_tok.nextToken();
		}
		if (path.startsWith("/")){
			System.out.println(buildername+": "+methodname+": Path detected.");			
			entries = doDir(path);
                	for (Enumeration e = entries.elements(); e.hasMoreElements();){
                		list+= (String)e.nextElement();
                	}
		}else{
			System.out.println(buildername+": "+methodname+": Invalid path -> path: "+path);
			list+="Invalid path -> path: "+path;
		}			
		System.out.println(buildername+": "+methodname+": Final list contains:"+list);
                return list;
	}

	/**
	 * doDir: This method returns a list of all files found at path: filespec (recursive),
	 *	  using the James util methods DirectoryLister.getDirectories() 
	 *	  and DirectoryLister.sortDirectories().
         */ 
	private Vector doDir(String filespec){
		String methodname = "doDir";
		Vector entries = null;
		Vector unsortedresult = null;
                String comparefield   = "modtime";
	        DirectoryLister dirlister = new DirectoryLister();

		System.out.println(buildername+": "+methodname+": Getting files from: "+filespec);
	        
		unsortedresult = dirlister.getDirectories(filespec);    //Retrieve all filepaths.
	        entries = dirlister.sortDirectories(unsortedresult,comparefield); //Entries=filepaths and moddates.

		System.out.println(buildername+": "+methodname+": Done.");
		return entries;
	}

	public Object getValue(MMObjectNode node,String field) {
		if (field.equals("getdir(info)")) {
			// send the command to get the dir
			node.setValue("state","getdir");
			node.commit();
			// should have a wait !!!!
			//try {Thread.sleep(15000);} catch (InterruptedException e) {}
			boolean changed=false;
			MMObjectNode newnode=null;
			while (!changed) {	
				waitUntilNodeChanged(node);
				newnode=getNode(node.getIntValue("number"));
				String state=newnode.getStringValue("state");
				System.out.println("WAIT RESULT="+state);
				if (state.equals("waiting")) changed=true;
			}
			String val=newnode.getStringValue("info");
			System.out.println("SCANNER-1->val="+val);
			return(val);
		}
		return(null);
	}
}
