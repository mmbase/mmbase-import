/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;
/** 
 * is this one @depricated?
 */
public class HttpAuth {
    private static org.mmbase.util.logging.Logger log = org.mmbase.util.logging.Logging.getLoggerInstance(HttpAuth.class.getName());
            
    private static org.mmbase.module.core.MMBase mmbase = (org.mmbase.module.core.MMBase) org.mmbase.module.core.MMBase.getModule("mmbaseroot");
    
    private static String remoteAuthenticationHost = null;
    private static String remoteAuthenticationPage = null;
    private static int remoteAuthenticationPort = 80;    
    
    /** 
     * With a given mimeline, the username and password will be retrieved, and with it 
     * there will be looked if it is an valid login. If it is a valid login, with a rank higher 
     * or equals as Rank::BASICUSER, it will return a userid, otherwise null.
     * @param mimeline The mimeline of the request
     * @returns a userid for the given user, of <code>null</code> when something goes wrong
     */
    public static String checkUser(String mimeline) {
        String user_password = org.mmbase.util.Encode.decode("BASE64", mimeline.substring(6)); 
        java.util.HashMap userInfo = new java.util.HashMap();
        java.util.StringTokenizer t = new java.util.StringTokenizer(user_password, ":"); 
        if (t.countTokens() == 2) {
            userInfo.put("username", t.nextToken());
            userInfo.put("password", t.nextToken());
        }
        org.mmbase.security.UserContext user = null;
        try {
            user = mmbase.getMMBaseCop().getAuthentication().login("name/password", userInfo, null);
        }
        catch(org.mmbase.security.SecurityException se) {
            log.warn("user login of name: '" + userInfo.get("username") + "' failed("+se+")");
            return null;
        }
        // when login failed, or when it was an anonymous user, it will not work...
        if (user == null || user.getRank().getInt() < org.mmbase.security.Rank.BASICUSER_INT) {
            log.warn("user login of name: '" + userInfo.get("username") + "' failed(invalid)");
            return null;            
        }
        return user.getIdentifier();
    }

    /** 
     * Authenticates a user, If the user cannot be authenticated a login-popup will appear
     * @param server server-account. (for exameple 'film' or 'www')
     * @param level loginlevel. (for example 'Basic' or 'MD5')
     * @returns username foan exception will be thrown.
     * @exception AuthorizationException if the authorization fails. 
     * @exception NotLoggedInException if the user hasn't logged in yet. 
     */
    public static String getAuthorization(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse res,String server, String level) throws AuthorizationException, NotLoggedInException {    
        if (log.isDebugEnabled()) {
            log.debug("server: " + server + ", level: " + level);
        }
        String mimeline = getMimeline(req);
        if (mimeline == null) {
            log.info("page " + req.getRequestURI() + " is secure, and user not yet authenticated");
            res.setStatus(javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);
            res.setHeader("WWW-Authenticate","Basic realm=\""+server+"\"");
            throw new NotLoggedInException("Not logged in Exception");
        }                
        if(remoteAuthenticationHost == null) {
            // use local validating
            String username = checkUser(mimeline);
            if (username == null) {
                log.service("Logging in of user failed");
                res.setStatus(javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);
                res.setHeader("WWW-Authenticate","Basic realm=\""+server+"\"");
                throw new AuthorizationException("User authorization failed");
            } 
            else {
                log.service("User " + username + " succesfully logged in");
                return username;
            }
        }
        else {
            try {
                // use remote validation            
                java.util.StringTokenizer t = new java.util.StringTokenizer(org.mmbase.util.Encode.decode("BASE64", mimeline.substring(6)), ":"); 
                String username = t.nextToken();
                String password = t.nextToken();

                java.net.Socket socket = new java.net.Socket(remoteAuthenticationHost, remoteAuthenticationPort);
	        java.io.BufferedInputStream instream = new java.io.BufferedInputStream(socket.getInputStream());
	        java.io.BufferedOutputStream outstream = new java.io.BufferedOutputStream(socket.getOutputStream());

                // vpro???
	        write(outstream,"GET "+remoteAuthenticationPage+" HTTP/1.0\nContent-Type: vpro/ballyhoo\nUser-Agent: VPRO/James remote password check\nAuthorization: "+password+"\n\n");
	        String result = read(instream);
                if (result.indexOf("401") < 0) {
                    // 401 not found, thus granted..
                    return username;
                } 
                else {
                    // was not granted...
                    String msg = "User authorization failed(server "+remoteAuthenticationHost+":"+remoteAuthenticationPort+remoteAuthenticationPage+")";
                    throw new AuthorizationException(msg);
                }
            }
            catch(java.net.UnknownHostException uhe) {
                String msg = "host not found " + uhe;
                log.error(msg);
                throw new AuthorizationException(msg);
            }
            catch(java.io.IOException ioe) {
                String msg = "communication failure " + ioe;
                log.error(msg);
                throw new AuthorizationException(msg);
            }
        }
    } 

