/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.applications.packaging.installhandlers;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

import org.mmbase.applications.packaging.*;
import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * @author Daniel Ockeloen
 * 
 * background hanlder for sending email, a call backthread
 * that is used to send email (one thread per active email
 * node)
 */
public class installStep {

    // logger
    static private Logger log = Logging.getLoggerInstance(installStep.class.getName()); 

   private String userfeedback;

   private Vector installsteps;

   private int timestamp;

   private int type=0;

   private int errorcount=0;
	
   private int warningcount=0;

   private int parent=-1;

   public static final int TYPE_ERROR=1;

   public static final int TYPE_WARNING=2;

   public installStep() {
	timestamp=(int)(System.currentTimeMillis()/1000);
   }

   public void setUserFeedBack(String line) {
	userfeedback=line;
   }

   public void setType(int type) {
	this.type=type;
	if (type==TYPE_WARNING) {
		warningcount++;
	} if (type==TYPE_ERROR) {
		errorcount++;
	}
   }
  
   public String getUserFeedBack() {
	return userfeedback;
   }

   public int getTimeStamp() {
	return timestamp;
   }

   public int getErrorCount() {
	// count all the errors of the subs
	if (installsteps!=null) {
		int total=errorcount;
		Enumeration e=installsteps.elements();
		while (e.hasMoreElements()) {
			installStep step=(installStep)e.nextElement();
			total+=step.getErrorCount();
		}
		return total;	
	} else {
		return errorcount;
	}
   }


   public int getWarningCount() {
	if (installsteps!=null) {
		int total=warningcount;
		Enumeration e=installsteps.elements();
		while (e.hasMoreElements()) {
			installStep step=(installStep)e.nextElement();
			total+=step.getWarningCount();
		}
		return total;	
	} else {
		return warningcount;
	}
   }


   public Enumeration getInstallSteps() {
	if (installsteps!=null) {
		return installsteps.elements();
	} else {
		return null;
	}
   }


    public Enumeration getInstallSteps(int logid) {
	// is it me ?
	if (logid==getId()) return getInstallSteps();

	// well maybe its one of my subs ?
    	Enumeration e=getInstallSteps();
	if (e!=null) {
		while (e.hasMoreElements()) {
			installStep step=(installStep)e.nextElement();
			Object o=step.getInstallSteps(logid);
			if (o!=null) {
				return (Enumeration)o;
			}
		}
	}
	return null;
    }


   public installStep getNextInstallStep() {
	// create new step
	installStep step=new installStep();
	step.setParent(getId());
	if (installsteps==null) {
		installsteps=new Vector();
		installsteps.addElement(step);
		return step;
	} else {
		installsteps.addElement(step);
		return step;
	}
    }

    public int getId() {
	return this.hashCode();
    }

    public void setParent(int parent) {
	this.parent=parent;
    }

    public boolean hasChilds() {
	if (installsteps!=null) {
		return true;
	} 
	return false;
   }

    public int getParent() {
	return parent;
    }
}
