/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.servlet;
 
// import the needed packages
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.mmbase.util.XMLBuilderReader;
import org.mmbase.util.XMLScreenWriter;
import org.mmbase.util.XMLChecker;
import org.apache.xerces.*;
// Dumped all xalan/XSLT stuff for the time being
//import org.apache.xalan.*;
//import org.apache.xalan.xslt.*;

/**
 * Performance Servlet is used for 2 reasons as a basic Servlet test to see if
 * the install went oke (same as SimpleServlet) and to see how fast the JVM is
 * we are running on (very basic test).
 */
public class mmconfig extends JamesServlet {

    protected static String active_color = "#00CC00";
    protected static String inactive_color = "#CC0000";

    /**
     * Implement a FilenameFilter for xml files
     */
    public class XMLFilenameFilter implements FilenameFilter {
	public boolean accept(File directory, String name) {
	    if (name.endsWith(".xml")) {
		return true;
	    } else {
		return false;
	    }
	}
    }

    /**
     * Put pathinfo data and operations together.
     *
     */
    public class PathInfo {
	private String[] pathElements;
	public PathInfo(String pathinfo) {
	    if (pathinfo == null) {
		pathElements = new String[0]; // empty array
	    } else {
		if (pathinfo.startsWith("/")) {
		    pathinfo = pathinfo.substring(1,pathinfo.length());
		}
		StringTokenizer st = new StringTokenizer(pathinfo,"/",false);
		pathElements = new String[st.countTokens()];
		int i=0;
		while (st.hasMoreTokens()) {
		    pathElements[i++] = (String)st.nextElement();
		}
	    }    
	}
	
	public boolean hasType() {
	    return pathElements.length > 0;
	}
	
	public String getType() {
	    return pathElements[0];
	}
	
	public boolean hasCommand() {
	    return pathElements.length > 1;
	}

	public String getCommand() {
	    return pathElements[1];
	}

	public boolean hasArgument() {
	    return pathElements.length > 2;
	}

	public String getArgument() {
	    return pathElements[2];
	}
    }
	
	public void init() {
	}

	/** 
	 * reload
	 */
	public void reload() {
	}

    

