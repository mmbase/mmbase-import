/*
 *  This software is OSI Certified Open Source Software.
 *  OSI Certified is a certification mark of the Open Source Initiative.
 *  The license (Mozilla version 1.0) can be read at the MMBase site.
 *  See http://www.MMBase.org/license
 */
package org.mmbase.applications.packaging.packagehandlers;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.mmbase.applications.packaging.PackageManager;
import org.mmbase.applications.packaging.installhandlers.installStep;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * ConfigBasicPackage, Handler for configuration files
 *
 * @author     Daniel Ockeloen (MMBased)
 */
public class ConfigBasicPackage extends BasicPackage implements PackageInterface {

    private static Logger log = Logging.getLoggerInstance(ConfigBasicPackage.class);


    /**
     *Constructor for the ConfigBasicPackage object
     */
    public ConfigBasicPackage() { }


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
            step.setUserFeedBack("config file installer started");

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
                    step.setUserFeedBack("installing config files ..");
                    installConfigFiles(jf, step);
                    step.setUserFeedBack("installing config files ... done");

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
            step.setUserFeedBack("config files installer ended");

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
            step.setUserFeedBack("config files uninstaller started");

            // step 3
            step = getNextInstallStep();
            step.setUserFeedBack("updating mmbase registry ..");
            updateRegistryUninstalled();
            step.setUserFeedBack("updating mmbase registry ... done");

            // step 4
            step = getNextInstallStep();
            step.setUserFeedBack("config files installer ended");

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
    private boolean installConfigFiles(JarFile jf, installStep step) {
        Enumeration e = jf.entries();
        while (e.hasMoreElements()) {
            ZipEntry zippy = (ZipEntry) e.nextElement();

            // this is just a demo version, the config package people should figure
            // out the real format.
            String name = zippy.getName();
            String configdir = PackageManager.getConfigPath() + File.separator;

            // only unpack all thats in the config dir
            if (name.indexOf("config/") == 0) {
                installStep substep = step.getNextInstallStep();
                // remove the "config/" to get the real install base
                name = name.substring(7);

                // check if its a dir or a file
                if (zippy.isDirectory()) {
                    File d = new File(configdir + name);
                    if (!d.exists()) {
                        substep.setUserFeedBack("creating dir : " + configdir + name + ".. ");
                        d.mkdir();
                        substep.setUserFeedBack("creating dir : " + configdir + name + ".. done");
                    }
                } else {
                    substep.setUserFeedBack("creating file : " + configdir + name + ".. ");
                    try {
                        InputStream in = jf.getInputStream(zippy);
                        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(configdir + name));
                        int val;
                        while ((val = in.read()) != -1) {
                            out.write(val);
                        }
                        out.close();
                        substep.setUserFeedBack("creating file : " + configdir + name + ".. done");
                    } catch (IOException f) {
                        substep.setUserFeedBack("creating file : " + configdir + name + ".. failed");
                        f.printStackTrace();
                    }
                }
            }
        }
        return true;
    }

}

