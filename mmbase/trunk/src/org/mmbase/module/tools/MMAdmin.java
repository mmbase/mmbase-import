/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package org.mmbase.module.tools;

import java.io.File;
import java.util.*;

import org.mmbase.cache.MultilevelCache;
import org.mmbase.module.*;
import org.mmbase.module.builders.Versions;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.tools.MMAppTool.MMAppTool;
import org.mmbase.storage.search.SearchQueryException;
import org.mmbase.storage.StorageException;
import org.mmbase.storage.StorageManagerFactory;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;
import org.mmbase.util.xml.*;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;


/**
 * @javadoc
 *
 * @author Daniel Ockeloen
 * @author Pierre van Rooden
 * @version $Id: MMAdmin.java,v 1.80 2004-01-08 16:59:19 pierre Exp $
 */
public class MMAdmin extends ProcessorModule {

    // logging routines
    private static Logger log = Logging.getLoggerInstance(MMAdmin.class);

    // true: ready (probeCall was called)
    private boolean state = false;
    /**
     * reference to MMBase
     * @scope private
     */
    MMBase mmb = null;
    /**
     * @javadoc
     * @scope private
     */
    MMAdminProbe probe = null;
    /**
     * @javadoc
     * @scope private
     */
    String lastmsg = "";
    /**
     * @javadoc
     */
    private boolean restartwanted = false;
    /**
     * @javadoc
     */
    private boolean kioskmode = false;

    /**
     * @javadoc
     */
    public MMAdmin() {}

    /**
     * @javadoc
     */
    public void init() {
        String dtmp = System.getProperty("mmbase.kiosk");
        if (dtmp != null && dtmp.equals("yes")) {
            kioskmode = true;
            log.info("*** Server started in kiosk mode ***");
        }
        mmb = (MMBase)getModule("MMBASEROOT");
        probe = new MMAdminProbe(this);
    }

    /**
     * Returns a virtual builder used to create node lists from the results
     * returned by getList().
     * The default method does not associate the builder with a cloud (mmbase module),
     * so processormodules that need this association need to override this method.
     * Note that different lists may return different builders.
     * @param command the LIST command for which to retrieve the builder
     * @param params contains the attributes for the list
     */
    public MMObjectBuilder getListBuilder(String command, Map params) {
        return new VirtualBuilder(mmb);
    }

    /**
     * Retrieves a specified builder.
     * The builder's name can be extended with the subpath of that builder's configuration file.
     * i.e. 'core/typedef' or 'basic/images'. The subpath part is ignored.
     * @param name The path of the builder to retrieve
     * @return a <code>MMObjectBuilder</code> is found, <code>null</code> otherwise
     */
    public MMObjectBuilder getMMObject(String path) {
        int pos = path.lastIndexOf(File.separator);
        if (pos != -1) {
            path = path.substring(pos + 1);
        }
        return mmb.getMMObject(path);
    }

    /**
     * Generate a list of values from a command to the processor
     * @javadoc
     */
    public Vector getList(scanpage sp, StringTagger tagger, String value) throws ParseException {
        String line = Strip.DoubleQuote(value, Strip.BOTH);
        StringTokenizer tok = new StringTokenizer(line, "-\n\r");
        if (tok.hasMoreTokens()) {
            String cmd = tok.nextToken();
            if (!checkUserLoggedOn(sp, cmd, false))
                return new Vector();
            if (cmd.equals("APPLICATIONS")) {
                tagger.setValue("ITEMS", "5");
                try {
                    return getApplicationsList();
                } catch (SearchQueryException e) {
                    log.warn(Logging.stackTrace(e));
                }
            }
            if (cmd.equals("BUILDERS")) {
                tagger.setValue("ITEMS", "4");
                return getBuildersList(tok);
            }
            if (cmd.equals("FIELDS")) {
                tagger.setValue("ITEMS", "4");
                return getFields(tok.nextToken());
            }
            if (cmd.equals("MODULEPROPERTIES")) {
                tagger.setValue("ITEMS", "2");
                return getModuleProperties(tok.nextToken());
            }
            if (cmd.equals("ISOGUINAMES")) {
                tagger.setValue("ITEMS", "2");
                return getISOGuiNames(tok.nextToken(), tok.nextToken());
            }
            if (cmd.equals("ISODESCRIPTIONS")) {
                tagger.setValue("ITEMS", "2");
                return getISODescriptions(tok.nextToken(), tok.nextToken());
            }
            if (cmd.equals("MODULES")) {
                tagger.setValue("ITEMS", "4");
                return getModulesList();
            }
            if (cmd.equals("DATABASES")) {
                tagger.setValue("ITEMS", "4");
                return getDatabasesList();
            }
            if (cmd.equals("MULTILEVELCACHEENTRIES")) {
                tagger.setValue("ITEMS", "8");
                return getMultilevelCacheEntries();
            }
            if (cmd.equals("NODECACHEENTRIES")) {
                tagger.setValue("ITEMS", "4");
                return getNodeCacheEntries();
            }
        }
        return null;
    }

    /**
     * @javadoc
     */
    private boolean checkAdmin(scanpage sp, String cmd) {
        return checkUserLoggedOn(sp, cmd, true);
    }

    /**
     * @javadoc
     */
    private boolean checkUserLoggedOn(scanpage sp, String cmd, boolean adminonly) {
        String user = null;
        try {
            user = HttpAuth.getAuthorization(sp.req, sp.res, "www", "Basic");
        } catch (javax.servlet.ServletException e) {}
        boolean authorized = (user != null) && (!adminonly || "admin".equals(user));
        if (!authorized) {
            lastmsg = "Unauthorized access : " + cmd + " by " + user;
            log.info(lastmsg);
        }
        return authorized;
    }

    /**
     * Execute the commands provided in the form values
     * @javadoc
     */
    public boolean process(scanpage sp, Hashtable cmds, Hashtable vars) {
        String cmdline, token;

        for (Enumeration h = cmds.keys(); h.hasMoreElements();) {
            cmdline = (String)h.nextElement();
            if (!checkAdmin(sp, cmdline))
                return false;
            StringTokenizer tok = new StringTokenizer(cmdline, "-\n\r");
            token = tok.nextToken();
            if (token.equals("SERVERRESTART")) {
                String user = (String)cmds.get(cmdline);
                doRestart(user);
            } else if (token.equals("LOAD") && !kioskmode) {
                ApplicationResult result = new ApplicationResult(this);
                String appname = (String)cmds.get(cmdline);
                try {
                    if (installApplication(appname, -1, null, result, new HashSet(), false)) {
                        lastmsg = result.getMessage();
                    } else {
                        lastmsg = "Problem installing application : " + appname + "\n" + result.getMessage();
                    }
                } catch (SearchQueryException e) {
                    log.warn(Logging.stackTrace(e));
                }
                if (vars != null)
                    vars.put("RESULT", lastmsg);
            } else if (token.equals("SAVE")) {
                String appname = (String)cmds.get(cmdline);
                String savepath = (String)vars.get("PATH");
                String goal = (String)vars.get("GOAL");
                log.info("APP=" + appname + " P=" + savepath + " G=" + goal);
                writeApplication(appname, savepath, goal);
            } else if (token.equals("APPTOOL")) {
                String appname = (String)cmds.get(cmdline);
                startAppTool(appname);
            } else if (token.equals("BUILDER")) {
                doBuilderPosts(tok.nextToken(), cmds, vars);
            } else if (token.equals("MODULE")) {
                doModulePosts(tok.nextToken(), cmds, vars);
            } else if (token.equals("MODULESAVE")) {
                if (kioskmode) {
                    log.warn("MMAdmin> refused to write module, am in kiosk mode");
                } else {
                    String modulename = (String)cmds.get(cmdline);
                    String savepath = (String)vars.get("PATH");
                    Module mod = (Module)getModule(modulename);
                    if (mod != null) {
                        try {
                            ModuleWriter moduleOut = new ModuleWriter(mod);
                            moduleOut.setIncludeComments(true);
                            moduleOut.writeToFile(savepath);
                        } catch (Exception e) {
                            log.error(Logging.stackTrace(e));
                            lastmsg =
                                "Writing finished, problems occurred\n\n"
                                    + "Error encountered="
                                    + e.getMessage()
                                    + "\n\n";
                            return false;
                        }
                        lastmsg =
                            "Writing finished, no problems.\n\n"
                                + "A clean copy of "
                                + modulename
                                + ".xml can be found at : "
                                + savepath
                                + "\n\n";
                    }
                }
            } else if (token.equals("BUILDERSAVE")) {
                if (kioskmode) {
                    log.warn("MMAdmin> refused to write builder, am in kiosk mode");
                } else {
                    String buildername = (String)cmds.get(cmdline);
                    String savepath = (String)vars.get("PATH");
                    MMObjectBuilder bul = getMMObject(buildername);
                    if (bul != null) {
                        try {
                            BuilderWriter builderOut = new BuilderWriter(bul);
                            builderOut.setIncludeComments(true);
                            builderOut.setExpandBuilder(false);
                            builderOut.writeToFile(savepath);
                        } catch (Exception e) {
                            log.error(Logging.stackTrace(e));
                            lastmsg =
                                "Writing finished, problems occurred\n\n"
                                    + "Error encountered="
                                    + e.getMessage()
                                    + "\n\n";
                            return false;
                        }
                        lastmsg =
                            "Writing finished, no problems.\n\n"
                                + "A clean copy of "
                                + buildername
                                + ".xml can be found at : "
                                + savepath
                                + "\n\n";
                    }
                }
            }

        }
        return false;
    }

