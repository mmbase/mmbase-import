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
 * remoteXML servlet has one job and one job only receive remoteXML files
 * for the mmbase system, check them and give them to mmbase so it can
 * be seen as a change on a object. It uses a POST command to get the
 * XML and will respond with a 200 OK if the xml was understood by the
 * mmbase system.
 *
 * @version $Revision: 1.8 $ $Date: 2000-05-22 09:24:56 $
 */
public class remoteXML extends JamesServlet {

	//private String classname = getClass().getName();
	private boolean debug = true;
	//private void debug( String msg ) { System.out.println( classname +":"+ msg ); }

	MMBase mmbase;
	
	public void init() {
		mmbase=(MMBase)getModule("MMBASEROOT");
		//System.out.println("MMBASE="+mmbase);
	}

	/**
 	* service call will be called by the server when a request is done
	* by a user.
 	*/
	public synchronized void service(HttpServletRequest req, HttpServletResponse res) throws ServletException,IOException
	{	
		incRefCount(req);
		try {
			if (req.getMethod().equals("POST")) {
				handlePost(req,res);
			} else 
			if (req.getMethod().equals("GET")) {
				handleGet(req,res);
			}
		} finally { decRefCount(req); }
	}

	private void handlePost(HttpServletRequest req,HttpServletResponse res) {
		try {
		HttpPost poster=new HttpPost(req);

		String xml=poster.getPostParameter("xmlnode");
		commitXML(xml,req);
		} catch(Exception e) {
			System.out.println("POST failed from remoteXML");
		}
	}

	private void handleGet(HttpServletRequest req,HttpServletResponse res) {
		String body="";

		String buildername  = getParam(req,0);
		String nodename		= getParam(req,1);
		String proto		= getParam(req,2);
		String host			= getParam(req,3);
		String port			= getParam(req,4);

		String servername	= proto+"://"+host+":"+port;

		MMObjectBuilder bul=mmbase.getMMObject(buildername);
		String number=bul.getNumberFromName(nodename);
		if (number==null) {
			ServiceBuilder sbul=(ServiceBuilder)bul;	
			MMServers server=(MMServers)mmbase.getMMObject("mmservers");

			if( debug ) {
				debug("handleGet(): buildername("+buildername+")");
				debug("handleGet(): nodename("+nodename+")");
				debug("handleGet(): number("+number+")");
				debug("handleGet(): server("+servername+")");
			}

			// String snumber=server.getNumberFromName( servername );
			MMObjectNode snode = null;
			Enumeration e2 = server.search( "WHERE host='"+servername+"'" );
			if( e2.hasMoreElements() ) 
				snode = (MMObjectNode) e2.nextElement();

			try {
				if( snode != null ) {
					sbul.addService(nodename,"org.test",snode);
				} else {
					debug("handleGet(): ERROR: snode("+snode+") for server("+servername+"): not found!");
					debug("handleGet(): buildername("+buildername+")");
					debug("handleGet(): nodename("+nodename+")");
					debug("handleGet(): number("+number+")");
					debug("handleGet(): server("+servername+")");

				}
			} catch(Exception e) {
				debug("handleGet(): ERROR: snode("+snode+"): got exception!");
                debug("handleGet(): buildername("+buildername+")");
                debug("handleGet(): nodename("+nodename+")");
                debug("handleGet(): number("+number+")");
				debug("handleGet(): server("+servername+")");
				e.printStackTrace();
			}
			//number=bul.getNumberFromName(number); // ???????????????????????????????????????????????????????
			number=bul.getNumberFromName(nodename);
		} else {
			if( debug ) {
				debug("handleGet(): number("+number+") found in builder("+buildername+"), this is good...");
                debug("handleGet(): buildername("+buildername+")");
                debug("handleGet(): nodename("+nodename+")");
                debug("handleGet(): number("+number+")");
				debug("handleGet(): server("+servername+")");
			}
		}
		MMObjectNode node=bul.getNode(number);
		if (node!=null) {
			body=node.toXML();	
		}
		try {
			// Open	a output stream so you can write to the client
			PrintStream out = new PrintStream(res.getOutputStream());
			// Set the content type of this request
			res.setContentType("text/plain");
			res.setContentLength(body.length());
			out.print(body);
			out.flush();
			out.close();
		} catch(Exception e) {
			debug("handleGet(): GET failed from remoteXML");
            debug("handleGet(): buildername("+buildername+")");
            debug("handleGet(): nodename("+nodename+")");
            debug("handleGet(): number("+number+")");
            debug("handleGet(): server("+servername+")");
		}
	}
	


	public boolean commitXML(String xml,HttpServletRequest req) {
		Hashtable values=getXMLValues(xml);
		String remhost=req.getRemoteAddr();
		String givenhost=(String)values.get("host");

		if (debug) debug("commitXML(): xml("+xml+")");

		// hack for braindead psion jdk
		if (givenhost!=null && givenhost.indexOf("http://localhost")!=-1) {
			debug("commitXML(): HOST REPLACE=http://"+remhost+":8080");
			values.put("host","http://"+remhost+":8080");	
		}

		String buildername=(String)values.get("buildername");
		values.remove("buildername");
		MMObjectBuilder bul=mmbase.getMMObject(buildername);
		if (bul!=null) {
			String number=(String)values.get("number");

			// convert name to number if needed
			try {
				int i=Integer.parseInt(number);
			} catch(Exception e) {
				// wow lets convert this sucker
				number=bul.getNumberFromName(number);
			}
			if (number==null) {
				ServiceBuilder sbul=(ServiceBuilder)bul;	
				MMServers server=(MMServers)mmbase.getMMObject("mmservers");
				String snumber=server.getNumberFromName("stationrunner");
				try {
					sbul.addService((String)values.get("name"),"cdplayerDummy",server.getNode(snumber));
				} catch(Exception e) {
					debug("commitXML(): ERROR: addService failed buildername("+buildername+"), snumber("+snumber+")");
					e.printStackTrace();
				}
				number=bul.getNumberFromName(number);
			}
			MMObjectNode node=bul.getNode(number);
			if (node!=null) {
				 mergeXMLNode(node,values);
				node.commit();
			} else {
				debug("commitXML(): remoteXML-> can't get node "+number);
			}
		} 
		return(true);
	}

	private void mergeXMLNode(MMObjectNode node,Hashtable values) {

		Enumeration t=values.keys();
		while (t.hasMoreElements()) {
			String key=(String)t.nextElement();
			String value=(String)values.get(key);

			// set the node
			String dbtype=node.getDBType(key);
			if (!key.equals("number") 
				&& !key.equals("otype") 
				&& !key.equals("buildername") 
				&& !key.equals("owner")) {
					node.setValue( key, dbtype, value );
			}
		}
	}

 
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
