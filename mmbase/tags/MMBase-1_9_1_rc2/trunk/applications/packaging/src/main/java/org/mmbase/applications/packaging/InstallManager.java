/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.applications.packaging;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import org.mmbase.applications.packaging.bundlehandlers.BundleInterface;
import org.mmbase.applications.packaging.installhandlers.installStep;
import org.mmbase.applications.packaging.installhandlers.installThread;
import org.mmbase.applications.packaging.packagehandlers.PackageInterface;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * install manager, keeps track of what is being installed, provides background
 * threads and logs errors & message to feedback to users.
 *
 * @author Daniel Ockeloen (MMBased)
 */
public class InstallManager {
    private static Logger log = Logging.getLoggerInstance(InstallManager.class);

    // is the install manager active (for dependencies reasons)
    private static boolean active = false;

    // signal if we are installing a bundle or a package
    private static boolean bundle = false;
 
    // package we are installing
    private static PackageInterface pkg;

    private static BundleInterface bnd;

    private static installThread runner;

    private static String state;

    private static boolean running = false;

    private static ArrayList<String> autoresetfiles = null;

    public static synchronized void init() {
        if (!PackageManager.isRunning()) PackageManager.init();
        if (!BundleManager.isRunning()) BundleManager.init();
        if (!ProviderManager.isRunning()) ProviderManager.init();
        if (!ShareManager.isRunning()) ShareManager.init();
        if (!ProjectManager.isRunning()) ProjectManager.init();
        running = true;
    }

    /**
    * install a package
    */
    public static synchronized boolean installPackage(PackageInterface p) {
        if (!active) {
            // turn the installManager to active
            active = true;
        
            // signal we are a package only install
            bundle = false;

            // reset the autoresetfiles
            autoresetfiles = null;

            // set the package
            pkg = p;

            state = "installing";

            p.clearInstallSteps();

            runner = new installThread();
        
            return true;
        } else {
            // returning false _allways_ means we where busy
            // error feedback is provided by the processsteps
            return false;    
        }
    }


    /**
    * install a bundle
    */
    public static synchronized boolean installBundle(BundleInterface b) {
        if (!active) {
            // turn the installManager to active
            active = true;
        
            // signal we are a bundle install
            bundle = true;

            // reset the autoresetfiles
            autoresetfiles = null;

            // set the bundle
            bnd = b;
            state = "installing";
            b.clearInstallSteps();
            runner = new installThread();
        
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
    public static void performInstall() {
        try {
            if (bnd != null) {
                bnd.install();
                state = "waiting";
                active = false;

            } else if (pkg != null) {
                pkg.install();
                state = "waiting";
                active = false;
            }
           // rename all the files that result in a server reset in a loop
           renameAutoResetFiles();
        } catch(Exception e) {
            log.error("perform Install problem",e);
        }
    }

    public void setState(String state) {
        this.state=state;
    }
    
    public String getState() {
        return state;
    }

    public static boolean isActive() {
        return active;
    }

    public static boolean isRunning() {
        return running;
    }

    public static PackageInterface getInstallingPackage() {
        return pkg;
    }

    public static BundleInterface getInstallingBundle() {
        return bnd;
    }

    public static Iterator<installStep> getInstallSteps() {
        return pkg.getInstallSteps();
    }

  
    public static void addAutoResetFile(String path) {
        if (autoresetfiles == null) autoresetfiles = new ArrayList<String>();
        autoresetfiles.add(path);
    }

    private static void renameAutoResetFiles() {
        if (autoresetfiles != null) {
	    Iterator<String> e=autoresetfiles.iterator();
            while (e.hasNext()) {
                String name=e.next();
                File oldfile = new File(name);
                File newfile = new File(name.substring(0,name.length()-4));
                if (oldfile.exists()) {
                    oldfile.renameTo(newfile);
		}
            }
        }
    }

}
