/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.remote;

import java.net.*;
import java.lang.*;
import java.io.*;
import java.util.*;

/**
 * server wrapper, Read parameters , start and stop daemon 
 * <PRE>
 * Usage : java org.mmbase.startRemoteBuilders
 * Params :  servicefile(s)
 * example : java org.mmbase.startRemoteBuilders /tmp/cdrom1.cfg
 * </PRE>
 *
 * @version  3 Okt 1999
 * @author Daniel Ockeloen
 */
public class startRemoteBuilders {

	private	String 	classname 	= getClass().getName();
	private boolean	debug		= true;
	private void	debug( String msg ) { System.out.println( classname +":"+ msg ); }

	static Vector runningServices=new Vector();

   /**
    * Main() called from OS with parameters defined above.
    */
    public static void main(String args[]) {

		String classname = "org.remote.startRemoteBuilders"; //getClass().getName();

	   /**
		* misc vars.
		*/
		boolean stopit = false;


		//MMRemoteMultiCast mmc;
		MMProtocolDriver con=null;
		MMRemoteProbe probe;
		
	   /**
		* Check if user started with command line options if not
		* start a server with the default config file and port. 
		*/
		if (args.length==0) {
			System.out.println( classname +":main(): no params !");
			System.exit(0);
		} else {
			ExtendedProperties ServiceReader=new ExtendedProperties();
			Hashtable servprops = ServiceReader.readProperties(args[0]);
			// decode protocol url
			String tmp=(String)servprops.get("connection");
		
			int pos=tmp.indexOf("://");
			String protocol=tmp.substring(0,pos);
			tmp=tmp.substring(pos+3);
			pos=tmp.indexOf(':');
			String host;
			int port=80;

			if (pos==-1) {
				host=tmp;
			} else {
				host=tmp.substring(0,pos);	
				try {
					port=Integer.parseInt(tmp.substring(pos+1));
				} catch(Exception e) {}
			}
			System.out.println( classname +":main(): Prot="+protocol);
			System.out.println( classname +":main(): Host="+host);
			System.out.println( classname +":main(): Port="+port);
			
			String name=(String)servprops.get("name");
			if (protocol.equals("ulticast")) {	
				System.out.println("starting multicast sender/receiver");
				con=(MMProtocolDriver)new MMRemoteMultiCast(name,host,port);
			} else if (protocol.equals("http")) {	
				System.out.println("starting http sender/receiver");
				con=(MMProtocolDriver)new MMHttpAcceptor(name,host,port);
			}

			System.out.println("starting check probe");
			//String number=(String)servprops.get("number");
			probe=new MMRemoteProbe(runningServices,con,name);
	

			System.out.println("starting services");
			int numberofparams=args.length;
			for (int i=1;i<args.length;i++) {
				String servicefile=args[i];
				ExtendedProperties Reader=new ExtendedProperties();
				Hashtable props = Reader.readProperties(servicefile);
				String buildername=(String)props.get("buildername");
				System.out.println("name="+buildername);
				try {
					Class newclass=Class.forName("org.mmbase.remote.builders."+buildername);
					System.out.println("startRemoteBuilders -> Loaded load class : "+newclass);
					RemoteBuilder serv = (RemoteBuilder)newclass.newInstance();
					if (serv!=null) {
						serv.init(con,servicefile);
						runningServices.addElement(serv);
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			System.out.println( classname+":main(): Running");
		}


	   /**
		* Let the server live until stopit is changed
		* this is a test server so change this if you need to.
		*/
		while(!stopit) {
			try {
				Thread.sleep(10*1000);
			} catch (Exception e) {
			}
		}

	   /**
		* Close down the daemon and return to OS.
		*/
		System.out.println("Stopping services");
    }


	static void postXML(String url,String name,String data) {
		String line=null;
		Socket connect;
		BufferedInputStream in=null;
		PrintStream out=null;

		StringTokenizer tok;
		String header,body;

		try {
			connect=new Socket("noise.vpro.nl",8080);
			try {
				out=new PrintStream(connect.getOutputStream());
			} catch (Exception e) {
			}

			/*
			try {
				connect_in=new BufferedInputStream(connect.getInputStream());
			} catch (Exception e) {
			}
			*/
			body=name+"="+data.replace(' ','+');
			header="POST "+url+" HTTP/1.0\nContent-type: application/x-www-form-urlencoded\nContent-length: "+(body.length())+"\nUser-Agent: org.mmbase\n";
			out.println(header);
			out.print(body);
			out.flush();
	
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

			try {
				connect.close();
			} catch(Exception e) {}
		} catch(Exception e) {
			System.out.println("Error connecting to object host : "+e);
		}	
	}

}
