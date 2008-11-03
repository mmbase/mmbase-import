/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/ 
package org.mmbase.applications.vprowizards.spring;

import javax.servlet.http.HttpServletRequest;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Transaction;

/**
 * This version of the cloud factory will pick the cloud from the session, that
 * has been set by the nl.vpro.redactie.AuthenticationInterceptor.
 * it assumes that there is a cloud in the session object, mapped to "cloud_mmbase"
 *TODO: the coupling of the cloud to this mapping is hard, and should be configurable.  
 * 
 * @author Ernst Bunders
 * 
 */
public class SessionReadingCloudFactory implements CloudFactory {

	public Transaction createTransaction(HttpServletRequest request) {
		Cloud cloud = (Cloud) request.getSession().getAttribute("cloud_mmbase");
		if(cloud != null){
			return cloud.getTransaction("vpro-wizards-controller");
		}
		return null;
	}
	
}


