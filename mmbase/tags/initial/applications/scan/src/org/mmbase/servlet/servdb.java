/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.servlet;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

import javax.servlet.http.*;
import javax.servlet.*;

import org.mmbase.module.*;
import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.module.builders.*;
import org.mmbase.module.corebuilders.InsRel;
import org.mmbase.module.builders.vwms.*;
import org.mmbase.util.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * servdb handles binairy request (*.db) files for MMbase spaces as images (img.db)
 * realaudio (realaudio.db) but also xml (xml.db) and dtd's (dtd.db) with servscan
 * it provides the communication between the clients browser and the mmbase space.
 *
 * @version 23 Oct 1997
 * @author Daniel Ockeloen
 */
public class servdb extends JamesServlet {

//  ---------------------------------------------------
	private String 	classname 	= getClass().getName();
	private	boolean	debug		= false;
//  ---------------------------------------------------

	private		Date 				lastmod 	= null, tempdate;
	private		String 				templine,templine2,templine3,templine4;
	private		int 				y,m,d,u,mi,se;
	private		cacheInterface 		cache;
	private		filebuffer 			buffer; 
	private		boolean 			cached;    
	private		int 				filesize;
	private		FileInputStream 	scan;
	private		Hashtable 			Roots 		= new Hashtable();
	private		LRUHashtable 		numrabuf 	= new LRUHashtable(200);
//	private		imagesInterface 	images;
	private		MMBase 	mmbase;
	private		PlaylistsInterface 	playlists;
    private 	sessionsInterface 	sessions;
    // org.mmbase private 	StatisticsInterface stats;
	private		static boolean 		flipper		=false;


	int 	minSpeed	= 16000;	// 16 is min for speed
	int		maxSpeed	= 80000;	// 80 is max for speed
	int		minChannels	= 1;		// 1 channel is min for channels
	int		maxChannels	= 2;		// 2 channels is max for channels
		
	/**
	 * Construct a servfile worker, it should be places in a worker
	 * pool (by the admin thread).
	 */
	public servdb() {
		super();
	}

	public void onload() {
	}
	public void unload() {
	}
	public void shutdown(){
	}

	/**
	 * Init the mapfile, this is needed because it was created using
	 * a newInstanceOf().
	 *
	 * @param int worker id
	 */
	public void init() {
		// org.mmbase Roots		= getRoots();
		playlists	= (PlaylistsInterface)	getModule("PLAYLISTS");
		cache		= (cacheInterface)		getModule("cache");
		//images		= (imagesInterface)		getModule("IMAGES");
		mmbase		= (MMBase)		getModule("MMBASEROOT");
    	sessions	= (sessionsInterface)	getModule("SESSION");
    	// org.mmbase stats		= (StatisticsInterface)	getModule("STATS");
		//org.mmbase start();
		lastmod 	= new Date();
	}


