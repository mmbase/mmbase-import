/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.config;

import java.util.Vector;
import java.util.Hashtable;
import java.io.File;
import java.io.IOException;

import org.mmbase.util.xml.BuilderReader;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @javadoc
 * @author  Case Roole, cjr@dds.nl
 * @version $Id: BuilderReport.java,v 1.6 2003-07-21 12:19:05 pierre Exp $
 */
public class BuilderReport extends AbstractReport {

    // logger
    private static Logger log                    = Logging.getLoggerInstance(BuilderReport.class.getName());
    // Core builders are the builders containing the meta data for the cloud
    private final static String coreBuilderList[]  = new String[]{"typedef", "reldef", "insrel"};

    // Standard builders come with the distributions and examples rely on them - it is likely that one wants to use them
    private final static String stdBuilderList[]   = new String[]{"images", "jumpers", "oalias"};

    /**
     * @javadoc
     */
    public String label() {
        return "Builders";
    }

    /**
     * @javadoc
     *
     * @return   String with database configuration
     */
    public String report() {
        String eol  = (String)specialChars.get("eol");
        String res  = "";

        if (configpath == null) {
            res = res + "mmbase.config option is not set - can't find configuration files" + eol;
        } else {

            Vector builderList;
            Hashtable builderStatus    = new Hashtable();
            Hashtable builderXMLCheck  = new Hashtable();
            try {
                builderList = listDirectory(configpath + File.separator + "builders");
            } catch (IOException e) {
                log.error("Error reading builder directory: " + e.getMessage());
                builderList = new Vector();
            }
            String buildername;
            String path;
            for (int i = 0; i < builderList.size(); i++) {
                buildername = (String)builderList.elementAt(i);
                path = configpath + File.separator + "builders" + File.separator + buildername + ".xml";
                // Aiai, parsing twice...
                res = res + "- " + buildername + ":";
                XMLParseResult pr      = new XMLParseResult(path);
                boolean foundXMLError  = false;
                if (!pr.hasDTD()) {
                    res = res + "(no dtd)";
                    builderXMLCheck.put(buildername, "0");
                } else {
                    int n  = pr.getResultList().size();
                    if (n != 0) {
                        foundXMLError = true;
                        res = res + "xml error(s):" + eol;
                        res = res + xmlErrorMessage(path, pr) + eol;
                        builderXMLCheck.put(buildername, "-1");
                    } else {
                        builderXMLCheck.put(buildername, "1");
                    }
                }
                if (!foundXMLError) {
                    BuilderReader reader  = new BuilderReader(path);
                    res = res + "status = " + reader.getStatus();

                    builderStatus.put(buildername, reader.getStatus());

                    String classfile         = reader.getClassFile();
                    String classpath;
                    if (classfile == null) {
                        res = res + "*** no class file set!" + eol;
                    } else {
                        if (!(classfile.indexOf(".") > 0)) {
                            classpath = "org.mmbase.module.builders." + classfile;
                        } else {
                            classpath = classfile;
                        }
                        try {
                            Class c  = Class.forName(classpath);
                            if (!classfile.equals("Dummy")) {
                                res = res + " (Java class = " + classpath + ")" + eol;
                            } else {
                                res = res + eol;
                            }
                        } catch (Exception e) {
                            res = res + "*** Error loading associated class " + classpath + eol;
                        }
                    }
                }
            }
            res = res + checkForCoreBuilders(builderList, builderXMLCheck, builderStatus);
        }
        return res;
    }

    /**
     * Determines whether a builder is active by parsing the builder file.
     *
     * @param path  Full path to builder configuration file
     * @return true if the whether builder is active
     */
    private boolean builderIsActive(String path) {
        BuilderReader reader  = new BuilderReader(path);
        return reader.getStatus().equalsIgnoreCase("active");
    }


    /**
     * Check whether all core builders, that is, builders containing the mmbase meta-data
     * are present and active.
     * @javadoc
     */
    private String checkForCoreBuilders(Vector v, Hashtable builderXMLCheck, Hashtable builderStatus) {
        Hashtable coreTable  = new Hashtable();
        Hashtable stdTable   = new Hashtable();

        String eol           = (String)specialChars.get("eol");

        String b;
        for (int i = 0; i < v.size(); i++) {
            boolean found  = false;
            b = (String)v.elementAt(i);
            for (int j = 0; j < coreBuilderList.length; j++) {
                if (b.equals(coreBuilderList[j])) {
                    coreTable.put(b, "1:0");
                    found = true;
                    break;
                }
            }
            if (!found) {
                for (int j = 0; j < stdBuilderList.length; j++) {
                    if (b.equals(stdBuilderList[j])) {
                        stdTable.put(b, "1:0");
                        found = true;
                        break;
                    }
                }
            }
        }

        // XXX TODO: check here whether builders are active or not!!

        // Create the report
        String res           = new String();
        res = res + "-- core builders --" + eol;
        for (int i = 0; i < coreBuilderList.length; i++) {
            String buildername  = coreBuilderList[i];
            res = res + buildername + ": ";
            if (coreTable.get(buildername) == null) {
                res = res + "FATAL ERROR: core builder not present";
            } else {
                String check  = (String)builderXMLCheck.get(buildername);
                if (check == null || check.equals("0")) {
                    String status  = (String)builderStatus.get(buildername);
                    if (status.equals("active")) {
                        res = res + "ok";
                    } else {
                        res = res + "FATAL ERROR: core builder not active";
                    }
                    res = res + " (WARNING: no dtd defined)";
                } else if (check.equals("-1")) {
                    res = res + "XML error";
                } else if (check.equals("1")) {
                    String status  = (String)builderStatus.get(buildername);
                    if (status == null || status.equals("")) {
                        res = res + "FATAL ERROR: no status set in core builder XML file";
                    } else if (status.equals("active")) {
                        res = res + "ok";
                    } else {
                        res = res + "FATAL ERROR: core builder not active";
                    }
                }
            }
            res = res + eol;
        }

        res = res + "-- standard builders --" + eol;
        for (int i = 0; i < stdBuilderList.length; i++) {
            String buildername  = stdBuilderList[i];
            res = res + buildername + ": ";
            if (stdTable.get(buildername) == null) {
                res = res + "not present";
            } else {
                res = res + "present (status not yet checked)";
            }
            res = res + eol;
        }
        return res;
    }
}

