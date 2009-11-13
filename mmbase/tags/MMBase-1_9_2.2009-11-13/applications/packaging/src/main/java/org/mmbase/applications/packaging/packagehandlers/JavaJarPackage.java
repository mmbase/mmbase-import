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

import org.mmbase.applications.packaging.InstallManager;
import org.mmbase.applications.packaging.PackageManager;
import org.mmbase.applications.packaging.installhandlers.installStep;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * JavaJarPackage, Handler for installing jar files
 *
 * @author     Daniel Ockeloen (MMBased)
 */
public class JavaJarPackage extends BasicPackage implements PackageInterface {

    private static Logger log = Logging.getLoggerInstance(JavaJarPackage.class);


    /**
     *Constructor for the JavaJarPackage object
     */
    public JavaJarPackage() { }


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
            step.setUserFeedBack("java/jar installer started");

            // step 2
            step = getNextInstallStep();
            step.setUserFeedBack("receiving package ..");
            JarFile jf = getJarFile();
            if (jf != null) {
                 step.setUserFeedBack("receiving package ... done (" + jf + ")");

                 // step 3
                 step = getNextInstallStep();
                 step.setUserFeedBack("checking dependencies ..");
                 if (dependsInstalled(jf, step)) {

                     step.setUserFeedBack("checking dependencies ... done");

                     // step 4
                     step = getNextInstallStep();
                     step.setUserFeedBack("installing jar files ..");
                     installJars(jf, step);
                     step.setUserFeedBack("installing jar files ... done");

                     // step 5
                     step = getNextInstallStep();
                     step.setUserFeedBack("updating mmbase registry ..");
                     updateRegistryInstalled();
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
            step.setUserFeedBack("java/jar installer ended");

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
            step.setUserFeedBack("java/jar uninstaller started");

            // step 3
            step = getNextInstallStep();
            step.setUserFeedBack("updating mmbase registry ..");
            updateRegistryUninstalled();
            step.setUserFeedBack("updating mmbase registry ... done");

            // step 4
            step = getNextInstallStep();
            step.setUserFeedBack("java/jar installer ended");

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
    private boolean installJars(JarFile jf, installStep step) {
        Enumeration e = jf.entries();
        while (e.hasMoreElements()) {
            ZipEntry zippy = (ZipEntry) e.nextElement();

            // this is just a demo version, the html package people should figure
            // out the real format.
            String name = zippy.getName();
            String jardir = PackageManager.getConfigPath();
            jardir = jardir.substring(0, jardir.length() - 6) + "lib" + File.separator;

            // only unpack all thats in the html dir
            if (name.indexOf("jars/") == 0) {
                installStep substep = step.getNextInstallStep();
                // remove the "jars/" to get the real install base
                name = name.substring(5);

                // check if its a dir or a file
                if (zippy.isDirectory()) {
                    File d = new File(jardir + name);
                    if (!d.exists()) {
                        substep.setUserFeedBack("creating dir : " + jardir + name + ".. ");
                        d.mkdir();
                        substep.setUserFeedBack("creating dir : " + jardir + name + ".. done");
                    }
                } else {
                    substep.setUserFeedBack("creating file : " + jardir + name + ".. ");
                    try {
                        BufferedInputStream in = new BufferedInputStream(jf.getInputStream(zippy));
                        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(jardir + name + "_tmp"));
			InstallManager.addAutoResetFile(jardir + name + "_tmp");
                        int val;
                        while ((val = in.read()) != -1) {
                            out.write(val);
                        }
                        out.close();
                        substep.setUserFeedBack("creating file : " + jardir + name + ".. done");
                    } catch (IOException f) {
                        substep.setUserFeedBack("creating file : " + jardir + name + ".. failed");
                        f.printStackTrace();
                    }
                }
            }
        }
        return true;
    }

}

