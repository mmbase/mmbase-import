/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import java.io.*;
import java.util.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import org.mmbase.bridge.*;

/**
 * MMCloud
 * Creates a cloud object (pulling it from if session necessary)
 *
 * @author Pierre van Rooden
 *
 **/
public class MMCloud extends MMTaglib implements BodyTag{

    private String cloudName=DEFAULT_CLOUD_NAME;

    public static boolean debug = true;
    private String authenticate = "PropertiesLogon";
    private String logon = null;
    private String pwd = null;
    private String cache = null;
    private String expire = "";
    String request_line = null;
    private HttpSession session;
    private HttpServletRequest request;

    
    //simple method to dump debug data into System.err
    public void debug(String debugdata){
	System.err.println("MMCloud:" + debugdata);
    }

    /**
     * implementation of TagExtraInfo return values declared here
     * should be filled at one point, currently fillVars is responsible for 
     * that ant gets called before every 
     **/
    public VariableInfo[] getVariableInfo(TagData data){
        VariableInfo[] variableInfo =    null;
        if (data.getAttribute("name")!= null){
            cloudName= "" + data.getAttribute("name");
        } else {
            cloudName=DEFAULT_CLOUD_NAME;
        }
	    variableInfo =    new VariableInfo[1];
	    variableInfo[0] =  new VariableInfo("cloud","org.mmbase.bridge.Cloud",true,VariableInfo.NESTED);
    	return variableInfo;
    }
    
    
    public void setName(String name){
        cloudName = name;
    }

    public void setLogon(String logon){
        this.logon = logon;
    }

    public void setPwd(String pwd){
        this.pwd = pwd;
    }

    public void setExpire(String expire){
        this.expire = expire;
    }

    public void setAuthenticate(String authenticate){
        this.authenticate = authenticate;
    }

    public void setCache(String cache){
        this.cache = cache;
    }

    /**
     *  Check name and retrieve cloud
     **/
    public int doStartTag() throws JspException{
        session=(HttpSession)pageContext.getSession();
        request=(HttpServletRequest)pageContext.getRequest();
	    Cloud cloud = (Cloud)session.getValue("cloud_"+cloudName);
	    if (cloud==null) {
    	    cloud=getDefaultCloudContext().getCloud(cloudName);
    	    if (cloud==null) {
        	    return SKIP_BODY;
    	    } else {
    	        session.putValue("cloud_"+cloudName,cloud);
    	    }
        }
//        if ((logon!=null) && (!logon.equals(cloud.getUserName()))) {
//            cloud.logon(authenticate,new Object[] {logon,pwd});
//        }
        setPageCloud(cloud);
        if (cache!=null) {
            Module cacher = getDefaultCloudContext().getModule("scancache");
            if (cacher instanceof CacheModule) {
                try {
                    // caching of cloud=part of page
                    request_line = request.getRequestURI();
                    String reload = (String)session.getValue("reload");
                    String rst=((CacheModule)cacher).get(cache,request_line,expire+">");
	                if (rst!=null && !("Y".equals(reload))) {
	                    pageContext.getOut().write(rst);
                        return Tag.SKIP_BODY;
                    }
                } catch (IOException ioe){
                    throw new JspTagException(ioe.toString());
                }
            }
        }
    	return EVAL_BODY_TAG;
    }

    public void doInitBody() throws JspException {
        pageContext.setAttribute("cloud",getPageCloud());
    }
    
    public int doAfterBody() throws JspException {
        try {
            if (cache!=null) {
                Module cacher = getDefaultCloudContext().getModule("scancache");
                if (cacher instanceof CacheModule) {
                    ((CacheModule)cacher).put(cache,request_line,bodyOut.getString());
                }
            }
            bodyOut.writeOut(bodyOut.getEnclosingWriter());
            return Tag.SKIP_BODY;
        } catch (IOException ioe){
            throw new JspTagException(ioe.toString());
        }
    }

}

