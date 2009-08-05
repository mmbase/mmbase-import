/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.applications.email;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;
import javax.naming.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * @author Daniel Ockeloen
 * 
 */
public class MimeBodyTag {

	private String type="text/plain";
	private String text="";
	private String id="default";
	private String related;
	private String alt;
	private String formatter;
	private String attachmentid;
	private String filepath;
	private String filename;
        private Vector altnodes;
        private MimeMultipart relatednodes;

    	// logger    
    	private static Logger log = Logging.getLoggerInstance(MimeBodyTag.class.getName());


	public void setFormatter(String formatter) {
		this.formatter=formatter;
	}

	public void addAlt(MimeBodyTag sub) {
		if (altnodes==null) {
			//altnodes=new MimeMultipart("alternative");
			//altnodes.addBodyPart(getMimeBodyPart());
			altnodes=new Vector();
		}
		//altnodes.addBodyPart(sub.getMimeBodyPart());
		altnodes.addElement(sub);
	}


	public void addRelated(MimeBodyTag sub) {
		try {
		if (relatednodes==null) {
			relatednodes=new MimeMultipart("related");
			relatednodes.addBodyPart(getMimeBodyPart());
		}
			relatednodes.addBodyPart(sub.getMimeBodyPart());
		} catch(Exception e) {
			log.error("Can't add related node");
		}
	}

	public void setAlt(String alt) {
		this.alt=alt;
	}

	public void setRelated(String related) {
		this.related=related;
	}

	public void setId(String id) {
		this.id=id;
	}

	public void setFile(String filepath) {
		this.filepath=filepath;
	}

	public void setFileName(String filename) {
		this.filename=filename;
	}

	public String getFileName() {
		if (filename==null) {
			// needs to be better, create a guessed name on getFile
			return "unknown";
		}
		return filename;
	}

	public void setAttachment(String attachmentid) {
		this.attachmentid=attachmentid;
	}

	public String getFormatter() {
		return formatter;
	}

	public String getFile() {
		return filepath;
	}

	public String getType() {
		return type;
	}

	public String getId() {
		return id;
	}

	public String getRelated() {
		return related;
	}

	public String getAlt() {
		return alt;
	}

	public void setType(String type) {
		this.type=type;
	}

	public void setText(String text) {
		this.text=text;
	}

	public String getText() {
		// is there a formatter requested ??
		if (formatter!=null) {
			if (formatter.equals("html2plain")) {
				return html2plain(text);
			}
		} 
		return text;
	}


	/**
	* convert 'html' to 'plain' text
	* this removes the br and p tags and converts them
	* to returns and dubble returns for email use.
	*/
	private static String html2plain(String input) {
		// define the result string
		String result="";

		// setup a tokenizer on all returns and linefeeds so
		// we can remove them
        	StringTokenizer tok = new StringTokenizer(input,"\n\r");
	        while (tok.hasMoreTokens()) {
			// add the content part stripped of its return/linefeed
            		result+=tok.nextToken();
		}

		// now use the html br and p tags to insert
		// the wanted returns 
            	StringObject obj=new StringObject(result);
                obj.replace("<br/>","\n");
                obj.replace("<br />","\n");
                obj.replace("<BR/>","\n");
                obj.replace("<BR />","\n");
                obj.replace("<br>","\n");
                obj.replace("<BR>","\n");
                obj.replace("<p>","\n\n");
                obj.replace("<p/>","\n\n");
                obj.replace("<p />","\n\n");
                obj.replace("<P>","\n\n");
		result=obj.toString();

		// return the coverted body
		return result;
	}


	public MimeMultipart getMimeMultipart() {
		try {
		if (altnodes!=null) {
			MimeMultipart result=new MimeMultipart("alternative");

			MimeMultipart r=getRelatedpart();
			if (r==null) {
				result.addBodyPart(getMimeBodyPart());
			} else {
				MimeBodyPart wrapper=new MimeBodyPart();
				wrapper.setContent(r);
				result.addBodyPart(wrapper);
			}

			Enumeration e=altnodes.elements();
			while (e.hasMoreElements()) {
				MimeBodyTag t=(MimeBodyTag)e.nextElement();
			
				r=t.getRelatedpart();
				if (r==null) {
					result.addBodyPart(t.getMimeBodyPart());
				} else {
					MimeBodyPart wrapper=new MimeBodyPart();
					wrapper.setContent(r);
					result.addBodyPart(wrapper);
				}
			
			}
			return result;
		}
		if (relatednodes!=null) return relatednodes;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public MimeMultipart getRelatedpart() {
		return relatednodes;
	}
	
	public MimeBodyPart getMimeBodyPart() {
		MimeBodyPart mmbp=new MimeBodyPart();
		try {
		DataHandler d=null;
		if (type.equals("text/plain")) { 
			d=new DataHandler(text,type);
			mmbp.setDataHandler(d);
		} else if (type.equals("text/html")) { 
			d=new DataHandler(text,type);
			mmbp.setDataHandler(d);
		} else if (type.equals("application/octet-stream")) { 

			String filepath=MMBaseContext.getHtmlRoot()+File.separator+getFile();
			if (filepath.indexOf("..")==-1 && filepath.indexOf("WEB-INF")==-1) {
				FileDataSource fds=new FileDataSource(filepath);
				d=new DataHandler(fds);
				mmbp.setDataHandler(d);
				mmbp.setFileName(getFileName());
			} else {
				log.error("file from there not allowed");
			}
		} else if (type.equals("image/gif") || type.equals("image/jpeg")) { 

			String filepath=MMBaseContext.getHtmlRoot()+File.separator+getFile();
			if (filepath.indexOf("..")==-1 && filepath.indexOf("WEB-INF")==-1) {
				FileDataSource fds=new FileDataSource(filepath);
				d=new DataHandler(fds);
				mmbp.setDataHandler(d);
				mmbp.setHeader("Content-ID","<"+id+">");
				mmbp.setHeader("Content-Disposition","inline");
			} else {
				log.error("file from there not allowed");
			}
		}

		} catch(Exception e){
			log.error("Can't add DataHandler");
		}
		
		return mmbp;
        }
}