    // basically replaces linefeeds and some characters.
    private String escape(String s) {
        if (s == null) {
            return "";
        } else {
            StringObject obj = new StringObject(s);
            obj.replace("&", "&amp;");
            obj.replace(">", "&gt;");
            obj.replace("<", "&lt;");
            obj.replace("\"", "&quot;");
            obj.replace("\n", "<br />");
            return obj.toString();
        }
    }

    /**
     * Handle a $MOD command
     * @javadoc
     */
    public String replace(scanpage sp, String cmds) {
        if (!checkUserLoggedOn(sp, cmds, false))
            return "";
        StringTokenizer tok = new StringTokenizer(cmds, "-\n\r");
        if (tok.hasMoreTokens()) {
            String cmd = tok.nextToken();
            if (cmd.equals("VERSION")) {
                return "" + getVersion(tok.nextToken());
            } else if (cmd.equals("DESCRIPTION")) {
                return escape(getDescription(tok.nextToken()));
            } else if (cmd.equals("LASTMSG")) {
                // return lastmsg in html-format.
                return escape(lastmsg);
            } else if (cmd.equals("BUILDERVERSION")) {
                return "" + getBuilderVersion(tok.nextToken());
            } else if (cmd.equals("BUILDERCLASSFILE")) {
                return "" + getBuilderClass(tok.nextToken());
            } else if (cmd.equals("BUILDERDESCRIPTION")) {
                return "" + getBuilderDescription(tok.nextToken());
            } else if (cmd.equals("GETDESCRIPTION")) {
                return getDescription(tok.nextToken(), tok.nextToken(), tok.nextToken());
            } else if (cmd.equals("GETGUINAMEVALUE")) {
                return getGuiNameValue(tok.nextToken(), tok.nextToken(), tok.nextToken());
            } else if (cmd.equals("GETBUILDERFIELD")) {
                return getBuilderField(tok.nextToken(), tok.nextToken(), tok.nextToken());
            } else if (cmd.equals("GETMODULEPROPERTY")) {
                return getModuleProperty(tok.nextToken(), tok.nextToken());
            } else if (cmd.equals("MODULEDESCRIPTION")) {
                return "" + getModuleDescription(tok.nextToken());
            } else if (cmd.equals("MODULECLASSFILE")) {
                return "" + getModuleClass(tok.nextToken());
            } else if (cmd.equals("MULTILEVELCACHEHITS")) {
                return ("" + MultilevelCache.getCache().getHits());
            } else if (cmd.equals("MULTILEVELCACHEMISSES")) {
                return ("" + MultilevelCache.getCache().getMisses());
            } else if (cmd.equals("MULTILEVELCACHEREQUESTS")) {
                return ("" + (MultilevelCache.getCache().getHits() + MultilevelCache.getCache().getMisses()));
            } else if (cmd.equals("MULTILEVELCACHEPERFORMANCE")) {
                return ("" + (MultilevelCache.getCache().getRatio() * 100));
            } else if (cmd.equals("MULTILEVELCACHESTATE")) {
                if (tok.hasMoreTokens()) {
                    String state = tok.nextToken();
                    if (state.equalsIgnoreCase("On")) {
                        MultilevelCache.getCache().setActive(true);
                        log.info("turned multilevelcache on");
                    } else if (state.equalsIgnoreCase("Off")) {
                        MultilevelCache.getCache().setActive(false);
                        log.info("turned multilevelcache off");
                    }
                } else {
                    if (MultilevelCache.getCache().isActive()) {
                        return "On";
                    } else {
                        return "Off";
                    }
                }
            } else if (cmd.equals("MULTILEVELCACHESIZE")) {
                return ("" + (MultilevelCache.getCache().getSize()));
            } else if (cmd.equals("NODECACHEHITS")) {
                return ("" + MMObjectBuilder.nodeCache.getHits());
            } else if (cmd.equals("NODECACHEMISSES")) {
                return ("" + MMObjectBuilder.nodeCache.getMisses());
            } else if (cmd.equals("NODECACHEREQUESTS")) {
                return ("" + (MMObjectBuilder.nodeCache.getHits() + MMObjectBuilder.nodeCache.getMisses()));
            } else if (cmd.equals("NODECACHEPERFORMANCE")) {
                return ("" + (MMObjectBuilder.nodeCache.getRatio() * 100));
            } else if (cmd.equals("NODECACHESIZE")) {
                return ("" + (MMObjectBuilder.nodeCache.getSize()));
            } else if (cmd.equals("TEMPORARYNODECACHESIZE")) {
                return ("" + (MMObjectBuilder.TemporaryNodes.size()));
            } else if (cmd.equals("RELATIONCACHEHITS")) {
                return ("" + MMObjectNode.getRelationCacheHits());
            } else if (cmd.equals("RELATIONCACHEMISSES")) {
                return ("" + MMObjectNode.getRelationCacheMiss());
            } else if (cmd.equals("RELATIONCACHEREQUESTS")) {
                return ("" + (MMObjectNode.getRelationCacheHits() + MMObjectNode.getRelationCacheMiss()));
            } else if (cmd.equals("RELATIONCACHEPERFORMANCE")) {

                return (
                    ""
                        + (1.0 * MMObjectNode.getRelationCacheHits())
                            / (MMObjectNode.getRelationCacheHits() + MMObjectNode.getRelationCacheMiss() + 0.0000000001)
                            * 100);
            }
        }
        return "No command defined";
    }

    /**
     * @javadoc
     */
    int getVersion(String appname) {
        String path = MMBaseContext.getConfigPath() + File.separator + "applications" + File.separator;
        XMLApplicationReader app = new XMLApplicationReader(path + appname + ".xml");
        if (app != null) {
            return app.getApplicationVersion();
        }
        return -1;
    }

    /**
     * @javadoc
     */
    int getBuilderVersion(String appname) {
        String path = MMBaseContext.getConfigPath() + File.separator + "builders" + File.separator;
        BuilderReader app = new BuilderReader(path + appname + ".xml", mmb);
        if (app != null) {
            return app.getBuilderVersion();
        }
        return -1;
    }

    /**
     * @javadoc
     */
    String getBuilderClass(String bulname) {
        String path = MMBaseContext.getConfigPath() + File.separator + "builders" + File.separator;
        BuilderReader bul = new BuilderReader(path + bulname + ".xml", mmb);
        if (bul != null) {
            return bul.getClassFile();
        }
        return "";
    }

    /**
     * @javadoc
     */
    String getModuleClass(String modname) {
        String path = MMBaseContext.getConfigPath() + File.separator + "modules" + File.separator;
        XMLModuleReader mod = new XMLModuleReader(path + modname + ".xml");
        if (mod != null) {
            return mod.getClassFile();
        }
        return "";
    }

    /**
     * @javadoc
     */
    public void setModuleProperty(Hashtable vars) {
        if (kioskmode) {
            log.warn("refused module property set, am in kiosk mode");
            return;
        }
        String modname = (String)vars.get("MODULE");
        String key = (String)vars.get("PROPERTYNAME");
        String value = (String)vars.get("VALUE");
        Module mod = (Module)getModule(modname);
        log.debug("MOD=" + mod);
        if (mod != null) {
            mod.setInitParameter(key, value);
            syncModuleXML(mod, modname);
        }

    }

    /**
     * @javadoc
     * @todo should obtain data from the configuration file
     */
    String getModuleProperty(String modname, String key) {
        /*
        String path=MMBaseContext.getConfigPath()+File.separator+"modules"+File.separator;
        XMLModuleReader mod=new XMLModuleReader(path+modname+".xml");
        if (mod!=null) {
            Hashtable props=mod.getProperties();
            String value=(String)props.get(key);
            return value;
        }
         */
        Module mod = (Module)getModule(modname);
        if (mod != null) {
            String value = mod.getInitParameter(key);
            if (value != null)
                return value;
        }
        return "";

    }

    /**
     * @javadoc
     */
    String getDescription(String appname) {
        String path = MMBaseContext.getConfigPath() + File.separator + "applications" + File.separator;
        XMLApplicationReader app = new XMLApplicationReader(path + appname + ".xml");
        if (app != null) {
            return app.getDescription();
        }
        return "";
    }

    /**
     * @javadoc
     */
    String getBuilderDescription(String appname) {
        String path = MMBaseContext.getConfigPath() + File.separator + "builders" + File.separator;
        BuilderReader app = new BuilderReader(path + appname + ".xml", mmb);
        if (app != null) {
            Hashtable desc = app.getDescriptions();
            String english = (String)desc.get("en");
            if (english != null) {
                return english;
            }
        }
        return "";
    }

    /**
     * @javadoc
     */
    String getModuleDescription(String modulename) {
        Module mod = (Module)getModule(modulename);
        if (mod != null) {
            String value = mod.getModuleInfo();
            if (value != null)
                return value;
        }
        return "";
    }

    /**
     * @javadoc
     */
    public void maintainance() {}

    /**
     * @javadoc
     * @bad-literal time for MMAdminProbe should be a constant or configurable
     */
    public void doRestart(String user) {
        if (kioskmode) {
            log.warn("MMAdmin> refused to reset the server, am in kiosk mode");
            return;
        }
        lastmsg = "Server Reset requested by '" + user + "' Restart in 3 seconds\n\n";
        log.info("Server Reset requested by '" + user + "' Restart in 3 seconds");
        restartwanted = true;
        probe = new MMAdminProbe(this, 3 * 1000);
    }

    /**
     * @javadoc
     */
    private boolean startAppTool(String appname) {
        if (kioskmode) {
            log.warn("refused starting app tool, am in kiosk mode");
            return false;
        }

        String path = MMBaseContext.getConfigPath() + File.separator + "applications" + File.separator;
        log.info("Starting apptool with : " + path + File.separator + appname + ".xml");
        MMAppTool app = new MMAppTool(path + File.separator + appname + ".xml");
        lastmsg = "Started a instance of the MMAppTool with path : \n\n";
        lastmsg += path + File.separator + appname + ".xml\n\n";
        return true;
    }

