/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
/*
$Id: MMHttpHandler.java,v 1.7 2000-11-27 16:33:53 vpro Exp $

$Log: not supported by cvs2svn $
Revision 1.6  2000/11/27 14:49:48  vpro
davzev: Changed debug var from RemoteBuilder.debug to true

Revision 1.5  2000/11/27 13:12:19  vpro
davzev: Added some method comments and changed argument names of method doGet and doXMLSignal, also removed some hardcoded numbers.

*/
package org.mmbase.remote;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

/**
 *
 * @version $Revision: 1.7 $ $Date: 2000-11-27 16:33:53 $
 * @author Daniel Ockeloen
 */
public class MMHttpHandler implements Runnable {

    private String  classname   = getClass().getName();
    private boolean debug       = true;
    private void debug( String msg ) { System.out.println( classname +":"+ msg ); }

	Thread kicker = null;

	Socket clientsocket;
	Hashtable listeners;
	
	public final static String REMOTE_REQUEST_URI_FILE = "remoteXML.db";

	public MMHttpHandler(Socket clientsocket,Hashtable listeners) {
		if (debug) debug("MMHttpHandler("+clientsocket+","+listeners+"): Created, initializing..");
		this.clientsocket=clientsocket;
		this.listeners=listeners;
		init();
	}

	public void init() {
		this.start();	
	}

	/**
	 * Starts the admin Thread.
	 */
	public void start() {
		/* Start up the main thread */
		if (kicker == null) {
			debug("start(): Creating and starting new Thread.");
			kicker = new Thread(this,"MMHttpHandler");
			kicker.start();
		} else {
			debug("start(): No create needed, thread already up and running, name:"+kicker.getName());
		}
	}
	
	/**
	 * Connects and retrieves HTTP method and checks for GET and POST to react to.
	 */
	public void run() {
		try {
			DataInputStream in = new DataInputStream(clientsocket.getInputStream());
			PrintStream out = new PrintStream(clientsocket.getOutputStream());

			String line=in.readLine();
			if (line!=null) {
				StringTokenizer tok=new StringTokenizer(line," \n\r\t");
				if (tok.hasMoreTokens()) {
					String method=tok.nextToken();
					if (debug) debug("run(): Got method: "+method);
					if (method.equals("GET")) doGet(tok,in,out);
					if (method.equals("POST")) doPost(tok,in,out);
				}
			}
			clientsocket.close();	
		} catch(Exception e) {
			debug("run(): ERROR: exception: "+e);
			e.printStackTrace();
		}
	}

	/**
	 * Gets the querystring from the GET cmd and passes it on to doXMLSignal.
	 * @param tok the StringTokenizer with the remaining GET info.
	 * @param in the DataInputStream
	 * @param out the PrintStream
	 */
	void doGet(StringTokenizer tok, DataInputStream in, PrintStream out) {
		if (tok.hasMoreTokens()) {
			String requestUrl=tok.nextToken();
			int filePos = requestUrl.indexOf(REMOTE_REQUEST_URI_FILE);
			if (filePos!=-1){
				if (debug) debug("doGet: Found requestURI file "+REMOTE_REQUEST_URI_FILE);
				int queryPos = requestUrl.indexOf("?");
				if (queryPos!=-1){
					String queryString=requestUrl.substring(queryPos+1); //+1 ='?' char.
					if (debug) debug("doGet: Retrieved querystring: "+queryString);
					doXMLSignal(queryString);
				} else
					debug("doGet: ERROR: No querystring: "+requestUrl);
			}else
				debug("doGet: WARNING: unknown requesturl:"+requestUrl);
		}
		if (debug) debug("doGet: Returning 200 OK");
		out.println("200 OK");
		out.flush();
	}
	
	/**
	 * Gets the servicebuilder instance using the querystring info and signals it using a
	 * nodeRemoteChanged to tell the service that it was changed on another server. 
	 * The service reference is the key to getting the service builder.
	 * @param queryString Contains the service reference, buildername and changetype.
	 */
	void doXMLSignal(String queryString) {
		if (debug) debug("doXMLSignal("+queryString+"): Getting info from queryString");

		StringTokenizer tok=new StringTokenizer(queryString,"+ \n\r\t");
		if (tok.hasMoreTokens()) {
			String serviceRef=tok.nextToken(); //Contains the service builder reference.
			if (tok.hasMoreTokens()) {
				String builderName=tok.nextToken();
				if (tok.hasMoreTokens()) {
					String ctype=tok.nextToken();
					RemoteBuilder serv=(RemoteBuilder)listeners.get(serviceRef);
					if (serv==null) {
						debug("doXMLSignal("+queryString+"): ERROR: no remote builder found for service reference:"+serviceRef);
					} else {
						debug("doXMLSignal("+queryString+"): Send nodeRemoteChanged signal to notify service that it's state has changed.");
						serv.nodeRemoteChanged(serviceRef,builderName,ctype);
					}
				} else debug("doXMLSignal("+queryString+"): ERROR: no 'ctype' found!");
			} else debug("doXMLSignal("+queryString+"): ERROR: no 'buildername' found!");
		} else debug("doXMLSignal("+queryString+"): ERROR: no 'service reference' found!");
	}

