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
import java.awt.*;
import java.awt.Image;
import javax.servlet.*;
import javax.servlet.http.*;


import org.mmbase.util.*;
import org.mmbase.module.*;
import org.mmbase.module.core.*;
import org.mmbase.module.gui.html.*;


/**
 * servscan is the 'extened html language' (parsing *.shtml) provided for MMbase its a system like
 * php3, asp or jsp but has its roots providing a simpler toolset for Interaction
 * designers and gfx designers its provides as a option but not demanded you can
 * also use the provides jsp for a more traditional parser system.
 * 
 * @version 8 Jan 1997
 * @author Daniel Ockeloen
 * @author Rico Jansen
 * @author Jan van Oosterom
 * @author Rob Vermeulen
 * @see vpro.james.modules.ProcessorInterface
 */
public class servscan extends JamesServlet {

	private String classname = getClass().getName();

	// modules used in servscan
	private static ProcessorModule grab=null;
	private static pagesInterface pages=null;
	private static areasInterface areas=null;
	private static sessionsInterface sessions=null;
	// private static idInterface id=null;
	// private static StatisticsInterface stats=null;

	private static String loadmode="no-cache";
	private static String htmlroot;

	//int lastlistitem=0;
	private static String  fileroot;
	private static boolean debug=false;
	private scanparser parser;


	// Davzev added on 27-10-1999: Defining constants    
	public static final String SHTML_CONTENTTYPE = "text/html";
 	public static final String DEFAULT_CHARSET = "iso-8859-1"; 

	/**
	 * Init the servscan, this is needed because it was created using
	 * a newInstanceOf().
	 *
	 * @param int worker id
	 */
	public void init() {
		super.init();
		//Roots=getRoots();
		// org.mmbase start();
		pages=(pagesInterface)getModule("PAGE");
		//areas=(areasInterface)getModule("AREA");
		parser=(scanparser)getModule("SCANPARSER");
		//id=(idInterface)getModule("ID");
		sessions=(sessionsInterface)getModule("SESSION");
		// org.mmbase stats=(StatisticsInterface)getModule("STATS");
	}

	/**
	 * Servlet request service.
	 * This processes the the page and sends it back
	 */
	public void service(HttpServletRequest req,HttpServletResponse res) throws ServletException, IOException {

		try {
		int len,qw_pos;
		Object obj;
		PrintWriter out=null;
		StringTokenizer tok;
		String rtn,part,part2,finals,tokje,header;
		sessionInfo session=null;
		String name=null;
		scanpage sp=new scanpage();
		sp.req_line=req.getRequestURI();
		sp.querystring=req.getQueryString();

		// needs to be replaced (get the context ones)		
		ServletConfig sc=getServletConfig();
		ServletContext sx=sc.getServletContext();
		String mimetype=sx.getMimeType(sp.req_line);
		if (mimetype==null) mimetype="text/html";
		sp.mimetype=mimetype;

		
		sp.setReq(req);

		// build env.
		Hashtable requestEnv = new Hashtable();


		String sname=getCookie(sp.req,res);
		sp.sname=sname;
		session=sessions.getSession(sp,sname);
		sp.session=session;

		doEditorReload(sp,res);

		out=res.getWriter();

		// POST
		if (req.getMethod().equals("POST")) {
			handlePost(sp,res);
		}

		// Generate page
		do {
			sp.rstatus=0;

			sp.body=parser.getfile(sp.req_line);

			name=doSecure(sp,res);

			long stime=handleTime(sp);

			if (handleCache(sp,res,out)) return;


			if (sp.body!=null && !sp.body.equals("")) {
					// Process HTML
					long oldtime = System.currentTimeMillis();
					sp.body=parser.handle_line(sp.body,session,sp);

					// Send data back
					if (sp.body!=null) {
						if (sp.rstatus==0) {
							if (mimetype.equals(SHTML_CONTENTTYPE)) {
						    	res.setContentType(mimetype+"; "+DEFAULT_CHARSET); 
							} else {
								res.setContentType(mimetype);
							}

							out.println(sp.body);
							handleCacheSave(sp,res);
						} else if (sp.rstatus==1) {
							res.setStatus(302,"OK");
							if (mimetype.equals(SHTML_CONTENTTYPE)) {
						    	res.setContentType(mimetype+"; "+DEFAULT_CHARSET); 
							} else {
								res.setContentType(mimetype);
							}
							res.setHeader("Location",sp.body);
						} else if (sp.rstatus==2) {
							sp.req_line=sp.body;	
							if (sp.req_line.indexOf('\n')!=-1) {
								sp.req_line=sp.req_line.substring(0,sp.req_line.indexOf('\n'));
							}
						} else if (sp.rstatus==4) {
							String tmp = req.getHeader("If-Modified-Since:");
							if (tmp!=null && sp.processor!=null) {
								res.setStatus(304,"Not Modified");

								if (mimetype.equals(SHTML_CONTENTTYPE)) {
						    		res.setContentType(mimetype+"; "+DEFAULT_CHARSET); 
								} else {
									res.setContentType(mimetype);
								}
							} else {
								setHeaders(sp,res,sp.body.length());
								out.println(sp.body);
							}
						}
					} else {
						sp.body="<TITLE>Servscan</TITLE>handle_line returned null<BR>";
						setHeaders(sp,res,sp.body.length());
						out.println(sp.body);
					}
				} else {
					break;
				}

				if (stime!=-1) {
					stime=System.currentTimeMillis()-stime;
					debug("service(): STIME "+stime+"ms "+(stime/1000)+"sec URI="+req.getRequestURI());
				}
			} while (sp.rstatus==2);	
			// End of page parser
		if (debug) debug("Checkpoint 7");
		out.close();
		out.flush();
		if (debug) debug("Checkpoint 8");
		} catch(Exception a) {
			a.printStackTrace();
		}
	}

