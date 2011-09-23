/*
 *  This software is OSI Certified Open Source Software.
 *  OSI Certified is a certification mark of the Open Source Initiative.
 *  The license (Mozilla version 1.0) can be read at the MMBase site.
 *  See http://www.MMBase.org/license
 */
package org.mmbase.applications.packaging.packagehandlers;

import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.mmbase.applications.packaging.installhandlers.installStep;
import org.mmbase.applications.packaging.util.ExtendedDocumentReader;
import org.mmbase.module.core.MMBase;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.module.corebuilders.RelDef;
import org.mmbase.module.corebuilders.TypeDef;
import org.mmbase.module.corebuilders.TypeRel;
import org.mmbase.util.xml.EntityResolver;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * DisplayHtmlPackage, Handler for html packages
 *
 * @author     Daniel Ockeloen (MMBased)
 */
public class CloudModelPackage extends BasicPackage implements PackageInterface {

    private static Logger log = Logging.getLoggerInstance(CloudModelPackage.class);

    /**
     * DTD resource filename of the cloudmodel DTD version 1.0
     */
    public final static String DTD_CLOUDMODEL_1_0 = "cloudmodel_1_0.dtd";

    /**
     * Public ID of the cloudmodel DTD version 1.0
     */
    public final static String PUBLIC_ID_CLOUDMODEL_1_0 = "-//MMBase//DTD cloudmodel config 1.0//EN";


