/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.util;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.mmbase.module.*;

/**
 * scanpage is a container class it holds all objects needed per scan request
 * it was introduced to make servscan threadsafe but will probably in the future
 * hold all request related info instead of HttpServletRequest and HttpServletResponse
 * because we want extend the model of offline page generation.
 *
 * @author Daniel Ockeloen
 */
public class scanpage {
	public ProcessorInterface processor;
	public int rstatus=0;
	public HttpServletRequest req;
	public Vector params;
	public HttpPost poster;
	public String name=null;
	public sessionInfo session;
	public String body;
	public String req_line;
	public String wantCache=null;
	public String mimetype=null;
	public String querystring=null;
	public String sname=null;

	public void setReq(HttpServletRequest req) {
		this.req=req;
	}

	/**
	 * Get the parameter specified.
	 */
	public String getParam(int num) {
		String str;

		if (params==null) {
			params=buildparams();
		}

		try {
			str=(String)params.elementAt(num);
		} catch(IndexOutOfBoundsException e) {
			str=null;
		}
		return(str);
	}


	// Support for params
	private Vector buildparams() {
		Vector params=new Vector();
		if (querystring!=null) {
			String paramline=querystring;
			//StringTokenizer tok=new StringTokenizer(querystring,"+\n\r",true);
			int pos=paramline.indexOf("+");
			while(pos!=-1) {
				params.addElement(paramline.substring(0,pos));
				paramline=paramline.substring(pos+1);
			    pos=paramline.indexOf("+");
			}
			params.addElement(paramline);
		}
		return(params);
	}

	public boolean setParamsVector(Vector params) {
		this.params=params;
		return(true);
	}

	public Vector getParamsVector() {
		if (params==null) params=buildparams();

		if (params.size()==0) return(null);

		return(params);
	}


	public boolean setParamsLine(String paramline) {
		this.params=new Vector();
		//StringTokenizer tok=new StringTokenizer(paramline,"+\n\r");
		// rico 
		int pos=paramline.indexOf("+");
		while(pos!=-1) {
			params.addElement(paramline.substring(0,pos));
			paramline=paramline.substring(pos+1);
		    pos=paramline.indexOf("+");
		}
		params.addElement(paramline);
		return(true);
	}

	public String getHeader(String name) {
		if (req!=null) {
			return(req.getHeader(name));
		} else {
			return(null);
		}
	}

	public String getSessionName() {
		return(sname);
	}
}

