/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
/*
$Id: MMHttpAcceptor.java,v 1.9 2000-11-29 13:57:22 vpro Exp $

$Log: not supported by cvs2svn $
Revision 1.8  2000/11/28 16:41:12  vpro
davzev: Added some method comments and debug.

*/
package org.mmbase.remote;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

/**
 *
 * @version $Revision: 1.9 $ $Date: 2000-11-29 13:57:22 $
 * @author Daniel Ockeloen
 */
public class MMHttpAcceptor implements Runnable,MMProtocolDriver {

    private String  classname   = getClass().getName();
    private boolean debug       = true;
    private void debug(String msg) { if (debug) System.out.println(classname +":"+ msg);}

	Thread kicker = null;

	ServerSocket serversocket; // Server Socket
	Socket clientsocket;       // Clients/request socket

	Hashtable listeners=new Hashtable();

	int port=8080;
	String remoteHost;
	int remotePort=80;
	
	public MMHttpAcceptor(String servername,String remoteHost,int remotePort) {
		this.remoteHost=remoteHost;
		this.remotePort=remotePort;
		if (debug) debug("MMHttpAcceptor("+servername+","+remoteHost+","+remotePort+"): Creating and initializing");
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
       	   /**
 			* Open serversocket and bind to that port
			*/
			int trycount=0;
			boolean okeonport=false;
			while (trycount<10 && okeonport==false) {
				try {
					serversocket = new ServerSocket(port,16);	
					//serversocket.setSoTimeout(Integer.MAX_VALUE);
					okeonport=true;
				} catch (Exception e) {
					debug("start():bind failed  on port :"+port);
					port=port+((trycount++)*10);
				}
			}
			if (okeonport) {
				debug("start():ok bind on port :"+port);
			} else {
				debug("start():ALL BINDS from 8080 to 9080 failed ");
			}
			if (debug) debug("start(): Creating and starting a new Thread.");
			kicker = new Thread(this,"MMHttpAcceptor");
			kicker.start();
		}
	}
	
	/**
	 * Stops the admin Thread.
	 */
	public void stop() {
		/* Stop thread */
		kicker.setPriority(Thread.MIN_PRIORITY);  
		kicker.suspend();
		kicker.stop();
		kicker = null;
	}

	/**
	 * Sets the priority of the admin thread a little higher and continuous with work.
	 */
	public void run() {
		while (kicker!=null) {
			try {
				kicker.setPriority(Thread.NORM_PRIORITY+1);  
				doWork();
			} catch(Exception e) {
				debug("Error: ");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Lowers the current thread priority and creates a new MMHttpHandler instance.
	 */
	public void doWork() {
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY-1); 
		while (kicker!=null) {
			try {
		       /**
				* Accept a request from the client, from
				* here on the race is on, the trick is to
				* be able to do this as fast as possible :)
				* Do what is wanted and close the connect
				* without hogging your host machine down.
			 	*/
				if (debug) debug("doWork(): Accepting client request and creating new MMHttpHandler.");
				clientsocket=serversocket.accept();
				
				new MMHttpHandler(clientsocket,listeners);
			} catch (Exception e) {
			   /**
				* This only to catch the exception caused by
				* closing the socket
				*/
				debug("AcceptError");
				e.printStackTrace();
					try {Thread.sleep(2*1000);} catch (InterruptedException g){}
				continue;
			}

		   /**
			* let the typerhandler service this request.
			*/
		}
	}

	public synchronized boolean commitNode(String nodename,String tableName,String xml) {
		if (debug) debug("commitNode("+nodename+","+tableName+","+xml+"): xml.length():"+xml.length());
		String line=null;
		Socket connect;
		BufferedInputStream in=null;
		PrintStream out=null;

		StringTokenizer tok;
		String header,body;
		String name="xmlnode";
		String url="/remoteXML.db";

		//System.out.println("DO POST ON : "+xml);
		try {
			if (debug) debug("commitNode: Performing HTTP POST and send the xml data to "+remoteHost+", "+remotePort);
			connect=new Socket(remoteHost,remotePort);
			try {
				out=new PrintStream(connect.getOutputStream());
			} catch (Exception e) {
				debug("PrintStream failure");
				e.printStackTrace();
			}

			body=name+"="+xml.replace(' ','+');
			out.print("POST "+url+" HTTP/1.1\r\n");
			out.print("Content-Length: "+body.length()+"\r\n");
			out.print("Content-Type: application/x-www-form-urlencoded\r\n");
			out.print("User-Agent: org.mmbase\r\n");
			out.print("\r\n");
			out.print(body);
			out.flush();
			out.close();
	
			/*
			line=readline(connect_in);
			while (line!=null && line.length()>1) {
					if (line.indexOf("200 OK")!=-1) {
						connect_result=true;	
						connect_auth=true;
					} else if (line.indexOf("401")!=-1) {
						connect_auth=false;
					}
					if (line.indexOf("Content-Length:")!=-1) {
						obj_len=Integer.parseInt(line.substring(16,line.indexOf('\n')));
					}
					line=readline(connect_in);
			}
			if (obj_len!=0) {
				line=readobj(connect_in,obj_len);
			}
			*/
		} catch(Exception e) {
			debug("Error connecting to object host : "+e);
		}	
		return(true);
	}

	/**
	 * Stores remote builder reference in hashtable using the service Reference name as key.
	 * @return true, always
	 */
	public boolean addListener(String buildername,String nodename,RemoteBuilder serv) {
 		if (debug) debug("addListerer("+buildername+","+nodename+","+serv+"): Adding remote builder ref to listeners hashtable key:"+nodename);
		listeners.put(nodename,serv);	
		return(true);
	}

	/**
	 * Connects to itself to retrieve the node information (in XML format) and save it as a hashtable.
	 * @return true, always
	 */
	public boolean getNode(String nodename,String tableName) {
 		if (debug) debug("getNode("+nodename+","+tableName+"): Getting node!");

		// connects to the server to obtain this node in xml
		// and parse it back to a node
		try {
			String proto=getProtocol();
			String host =getLocalHost();
			String sport=""+getLocalPort();
			if (debug) debug("getNode("+nodename+","+tableName+"): proto:"+proto+", host:"+host+", sport:"+sport);
 
			Socket connect=new Socket(remoteHost,remotePort);
			PrintStream out=new PrintStream(connect.getOutputStream());
			if (debug) debug("getNode("+nodename+","+tableName+"): Performing GET /remoteXML.db?"+tableName+"+"+nodename+"+"+proto+"+"+host+"+"+sport+" HTTP/1.1\r\n");
			out.print("GET /remoteXML.db?"+tableName+"+"+nodename+"+"+proto+"+"+host+"+"+sport+" HTTP/1.1\r\n");
			out.print("Pragma: no-cache\r\n");
			out.print("User-Agent: org.mmbase\r\n");
			out.print("\r\n");
			out.flush();
			DataInputStream in=new DataInputStream(connect.getInputStream());
			String line=readline(in);
			if (debug) debug("getNode("+nodename+","+tableName+"): Return value on GET request:"+line);
			if (line!=null && !line.equals("")) {
				if (line.indexOf("200 OK")!=-1) {
					if (debug) debug("getNode("+nodename+","+tableName+"): Reading XML data from header of requested file");
					Hashtable headers=readHeaders(in);
					try {
						int len=Integer.parseInt((String)headers.get("Content-Length"));
	   	 				byte[] buffer=readContentLength(len,in);
						String xml=new String(buffer,0,0,buffer.length);
						RemoteBuilder serv=(RemoteBuilder)listeners.get(nodename);
						if (serv==null) return(true);
						if (debug) debug("getNode("+nodename+","+tableName+"): Saving XML data as Hashtable with key value pairs");
						serv.gotXMLValues(xml);
					} catch(Exception e) {
	 					debug("getNode("+nodename+","+tableName+"): ERROR: while connecting to host("+remoteHost+","+remotePort+"): ");
						e.printStackTrace();
					}
				} else {
					debug("getNode(): Error expected 200 OK got: "+line);
				}
			} else {
				debug("getNode(): Error nothing received from server");
			}
			connect.close();
		} catch(Exception e) { 
			debug("getNode() general failure"); e.printStackTrace();
		}
		return(true);
	}

	public int getLocalPort() {
		return(port);
	}

	public String getProtocol() {
		return("http");
	}

	public String getLocalHost() {
		try {
			return(InetAddress.getLocalHost().getHostName());
		} catch(Exception e) {
			debug("getLocalHost(): ERROR"); e.printStackTrace();
			return("");
		}
	}

	/**
	 * Reads the data stored in HTTP header file that was sent back on request
	 * @param in the DataInputStream from which data will be read.
	 * @return a Hashtable with data from the header. 
	 */
	Hashtable readHeaders(DataInputStream in) {
		Hashtable headers=new Hashtable();
		String line=readline(in);
		while (line!=null && line.length()>2) {
			int pos=line.indexOf(":");
			if (pos!=-1) {
				headers.put(line.substring(0,pos),line.substring(pos+2,line.length()-2));	
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
                System.out.println("(readContentLength) -> can't read post msg from client");
            }
        return(buffer);
    }

	public String toString()
	{
		return classname + "(): remoteHost("+this.remoteHost+"), remotePort("+remotePort+")";
	}
}
