/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
/*
$Log: not supported by cvs2svn $
Revision 1.15  2001/04/03 15:45:48  install
Rob changed to new SecurityManager

Revision 1.14  2001/04/03 15:09:54  install
Rob

Revision 1.13  2001/03/30 09:50:40  install
Rob

Revision 1.12  2001/03/22 17:30:49  vpro
Davzev: First off added some better docs for this servlet.
Second, when a new RemoteBuilder node is send through either a GET or POST,
an insert will be done using one method instead of copy/pasted code.
When unknown node requests (GET/POST) come in  that arent of type ServiceBuilder,
they will be ignored and request will be cancelled.
All other node requests that can be find in mmbase will be handled correctly.
Finally, I removed some weird vpro specific code.

$Id: remoteXML.java,v 1.16 2001-04-03 16:22:36 eduard Exp $
*/
package org.mmbase.servlet;
 
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.mmbase.util.*;
import org.mmbase.module.builders.*;
import org.mmbase.module.core.*;
import org.mmbase.security.*;

/**
 * The remoteXML Servlet serves GET requests coming from remotebuilders 
 * and POST requests coming both remotebuilders and mmservers. 
 * Remotebuilder requests can come from a cdplayer that wants to update itself or wants to send 
 * itself (eg. after statechange) to mmbase, inwhich mmbase merges and commits it. 
 * 
 * The servletname is called remoteXML.db
 * - An incoming GET request looks like: 
 * "/remoteXML.db?builderTypeName+serviceName+http+hostname+portnr GET"
 * The buildertypename eg. cdplayers, serviceName(cdplayersnode.name) eg. CDROM-1
 * - An incoming POST request looks like: "/remoteXML.db POST"
 * 
 * @version $Revision: 1.16 $ $Date: 2001-04-03 16:22:36 $
 */
public class remoteXML extends JamesServlet {
	private boolean debug = true;
	MMBase mmbase;
	private org.mmbase.security.MMBaseCop mmbaseCop = null;