	/**
 	* service call will be called by the server when a request is done
	* by a user.
 	*/
	public synchronized void service(HttpServletRequest req, HttpServletResponse res) throws ServletException,IOException
	{	
		// Open	a output stream so you can write to the client
		PrintStream out = new PrintStream(res.getOutputStream());
		String requestURI = req.getRequestURI();
		if (requestURI.endsWith("/")) {
		    requestURI = requestURI.substring(0,requestURI.length()-1);
		}
		String[] dirlist;

		String configpath = System.getProperty("mmbase.config");
		if (configpath.endsWith(File.separator)) {
		    configpath = configpath.substring(0,configpath.length()-1);
		}

		// Set the content type of this request
		res.setContentType("text/html");

		PathInfo pathInfo = new PathInfo(req.getPathInfo());

		// Write header to client
		//res.writeHeaders();

		// WRITE MESSAGE TO CLIENT 
		out.println("<HTML>");
		out.println("<HEAD><TITLE>MMBase Configuration</TITLE></HEAD>");
		out.println("<BODY BGCOLOR=\"#FFFFFF\">");

		if (!pathInfo.hasType()) {
		    // Toplevel here: give option of database, modules or builders
		    out.println("<ul>\n"+
				"<li><a href=\""+requestURI+"/databases\">databases</a>\n"+
				"<li><a href=\""+requestURI+"/modules\">modules</a>\n"+
				"<li><a href=\""+requestURI+"/builders\">builders</a>\n"+
				"</ul>\n"
				);
		} else if (pathInfo.getType().equalsIgnoreCase("builders")) {

		    String dirpath = configpath+File.separator+"builders";

		    if (pathInfo.hasCommand()) {
			if (pathInfo.getCommand().equalsIgnoreCase("show")) {
			    if (pathInfo.hasArgument()) {
				String showfilepath = dirpath+File.separator+pathInfo.getArgument();
				//displayBuilder(out,showfilepath);
				prettyPrintXML(out,showfilepath);
			    } else {
				out.println("boom, must have argument to show");
			    }
			} else if (pathInfo.getCommand().equalsIgnoreCase("check")) {
			    if (pathInfo.hasArgument()) {
				String showfilepath = dirpath+File.separator+pathInfo.getArgument();
				//displayBuilder(out,showfilepath);
				checkXML(out,showfilepath);
			    } else {
				out.println("boom, must have argument to show");
			    }
			} else {
			    out.println("only show command has been implemented");
			}
			
		    } else {
			try {
			    dirlist = listDirectory(dirpath);
			    out.println("<b>Builders:</b><br>\n<table cellspacing=0 cellpadding=0 border=1>\n");
			    for (int i=0;i<dirlist.length;i++) {
				out.println("  <tr>\n");
				String filename = dirpath+File.separator+dirlist[i];
				out.println("    <td><a href=\""+requestURI+"/show/"+dirlist[i]+"\">"+dirlist[i]+"</a></td>\n");
				out.println("    <td><a href=\""+requestURI+"/check/"+dirlist[i]+"\"><font size=-1>check</font></a></td>\n");
				out.println("  </tr>\n");
			    }
			    out.println("</table>\n");
			} catch (IOException e) {
			    out.println("IOexception: "+e.getMessage());
			}
		    }
		} else if (pathInfo.getType().equalsIgnoreCase("databases")) {

		    String dirpath = configpath+File.separator+"databases";

		    if (pathInfo.hasCommand()) {
			if (pathInfo.getCommand().equalsIgnoreCase("show")) {
			    if (pathInfo.hasArgument()) {
				String showfilepath = dirpath+File.separator+pathInfo.getArgument();
				//displayBuilder(out,showfilepath);
				prettyPrintXML(out,showfilepath);
			    } else {
				out.println("boom, must have argument to show");
			    }
			} else if (pathInfo.getCommand().equalsIgnoreCase("check")) {
			    if (pathInfo.hasArgument()) {
				String showfilepath = dirpath+File.separator+pathInfo.getArgument();
				//displayBuilder(out,showfilepath);
				checkXML(out,showfilepath);
			    } else {
				out.println("boom, must have argument to show");
			    }
			} else {
			    out.println("only show and check commands has been implemented");
			}
			
		    } else {
			try {
			    dirlist = listDirectory(dirpath);
			    out.println("<b>Supported databases:</b><br>\n<table cellspacing=0 cellpadding=0 border=1>\n");
			    for (int i=0;i<dirlist.length;i++) {
				out.println("  <tr>\n");
				String filename = dirpath+File.separator+dirlist[i];
				out.println("    <td><a href=\""+requestURI+"/show/"+dirlist[i]+"\">"+dirlist[i]+"</a></td>\n");
				out.println("    <td><a href=\""+requestURI+"/check/"+dirlist[i]+"\"><font size=-1>check</font></a></td>\n");
				out.println("  </tr>\n");
			    }
			    out.println("</table>\n");
			} catch (IOException e) {
			    out.println("IOexception: "+e.getMessage());
			}
		    }
		} else if (pathInfo.getType().equalsIgnoreCase("modules")) {

		    String dirpath = configpath+File.separator+"modules";

		    if (pathInfo.hasCommand()) {
			if (pathInfo.getCommand().equalsIgnoreCase("show")) {
			    if (pathInfo.hasArgument()) {
				String showfilepath = dirpath+File.separator+pathInfo.getArgument();
				//displayBuilder(out,showfilepath);
				prettyPrintXML(out,showfilepath);
			    } else {
				out.println("boom, must have argument to show");
			    }
			} else if (pathInfo.getCommand().equalsIgnoreCase("check")) {
			    if (pathInfo.hasArgument()) {
				String showfilepath = dirpath+File.separator+pathInfo.getArgument();
				//displayBuilder(out,showfilepath);
				checkXML(out,showfilepath);
			    } else {
				out.println("boom, must have argument to show");
			    }
			} else {
			    out.println("only show command has been implemented");
			}
		    } else {
			try {
			    dirlist = listDirectory(dirpath);
			    out.println("<b>Modules:</b><br>\n<table cellspacing=0 cellpadding=0 border=1>\n");
			    for (int i=0;i<dirlist.length;i++) {
				out.println("  <tr>\n");
				String filename = dirpath+File.separator+dirlist[i];
				out.println("    <td><a href=\""+requestURI+"/show/"+dirlist[i]+"\">"+dirlist[i]+"</a></td>\n");
				out.println("    <td><a href=\""+requestURI+"/check/"+dirlist[i]+"\"><font size=-1>check</font></a></td>\n");
				out.println("  </tr>\n");
			    }
			    out.println("</table>\n");
			} catch (IOException e) {
			    out.println("IOexception: "+e.getMessage());
			}
		    }
		} else {
		    out.println("this alternative not yet implemented");
		}
		
		out.println("</BODY>");
		out.println("</HTML>");

	}
	
