/*
 *  This software is OSI Certified Open Source Software.
 *  OSI Certified is a certification mark of the Open Source Initiative.
 *  The license (Mozilla version 1.0) can be read at the MMBase site.
 *  See http://www.MMBase.org/license
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
 * @author     Daniel Ockeloen
 *
 * background hanlder for sending email, a call backthread
 * that is used to send email (one thread per active email
 * node)
 * @created    July 20, 2004
 */
public class installStep {

    // logger
    private static Logger log = Logging.getLoggerInstance(installStep.class);

    private String userfeedback;

    private Vector installsteps;

    private int timestamp;

    private int type = 0;

    private int errorcount = 0;

    private int warningcount = 0;

    private int parent = -1;

    /**
     *  Description of the Field
     */
    public final static int TYPE_ERROR = 1;

    /**
     *  Description of the Field
     */
    public final static int TYPE_WARNING = 2;


    /**
     *Constructor for the installStep object
     */
    public installStep() {
        timestamp = (int) (System.currentTimeMillis() / 1000);
    }


    /**
     *  Sets the userFeedBack attribute of the installStep object
     *
     * @param  line  The new userFeedBack value
     */
    public void setUserFeedBack(String line) {
        userfeedback = line;
    }


    /**
     *  Sets the type attribute of the installStep object
     *
     * @param  type  The new type value
     */
    public void setType(int type) {
        this.type = type;
        if (type == TYPE_WARNING) {
            warningcount++;
        }
        if (type == TYPE_ERROR) {
            errorcount++;
        }
    }


    /**
     *  Gets the userFeedBack attribute of the installStep object
     *
     * @return    The userFeedBack value
     */
    public String getUserFeedBack() {
        return userfeedback;
    }


    /**
     *  Gets the timeStamp attribute of the installStep object
     *
     * @return    The timeStamp value
     */
    public int getTimeStamp() {
        return timestamp;
    }


    /**
     *  Gets the errorCount attribute of the installStep object
     *
     * @return    The errorCount value
     */
    public int getErrorCount() {
        // count all the errors of the subs
        if (installsteps != null) {
            int total = errorcount;
            Enumeration e = installsteps.elements();
            while (e.hasMoreElements()) {
                installStep step = (installStep) e.nextElement();
                total += step.getErrorCount();
            }
            return total;
        } else {
            return errorcount;
        }
    }


    /**
     *  Gets the warningCount attribute of the installStep object
     *
     * @return    The warningCount value
     */
    public int getWarningCount() {
        if (installsteps != null) {
            int total = warningcount;
            Enumeration e = installsteps.elements();
            while (e.hasMoreElements()) {
                installStep step = (installStep) e.nextElement();
                total += step.getWarningCount();
            }
            return total;
        } else {
            return warningcount;
        }
    }


    /**
     *  Gets the installSteps attribute of the installStep object
     *
     * @return    The installSteps value
     */
    public Enumeration getInstallSteps() {
        if (installsteps != null) {
            return installsteps.elements();
        } else {
            return null;
        }
    }


    /**
     *  Gets the installSteps attribute of the installStep object
     *
     * @param  logid  Description of the Parameter
     * @return        The installSteps value
     */
    public Enumeration getInstallSteps(int logid) {
        // is it me ?
        if (logid == getId()) {
            return getInstallSteps();
        }

        // well maybe its one of my subs ?
        Enumeration e = getInstallSteps();
        if (e != null) {
            while (e.hasMoreElements()) {
                installStep step = (installStep) e.nextElement();
                Object o = step.getInstallSteps(logid);
                if (o != null) {
                    return (Enumeration) o;
                }
            }
        }
        return null;
    }


    /**
     *  Gets the nextInstallStep attribute of the installStep object
     *
     * @return    The nextInstallStep value
     */
    public installStep getNextInstallStep() {
        // create new step
        installStep step = new installStep();
        step.setParent(getId());
        if (installsteps == null) {
            installsteps = new Vector();
            installsteps.addElement(step);
            return step;
        } else {
            installsteps.addElement(step);
            return step;
        }
    }


    /**
     *  Gets the id attribute of the installStep object
     *
     * @return    The id value
     */
    public int getId() {
        return this.hashCode();
    }


    /**
     *  Sets the parent attribute of the installStep object
     *
     * @param  parent  The new parent value
     */
    public void setParent(int parent) {
        this.parent = parent;
    }


    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public boolean hasChilds() {
        if (installsteps != null) {
            return true;
        }
        return false;
    }


    /**
     *  Gets the parent attribute of the installStep object
     *
     * @return    The parent value
     */
    public int getParent() {
        return parent;
    }
}

