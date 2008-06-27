/*
 *  This software is OSI Certified Open Source Software.
 *  OSI Certified is a certification mark of the Open Source Initiative.
 *  The license (Mozilla version 1.0) can be read at the MMBase site.
 *  See http://www.MMBase.org/license
 */
package org.mmbase.applications.packaging.packagehandlers;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.mmbase.applications.packaging.installhandlers.installStep;
import org.mmbase.module.core.MMBaseContext;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * DisplayHtmlPackage, Handler for html packages
 *
 * @author     Daniel Ockeloen (MMBased)
 */
public class DisplayHtmlPackage extends BasicPackage implements PackageInterface {

    private static Logger log = Logging.getLoggerInstance(DisplayHtmlPackage.class);


    /**
     *Constructor for the DisplayHtmlPackage object
     */
    public DisplayHtmlPackage() { }


    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public boolean install() {
        boolean result = true;
        // needs to be set to false on a error
        try {

            // step1
            installStep step = getNextInstallStep();
            step.setUserFeedBack("display/html installer started");
            setProgressBar(1000);
            // lets have 100 steps;

            // step 2
            step = getNextInstallStep();
            step.setUserFeedBack("receiving package ..");
	    if (getBundleStep()!=null) getBundleStep().setUserFeedBack("calling package installer "+getName()+"..receiving package");
            JarFile jf = getJarFile();
            if (jf != null) {
                step.setUserFeedBack("receiving package ... done (" + jf + ")");

                increaseProgressBar(100);
                // downloading is 10%

                // step 3
                step = getNextInstallStep();
                step.setUserFeedBack("checking dependencies ..");
                if (dependsInstalled(jf, step)) {

                    increaseProgressBar(100);
                    // downloading is 20%

                    step.setUserFeedBack("checking dependencies ... done");

                    // step 4
                    step = getNextInstallStep();
                    step.setUserFeedBack("installing html pages ..");
                    installPages(jf, step);
                    step.setUserFeedBack("installing html pages ... done");

                    increaseProgressBar(100);
                    // downloading is 80%

                    // step 5
                    step = getNextInstallStep();
                    step.setUserFeedBack("updating mmbase registry ..");
                    updateRegistryInstalled();
                    increaseProgressBar(100);
                    // downloading is 90%
                    step.setUserFeedBack("updating mmbase registry ... done");
                } else {
                    step.setUserFeedBack("checking dependencies ... failed");
                    setState("failed");
                    result = false;
                }
            } else {
                step.setUserFeedBack("getting the mmp package...failed (server down or removed disk ? )");
                step.setType(installStep.TYPE_ERROR);
                try {
                    Thread.sleep(2000);
                } catch(Exception ee) {}
            }


            // step 6
            step = getNextInstallStep();
            step.setUserFeedBack("display/html installer ended");
            increaseProgressBar(100);
            // downloading is 100%

        } catch (Exception e) {
            log.error("install crash on : " + this);
            result = false;
        }
        return result;
    }


    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public boolean uninstall() {
        try {

            // step1
            installStep step = getNextInstallStep();
            step.setUserFeedBack("display/html uninstaller started");

            // step 3
            step = getNextInstallStep();
            step.setUserFeedBack("updating mmbase registry ..");
            updateRegistryUninstalled();
            step.setUserFeedBack("updating mmbase registry ... done");

            // step 4
            step = getNextInstallStep();
            step.setUserFeedBack("display/html installer ended");

        } catch (Exception e) {
            log.error("install crash on : " + this);
        }
        return true;
    }


    /**
     *  Description of the Method
     *
     * @param  jf    Description of the Parameter
     * @param  step  Description of the Parameter
     * @return       Description of the Return Value
     */
    private boolean installPages(JarFile jf, installStep step) {
        Enumeration e = jf.entries();
        increaseProgressBar(100);
        // downloading is 30%
        while (e.hasMoreElements()) {
            increaseProgressBar(1);
            // downloading is 1%
            ZipEntry zippy = (ZipEntry) e.nextElement();

            // this is just a demo version, the html package people should figure
            // out the real format.
            String name = zippy.getName();
            String htmldir = MMBaseContext.getHtmlRoot() + File.separator;


            // only unpack all thats in the html dir
            name = name.replace('/',File.separatorChar);
            name = name.replace('\\',File.separatorChar);
            if (name.indexOf("html"+File.separator) == 0) {
                installStep substep = step.getNextInstallStep();
                // remove the "html/" to get the real install base
                name = name.substring(5);

                // check if its a dir or a file
                if (zippy.isDirectory()) {
                    File d = new File(htmldir + name);
                    if (!d.exists()) {
                        substep.setUserFeedBack("creating dir : " + htmldir + name + ".. ");
                        d.mkdir();
                        substep.setUserFeedBack("creating dir : " + htmldir + name + ".. done");
                    }
                } else {
        	    step.setUserFeedBack("installing html pages .. "+name);
		    if (getBundleStep()!=null) getBundleStep().setUserFeedBack("calling package installer "+getName()+".. "+name);
                    substep.setUserFeedBack("creating file : " + htmldir + name + ".. ");
                    try {
                        BufferedInputStream in = new BufferedInputStream(jf.getInputStream(zippy));
                        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(htmldir + name));
                        int val;
                        while ((val = in.read()) != -1) {
                            out.write(val);
                        }
                        out.close();
                        substep.setUserFeedBack("creating file : " + htmldir + name + ".. done");
                    } catch (IOException f) {
                        substep.setUserFeedBack("creating file : " + htmldir + name + ".. failed");
                        f.printStackTrace();
                    }
                }
            }
        }
        increaseProgressBar(100);
        // downloading is 70%
        return true;
    }

}