	private final void setHeaders(scanpage sp,HttpServletResponse res,int len) {
		// org.mmbase String ac=sp.req.getAcceptor();
		String ac="";

		//res.setContentType("text/html; charset=iso-8859-1");
		if (sp.mimetype.equals(SHTML_CONTENTTYPE)) {
//			debug("setHeaders(): Setting contenttype to: "+sp.mimetype+"; "+DEFAULT_CHARSET); 
		   	res.setContentType(sp.mimetype+"; "+DEFAULT_CHARSET); 
		} else {
//			debug("servscan::service -> Setting contenttype to: "+sp.mimetype); 
			res.setContentType(sp.mimetype);
		}
		//res.setContentLength(len);
		if (true || ac.equals("film")) {
			Date d=new Date(); // Note this one IS correct
			Date d2=new Date(System.currentTimeMillis()-7200000);
			String dt=RFC1123.makeDate(d);
			String dt2=RFC1123.makeDate(d2);
//			debug("setHeaders(): "+ac+" time="+dt);
			res.setHeader("Expires",dt2);
			res.setHeader("Last-Modified",dt);
			res.setHeader("Date",dt);
			res.setHeader("Cache-Control","no-cache");
			res.setHeader("Pragma","no-cache");
		} else {
			Date d=new Date(0);
	//		Date d=new Date(DateSupport.currentTimeMillis()-(1000*24*3600)); // probably this one
//			debug("setHeaders(): "+ac+" time="+dt);
			String dt=RFC1123.makeDate(d);
//			res.setHeader("Expires",dt);
//			res.setHeader("Last-Modified",dt);
			res.setHeader("Date",dt);
			res.setHeader("Cache-Control","no-cache");
			res.setHeader("Pragma","no-cache");
		}
//		res.setHeader("Cache-Control","no-cache");
//		res.setHeader("Pragma","no-cache");
	}
		


	/* org.mmbase
	private final String do_reload(scanpage sp,String part)
	{
		sp.rstatus=4;
		body2=part;
		return(""); 
	}
	*/


	/**
	*/
	private final String do_cache(String part)
	{
		return(null);
	}




	public String getServletInfo() {
		return("extended html parser that adds extra html commands and a interface to modules.");
	}

	 void handlePost(scanpage  sp,HttpServletResponse res) throws Exception {
		String rtn,part,part2,finals,tokje,header;
		Hashtable proc_cmd = new Hashtable();
		Hashtable proc_var = new Hashtable();
		Object obj;
		HttpServletRequest req=sp.req;
		HttpPost poster=new HttpPost(req);
		sp.poster=poster;
		String name=null;
	
			
		// first check if it has a secure tag.
			String sec = poster.getPostParameter("SECURE");

				if (sec!=null) {
					name=getAuthorization(req,res);
					String sname=getCookie(req,res);
					if (name==null) { 
						debug("handlePost(): Warning Username is null");
						return;
					}
				}
	
				// Process method=post information 
				for (Enumeration t=poster.getPostParameters().keys();t.hasMoreElements();) {
					obj=t.nextElement();
					part=(String)obj;
					if (part.indexOf("AREA-")==0) {
						if (areas!=null) areas.setValue(part.substring(5),poster.getPostParameter((String)obj));
					} else if (part.indexOf("PAGE-")==0) {
						/*
						if (page!=null) {
							if (poster.checkPostMultiParameter((String)obj)) {
								// MULTIPLE SUPPORT
								// pages.setValue(page,part.substring(5),poster.getPostMultiParameter((String)obj));
							} else {
								pages.setValue(page,part.substring(5),poster.getPostParameter((String)obj));
							}
						}
						*/
					} else if (part.indexOf("SESSION-")==0) {
						if (sp.session!=null) {
							if (poster.checkPostMultiParameter((String)obj)) {
								// MULTIPLE SUPPORT
								// sessions.setValue(sp.session,part.substring(8),poster.getPostMultiParameter((String)obj));
							} else {
								sessions.setValue(sp.session,part.substring(8),poster.getPostParameter((String)obj));
							}
						} else {
						}
					// Personal objects
					} else if (part.indexOf("ID-")==0) {
						//SESSION HACK getAuthorization(req.getAcceptor(),"Basic");
						//aaaa name=getRemoteUser();
						//name check
						if (name==null) { 
							debug("handlePost(): Warning Username is null");
							return;
						}
						if (name!=null && name.length()>1) {
							// setUserServletProperty(part.substring(3),poster.getPostParameter((String)obj),0);

							if (poster.checkPostMultiParameter((String)obj)) {
								// MULTIPLE SUPPORT
								// id.setValue(name,part.substring(3),req.getPostMultiParameter((String)obj));
							} else {
								// id.setValue(name,part.substring(3),poster.getPostParameter((String)obj));
							}
						}
					// PRC-CMD- commands
					} else	if (part.indexOf("PRC-CMD-")==0) {
						if (poster.checkPostMultiParameter((String)obj)) {
							proc_cmd.put(part.substring(8),poster.getPostMultiParameter((String)obj));
						} else {
							proc_cmd.put(part.substring(8),poster.getPostParameter((String)obj));
						}
					// PRC-VAR- vars
					} else	if (part.indexOf("PRC-VAR-")==0) {
						if (poster.checkPostMultiParameter((String)obj)) {
							proc_var.put(part.substring(8),poster.getPostMultiParameter((String)obj));
						} else {
							proc_var.put(part.substring(8),poster.getPostParameter((String)obj));
						}
					}
				}	
				// If there are cmds process them
				if (!proc_cmd.isEmpty()) parser.do_proc_input(sp.req_line,poster,proc_var,proc_cmd,sp);
	 }


