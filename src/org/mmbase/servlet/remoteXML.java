/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.servlet;
 
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.mmbase.util.*;
import org.mmbase.module.builders.*;
import org.mmbase.module.core.*;

/**
 * The remoteXML Servlet serves GET and POST requests coming eg. remotebuilders
 * like a cdplayer that wants to know it's new state of itself or wants to send its 
 * own state change back to mmbase, inwhich mmbase merges and commits it. 
 * The servletname is called remoteXML.db
 * - An incoming GET request looks like: 
 * "/remoteXML.db?builderTypeName+serviceName+http+hostname+portnr GET"
 * The buildertypename eg. cdplayers, serviceName(cdplayersnode.name) eg. CDROM-1
 * - An incoming POST request looks like: "/remoteXML.db POST"
 * 
 * @version $Revision: 1.11 $ $Date: 2001-03-15 14:38:20 $
 */
public class remoteXML extends JamesServlet {
	private boolean debug = true;
	MMBase mmbase;

	/**
	 * Initializing mmbase root variable.
	 */
	public void init() {
		if (debug) debug("init: Initializing mmbase root variable.");
		mmbase=(MMBase)getModule("MMBASEROOT");
	}

	/**
 	* service call will be called by the server when a request is done
	* by a user.
	* @param req the current HttpServletRequest
	* @param res the current HttpServletResponse
 	*/
	public synchronized void service(HttpServletRequest req, HttpServletResponse res) throws ServletException,IOException {
		incRefCount(req);
		try {
			if (req.getMethod().equals("POST")) {
				if (debug) debug("service: Incoming request: POST");
				handlePost(req,res);
			} else 
			if (req.getMethod().equals("GET")) {
				if (debug) debug("service: Incoming request: GET");
				handleGet(req,res);
			}
		} finally { decRefCount(req); }
	}

	/**
	 * Gets the posted contents and attempt to read & commit it to mmbase.
	 * @param req the current HttpServletRequest
	 * @param res the current HttpServletResponse
	 */
	private void handlePost(HttpServletRequest req,HttpServletResponse res) {
		if (debug) debug("handlePost: Getting posted contents and attempt to read & commit it to mmbase");
		try {
			HttpPost poster=new HttpPost(req);
			String xml=poster.getPostParameter("xmlnode");
			commitXML(xml,req);
		} catch(Exception e) {
			debug("handlePost: ERROR POST failed from remoteXML");
			e.printStackTrace();
		}
	}

