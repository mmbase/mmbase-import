/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

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
		parser=(scanparser)getModule("SCANPARSER");
		//id=(idInterface)getModule("ID");
		sessions=(sessionsInterface)getModule("SESSION");
		// org.mmbase stats=(StatisticsInterface)getModule("STATS");

		// Set the servletcontext in class MMBaseContext sothat it can be requested by everyone using
		// static method MMBaseContext.getServletContext() added on 23 December 1999 by daniel & davzev.
		// Removed on 24 - feb -2000 by Wilbert, done by super.init()
		// ServletConfig sc=getServletConfig();
		// ServletContext sx=sc.getServletContext();
		// MMBaseContext.setServletContext(sx);
	}
	
	/**
	 * Adds DEFAULT_CHARSET to mimetype given by SHTML_CONTENTTYPE for handling
	 * of the charset used by the database
	 */
	private String addCharSet( String mimetype ) {
		if (mimetype.equals(SHTML_CONTENTTYPE)) 
			return mimetype+"; "+DEFAULT_CHARSET; 
		return mimetype;
	}
	
	/**
	 * handle_line is called by service to parse the SHTML in body.
	 * It can be used by children to do their own parsing. The default
	 * implementation calls parser.handle_line (from module scanparser)
	 * to do the parsing.
	 */
	protected String handle_line(String body, sessionInfo session, scanpage sp) throws ParseException {
		return parser.handle_line(body, session, sp);
	}

	/**
	 * Servlet request service.
	 * This processes the the page and sends it back
	 */
	public void service(HttpServletRequest req,HttpServletResponse res) throws ServletException, IOException {
		incRefCount(req);
		try {
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
	
			String sname=getCookie(sp.req,res);
			sp.sname=sname;
			sessionInfo session=sessions.getSession(sp,sname);
			sp.session=session;
	
			doEditorReload(sp,res);
	
			// POST
			if (req.getMethod().equals("POST")) {
				handlePost(sp,res);
			}
	
			// Generate page
			PrintWriter out=res.getWriter();
			do {
				sp.rstatus=0;
				sp.body=parser.getfile(sp.req_line);
				doSecure(sp,res); // name=doSecure(sp,res); but name not used here
				long stime=handleTime(sp);
	
				if (handleCache(sp,res,out)) return;
	
				if (sp.body!=null && !sp.body.equals("")) {
					long oldtime = System.currentTimeMillis();
					// Process HTML
					sp.body = handle_line(sp.body, session, sp);
					// Send data back
					if (sp.body!=null) {
						if (sp.rstatus==0) {
							res.setContentType( addCharSet( mimetype ) ); 
							out.print(sp.body);
							handleCacheSave(sp,res);
						} else if (sp.rstatus==1) {
							res.setStatus(302,"OK");
							res.setContentType( addCharSet( mimetype ) ); 
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
								res.setContentType( addCharSet(mimetype) ); 
							} else {
								setHeaders(sp,res,sp.body.length());
								out.print(sp.body);
							}
						}
					} else {
						sp.body="<TITLE>Servscan</TITLE>handle_line returned null<BR>";
						setHeaders(sp,res,sp.body.length());
						out.print(sp.body);
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
			
			out.flush();
			out.close();
		} catch(NotLoggedInException e) {
			String page = null;
			if( req	!= null ) {
				page = req.getRequestURI();
				if( req.getQueryString()!=null ) 
					page += "?" + req.getQueryString();
			}
			debug( "service(): page("+page+"): NotLoggedInException: " + e );
		} catch(Exception a) {
			String page = null;
            if( req != null ) {
                page = req.getRequestURI();
                if( req.getQueryString()!=null )
                    page += "?" + req.getQueryString();
            }
			debug( "service(): page("+page+"): Exception on page: " + req.getRequestURI() );
			a.printStackTrace();
		} finally { decRefCount(req); }
	}// service

	private final void setHeaders(scanpage sp,HttpServletResponse res,int len) {
		res.setContentType( addCharSet(sp.mimetype) );
		// res.setContentLength(len);
		// org.mmbase String ac=sp.req.getAcceptor();
		String ac="";
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
					if( debug ) debug("handlePost(): Secure tag found in page("+sp.getUrl()+"), displaying username/password window at client-side.");
					name=getAuthorization(req,res);
					if( debug ) debug("handlePost(): Secure tag found in page("+sp.getUrl()+"), displaying username/password window at client-side.");
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
					if (part.indexOf("SESSION-")==0) {
						if (sp.session!=null) {
							if (poster.checkPostMultiParameter((String)obj)) {
								// MULTIPLE SUPPORT
								sessions.addSetValues(sp.session,part.substring(8),poster.getPostMultiParameter((String)obj));
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
					out.print(rst);
					out.flush();
					out.close();
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
					out.print(rst);
					out.flush();
					out.close();
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

			if( debug ) debug("doSecure("+sp.getUrl()+"): Secure tag found, calling getAuthorization()...");
			name=getAuthorization(sp.req,res);
			if( debug ) debug("doSecure("+sp.getUrl()+"): getting cookie from user...");
			String sname=getCookie(sp.req,res);

			// check name
			// ----------

			if (name==null) { 
				if( debug ) debug("doSecure("+sp.getUrl()+"): WARNING: no username found!");
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
