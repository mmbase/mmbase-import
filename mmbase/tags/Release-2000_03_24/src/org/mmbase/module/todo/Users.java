/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.module;

import java.lang.*;
import java.util.*;
import java.io.*;

import org.mmbase.util.*;

/**
 * Users.
 *
 * @author Daniel Ockeloen
 */
public class Users extends ProcessorModule implements UsersInterface,Runnable {

	Thread kicker=null;
	private Hashtable userList = new Hashtable();
	private Vector listeners = new Vector();
	objectstore os;
	authInterface auth;

	public void reload() {
		os=(objectstore)getModule("os");
		auth=(authInterface)getModule("auth");
	}

	public void onload() {
	}

	public void unload() {
		stop();
	}

	public void shutdown() {
	}

	public void init() {
		os=(objectstore)getModule("os");
		auth=(authInterface)getModule("auth");
	}

	/**
	 * Users
	 */
	public Users() {
		System.out.println("Users started");
		this.start();
	}

	/**
	 * Starts the admin Thread.
	 */
	public void start() {
		/* Start up the main thread */
		if (kicker == null) {
			kicker = new Thread(this);
			kicker.start();
		}
	}
	
	public void addListen(UsersCallBackInterface wanted) {
		listeners.addElement(wanted);
	}


	public void removeListen(UsersCallBackInterface wanted) {
		listeners.removeElement(wanted);
	}

	public String getProperty(String name,String property) {
		if (userList.containsKey(name)) {
			return(((User)userList.get(name)).getProperty("private",property));	
		} else {
			return(null);
		} 
	}

	public String getServletProperty(String table,String name,String property, int type) {
		String tmp;

		// System.out.println("G="+table+" "+name+" "+property+" ");
		if (userList.containsKey(name)) {
			User nu = (User)userList.get(name);
			if (!nu.CacheHashAvail("servlet_"+type+"_"+table)) {
				if (!nu.loadPropertyCache("servlet_"+type+"_"+table,type,100)) {
					nu.addPropertyCache("servlet_"+type+"_"+table,type,100);
				}
			}
			return(nu.getProperty("servlet_"+type+"_"+table,property));	
		} else {
			return(null);
		} 
	}


	public String delServletProperty(String table,String name,String property, int type) {
		String tmp;

		// System.out.println("G="+table+" "+name+" "+property+" ");
		if (userList.containsKey(name)) {
			User nu = (User)userList.get(name);
			if (!nu.CacheHashAvail("servlet_"+type+"_"+table)) {
				if (!nu.loadPropertyCache("servlet_"+type+"_"+table,type,100)) {
					nu.addPropertyCache("servlet_"+type+"_"+table,type,100);
				}
			}
			return(nu.delProperty("servlet_"+type+"_"+table,property));	
		} else {
			return(null);
		} 
	}


	public Hashtable getServletProperties(String table,String name, int type) {
		String tmp;

		//System.out.println("G="+table+" "+name+" "+property+" ");
		if (userList.containsKey(name)) {
			User nu = (User)userList.get(name);
			if (!nu.CacheHashAvail("servlet_"+type+"_"+table)) {
				if (!nu.loadPropertyCache("servlet_"+type+"_"+table,type,100)) {
					nu.addPropertyCache("servlet_"+type+"_"+table,type,100);
				}
			}
			return(nu.getProperties("servlet_"+type+"_"+table));	
		} else {
			return(null);
		} 
	}


	public String getModuleProperty(String table,String name,String property, int type) {
		String tmp;

		if (userList.containsKey(name)) {
			User nu = (User)userList.get(name);
			if (!nu.CacheHashAvail("module_"+type+"_"+table)) {
				if (!nu.loadPropertyCache("module_"+type+"_"+table,type,100)) {
					nu.addPropertyCache("module_"+type+"_"+table,type,100);
				}
			}
			return(nu.getProperty("module_"+type+"_"+table,property));	
		} else {
			return(null);
		} 
	}



	public boolean setServletProperty(String table,String name,String property, String value, int type) {
		//System.out.println("S="+table+" "+name+" "+property+" "+value);
		if (userList.containsKey(name)) {
			User nu = (User)userList.get(name);
			if (!nu.setProperty("servlet_"+type+"_"+table,property,value)) {
				if (!nu.loadPropertyCache("servlet_"+type+"_"+table,type,100)) {
					nu.addPropertyCache("servlet_"+type+"_"+table,type,100);
				}
				nu.setProperty("servlet_"+type+"_"+table,property,value);
			}
			return(true);
		} else {
			return(false);
		} 
	}