	void doPost(StringTokenizer tok, DataInputStream in, PrintStream out) {
		Hashtable headers=readHeaders(in);
		// well is it a post ?
		String header=(String)headers.get("Content-type");
        if (header!=null && header.equals("application/x-www-form-urlencoded")) {
					header=(String)headers.get("Content-length");
        			if (header!=null) {
						try {
							int len=Integer.parseInt(header);
    						byte[] buffer=readContentLength(len,in);
    						Hashtable posted=readPostUrlEncoded(buffer);
						} catch(Exception e) { debug("doPost(): ERROR: could not handle post!"); e.printStackTrace(); }
					} else debug("doPost(): ERROR: No 'Content-length' specified in this post!");
		}
		out.println("200 OK");
		out.flush();
	}

	/**
    * read a block into a array of ContentLenght size from the users networksocket
    *
    * @param table the hashtable that is used as the source for the mapping process
    * @return byte[] buffer of length defined in the content-length mimeheader
    */
    public byte[] readContentLength(int len,DataInputStream in) {
        int len2,len3;
        byte buffer[]=null;
 
        // Maximum postsize
            try {
                buffer=new byte[len];
                // can come back before done len !!!!

				// HUGE hack to counter a bug i can't find. for some reason
				// i get a extra \10.
				int x=in.read();
				if (x==10) {
                	len2=in.read(buffer,0,len);
				} else {
					buffer[0]=(byte)x;
                	len2=in.read(buffer,1,len-1);
				}
				len--;
                while (len2<len) {
                    len3=in.read(buffer,len2,len-len2);
                    if (len3==-1) {
                        break;
                    } else {
                        len2+=len3;
                    }
                }
            } catch (Exception e) {
                debug("readContentLength("+len+"): ERROR: can't read post msg from client");
				e.printStackTrace();
            }
        return(buffer);
    }

	Hashtable readHeaders(DataInputStream in) {
		Hashtable headers=new Hashtable();
		String line=readline(in);
		while (line!=null && line.length()>1) {
			int pos=line.indexOf(":");
			if (pos!=-1) {
				headers.put(line.substring(0,pos),line.substring(pos+2,line.length()-1));	
			}
			line=readline(in);
		}
		return(headers);
	}  

	String readline(InputStream in) {
		StringBuffer rtn=new StringBuffer();
		int temp;
		do {
			try {
				temp=in.read();
			} catch(IOException e) {
				return(null);
			}
			if (temp==-1) {
				return(null);
			}
			if (temp!=0) rtn.append((char)temp);
		} while(temp!='\n');
		return(rtn.toString());
	}


	/**
    * read post info from buffer, must be defined in UrlEncode format.
    *
    * @param postbuffer buffer with the postbuffer information
    * @param post_header hashtable to put the postbuffer information in
    */
    private Hashtable readPostUrlEncoded(byte[] postbuffer) {
        String mimestr="";
        int nentrys=0,i=0,idx;
        char letter;
		Hashtable post_header=new Hashtable();
 
        String buffer = new String(postbuffer,0);
        buffer=buffer.replace('+',' ');
        StringTokenizer tok = new StringTokenizer(buffer,"&");
        while (tok.hasMoreTokens()) {
            mimestr=tok.nextToken();
            if ((idx=mimestr.indexOf('='))!=-1) {
                while ((i=mimestr.indexOf('%',i))!=-1) {
                    // Unescape the 'invalids' in the buffer (%xx) form
                    try {
                        letter=(char)Integer.parseInt(mimestr.substring(i+1,i+3),16);
                        mimestr=mimestr.substring(0,i)+letter+mimestr.substring(i+3);
                    } catch (Exception e) {
                    }
                    i++;
                }
				post_header.put(mimestr.substring(0,idx),mimestr.substring(idx+1));
            } else {
                post_header.put(mimestr,"");
            }
        }
        return(post_header);
    }
}
