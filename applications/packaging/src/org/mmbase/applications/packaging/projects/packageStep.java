/*
 *  This software is OSI Certified Open Source Software.
 *  OSI Certified is a certification mark of the Open Source Initiative.
 *  The license (Mozilla version 1.0) can be read at the MMBase site.
 *  See http://www.MMBase.org/license
 */
package org.mmbase.applications.packaging.projects;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @version $Id$
 * @author     Daniel Ockeloen
 * @rename PackageStep
 */
public class packageStep {

    private String userfeedback;

    private ArrayList<packageStep> packagesteps;

    private int timestamp;

    private int type = 0;

    private int errorcount = 0;

    private int warningcount = 0;

    private int parent = -1;

    public final static int TYPE_ERROR = 1;

    public final static int TYPE_WARNING = 2;


    /**
     * Constructor for the PackageStep object
     */
    public packageStep() {
        timestamp = (int) (System.currentTimeMillis() / 1000);
    }


    /**
     *  Sets the userFeedBack attribute of the packageStep object
     *
     * @param  line  The new userFeedBack value
     */
    public void setUserFeedBack(String line) {
        userfeedback = line;
    }



    /**
     *  Sets the type attribute of the packageStep object
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
     *  Gets the userFeedBack attribute of the packageStep object
     *
     * @return    The userFeedBack value
     */
    public String getUserFeedBack() {
        return userfeedback;
    }


    /**
     *  Gets the timeStamp attribute of the packageStep object
     *
     * @return    The timeStamp value
     */
    public int getTimeStamp() {
        return timestamp;
    }


    /**
     *  Gets the errorCount attribute of the packageStep object
     *
     * @return    The errorCount value
     */
    public int getErrorCount() {
        // count all the errors of the subs
        if (packagesteps != null) {
            int total = errorcount;
            Iterator<packageStep> e = packagesteps.iterator();
            while (e.hasNext()) {
                packageStep step = e.next();
                total += step.getErrorCount();
            }
            return total;
        } else {
            return errorcount;
        }
    }


    /**
     *  Gets the warningCount attribute of the packageStep object
     *
     * @return    The warningCount value
     */
    public int getWarningCount() {
        if (packagesteps != null) {
            int total = warningcount;
            Iterator<packageStep> e = packagesteps.iterator();
            while (e.hasNext()) {
                packageStep step = e.next();
                total += step.getWarningCount();
            }
            return total;
        } else {
            return warningcount;
        }
    }


    /**
     *  Gets the packageSteps attribute of the packageStep object
     *
     * @return    The packageSteps value
     */
    public Iterator<packageStep> getPackageSteps() {
        if (packagesteps != null) {
            return packagesteps.iterator();
        } else {
            return null;
        }
    }


    /**
     *  Gets the packageSteps attribute of the packageStep object
     *
     * @param  logid  Description of the Parameter
     * @return        The packageSteps value
     */
    public Iterator<packageStep> getPackageSteps(int logid) {
        // is it me ?
        if (logid == getId()) {
            return getPackageSteps();
        }

        // well maybe its one of my subs ?
        Iterator<packageStep> e = getPackageSteps();
        if (e != null) {
            while (e.hasNext()) {
                packageStep step = e.next();
                Object o = step.getPackageSteps(logid);
                if (o != null) {
                    return (Iterator<packageStep>) o;
                }
            }
        }
        return null;
    }


    /**
     *  Gets the nextPackageStep attribute of the packageStep object
     *
     * @return    The nextPackageStep value
     */
    public packageStep getNextPackageStep() {
        // create new step
        packageStep step = new packageStep();
        step.setParent(getId());
        if (packagesteps == null) {
            packagesteps = new ArrayList<packageStep>();
            packagesteps.add(step);
            return step;
        } else {
            packagesteps.add(step);
            return step;
        }
    }


    /**
     *  Gets the id attribute of the packageStep object
     *
     * @return    The id value
     */
    public int getId() {
        return this.hashCode();
    }


    /**
     *  Sets the parent attribute of the packageStep object
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
        if (packagesteps != null) {
            return true;
        }
        return false;
    }


    /**
     *  Gets the parent attribute of the packageStep object
     *
     * @return    The parent value
     */
    public int getParent() {
        return parent;
    }
}