    /**
     * Installs the application
     * @param applicationName Name of the application file, without the xml extension
     *                        This is also assumed to be the name of teh application itself
     *                        (if not, a warning will be issued)
     * @param result the result object, containing error messages when the installation fails,
     * or the installnotice if succesfull or already installed
     * @param installationSet set of installations that are currently being installed.
     *                        used to check if there are circular dependencies
     * @param autoDeploy if true, the installation is only installed if the application is set to autodeploy
     * @return true if succesfull, false otherwise
     */
    private boolean installApplication(
        String applicationName,
        int requiredVersion,
        String requiredMaintainer,
        ApplicationResult result,
        Set installationSet,
        boolean autoDeploy)
        throws SearchQueryException {
        if (installationSet.contains(applicationName)) {
            return result.error("Circular reference to application with name " + applicationName);
        }
        String path = MMBaseContext.getConfigPath() + File.separator + "applications" + File.separator;
        XMLApplicationReader app = new XMLApplicationReader(path + applicationName + ".xml");
        Versions ver = (Versions)mmb.getMMObject("versions");
        if (app != null) {
            // test autodeploy
            if (autoDeploy && !app.getApplicationAutoDeploy()) {
                return true;
            }
            String name = app.getApplicationName();
            String maintainer = app.getApplicationMaintainer();
            if (requiredMaintainer != null && !maintainer.equals(requiredMaintainer)) {
                return result.error("Install error: " + name + " requires maintainer '" + requiredMaintainer +
                                    "' but found maintainer '" + maintainer + "'");
            }
            int version = app.getApplicationVersion();
            if (requiredVersion != -1 && version != requiredVersion) {
                return result.error("Install error: " + name + " requires version '" + requiredVersion +
                                    "' but found version '" + version + "'");
            }
            int installedVersion = ver.getInstalledVersion(name, "application");
            if (installedVersion == -1 || version > installedVersion) {
                if (!name.equals(applicationName)) {
                    result.warn("Application name " + name + " not the same as the base filename " + applicationName + ".\n"
                                + "This may cause problems when referring to this application.");
                }
                // We should possibly check whether the maintainer is valid here (see sample code below).
                // There is currently no way to do this, though, unless we use awful queries.
                // what we need is a getInstalledMaintainer() method on the Versions builder
                /* sample code
                String installedMaintainer=ver.getInstalledMaintainer(name,"application");
                if (!maintainer.equals(installedAppMaintainer)) {
                    return result.error("Install error: "+name+" is of maintainer '"+maintainer+"' but installed application is of maintainer '"+installedMaintainer+"'");
                }
                 */
                // should be installed - add to installation set
                installationSet.add(applicationName);
                List requires = app.getRequirements();
                for (Iterator i = requires.iterator(); i.hasNext();) {
                    Map reqapp = (Map)i.next();
                    String reqType = (String)reqapp.get("type");
                    if (reqType == null || reqType.equals("application")) {
                        String appName = (String)reqapp.get("name");
                        int installedAppVersion = ver.getInstalledVersion(appName, "application");
                        String appMaintainer = (String)reqapp.get("maintainer");
                        int appVersion = -1;
                        try {
                            String appVersionAttr = (String)reqapp.get("version");
                            if (appVersionAttr != null)
                                appVersion = Integer.parseInt(appVersionAttr);
                        } catch (Exception e) {}
                        if (installedAppVersion == -1 || appVersion > installedAppVersion) {
                            log.service("Application '" + applicationName + "' requires : " + appName);
                            if (!installApplication(appName, appVersion, appMaintainer,
                                result, installationSet, false)) {
                                return false;
                            }
                        } else if (appMaintainer != null) {
                            // we should possibly check whether the maintainer is valid here (see sample code below).
                            // There is currently no way to do this, though, unless we use awful queries.
                            // what we need is a getInstalledMaintainer() method on the Versions builder
                            /* sample code
                            String installedAppMaintainer=ver.getInstalledMaintainer(name,"application");
                            if (!appMaintainer.equals(installedAppMaintainer)) {
                                return result.error("Install error: "+name+" requires maintainer '"+appMaintainer+"' but found maintainer '"+installedAppMaintainer+"'");
                            }
                             */
                        }
                    }
                }
                // note: currently name and application file name should be the same
                if (installedVersion == -1) {
                    log.info("Installing application : " + name);
                } else {
                    log.info("installing application : " + name
                            + " new version from " + installedVersion + " to " + version);
                }
                if (installBuilders(app.getNeededBuilders(), path + applicationName, result)
                    && installRelDefs(app.getNeededRelDefs(), result)
                    && installAllowedRelations(app.getAllowedRelations(), result)
                    && installDataSources(app.getDataSources(), applicationName, result)
                    && installRelationSources(app.getRelationSources(), applicationName, result)) {
                    if (installedVersion == -1) {
                        ver.setInstalledVersion(name, "application", maintainer, version);
                    } else {
                        ver.updateInstalledVersion(name, "application", maintainer, version);
                    }
                    log.info("Application '" + name + "' deployed succesfully.");
                    result.success(
                        "Application loaded oke\n\n"
                            + "The application has the following install notice for you : \n\n"
                            + app.getInstallNotice());
                }
                // installed or failed - remove from installation set
                installationSet.remove(applicationName);
            } else {
                // only return this message if the application is the main (first) application
                // and if it was not auto-deployed (as in that case messages would not be deemed very useful)
                if (installationSet.size() == 1) {
                    result.success(
                        "Application was allready loaded (or a higher version)\n\n"
                            + "To remind you here is the install notice for you again : \n\n"
                            + app.getInstallNotice());
                }
            }
        } else {
            result.error("Install error: can't find xml file: " + path + applicationName + ".xml");
        }
        return result.isSuccess();
    }

