/* -*- tab-width:4 ; -*-

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
/*
$Id: MMHttpHandler.java,v 1.13 2001-07-02 16:57:56 vpro Exp $
*/
package org.mmbase.remote;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

//import org.mmbase.util.logging.Logger;
//import org.mmbase.util.logging.Logging;

/**
 *
 * @version $Revision: 1.13 $ $Date: 2001-07-02 16:57:56 $
 * @author Daniel Ockeloen
 */
public class MMHttpHandler implements Runnable { 
    //Logging removed automaticly by Michiel, and replace with __-methods
    private static String __classname = MMHttpHandler.class.getName();


    boolean __debug = true;
    private static void __debug(String s) { System.out.println(__classname + ":" + s); }
    //private static Logger log = Logging.getLoggerInstance(MMHttpHandler.class.getName()); 

	Thread kicker = null;

	Socket clientsocket;
	Hashtable listeners;
	
	public final static String REMOTE_REQUEST_URI_FILE = "remoteXML.db";
	public final static String CONTENT_TYPE = "application/x-www-form-urlencoded";

	public MMHttpHandler(Socket clientsocket,Hashtable listeners) {
        if (__debug) {
            /*log.debug*/__debug("MMHttpHandler("+clientsocket+","+listeners+"): Created, initializing..");
        }
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
			/*log.info*/__debug("start(): Creating and starting new Thread.");
			kicker = new Thread(this,"MMHttpHandler");
			kicker.start();
		} else {
			/*log.info*/__debug("start(): No create needed, thread already up and running, name:"+kicker.getName());
		}
	}
	
	/**
	 * Connects and retrieves HTTP method and checks for GET and POST to react to.
	 * Before handling request first a shared secret check is done.
	 */
	public void run() {
		try {
			DataInputStream in = new DataInputStream(clientsocket.getInputStream());
			PrintStream out = new PrintStream(clientsocket.getOutputStream());

			String key = null; // incoming shared secret key.
			String line=in.readLine();
			if (line!=null) {

				// Read all remaining data that was sent with request.
				String data = in.readLine();
				if (__debug) {
					/*log.debug*/__debug("run: First line of date that was sent with request: "+data);
				}
				while (!data.equals("")) {
					// 'sharedSecret: ' length of this string is 14 chars.
					if (data.startsWith("sharedSecret"))
						key = data.substring(14);
					if (__debug) {
						/*log.debug*/__debug("run: Remaining line of data that was sent with request: "+data);
					}
					data = in.readLine();
				}

				/**
				 * checking here if the shared secret is correct .
				 */
			    	if(!startRemoteBuilders.checkSharedSecret(key)) {
					__debug("MMHttpHandler.run: sharedsecret is NOT correct, system is NOT authenticated");
		    		} else {
					StringTokenizer tok=new StringTokenizer(line," \n\r\t");
					if (tok.hasMoreTokens()) {
						String method=tok.nextToken();
						if (__debug) {
							/*log.debug*/__debug("MMHttpHandler.run: Incoming request: "+method);
						}
						if (method.equals("GET"))
							doGet(tok,out);
						if (method.equals("POST")) 
							doPost(tok,in,out);
					}
				}
			}

			// Close socket.
			if (__debug) {
				/*log.debug*/__debug("run: Closing socket.");
			}
			clientsocket.close();	
		} catch(Exception e) {
			/*log.error*/__debug("run(): exception: "+e);
			/*log.error*/e.printStackTrace();
		}
	}

	/**
	 * Gets the querystring from the GET cmd and passes it on to doXMLSignal.
	 * @param tok the StringTokenizer with the remaining GET info.
	 * @param out the PrintStream to send statuscode over and for flushing.
	 */
	void doGet(StringTokenizer tok, PrintStream out) {
		String statusCode= null;
		if (tok.hasMoreTokens()) {
			String requestUrl=tok.nextToken();
			int filePos = requestUrl.indexOf(REMOTE_REQUEST_URI_FILE);
			if (filePos!=-1){               
				if (__debug) {
					/*log.debug*/__debug("doGet: Found requestURI file "+REMOTE_REQUEST_URI_FILE);
				}
				int queryPos = requestUrl.indexOf("?");
				if (queryPos!=-1){
					String queryString=requestUrl.substring(queryPos+1); //+1 ='?' char.
					if (__debug) {
						/*log.debug*/__debug("doGet: Retrieved querystring: "+queryString);
					}
					doXMLSignal(queryString);
					statusCode = "200 OK";
				} else {
					/*log.error*/__debug("doGet: No querystring: "+requestUrl);
					statusCode = "400 Bad Request"; 
				}
			}else {
				/*log.warn*/__debug("doGet: WARNING: unknown requesturl:"+requestUrl);
				statusCode = "404 Not Found"; 
			}
		}
		if (__debug) {
			/*log.debug*/__debug("doGet: Returning "+statusCode);
		}
		out.println(statusCode);
		out.flush();
	}
	
	/**
	 * Gets the servicebuilder instance using the querystring info and signals it using a
	 * nodeRemoteChanged to tell the service that it was changed on another server. 
	 * The service reference is the key to getting the service builder.
	 * @param queryString Contains the service reference, buildername and changetype.
	 */
	void doXMLSignal(String queryString) {
        if (__debug) {
            /*log.debug*/__debug("doXMLSignal("+queryString+"): Getting info from queryString");
        }

		StringTokenizer tok=new StringTokenizer(queryString,"+ \n\r\t");
		if (tok.hasMoreTokens()) {
			String serviceRef=tok.nextToken(); //Contains the service builder reference.
			if (tok.hasMoreTokens()) {
				String builderName=tok.nextToken();
				if (tok.hasMoreTokens()) {
					String ctype=tok.nextToken();
					RemoteBuilder serv=(RemoteBuilder)listeners.get(serviceRef);
					if (serv==null) {
						/*log.error*/__debug("doXMLSignal("+queryString+"): no remote builder found for service reference:"+serviceRef);
					} else {
						/*log.info*/__debug("doXMLSignal("+queryString+"): Send nodeRemoteChanged signal to notify service that it's state has changed.");
						serv.nodeRemoteChanged(serviceRef,builderName,ctype);
					}
				} else /*log.error*/__debug("doXMLSignal("+queryString+"): no 'ctype' found!");
			} else /*log.error*/__debug("doXMLSignal("+queryString+"): no 'buildername' found!");
		} else /*log.error*/__debug("doXMLSignal("+queryString+"): no 'service reference' found!");
	}

	void doPost(StringTokenizer tok, DataInputStream in, PrintStream out) {
		String statusCode = null;
		Hashtable headers=readHeaders(in);
		// well is it a post ?
		String header=(String)headers.get("Content-type");
        if (header!=null && header.equals(CONTENT_TYPE)) {
			header=(String)headers.get("Content-length");
        	if (header!=null) {
				try {
					int len=Integer.parseInt(header);
    				byte[] buffer=readContentLength(len,in);
    				Hashtable posted=readPostUrlEncoded(buffer);
					statusCode = "200 OK";
				} catch(Exception e) { 
					/*log.error*/__debug("doPost(): could not handle post!"); 
					/*log.error*/e.printStackTrace();
					statusCode = "400 Bad Request, error during reading posted content";
				}
			} else {
				/*log.error*/__debug("doPost(): No 'Content-length' specified in this post!");
				statusCode = "411 Length Required"; 
			}
		} else {
			/*log.error*/__debug("doPost(): 'Content-type' is: "+header+", should be : "+CONTENT_TYPE);
			statusCode = "415 Unsupported Media Type"; 
		}
        if (__debug) {
            /*log.debug*/__debug("doPost: Returning "+statusCode);
        }
		out.println(statusCode);
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
                /*log.error*/__debug("readContentLength("+len+"): can't read post msg from client");
				/*log.error*/e.printStackTrace();
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
						/*log.error*/e.printStackTrace();
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
