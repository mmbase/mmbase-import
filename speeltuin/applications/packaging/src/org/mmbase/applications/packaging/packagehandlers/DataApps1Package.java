/*
 *  This software is OSI Certified Open Source Software.
 *  OSI Certified is a certification mark of the Open Source Initiative.
 *  The license (Mozilla version 1.0) can be read at the MMBase site.
 *  See http://www.MMBase.org/license
 */
package org.mmbase.applications.packaging.packagehandlers;

import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
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
 * DisplayHtmlPackage, Handler for html packages
 *
 * @author     Daniel Ockeloen (MMBased)
 * @created    July 20, 2004
 */
public class DataApps1Package extends BasicPackage implements PackageInterface {

    private static Logger log = Logging.getLoggerInstance(DataApps1Package.class);

    /**
     *  Description of the Field
     */
    public final static String DTD_DATASET_1_0 = "dataset_1_0.dtd";
    /**
     *  Description of the Field
     */
    public final static String DTD_OBJECTSET_1_0 = "objectset_1_0.dtd";
    /**
     *  Description of the Field
     */
    public final static String DTD_RELATIONSET_1_0 = "relationset_1_0.dtd";

    /**
     *  Description of the Field
     */
    public final static String PUBLIC_ID_DATASET_1_0 = "-//MMBase//DTD dataset config 1.0//EN";
    /**
     *  Description of the Field
     */
    public final static String PUBLIC_ID_OBJECTSET_1_0 = "-//MMBase//DTD objectset config 1.0//EN";
    /**
     *  Description of the Field
     */
    public final static String PUBLIC_ID_RELATIONSET_1_0 = "-//MMBase//DTD relationset config 1.0//EN";


    /**
     * Register the Public Ids for DTDs used by DatabaseReader
     * This method is called by XMLEntityResolver.
     */
    public static void registerPublicIDs() {
        XMLEntityResolver.registerPublicID(PUBLIC_ID_DATASET_1_0, DTD_DATASET_1_0, DataApps1Package.class);
        XMLEntityResolver.registerPublicID(PUBLIC_ID_OBJECTSET_1_0, DTD_OBJECTSET_1_0, DataApps1Package.class);
        XMLEntityResolver.registerPublicID(PUBLIC_ID_RELATIONSET_1_0, DTD_RELATIONSET_1_0, DataApps1Package.class);
    }


    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public boolean install() {
        try {

            // step1
            installStep step = getNextInstallStep();
            step.setUserFeedBack("data/apps1 installer started");

            // step 2
            step = getNextInstallStep();
            step.setUserFeedBack("receiving package ..");
            setProgressBar(1000);
            // lets have 100 steps;

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
                    // 20%

                    step.setUserFeedBack("checking dependencies ... done");

                    // step 4
                    step = getNextInstallStep();
                    step.setUserFeedBack("loading datasets ..");
                    if (installDataSets(jf, step)) {
                        step.setUserFeedBack("loading datasets ... done");
                    } else {
                        step.setUserFeedBack("loading datasets ... failed");
                    }
                    increaseProgressBar(200);
                    // 70%

                    // step 5
                    step = getNextInstallStep();
                    step.setUserFeedBack("updating mmbase registry ..");
                    updateRegistryInstalled();
                    increaseProgressBar(100);
                    // 90%
                    step.setUserFeedBack("updating mmbase registry ... done");

                } else {
                    step.setUserFeedBack("checking dependencies ... failed");
                    setState("failed");
                    return false;
                }
            } else {
                step.setUserFeedBack("getting the mmp package...failed (server down or removed disk ? )");
                step.setType(installStep.TYPE_ERROR);
                try {
                    Thread.sleep(2000);
                } catch(Exception ee) {}
            }