	public boolean setModuleProperty(String table,String name,String property, String value, int type) {
		if (userList.containsKey(name)) {
			User nu = (User)userList.get(name);
			if (!nu.setProperty("module_"+type+"_"+table,property,value)) {
				if (!nu.loadPropertyCache("module_"+type+"_"+table,type,100)) {
					nu.addPropertyCache("module_"+type+"_"+table,type,100);
				}
				nu.setProperty("module_"+type+"_"+table,property,value);
			}
			return(true);
		} else {
			return(false);
		} 
	}

	public boolean addUser(String name) {
		// was this user allready in memory ?
		if (userList.containsKey(name)) {
			// user was allready added to the user pool
			return(true);
		} else {
			User nu = new User(os,name);
			// Check if user is in the object store
			if (nu.loadPropertyCache("private",1,100)) {
				// user in os so load needed parts from objectstore
			} else {
				// Set user defaults
			}
			userList.put(name,nu);	
		}
		return(true);
	}


	public boolean addUser(String name,requestInfo req) {
		// was this user allready in memory ?
		if (userList.containsKey(name)) {
			// user was allready added to the user pool
			return(true);
		} else {
			// its a new user so create a clean setup
			// Check if user is in the object store
			User nu = new User(os,name);
			if (nu.loadPropertyCache("private",1,100)) {
				// user in os so load needed parts from objectstore
				if (nu.loadPropertyCache("private",0,100)) {
				nu.setProperty("private","LastHost",req.clientsocket.getInetAddress().getHostName());
				nu.setProperty("private","LastLogin",(new Date()).toString());
				}
			} else {
				// create a default hash for private info
				nu.addPropertyCache("private",1,100);
				nu.setProperty("private","FirstLogin",(new Date()).toString());
				nu.setProperty("private","HostName",req.clientsocket.getInetAddress().getHostName());
			}
			userList.put(name,nu);	
		}
		return(true);
	}

	public boolean storeUser(String name) {
		User U=(User)userList.get(name);
		if (U!=null) {
			return(true);
		} else {
			return(false);
		}
	}

	public boolean removeUser(String name) {
		return(true);
	}

	/**
	 * Stops the admin Thread.
	 */
	public void stop() {
		/* Stop thread */
		if (kicker!=null) {
			kicker.stop();
			kicker = null;
		}
	}
	
	public void run() {
		while (kicker!=null) {
			try {
				doWork();
			} catch (Exception e) {
				System.out.println("User Run Exception : ");
				e.printStackTrace();
			}
		}
	}
	

	public void signalActive(String username) {
		User user=(User)userList.get(username);
		if (user!=null) user.signalActive();
	}

	/**
	 * Main admin loop, ones every admin.update it gets run to perform
	 * a check on the whole server, at this time mosty number of workers
	 * in each pool.
	 */
	public void doWork() {
		String username;
		User user;
		UsersCallBackInterface callb;

		while (kicker!=null) {
			try {Thread.sleep(30*1000);} catch (InterruptedException e){}
			for (Enumeration e=userList.keys();e.hasMoreElements();) {
				username=(String)e.nextElement();
				user=(User)userList.get(username);
				if (!username.equals("other") && user.timeOut()) {
				//	System.out.println("Time out on user : "+username);
					for (Enumeration f=listeners.elements();f.hasMoreElements();) {
						callb=(UsersCallBackInterface)f.nextElement();	
						callb.userTimeout(username);
						userList.remove(username);
					}
				}
			}
		}

	}

	public ballyhooMsg inBox(ballyhooMsg inmsg) {
		String cmd;
		Object target;
		ballyhooMsg bh=null;
		System.out.println("msg to user:"+inmsg.msg);
		return(bh);
	}

	Hashtable initparam2hashtable(String line) {
		Hashtable hashtmp = new Hashtable();
		if (line==null) return(null);
		StringTokenizer tok = new StringTokenizer(line,",\n\r");
		while (tok.hasMoreTokens()) {
			hashtmp.put(tok.nextToken(),"none");
		}
		return(hashtmp);
	}

	/**
	 * Stops the admin Thread.
	 */
	 public Hashtable state() {
		state.put("Users",""+userList.size());
		return(state);
	 }

	public String getModuleInfo() {
		return("Users provide a way to keep info on users");
	}

	public Vector getList(scanpage sp, StringTagger tagger, String inputline) {
    	String line = Strip.DoubleQuote(inputline,Strip.BOTH);
		if (line.indexOf("users")==0) {
			Vector results=new Vector();
			for (Enumeration e=userList.keys();e.hasMoreElements();) {
				String val=(String)e.nextElement();
				results.addElement(val);
				results.addElement(auth.getEmail(val,"www"));
			}
			return(results);
		}
		return(new Vector());
	}

}
