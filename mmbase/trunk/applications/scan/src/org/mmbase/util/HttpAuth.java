package org.mmbase.util;

import java.util.*;
import java.io.*;
import java.net.*;

import javax.servlet.http.*;


/**
 *
 */

/*
 */
public class HttpAuth {

    public static final int SC_OK = 200;
    public static final int SC_CREATED = 201;
    public static final int SC_ACCEPTED = 202;
    public static final int SC_NO_CONTENT = 204;
    public static final int SC_MOVED_PERMANENTLY = 301;
    public static final int SC_MOVED_TEMPORARILY = 302;
    public static final int SC_NOT_MODIFIED = 304;
    public static final int SC_BAD_REQUEST = 400;
    public static final int SC_UNAUTHORIZED = 401;
    public static final int SC_FORBIDDEN = 403;
    public static final int SC_NOT_FOUND = 404;
    public static final int SC_INTERNAL_SERVER_ERROR = 500;
    public static final int SC_NOT_IMPLEMENTED = 501;
    public static final int SC_BAD_GATEWAY = 502;
    public static final int SC_SERVICE_UNAVAILABLE = 503;

	static Hashtable p_base64;

	private static String checklocalurl=null;

	private static Hashtable LocalCache = new Hashtable(); 

	public HttpAuth() {
	}

	private static Hashtable readPasswordFromDisk() {
		Hashtable results=new Hashtable();

		// get us a propertie reader	
		ExtendedProperties Reader=new ExtendedProperties();

		// load the properties file of this server

		String accountconfig=System.getProperty("mmbase.config");
		Hashtable accounts = Reader.readProperties(accountconfig+"/accounts.properties");

		// oke try loading all these modules and start em up
		for (Enumeration e=accounts.keys();e.hasMoreElements();) {
			String key=(String)e.nextElement();
			String value=(String)accounts.get(key);
			//System.out.println("name="+key+"=Basic "+Base64.encode(key+" "+value));
			results.put("Basic "+Base64.encode(key+":"+value),key);
		}
		return(results);
	}

	/**
	 * 
	 */
	public static String getLoginName2(String mimel) {

		String name=(String)p_base64.get(mimel);
		if (name==null) { 
			return(null);
		} else {
			return(name);
		}
	}


	public static boolean checkUser(String wname,String mimeline) {
		String name=(String)p_base64.get(mimeline);
		//System.out.println(p_base64);
		if (name!=null && name.equals(wname)) { 
			return(true);
		} else {
		if 	(checklocalurl!=null) {
			name=getLoginName(mimeline);
			if (name==null) return(false);
			boolean result=checkLocal(name,"www",mimeline,"basic");
			if (result) {
				p_base64.put(mimeline,name);
				return(true);
			} 
		}
		}
		return(false);

		// mimeline=mimeline.substring(mimeline.indexOf(' ')+1);
		//System.out.println(mimeline);
		//System.out.println(Base64.decode(mimeline));
		/*
		if (name!=null && name.equals(wname)) { 
			return(true);
		} else {
			return(false);
		}
		*/
	}

	/** 
	 * Authenticates a user, If the user cannot be authenticated a login-popup will appear
	 * @param server server-account. (for exameple 'film' or 'www')
	 * @param level loginlevel. (for example 'Basic' or 'MD5')
     * @exception AuthorizationException if the authorization fails. 
     * @exception NotLoggedInException if the user hasn't logged in yet. 
	 */
	public static String getAuthorization(HttpServletRequest req,HttpServletResponse res,String server, String level) throws AuthorizationException, NotLoggedInException {
	
		//System.out.println("BASE="+p_base64);
		if (p_base64==null) {
			p_base64=readPasswordFromDisk();
		}
		//System.out.println("BASE2="+p_base64);

        String passwd = ((String)req.getHeader("Authorization"));
		//System.out.println("passwd="+passwd);

		/** Is user authorized yet? **/
        if (passwd==null) {
    			res.setStatus(SC_UNAUTHORIZED);
				res.setHeader("WWW-Authenticate","Basic realm=\""+server+"\"");
				throw new NotLoggedInException("Not logged in Exception (Generate page)");
		} else {
			//System.out.println("passwd="+passwd);
			/** Get name from user **/
			String name=getLoginName(passwd);

			/** Authenticate user **/
			if (!checkUser(name,passwd)) {
    			res.setStatus(SC_UNAUTHORIZED);
				res.setHeader("WWW-Authenticate","Basic realm=\""+server+"\"");
				throw new AuthorizationException("User authorization failed (Generate page)");
			} else {
				//if (users!=null) users.addUser(name,requestInfo);
				return(name);
			}
	  	}
	} 

