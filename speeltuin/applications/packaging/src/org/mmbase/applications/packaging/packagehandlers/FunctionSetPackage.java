/*
 *  This software is OSI Certified Open Source Software.
 *  OSI Certified is a certification mark of the Open Source Initiative.
 *  The license (Mozilla version 1.0) can be read at the MMBase site.
 *  See http://www.MMBase.org/license
 */
package org.mmbase.applications.packaging.packagehandlers;

import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;
import org.mmbase.util.*;
import org.mmbase.module.builders.Versions;
import org.mmbase.applications.packaging.*;
import org.mmbase.applications.packaging.packagehandlers.*;
import org.mmbase.applications.packaging.providerhandlers.*;
import org.mmbase.applications.packaging.installhandlers.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.jar.*;
import java.util.zip.*;

import org.w3c.dom.*;
import org.xml.sax.*;

/**
 * FunctionSetPackage, Handler for html packages
 *
 * @author     Daniel Ockeloen (MMBased)
 * @created    July 20, 2004
 */
public class FunctionSetPackage extends BasicPackage implements PackageInterface {

    private static Logger log = Logging.getLoggerInstance(DisplayHtmlPackage.class);

    /**
     * DTD resource filename of the functionsets DTD version 1.0
     */
    public final static String DTD_FUNCTIONSETS_1_0 = "functionsets_1_0.dtd";

    /**
     * Public ID of the functionsets DTD version 1.0
     */
    public final static String PUBLIC_ID_FUNCTIONSETS_1_0 = "-//MMBase//DTD functionsets config 1.0//EN";


    /**
     * Register the Public Ids for DTDs used by DatabaseReader
     * This method is called by XMLEntityResolver.
     */
    public static void registerPublicIDs() {
        XMLEntityResolver.registerPublicID(PUBLIC_ID_FUNCTIONSETS_1_0, DTD_FUNCTIONSETS_1_0, FunctionSetPackage.class);
    }


    /**
     *Constructor for the FunctionSetPackage object
     */
    public FunctionSetPackage() { }


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
            step.setUserFeedBack("function/set installer started");

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
                    step.setUserFeedBack("installing functionsets ..");
                    installFunctionSets(jf, step);
                    step.setUserFeedBack("installing functionsets ... done");

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
            step.setUserFeedBack("function/set installer ended");

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
            step.setUserFeedBack("function/set uninstaller started");

            // step 3
            step = getNextInstallStep();
            step.setUserFeedBack("updating mmbase registry ..");
            updateRegistryUninstalled();
            step.setUserFeedBack("updating mmbase registry ... done");

            // step 4
            step = getNextInstallStep();
            step.setUserFeedBack("function/set installer ended");

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
    private boolean installFunctionSets(JarFile jf, installStep step) {
        String functiondir = MMBaseContext.getConfigPath() + File.separator + "functions" + File.separator;

        JarEntry je = jf.getJarEntry("functionsets.xml");
        if (je == null) {
            // temp extra until i remove the file ref !
            je = jf.getJarEntry("functionsetfile.xml");
        }
        if (je != null) {
            try {
                InputStream input = jf.getInputStream(je);
                XMLBasicReader reader = new XMLBasicReader(new InputSource(input), FunctionSetPackage.class);
                for (Enumeration ns = reader.getChildElements("functionsets", "functionset"); ns.hasMoreElements(); ) {
                    Element n = (Element) ns.nextElement();
                    String name = n.getAttribute("name");
                    String file = n.getAttribute("file");
                    if (file != null) {
                        if (!installFunctionSet(jf, step, name, file)) {
                            return false;
                        }
                    }
                }
            } catch (Exception e) {
                installStep substep = step.getNextInstallStep();
                substep.setType(installStep.TYPE_ERROR);
                substep.setUserFeedBack("can't open functionsetfile.xml");
                return false;
            }
        }
        return true;
    }


    /**
     *  Description of the Method
     *
     * @param  jf    Description of the Parameter
     * @param  step  Description of the Parameter
     * @param  name  Description of the Parameter
     * @param  file  Description of the Parameter
     * @return       Description of the Return Value
     */
    private boolean installFunctionSet(JarFile jf, installStep step, String name, String file) {
        String functiondir = MMBaseContext.getConfigPath() + File.separator + "functions" + File.separator;
        installStep substep = step.getNextInstallStep();

        JarEntry je = jf.getJarEntry(file);
        if (je != null) {
            substep.setUserFeedBack("creating fuction file : " + functiondir + file + ".. ");
            try {
                InputStream in = jf.getInputStream(je);
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(functiondir + "sets" + File.separator + file));
                int val;
                while ((val = in.read()) != -1) {
                    out.write(val);
                }
                out.close();
                substep.setUserFeedBack("creating file : " + functiondir + file + ".. done");
            } catch (IOException f) {
                substep.setUserFeedBack("creating file : " + functiondir + file + ".. failed");
                substep.setType(installStep.TYPE_ERROR);
                f.printStackTrace();
                return false;
            }
        }
        // so the functionset file is installed/updated then update the
        // functionsetfile. the update/write should be done my the function
        // manager but until functions are clear in 1.7 ill do it like this.
        XMLBasicReader reader = new XMLBasicReader(functiondir + "functionsets.xml", FunctionSetPackage.class);
        String body = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
        body += "<!DOCTYPE functionsets PUBLIC \"//MMBase - functionsets//\" \"http://www.mmbase.org/dtd/functionsets_1_0.dtd\">\n";
        body += "<functionsets>\n";
        boolean found = false;
        for (Enumeration ns = reader.getChildElements("functionsets", "functionset"); ns.hasMoreElements(); ) {
            Element n = (Element) ns.nextElement();
            String oldname = n.getAttribute("name");
            String oldfile = n.getAttribute("file");
            body += "\t<functionset name=\"" + oldname + "\" file=\"" + oldfile + "\" />\n";
            if (name.equals(oldname) && file.equals(oldfile.substring(5))) {
                found = true;
            }
        }

        if (!found) {
            body += "\t<functionset name=\"" + name + "\" file=\"sets/" + file + "\" />\n";
            body += "</functionsets>\n";
            saveFile(functiondir + "functionsets.xml", body);
        }

        return true;
    }


    /**
     *  Description of the Method
     *
     * @param  filename  Description of the Parameter
     * @param  value     Description of the Parameter
     * @return           Description of the Return Value
     */
    static boolean saveFile(String filename, String value) {
        File sfile = new File(filename);
        try {
            DataOutputStream scan = new DataOutputStream(new FileOutputStream(sfile));
            scan.writeBytes(value);
            scan.flush();
            scan.close();
        } catch (Exception e) {
            log.error(Logging.stackTrace(e));
        }
        return true;
    }
}