            increaseProgressBar(100);
            // 100%
            // step 6
            step = getNextInstallStep();
            step.setUserFeedBack("data/apps1 installer ended");

        } catch (Exception e) {
            log.error("install crash on : " + this);
        }
        return true;
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
            step.setUserFeedBack("data/apps1 uninstaller started");

            // step 3
            step = getNextInstallStep();
            step.setUserFeedBack("updating mmbase registry ..");
            updateRegistryUninstalled();
            step.setUserFeedBack("updating mmbase registry ... done");

            // step 4
            step = getNextInstallStep();
            step.setUserFeedBack("data/apps1 installer ended");

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
    private boolean installDataSets(JarFile jf, installStep step) {
        MMBase mmb = MMBase.getMMBase();
        installStep substep = step.getNextInstallStep();
        substep.setUserFeedBack("Opening data.xml ..");
        increaseProgressBar(100);
        // 30%
        try {
            JarEntry je = jf.getJarEntry("data.xml");
            if (je != null) {
                InputStream input = jf.getInputStream(je);
                XMLBasicReader reader = new XMLBasicReader(new InputSource(input), DataApps1Package.class);
                substep.setUserFeedBack("Opening data.xml ... done");
                increaseProgressBar(100);
                // 40%
                for (Enumeration ns = reader.getChildElements("dataset.objectsets", "objectset"); ns.hasMoreElements(); ) {
                    Element n = (Element) ns.nextElement();
                    String path = n.getAttribute("path");
                    substep = step.getNextInstallStep();
                    substep.setUserFeedBack("loading objects " + path + "..");
                    if (installObjectSet(jf, path)) {
                        substep.setUserFeedBack("loading objects " + path + "... done ");
                    } else {
                        substep.setUserFeedBack("loading objects " + path + "... failed, see logfile what happend");
                        step.setType(installStep.TYPE_ERROR);
                        return false;
                    }

                }
                increaseProgressBar(100);
                // 50%
                for (Enumeration ns = reader.getChildElements("dataset.relationsets", "relationset"); ns.hasMoreElements(); ) {
                    Element n = (Element) ns.nextElement();
                    String name = n.getAttribute("name");
                    String path = n.getAttribute("path");
                    installRelationSet(jf, path);

                }
            }
        } catch (Exception e) {
            substep.setUserFeedBack("Opening data.xml ... failed");
            substep.setType(installStep.TYPE_ERROR);
            log.error("problem opending data.xml file");
        }
        increaseProgressBar(100);
        // 60%
        return true;
    }



    /**
     * @param  jf    Description of the Parameter
     * @param  path  Description of the Parameter
     * @return       Description of the Return Value
     * @javadoc
     */
    private boolean installObjectSet(JarFile jf, String path) {
        MMBase mmb = MMBase.getMMBase();
        MMObjectBuilder syncbul = mmb.getMMObject("syncnodes");
        if (syncbul != null) {
            JarEntry je = jf.getJarEntry(path);
            if (je != null) {
                try {
                    InputStream input = jf.getInputStream(je);
                    XMLBasicReader nodereader = new XMLBasicReader(new InputSource(input), DataApps1Package.class);
                    Element nr = nodereader.getElementByPath("objectset");
                    String type = nr.getAttribute("type");
                    String exportsource = nr.getAttribute("exportsource");
                    installStep step = getNextInstallStep();
                    step.setUserFeedBack("installing dataset : " + type + " ..");
                    int timestamp = -1;
                    try {
                        timestamp = Integer.parseInt(nr.getAttribute("timestamp"));
                    } catch (Exception e) {}
                    for (Enumeration ns = nodereader.getChildElements("objectset", "object");
                            ns.hasMoreElements(); ) {
                        Element n = (Element) ns.nextElement();
                        String exportnumber = n.getAttribute("number");
                        MMObjectBuilder bul = mmb.getMMObject(type);
                        String query = "exportnumber==" + exportnumber + "+exportsource=='" + exportsource + "'";
                        Enumeration b = syncbul.search(query);
                        if (b.hasMoreElements()) {
                            MMObjectNode syncnode = (MMObjectNode) b.nextElement();
                            log.info("node allready installed : " + exportnumber);
                        } else {
                            log.info("node installing !! : " + exportnumber);
                            MMObjectNode loadednode = createNewObject(nodereader, bul, n, jf);
                            MMObjectNode localnode = getExistingContentNode(loadednode, bul);
                            if (localnode == null) {
                                int localnumber = loadednode.insert(loadednode.getStringValue("owner"));
                                MMObjectNode syncnode = syncbul.getNewNode("import");
                                syncnode.setValue("exportsource", exportsource);
                                syncnode.setValue("exportnumber", exportnumber);
                                syncnode.setValue("timestamp", timestamp);
                                syncnode.setValue("localnumber", localnumber);
                                syncnode.insert("import");
                            } else {
                                MMObjectNode syncnode = syncbul.getNewNode("import");
                                syncnode.setValue("exportsource", exportsource);
                                syncnode.setValue("exportnumber", exportnumber);
                                syncnode.setValue("timestamp", timestamp);
                                syncnode.setValue("localnumber", localnode.getNumber());
                                syncnode.insert("import");
                            }
                        }
                    }
                    step.setUserFeedBack("installing objectset : " + type + " ...done");
                } catch (Exception e) {
                    log.error("can't read node xml from jar file");
                    e.printStackTrace();
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }


    /**
     *  Description of the Method
     *
     * @param  nodereader  Description of the Parameter
     * @param  bul         Description of the Parameter
     * @param  n           Description of the Parameter
     * @param  jf          Description of the Parameter
     * @return             Description of the Return Value
     */
    private MMObjectNode createNewObject(XMLBasicReader nodereader, MMObjectBuilder bul, Element n, JarFile jf) {
        String owner = n.getAttribute("owner");
        String alias = n.getAttribute("alias");

        if (owner == null) {
            owner = "import";
        }
        MMObjectNode newnode = bul.getNewNode(owner);
        if (alias != null) {
            newnode.setAlias(alias);
        }

        for (Enumeration ns2 = nodereader.getChildElements(n, "field");
                ns2.hasMoreElements(); ) {
            Element n2 = (Element) ns2.nextElement();
            String field = n2.getAttribute("name");
            org.w3c.dom.Node n3 = n2.getFirstChild();
            String value = null;
            if (n3 != null) {
                value = n3.getNodeValue();
            }

            int type = bul.getDBType(field);
            if (type != -1) {
                if (type == FieldDefs.TYPE_STRING || type == FieldDefs.TYPE_XML) {
                    if (value == null) {
                        value = "";
                    }
                    newnode.setValue(field, value);
                } else if (type == FieldDefs.TYPE_NODE) {
                    try {
                        newnode.setValue(field, Integer.parseInt(value));
                    } catch (Exception e) {
                        log.warn("error setting node-field " + e);
                        newnode.setValue(field, -1);
                    }
                } else if (type == FieldDefs.TYPE_INTEGER) {
                    try {
                        newnode.setValue(field, Integer.parseInt(value));
                    } catch (Exception e) {
                        log.warn("error setting integer-field " + e);
                        newnode.setValue(field, -1);
                    }
                } else if (type == FieldDefs.TYPE_FLOAT) {
                    try {
                        newnode.setValue(field, Float.parseFloat(value));
                    } catch (Exception e) {
                        log.warn("error setting float-field " + e);
                        newnode.setValue(field, -1);
                    }
                } else if (type == FieldDefs.TYPE_DOUBLE) {
                    try {
                        newnode.setValue(field, Double.parseDouble(value));
                    } catch (Exception e) {
                        log.warn("error setting double-field " + e);
                        newnode.setValue(field, -1);
                    }
                } else if (type == FieldDefs.TYPE_LONG) {
                    try {
                        newnode.setValue(field, Long.parseLong(value));
                    } catch (Exception e) {
                        log.warn("error setting long-field " + e);
                        newnode.setValue(field, -1);
                    }
                } else if (type == FieldDefs.TYPE_BYTE) {
                    String filename = n2.getAttribute("file");
                    JarEntry je = jf.getJarEntry("data/" + filename);
                    if (je != null) {
                        int buffersize = (int) je.getSize();
                        byte[] buffer = new byte[buffersize];
                        try {
                            DataInputStream in = new DataInputStream(jf.getInputStream(je));
                            in.readFully(buffer, 0, buffersize);
                        } catch (Exception e) {
                            log.error("Jar file (handle) read error");
                            e.printStackTrace();
                        }
                        newnode.setValue(field, buffer);
                    }
                } else {
                    log.error("FieldDefs not found for #" + type + " was not known for field with name: '" + field + "' and with value: '" + value + "'");
                }
            }
        }
        return newnode;
    }


    /**
     * @param  newnode  Description of the Parameter
     * @param  bul      Description of the Parameter
     * @return          The existingContentNode value
     * @javadoc
     */
    private MMObjectNode getExistingContentNode(MMObjectNode newnode, MMObjectBuilder bul) {
        String checkQ = "";
        Vector vec = bul.getFields();
        for (Enumeration h = vec.elements(); h.hasMoreElements(); ) {
            FieldDefs def = (FieldDefs) h.nextElement();
            if (def.isKey()) {
                int type = def.getDBType();
                String name = def.getDBName();
                if (type == FieldDefs.TYPE_STRING) {
                    String value = newnode.getStringValue(name);
                    if (checkQ.equals("")) {
                        checkQ += name + "=='" + value + "'";
                    } else {
                        checkQ += "+" + name + "=='" + value + "'";
                    }
                }
            }
        }
        if (!checkQ.equals("")) {
            Enumeration r = bul.search(checkQ);
            if (r.hasMoreElements()) {
                MMObjectNode oldnode = (MMObjectNode) r.nextElement();
                return oldnode;
            }
        }
        return null;
    }


    /**
     * @param  jf    Description of the Parameter
     * @param  path  Description of the Parameter
     * @return       Description of the Return Value
     * @javadoc
     */
    private boolean installRelationSet(JarFile jf, String path) {
        MMBase mmb = MMBase.getMMBase();
        RelDef reldef = mmb.getRelDef();
        MMObjectBuilder syncbul = mmb.getMMObject("syncnodes");
        if (syncbul != null) {
            JarEntry je = jf.getJarEntry(path);
            if (je != null) {
                try {
                    InputStream input = jf.getInputStream(je);
                    XMLBasicReader nodereader = new XMLBasicReader(new InputSource(input), DataApps1Package.class);
                    Element nr = nodereader.getElementByPath("relationset");
                    String type = nr.getAttribute("type");
                    String exportsource = nr.getAttribute("exportsource");
                    installStep step = getNextInstallStep();
                    step.setUserFeedBack("installing relationset : " + type + " ..");
                    int timestamp = -1;
                    try {
                        timestamp = Integer.parseInt(nr.getAttribute("timestamp"));
                    } catch (Exception e) {}
                    for (Enumeration ns = nodereader.getChildElements("relationset", "relation");
                            ns.hasMoreElements(); ) {
                        Element n = (Element) ns.nextElement();
                        String exportnumber = n.getAttribute("number");
                        String snumber = n.getAttribute("snumber");
                        String dnumber = n.getAttribute("dnumber");
                        String rtype = n.getAttribute("rtype");
                        String dir = n.getAttribute("dir");

                        MMObjectBuilder bul = mmb.getMMObject(type);

                        Enumeration b = syncbul.search("exportnumber==" + exportnumber + "+exportsource=='" + exportsource + "'");
                        if (b.hasMoreElements()) {
                            MMObjectNode syncnode = (MMObjectNode) b.nextElement();
                            log.debug("relation allready installed : " + exportnumber);
                        } else {
                            MMObjectNode loadednode = createNewObject(nodereader, bul, n, jf);
                            // find snumber
                            b = syncbul.search("exportnumber==" + snumber + "+exportsource=='" + exportsource + "'");
                            int realsnumber = -1;
                            if (b.hasMoreElements()) {
                                MMObjectNode n2 = (MMObjectNode) b.nextElement();
                                realsnumber = n2.getIntValue("localnumber");
                            }

                            // find dnumber
                            int realdnumber = -1;
                            b = syncbul.search("exportnumber==" + dnumber + "+exportsource=='" + exportsource + "'");
                            if (b.hasMoreElements()) {
                                MMObjectNode n2 = (MMObjectNode) b.nextElement();
                                realdnumber = n2.getIntValue("localnumber");
                            }

                            // figure out rnumber
                            int realrnumber = reldef.getNumberByName(rtype);
                            // directionality
                            if (InsRel.usesdir) {
                                int realdir = 0;
                                if (dir != null) {
                                    if ("unidirectional".equals(dir)) {
                                        realdir = 1;
                                    } else if ("bidirectional".equals(dir)) {
                                        realdir = 2;
                                    } else {
                                        log.error("invalid 'dir' attribute encountered in " + bul.getTableName() + " value=" + dir);
                                    }
                                }
                                if (realdir == 0) {
                                    MMObjectNode relnode = reldef.getNode(realrnumber);
                                    if (relnode != null) {
                                        realdir = relnode.getIntValue("dir");
                                    }
                                }
                                if (realdir != 1) {
                                    realdir = 2;
                                }
                                loadednode.setValue("dir", realdir);
                            }

                            loadednode.setValue("snumber", realsnumber);
                            loadednode.setValue("dnumber", realdnumber);
                            loadednode.setValue("rnumber", realrnumber);

                            int localnumber = -1;
                            if (realsnumber != -1 && realdnumber != -1) {
                                localnumber = loadednode.insert(loadednode.getStringValue("admin"));
                                if (localnumber != -1) {
                                    MMObjectNode syncnode = syncbul.getNewNode("import");
                                    syncnode.setValue("exportsource", exportsource);
                                    syncnode.setValue("exportnumber", exportnumber);
                                    syncnode.setValue("timestamp", timestamp);
                                    syncnode.setValue("localnumber", localnumber);
                                    syncnode.insert("import");
                                }
                            } else {
                                log.error("Cannot sync relation (exportnumber==" + exportnumber + ", snumber:" + snumber + ", dnumber:" + dnumber + ")");
                            }
                        }
                    }
                    step.setUserFeedBack("installing relationset : " + type + " ...done");
                } catch (Exception e) {
                    log.error("can't read node xml from jar file");
                    e.printStackTrace();
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

}