	public static String getRemoteUser(HttpServletRequest req) {
        String mimeline = ((String)req.getHeader("Authorization"));
		String name=(String)p_base64.get(mimeline);
		return(name);
	}

	public static String getRemoteUser(scanpage sp) {
		HttpServletRequest req=sp.req;
        String mimeline = ((String)req.getHeader("Authorization"));
		String name=(String)p_base64.get(mimeline);
		return(name);
	}



	static boolean checkLocal(String name, String area, String passwd, String level) {
		System.out.println("performing a remore check at : "+checklocalurl+" for "+name);
		String passwd2=(String)LocalCache.get(name+area);
		if(passwd2==null) { // Not in cache so retrieve
			try {
				int port=80;
				String machine,page;
				String tmp=checklocalurl;
				int pos=tmp.indexOf('/');
				if (pos!=-1) {
					machine=tmp.substring(0,pos);
					page=tmp.substring(pos);	
				} else {
					machine=tmp;
					page="/";
				}
				pos=machine.indexOf(':');
				if (pos!=-1) {
					try {
						port=Integer.parseInt(machine.substring(pos));
					} catch (Exception e) {}
					machine=machine.substring(0,pos);
				}
				Socket socket=new Socket(machine,port);
				BufferedInputStream instream=new BufferedInputStream(socket.getInputStream());
				BufferedOutputStream outstream=new BufferedOutputStream(socket.getOutputStream());
				write(outstream,"GET "+page+" HTTP/1.0\nContent-Type: vpro/ballyhoo\nUser-Agent: VPRO/James remote password check\nAuthorization: "+passwd+"\n\n");
				String result = read(instream);
				if (result.indexOf("401")==-1) {
					LocalCache.put(name+area,passwd);
					return(true);
				} else {
					return(false);
				}
			} catch (Exception e) {}
		} else {
			if (passwd2.equals(passwd)) {
				return(true);
			} else {
				return(false);
			}
		}
		return(false);
	}


	private static int write(BufferedOutputStream out,String line) {
		int len=line.length();
		byte[] buffer=new byte[len];

		line.getBytes(0,len,buffer,0);
		try {
			out.write(buffer,0,len);
			out.flush();
		} catch(IOException e) {
			return(-1);
		} //catch (Exception e) { }
		//System.out.println("Send "+line);
		return(len);
	}


	private static String read(BufferedInputStream in) {
		StringBuffer str=new StringBuffer();
		int rtn=0;

		do {
			try {
				rtn=in.read();
			} catch(IOException e) {
				return(null);
			}
			if (rtn==-1) return(null);
			str.append((char)rtn);
		} while(rtn!='\n');
		return(str.toString());
	}



	/**
	 * 
	 */
	public static String getLoginName(String mimel) {
		int i,a,b,c,d;
		char sa,sb,sc,sd;
		String decodeline;
		String decodedline;
		String encodeline;
		String encodedline;
		String temp,name;
		Hashtable newobject;

		decodeline=mimel.substring(mimel.indexOf(' ')+1);
		name=(String)p_base64.get(decodeline);
		if (name==null) { 
			decodedline=Base64.decode(decodeline);

			try {
				name=decodedline.substring(0,decodedline.indexOf(':'));
			} catch (Exception e) {
				System.out.println(decodedline);
			}
			newobject = new Hashtable();	
			newobject.put("base64",mimel.substring(mimel.indexOf(' ')+1));
			if (mimel!=null && name!=null) {
				p_base64.put(mimel.substring(mimel.indexOf(' ')+1),name);
			} else {
				//System.out.println("auth.java: no mimeline for "+name+" dec="+decodeline);
			}
		}
		return(name);
	}

	public static void setLocalCheckUrl(String url) {
		if (checklocalurl!=null) {
			System.out.println("HpptAuth -> SetLocalUrl ALLREADY SET !!!!");
		} else {
			System.out.println("HpptAuth -> SetLocalUrl to "+url);
			checklocalurl=url;
		}
	}
}