	boolean handleCacheSave(scanpage sp,HttpServletResponse res) {
		if (sp.wantCache!=null) {
			 String req_line=sp.req_line;
			 if (sp.querystring!=null) req_line+="?"+sp.querystring;
			 parser.scancache.newput(sp.wantCache,res,req_line,sp.body);
		}
		return(true);
	}

	boolean handleCache(scanpage sp,HttpServletResponse res,PrintWriter out) {
			String req_line=sp.req_line;
			if (sp.querystring!=null) req_line+="?"+sp.querystring;

			// new new new scancache setup, needs to be moved
			// ----------------------------------------------

			if (sp.body!=null && sp.body.indexOf("<CACHE HENK>")!=-1) {
				sp.wantCache="HENK";
//				debug("handleCache(): CACHE="+parser.scancache);
				String rst=parser.scancache.get(sp.wantCache,req_line);
				String pragma = sp.getHeader("Pragma");
				if (rst!=null && (pragma==null || !pragma.equals(loadmode))) {
					setHeaders(sp,res,rst.length());
					// org.mmbase res.writeHeaders();
					out.println(rst);
					out.close();
					out.flush();
					debug("handleCache(): cache.hit("+req_line+")");
					return(true);
				} else {
					debug("hanldeCache(): cache.miss("+req_line+")");
				}
			}

			if (sp.body!=null && sp.body.indexOf("<CACHE PAGE>")!=-1) {
				sp.wantCache="PAGE";
				String pragma = sp.getHeader("Pragma");
				String rst=parser.scancache.get(sp.wantCache,req_line);
				if (rst!=null && (pragma==null || !pragma.equals(loadmode))) {
					setHeaders(sp,res,rst.length());
					// org.mmbase res.writeHeaders();
					out.println(rst);
					out.close();
					out.flush();
					debug("handleCache(): cache.hit("+req_line+")");
					return(true);
				} else {
					debug("handleCache(): cache.miss("+req_line+")");
				}
			}

			return (false);
		}


	private long handleTime(scanpage sp) {
		if (sp.body!=null && sp.body.indexOf("<TIME>")!=-1) {
			return(System.currentTimeMillis());
		}
		return(-1);
	}


	private String doSecure(scanpage sp,HttpServletResponse res) throws Exception {
		String name=null;
		if (sp.body!=null && sp.body.indexOf("<SECURE>")!=-1) {
			name=getAuthorization(sp.req,res);
			String sname=getCookie(sp.req,res);

			// check name
			// ----------

			if (name==null) { 
				debug("doSecure(): Warning: Username is null");
				return(null);
			}
		}
		return(name);
	}


	void doEditorReload(scanpage sp,HttpServletResponse res) {
		if (sessions!=null) {
			String sname=getCookie(sp.req,res);
			sessionInfo session=sessions.getSession(sp,sname);


			// try to obtain and set the reload mode.
			String tmp=session.getValue("RELOAD");
			if (tmp!=null && tmp.equals("R")) {
				// oke check if it still valid
				String tmp2=session.getValue("RELOADTIME");
				if (tmp2!=null) {
					try {
						int then=Integer.parseInt(tmp2);
						int now=(int)(DateSupport.currentTimeMillis()/1000);
						if ((now-then)<300) {
							loadmode="no-cache";
						} else {
							session.setValue("RELOAD","N");
							loadmode="o-cache";
						}
					} catch(Exception e) {
						session.setValue("RELOAD","N");
						loadmode="o-cache";
					}
				} else {
					session.setValue("RELOAD","N");
					loadmode="o-cache";
				}
			} else {
				loadmode="o-cache";
			}
		}
	}

	private void debug( String msg )
	{
		if (debug) System.out.println( classname + ":" + msg );
	}
}