	/**
	 * Dit is rare code die ik misschien moet verbeteren of niet.
	 * Any how , als de nodename niet gevonden wordt, wordt de host gecheckt.
	 * Als deze hetzelfde is wordt ie niet gebruiokt, maar weeer de oude null
	 * en dan wordt er een lege body gezend.
	 */
	private void handleGet(HttpServletRequest req,HttpServletResponse res) {
		if (debug) debug("handleGet: Getting info from querystring");
		String body="";
		String buildername  = getParam(req,0);
		String nodename		= getParam(req,1);
		String proto		= getParam(req,2);
		String host			= getParam(req,3);
		String port			= getParam(req,4);
		String servername	= proto+"://"+host+":"+port;
		if (debug) debug("handleGet: Buildername:"+buildername+" Nodename:"+nodename+" Servername:"+servername);

		if (debug) debug("handleGet: Getting node for reference:"+nodename);
		MMObjectBuilder bul=mmbase.getMMObject(buildername);
		String number=bul.getNumberFromName(nodename);
		if (number!=null) {
			if (debug) debug("handleGet: Found number "+number+" for nodename:"+nodename);
		} else {
			if (debug) debug("handleGet: NOTE: Can't find objnr for "+nodename+", maybe a new remotebuilder?");
			ServiceBuilder sbul=(ServiceBuilder)bul;	
			MMServers server=(MMServers)mmbase.getMMObject("mmservers");
			// String snumber=server.getNumberFromName( servername );
			if (debug) debug("handleGet: Searching mmserver where host=servername="+servername);
			Enumeration e2 = server.search( "WHERE host='"+servername+"'" );
			if (e2.hasMoreElements()) {
				MMObjectNode snode = (MMObjectNode) e2.nextElement();
				try {
					sbul.addService(nodename,"org.test",snode);
				} catch(Exception e) {
					debug("handleGet: ERROR by addService, snode("+snode+"): got exception!");
					debug("handleGet: ERROR by addService, Buildername:"+buildername+" Nodename:"+nodename+" Servername:"+servername);
					e.printStackTrace();
				}
			} else {
				debug("handleGet(): ERROR: Can't find mmservernode where host="+servername);
			}
			
			//number=bul.getNumberFromName(number); // ???????????????????????????????????????????????????????
			number=bul.getNumberFromName(nodename);	
		}
		
		MMObjectNode node=bul.getNode(number);
		if (node!=null) {
			body=node.toXML();	
		}
		try {
			if (debug) debug("handleGet: Sending body back to client.");
			// Open	a output stream so you can write to the client
			PrintStream out = new PrintStream(res.getOutputStream());
			// Set the content type of this request
			res.setContentType("text/plain");
			res.setContentLength(body.length());
			out.print(body);
			out.flush();
			out.close();
		} catch(Exception e) {
			debug("handleGet(): ERROR: Sending requested data for GET failed.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Converts the node from an xml String to an MMObjectNode and commits it.
	 * When number field can't be found in the xmlnode, a new service node is created
	 * and inserted.
	 * @param xml a String with the service in xml form.
	 * @param req the HttpServletRequest.
	 * @return true, always.
	 */
	public boolean commitXML(String xml,HttpServletRequest req) {
		if (debug) debug("commitXML: Storing xml in db, xml:"+xml);

		Hashtable values=getXMLValues(xml);
		String remhost=req.getRemoteAddr();
		String givenhost=(String)values.get("host");

		// hack for braindead psion jdk
		if (givenhost!=null && givenhost.indexOf("http://localhost")!=-1) {
			debug("commitXML: HOST REPLACE=http://"+remhost+":8080");
			values.put("host","http://"+remhost+":8080");	
		}

		String buildername=(String)values.get("buildername");
		MMObjectBuilder bul=mmbase.getMMObject(buildername);
		if (bul!=null) {
			if (debug) debug("commitXML: Getting name value from xml'ed node.");
			String name=(String)values.get("name");
			String number=bul.getNumberFromName(name);
			if (number==null) {
				if (debug) debug("commitXML: number is null! creating new "+buildername+" node");
				ServiceBuilder sbul=(ServiceBuilder)bul;	
				MMServers mmserverbul=(MMServers)mmbase.getMMObject("mmservers");
				String snumber=mmserverbul.getNumberFromName("stationrunner");
				try {
					sbul.addService((String)values.get("name"),"cdplayerDummy",mmserverbul.getNode(snumber));
				} catch(Exception e) {
					debug("commitXML: ERROR: addService failed buildername("+buildername+"), snumber("+snumber+")");
					e.printStackTrace();
				}
				// What the hell, this doesn't work
				number=bul.getNumberFromName(name);
			}
			if (debug) debug("commitXML: Getting node for "+buildername+" obj "+number);
			MMObjectNode node=bul.getNode(number);
			if (node!=null) {
				mergeXMLNode(node,values);
				node.commit();
			} else {
				debug("commitXML: ERROR can't get node for "+buildername+" obj "+number);
			}
		} 
		return(true);
	}

	/**
	 * Merges the node values received through xml with a real service MMObjectNode. 
	 * System fields (number,otype,buildername and owner) aren't merged.
	 * @param node current Service node from this mmbase
	 * @param values the received service node values.
	 */
	private void mergeXMLNode(MMObjectNode node,Hashtable values) {
		if (debug) debug("mergeXMLNode: Merging data for node "+node.getStringValue("name")); 
		Enumeration t=values.keys();
		while (t.hasMoreElements()) {
			String key=(String)t.nextElement();
			String value=(String)values.get(key);

			// setting node , skipping system fields.
			int dbtype=node.getDBType(key);
			if (!key.equals("number") 
				&& !key.equals("otype") 
				&& !key.equals("buildername") 
				&& !key.equals("owner")) {
					node.setValue( key, dbtype, value );
			}
		}
	}

	/**
	 * Gets the node fields and values from the xml string, and saves them as an
	 * hastable.
	 * @param body String with node in XML form.
	 * @return the node as Hashtable
	 */
	public Hashtable getXMLValues(String body) {
		Hashtable values=new Hashtable();
		StringTokenizer tok = new StringTokenizer(body,"\n\r");
		String xmlline=tok.nextToken();
		String docline=tok.nextToken();
		
		String builderline=tok.nextToken();
		values.put("buildername",builderline.substring(1,builderline.length()-1));
		String endtoken="</"+builderline.substring(1);
		
		String nodedata=body.substring(body.indexOf(builderline)+builderline.length());
		nodedata=nodedata.substring(0,nodedata.indexOf(endtoken));

		int bpos=nodedata.indexOf("<");
		while (bpos!=-1) {
			String key=nodedata.substring(bpos+1);
			key=key.substring(0,key.indexOf(">"));
			String begintoken="<"+key+">";
			endtoken="</"+key+">";
		
			// (marcel) optimist
	
			String value=nodedata.substring(nodedata.indexOf(begintoken)+begintoken.length());
			value=value.substring(0,value.indexOf(endtoken));

			values.put(key,value);

			nodedata=nodedata.substring(nodedata.indexOf(endtoken)+endtoken.length());
			bpos=nodedata.indexOf("<");
		}
		return(values);
	}
}