    /**
     * @javadoc
     */
    protected boolean installDataSources(Vector ds, String appname, ApplicationResult result) {
        MMObjectBuilder syncbul = mmb.getMMObject("syncnodes");

        List nodeFieldNodes = new ArrayList(); // a temporary list with all nodes that have NODE fields, which should be synced, later.
        if (syncbul != null) {
            for (Enumeration h = ds.elements(); h.hasMoreElements();) {
                Hashtable bh = (Hashtable)h.nextElement();
                String path = (String)bh.get("path");
                String prepath = MMBaseContext.getConfigPath() + File.separator + "applications" + File.separator;

                if (fileExists(prepath + path)) {
                    XMLNodeReader nodereader = new XMLNodeReader(prepath + path, prepath + appname + File.separator, mmb);
                    String exportsource = nodereader.getExportSource();
                    int timestamp = nodereader.getTimeStamp();

                    // loop all nodes , and add to syncnodes.
                    for (Enumeration n = nodereader.getNodes(mmb).elements(); n.hasMoreElements();) {
                        MMObjectNode newNode = (MMObjectNode)n.nextElement();

                        int exportnumber = newNode.getIntValue("number");
                        String query = "exportnumber==" + exportnumber + "+exportsource=='" + exportsource + "'";
                        Enumeration b = syncbul.search(query);
                        if (b.hasMoreElements()) {
                            // XXX To do : we may want to load the node and check/change the fields
                            MMObjectNode syncnode = (MMObjectNode)b.nextElement();
                            log.debug("node allready installed : " + exportnumber);
                        } else {
                            newNode.setValue("number", -1);
                            int localnumber = doKeyMergeNode(syncbul, newNode, exportsource, result);
                            if (localnumber != -1) { // this node was not yet imported earlier
                                MMObjectNode syncnode = syncbul.getNewNode("import");
                                syncnode.setValue("exportsource", exportsource);
                                syncnode.setValue("exportnumber", exportnumber);
                                syncnode.setValue("timestamp", timestamp);
                                syncnode.setValue("localnumber", localnumber);
                                syncnode.insert("import");

                                if (localnumber == newNode.getNumber()) {
                                    // && (newNode.parent instanceof Message)) { terrible stuff

                                    // determine if there were NODE fields, which need special treatment later.
                                    List fields = newNode.parent.getFields();
                                    Iterator i = fields.iterator();
                                    while (i.hasNext()) {
                                        FieldDefs def = (FieldDefs) i.next();

                                        // Fields with type NODE and notnull=true will be handled
                                        // by the doKeyMergeNode() method.
                                        if (def.getDBType() == FieldDefs.TYPE_NODE
                                            && ! def.getDBName().equals("number")
                                            && ! def.getDBNotNull()) {

                                            newNode.values.put("__exportsource", exportsource);
                                            nodeFieldNodes.add(newNode);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            treatNodeFields(nodeFieldNodes, syncbul);

            return result.isSuccess();
        } else {
            return result.error("Application installer : can't reach syncnodes builder"); //
        }
    }

    private void treatNodeFields(List nodeFieldNodes, MMObjectBuilder syncbul) {
        Iterator i = nodeFieldNodes.iterator();
        while (i.hasNext()) {
            MMObjectNode importedNode = (MMObjectNode) i.next();
            String exportsource = (String) importedNode.values.get("__exportsource");
            // clean it up
            importedNode.values.remove("__exportsource");

            List fields = importedNode.parent.getFields();
            Iterator j = fields.iterator();
            while (j.hasNext()) {
                FieldDefs def = (FieldDefs) j.next();
                if (def.getDBType() == FieldDefs.TYPE_NODE &&
                    !def.getDBName().equals("number") &&
                    !def.getDBName().equals("snumber") &&
                    !def.getDBName().equals("dnumber") &&
                    !def.getDBName().equals("rnumber")
                   ) {
                    String fieldName = def.getDBName();

                    updateFieldWithTypeNode(
                       syncbul,
                       importedNode,
                       exportsource,
                       fieldName);
                }
            }
            if (importedNode.isChanged()) {
                importedNode.commit();
            }
        }
    }

    /**
     * @javadoc
     */
    private int doKeyMergeNode(MMObjectBuilder syncbul, MMObjectNode newnode, String exportsource, ApplicationResult result) {
        MMObjectBuilder bul = newnode.parent;
        if (bul != null) {
            String checkQ = "";
            Vector vec = bul.getFields();
            Constraint constraint = null;
            NodeSearchQuery query = null;
            for (Enumeration h = vec.elements(); h.hasMoreElements();) {
                FieldDefs def = (FieldDefs)h.nextElement();
                // check for notnull fields with type NODE.
                if (def.getDBType() == FieldDefs.TYPE_NODE
                    && ! def.getDBName().equals("number")
                    && def.getDBNotNull()) {

                    // Dangerous territory here.
                    // The node contains a reference to another node.
                    // The referenced node has to exist when this node is inserted.
                    // trying to update the node.
                    updateFieldWithTypeNode(syncbul,
                                            newnode,
                                            exportsource,
                                            def.getDBName());
                    if (newnode.getIntValue(def.getDBName()) == -1) {
                       // guess that failed
                       result.error("Insert of node " + newnode + " failed. A field with type NODE is not allowed to have a null value. " +
                                    "The referenced node is not found. Try to reorder the nodes so the referenced node is imported before this one.");
                       return -1;
                    }
                }

                // generation of key constraint to check if there is a node already present.
                // if a node is present then we can't insert this one.
                if (def.isKey()) {
                    int type = def.getDBType();
                    String name = def.getDBName();
                    if (type == FieldDefs.TYPE_STRING) {
                        String value = newnode.getStringValue(name);
                        if (query==null) {
                            query = new NodeSearchQuery(bul);
                        }
                        StepField field = query.getField(def);
                        Constraint newConstraint = new BasicFieldValueConstraint(field, value);
                        if (constraint==null) {
                            constraint= newConstraint;
                        } else {
                            BasicCompositeConstraint compConstraint = new BasicCompositeConstraint(CompositeConstraint.LOGICAL_AND);
                            compConstraint.addChild(constraint);
                            compConstraint.addChild(newConstraint);
                            constraint = compConstraint;
                        }
                    }
                }
            }
            if (query!=null && constraint !=null) {
                query.setConstraint(constraint);
                try {
                    List nodes = bul.getNodes(query);
                    if (nodes.size()>0) {
                        MMObjectNode oldnode = (MMObjectNode)nodes.get(0);
                        return oldnode.getIntValue("number");
                    }
                } catch (SearchQueryException sqe) {
                    result.error("Application installer can't search builder storage (" + sqe.getMessage()+")");
                    return -1;
                }
            }

            int localnumber = newnode.insert("import");
            if (localnumber == -1) {
                result.error("Insert of node " + newnode + " failed.");
            }
            return localnumber;

        } else {
            result.error("Application installer can't find builder for : " + newnode);
            return -1;
        }
    }

   /** update the field with the real node number of the referenced node
    *
    * @param syncbul syncnode builder
    * @param importedNode Node to update
    * @param exportsource export source of the node to update
    * @param fieldname name of the field
    */
   private void updateFieldWithTypeNode(
      MMObjectBuilder syncbul,
      MMObjectNode importedNode,
      String exportsource,
      String fieldname) {

      int exportnumber;
      try {
          exportnumber = Integer.parseInt((String) importedNode.values.get("__" + fieldname));
      } catch (Exception e) {
          exportnumber = -1;
      }

      // clean it up (don't know if this is necessary, but don't risk anything!)
      importedNode.values.remove("__" + fieldname);

      int localNumber = -1;
      String query = "where exportnumber=" + exportnumber + " and exportsource='" + exportsource + "'";
      Enumeration b = syncbul.search(query);
      if (b.hasMoreElements()) {
          MMObjectNode n2 = (MMObjectNode) b.nextElement();
          localNumber = n2.getIntValue("localnumber");
      }
      importedNode.setValue(fieldname, localNumber);
   }

   /**
     * @javadoc
     */
    boolean installRelationSources(Vector ds, String appname, ApplicationResult result) {
        MMObjectBuilder syncbul = mmb.getMMObject("syncnodes");
        if (syncbul != null) {
            List nodeFieldNodes = new ArrayList(); // a temporary list with all nodes that have NODE fields, which should be synced, later.
            for (Enumeration h = ds.elements(); h.hasMoreElements();) {
                Hashtable bh = (Hashtable)h.nextElement();
                String path = (String)bh.get("path");
                String prepath = MMBaseContext.getConfigPath() + File.separator + "applications" + File.separator;
                if (fileExists(prepath + path)) {
                    XMLRelationNodeReader nodereader = new XMLRelationNodeReader(prepath+ path, prepath + appname + File.separator, mmb);

                    String exportsource = nodereader.getExportSource();
                    int timestamp = nodereader.getTimeStamp();

                    for (Enumeration n = (nodereader.getNodes(mmb)).elements(); n.hasMoreElements();) {
                        MMObjectNode newnode = (MMObjectNode)n.nextElement();
                        int exportnumber = newnode.getIntValue("number");
                        Enumeration b =
                            syncbul.search("exportnumber==" + exportnumber + "+exportsource=='" + exportsource + "'");
                        if (b.hasMoreElements()) {
                            // XXX To do : we may want to load the relation node and check/change the fields
                            MMObjectNode syncnode = (MMObjectNode)b.nextElement();
                            log.debug("node allready installed : " + exportnumber);
                        } else {
                            newnode.setValue("number", -1);
                            // The following code determines the 'actual' (synced) numbers for the destination and source nodes
                            // This will normally work well, however:
                            // It is _theoretically_ possible that one or both nodes are _themselves_ relation nodes.
                            // (since relations are nodes).
                            // Due to the order in which syncing takles place, it is possible that such structures will fail
                            // to get imported.
                            // ye be warned.

                            // find snumber
                            int snumber = newnode.getIntValue("snumber");
                            b = syncbul.search("exportnumber==" + snumber + "+exportsource=='" + exportsource + "'");
                            if (b.hasMoreElements()) {
                                MMObjectNode n2 = (MMObjectNode)b.nextElement();
                                snumber = n2.getIntValue("localnumber");
                            } else {
                                snumber = -1;
                            }

                            // find dnumber
                            int dnumber = newnode.getIntValue("dnumber");
                            b = syncbul.search("exportnumber==" + dnumber + "+exportsource=='" + exportsource + "'");
                            if (b.hasMoreElements()) {
                                MMObjectNode n2 = (MMObjectNode)b.nextElement();
                                dnumber = n2.getIntValue("localnumber");
                            } else {
                                dnumber = -1;
                            }

                            newnode.setValue("snumber", snumber);
                            newnode.setValue("dnumber", dnumber);
                            int localnumber = -1;
                            if (snumber != -1 && dnumber != -1) {
                                // localnumber = doKeyMergeNode(syncbul, newnode, exportsource, result);
                                localnumber = newnode.insert("import");
                                if (localnumber != -1) {
                                    MMObjectNode syncnode = syncbul.getNewNode("import");
                                    syncnode.setValue("exportsource", exportsource);
                                    syncnode.setValue("exportnumber", exportnumber);
                                    syncnode.setValue("timestamp", timestamp);
                                    syncnode.setValue("localnumber", localnumber);
                                    syncnode.insert("import");
                                    if (localnumber == newnode.getNumber()) {

                                        // determine if there were NODE fields, which need special treatment later.
                                        List fields = newnode.parent.getFields();
                                        Iterator i = fields.iterator();
                                        while (i.hasNext()) {
                                            FieldDefs def = (FieldDefs) i.next();

                                            // Fields with type NODE and notnull=true will be handled
                                            // by the doKeyMergeNode() method.
                                            if (def.getDBType() == FieldDefs.TYPE_NODE
                                                && ! def.getDBName().equals("number")
                                                && ! def.getDBNotNull()) {

                                                newnode.values.put("__exportsource", exportsource);
                                                nodeFieldNodes.add(newnode);
                                                break;
                                            }
                                        }
                                    }
                                }
                            } else {
                                result.error("Cannot sync relation (exportnumber==" + exportnumber
                                        + ", snumber:" + snumber + ", dnumber:" + dnumber + ")");
                            }

                        }
                    }
                }
            }
            treatNodeFields(nodeFieldNodes,syncbul);
        } else {
            result.error("Application installer : can't reach syncnodes builder");
        }
        return result.isSuccess();
    }

    /**
     * Checks and if required installs needed relation definitions.
     * Retrieves, for each reldef entry, the attributes, and passes these on to {@link #installRelDef}
     * @param reldefs a list of hashtables. Each hashtable represents a reldef entry, and contains a list of name-value
     *      pairs (the reldef attributes).
     * @return Always <code>true</code> (?)
     */
    boolean installRelDefs(Vector reldefs, ApplicationResult result) {
        for (Enumeration h = reldefs.elements(); h.hasMoreElements();) {
            Hashtable bh = (Hashtable)h.nextElement();
            String source = (String)bh.get("source");
            String target = (String)bh.get("target");
            String direction = (String)bh.get("direction");
            String guisourcename = (String)bh.get("guisourcename");
            String guitargetname = (String)bh.get("guitargetname");
            // retrieve builder info
            int builder = -1;
            if (mmb.getRelDef().usesbuilder) {
                String buildername = (String)bh.get("builder");
                // if no 'builder' attribute is present (old format), use source name as builder name
                if (buildername == null) {
                    buildername = (String)bh.get("source");
                }
                builder = mmb.getTypeDef().getIntValue(buildername);
            }
            // is not explicitly set to unidirectional, direction is assumed to be bidirectional
            if ("unidirectional".equals(direction)) {
                if (!installRelDef(source, target, 1, guisourcename, guitargetname, builder, result))
                    return false;
            } else {
                if (!installRelDef(source, target, 2, guisourcename, guitargetname, builder, result))
                    return false;
            }
        }
        return true;
    }

    /**
     * Checks and if required installs needed allowed type relations.
     * Retrieves, for each allowed relation entry, the attributes, and passes these on to {@link #installTypeRel}
     * @param relations a list of hashtables. Each hashtable represents a allowedrelation entry, and contains a list of name-value
     *      pairs (the allowed relation attributes).
     * @return <code>true</code> if succesfull, <code>false</code> if an error occurred
     */
    boolean installAllowedRelations(Vector relations, ApplicationResult result) {
        for (Enumeration h = relations.elements(); h.hasMoreElements();) {
            Hashtable bh = (Hashtable)h.nextElement();
            String from = (String)bh.get("from");
            String to = (String)bh.get("to");
            String type = (String)bh.get("type");
            if (!installTypeRel(from, to, type, -1, result))
                return false;
        }
        return true;
    }

    /**
     * Lists the required builders for this application, and makes attempts to install any builders that are
     * not present.
     * If there is a failure, the function returns false.
     * Failure messages are stored in the lastmsg member.
     * @param neededbuilders a list with builder data that need be installed on teh system for this application to work
     *                       each element in teh list is a Map containing builder properties (in particular, 'name').
     * @param applicationroot the rootpath where the application's configuration files are located
     * @return true if the builders were succesfully installed, false if the installation failed
     */
    boolean installBuilders(List neededbuilders, String applicationRoot, ApplicationResult result) {
        for (Iterator i = neededbuilders.iterator(); i.hasNext();) {
            Map builderdata = (Map)i.next();
            String name = (String)builderdata.get("name");
            MMObjectBuilder bul = getMMObject(name);
            // if builder not loaded
            if (bul == null) {
                // if 'inactive' in the config/builder path, fail
                String path = mmb.getBuilderPath(name, "");
                if (path != null) {
                    result.error(
                        "The builder '"
                            + name
                            + "' was already on our system, but inactive."
                            + "To install this application, make the builder '"
                            + path
                            + name
                            + ".xml"
                            + "' active");
                    continue;
                }
                // attempt to open the application root
                File appFile = new File(applicationRoot);
                if (!appFile.exists()) {
                    return result.error("Could not find application directory :  '" + appFile + "'");
                }
                // attempt to open the %application%/builders/ folder
                appFile = new File(appFile.getAbsolutePath() + java.io.File.separator + "builders");
                if (!appFile.exists()) {
                    return result.error(
                        "Could not find the 'builders' folder inside the application directory  '" + appFile + "'");
                }
                // attempt to open the applicationfile
                appFile = new File(appFile.getAbsolutePath() + java.io.File.separator + name + ".xml");
                if (!appFile.exists()) {
                    result.error("Could not find the builderfile :  '" + appFile + "'(builder '" + name + "')");
                    continue;
                }
                // check the presence of typedef (if not present, fail)
                MMObjectBuilder objectTypes = mmb.getTypeDef();
                if (objectTypes == null) {
                    return result.error("Could not find the typedef builder.");
                }
                // try to add a node to typedef, same as adding a builder...
                MMObjectNode type = objectTypes.getNewNode("system");
                // fill the name....
                type.setValue("name", name);

                // fill the config...
                org.w3c.dom.Document config = null;
                try {
                    config =
                        XMLBasicReader.getDocumentBuilder(BuilderReader.class).parse(appFile);
                } catch (org.xml.sax.SAXException se) {
                    String msg = "builder '" + name + "':\n" + se.toString() + "\n" + Logging.stackTrace(se);
                    log.error(msg);
                    result.error("A XML parsing error occurred (" + se.toString() + "). Check the log for details.");
                    continue;
                } catch (java.io.IOException ioe) {
                    String msg = "builder '" + name + "':\n" + ioe.toString() + "\n" + Logging.stackTrace(ioe);
                    log.error(msg);
                    result.error("A file I/O error occurred (" + ioe.toString() + "). Check the log for details.");
                    continue;
                }
                type.setValue("config", config);
                // insert into mmbase
                objectTypes.insert("system", type);
                // we now made the builder active.. look for other builders...
            }
        }
        return result.isSuccess();
    }

    /**
     * Checks whether a given relation definition exists, and if not, creates that definition.
     * @param sname source name of the relation definition
     * @param dname destination name of the relation definition
     * @param dir directionality (uni or bi)
     * @param sguiname source GUI name of the relation definition
     * @param dguiname destination GUI name of the relation definition
     * @param builder references the builder to use (only in new format)
     * @return <code>true</code> if succesfull, <code>false</code> if an error occurred
     */
    private boolean installRelDef(
        String sname,
        String dname,
        int dir,
        String sguiname,
        String dguiname,
        int builder,
        ApplicationResult result) {
        RelDef reldef = mmb.getRelDef();
        if (reldef != null) {
            if (reldef.getNumberByName(sname + "/" + dname) == -1) {
                MMObjectNode node = reldef.getNewNode("system");
                node.setValue("sname", sname);
                node.setValue("dname", dname);
                node.setValue("dir", dir);
                node.setValue("sguiname", sguiname);
                node.setValue("dguiname", dguiname);
                if (reldef.usesbuilder) {
                    // if builder is unknown (falsely specified), use the InsRel builder
                    if (builder <= 0) {
                        builder = mmb.getInsRel().oType;
                    }
                    node.setValue("builder", builder);
                }
                int id = reldef.insert("system", node);
                if (id != -1) {
                    log.debug("RefDef (" + sname + "," + dname + ") installed");
                } else {
                    return result.error("RelDef (" + sname + "," + dname + ") could not be installed");
                }
            }
        } else {
            return result.error("Can't get reldef builder");
        }
        return true;
    }

    /**
     * Checks and if required installs an allowed type relation (typerel object).
     * @param sname source type name of the type relation
     * @param dname destination type name of the type relation
     * @param rname role name of the type relation
     * @param count cardinality of the type relation
     * @return <code>true</code> if succesfull, <code>false</code> if an error occurred
     */
    private boolean installTypeRel(String sname, String dname, String rname, int count, ApplicationResult result) {
        TypeRel typerel = mmb.getTypeRel();
        if (typerel != null) {
            TypeDef typedef = mmb.getTypeDef();
            if (typedef == null) {
                return result.error("Can't get typedef builder");
            }
            RelDef reldef = mmb.getRelDef();
            if (reldef == null) {
                return result.error("Can't get reldef builder");
            }

            // figure out rnumber
            int rnumber = reldef.getNumberByName(rname);
            if (rnumber == -1) {
                return result.error("No reldef with role '" + rname + "' defined");
            }

            // figure out snumber
            int snumber = typedef.getIntValue(sname);
            if (snumber == -1) {
                return result.error("No builder with name '" + sname + "' defined");
            }

            // figure out dnumber
            int dnumber = typedef.getIntValue(dname);
            if (dnumber == -1) {
                return result.error("No builder with name '" + dname + "' defined");
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
                    return result.error("TypeRel (" + sname + "," + dname + "," + rname + ") could not be installed");
                }
            }
            return true;
        } else {
            return result.error("Can't get typerel builder");
        }
    }

    /**
     * @javadoc
     * @deprecated-now not used (?)
     */
    /*
    private void checkRelation(int snumber, int dnumber, String rname, int dir) {
        InsRel insrel = mmb.getInsRel();
        if (insrel != null) {
            RelDef reldef = mmb.getRelDef();
            if (reldef == null) {
                log.warn("can't get reldef builder");
            }
            // figure out rnumber
            int rnumber = reldef.getNumberByName(rname);
            if (rnumber == -1) {
                log.warn("no reldef : " + rname + " defined");
                return;
            }

            MMObjectNode node = insrel.getRelation(snumber, dnumber, rnumber);
            if (node == null) {
                node = insrel.getNewNode("system");
                node.setValue("snumber", snumber);
                node.setValue("dnumber", dnumber);
                node.setValue("rnumber", rnumber);
                if (insrel.usesdir) {
                    if (dir <= 0) {
                        // have to get dir value form reldef
                        MMObjectNode relnode = reldef.getNode(rnumber);
                        dir = relnode.getIntValue("dir");
                    }
                    // correct if value is invalid
                    if (dir <= 0)
                        dir = 2;
                    node.setValue("dir", dir);
                }
                int id = insrel.insert("system", node);
            }
        } else {
            log.warn("can't get insrel builder");
        }
    }
    */

    /**
     * @javadoc
     */
    public void probeCall() throws SearchQueryException {

        if (restartwanted) {
            System.exit(0);
        }
        Versions ver = (Versions)mmb.getMMObject("versions");
        if (ver == null) {
            log.warn("Versions builder not installed, Can't auto deploy apps");
            return;
        }
        String path = MMBaseContext.getConfigPath() + File.separator + "applications" + File.separator;
        // new code checks all the *.xml files in builder dir
        File bdir = new File(path);
        if (bdir.isDirectory()) {
            String files[] = bdir.list();
            if (files == null)
                return;
            for (int i = 0; i < files.length; i++) {
                String aname = files[i];
                if (aname.endsWith(".xml")) {
                    ApplicationResult result = new ApplicationResult(this);
                    if (!installApplication(aname.substring(0, aname.length() - 4),
                        -1,
                        null,
                        result,
                        new HashSet(),
                        true)) {
                        log.error("Problem installing application : " + aname);
                    }
                }
            }
        }
        state = true;
    }

    /**
     * Wether MMAdmin module was completely initialized (applications auto-deployed and so on).
     * @since MMBase-1.7
     */

    public boolean getState() {
        return state;
    }

    /**
     * @javadoc
     */
    private boolean writeApplication(String appname, String targetpath, String goal) {
        if (kioskmode) {
            log.warn("refused to write application, am in kiosk mode");
            return false;
        }
        String path = MMBaseContext.getConfigPath() + File.separator + "applications" + File.separator;
        XMLApplicationReader app = new XMLApplicationReader(path + appname + ".xml");
        Vector savestats = XMLApplicationWriter.writeXMLFile(app, targetpath, goal, mmb);
        lastmsg = "Application saved oke\n\n";
        lastmsg += "Some statistics on the save : \n\n";
        for (Enumeration h = savestats.elements(); h.hasMoreElements();) {
            String result = (String)h.nextElement();
            lastmsg += result + "\n\n";
        }
        return true;
    }

    /**
     * @javadoc
     */
    Vector getApplicationsList() throws SearchQueryException {
        Versions ver = (Versions)mmb.getMMObject("versions");
        if (ver == null) {
            log.warn("Versions builder not installed, Can't get to apps");
            return null;
        }
        Vector results = new Vector();

        String path = MMBaseContext.getConfigPath() + File.separator + "applications" + File.separator;
        // new code checks all the *.xml files in builder dir
        File bdir = new File(path);
        if (bdir.isDirectory()) {
            String files[] = bdir.list();
            if (files == null)
                return results;
            for (int i = 0; i < files.length; i++) {
                String aname = files[i];
                if (aname.endsWith(".xml")) {
                    XMLApplicationReader app = new XMLApplicationReader(path + aname);
                    String name = app.getApplicationName();
                    results.addElement(name);
                    results.addElement("" + app.getApplicationVersion());
                    int installedversion = ver.getInstalledVersion(name, "application");
                    if (installedversion == -1) {
                        results.addElement("no");
                    } else {
                        results.addElement("yes (ver : " + installedversion + ")");
                    }
                    results.addElement(app.getApplicationMaintainer());
                    boolean autodeploy = app.getApplicationAutoDeploy();
                    if (autodeploy) {
                        results.addElement("yes");
                    } else {
                        results.addElement("no");
                    }
                }
            }
        }
        return results;
    }

    /**
     * @javadoc
     */
    Vector getBuildersList() {
        return getBuildersList(null);
    }

    /**
     * @javadoc
     */
    Vector getBuildersList(StringTokenizer tok) {
        String subpath = "";
        if ((tok != null) && (tok.hasMoreTokens())) {
            subpath = tok.nextToken();
        }
        Versions ver = (Versions)mmb.getMMObject("versions");
        if (ver == null) {
            log.warn("Versions builder not installed, Can't get to builders");
            return null;
        }
        String path = MMBaseContext.getConfigPath() + File.separator + "builders" + File.separator;
        return getBuildersList(path, subpath, ver);
    }

    /**
     * @javadoc
     */
    Vector getBuildersList(String configpath, String subpath, Versions ver) {
        Vector results = new Vector();
        File bdir = new File(configpath + subpath);
        if (bdir.isDirectory()) {
            if (!"".equals(subpath)) {
                subpath = subpath + File.separator;
            }
            String files[] = bdir.list();
            if (files == null)
                return results;
            for (int i = 0; i < files.length; i++) {
                String aname = files[i];
                if (aname.endsWith(".xml")) {
                    String name = aname;
                    String sname = name.substring(0, name.length() - 4);
                    BuilderReader app = new BuilderReader(configpath + subpath + aname, mmb);
                    results.addElement(subpath + sname);
                    results.addElement("" + app.getBuilderVersion());
                    int installedversion = -1;
                    try {
                        installedversion = ver.getInstalledVersion(sname, "builder");
                    } catch (SearchQueryException e) {
                        log.warn(Logging.stackTrace(e));
                    }
                    if (installedversion == -1) {
                        results.addElement("no");
                    } else {
                        results.addElement("yes");
                    }
                    results.addElement(app.getBuilderMaintainer());
                } else {
                    results.addAll(getBuildersList(configpath, subpath + aname, ver));
                }
            }
        }
        return results;
    }

    /**
     * @javadoc
     */
    Vector getModuleProperties(String modulename) {
        Vector results = new Vector();
        String path = MMBaseContext.getConfigPath() + File.separator + "modules" + File.separator;
        XMLModuleReader mod = new XMLModuleReader(path + modulename + ".xml");
        if (mod != null) {
            Hashtable props = mod.getProperties();
            for (Enumeration h = props.keys(); h.hasMoreElements();) {
                String key = (String)h.nextElement();
                String value = (String)props.get(key);
                results.addElement(key);
                results.addElement(value);
            }

        }
        return results;
    }

    /**
     * @javadoc
     */
    Vector getFields(String buildername) {
        Vector results = new Vector();
        String path = MMBaseContext.getConfigPath() + File.separator + "builders" + File.separator;
        BuilderReader bul = new BuilderReader(path + buildername + ".xml", mmb);
        if (bul != null) {
            Vector defs = bul.getFieldDefs();
            for (Enumeration h = defs.elements(); h.hasMoreElements();) {
                FieldDefs def = (FieldDefs)h.nextElement();
                results.addElement("" + def.getDBPos());
                results.addElement("" + def.getDBName());
                results.addElement(def.getDBTypeDescription());
                int size = def.getDBSize();
                if (size == -1) {
                    results.addElement("fixed");
                } else {
                    results.addElement("" + size);
                }
            }

        }
        return results;
    }

    /**
     * @javadoc
     */
    Vector getModulesList() {
        Vector results = new Vector();

        String path = MMBaseContext.getConfigPath() + File.separator + "modules" + File.separator;
        // new code checks all the *.xml files in builder dir
        File bdir = new File(path);
        if (bdir.isDirectory()) {
            String files[] = bdir.list();
            if (files == null)
                return results;
            for (int i = 0; i < files.length; i++) {
                String aname = files[i];
                if (aname.endsWith(".xml")) {
                    String name = aname;
                    String sname = name.substring(0, name.length() - 4);
                    XMLModuleReader app = new XMLModuleReader(path + aname);
                    results.addElement(sname);

                    results.addElement("" + app.getModuleVersion());
                    String status = app.getStatus();
                    if (status.equals("active")) {
                        results.addElement("yes");
                    } else {
                        results.addElement("no");
                    }
                    results.addElement(app.getModuleMaintainer());
                }
            }
        }
        return results;
    }

    /**
     * @javadoc
     */
    Vector getDatabasesList() {
        Versions ver = (Versions)mmb.getMMObject("versions");
        if (ver == null) {
            log.warn("Versions builder not installed, Can't get to builders");
            return null;
        }
        Vector results = new Vector();

        String path = MMBaseContext.getConfigPath() + File.separator + "databases" + File.separator;
        // new code checks all the *.xml files in builder dir
        File bdir = new File(path);
        if (bdir.isDirectory()) {
            String files[] = bdir.list();
            if (files == null)
                return results;
            for (int i = 0; i < files.length; i++) {
                String aname = files[i];
                if (aname.endsWith(".xml")) {
                    String name = aname;
                    String sname = name.substring(0, name.length() - 4);
                    results.addElement(sname);

                    results.addElement("0");
                    results.addElement("yes");
                    results.addElement("mmbase.org");
                }
            }
        }
        return results;
    }

    /**
     * @javadoc
     */
    private boolean fileExists(String path) {
        File f = new File(path);
        if (f.exists() && f.isFile()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @javadoc
     */
    private String getBuilderField(String buildername, String fieldname, String key) {
        MMObjectBuilder bul = getMMObject(buildername);
        if (bul != null) {
            FieldDefs def = bul.getField(fieldname);
            if (key.equals("dbkey")) {
                if (def.isKey()) {
                    return "true";
                } else {
                    return "false";
                }
            } else if (key.equals("dbnotnull")) {
                if (def.getDBNotNull()) {
                    return "true";
                } else {
                    return "false";
                }
            } else if (key.equals("dbname")) {
                return def.getDBName();
            } else if (key.equals("dbsize")) {
                int size = def.getDBSize();
                if (size != -1) {
                    return "" + size;
                } else {
                    return "fixed";
                }
            } else if (key.equals("dbstate")) {
                return def.getDBStateDescription();
            } else if (key.equals("dbmmbasetype")) {
                return def.getDBTypeDescription();
            } else if (key.equals("editorinput")) {
                int pos = def.getGUIPos();
                if (pos == -1) {
                    return "not shown";
                } else {
                    return "" + pos;
                }
            } else if (key.equals("editorsearch")) {
                int pos = def.getGUISearch();
                if (pos == -1) {
                    return "not shown";
                } else {
                    return "" + pos;
                }
            } else if (key.equals("editorlist")) {
                int pos = def.getGUIList();
                if (pos == -1) {
                    return "not shown";
                } else {
                    return "" + pos;
                }
            } else if (key.equals("guitype")) {
                return def.getGUIType();
            }
        }
        return "";
    }

    /**
     * @javadoc
     */
    private Vector getISOGuiNames(String buildername, String fieldname) {
        Vector results = new Vector();
        MMObjectBuilder bul = getMMObject(buildername);
        if (bul != null) {
            FieldDefs def = bul.getField(fieldname);
            Map guinames = def.getGUINames();
            for (Iterator h = guinames.entrySet().iterator(); h.hasNext();) {
                Map.Entry me = (Map.Entry)h.next();
                results.addElement(me.getKey());
                results.addElement(me.getValue());
            }
        }
        return results;
    }

    /**
     * @javadoc
     */
    private Vector getISODescriptions(String buildername, String fieldname) {
        Vector results = new Vector();
        MMObjectBuilder bul = getMMObject(buildername);
        if (bul != null) {
            FieldDefs def = bul.getField(fieldname);
            Map guinames = def.getDescriptions();
            for (Iterator h = guinames.entrySet().iterator(); h.hasNext();) {
                Map.Entry me = (Map.Entry)h.next();
                results.addElement(me.getKey());
                results.addElement(me.getValue());
            }
        }
        return results;
    }

    /**
     * @javadoc
     */
    private String getGuiNameValue(String buildername, String fieldname, String key) {
        MMObjectBuilder bul = getMMObject(buildername);
        if (bul != null) {
            FieldDefs def = bul.getField(fieldname);
            String value = def.getGUIName(key);
            if (value != null) {
                return value;
            }
        }
        return "";
    }

    /**
     * @javadoc
     */
    private String getDescription(String buildername, String fieldname, String key) {
        MMObjectBuilder bul = getMMObject(buildername);
        if (bul != null) {
            FieldDefs def = bul.getField(fieldname);
            String value = def.getDescription(key);
            if (value != null) {
                return value;
            }
        }
        return "";
    }

    /**
     * @javadoc
     */
    public void doModulePosts(String command, Hashtable cmds, Hashtable vars) {
        if (command.equals("SETPROPERTY")) {
            setModuleProperty(vars);
        }
    }

    /**
     * @javadoc
     */
    public void doBuilderPosts(String command, Hashtable cmds, Hashtable vars) {
        if (command.equals("SETGUINAME")) {
            setBuilderGuiName(vars);
        } else if (command.equals("SETDESCRIPTION")) {
            setBuilderDescription(vars);
        } else if (command.equals("SETGUITYPE")) {
            setBuilderGuiType(vars);
        } else if (command.equals("SETEDITORINPUT")) {
            setBuilderEditorInput(vars);
        } else if (command.equals("SETEDITORLIST")) {
            setBuilderEditorList(vars);
        } else if (command.equals("SETEDITORSEARCH")) {
            setBuilderEditorSearch(vars);
        } else if (command.equals("SETDBSIZE")) {
            setBuilderDBSize(vars);
        } else if (command.equals("SETDBKEY")) {
            setBuilderDBKey(vars);
        } else if (command.equals("SETDBNOTNULL")) {
            setBuilderDBNotNull(vars);
        } else if (command.equals("SETDBMMBASETYPE")) {
            setBuilderDBMMBaseType(vars);
        } else if (command.equals("SETDBSTATE")) {
            setBuilderDBState(vars);
        } else if (command.equals("ADDFIELD")) {
            addBuilderField(vars);
        } else if (command.equals("REMOVEFIELD")) {
            removeBuilderField(vars);
        }
    }

    /**
     * @javadoc
     */
    public void setBuilderGuiName(Hashtable vars) {
        if (kioskmode) {
            log.warn("refused gui name set, am in kiosk mode");
            return;
        }
        String builder = (String)vars.get("BUILDER");
        String fieldname = (String)vars.get("FIELDNAME");
        String country = (String)vars.get("COUNTRY");
        String value = (String)vars.get("VALUE");

        MMObjectBuilder bul = getMMObject(builder);
        FieldDefs def = bul.getField(fieldname);
        if (def != null) {
            def.setGUIName(country, value);
        }
        syncBuilderXML(bul, builder);
    }

    /**
     * @javadoc
     */
    public void setBuilderDescription(Hashtable vars) {
        if (kioskmode) {
            log.warn("refused gui name set, am in kiosk mode");
            return;
        }
        String builder = (String)vars.get("BUILDER");
        String fieldname = (String)vars.get("FIELDNAME");
        String country = (String)vars.get("COUNTRY");
        String value = (String)vars.get("VALUE");

        MMObjectBuilder bul = getMMObject(builder);
        FieldDefs def = bul.getField(fieldname);
        if (def != null) {
            def.setDescription(country, value);
        }
        syncBuilderXML(bul, builder);
    }

    /**
     * @javadoc
     */
    public void setBuilderGuiType(Hashtable vars) {
        if (kioskmode) {
            log.warn("refused gui type set, am in kiosk mode");
            return;
        }
        String builder = (String)vars.get("BUILDER");
        String fieldname = (String)vars.get("FIELDNAME");
        String value = (String)vars.get("VALUE");

        MMObjectBuilder bul = getMMObject(builder);
        FieldDefs def = bul.getField(fieldname);
        if (def != null) {
            def.setGUIType(value);
        }
        syncBuilderXML(bul, builder);
    }

    /**
     * @javadoc
     */
    public void setBuilderEditorInput(Hashtable vars) {
        if (kioskmode) {
            log.warn("refused editor input set, am in kiosk mode");
            return;
        }
        String builder = (String)vars.get("BUILDER");
        String fieldname = (String)vars.get("FIELDNAME");
        String value = (String)vars.get("VALUE");

        MMObjectBuilder bul = getMMObject(builder);
        FieldDefs def = bul.getField(fieldname);
        if (def != null) {
            try {
                int i = Integer.parseInt(value);
                def.setGUIPos(i);
            } catch (Exception e) {}
        }
        syncBuilderXML(bul, builder);
    }

    /**
     * @javadoc
     */
    public void setBuilderEditorList(Hashtable vars) {
        if (kioskmode) {
            log.warn("refused editor list set, am in kiosk mode");
            return;
        }
        String builder = (String)vars.get("BUILDER");
        String fieldname = (String)vars.get("FIELDNAME");
        String value = (String)vars.get("VALUE");

        MMObjectBuilder bul = getMMObject(builder);
        FieldDefs def = bul.getField(fieldname);
        if (def != null) {
            try {
                int i = Integer.parseInt(value);
                def.setGUIList(i);
            } catch (Exception e) {}
        }
        syncBuilderXML(bul, builder);
    }

    /**
     * @javadoc
     */
    public void setBuilderEditorSearch(Hashtable vars) {
        if (kioskmode) {
            log.warn("refused editor pos set, am in kiosk mode");
            return;
        }
        String builder = (String)vars.get("BUILDER");
        String fieldname = (String)vars.get("FIELDNAME");
        String value = (String)vars.get("VALUE");

        MMObjectBuilder bul = getMMObject(builder);
        FieldDefs def = bul.getField(fieldname);
        if (def != null) {
            try {
                int i = Integer.parseInt(value);
                def.setGUISearch(i);
            } catch (Exception e) {}
        }
        syncBuilderXML(bul, builder);
    }

    /**
     * @javadoc
     */
    public void setBuilderDBSize(Hashtable vars) {
        if (kioskmode) {
            log.warn("Refused set DBSize field, am in kiosk mode");
            return;
        }
        String builder = (String)vars.get("BUILDER");
        String fieldname = (String)vars.get("FIELDNAME");
        String value = (String)vars.get("VALUE");

        MMObjectBuilder bul = getMMObject(builder);
        FieldDefs def = bul.getField(fieldname);
        if (def != null) {
            int oldSize = def.getDBSize();
            try {
                int newSize = Integer.parseInt(value);
                if (newSize != oldSize) {
                    def.setDBSize(newSize);
                    StorageManagerFactory factory = mmb.getStorageManagerFactory();
                    if (factory!=null) {
                        try {
                            // make change in storage
                            factory.getStorageManager().change(def);
                            syncBuilderXML(bul, builder);
                        } catch (StorageException se) {
                            def.setDBSize(oldSize);
                            throw new RuntimeException(se);
                        }
                    } else {
                        if (mmb.getDatabase().changeField(bul, fieldname)) {
                            syncBuilderXML(bul, builder);
                        }
                    }
                }
            } catch (NumberFormatException nfe)  {
                throw new RuntimeException(nfe);
            }
        }
    }

    /**
     * @javadoc
     */
    public void setBuilderDBMMBaseType(Hashtable vars) {
        if (kioskmode) {
            log.warn("Refused set setDBMMBaseType field, am in kiosk mode");
            return;
        }
        String builder = (String)vars.get("BUILDER");
        String fieldname = (String)vars.get("FIELDNAME");
        String value = (String)vars.get("VALUE");

        MMObjectBuilder bul = getMMObject(builder);
        FieldDefs def = bul.getField(fieldname);
        if (def != null) {
            int oldType = def.getDBType();
            int newType = FieldDefs.getDBTypeId(value);
            if (oldType != newType) {
                def.setDBType(newType);
                StorageManagerFactory factory = mmb.getStorageManagerFactory();
                if (factory!=null) {
                    try {
                        // make change in storage
                        factory.getStorageManager().change(def);
                        syncBuilderXML(bul, builder);
                    } catch (StorageException se) {
                        def.setDBType(oldType);
                        throw new RuntimeException(se);
                    }
                } else {
                    if (mmb.getDatabase().changeField(bul, fieldname)) {
                        syncBuilderXML(bul, builder);
                    }
                }
            }
        }
    }

    /**
     * @javadoc
     */
    public void setBuilderDBState(Hashtable vars) {
        if (kioskmode) {
            log.warn("Refused set DBState field, am in kiosk mode");
            return;
        }
        String builder = (String)vars.get("BUILDER");
        String fieldname = (String)vars.get("FIELDNAME");
        String value = (String)vars.get("VALUE");

        MMObjectBuilder bul = getMMObject(builder);
        FieldDefs def = bul.getField(fieldname);
        if (def != null) {
            int oldState = def.getDBState();
            int newState = FieldDefs.getDBStateId(value);
            if (oldState != newState) {
                def.setDBState(newState);
                StorageManagerFactory factory = mmb.getStorageManagerFactory();
                if (factory!=null) {
                    // add field if it was not persistent before
                    if ((newState == FieldDefs.DBSTATE_PERSISTENT || newState == FieldDefs.DBSTATE_SYSTEM) &&
                        (oldState != FieldDefs.DBSTATE_PERSISTENT && oldState != FieldDefs.DBSTATE_SYSTEM)) {
                        try {
                            // make change in storage
                            factory.getStorageManager().create(def);
                            // only then add to builder
                            bul.addField(def);
                            syncBuilderXML(bul, builder);
                        } catch (StorageException se) {
                            def.setDBState(oldState);
                            throw new RuntimeException(se);
                        }
                    }
                    // TODO:  remove field if no longer persistent ???
                } else {
                    // TODO: should be ADD or REMOVE, not CHANGE
                    if (mmb.getDatabase().changeField(bul, fieldname)) {
                        syncBuilderXML(bul, builder);
                    }
                }
            }
        }
    }

    /**
     * @javadoc
     */
    public void setBuilderDBKey(Hashtable vars) {
        if (kioskmode) {
            log.warn("Refused set dbkey field, am in kiosk mode");
            return;
        }
        String builder = (String)vars.get("BUILDER");
        String fieldname = (String)vars.get("FIELDNAME");
        String value = (String)vars.get("VALUE");

        MMObjectBuilder bul = getMMObject(builder);
        FieldDefs def = bul.getField(fieldname);
        if (def != null) {
            if (value.equals("true")) {
                def.setDBKey(true);
            } else {
                def.setDBKey(false);
            }
        }
        // TODO: when changing key, shoudl call CHANGE
        syncBuilderXML(bul, builder);
    }

    /**
     * @javadoc
     */
    public void setBuilderDBNotNull(Hashtable vars) {
        if (kioskmode) {
            log.warn("Refused set NotNull field, am in kiosk mode");
            return;
        }
        String builder = (String)vars.get("BUILDER");
        String fieldname = (String)vars.get("FIELDNAME");
        String value = (String)vars.get("VALUE");

        MMObjectBuilder bul = getMMObject(builder);
        FieldDefs def = bul.getField(fieldname);
        if (def != null) {
            boolean oldNotNull = def.getDBNotNull();
            boolean newNotNull = value.equals("true");
            if (oldNotNull != newNotNull) {
                def.setDBNotNull(newNotNull);
                StorageManagerFactory factory = mmb.getStorageManagerFactory();
                if (factory!=null) {
                    try {
                        // make change in storage
                        factory.getStorageManager().change(def);
                        syncBuilderXML(bul, builder);
                    } catch (StorageException se) {
                        def.setDBNotNull(oldNotNull);
                        throw new RuntimeException(se);
                    }
                } else {
                    if (mmb.getDatabase().changeField(bul, fieldname)) {
                        syncBuilderXML(bul, builder);
                    }
                }
            }
        }
    }

    /**
     * @javadoc
     */
    public void addBuilderField(Hashtable vars) {
        if (kioskmode) {
            log.warn("Refused add builder field, am in kiosk mode");
            return;
        }
        String builder = (String)vars.get("BUILDER");
        MMObjectBuilder bul = getMMObject(builder);
        if (bul != null) {
            // Determine position of new field.
            // This should be the number of the last field as denied in the builder xml,
            // as the DBPos field is incremented for each field in that file.
            int pos = bul.getFields(FieldDefs.ORDER_CREATE).size() + 1;

            FieldDefs def = new FieldDefs();
            def.setParent(bul);
            def.setDBPos(pos);

            def.setGUIPos(pos);
            def.setGUIList(-1);
            def.setGUISearch(pos);

            String value = (String)vars.get("dbname");
            def.setDBName(value);
            def.setGUIName("en", value);

            log.service("Adding field " + value);

            value = (String)vars.get("mmbasetype");
            def.setDBType(value);

            value = (String)vars.get("dbstate");
            def.setDBState(value);

            value = (String)vars.get("dbnotnull");
            def.setDBNotNull(value.equals("true"));

            value = (String)vars.get("dbkey");
            def.setDBKey(value.equals("true"));

            value = (String)vars.get("dbsize");
            try {
                int i = Integer.parseInt(value);
                def.setDBSize(i);
            } catch (Exception e) {
                log.debug("dbsize had invalid value, not setting size");
            }

            value = (String)vars.get("guitype");
            def.setGUIType(value);

            StorageManagerFactory factory = mmb.getStorageManagerFactory();
            if (factory!=null) {
                try {
                    // make change in storage
                    factory.getStorageManager().create(def);
                    // only then add to builder
                    bul.addField(def);
                    syncBuilderXML(bul, builder);
                } catch (StorageException se) {
                    throw new RuntimeException(se);
                }
            } else {
                // for old support classes, need to add field to builder first
                bul.addField(def);
                if (mmb.getDatabase().addField(bul, def.getDBName())) {
                    syncBuilderXML(bul, builder);
                } else {
                    log.warn("Could not sync builder XML because addField returned false (tablesizeprotection?)");
                }
            }
        } else {
            log.service("Cannot add field to builder " + builder + " because it could not be found");
        }
    }

    /**
     * @javadoc
     */
    public void removeBuilderField(Hashtable vars) {
        if (kioskmode) {
            log.warn("Refused remove builder field, am in kiosk mode");
            return;
        }
        String builder = (String)vars.get("BUILDER");
        String fieldname = (String)vars.get("FIELDNAME");
        String value = (String)vars.get("SURE");

        MMObjectBuilder bul = getMMObject(builder);
        if (bul != null && value != null && value.equals("Yes")) {
            FieldDefs def = bul.getField(fieldname);
            StorageManagerFactory factory = mmb.getStorageManagerFactory();
            if (factory!=null) {
                try {
                    // make change in storage
                    factory.getStorageManager().delete(def);
                    // only then delete in builder
                    bul.removeField(fieldname);
                    syncBuilderXML(bul, builder);
                } catch (StorageException se) {
                    throw new RuntimeException(se);
                }
            } else {
                // for old support classes, need to delete field from builder first
                bul.removeField(fieldname);
                if (mmb.getDatabase().removeField(bul, def.getDBName())) {
                    syncBuilderXML(bul, builder);
                } else {
                    bul.addField(def);
                }
            }
        }
    }

    /**
     * @javadoc
     */
    public void syncBuilderXML(MMObjectBuilder bul, String builder) {
        String savepath =
            MMBaseContext.getConfigPath() + File.separator + "builders" + File.separator + builder + ".xml";
        log.service("Syncing builder xml (" + savepath + ") for builder " + builder);
        try {
            BuilderWriter builderOut = new BuilderWriter(bul);
            builderOut.setIncludeComments(false);
            builderOut.setExpandBuilder(false);
            builderOut.writeToFile(savepath);
        } catch (Exception e) {
            log.error(Logging.stackTrace(e));
        }
    }

    /**
     * @javadoc
     */
    public void syncModuleXML(Module mod, String modname) {
        String savepath =
            MMBaseContext.getConfigPath() + File.separator + "modules" + File.separator + modname + ".xml";
        try {
            ModuleWriter moduleOut = new ModuleWriter(mod);
            moduleOut.setIncludeComments(false);
            moduleOut.writeToFile(savepath);
        } catch (Exception e) {
            log.error(Logging.stackTrace(e));
        }
    }

    /**
     * @javadoc
     */
    public Vector getMultilevelCacheEntries() {
        Vector results = new Vector();
        Iterator res = MultilevelCache.getCache().getOrderedEntries().iterator();
        while (res.hasNext()) {
            Map.Entry entry = (Map.Entry)res.next();
            /*
            StringTagger tagger=en.getTagger();
            Vector type=tagger.Values("TYPE");
            Vector where=tagger.Values("WHERE");
            Vector dbsort=tagger.Values("DBSORT");
            Vector dbdir=tagger.Values("DBDIR");
            Vector fields=tagger.Values("FIELDS");
            results.addElement(""+en.getKey());
            results.addElement(""+type);
            results.addElement(""+fields);
            if (where!=null) {
                results.addElement(where.toString());
            } else {
                results.addElement("");
            }
            if (dbsort!=null) {
                results.addElement(dbsort.toString());
            } else {
                results.addElement("");
            }
            if (dbdir!=null) {
                results.addElement(dbdir.toString());
            } else {
                results.addElement("");
            }
            results.addElement(tagger.ValuesString("ALL"));
            */
            results.add(entry.getKey());
            results.addElement("" + MultilevelCache.getCache().getCount(entry.getKey()));
        }
        return results;
    }

    /**
     * @javadoc
     */
    public Vector getNodeCacheEntries() {
        Vector results = new Vector();
        Iterator iter = MMObjectBuilder.nodeCache.getOrderedEntries().iterator();
        while (iter.hasNext()) {
            MMObjectNode node = (MMObjectNode)iter.next();
            results.addElement("" + MMObjectBuilder.nodeCache.getCount(node.getIntegerValue("number")));
            results.addElement("" + node.getIntValue("number"));
            results.addElement(node.getStringValue("owner"));
            results.addElement(mmb.getTypeDef().getValue(node.getIntValue("otype")));
        }
        return results;
    }

    class ApplicationResult {

        protected String resultMessage;
        protected boolean success;
        protected MMAdmin adminModule;

        ApplicationResult(MMAdmin adminModule) {
            this.adminModule = adminModule;
            resultMessage = "";
            success = true;
        }

        String getMessage() {
            return resultMessage;
        }

        boolean isSuccess() {
            return success;
        }

        boolean error(String message) {
            success = false;
            log.error(message);
            if (!resultMessage.equals(""))
                resultMessage += "\n";
            resultMessage += message;
            return false;
        }

        boolean warn(String message) {
            success = false;
            log.warn(message);
            if (!resultMessage.equals(""))
                resultMessage += "\n";
            resultMessage += message;
            return false;
        }

        boolean success(String message) {
            success = true;
            if (!resultMessage.equals(""))
                resultMessage += "\n";
            resultMessage += message;
            return true;
        }

    }

}