    /**
     * Register the Public Ids for DTDs used by DatabaseReader
     * This method is called by EntityResolver.
     */
    public static void registerPublicIDs() {
        EntityResolver.registerPublicID(PUBLIC_ID_CLOUDMODEL_1_0, DTD_CLOUDMODEL_1_0, CloudModelPackage.class);
    }


    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public boolean install() {
        boolean result = true;
        try {

            // step1
            installStep step = getNextInstallStep();
            step.setUserFeedBack("cloud/model installer started");

            setProgressBar(1000);
            increaseProgressBar(100);
            // downloading is 10%
            // step 2
            step = getNextInstallStep();
            step.setUserFeedBack("receiving package ..");
            JarFile jf = getJarFile();
            if (jf != null) {
                step.setUserFeedBack("receiving package ... done (" + jf + ")");
                increaseProgressBar(100);
                // downloading is 20%

                // step 3
                step = getNextInstallStep();
                step.setUserFeedBack("checking dependencies ..");
                if (dependsInstalled(jf, step)) {
                    step.setUserFeedBack("checking dependencies ... done");
                    increaseProgressBar(100);
                    // downloading is 30%

                    // step 5
                    step = getNextInstallStep();
                    step.setUserFeedBack("Opening model.xml file ..");
                    ExtendedDocumentReader reader = getModelReader(jf);
                    if (reader != null) {
                        step.setUserFeedBack("Opening model.xml file ... done");

                        step = getNextInstallStep();
                        step.setUserFeedBack("updating builders..");
                        increaseProgressBar(100);
                        // downloading is 40%
                        if (installNeededBuilders(jf, reader, step)) {
                            step.setUserFeedBack("updating builders... done");
                            increaseProgressBar(100);
                            // downloading is 50%
                        } else {
                            step.setUserFeedBack("updating builders... failed");
                            step = getNextInstallStep();
                            step.setUserFeedBack("cloud/model installer ended");
                            return false;
                        }

                        step = getNextInstallStep();
                        step.setUserFeedBack("updating relation defs..");
                        increaseProgressBar(100);
                        // downloading is 60%
                        if (installNeededRelDefs(jf, reader, step)) {
                            step.setUserFeedBack("updating relation defs... done");
                            increaseProgressBar(100);
                            // downloading is 70%
                        } else {
                            step.setUserFeedBack("updating relation defs... failed");
                            step.setType(installStep.TYPE_ERROR);
                            step = getNextInstallStep();
                            step.setUserFeedBack("cloud/model installer ended");
                            return false;
                        }

                        increaseProgressBar(100);
                        // downloading is 80%

                        step = getNextInstallStep();
                        step.setUserFeedBack("updating allowed relations");
                        installAllowedRelations(jf, reader, step);
                        step = getNextInstallStep();
                        step.setUserFeedBack("updating allowed relations done");

                        increaseProgressBar(100);
                        // downloading is 90%

                        // step 5
                        step = getNextInstallStep();
                        step.setUserFeedBack("updating mmbase registry ..");
                        updateRegistryInstalled();
                        step.setUserFeedBack("updating mmbase registry ... done");
                        increaseProgressBar(100);
                        // downloading is 100%
                    } else {
                        step.setUserFeedBack("Opening model.xml file ... failed");
                        step.setType(installStep.TYPE_ERROR);
                    }

                } else {
                    step.setUserFeedBack("checking dependencies ... failed");
                    step.setType(installStep.TYPE_ERROR);
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
            step.setUserFeedBack("cloud/model installer ended");

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
            step.setUserFeedBack("cloud/model uninstaller started");

            // step 3
            step = getNextInstallStep();
            step.setUserFeedBack("updating mmbase registry ..");
            updateRegistryUninstalled();
            step.setUserFeedBack("updating mmbase registry ... done");

            // step 4
            step = getNextInstallStep();
            step.setUserFeedBack("cloud/model installer ended");

        } catch (Exception e) {
            log.error("install crash on : " + this);
        }
        return true;
    }



    /**
     *  Description of the Method
     *
     * @param  jf      Description of the Parameter
     * @param  reader  Description of the Parameter
     * @param  step    Description of the Parameter
     * @return         Description of the Return Value
     */
    private boolean installNeededRelDefs(JarFile jf, ExtendedDocumentReader reader, installStep step) {
        MMBase mmb = MMBase.getMMBase();
        for (Element n: reader.getChildElements("cloudmodel.neededreldeflist", "reldef")) {
            String buildername = n.getAttribute("builder");
            String source = n.getAttribute("source");
            String target = n.getAttribute("target");
            String direction = n.getAttribute("direction");
            String guisourcename = n.getAttribute("guisourcename");
            String guitargetname = n.getAttribute("guitargetname");

            // retrieve builder info
            int builder = -1;
            if (RelDef.usesbuilder) {
                // if no 'builder' attribute is present (old format), use source name as builder name
                if (buildername == null) {
                    buildername = source;
                }

                builder = mmb.getTypeDef().getIntValue(buildername);
            }
            // is not explicitly set to unidirectional, direction is assumed to be bidirectional
            if ("unidirectional".equals(direction)) {
                if (!installRelDef(source, target, 1, guisourcename, guitargetname, builder, step)) {
                    return false;
                }
            } else {
                if (!installRelDef(source, target, 2, guisourcename, guitargetname, builder, step)) {
                    return false;
                }
            }

        }
        return true;
    }


    /**
     *  Description of the Method
     *
     * @param  jf      Description of the Parameter
     * @param  reader  Description of the Parameter
     * @param  step    Description of the Parameter
     * @return         Description of the Return Value
     */
    private boolean installAllowedRelations(JarFile jf, ExtendedDocumentReader reader, installStep step) {
        for (Element n: reader.getChildElements("cloudmodel.allowedrelationlist", "relation")) {
            String from = n.getAttribute("from");
            String to = n.getAttribute("to");
            String type = n.getAttribute("type");

            installStep substep = step.getNextInstallStep();
            substep.setUserFeedBack("checking allowed relation " + from + " " + to + " " + type + "..");
            if (getBundleStep()!=null) getBundleStep().setUserFeedBack("calling package installer "+getName()+".. allowed relation " + from + " " + to + " " + type);
            if (installTypeRel(from, to, type, -1)) {
                substep.setUserFeedBack("checking allowed relation " + from + " " + to + " " + type + ".. installed");
            } else {
                substep.setUserFeedBack("checking allowed relation " + from + " " + to + " " + type + ".. failed");
                substep.setType(installStep.TYPE_ERROR);
            }
        }
        return true;
    }


    /**
     *  Description of the Method
     *
     * @param  jf      Description of the Parameter
     * @param  reader  Description of the Parameter
     * @param  step    Description of the Parameter
     * @return         Description of the Return Value
     */
    private boolean installNeededBuilders(JarFile jf, ExtendedDocumentReader reader, installStep step) {
        for (Element n3: reader.getChildElements("cloudmodel.neededbuilderlist", "builder")) {
            String name = reader.getElementValue(n3);
            installStep substep = step.getNextInstallStep();
            substep.setUserFeedBack("checking builder " + name + " ..");
            if (getBundleStep()!=null) getBundleStep().setUserFeedBack("calling package installer "+getName()+"..checking builder "+ name);

            MMBase mmb = MMBase.getMMBase();

            MMObjectBuilder bul = mmb.getMMObject(name);
            // if builder not loaded
            if (bul == null) {
                // if 'inactive' in the config/builder path, fail
                String path = mmb.getBuilderPath(name, "");
                if (path != null) {
                    substep.setUserFeedBack("checking builder " + name + " .. failed, builder on system but not active. Please turn the builder on in xml file");
                    substep.setType(installStep.TYPE_ERROR);
                    continue;
                }

                // check the presence of typedef (if not present, fail)
                MMObjectBuilder objectTypes = mmb.getTypeDef();
                if (objectTypes == null) {
                    substep.setUserFeedBack("checking builder " + name + " .. failed, could not find the typedef builder.");
                    substep.setType(installStep.TYPE_ERROR);
                    return false;
                }
                // try to add a node to typedef, same as adding a builder...
                MMObjectNode type = objectTypes.getNewNode("system");
                // fill the name....
                type.setValue("name", name);

                // fill the config...
                JarEntry je = jf.getJarEntry("builders/" + name + ".xml");
                if (je == null) je = jf.getJarEntry("builders\\" + name + ".xml");
                try {
                    InputStream input = jf.getInputStream(je);

                    org.w3c.dom.Document config = null;
                    try {
                        config = ExtendedDocumentReader.getDocumentBuilder(ExtendedDocumentReader.class).parse(new InputSource(input));
                    } catch (org.xml.sax.SAXException se) {
                        substep.setUserFeedBack("checking builder " + name + " .. failed,A XML parsing error occurred (" + se.toString() + "). Check the log for details.");
                        substep.setType(installStep.TYPE_ERROR);
                        return false;
                    } catch (java.io.IOException ioe) {
                        substep.setUserFeedBack("checking builder " + name + " .. failed,A file I/O error occurred (" + ioe.toString() + "). Check the log for details.");
                        substep.setType(installStep.TYPE_ERROR);
                        return false;
                    }
                    type.setValue("config", config);

                } catch (Exception e) {
                    substep.setUserFeedBack("checking builder " + name + " .. failed, can't open builder.xml in cloudmodel jar");
                    substep.setType(installStep.TYPE_ERROR);
                    return false;
                }
                // insert into mmbase
                try {
                    objectTypes.insert("system", type);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // we now made the builder active.. look for other builders...
                substep.setUserFeedBack("checking builder " + name + " ...installed");
            } else {
                substep.setUserFeedBack("checking builder " + name + " ...allready installed");
                substep.setType(installStep.TYPE_WARNING);
            }

        }
        return true;
    }


    /**
     *  Gets the modelReader attribute of the CloudModelPackage object
     *
     * @param  jf  Description of the Parameter
     * @return     The modelReader value
     */
    private ExtendedDocumentReader getModelReader(JarFile jf) {
        try {
            JarEntry je = jf.getJarEntry("model.xml");
            if (je != null) {
                InputStream input = jf.getInputStream(je);
                ExtendedDocumentReader reader = new ExtendedDocumentReader(new InputSource(input), CloudModelPackage.class);
                if (reader != null) {
                    return reader;
                }
            }
        } catch (Exception e) {
            log.error("problem opending model.xml file");
        }
        return null;
    }



    /**
     * Checks whether a given relation definition exists, and if not, creates that definition.
     *
     * @param  sname     source name of the relation definition
     * @param  dname     destination name of the relation definition
     * @param  dir       directionality (uni or bi)
     * @param  sguiname  source GUI name of the relation definition
     * @param  dguiname  destination GUI name of the relation definition
     * @param  builder   references the builder to use (only in new format)
     * @param  step      Description of the Parameter
     * @return           <code>true</code> if succesfull, <code>false</code> if an error occurred
     */
    private boolean installRelDef(String sname, String dname, int dir, String sguiname, String dguiname, int builder, installStep step) {
        MMBase mmb = MMBase.getMMBase();
        RelDef reldef = mmb.getRelDef();
        if (reldef != null) {
            installStep substep = step.getNextInstallStep();
            substep.setUserFeedBack("checking reldef " + sname + "/" + dname + " ..");
            if (getBundleStep()!=null) getBundleStep().setUserFeedBack("calling package installer "+getName()+"..checking reldef " + sname + "/" + dname + " ..");
            if (reldef.getNumberByName(sname + "/" + dname) == -1) {
                MMObjectNode node = reldef.getNewNode("system");
                node.setValue("sname", sname);
                node.setValue("dname", dname);
                node.setValue("dir", dir);
                node.setValue("sguiname", sguiname);
                node.setValue("dguiname", dguiname);
                if (RelDef.usesbuilder) {
                    // if builder is unknown (falsely specified), use the InsRel builder
                    if (builder <= 0) {
                        builder = mmb.getInsRel().getNumber();
                    }
                    node.setValue("builder", builder);
                }
                int id = reldef.insert("system", node);
                if (id != -1) {
                    substep.setUserFeedBack("checking reldef " + sname + "/" + dname + " ..installed");
                } else {
                    substep.setUserFeedBack("checking reldef " + sname + "/" + dname + " .. not installed");
                    substep.setType(installStep.TYPE_ERROR);
                    return false;
                }
            } else {
                substep.setUserFeedBack("checking reldef " + sname + "/" + dname + " .. allready installed");
                substep.setType(installStep.TYPE_WARNING);
            }
        } else {
            installStep substep = step.getNextInstallStep();
            substep.setUserFeedBack("Can't use reldef !");
            substep.setType(installStep.TYPE_ERROR);
            return false;
        }
        return true;
    }



    /**
     * Checks and if required installs an allowed type relation (typerel object).
     *
     * @param  sname  source type name of the type relation
     * @param  dname  destination type name of the type relation
     * @param  rname  role name of the type relation
     * @param  count  cardinality of the type relation
     * @return        <code>true</code> if succesfull, <code>false</code> if an error occurred
     */
    private boolean installTypeRel(String sname, String dname, String rname, int count) {
        MMBase mmb = MMBase.getMMBase();
        TypeRel typerel = mmb.getTypeRel();
        if (typerel != null) {
            TypeDef typedef = mmb.getTypeDef();
            if (typedef == null) {
                //return result.error("Can't get typedef builder");
                return false;
            }
            RelDef reldef = mmb.getRelDef();
            if (reldef == null) {
                //return result.error("Can't get reldef builder");
                return false;
            }

            // figure out rnumber
            int rnumber = reldef.getNumberByName(rname);
            if (rnumber == -1) {
                //return result.error("No reldef with role '"+rname+"' defined");
                return false;
            }

            // figure out snumber
            int snumber = typedef.getIntValue(sname);
            if (snumber == -1) {
                //return result.error("No builder with name '"+sname+"' defined");
                return false;
            }

            // figure out dnumber
            int dnumber = typedef.getIntValue(dname);
            if (dnumber == -1) {
                //return result.error("No builder with name '"+dname+"' defined");
                return false;
            }

            if (!typerel.contains(snumber, dnumber, rnumber, TypeRel.STRICT)) {
                MMObjectNode node = typerel.getNewNode("system");
                node.setValue("snumber", snumber);
                node.setValue("dnumber", dnumber);
                node.setValue("rnumber", rnumber);
                node.setValue("max", count);
                int id = typerel.insert("system", node);
                if (id != -1) {
                    log.debug("TypeRel (" + sname + "," + dname + "," + rname + ") installed");
                } else {
                    //return result.error("TypeRel ("+sname+","+dname+","+rname+") could not be installed");
                    return false;
                }
            }
            return true;
        } else {
            //return result.error("Can't get typerel builder");
            return false;
        }
    }

}