	/**
	 * Initializing mmbase root variable.
	 */
	public void init() {
		if (debug) debug("init: Initializing mmbase root variable.");
		mmbase=(MMBase)getModule("MMBASEROOT");
		mmbaseCop = mmbase.getMMBaseCop();
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
			String sharedsecret = req.getHeader("sharedSecret");
			//debug("Sharedsecret = "+sharedsecret);

			// Check if the remote machine knows the same shared secret. 
			if(mmbaseCop.checkSharedSecret(sharedsecret)) {
				debug("warning - sharedsecret is correct, system is authenticated"); 
			} else {
				debug("warning - sharedsecret is NOT correct, system is NOT authenticated"); 
			}
			
			if (req.getMethod().equals("POST")) {
				if (debug) debug("service: Incoming request: POST");
				handlePost(req,res);
			} else 
			if (req.getMethod().equals("GET")) {
				if (debug) debug("service: Incoming request: GET");
				handleGet(req,res);
			}
		} catch (Exception e)  {
			debug("error "+e);
			e.printStackTrace();
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
			boolean commitOk = commitXML(xml,req);
			if (!commitOk) debug("handlePost: ERROR: commitXML Failed for xml:"+xml); 
		} catch(Exception e) {
			debug("handlePost: ERROR POST failed from remoteXML");
			e.printStackTrace();
		}
	}

	/**
	 * Checks what node is being requested and tries to find it in mmbase. If it can be found, 
	 * it will be send back in xml. If it can't be found it will be inserted in mmbase but only if 
	 * this node is of type ServiceBuilder, otherwise request & insertion will be canceled.
	 * @param req the current HttpServletRequest
	 * @param res the current HttpServletResponse
	 */
	private void handleGet(HttpServletRequest req,HttpServletResponse res) {
		if (debug) debug("handleGet: Getting info from querystring");
		String buildername =getParam(req,0);
		String nodename = getParam(req,1);
		String proto = getParam(req,2);
		String host = getParam(req,3);
		String port = getParam(req,4);
		String remoteUrl= proto+"://"+host+":"+port;
		if (debug) debug("handleGet: Buildername:"+buildername+" Nodename:"+nodename+" remoteUrl:"+remoteUrl);

		if (debug) debug("handleGet: Getting node for reference:"+nodename);
		MMObjectBuilder bul=mmbase.getMMObject(buildername);
		if (bul!=null) {
			int number=-1;
			String numberStr=bul.getNumberFromName(nodename);
			if (numberStr!=null) {
				if (debug) debug("handleGet: Found number "+numberStr+" for nodename:"+nodename);
				try { number = Integer.parseInt(numberStr);} catch (NumberFormatException nfe) {
					debug("handleGet: ERROR: number:"+numberStr+" is not a number.");
					nfe.printStackTrace();
				}
			} else {
				debug("handleGet: Can't find objnr for "+nodename+" -> inserting this new "+buildername+" node");
				if (bul instanceof ServiceBuilder) {
					ServiceBuilder serviceBuilder=(ServiceBuilder)bul;	
					number = insertRemoteBuilderNode(serviceBuilder,buildername,nodename,remoteUrl);
					debug("handleGet: INSERTED "+buildername+" node:"+nodename+" object:"+number);
				} else
					debug("handleGet: INFO: Requested node is not of type ServiceBuilder but of type:"+buildername+", skipping insertion.");
			}
			if (number!=-1) {
				String body="";
				MMObjectNode node=bul.getNode(number);
				if (node!=null) {
					if (debug) debug("handleGet: Filling body with xml version of "+buildername+" node:"+nodename);
					body=node.toXML();	
				} else 
					debug("handleGet: ERROR: Can't get node for number:"+number+", node="+node);
				try {
					if (debug) debug("handleGet: Sending body back to client.");
					// Open	a output stream so you can write to the client
					PrintStream out = new PrintStream(res.getOutputStream());
					res.setContentType("text/plain");
					res.setContentLength(body.length());
					out.print(body);
					out.flush();
					out.close();
				} catch(Exception e) {
					debug("handleGet: ERROR: Sending requested data for GET failed.");
					e.printStackTrace();
				}
			} else
				if (debug) debug("handleGet: ERROR: number="+number+" node insert failed or node is wrong type, cancelling request.");
		} else
			debug("handleGet: ERROR can't get builder: "+buildername+" from mmbase.");
	}

	/**
	 * Searches mmserver object representing the remoteserver from which the GET/POST request comes, 
	 * and adds the remotebuilder node as a servicebuilder node to mmbase.
	 * MMServer search is done using remotebuilder serverUrl as host fieldname. 
	 * @param serviceBuilder a serviceBuilder reference.
	 * @param builderName the name of the remotebuilder type.
	 * @param nodeName the name of the new node that needs to be added. 
	 * @param remoteUrl the url of the remoteserver that sent the request.
	 * @return true when insert succeeds; false otherwise.
	 */	
	public int insertRemoteBuilderNode(ServiceBuilder serviceBuilder,String builderName,String nodeName,String remoteUrl) {
		MMServers mmserverBuilder=(MMServers)mmbase.getMMObject("mmservers");
		if (debug) debug("insertRemoteBuilderNode: Searching mmserver where host=remoteUrl="+remoteUrl);
		Enumeration mmsEnum = mmserverBuilder.search( "WHERE host='"+remoteUrl+"'");
		if (mmsEnum.hasMoreElements()) {
			MMObjectNode mmserverNode = (MMObjectNode) mmsEnum.nextElement();
			try {
				// ? dunno what this var does, but it's not used by remote system as far as I've found out.
				String localclass = "org.test";
				// Inserts a remotebuilder type node as a servicebuilder node and relates it to mmserverNode.
				serviceBuilder.addService(nodeName,localclass,mmserverNode);
			} catch(Exception e) {
				debug("insertRemoteBuilderNode: ERROR in addService, mmserverNode:"+mmserverNode);
				debug("insertRemoteBuilderNode: ERROR in addService, Buildername:"+builderName+" Nodename:"+nodeName+" remoteUrl:"+remoteUrl);
				e.printStackTrace();
				return -1;
			}
			// Search newly inserted servicenode and return number.
			Enumeration e = serviceBuilder.search("WHERE name='"+nodeName+"'");
			if (e.hasMoreElements()) {
				MMObjectNode serviceNode = (MMObjectNode) e.nextElement();
				return serviceNode.getIntValue("number");	
			} else {
				debug("insertRemoteBuilderNode: ERROR: Can't find just inserted! remotebuilder node where name="+nodeName);
				return -1;
			}
		} else {
			debug("insertRemoteBuilderNode: ERROR: Can't find mmservernode where host="+remoteUrl);
			return -1;
		}
	}

	/**
	 * Checks what node is being posted and tries to find it in mmbase. If it can be found, 
	 * the posted node will be merged . If it can't be found it will be inserted in mmbase but only if 
	 * this node is of type ServiceBuilder, otherwise post will be canceled.
	 * @param xml posted node in xml.
	 * @param req the HttpServletRequest.
	 * @return true when posted node is merged, false otherwise.
	 */
	public boolean commitXML(String xml,HttpServletRequest req) {
		if (debug) debug("commitXML: Storing xmlnode in db, xml:"+xml);

		Hashtable values=getXMLValues(xml);

		// hack for braindead psion jdk
		String remhost=req.getRemoteAddr();
		String givenhost=(String)values.get("host");
		if (givenhost!=null && givenhost.indexOf("http://localhost")!=-1) {
			debug("commitXML: HOST REPLACE=http://"+remhost+":8080");
			values.put("host","http://"+remhost+":8080");	
		}

		String buildername=(String)values.get("buildername");
		MMObjectBuilder bul=mmbase.getMMObject(buildername);
		if (bul!=null) {
			String nodename=(String)values.get("name");
			int number=-1;
			String numberStr=bul.getNumberFromName(nodename);
			if (numberStr!=null) {
				if (debug) debug("commitXML: Found number "+numberStr+" for nodename:"+nodename);
				try { number = Integer.parseInt(numberStr);} catch (NumberFormatException nfe) {
					debug("commitXML: ERROR: number:"+numberStr+" is not a number.");
					nfe.printStackTrace();
					return false;
				}
			} else {
				debug("commitXML: Can't find objnr for "+nodename+" -> inserting this new "+buildername+" node");
				if (bul instanceof ServiceBuilder) {
					ServiceBuilder serviceBuilder=(ServiceBuilder)bul;	
					String remoteUrl=(String)values.get("host");
					number = insertRemoteBuilderNode(serviceBuilder,buildername,nodename,remoteUrl);
				} else {
					debug("commitXML: INFO: Posted node is not of type ServiceBuilder but of type:"+buildername+", skipping insertion.");
					return false;
				}
			}
			if (number!=-1) {
				if (debug) debug("commitXML: Getting node for "+buildername+" obj "+number);
				MMObjectNode node=bul.getNode(number);
				if (node!=null) {
					mergeXMLNode(node,values); //merges related fields. in node.
					node.commit();
					return true;
				} else {
					debug("commitXML: ERROR: Can't get node for number:"+number+", node="+node);
					return false;
				}
			} else {
				if (debug) debug("handleGet: ERROR: number="+number+" node insert failed or node is wrong type, cancelling post.");
				return false;
			}
		} else {
			debug("commitXML: ERROR can't get builder: "+buildername+" from mmbase.");
			return false;
		}
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
