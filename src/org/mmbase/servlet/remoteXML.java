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
import org.mmbase.security.*;
import org.mmbase.util.logging.*;

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
 * @version $Revision: 1.23 $ $Date: 2001-07-02 16:41:04 $
 */
public class remoteXML extends JamesServlet {
	private static Logger log = Logging.getLoggerInstance(remoteXML.class.getName());
	MMBase mmbase;
	private MMBaseCop mmbaseCop = null;

	/**
	 * Initializing mmbase root variable and get mmbaseCop.
	 */
	public void init() {
		log.debug("Initializing mmbase root variable.");
		mmbase = (MMBase)getModule("MMBASEROOT");
		log.debug("Getting mmbaseCop.");
		mmbaseCop = mmbase.getMMBaseCop();
	}

	/**
 	 * Checks all incoming requests and validates it wich shared secret before handling.
	 * @param req the current HttpServletRequest
	 * @param res the current HttpServletResponse
 	 */
	public synchronized void service(HttpServletRequest req, HttpServletResponse res) throws ServletException,IOException {
		incRefCount(req);
		try {
			String sharedsecret = req.getHeader("sharedSecret");
			// log.debug("Sharedsecret = "+sharedsecret);

			// Check if the remote machine knows the same shared secret. 
			if(!mmbaseCop.checkSharedSecret(sharedsecret)) {
				log.error("sharedsecret is NOT correct, system is NOT authenticated"); 
				return;
			}
			
			if (req.getMethod().equals("POST")) {
				log.info("Incoming request: POST");
				handlePost(req,res);
			} else 
			if (req.getMethod().equals("GET")) {
				log.info("Incoming request: GET");
				handleGet(req,res);
			}
		} catch (Exception e)  {
			e.printStackTrace();
		} finally { decRefCount(req); }
	}