	private void stub(){
	}

    /**
     * Retrieve all xml files in a directory
     * 
     * @param path Directory path
     * @return String array containing the names of the xml files in the directory
     *
     */
    protected String[] listDirectory(String path) throws IOException {
	File dir = new File(path);
	if (!dir.isDirectory()) {
	    throw new IOException("Path "+path+" is not a directory.\n");
	} else {
	    return dir.list(new XMLFilenameFilter());
	}
    }

    /**
     * XML validation to find configuration errors
     *
     */
    protected void checkXML(PrintStream out, String path) {
	out.println("Checking XML syntax of: <tt>"+path+"</tt><p>\n");
	XMLChecker validator = new XMLChecker(out);
	validator.validateAndReport(path);
    }
    
    /*
     * @param out Stream to http result
     * @param path Path to the builder file
     */
    /**
       cjr: dumped xalan/XSLT stuff for the time being
    protected void displayBuilder(PrintStream out,String path) {
	//displayAfterXSLTTransformation(out,path,"/opt/mmbase/builder-display.xsl");

    }
    */

    /**
     * Do XSLT transformation of xml file and print result to stream.
     *
     * @param out Stream to write output to
     * @param path Path to xml input file
     * @param xsltpath Path to xslt transformation file
     */
    /* 
       cjr: dumped xalan/XSLT stuff for the time being
    protected void displayAfterXSLTTransformation(PrintStream out,String path,String xsltpath) {
	try {
	    // Use XSLTProcessorFactory to instantiate an XSLTProcessor.
	    org.apache.xalan.xslt.XSLTProcessor processor =
		org.apache.xalan.xslt.XSLTProcessorFactory.getProcessor();
                              
	    // Create the 3 objects the XSLTProcessor needs to perform the transformation.
	    org.apache.xalan.xslt.XSLTInputSource xmlSource = 
		new org.apache.xalan.xslt.XSLTInputSource (path);
	    org.apache.xalan.xslt.XSLTInputSource xslSheet = 
		new org.apache.xalan.xslt.XSLTInputSource (xsltpath);
	    org.apache.xalan.xslt.XSLTResultTarget xmlResult = 
		new org.apache.xalan.xslt.XSLTResultTarget (out);
	    
	    // Perform the transformation.
	    processor.process(xmlSource, xslSheet, xmlResult);
	} catch (org.xml.sax.SAXException e) {
	    out.println("Error doing xslt conversion of xml file: " + e.getMessage());
	}
    }
    */
 

    /*
     * @param out PrintWriter to http result
     * @param path Path to the builder file
     */
    protected void prettyPrintXML(PrintStream out,String path) {
	XMLScreenWriter screen = new XMLScreenWriter(path);
	screen.write(out);
    }

	/**
	* Info method, provides the user/server with some basic info on
	* this Servlet
 	*/
	public String getServletInfo() {
		return ("Configuration status and xml analysis tool - cjr");
	}
}