    /** 
     * getRemoteUser
     * @param req
     * @returns the remote user
     */
    public static String getRemoteUser(javax.servlet.http.HttpServletRequest req) {
        return checkUser(getMimeline(req));
    }
    
    /** 
     * getRemoteUser
     * @param sp
     * @returns the remote user
     */
    public static String getRemoteUser(scanpage sp) {
        return getRemoteUser(sp.req);
    }


    /** 
     * Sets the url on which an authentication has to be checked.
     * @param url
     */
    public static void setLocalCheckUrl(String url) {
        if (remoteAuthenticationHost != null) {
            log.error("check url was already set ('" + remoteAuthenticationHost + "')");
            return;
        }
        int pos=url.indexOf('/');
        if (pos!=-1) {
            remoteAuthenticationHost = url.substring(0,pos);
            remoteAuthenticationPage = url.substring(pos);	
        } 
        else {
	    remoteAuthenticationHost = url;
	    remoteAuthenticationPage = "/";
        }
	pos = remoteAuthenticationHost.indexOf(':');
	if (pos!=-1) {
	    try {
		remoteAuthenticationPort = Integer.parseInt(remoteAuthenticationHost.substring(pos));
            } 
            catch (Exception e) {
                log.error(e.toString());
            }
            remoteAuthenticationHost = remoteAuthenticationHost.substring(0,pos);            	    
        }
    }
    
    private static String getMimeline(javax.servlet.http.HttpServletRequest req) {
        return ((String)req.getHeader("Authorization"));
    }        
    
    private static int write(java.io.BufferedOutputStream out,String line) {
        int len=line.length();
	byte[] buffer=new byte[len];
        
        // next line is depricated!!
	line.getBytes(0,len,buffer,0);
	try {
	    out.write(buffer,0,len);
            out.flush();
        } 
        catch(java.io.IOException e) {
	    return -1;
        } //catch (Exception e) { }
	//debug("Send "+line);
	return len;
    }