	/**
	 * Gets the posted contents and attempt to read & commit it to mmbase.
	 * @param req the current HttpServletRequest
	 * @param res the current HttpServletResponse
	 */
	private void handlePost(HttpServletRequest req,HttpServletResponse res) {
		log.info("Getting posted contents and attempt to read & commit it to mmbase");
		try {
			HttpPost poster=new HttpPost(req);
			String xml=poster.getPostParameter("xmlnode");
			boolean commitOk = commitXML(xml,req);
			if (!commitOk) log.error("commitXML Failed for xml:"+xml); 
		} catch(Exception e) {
			log.error("POST failed from remoteXML");
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
		log.info("Getting info from querystring");
		String buildername =getParam(req,0);
		String nodename = getParam(req,1);
		String proto = getParam(req,2);
		String host = getParam(req,3);
		String port = getParam(req,4);
		String remoteUrl= proto+"://"+host+":"+port;
		log.info("Buildername:"+buildername+" Nodename:"+nodename+" remoteUrl:"+remoteUrl);

		log.info("Getting node for reference:"+nodename);
		MMObjectBuilder bul=mmbase.getMMObject(buildername);
		if (bul!=null) {
			int number=-1;
			MMObjectNode node=getNodeThroughField(bul,"name",nodename);
			if (node==null) {
				log.info("Can't find objnr for "+nodename+" -> inserting this new "+buildername+" node");
				if (bul instanceof ServiceBuilder) {
					ServiceBuilder serviceBuilder=(ServiceBuilder)bul;	
					number = insertRemoteBuilderNode(serviceBuilder,buildername,nodename,remoteUrl);
					if (number!=-1) {
						log.info("INSERTED "+buildername+" node:"+nodename+" object:"+number);
						node=bul.getNode(number);
					} else
						log.error("number="+number+" node insert failed or node is wrong type.");
				} else
					log.warn("Requested node is not of type ServiceBuilder but of type:"+buildername+", skipping insertion.");
			}
			if (node!=null) {
				log.info("Filling body with xml version of "+buildername+" node:"+nodename);
				String body=node.toXML();	
				try {
					log.info("Sending body back to client.");
					// Open	a output stream so you can write to the client
					PrintStream out = new PrintStream(res.getOutputStream());
					res.setContentType("text/plain");
					res.setContentLength(body.length());
					out.print(body);
					out.flush();
					out.close();
				} catch(Exception e) {
					log.error("Sending requested data for GET failed.");
					e.printStackTrace();
				}
			} else 
				log.error("Can't get node for number:"+number+", node="+node+" cancelling GET request.");
		} else
			log.error("Can't get builder: "+buildername+" from mmbase.");
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
		log.info("Searching mmserver where host=remoteUrl="+remoteUrl);
		Enumeration mmsEnum = mmserverBuilder.search( "WHERE host='"+remoteUrl+"'");
		if (mmsEnum.hasMoreElements()) {
			MMObjectNode mmserverNode = (MMObjectNode) mmsEnum.nextElement();
			try {
				// ? dunno what this var does, but it's not used by remote system as far as I've found out.
				String localclass = "org.test";
				// Inserts a remotebuilder type node as a servicebuilder node and relates it to mmserverNode.
				serviceBuilder.addService(nodeName,localclass,mmserverNode);
			} catch(Exception e) {
				log.error("in addService, mmserver:"+mmserverNode+"Buildername:"+builderName+" Nodename:"+nodeName+" remoteUrl:"+remoteUrl);
				e.printStackTrace();
				return -1;
			}
			// Search newly inserted servicenode and return number.
			Enumeration e = serviceBuilder.search("WHERE name='"+nodeName+"'");
			if (e.hasMoreElements()) {
				MMObjectNode serviceNode = (MMObjectNode) e.nextElement();
				return serviceNode.getIntValue("number");	
			} else {
				log.error("Can't find just inserted! remotebuilder node where name="+nodeName);
				return -1;
			}
		} else {
			log.error("Can't find mmservernode where host="+remoteUrl);
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
		log.info("Storing xmlnode in db, xml:"+xml);
		Hashtable values=getXMLValues(xml);

		// HACK for braindead psion jdk, ask daniel...
		String remhost=req.getRemoteAddr();
		String givenhost=(String)values.get("host");
		if (givenhost!=null && givenhost.indexOf("http://localhost")!=-1) {
			log.info("HOST REPLACE=http://"+remhost+":8080");
			values.put("host","http://"+remhost+":8080");	
		}

		String buildername=(String)values.get("buildername");
		MMObjectBuilder bul=mmbase.getMMObject(buildername);
		if (bul!=null) {
			String nodename=(String)values.get("name");
			int number=-1;
			MMObjectNode node=getNodeThroughField(bul,"name",nodename);
			if (node==null) {
				log.error("Can't find objnr for "+nodename+" -> inserting this new "+buildername+" node");
				if (bul instanceof ServiceBuilder) {
					ServiceBuilder serviceBuilder=(ServiceBuilder)bul;	
					String remoteUrl=(String)values.get("host");
					number = insertRemoteBuilderNode(serviceBuilder,buildername,nodename,remoteUrl);
					if (number!=-1) {
						log.info("INSERTED "+buildername+" node:"+nodename+" object:"+number);
						node=bul.getNode(number);
					} else
						log.error("number="+number+" node insert failed or node is wrong type.");
				} else {
					log.warn("Posted node is not of type ServiceBuilder but of type:"+buildername+", skipping insertion.");
					return false;
				}
			}
			if (node!=null) {	
				mergeXMLNode(node,values); //merges related fields. in node.
				node.commit();
				return true;
			} else {
				log.error("Can't get node for number:"+number+", node="+node);
				return false;
			}
		} else {
			log.error("Can't get builder: "+buildername+" from mmbase.");
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
		log.info("Merging data for node "+node.getStringValue("name")); 
		Enumeration t=values.keys();
		while (t.hasMoreElements()) {
			String key=(String)t.nextElement();
			String value=(String)values.get(key);

			// setting node, skipping system fields.
			if (!key.equals("number") 
				&& !key.equals("otype") 
				&& !key.equals("buildername") 
				&& !key.equals("owner")) {
					int dbtype=node.getDBType(key);
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
	
	/**
	 * Searches all nodes where field name equals a certain String value and returns the first node found.
	 * @param bul builder reference.
	 * @param field fieldname.
	 * @param value a String value.
	 * @return the first node found or null when nothing was found.
	 */
	private MMObjectNode getNodeThroughField(MMObjectBuilder bul,String field,String value) {
		Enumeration e=bul.search("WHERE "+field+"='"+value+"'");
		if (e.hasMoreElements())
			return (MMObjectNode)e.nextElement();
		else 
			return null;
	}

	/**
	 * Searches all nodes where field name equals a certain int value and returns the first node found.
	 * @param bul builder reference.
	 * @param field fieldname.
	 * @param value a int value.
	 * @return the first node found or null when nothing was found.
	 */
	private MMObjectNode getNodeThroughField(MMObjectBuilder bul,String field,int value) {
		Enumeration e=bul.search("WHERE "+field+"="+value);
		if (e.hasMoreElements())
			return (MMObjectNode)e.nextElement();
		else 
			return null;
	}
}
