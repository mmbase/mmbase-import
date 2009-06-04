/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.applications.packaging;

import java.util.Iterator;

import org.mmbase.applications.packaging.bundlehandlers.BundleInterface;
import org.mmbase.applications.packaging.installhandlers.installStep;
import org.mmbase.applications.packaging.installhandlers.uninstallThread;
import org.mmbase.applications.packaging.packagehandlers.PackageInterface;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * install manager, keeps track of what is being installed, provides background
 * threads and logs errors & message to feedback to users.
 *
 * @author Daniel Ockeloen (MMBased)
 */
public class UninstallManager {
    private static Logger log = Logging.getLoggerInstance(UninstallManager.class);

    // is the uninstall manager active (for dependencies reasons)
    private static boolean active = false;

    // signal if we are installing a bundle or a package
    private static boolean bundle = false;
 
    // package we are uninstalling
    private static PackageInterface pkg;

    private static BundleInterface bnd;

    private static uninstallThread runner;

    private static String state;

    /**
    * uninstall a package
    */
    public static synchronized boolean uninstallPackage(PackageInterface p) {
        if (!active) {
            // turn the uninstallManager to active
            active = true;
        
            // signal we are a package only install
            bundle = false;

            // set the package
            pkg = p;
            state = "uninstalling";
            p.clearInstallSteps();
            runner = new uninstallThread();
            return true;
        } else {
            // returning false _allways_ means we where busy
            // error feedback is provided by the processsteps
            return false;    
        }
    }


    /**
    * called by the install thread class, performs the real install in 
    * the background and keeps providing feedback using the steps interfaces
    */
    public static void performUninstall() {
        try {
            if (bnd != null) {
                bnd.uninstall();
                state = "waiting";
                active = false;
                bnd = null;
            } else if (pkg != null) {
                pkg.uninstall();
                state = "waiting";
                active = false;
                pkg = null;
            }
        } catch(Exception e) {
            log.error("performInstall problem");
        }
    }


    /**
    * uninstall a package
    */
    public static synchronized boolean uninstallBundle(BundleInterface b) {
        if (!active) {
            // turn the uninstallManager to active
            active = true;
        
            // signal we are a package only install
            bundle = true;

            // set the package
            bnd = b;

            state = "uninstalling";

            b.clearInstallSteps();

            runner = new uninstallThread();
        
            return true;
        } else {
            // returning false _allways_ means we where busy
            // error feedback is provided by the processsteps
            return false;    
        }
    }


    public void setState(String state) {
        this.state = state;
    }
    
    public String getState() {
        return state;
    }

    public static boolean isActive() {
        return active;
    }

    public static PackageInterface getUnInstallingPackage() {
        return pkg;
    }

    public static BundleInterface getUnInstallingBundle() {
        return bnd;
    }

    public static Iterator<installStep> getUninstallSteps() {
        return pkg.getInstallSteps();
    }
}
