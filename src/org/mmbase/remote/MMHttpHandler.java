/*
*/

package org.mmbase.remote;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

/**
 *
 * @version 27 Mar 1997
 * @author Daniel Ockeloen
 */
public class MMHttpHandler implements Runnable {

    private String  classname   = getClass().getName();
    private boolean debug       = false;

    private void debug( String msg ) {
        if( debug )
            System.out.println( classname +":"+ msg );
    }

	Thread kicker = null;

	Socket clientsocket;
	Hashtable listeners;

	public MMHttpHandler(Socket clientsocket,Hashtable listeners) {
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
			kicker = new Thread(this,"MMHttpAcceptor");
			kicker.start();
		}
	}
	
	public void run() {
		try {
			DataInputStream in=new DataInputStream(clientsocket.getInputStream());
			PrintStream out = new PrintStream(clientsocket.getOutputStream());

			String line=in.readLine();
			if (line!=null) {
				StringTokenizer tok=new StringTokenizer(line," \n\r\t");
				if (tok.hasMoreTokens()) {
					String method=tok.nextToken();
					// System.out.println("METHOD : "+method);
					if (method.equals("GET")) doGet(tok,in,out);
					if (method.equals("POST")) doPost(tok,in,out);
				}
			}

			clientsocket.close();	
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	void doGet(StringTokenizer tok, DataInputStream in, PrintStream out) {
		if (tok.hasMoreTokens()) {
			String query=tok.nextToken();
			if (query.indexOf("/remoteXML.db?")==0) {
				doXMLSignal(query.substring(14));
			}
		}
		out.println("200 OK");
		out.flush();
	}

	
	void doXMLSignal(String line) {
		StringTokenizer tok=new StringTokenizer(line,"+ \n\r\t");
		if (tok.hasMoreTokens()) {
			String number=tok.nextToken();
			if (tok.hasMoreTokens()) {
				String builder=tok.nextToken();
				if (tok.hasMoreTokens()) {
					String ctype=tok.nextToken();
					RemoteBuilder serv=(RemoteBuilder)listeners.get(number);
					if (serv==null) return;
					serv.nodeRemoteChanged(number,builder,ctype);
				}
			}	
		}
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
						} catch(Exception e) {}
					}
		}
		out.println("200 OK");
		out.flush();
	}

	/**
    * read a block into a array of ContentLenght size from the users networksocket
    *
    * @param table the hashtable that is used as the source for the mapping process
    * @returns byte[] buffer of length defined in the content-length mimeheader
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
                System.out.println("(readContentLength) -> can't read post msg from client");
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
