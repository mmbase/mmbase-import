/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
/*
$Id: startRemoteBuilders.java,v 1.15 2001-12-14 09:33:32 pierre Exp $
*/
package org.mmbase.remote;

import java.net.*;
import java.lang.*;
import java.io.*;
import java.util.*;

//import org.mmbase.util.logging.Logger;
//import org.mmbase.util.logging.Logging;

/**
 * server wrapper, Read parameters , start and stop daemon 
 * <PRE>
 * Usage : java org.mmbase.startRemoteBuilders
 * Params :  servicefile(s)
 * example : java org.mmbase.startRemoteBuilders /tmp/cdrom1.cfg
 * </PRE>
 *
 * @rename StartRemoteBuilders
  * @version $Revision: 1.15 $ $Date: 2001-12-14 09:33:32 $
 * @author Daniel Ockeloen
 */
public class startRemoteBuilders { 
    //Logging removed automaticly by Michiel, and replace with __-methods
    private static String __classname = startRemoteBuilders.class.getName();


    boolean __debug = false;
    private static void __debug(String s) { System.out.println(__classname + ":" + s); }
    //private static Logger log = Logging.getLoggerInstance(startRemoteBuilders.class.getName());

    private static String sharedSecret = "NOKEYUSED";

    static Vector runningServices=new Vector();

   /**
    * Main() called from OS with parameters defined above.
    */
    public static void main(String args[]) {
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
            System.out.println("main(): no params !");
            System.exit(0);
        } else {
            ExtendedProperties ServiceReader=new ExtendedProperties();
            Hashtable servprops = ServiceReader.readProperties(args[0]);

            // Set the shared Secret
            // This key is shared by remoteBuilders and the MMBase system 
            // to be able to authenticate each other.
            if(servprops.containsKey("sharedsecret")) {
                System.out.println("main(): Found key 'sharedsecret.");
                sharedSecret=(String)servprops.get("sharedsecret");
            } else {
                System.out.println("main(): warning, please set the sharedsecret in server.properties");
            }

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
            /*log.debug*/__debug("main(): Prot="+protocol);
            /*log.debug*/__debug("main(): Host="+host);
            /*log.debug*/__debug("main(): Port="+port);
            /*log.debug*/__debug("main(): Key ="+sharedSecret);
            
            String name=(String)servprops.get("name");
            if (protocol.equals("multicast")) {    
                /*log.debug*/__debug("main(): starting multicast sender/receiver");
                con=(MMProtocolDriver)new MMRemoteMultiCast(name,host,port);
            } else if (protocol.equals("http")) {    
                /*log.debug*/__debug("main(): starting http sender/receiver");
                con=(MMProtocolDriver)new MMHttpAcceptor(name,host,port);
            }

            /*log.debug*/__debug("main(): starting check probe");
            //String number=(String)servprops.get("number");
            probe=new MMRemoteProbe(runningServices,con,name);
    

            /*log.debug*/__debug("main(): starting services");
            int numberofparams=args.length;
            for (int i=1;i<args.length;i++) {
                String servicefile=args[i];
                ExtendedProperties Reader=new ExtendedProperties();
                Hashtable props = Reader.readProperties(servicefile);
                String buildername=(String)props.get("buildername");
                /*log.debug*/__debug("main(): name="+buildername);
                try {
                    Class newclass=Class.forName("org.mmbase.remote.builders."+buildername);
                    /*log.debug*/__debug("main(): startRemoteBuilders -> Loaded load class : "+newclass);
                    RemoteBuilder serv = (RemoteBuilder)newclass.newInstance();
                    if (serv!=null) {
                        serv.init(con,servicefile);
                        runningServices.addElement(serv);
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
            /*log.debug*/__debug("main(): Running");
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
        /*log.debug*/__debug("main(): Stopping services");
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
            /*log.error*/__debug("postXML(): connecting to object host : "+e);
            /*log.error*/e.printStackTrace();
        }    
    }

    /**
     * checks if the received shared secret is equals to your own shared secret
     * @param receive shared secret
     * @return true if received shared secret equals your own shared secret 
     * @return false if received shared secret not equals your own shared secret 
     */ 
    public static boolean checkSharedSecret(String key) {
        if(sharedSecret.equals(key)) {
            return true;
        } else {
            /*log.error*/__debug("ERROR, the shared "+sharedSecret+"!="+key+" secrets don't match.");    
            return false;
        }
    }

    /**
     * get the shared Secret
     * @return the shared Secret
     */
    public static String getSharedSecret() {
        return sharedSecret;
    }
}