    private static String read(java.io.BufferedInputStream in) {
        StringBuffer str=new StringBuffer();
	int rtn=0;
        do {
	    try {
		rtn=in.read();
	    } 
            catch(java.io.IOException e) {
		return null;
            }
            if (rtn==-1) {
                return null;
            }
	    str.append((char)rtn);
        } 
        while(rtn!='\n');   
	return str.toString();
    }    
}
/*
// THE OLD CODE... 
// still here, to know what the previous behaviour was, so that we know on failure what should be the behaviour..
package org.mmbase.util;

import java.util.*;
import java.io.*;
import java.net.*;

import javax.servlet.http.*;

import org.mmbase.util.logging.*;
import org.mmbase.module.core.*;

public class HttpAuth {
    static Logger log = Logging.getLoggerInstance(HttpAuth.class.getName());
    static Hashtable p_base64;

    private static String checklocalurl=null;
    private static Hashtable LocalCache = new Hashtable(); 

    public HttpAuth() {
    }

    static {
        p_base64=readPasswordFromDisk();
    }

    private static Hashtable readPasswordFromDisk() {
	Hashtable results=new Hashtable();

	// get us a propertie reader	
	ExtendedProperties Reader=new ExtendedProperties();

	// load the properties file of this server
        String accountconfig;
        accountconfig=MMBaseContext.getConfigPath();

	Hashtable accounts = Reader.readProperties(accountconfig+"/accounts.properties");

	// oke try loading all these modules and start em up
	for (Enumeration e=accounts.keys();e.hasMoreElements();) {
	    String key=(String)e.nextElement();
	    String value=(String)accounts.get(key);
	    //debug("name="+key+"=Basic "+Base64.encode(key+" "+value));
	    results.put("Basic "+Base64.encode(key+":"+value),key);
        }
	return results;
    }

    public static String getLoginName2(String mimel) {
        String name=(String)p_base64.get(mimel);
	if (name==null) { 
	    return(null);
        } 
        else {
	    return name;
        }
    }


    public static boolean checkUser(String wname,String mimeline) {
        String name=(String)p_base64.get(mimeline);
	//debug(p_base64);
        if (name!=null && name.equals(wname)) { 
	    return true;
        } 
        else {
	    if (checklocalurl!=null) {
		name=getLoginName(mimeline);
		if (name==null) {
                    return false;
                }
		boolean result=checkLocal(name,"www",mimeline,"basic");
		if (result) {
                    p_base64.put(mimeline,name);
		    return true;
		} 
	    }
	}
	return false;
	// mimeline=mimeline.substring(mimeline.indexOf(' ')+1);
	//debug(mimeline);
	//debug(Base64.decode(mimeline));
//	if (name!=null && name.equals(wname)) { 
//		return(true);
//	} else {
//		return(false);
//	}
    }

    public static String getAuthorization(HttpServletRequest req,HttpServletResponse res,String server, String level) throws AuthorizationException, NotLoggedInException {
        if (log.isDebugEnabled()) {
	    log.debug("server: " + server + ", level: " + level);
        }
	//debug("BASE="+p_base64);
	if (p_base64==null) {
	    p_base64=readPasswordFromDisk();
        }
	//debug("BASE2="+p_base64);
        String passwd = ((String)req.getHeader("Authorization"));
	//debug("passwd="+passwd);
        // Is user authorized yet?
        if (passwd==null) {
	    log.info("page " + req.getRequestURI() + " is secure, and user not yet authenticated");
	    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	    res.setHeader("WWW-Authenticate","Basic realm=\""+server+"\"");
	    throw new NotLoggedInException("Not logged in Exception (Generate page)");
        } 
        else {
	    //debug("passwd="+passwd);
            // Get name from user
	    String name=getLoginName(passwd);
            // Authenticate user
            if (!checkUser(name, passwd)) {
		log.service("Logging in of user " + name + "failed");
    		res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		res.setHeader("WWW-Authenticate","Basic realm=\""+server+"\"");
		throw new AuthorizationException("User authorization failed (Generate page)");
            } 
            else {
		log.service("User " + name + " succesfully logged in");
		//if (users!=null) users.addUser(name,requestInfo);
		return name;
            }
        }
    } 

    public static String getRemoteUser(HttpServletRequest req) {
        try {
            String mimeline = ((String)req.getHeader("Authorization"));
            String name=(String)p_base64.get(mimeline);
            return name;
        } 
        catch (Exception e) {
	    return "Unknown";
        }
    }

    public static String getRemoteUser(scanpage sp) {
        HttpServletRequest req=sp.req;
        String mimeline = ((String)req.getHeader("Authorization"));
	String name=(String)p_base64.get(mimeline);
	return name;
    }



    static boolean checkLocal(String name, String area, String passwd, String level) {
        log.info("checkLocal("+name+","+area+", ... , "+level+"): checking remote("+checklocalurl+") for name("+name+")");
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
                } 
                else {
		    machine=tmp;
		    page="/";
                }
		pos=machine.indexOf(':');
		if (pos!=-1) {
		    try {
			port=Integer.parseInt(machine.substring(pos));
                    } 
                    catch (Exception e) {
                    }
		    machine=machine.substring(0,pos);
                }
                Socket socket=new Socket(machine,port);
		BufferedInputStream instream=new BufferedInputStream(socket.getInputStream());
		BufferedOutputStream outstream=new BufferedOutputStream(socket.getOutputStream());
		write(outstream,"GET "+page+" HTTP/1.0\nContent-Type: vpro/ballyhoo\nUser-Agent: VPRO/James remote password check\nAuthorization: "+passwd+"\n\n");
		String result = read(instream);
                if (result.indexOf("401")==-1) {
		    LocalCache.put(name+area,passwd);
		    return true;
                } 
                else {
		    return false;
                }
            } 
            catch (Exception e) {
            }
        } 
        else {
	    if (passwd2.equals(passwd)) {
		return true;
            } 
            else {
		return false;
            }
        }
	return false;
    }


    private static int write(BufferedOutputStream out,String line) {
        int len=line.length();
	byte[] buffer=new byte[len];

	line.getBytes(0,len,buffer,0);
	try {
	    out.write(buffer,0,len);
            out.flush();
        } 
        catch(IOException e) {
	    return -1;
        } //catch (Exception e) { }
	//debug("Send "+line);
	return len;
    }

    private static String read(BufferedInputStream in) {
        StringBuffer str=new StringBuffer();
	int rtn=0;

        do {
	    try {
		rtn=in.read();
	    } 
            catch(IOException e) {
		return null;
            }
            if (rtn==-1) {
                return null;
            }
	    str.append((char)rtn);
        } 
        while(rtn!='\n');
        
	return str.toString();
    }


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
	    } 
            catch (Exception e) {
		log.error("getLoginName(): Exception: while decoding("+decodedline+"): " + e);
            }
	    newobject = new Hashtable();	
	    newobject.put("base64",mimel.substring(mimel.indexOf(' ')+1));
	    if (mimel!=null && name!=null) {
		p_base64.put(mimel.substring(mimel.indexOf(' ')+1),name);
            } 
            else {
                //debug("auth.java: no mimeline for "+name+" dec="+decodeline);
            }
        }
	return name;
    }

    public static void setLocalCheckUrl(String url) {
        if (checklocalurl!=null) {
	    log.error("setLocalCheckUrl("+url+"): SetLocalUrl ALLREADY SET !!!!");
        }
        else {
	    log.info("setLocalUrl("+url+"): url is set to local");
	    checklocalurl=url;
        }
    }
}
*/
