/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.applications.packaging.projects;

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
public class packageStep {

    // logger
    static private Logger log = Logging.getLoggerInstance(packageStep.class.getName()); 

   private String userfeedback;

   private Vector packagesteps;

   private int timestamp;

   private int type=0;

   private int errorcount=0;
	
   private int warningcount=0;

   private int parent=-1;

   public static final int TYPE_ERROR=1;

   public static final int TYPE_WARNING=2;

   public packageStep() {
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
	if (packagesteps!=null) {
		int total=errorcount;
		Enumeration e=packagesteps.elements();
		while (e.hasMoreElements()) {
			packageStep step=(packageStep)e.nextElement();
			total+=step.getErrorCount();
		}
		return total;	
	} else {
		return errorcount;
	}
   }


   public int getWarningCount() {
	if (packagesteps!=null) {
		int total=warningcount;
		Enumeration e=packagesteps.elements();
		while (e.hasMoreElements()) {
			packageStep step=(packageStep)e.nextElement();
			total+=step.getWarningCount();
		}
		return total;	
	} else {
		return warningcount;
	}
   }


   public Enumeration getPackageSteps() {
	if (packagesteps!=null) {
		return packagesteps.elements();
	} else {
		return null;
	}
   }


    public Enumeration getPackageSteps(int logid) {
	// is it me ?
	if (logid==getId()) return getPackageSteps();

	// well maybe its one of my subs ?
    	Enumeration e=getPackageSteps();
	if (e!=null) {
		while (e.hasMoreElements()) {
			packageStep step=(packageStep)e.nextElement();
			Object o=step.getPackageSteps(logid);
			if (o!=null) {
				return (Enumeration)o;
			}
		}
	}
	return null;
    }


   public packageStep getNextPackageStep() {
	// create new step
	packageStep step=new packageStep();
	step.setParent(getId());
	if (packagesteps==null) {
		packagesteps=new Vector();
		packagesteps.addElement(step);
		return step;
	} else {
		packagesteps.addElement(step);
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
	if (packagesteps!=null) {
		return true;
	} 
	return false;
   }

    public int getParent() {
	return parent;
    }
}