    public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException,IOException
    {
		scanpage sp = getscanpage( req, res );

		boolean isInternal = isInternal(sp);

		// if (debug)
		{
			String msg = "["+getAddress(sp);

			if( isInternal )
				msg = msg + "(*)"; 

			msg = msg + "]"+req.getRequestURI()+"?"+req.getQueryString();
			debug("service("+msg+")");
		}

		int len;
		boolean done=false;
		cacheline cline=null;
		long nowdate=0;
		int cmd;
		String fileroot;
		// org.mmbase String mimetype=getContentType();
		String mimetype="image/jpeg";

		String req_line=req.getRequestURI();
//		debug("REQ_LINE="+req_line);
		// org.mmbase res.setKeepAlive(true);

		BufferedOutputStream out=null;
		try {
			out=new BufferedOutputStream(res.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}

		HttpPost poster=new HttpPost(req);

		// added to do enable Referer logging
		// ----------------------------------

		String ref=req.getHeader("Referer");
		//if (ref!=null && ref.indexOf("vpro.nl")==-1 && ref.indexOf(".58.169.")==-1) {
		if (ref!=null && ref.indexOf("vpro.nl")==-1 && ref.indexOf("vpro.omroep.nl")==-1 && ref.indexOf(".58.169.")==-1) {
			// second layer to make sure its valid/clean
			int pos=ref.indexOf("?");
			if (pos!=-1) {
				// probably a search engine remove the keywords need to be
				// counted in the future
				ref=ref.substring(0,pos);
			}
			debug("service(): Referer="+ref);
			if (ref.length()>70) ref=ref.substring(0,70);
			// org.mmbase if (stats!=null) stats.countSimpleEvent("Linked="+ref);	
		}



		// org.mmbase fileroot=(String)Roots.get(req.getAcceptor());
		// org.mmbase if (fileroot==null) fileroot=(String)Roots.get("www");
		fileroot="/usr/local/orion/default-site/html/";

		if (req_line.indexOf('#')!=-1) {
			req_line=req_line.substring(0,req_line.indexOf('#')-1);
		}
		templine2 = req.getHeader("Pragma");

		if (cache!=null && cache.get("www"+req.getRequestURI()+req.getQueryString())!=null && (templine2==null || templine2.indexOf("no-cache")==-1)) {
//			debug("service(): GRRR1");
			cline=cache.get("www"+req.getRequestURI()+req.getQueryString());
			filesize=cline.filesize;
			lastmod=cline.lastmod;
			mimetype=cline.mimetype;

			// are we sure we want to send the whole file ?

			templine = req.getHeader("If-Modified-Since");
			if (templine!=null) templine+=";"; // added for a netscape bug ?
			if (templine2==null) templine2=" ";
			try {
				nowdate=new Date(templine.substring(0,templine.indexOf(';'))).getTime();
			} catch(Exception e) {
				nowdate=(new Date(0)).getTime();
			}
			if (1==2 && templine!=null  && templine2.indexOf("no-cache")==-1 && !(lastmod.getTime()>nowdate)) {
				
		 		// logAccess(304,""+cline.filesize);
				res.setStatus(304,"Not Modified");
				res.setContentType(mimetype);
				res.setContentLength(cline.filesize);
				res.setHeader("Date",RFC1123.makeDate(new Date()));
				res.setHeader("Last-modified",RFC1123.makeDate(lastmod));
			} else {
			 	// logAccess(200,""+cline.filesize);
				try {
					if (cline.mimetype==null) {
						res.setContentType("text/html");
					} else {
						res.setContentType(cline.mimetype);
					}
					res.setContentLength(cline.filesize);
					res.setHeader("Date",RFC1123.makeDate(new Date()));
					res.setHeader("Last-modified",RFC1123.makeDate(cline.lastmod));
					out.write(cline.buffer,0,cline.filesize);
					out.flush();
					out.close();
				} catch(Exception e) {
					debug("service(): ERROR: Error writing to socket() : " + e.toString());
				}
			}
		} else {
			
			if (req_line.indexOf("..")!=-1) {
				req_line="index.html";
			}

			// 2hoog hack
			if (req_line.indexOf("/www/")==0) {
				req_line=req_line.substring(4);	
			}
			if (req_line.indexOf("/htbin/scan/www/")==0) {
				req_line=req_line.substring(15);	
			}

			if (done==false) {

				lastmod = new Date();
				cline = new cacheline(0);
				cline.lastmod=lastmod;
				cline.mimetype="image/jpeg";
				mimetype=cline.mimetype;
				// try {
					// hack for db  len=scan.read(cline.buffer,0,filesize);
			
					// ---
					// img
					// ---

					if (req.getRequestURI().indexOf("img")!=-1) {
						Images bul=(Images)mmbase.getMMObject("images");
						cline.buffer=bul.getImageBytes5(getParamVector(req));
						cline.mimetype=bul.getImageMimeType(getParamVector(req));
						mimetype=cline.mimetype;

						// check point, plugin needed for mirror system
						checkImgMirror(req);
					} 
					else 

					// ---
					// xml
					// ---
					if (req.getRequestURI().indexOf("xml")!=-1) {
						cline.buffer=getXML(getParamVector(req));
						cline.mimetype="text/plain";
						mimetype=cline.mimetype;
					} 
					else 

					// ---
					// dtd
					// ---
					if (req.getRequestURI().indexOf("dtd")!=-1) {
						cline.buffer=getDTD(getParamVector(req));
						cline.mimetype="text/html";
						mimetype=cline.mimetype;
					} 
					else 

					// --------
					// rastream
					// --------
					if (req.getRequestURI().indexOf("rastream")!=-1) 
					{
						debug("service(rastream)");

						// is it a audiopart or an episode ?
						// ---------------------------------

						Vector vec = getParamVector(req);

						if (vec.contains("a(session)")) {
							vec=addRAMSpeed(sp,vec,res);
						}

						if ( getParamValue("ea", vec)  != null ) {
							if (debug) debug("service(rastream): episode found");
							cline.buffer = playlists.getRAMfile( isInternal, vec );
						} else {
							if (debug) debug("service(rastream): rastream found");
							cline.buffer = getRAStream(vec,sp,res);
						}

						if (cline.buffer!=null) {
							//debug("Buffer not null, returning stream");
							cline.mimetype ="audio/x-pn-realaudio";
							mimetype=cline.mimetype;
						} else {
							String ur=getParamValue("url",getParamVector(req));
							String n=getParamValue("n",getParamVector(req));
							//debug("Buffer is null!!! Returning url("+ur+") and params("+n+").");
							res.setStatus(302,"OK");
							res.setContentType("text/html");
							res.setHeader("Location",ur+"?"+n);
							return;
						}
					} 
					else

					// --------
					// rmstream
					// --------
					if (req.getRequestURI().indexOf("rmstream")!=-1) 
					{

						if (debug) debug("service(rastream)");

						// is it a audiopart or an episode ?
						// ---------------------------------

						Vector vec = getParamVector(req);

						if (vec.contains("a(session)")) {
							vec=addRAMSpeed(sp,vec,res);
						}

						if ( getParamValue("ea", vec)  != null )
						{
							if (debug) debug("service(rastream): episode found");
							cline.buffer = playlists.getRAMfile(isInternal, vec );
						}
						else
						{
							if (debug) debug("service(rastream): rastream found");
							cline.buffer=getRMStream(vec,sp,res);
						}

						if (cline.buffer!=null) {
							//debug("Buffer not null, returning stream");
							cline.mimetype="video/vnd.rn-realvideo";
							mimetype=cline.mimetype;
						} else {
							String ur=getParamValue("url",getParamVector(req));
							String n=getParamValue("n",getParamVector(req));
							debug("service(): --> Buffer is null!!! Returning url("+ur+") and params("+n+") <--");
							res.setStatus(302,"OK");
							res.setContentType("text/html");
							res.setHeader("Location",ur+"?"+n);
							return;
						}
	// ---

					// --------
					// playlist
					// --------

					} 
					else if (req.getRequestURI().indexOf("playlist")!=-1) {
						// added to do enable Referer logging
						ref=req.getHeader("Referer");
						if (ref!=null && ref.indexOf("vpro.nl")==-1 && ref.indexOf("vpro.omroep.nl")==-1 && ref.indexOf(".58.169.")==-1) 
						{
							// second layer to make sure its valid/clean
							int pos=ref.indexOf("?");
							if (pos!=-1) {
								// probably a search engine remove the keywords need to be
								// counted in the future
								ref=ref.substring(0,pos);
							}
							debug("servdb2 R="+ref);
							if (ref.length()>70) ref=ref.substring(0,70);
							// org.mmbase if (stats!=null) stats.countSimpleEvent("Desktop="+ref);	
						}
						//debug("Playlist="+playlists);
						if (playlists!=null) {
							Vector vec=getParamVector(req);
							vec=checkPostPlaylist(poster,sp,vec);
							if (vec.contains("a(session)")) {
								vec=addRAMSpeed(sp,vec,res);
							}
							// filter and replace the mods found if needed
							vec=filterSessionMods(sp,vec,res);
							vec=checkSessionJingle(sp,vec,res);
							// call the playlist module for the playlist wanted
							cline.buffer=playlists.getRAMfile(isInternal, vec);
							cline.mimetype="audio/x-pn-realaudio";
							mimetype=cline.mimetype;
						}
					// ----
					// jump
					// ----

					} else if (req.getRequestURI().indexOf("jump")!=-1) {
						// do jumper
						long begin=(long)System.currentTimeMillis();
						Jumpers bul=(Jumpers)mmbase.getMMObject("jumpers");
						String key=(String)(getParamVector(req)).elementAt(0);
						String url = (String)bul.getJump(key);
						debug("jump.db Url="+url);
						if (url!=null) {
							// jhash.put(key,url);
							res.setStatus(302,"OK");
							res.setContentType("text/html");
							res.setHeader("Location",url);
							Date d=new Date(0);
							String dt=RFC1123.makeDate(d);
							res.setHeader("Expires",dt);
							res.setHeader("Last-Modified",dt);
							res.setHeader("Date",dt);
						}
						long end=(long)System.currentTimeMillis();
						//debug("getUrl="+(end-begin)+" ms");
					}
			
					if (cline.buffer!=null) {
						len=cline.buffer.length;
						filesize=len;
					} else {
						len=0;
					}

					if (len!=-1)  {
						try {
							res.setContentType(mimetype);
							//res.setContentLength(filesize);
							cline.filesize=filesize;
							res.setHeader("Last-modified",RFC1123.makeDate(lastmod));
							res.setHeader("Date",RFC1123.makeDate(new Date()));
							res.setContentLength(cline.filesize);
							out.write(cline.buffer,0,filesize);
							out.flush();
							out.close();
							cache.put("www"+req.getRequestURI()+req.getQueryString(),cline);
						} catch(Exception e) {
							debug("Servfile : Error writing to socket");
							len=-1;
						}
					}
			}
		}
	}

	private final void setHeaders(HttpServletRequest req,HttpServletResponse res,int len) {
		String ac="www";

		res.setContentType("text/html; charset=iso-8859-1");
		res.setContentLength(len);
		Date d=new Date(0);
//		Date d=new Date(DateSupport.currentTimeMillis()-(1000*24*3600)); // probably this one
		String dt=RFC1123.makeDate(d);
//		debug("TIJD="+dt);
		res.setHeader("Expires",dt);
		res.setHeader("Last-Modified",dt);
		res.setHeader("Date",dt);
	}

	boolean Show_Directory(String pathname,File dirfile, PrintWriter out) {
		String body,bfiles,bdirs,header,line;
		int i;

		String files[] = dirfile.list();		
		bfiles="";bdirs="";

		body="<TITLE>James : Index of "+pathname+"</TITLE>";	
		body+="<BODY BGCOLOR=\"#FFFFFF\">";	
		body+="<h1>James : Index of "+pathname+"</h1>";	
		if (pathname.lastIndexOf('/')!=-1) {
			bdirs+="<IMG SRC=\"/jamesdoc/images/back.gif\"><A HREF=\""+pathname.substring(0,pathname.lastIndexOf('/'))+"\" TARGET=\""+pathname+"\">Parent Directory</A>\n";

		}	
		bdirs+="<HR>";
		
		// Luke Gorrie's fix, avoid URL's with '//' in them
		// So if the URL ends with '/' then strip that.
		i=pathname.length();
		if (pathname.charAt(i-1)=='/') pathname=pathname.substring(0,i-1);

		for (i=0;i<files.length;i++) {
			File theFile = new File(dirfile,files[i]);
			if (theFile.isDirectory()) {
				bdirs+="<TR><TD><IMG SRC=\"/jamesdoc/images/dir.gif\"></TD><TD><A HREF=\""+pathname+"/"+files[i]+"\">"+files[i]+"/</A></TD>";
				bdirs+="<TD>"+(new Date(theFile.lastModified())).toString();
				bdirs+="</TD><TD>-</TD></TR>\n";
			} else {
				bfiles+="<TR><TD><IMG SRC=\"/jamesdoc/images/text.gif\"></TD><TD><A HREF=\""+pathname+"/"+files[i]+"\">"+files[i]+"</A></TD><TD>"+(new Date(theFile.lastModified())).toString()+"</TD><TD>"+(theFile.length()/1024)+" Kb</TD></TR>";
			}
		}	

		body+="<TABLE WIDH=100%>";
		if (files.length!=0) {
			body+="<TR><TD><B>Type</B></TD><TD><B>Name</B></TD><TD><B>Last-modified</B></TD><TD><B>Size</B></TD>";	
		} else {
			body+="<TR><TD>No Files Found</TD>";	
		}
		body+=bdirs+bfiles;
		body+="</TABLE>";
		header="HTTP/1.0 200 OK\nMIME-Version: 1.0\nServer: James/utils\nContent-type: text/html\nContent-Length: "+body.length()+"\n\n";

		out.print(header+body);
		return true;
	}	

	/*
	public void stop(){
		if (scan!=null) {
			try {
				scan.close();
			} catch (IOException e) {
			}
		}
		// Should be here not directly above
		super.stop();
	}
	*/

	public String getServletInfo()  {
		return("ServFile handles normal file requests, Daniel Ockeloen");
	}

	Hashtable getRoots() {
		int pos;
		String tmp,tmp2;
		Hashtable result=new Hashtable();
		//result.put("www",DocumentRoot);
		
		for (Enumeration e=getInitParameters().keys();e.hasMoreElements();) {
			tmp=(String)e.nextElement();
			tmp2=(String)getInitParameter(tmp);
			pos=tmp.indexOf("Root.");
			if (pos==0) {
				result.put(tmp.substring(5),tmp2);
			}
		}
		return(result);
	}

	private scanpage getscanpage( HttpServletRequest req, HttpServletResponse res )
	{
		scanpage sp=new scanpage();
        sp.req_line=req.getRequestURI();
        sp.querystring=req.getQueryString();
        sp.setReq(req);
        String sname=getCookie(sp.req,res);
        sp.sname=sname;
        sessionInfo session=sessions.getSession(sp,sp.sname);
        sp.session=session;
        ServletConfig sc=getServletConfig();
        ServletContext sx=sc.getServletContext();
        String mimetype=sx.getMimeType(sp.req_line);
        if (mimetype==null) mimetype="text/html";
        sp.mimetype=mimetype;

		return sp;
	}

	public Vector filterSessionMods(scanpage sp,Vector params,HttpServletResponse res) {
		// debug("filterSessionMods("+sp+","+params+","+res+"): start");
		sessionInfo session=sessions.getSession(sp,sp.sname);
		if (session!=null) {
				int pos1;
				String line;
				Enumeration e=params.elements();
				if (e.hasMoreElements()) {
					line=(String)e.nextElement();	
					debug("filterSessionMods(): line("+line+")");
					pos1=line.indexOf("(SESSION-");

					// debug("filterSessionMods(): pos1("+pos1+")");

					if (pos1!=-1) {
						int pos2=line.indexOf(")");
					// debug("filterSessionMods(): pos2("+pos2+")");
						String part1=line.substring(0,pos1);		
						String part2=line.substring(pos1+9,pos2);		
						debug("servdb -> REPLACE="+part1+" "+part2);

						String value=sessions.replace(sp,part2);

						//String value=null;
						debug("servdb -> REPLACE2="+value);
						if (value==null) {
							value="";
						}
						params.removeElement(line);
						params.addElement(part1+"("+value+")");
					}
				}
			}
			else
				debug("filterSessionMods(): ERROR: session is null!");

		return(params);
	}

	public Vector checkSessionJingle(scanpage sp,Vector params,HttpServletResponse res) {
		sessionInfo session=sessions.getSession(sp,sp.sname);
		boolean havesession=false,havesbj=false;
		String str="";
		int i=0;

		for (Enumeration e=params.elements();e.hasMoreElements();) {
			str=(String)e.nextElement();
			if (str.startsWith("sbj(")) {
				havesbj=true;
				break;
			}
			i++;
		}

		if (havesbj) {
			int t;
			Vector v=new Vector();
			// str is param
			StringTokenizer tok=new StringTokenizer(str,",()");
			if (tok.hasMoreTokens()) tok.nextToken();
			while(tok.hasMoreTokens()) v.addElement(tok.nextToken());
			
			if (session!=null) {
				// If we have session AND speed
				if ((str=sessions.getValue(session,"SETTING_RASPEED"))!=null) {
					try { 
						t=Integer.parseInt(str);
					} catch(Exception e) {
						t=-1;
					} 
					if (t>0) havesession=true;
				}
			}
			// i is index in param list
			if (havesession) {
				str=(String)v.elementAt(1);
			} else {
				str=(String)v.elementAt(0);
			}
			debug("checkSessionJingle(): "+havesession+" : "+str);
			params.setElementAt("bj("+str+")",i);
			debug("checkSessionJingle(): "+params.elementAt(i));
		}
		return(params);
	}

	public Vector addRAMSpeed(scanpage sp, Vector params,HttpServletResponse res) {
		String wspeed=null,wchannels=null;
		int ispeed=16000;int ichannels=1;

		sessionInfo session=sessions.getSession(sp,sp.sname);
		if (session!=null) {
				wspeed=sessions.getValue(session,"SETTING_RASPEED");
				// debug("w="+wspeed);
				if (wspeed!=null) {
					wchannels=sessions.getValue(session,"SETTING_RACHANNELS");
					// debug("w="+wchannels);
				} else {
					params.addElement("s(16000)");
					params.addElement("c(1)");
					// so no speed set then return to signal a goto
					//return(null); // removed for mtus
				}
				try {
					ispeed=Integer.parseInt(wspeed);
					ichannels=Integer.parseInt(wchannels);
				} catch(Exception e) {
					params.addElement("s(16000)");
					params.addElement("c(1)");
				}
				params.addElement("s("+ispeed+")");
				params.addElement("c("+ichannels+")");
				//debug("ADDED="+ispeed+" "+ichannels);
			}
		return(params);
	}




	public byte[] getXML(Vector params) {
		debug("getXML(): param="+params);
		String result="";
		if (params.size()==0) return(null);
		MMObjectBuilder bul=mmbase.getMMObject("insrel");
		if (params.size()==1) {
			MMObjectNode node=null;
			try {
				debug("getXML(): 1 bul="+bul+" node="+node);
				node=bul.getNode((String)params.elementAt(0));	
				debug("getXML(): 2 bul="+bul+" node="+node);
			} catch(Exception e) { }
			if (node!=null) {
				result=node.toXML();
			} else {
				result="Sorry no valid mmnode so no xml can be given";
			}
		} else if (params.size()==2) {
			try {
				int start=Integer.parseInt((String)params.elementAt(0));
				int end=Integer.parseInt((String)params.elementAt(1));
				for (int i=start;i<(end+1);i++) {
					MMObjectNode node=null;
					try {
						node=bul.getNode(i);	
					} catch(Exception e) { }
					if (node!=null) {
						result+=node.toXML()+"\n\n";
					}
				}
			} catch(Exception f) {
				result="Sorry no valid mmnode so no xml can be given";
			}
		}
		byte[] data=new byte[result.length()];
		result.getBytes(0,result.length(),data,0);	
		return(data);	
	}

	public byte[] getDTD(Vector params) {
		String result="Test DTD";
		byte[] data=new byte[result.length()];
		result.getBytes(0,result.length(),data,0);	
		return(data);	
	}

	public byte[] getRAStream(Vector params,scanpage sp,HttpServletResponse resp) {
		flipper=!flipper;
		String wspeed=null,wpart=null,wchannels=null;
		int ispeed=80000;int ichannels=2;
		MMObjectBuilder bul=mmbase.getMMObject("rawaudios");
		// check if we want autoread ?
		String auto=getParamValue("a",params);
		if (auto!=null && auto.equals("session")) {
			// aaaa
			String nummer=getParamValue("n",params);
			sessionInfo session=sessions.getSession(sp,sp.sname);

			if (session!=null) {
				wpart=nummer;
				wspeed=sessions.getValue(session,"SETTING_RASPEED");
				if (wspeed!=null) {
					wchannels=sessions.getValue(session,"SETTING_RACHANNELS");
				} else {
					// so no speed set then return to signal a goto
					wspeed = "16000";
					wchannels = "1";
				}
				try {
					ispeed=Integer.parseInt(wspeed);
					ichannels=Integer.parseInt(wchannels);
				} catch(Exception e) {
				}
			}
		} else {
		try {
			try {
			wpart = (String)params.elementAt(0);
			int u=Integer.parseInt(wpart);
			} catch(Exception e) {
				debug("getRAStream(): ERROR: RAservlet got a string as number value");
				wpart="1";
			}
		} catch(Exception e) {
			wpart = null;
		}
		try {
			wspeed = (String)params.elementAt(1);
			// hack hack
			ispeed=Integer.parseInt(wspeed);
		} catch(Exception e) {
			wspeed = null;
		}
		try {
			wchannels = (String)params.elementAt(2);
			// hack hack
			ichannels=Integer.parseInt(wchannels);
		} catch(Exception e) {
			wchannels = null;
		}
		}
		debug("getRAStream(): Number="+wpart+" speed="+wspeed+" wchannels="+wchannels);
		// added for statistics
		//setCount(wspeed,wchannels); 

		String result="",bestresult="";
		int bestspeed=0,bestchannels=0;
		if (wpart!=null) 
		{
			String where="id=="+wpart;
			//if (wspeed!=null) where+="+speed=="+wspeed;
			//if (wchannels!=null) where+="+channels=="+wchannels;
			//Enumeration res=bul.search(where);
			Vector tmp=(Vector)numrabuf.get(where);
			if (tmp==null) {
				tmp=bul.searchVector(where);
				if (tmp!=null) {
					numrabuf.put(where,tmp);
				} else {
					numrabuf.put(where,new Vector());
				}	
			}
			Enumeration res=tmp.elements();
			MMObjectNode node;
			boolean surestream=false;
			for (;res.hasMoreElements();) 
			{
				node=(MMObjectNode)res.nextElement();
				//debug("getRAStream(): WOW="+node);
				int id		= node.getIntValue("id");	
				int format	= node.getIntValue("format");
				int speed	= node.getIntValue("speed");
				int status	= node.getIntValue("status");
				int channels= node.getIntValue("channels");
				String url	= node.getStringValue("url");

				// try to obtain the song info for ra window
				MMObjectBuilder bul2=mmbase.getMMObject("cdtracks");
				MMObjectNode node2=bul2.getNode( id );
				String title=node2.getStringValue("title");
				if (title==null) title="";

				//debug("getRAStream("+id+"): Gettting start/stop time.");

				// ---------
				// startstop
				// ---------
		
				String	starttime = null;
				String	stoptime  = null;	
				// get prop object	
				// ---------------

				MMObjectBuilder 	bulstartstop 	= mmbase.getMMObject("properties");
				if (bulstartstop != null)
				{
					// get node from props
					// -------------------

					MMObjectNode		nodestartstop 	= bulstartstop.getNode( id ); // check this
					if (nodestartstop != null)
					{
						//debug("Found node");
						// get value from prop
						// -------------------

						MMObjectNode sStartprop = (MMObjectNode) nodestartstop.getProperty("starttime");
						if (sStartprop != null)
						{
							String				sStartkey		= sStartprop.getStringValue( "key") ;
							String				sStartvalue		= sStartprop.getStringValue( "value" );
							starttime = sStartvalue;
							//debug("starttime:"+sStartvalue);
						}
						//else
							//debug("no starttgime defined");
 
						MMObjectNode	sStopprop = (MMObjectNode) nodestartstop.getProperty("stoptime");
						if (sStopprop != null)
						{
							String				sStopkey		= sStopprop.getStringValue( "key" ) ;
							String				sStopvalue		= sStopprop.getStringValue( "value" );
							stoptime = sStopvalue;
							//debug("stoptime:"+sStopvalue);
						}
						//else
							//debug("no stoptime defined");
					}
					//else
						//debug("getRAStream("+id+"): ERROR: could not get node for start/stop times.");
				}
				//else
					//debug("getRAStream("+id+"): ERROR: Cannot get properties for this file.");


				// try to obtain the group
				String author=null;
				Enumeration g=mmbase.getInsRel().getRelated(node2.getIntValue("number"),1573);
				if (g.hasMoreElements()) {
					node2=(MMObjectNode)g.nextElement();
					author=node2.getStringValue("name");
					//debug("Found related"+author+" "+node2);
				}
				if (author==null) author="";

				// end obtain
				if (surestream==false && format==2 && url!=null && status>1) 
				{

					// niet duidelijk waar dit voor is, daniel.
					// detect new url format (multihost)

					String startend = "";
					if (starttime != null) startend  = "&start=\""+starttime+"\"";
					if (stoptime  != null) startend += "&end=\""+stoptime+"\"";

					if (url.indexOf("F=")!=-1) {
						String tmpa=getBestMirrorUrl(sp,url);
						result+="pnm://"+tmpa+"?title=\""+title+"\"&author=\""+author+"\""+startend+"\n";
					} else {
						if (url.indexOf("http://station.vpro.nl/audio/ra/")==0) {
							if (flipper) 
								result+="pnm://station.vpro.nl/"+url.substring(32)+"?title=\""+title+"\"&author=\""+author+"\""+startend+"\n";
							else
								result+="pnm://station.vpro.nl/"+url.substring(32)+"?title=\""+title+"\"&author=\""+author+"\""+startend+"\n";
						}
					}
					// eind van niet duidelijk.
					if (wspeed!=null && wchannels!=null) 
					{
						if (speed>bestspeed && speed<=ispeed) {
							bestspeed=speed;
							bestchannels=channels;
							// detect new url format (multihost)
							if (url.indexOf("F=")!=-1) {
								String tmpa=getBestMirrorUrl(sp,url);
								bestresult="pnm://"+tmpa+"?title=\""+title+"\"&author=\""+author+"\""+startend+"\n";
							} else {
								if (flipper) 
									bestresult="pnm://station.vpro.nl/"+url.substring(32)+"?title=\""+title+"\"&author=\""+author+"\""+startend+"\n";
								 else 
									bestresult="pnm://station.vpro.nl/"+url.substring(32)+"?title=\""+title+"\"&author=\""+author+"\""+startend+"\n";
							}
						} else if (speed==bestspeed && channels>bestchannels && channels<=ichannels) {
							bestspeed=speed;
							bestchannels=channels;
							// detect new url format (multihost)
							if (url.indexOf("F=")!=-1) {
								String tmpa=getBestMirrorUrl(sp,url);
								bestresult="pnm://"+tmpa+"?title=\""+title+"\"&author=\""+author+"\"\n";
							} else {
								if (flipper) {
									bestresult="pnm://station.vpro.nl/"+url.substring(32)+"?title=\""+title+"\"&author=\""+author+"\""+startend+"\n";
								} else {
									bestresult="pnm://station.vpro.nl/"+url.substring(32)+"?title=\""+title+"\"&author=\""+author+"\""+startend+"\n";
								}
							}
						}
					}
				} 
				else 
				if (format==6 && url!=null && status>1) 
				{
					debug("getRAStream("+url+"): surestream detected");
					surestream=true;
					// niet duidelijk waar dit voor is, daniel.
					// detect new url format (multihost)

					String startend = "";
					if (starttime != null) startend  = "&start=\""+starttime+"\"";
					if (stoptime  != null) startend += "&end=\""+stoptime+"\"";

					if (url.indexOf("F=")!=-1) {
						String tmpa=getBestMirrorUrl(sp,url);
						bestresult="rtsp://"+tmpa+"?title=\""+title+"\"&author=\""+author+"\""+startend+"\n";
					} else {
						if (url.indexOf("rtsp://station.vpro.nl/audio/ra/")==0) {
							bestresult="rtsp://station.vpro.nl/"+url.substring(32)+"?title=\""+title+"\"&author=\""+author+"\""+startend+"\n";
						}
					}
					bestspeed=speed;
					bestchannels=channels;
				}
			}
		}
		if (wspeed!=null && wchannels!=null) {
			// debug("getRAStream(): I have("+bestresult+")");

			byte[] data=new byte[bestresult.length()];
			bestresult.getBytes(0,bestresult.length(),data,0);	
			return(data);	
		} else {
			// debug("getRAStream(): result: I have("+result+")");

			byte[] data=new byte[result.length()];
			result.getBytes(0,result.length(),data,0);	
			return(data);	
		}
	}


	public byte[] getRMStream(Vector params, scanpage sp,HttpServletResponse resp) {
		flipper=!flipper;
		String wspeed=null,wpart=null,wchannels=null;
		int ispeed=80000;int ichannels=2;
		MMObjectBuilder bul=mmbase.getMMObject("rawaudios");
		// check if we want autoread ?
		String auto=getParamValue("a",params);
		if (auto!=null && auto.equals("session")) {
			// aaaa
			String nummer=getParamValue("n",params);
			sessionInfo session=sessions.getSession(sp,sp.sname);
			if (session!=null) {
				wpart=nummer;
				wspeed=sessions.getValue(session,"SETTING_RASPEED");
				if (wspeed!=null) {
					wchannels=sessions.getValue(session,"SETTING_RACHANNELS");
				} else {
					// so no speed set then return to signal a goto
					return(null);
				}
				try {
					ispeed=Integer.parseInt(wspeed);
					ichannels=Integer.parseInt(wchannels);
				} catch(Exception e) {
				}
			}
		} else {
		try {
			try {
			wpart = (String)params.elementAt(0);
			int u=Integer.parseInt(wpart);
			} catch(Exception e) {
				debug("getRMStream(): RAservlet got a string as number value");
				wpart="1";
			}
		} catch(Exception e) {
			wpart = null;
		}
		try {
			wspeed = (String)params.elementAt(1);
			// hack hack
			ispeed=Integer.parseInt(wspeed);
		} catch(Exception e) {
			wspeed = null;
		}
		try {
			wchannels = (String)params.elementAt(2);
			// hack hack
			ichannels=Integer.parseInt(wchannels);
		} catch(Exception e) {
			wchannels = null;
		}
		}
		//debug("Number="+wpart+" speed="+wspeed+" wchannels="+wchannels);
		// added for statistics
		//setCount(wspeed,wchannels); 

		String result="",bestresult="";
		int bestspeed=0,bestchannels=0;
		if (wpart!=null) 
		{
			String where="id=="+wpart;
			//if (wspeed!=null) where+="+speed=="+wspeed;
			//if (wchannels!=null) where+="+channels=="+wchannels;
			//Enumeration res=bul.search(where);
			Vector tmp=(Vector)numrabuf.get(where);
			if (tmp==null) {
				tmp=bul.searchVector(where);
				if (tmp!=null) {
					numrabuf.put(where,tmp);
				} else {
					numrabuf.put(where,new Vector());
				}	
			}
			Enumeration res=tmp.elements();
			MMObjectNode node;
			for (;res.hasMoreElements();) 
			{
				node=(MMObjectNode)res.nextElement();
				int id		= node.getIntValue("id");	
				int format	= node.getIntValue("format");
				int speed	= node.getIntValue("speed");
				int status	= node.getIntValue("status");
				int channels= node.getIntValue("channels");
				String url	= node.getStringValue("url");

				// try to obtain the song info for ra window
				MMObjectBuilder bul2=mmbase.getMMObject("cdtracks");
				MMObjectNode node2=bul2.getNode( id );
				String title=node2.getStringValue("title");
				if (title==null) title="";

				//debug("getRAStream("+id+"): Gettting start/stop time.");

				// ---------
				// startstop
				// ---------
		
				String	starttime = null;
				String	stoptime  = null;	
				// get prop object	
				// ---------------

				MMObjectBuilder 	bulstartstop 	= mmbase.getMMObject("properties");
				if (bulstartstop != null)
				{
					// get node from props
					// -------------------

					MMObjectNode		nodestartstop 	= bulstartstop.getNode( id ); // check this
					if (nodestartstop != null)
					{
						//debug("Found node");
						// get value from prop
						// -------------------

						MMObjectNode sStartprop = (MMObjectNode) nodestartstop.getProperty("starttime");
						if (sStartprop != null)
						{
							String				sStartkey		= sStartprop.getStringValue( "key") ;
							String				sStartvalue		= sStartprop.getStringValue( "value" );
							starttime = sStartvalue;
							//debug("starttime:"+sStartvalue);
						}
						//else
							//debug("no starttgime defined");
 
						MMObjectNode	sStopprop = (MMObjectNode) nodestartstop.getProperty("stoptime");
						if (sStopprop != null)
						{
							String				sStopkey		= sStopprop.getStringValue( "key" ) ;
							String				sStopvalue		= sStopprop.getStringValue( "value" );
							stoptime = sStopvalue;
							//debug("stoptime:"+sStopvalue);
						}
						//else
							//debug("no stoptime defined");
					}
					//else
						//debug("getRAStream("+id+"): ERROR: could not get node for start/stop times.");
				}
				//else
					//debug("getRAStream("+id+"): ERROR: Cannot get properties for this file.");


				// try to obtain the group
				String author=null;
				Enumeration g=mmbase.getInsRel().getRelated(node2.getIntValue("number"),1573);
				if (g.hasMoreElements()) {
					node2=(MMObjectNode)g.nextElement();
					author=node2.getStringValue("name");
					//debug("Found related"+author+" "+node2);
				}
				if (author==null) author="";

				// end obtain
				if (format==2 && url!=null && status>1) 
				{

					// niet duidelijk waar dit voor is, daniel.
					// detect new url format (multihost)

					String startend = "";
					if (starttime != null) startend  = "&start=\""+starttime+"\"";
					if (stoptime  != null) startend += "&end=\""+stoptime+"\"";

					if (url.indexOf("F=")!=-1) {
						String tmpa=getBestMirrorUrl(sp,url);
						result+="rtsp://"+tmpa+"?title=\""+title+"\"&author=\""+author+"\""+startend+"\n";

						// debug("#1_0 : " + result);
					} else {
						if (url.indexOf("http://station.vpro.nl/audio/ra/")==0) {
							if (flipper) 
							{
								result+="rtsp://station.vpro.nl/"+url.substring(32)+"?title=\""+title+"\"&author=\""+author+"\""+startend+"\n";
								// debug("#1_1 : " + result);
							}
							else
							{
								result+="rtsp://station.vpro.nl/"+url.substring(32)+"?title=\""+title+"\"&author=\""+author+"\""+startend+"\n";
								// debug("#1_2 : " + result + ", url("+url+")");
							}
						}
					}
					// eind van niet duidelijk.
					if (wspeed!=null && wchannels!=null) 
					{
						if (speed>bestspeed && speed<=ispeed) {
							bestspeed=speed;
							bestchannels=channels;
							// detect new url format (multihost)
							if (url.indexOf("F=")!=-1) {
								String tmpa=getBestMirrorUrl(sp,url);
								bestresult="rtsp://"+tmpa+"?title=\""+title+"\"&author=\""+author+"\""+startend+"\n";
								// debug("#2_0 : " + bestresult);
							} else {
								if (flipper) 
								{
									bestresult="rtsp://station.vpro.nl/"+url.substring(32)+"?title=\""+title+"\"&author=\""+author+"\""+startend+"\n";
									// debug("#2_1 : " + bestresult + ", url("+url+")");
								}
								else 
								{
									bestresult="rtsp://station.vpro.nl/"+url.substring(32)+"?title=\""+title+"\"&author=\""+author+"\""+startend+"\n";
									// debug("#2_2 : " + bestresult);
								}
							}
						} else if (speed==bestspeed && channels>bestchannels && channels<=ichannels) {
							bestspeed=speed;
							bestchannels=channels;
							// detect new url format (multihost)
							if (url.indexOf("F=")!=-1) {
								String tmpa=getBestMirrorUrl(sp,url);
								bestresult="rtsp://"+tmpa+"?title=\""+title+"\"&author=\""+author+"\"\n";
								// debug("#3_0 : " + bestresult);
							} else {
								if (flipper) {
									bestresult="rtsp://station.vpro.nl/"+url.substring(32)+"?title=\""+title+"\"&author=\""+author+"\""+startend+"\n";
									// debug("#3_1 : " + bestresult);
								} else {
									bestresult="rtsp://station.vpro.nl/"+url.substring(32)+"?title=\""+title+"\"&author=\""+author+"\""+startend+"\n";
									// debug("#3_2 : " + bestresult);
								}
							}
						}
					}
				}
			}
		}
		if (wspeed!=null && wchannels!=null) {
			// debug("getRAStream(): Bestresult: I have("+bestresult+")");

			byte[] data=new byte[bestresult.length()];
			bestresult.getBytes(0,bestresult.length(),data,0);	
			return(data);	
		} else {
			// debug("getRAStream(): result: I have("+result+")");

			byte[] data=new byte[result.length()];
			result.getBytes(0,result.length(),data,0);	
			return(data);	
		}
	}



	/**
	* try to obtain a decoded param string from the input Vector
	* format in : s(11212)
	* format out 11212
	* on a get with 's'
	*/	
	public String getParamValue(String wanted,Vector params) {
		String val=null;
		int pos=-1;
	        Enumeration e=params.elements();
		while (e.hasMoreElements()) {
		 	val=(String)e.nextElement();
			pos=val.indexOf((wanted+"("));
			if (pos!=-1) {
				pos=val.indexOf('(');
				int pos2=val.indexOf(')');
				return(val.substring(pos+1,pos2));
			}
		}
		return(null);
	}

	/**
	 * Moved to module RALogger.java
	 *
	private void setCount(String wspeed, String wchannels) {
		if (stats!=null) {
			if (wspeed.equals("16000") && wchannels.equals("1")) {
				stats.setCount("64127",1);
			} else if (wspeed.equals("32000") && wchannels.equals("1")) {
				stats.setCount("64128",1);
			} else if (wspeed.equals("40000") && wchannels.equals("1")) {
				stats.setCount("64130",1);
			} else if (wspeed.equals("40000") && wchannels.equals("2")) {
				stats.setCount("64131",1);
			} else if (wspeed.equals("80000") && wchannels.equals("2")) {
				stats.setCount("64132",1);
			}
		}
	}
	*/

	Vector checkPostPlaylist(HttpPost poster,scanpage sp, Vector vec) {
		if (sp.req.getMethod().equals("POST")) {
			if (poster.checkPostMultiParameter("only")) {
				String line="";
				Vector only=poster.getPostMultiParameter("only");
				for (Enumeration e=only.elements();e.hasMoreElements();) {
					if (!line.equals("")) {
						line+=","+(String)e.nextElement();
					} else {
						line+=(String)e.nextElement();
					}
				}
				vec.addElement("o("+line+")");
			}
		}
		return(vec);
	}

/*
	String getBestMirrorUrl(scanpage sp, String url) {		
		StringTagger tagger=new StringTagger(url);
		String file=tagger.Value("F");
		String besthost="";
		int bestscore=0;
		for (int i=1;i<7;i++) {
			String host=tagger.Value("H"+i);
			if (host!=null && !host.equals("")) {
				int tmpscore=0;
				if (host.indexOf("omroep")!=-1) {
					tmpscore+=100;
				} else if (host.indexOf("vpro")!=-1) {
					tmpscore+=50;
				}
			
				if (tmpscore>bestscore) {
					bestscore=tmpscore;
					besthost=host;
				}
			}
		}	
		// debug("RA FILE="+file+" BEST HOST="+besthost+" BEST SCORE="+bestscore);
		return(besthost+file);
	}
	*/

    private boolean isInternal( scanpage sp )
    {
        boolean intern  = false;
        String  ip      = sp.req.getRemoteAddr();

        // computers within vpro domain, use *.vpro.nl as server, instead *.omroep.nl
        // do we come from proxy?
        // ----------------------

        if( ip != null && !ip.equals(""))
        {
            if( ip.indexOf("vpro6d.vpro.nl")!= -1  || ip.indexOf("145.58.172.6")!= -1 )
            {
                // positive on proxy, get real ip
                // ------------------------------
                ip = sp.req.getHeader("X-Forwarded-For");
                // come from intern?
                if( ip != null && !ip.equals("") && ip.indexOf("145.58") != -1 )
                    intern = true;
            }
            else
            if( ip.indexOf("145.58") != -1 )
                intern = true;
        }
        return intern;
    }

	private String getAddress( scanpage sp )
	{
		String 	result 		= null;
		boolean	fromProxy 	= false;

		// get address 
		// -----------

		String addr = sp.req.getRemoteHost();

		if( addr != null && !addr.equals("") )
		{
			// from proxy ?
			// ------------

			if( addr.indexOf("vpro6d.vpro.nl") != -1 || addr.indexOf("145.58.172.6") != -1 )
			{
				// get real address 
				// ----------------

				fromProxy = true;
				addr = sp.req.getHeader("X-Forwarded-For");
				if( addr != null && !addr.equals("") )
					result = addr;
			}
			else
				result = addr;
		}

		result = getHostNames( addr );		
		if( fromProxy )
			result = "zen.vpro.nl->" + result;
	
		return result;
	}

	private String getHostNames( String host )
	{
		String result 	= null;
		String hn 		= null;

		if( host.indexOf(",") != -1 )
		{
			int pos;
			while( (pos = host.indexOf(",")) != -1 )
			{
				hn = host.substring( 0, pos );
				host = host.substring( pos + 2 );
				if( result == null )
					result  = getHostName( hn );
				else
					result += "->" + getHostName( hn );
			}		
		}
		else
			result = getHostName( host );	
	
		return result;
	}
		
	private String getHostName( String hostname )
	{
		String hn = null;
		try
		{
			hn = InetAddress.getByName( hostname ).getHostName();
		}
		catch( UnknownHostException e )
		{
			hn = hostname;
		}
		return hn;
	}	


    String getBestMirrorUrl(scanpage sp, String url) {
        String besthost = null;
        StringTagger tagger = new StringTagger(url);
        String       file   = tagger.Value("F");

        if( url != null && !url.equals(""))
        {
            boolean found   = false;
            if( isInternal(sp) )
            {
                String  host    = null;
                for( int i=0; i<7 && !found; i++)
                {
                    host=tagger.Value("H"+i);
                    // get host *.vpro.nl
                    // ------------------
                    if( host != null && !host.equals("") && host.indexOf("vpro") != -1 )
                    {
                        besthost = host;
                        found = true;
                    }
                }
                if( besthost == null )
                {
                    // no host for this part found in vpro domain, use other
                    // -----------------------------------------------------

                    debug("getBestMirrorUrl(): ERROR: Could not determine a valid host in vpro-domain, using another!");
                    found = false;  // extra secure
                }
            }
            //else  // hack hack
            if( !found )
            {
                int bestscore=0;
                for (int i=1;i<7;i++) {
                    String host=tagger.Value("H"+i);
                    if (host!=null && !host.equals("")) {
                        int tmpscore=0;
                        if (host.indexOf("omroep")!=-1) {
                            tmpscore+=100;
                        } else if (host.indexOf("vpro")!=-1) {
                            tmpscore+=50;
                        }

                        if (tmpscore>bestscore) {
                            bestscore=tmpscore;
                            besthost=host;
                        }
                    }
                }
            }
        }
            //debug("RA FILE="+file+" BEST HOST="+besthost+" BEST SCORE="+bestscore);
        if( besthost == null || besthost.equals(""))
        {
            debug("getBestMirrorUrl(): ERROR: No host could be found, using station.vpro.nl as default!");
            besthost = "station.vpro.nl";
        }
        return(besthost+file);
    }

	private void debug( String msg )
	{
		if (debug) System.out.println( classname + ":" + msg );
	}


	private void checkImgMirror(HttpServletRequest req) {
		String host=req.getRemoteHost();
		if (host!=null && (host.equals("sneezy.omroep.nl") || host.equals("images.vpro.nl")) && mmbase!=null) {
			debug("SERVDB22->"+req.getQueryString());
			NetFileSrv bul=(NetFileSrv)mmbase.getMMObject("netfilesrv");
			if (bul!=null) {
				debug("SERVDB2->"+req.getQueryString());
				bul.fileChange("images","main","/img.db:"+req.getQueryString()+".asis");
			}
		}
	}



	/**
	 * Write String to the current client socket
	 *
	 * @param String line to be writen to the client
	 * @return 0 if done, -1 if a error has accured
	 */
	public int writeline(HttpServletResponse res,String line) {
		int len=0;
		len=line.length();
		byte templine[]=new byte[len];
		line.getBytes(0,len,templine,0);
		try {
			res.getOutputStream().write(templine,0,len);
			// added a flush why is this not done  ??? daniel 15 Okt, 1998
			res.getOutputStream().flush();
		} catch(Exception e) {
			return(-1);
		}
		return(0);
		/*
		try {
			(new PrintStream(clientsocket.getOutputStream(),true)).print(line);
		} catch (IOException e) {
			debug("worker -> Write error in worker");
		}
		return(0);
		*/
	}
}
