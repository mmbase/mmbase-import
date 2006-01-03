/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.mmbob.util.transformers;


import org.mmbase.util.logging.*;
import org.mmbase.util.*;
import org.mmbase.util.transformers.*;

/**
 */
public class BBCode {
    private static Logger log = Logging.getLoggerInstance(BBCode.class);

    public static String decode(String body) {
        StringObject obj=new StringObject(body);
	obj.replace("[/QUOTE]","</quote>");
	obj.replace("[b]","<b>");
	obj.replace("[/b]","</b>");
	obj.replace("[i]","<i>");
	obj.replace("[/i]","</i>");
	obj.replace("[u]","<u>");
	obj.replace("[/u]","</u>");
	body = obj.toString();
	if (body.indexOf("[list")!=-1) body = decodeList(body);
	if (body.indexOf("[color=")!=-1) body = decodeColor(body);
	if (body.indexOf("[size=")!=-1) body = decodeSize(body);
	if (body.indexOf("[email]")!=-1) body = decodeEmail(body);
	if (body.indexOf("[img]")!=-1) body = decodeImage(body);
	if (body.indexOf("[url]")!=-1) body = decodeUrl(body);
	return body;
    }

    private static String decodeList(String body) {
	int pos = body.indexOf("[list");
	int endpos = body.indexOf("[/list]");
	while (pos!=-1 && endpos!=-1) {
		String newbody = body.substring(0,pos);
		if (body.charAt(pos+5)==']') {
			newbody+="<ul>";
			String tmp = body.substring(pos+6,endpos);
        		StringObject obj=new StringObject(tmp);
			obj.replace("[*]","<li>");
			newbody+=obj.toString()+"</ul>"+body.substring(endpos+7);
		} else if (body.charAt(pos+6)=='a') {
			newbody+="<ol type=\"a\">";
			String tmp = body.substring(pos+8,endpos);
        		StringObject obj=new StringObject(tmp);
			obj.replace("[*]","<li>");
			newbody+=obj.toString()+"</ol>"+body.substring(endpos+7);
		} else if (body.charAt(pos+6)=='1') {
			newbody+="<ol type=\"1\">";
			String tmp = body.substring(pos+8,endpos);
        		StringObject obj=new StringObject(tmp);
			obj.replace("[*]","<li>");
			newbody+=obj.toString()+"</ol>"+body.substring(endpos+7);
		}
		body = newbody;
		pos = body.indexOf("[list");
		endpos = body.indexOf("[/list]");
	}	
	return body;
    }


    private static String decodeColor(String body) {
	int pos = body.indexOf("[color=");
	int endpos = body.indexOf("[/color]");
	while (pos!=-1 && endpos!=-1) {
		String newbody = body.substring(0,pos);
		int colorendpos=body.indexOf(']',pos);
		if (colorendpos!=-1) {
			String cs = body.substring(pos+7,colorendpos);
			newbody+="<font color=\""+cs+"\">";
			String tmp = body.substring(colorendpos+1,endpos);
			newbody+=tmp+"</font>"+body.substring(endpos+8);
		}
		body = newbody;
		pos = body.indexOf("[color=");
		endpos = body.indexOf("[/color]");
	}	
	return body;
    }


    private static String decodeSize(String body) {
	int pos = body.indexOf("[size=");
	int endpos = body.indexOf("[/size]");
	while (pos!=-1 && endpos!=-1) {
		String newbody = body.substring(0,pos);
		int sizeendpos=body.indexOf(']',pos);
		if (sizeendpos!=-1) {
			String cs = body.substring(pos+7,sizeendpos);
			newbody+="<font size=\""+cs+"\">";
			String tmp = body.substring(sizeendpos+1,endpos);
			newbody+=tmp+"</font>"+body.substring(endpos+7);
		}
		body = newbody;
		pos = body.indexOf("[size=");
		endpos = body.indexOf("[/size]");
	}	
	return body;
    }


    private static String decodeEmail(String body) {
	int pos = body.indexOf("[email]");
	int endpos = body.indexOf("[/email]");
	while (pos!=-1 && endpos!=-1) {
		String newbody = body.substring(0,pos);
		if (pos!=-1) {
			String tmp = body.substring(pos+7,endpos);
			newbody+="<a href=\"mailto:"+tmp+"\">"+tmp+"</a>";
			newbody+=body.substring(endpos+8);
		}
		body = newbody;
		pos = body.indexOf("[email]");
		endpos = body.indexOf("[/email]");
	}	
	return body;
    }


    private static String decodeImage(String body) {
	int pos = body.indexOf("[img]");
	int endpos = body.indexOf("[/img]");
	while (pos!=-1 && endpos!=-1) {
		String newbody = body.substring(0,pos);
		if (pos!=-1) {
			String tmp = body.substring(pos+5,endpos);
			newbody+="<a href=\""+tmp+"\" target=\""+tmp+"\"><img src=\""+tmp+"\" width=\"120\"></a>";
			newbody+=body.substring(endpos+6);
		}
		body = newbody;
		pos = body.indexOf("[img]");
		endpos = body.indexOf("[/img]");
	}	
	return body;
    }


    private static String decodeUrl(String body) {
	int pos = body.indexOf("[url]");
	int endpos = body.indexOf("[/url]");
	while (pos!=-1 && endpos!=-1) {
		String newbody = body.substring(0,pos);
		if (pos!=-1) {
			String tmp = body.substring(pos+5,endpos);
			if (tmp.indexOf("thread.jsp")==-1) {
				newbody+="<a href=\""+tmp+"\" target=\""+tmp+"\">"+tmp+"</a>";
			} else {
				newbody+="<a href=\""+tmp+"\">"+tmp+"</a>";
			}
			newbody+=body.substring(endpos+6);
		}
		body = newbody;
		pos = body.indexOf("[url]");
		endpos = body.indexOf("[/url]");
	}	
	return body;
    }

}
