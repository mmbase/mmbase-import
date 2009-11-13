/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package org.mmbase.module.tools;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;

import javax.servlet.http.HttpSession;

import org.mmbase.bridge.*;
import org.mmbase.cache.*;
import org.mmbase.core.CoreField;
import org.mmbase.core.util.Fields;
import org.mmbase.datatypes.DataType;
import org.mmbase.datatypes.DataTypes;
import org.mmbase.module.Module;
import org.mmbase.module.ProcessorModule;
import org.mmbase.module.builders.Versions;
import org.mmbase.module.core.*;
import org.mmbase.security.Rank;
import org.mmbase.storage.StorageException;
import org.mmbase.storage.search.SearchQueryException;
import org.mmbase.util.*;
import org.mmbase.util.functions.*;
import org.mmbase.util.logging.*;
import org.mmbase.util.xml.applicationdata.*;
import org.mmbase.util.xml.*;
import org.xml.sax.InputSource;

/**
 * @javadoc
 *
 * @application Admin, Application
 * @author Daniel Ockeloen
 * @author Pierre van Rooden
 * @version $Id$
 */
public class MMAdmin extends ProcessorModule {
    private static final Logger log = Logging.getLoggerInstance(MMAdmin.class);

    // true: ready (probeCall was called)
    private boolean state = false;

    /**
     * reference to MMBase
     */
    private MMBase mmb = null;

    /**
     * @javadoc
     */
    private String lastmsg = "";

    /**
     * @javadoc
     */
    private boolean kioskmode = false;

    private static final Parameter<String> PARAM_APPLICATION = new Parameter<String>("application", String.class);
    private static final Parameter<String> PARAM_BUILDER = new Parameter<String>("builder", String.class);
    private static final Parameter<String> PARAM_MODULE = new Parameter<String>("module", String.class);
    private static final Parameter<String> PARAM_FIELD = new Parameter<String>("field", String.class);
    private static final Parameter<String> PARAM_KEY = new Parameter<String>("key", String.class);
    private static final Parameter<String> PARAM_CMD = new Parameter<String>("cmd", String.class);
    private static final Parameter<String> PARAM_PATH = new Parameter<String>("path", String.class);
    private static final Parameter<?>[] PARAMS_BUILDER = new Parameter<?>[] { PARAM_BUILDER, PARAM_PAGEINFO};
    private static final Parameter<?>[] PARAMS_APPLICATION = new Parameter<?>[] { PARAM_APPLICATION, PARAM_PAGEINFO};

    {
        addFunction(new GetNodeListFunction("APPLICATIONS", PARAMS_PAGEINFO));
        addFunction(new GetNodeListFunction("BUILDERS", PARAMS_PAGEINFO));
        addFunction(new GetNodeListFunction("MODULES", PARAMS_PAGEINFO));
        addFunction(new GetNodeListFunction("DATABASES", PARAMS_PAGEINFO));
        addFunction(new GetNodeListFunction("MULTILEVELCACHEENTRIES", PARAMS_PAGEINFO));
        addFunction(new GetNodeListFunction("NODECACHEENTRIES", PARAMS_PAGEINFO));
        addFunction(new GetNodeListFunction("FIELDS", PARAMS_BUILDER));

        addFunction(new ReplaceFunction("VERSION", PARAMS_APPLICATION));
        addFunction(new ReplaceFunction("INSTALLEDVERSION", PARAMS_APPLICATION));
        addFunction(new ReplaceFunction("DESCRIPTION", PARAMS_APPLICATION));

        addFunction(new ReplaceFunction("BUILDERVERSION", PARAMS_BUILDER));
        addFunction(new ReplaceFunction("BUILDERCLASSFILE", PARAMS_BUILDER));
        addFunction(new ReplaceFunction("BUILDERDESCRIPTION", PARAMS_BUILDER));

        addFunction(new ReplaceFunction("GETDESCRIPTION", new Parameter[] {PARAM_BUILDER, PARAM_FIELD, Parameter.LANGUAGE, PARAM_PAGEINFO}));
        addFunction(new ReplaceFunction("GETGUINAMEVALUE", new Parameter[] {PARAM_BUILDER, PARAM_FIELD, Parameter.LANGUAGE, PARAM_PAGEINFO}));
        addFunction(new ReplaceFunction("GETBUILDERFIELD", new Parameter[] {PARAM_BUILDER, PARAM_FIELD, PARAM_KEY, PARAM_PAGEINFO}));
        addFunction(new ReplaceFunction("GETMODULEPROPERTY", new Parameter[] {PARAM_MODULE, PARAM_KEY, PARAM_PAGEINFO}));
        addFunction(new ReplaceFunction("MODULEDESCRIPTION", new Parameter[] {PARAM_MODULE, PARAM_PAGEINFO}));
        addFunction(new ReplaceFunction("MODULECLASSFILE", new Parameter[] {PARAM_MODULE, PARAM_PAGEINFO}));

        addFunction(new ReplaceFunction("MULTILEVELCACHEHITS", PARAMS_PAGEINFO));
        addFunction(new ReplaceFunction("MULTILEVELCACHEMISSES", PARAMS_PAGEINFO));
        addFunction(new ReplaceFunction("MULTILEVELCACHEREQUESTS", PARAMS_PAGEINFO));
        addFunction(new ReplaceFunction("MULTILEVELCACHEPERFORMANCE", PARAMS_PAGEINFO));
        addFunction(new ReplaceFunction("MULTILEVELCACHESTATE", new Parameter<?>[] {new Parameter<String>("state", String.class), PARAM_PAGEINFO}));
        addFunction(new ReplaceFunction("MULTILEVELCACHESIZE", PARAMS_PAGEINFO));

        addFunction(new ReplaceFunction("NODECACHEHITS", PARAMS_PAGEINFO));
        addFunction(new ReplaceFunction("NODECACHEMISSES", PARAMS_PAGEINFO));
        addFunction(new ReplaceFunction("NODECACHEREQUESTS", PARAMS_PAGEINFO));
        addFunction(new ReplaceFunction("NODECACHEPERFORMANCE", PARAMS_PAGEINFO));
        addFunction(new ReplaceFunction("NODECACHESIZE", PARAMS_PAGEINFO));
        addFunction(new ReplaceFunction("TEMPORARYNODECACHESIZE", PARAMS_PAGEINFO));
        addFunction(new ReplaceFunction("RELATIONCACHEHITS", PARAMS_PAGEINFO));
        addFunction(new ReplaceFunction("RELATIONCACHEMISSES", PARAMS_PAGEINFO));
        addFunction(new ReplaceFunction("RELATIONCACHEREQUESTS", PARAMS_PAGEINFO));
        addFunction(new ReplaceFunction("RELATIONCACHEPERFORMANCE", PARAMS_PAGEINFO));

        addFunction(new ProcessFunction("LOAD", new Parameter[] {PARAM_APPLICATION, PARAM_PAGEINFO, new Parameter("RESULT", String.class, "")}));
        addFunction(new ProcessFunction("SAVE", new Parameter[] {PARAM_APPLICATION, PARAM_PATH, PARAM_PAGEINFO, new Parameter("RESULT", String.class, "")}));
        addFunction(new ProcessFunction("BUILDERSAVE", new Parameter[] {PARAM_BUILDER, PARAM_PATH, PARAM_PAGEINFO, new Parameter("RESULT", String.class, "")}));
    }

    public MMAdmin(String name) {
        super(name);
    }

    /**
     * @javadoc
     */
    @Override public void init() {
        try {
            String dtmp = System.getProperty("mmbase.kiosk");
            if (dtmp != null && dtmp.equals("yes")) {
                kioskmode = true;
                log.info("*** Server started in kiosk mode ***");
            }
        } catch (SecurityException se) {
            log.debug(se);
        }
        mmb = MMBase.getMMBase();
        org.mmbase.util.ThreadPools.jobsExecutor.execute(new Runnable() {
                public void run() {
                    while (!mmb.getState()) {
                        try {Thread.sleep(2000);} catch (InterruptedException e){ return;}
                    }
                    try {
                        MMAdmin.this.probeCall();
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
            });
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
    @Override public MMObjectBuilder getListBuilder(String command, Map<String, ?> params) {
        return new VirtualBuilder(mmb);
    }

    /**
     * Retrieves a specified builder.
     * The builder's name can be extended with the subpath of that builder's configuration file.
     * i.e. 'core/typedef' or 'basic/images'. The subpath part is ignored.
     * @param path The path of the builder to retrieve
     * @return a <code>MMObjectBuilder</code> is found, <code>null</code> otherwise
     */
    public MMObjectBuilder getBuilder(String path) {
        int pos = path.lastIndexOf(File.separator);
        if (pos != -1) {
            path = path.substring(pos + 1);
        }
        return mmb.getBuilder(path);
    }

    /**
     * Generate a list of values from a command to the processor
     * @javadoc
     */
    public List<String> getList(PageInfo sp, StringTagger tagger, String value) {
        String line = Strip.doubleQuote(value, Strip.BOTH);
        StringTokenizer tok = new StringTokenizer(line, "-\n\r");
        if (tok.hasMoreTokens()) {
            String cmd = tok.nextToken();
            log.debug("Cmd '" + cmd + "'");
            if (!checkUserLoggedOn(sp, cmd, false)) {
                log.warn("Could not find cloud for " + sp + " returning empty list for " + tagger + "/" + value);
                return new Vector<String>();
            }
            if (cmd.equals("APPLICATIONS")) {
                tagger.setValue("ITEMS", "6");
                try {
                    return getApplicationsList();
                } catch (SearchQueryException e) {
                    log.warn(Logging.stackTrace(e));
                }
            }
            if (cmd.equals("BUILDERS")) {
                tagger.setValue("ITEMS", "4");
                if ((tok != null) && (tok.hasMoreTokens())) {
                    tok.nextToken();
                }
                return getBuildersList();
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
    private boolean checkAdmin(PageInfo sp, String cmd) {
        return checkUserLoggedOn(sp, cmd, true);
    }

    /**
     * @javadoc
     */
    private boolean checkUserLoggedOn(PageInfo sp, String cmd, boolean adminonly) {

        if (sp.getCloud() != null) {
            if ((!adminonly) || sp.getCloud().getUser().getRank().getInt() >= Rank.ADMIN.getInt()) {
                log.debug("Found cloud " + sp.getCloud().getUser());
                return true;
            }
        }
        // check if the we are using jsp, and logged on as user with rank is admin, this means that
        // there is some user with rank Administrator in the session...

        if (sp.req != null) {
            HttpSession session = sp.req.getSession(false);
            Enumeration e = session.getAttributeNames();
            while (e.hasMoreElements()) {
                String attribute = (String) e.nextElement();
                Object o = session.getAttribute(attribute);

                if (o instanceof Cloud) {
                    Cloud cloud = (Cloud) o;
                    Rank curRank = cloud.getUser().getRank();
                    if (curRank.getInt() >= Rank.ADMIN.getInt()) {
                        // log.service("Found an administrator cloud in session key=" + attribute);
                        return true;
                    }
                }
            }
        }
        log.debug("No cloud specified, using class security");
        Map<String, Object> loginInfo = new HashMap<String, Object>();
        loginInfo.put("rank", "administrator");
        Cloud cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase", "class", loginInfo);
        log.debug("Found " + cloud);
        return cloud.getUser().getRank().getInt() >= Rank.ADMIN.getInt();
    }

    /**
     * Execute the commands provided in the form values
     * @javadoc
     */
    @Override public boolean process(PageInfo sp, Hashtable<String,Object> cmds, Hashtable<String,Object> vars) {
        String cmdline, token;
        for (Enumeration<String> h = cmds.keys(); h.hasMoreElements();) {
            cmdline = h.nextElement();
            log.debug("cmdline: " + cmdline);
            if (!checkAdmin(sp, cmdline)) {
                log.warn("Could not find cloud for " + sp + " returning false for process " + cmds + "/" + vars);
                return false;
            }
            StringTokenizer tok = new StringTokenizer(cmdline, "-\n\r");
            token = tok.nextToken();
            if (token.equals("SERVERRESTART")) {
                lastmsg = "Server restart is not implemented any more";
                return false;
            } else if (token.equals("LOAD") && !kioskmode) {
                ApplicationResult result = new ApplicationResult();
                String appname = (String)cmds.get(cmdline);
                if ("".equals(appname)) {
                    log.warn("Found empty app-name in " + cmds + " (used key " + cmdline + ")");
                }
                try {
                    if (new ApplicationInstaller(mmb, this).installApplication(appname, -1, null, result, new HashSet<String>(), false)) {
                        lastmsg = result.getMessage();
                    } else {
                        lastmsg = "Problem installing application : " + appname + ", cause: " + result.getMessage();
                    }
                } catch (SearchQueryException e) {
                    log.warn(Logging.stackTrace(e));
                }
                if (vars != null) {
                    vars.put("RESULT", lastmsg);
                }
            } else if (token.equals("SAVE")) {
                String appname = (String)cmds.get(cmdline);
                String savepath = (String)vars.get("path");
                String goal = (String)vars.get("goal");
                boolean includeComments = true;
                /* if (tok.hasMoreTokens()) {
                    includeComments = "true".equals(tok.nextToken());
                } */
                writeApplication(appname, savepath, goal, includeComments);
                if (vars != null) {
                    vars.put("RESULT", lastmsg);
                }
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
                    Module mod = getModule(modulename);
                    if (mod != null) {
                        try {
                            boolean includeComments = false;
                            if (tok.hasMoreTokens()) {
                                includeComments = "true".equals(tok.nextToken());
                            }
                            ModuleWriter moduleOut = new ModuleWriter(mod);
                            moduleOut.setIncludeComments(includeComments);
                            moduleOut.writeToFile(savepath);
                        } catch (Exception e) {
                            log.error(Logging.stackTrace(e));
                            lastmsg = "Writing finished, problems occurred\n\nError encountered=" + e.getMessage() + "\n\n";
                            return false;
                        }
                        lastmsg = "Writing finished, no problems.\n\nA clean copy of " + modulename + ".xml can be found at : " + savepath + "\n\n";
                    } else {
                        lastmsg = "Writing failed, module : " + modulename + ".xml because module is not loaded\n\n";
                        return false;
                    }
                }
            } else if (token.equals("BUILDERSAVE")) {
                if (kioskmode) {
                    log.warn("MMAdmin> refused to write builder, am in kiosk mode");
                } else {
                    String buildername = (String)cmds.get(cmdline);
                    String savepath = (String)vars.get("path");
                    MMObjectBuilder bul = getBuilder(buildername);
                    if (bul != null) {
                        try {
                            boolean includeComments = false;
                            if (tok.hasMoreTokens()) {
                                includeComments = "true".equals(tok.nextToken());
                            }
                            BuilderWriter builderOut = new BuilderWriter(bul);
                            builderOut.setIncludeComments(includeComments);
                            builderOut.setExpandBuilder(false);
                            builderOut.writeToFile(savepath);
                            lastmsg =
                                "Writing finished, no problems.\n\nA clean copy of " + buildername + ".xml can be found at : " + savepath + "\n\n";
                        } catch (Exception e) {
                            log.error(Logging.stackTrace(e));
                            lastmsg = "Writing finished, problems occurred\n\n" + "Error encountered=" + e.getMessage() + "\n\n";
                        }
                        if (vars != null) {
                            vars.put("RESULT", lastmsg);
                        }
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
            Encode encoder = new Encode("ESCAPE_XML");
            String escaped = encoder.encode(s);
            return escaped.replaceAll("\n", "<br />");
        }
    }

    /**
     * @since MMBase-1.8.5
     */
    protected Collection<String> getIgnoredAutodeployApplications() {
        return Casting.toCollection(getInitParameter("ignored-auto-deploy"));
    }

    /**
     * Handle a $MOD command
     * @javadoc
     */
    @Override public String replace(PageInfo sp, String cmds) {
        if (!checkUserLoggedOn(sp, cmds, false)) return "";
        StringTokenizer tok = new StringTokenizer(cmds, "-\n\r");
        if (tok.hasMoreTokens()) {
            String cmd = tok.nextToken();
            if (cmd.equals("VERSION")) {
                return "" + getVersion(tok.nextToken());
            } else if (cmd.equals("INSTALLEDVERSION")) {
                Versions ver = (Versions) mmb.getBuilder("versions");
                if (ver == null) {
                    log.warn("Versions builder not installed, Can't get to apps");
                    return null;
                }
                return "" + ver.getInstalledVersion(tok.nextToken(), "application");
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
                return ("" + (MultilevelCache.getCache().maxSize()));
            } else if (cmd.equals("NODECACHEHITS")) {
                return ("" + NodeCache.getCache().getHits());
            } else if (cmd.equals("NODECACHEMISSES")) {
                return ("" + NodeCache.getCache().getMisses());
            } else if (cmd.equals("NODECACHEREQUESTS")) {
                return ("" + (NodeCache.getCache().getHits() + NodeCache.getCache().getMisses()));
            } else if (cmd.equals("NODECACHEPERFORMANCE")) {
                return ("" + (NodeCache.getCache().getRatio() * 100));
            } else if (cmd.equals("NODECACHESIZE")) {
                return ("" + (NodeCache.getCache().maxSize()));
            } else if (cmd.equals("TEMPORARYNODECACHESIZE")) {
                return ("" + (MMObjectBuilder.temporaryNodes.size()));
            } else if (cmd.equals("RELATIONCACHEHITS")) {
                return ("" + RelationsCache.getCache().getHits());
            } else if (cmd.equals("RELATIONCACHEMISSES")) {
                return ("" + RelationsCache.getCache().getMisses());
            } else if (cmd.equals("RELATIONCACHEREQUESTS")) {
                return ("" + (RelationsCache.getCache().getHits() + RelationsCache.getCache().getMisses()));
            } else if (cmd.equals("RELATIONCACHEPERFORMANCE")) {
                return
                    ""
                    + (1.0 * RelationsCache.getCache().getHits())
                    / (RelationsCache.getCache().getHits() + RelationsCache.getCache().getMisses() + 0.0000000001)
                    * 100;
            }
        }
        return "No command defined";
    }

    /**
     * @javadoc
     */
    int getVersion(String appname) {
        ApplicationReader reader = getApplicationReader(appname);
        if (reader != null) {
            return reader.getVersion();
        }
        return -1;
    }

    // determine xmlpath to a builder, provided it is loaded by MMBase.
    private String getXMLPath(String builderName) {
        MMObjectBuilder bul = mmb.getBuilder(builderName);
        if (bul==null) {
            return "";
        } else {
            return bul.getXMLPath();
        }
    }

    /**
     * @javadoc
     */
    int getBuilderVersion(String builderName) {
        int version = -1;
        BuilderReader bul = mmb.getBuilderReader(getXMLPath(builderName) + builderName);
        if (bul != null) {
            version = bul.getVersion();
        }
        return version;
    }

    /**
     * @javadoc
     */
    String getBuilderClass(String builderName) {
        String className = "";
        BuilderReader bul = mmb.getBuilderReader(getXMLPath(builderName) + builderName);
        if (bul != null) {
            className = bul.getClassName();
        }
        return className;
    }

    /**
     * @javadoc
     */
    String getModuleClass(String modname) {
        String className = "";
        ModuleReader mod = getModuleReader(modname);
        if (mod != null) {
            className = mod.getClassName();
        }
        return className;
    }

    /**
     * @javadoc
     */
    public void setModuleProperty(Hashtable<String,Object> vars) {
        if (kioskmode) {
            log.warn("refused module property set, am in kiosk mode");
            return;
        }
        String modname = (String) vars.get("MODULE");
        String key = (String) vars.get("PROPERTYNAME");
        String value = (String) vars.get("VALUE");
        Module mod = getModule(modname);
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
        Module mod = getModule(modname);
        if (mod != null) {
            String value = mod.getInitParameter(key);
            if (value != null) {
                return value;
            }
        }
        return "";

    }

    /**
     * @javadoc
     */
    String getDescription(String appname) {
        ApplicationReader reader = getApplicationReader(appname);
        if (reader != null) {
            return reader.getDescription();
        }
        return "";

    }

    /**
     * @javadoc
     */
    String getBuilderDescription(String builderName) {
        String description = "";
        BuilderReader bul = mmb.getBuilderReader(getXMLPath(builderName) + builderName);
        if (bul != null) {
            Map<String,String> desc = bul.getDescriptions();
            String english = desc.get("en");
            if (english != null) {
                description = english;
            }
        }
        return description;
    }

    /**
     * @javadoc
     */
    String getModuleDescription(String modulename) {
        Module mod = getModule(modulename);
        if (mod != null) {
            String value = mod.getModuleInfo();
            if (value != null)
                return value;
        }
        return "";
    }


    /**
     * Called when MMBase is up.  It'll install the applications
     * marked as 'autodeploy'.  It will do nothing, besides logging a
     * warning, if the 'versions' builder could not be found.
     */
    protected void probeCall() throws SearchQueryException {
        Versions ver = (Versions)mmb.getBuilder("versions");
        if (ver == null) {
            log.warn("Versions builder not installed, Can't auto deploy apps");
            return;
        }
        ApplicationInstaller installer = new ApplicationInstaller(mmb, this);

        installer.installApplications();
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
    private boolean writeApplication(String name, String targetPath, String goal, boolean includeComments) {
        if (kioskmode) {
            log.warn("refused to write application, am in kiosk mode");
            return false;
        }
        ApplicationReader reader = getApplicationReader(name);
        ApplicationWriter writer = new ApplicationWriter(reader, mmb);
        writer.setIncludeComments(includeComments);
        java.io.Writer w = new java.io.StringWriter();
        Logger logger = new WriterLogger(w);
        try {
            writer.writeToPath(targetPath, logger);
            lastmsg = "Application saved oke\n\n";
        } catch (Exception e) {
            lastmsg = "Saving application failed\n\n" + Logging.stackTrace(e) + "\n\n";
        }
        lastmsg += "Some statistics on the save : \n\n" + w.toString();
        return true;
    }

    /**
     * This method uses the {@link ResourceLoader} to fetch an application by name. for this purpose
     * it requests the resource by adding <code>applications/</code> to the start of the appName and appends <code>.xml</core> to the end
     * @param appName the name of the application to be read.
     * @return the ApplicationReader for the application, or null is the application wat not found or an exception occured. In the later a message is logged
     */
    private ApplicationReader getApplicationReader(String appName) {
        String resourceName = appName + ".xml";
        try {
            ResourceLoader applicationLoader = ResourceLoader.getConfigurationRoot().getChildResourceLoader("applications");
            InputSource is = applicationLoader.getInputSource(resourceName);
            if (is == null) {
                return null;
            }
            return new ApplicationReader(is);
        } catch (Exception e) {
            log.error("error while reading application from resource " + resourceName  + " : " + e.getMessage() , e);
            return null;
        }
    }

    /**
     * @javadoc
     */
    Vector<String> getApplicationsList() throws SearchQueryException {
        Vector<String> results = new Vector<String>(); //sigh, synchronized, for what?
        if (mmb == null) {
            log.warn("MMBase not yet initialized, Can't get to apps");
            return results;
        }
        Versions ver = (Versions) mmb.getBuilder("versions");
        if (ver == null) {
            log.warn("Versions builder not installed, Can't get to apps");
            return results;
        }

        ResourceLoader applicationLoader = ResourceLoader.getConfigurationRoot().getChildResourceLoader("applications");
        for (String appResource : applicationLoader.getResourcePaths(ResourceLoader.XML_PATTERN, false)) {
            log.debug("application " + appResource);
            ApplicationReader reader;
            try {
                reader = new ApplicationReader(applicationLoader.getInputSource(appResource));
            } catch (Exception e) {
                log.error(e);
                continue;
            }

            String name = reader.getName();
            results.add(name);
            results.add("" + reader.getVersion());
            int installedversion = ver.getInstalledVersion(name, "application");
            if (installedversion == -1) {
                results.add("no");
            } else {
                results.add("yes (ver : " + installedversion + ")");
            }
            results.add(reader.getMaintainer());
            boolean autodeploy = reader.hasAutoDeploy();
            if (autodeploy) {
                results.add("yes");
            } else {
                results.add("no");
            }
            results.add(org.mmbase.util.Casting.toString(reader.getRequirements()));
        }
        return results;
    }

    /**
     * @javadoc
     */
    List<String> getBuildersList() {
        Versions ver = (Versions)mmb.getBuilder("versions");
        List<String> results = new ArrayList<String>();

        List<String> builders = new ArrayList<String>();
        ResourceLoader builderLoader = mmb.getBuilderLoader();
        for (String builderResource:  builderLoader.getResourcePaths(ResourceLoader.XML_PATTERN, true)) {
            String builderName = ResourceLoader.getName(builderResource);
            BuilderReader reader = mmb.getBuilderReader(getXMLPath(builderName) + builderName);
            if (reader == null) {
                log.error("Did not find reader for " + builderResource);
                continue;
            }
            builders.add(builderName);
        }
        Collections.sort(builders);
        for (String builderName : builders) {
            results.add(builderName);
            BuilderReader reader = mmb.getBuilderReader(getXMLPath(builderName) + builderName);
            results.add("" + reader.getVersion());
            int installedversion = -1;
            if (ver != null) {
                installedversion = ver.getInstalledVersion(builderName, "builder");
            }
            if (installedversion == -1) {
                results.add("no");
            } else {
                results.add("yes");
            }
            results.add(reader.getMaintainer());
        }

        return results;
    }

    /**
     * @javadoc
     */
    Vector<String> getModuleProperties(String modulename) {
        Vector<String> results = new Vector<String>();
        ModuleReader mod = getModuleReader(modulename);
        if (mod != null) {
            Map<String,String> props = mod.getProperties();

            try {
                Map<String, String> contextMap = ApplicationContextReader.getProperties("mmbase/" + modulename);
                props.putAll(contextMap);
            } catch (Exception e) {
                log.error(e);
            }

            for (String key : props.keySet()) {
                String value = props.get(key);
                results.add(key);
                results.add(value);
            }
        }
        return results;
    }

    /**
     * @javadoc
     */
    Vector<String> getFields(String builderName) {
        Vector<String> results = new Vector<String>();
        BuilderReader bul = mmb.getBuilderReader(getXMLPath(builderName) + builderName);
        if (bul != null) {
            List<CoreField> defs = bul.getFields();
            for (CoreField def : defs) {
                results.add("" + def.getStoragePosition());
                results.add("" + def.getName());
                results.add(Fields.getTypeDescription(def.getType()));
                int size = def.getMaxLength();
                if (size == -1) {
                    results.add("fixed");
                } else {
                    results.add("" + size);
                }
            }
        }
        return results;
    }

    /**
     * @javadoc
     */
    Vector<String> getModulesList() {
        Vector<String> results = new Vector<String>();
        ResourceLoader moduleLoader = getModuleLoader();
        // new code checks all the *.xml files in modules dir
        for (String path : moduleLoader.getResourcePaths(ResourceLoader.XML_PATTERN, false)) {;
            String sname = ResourceLoader.getName(path);
            ModuleReader reader = getModuleReader(sname);
            if (reader == null) {
                log.error("Could not load module with xml '" + path + "'");
                continue;
            }
            results.add(sname);
            results.add("" + reader.getVersion());
            String status = reader.getStatus();
            if (status.equals("active")) {
                results.add("yes");
            } else {
                results.add("no");
            }
            results.add(reader.getMaintainer());
        }
        return results;
    }

    /**
     * @javadoc
     */
    Vector<String> getDatabasesList() {
        Versions ver = (Versions)mmb.getBuilder("versions");
        if (ver == null) {
            log.warn("Versions builder not installed, Can't get to builders");
            return null;
        }
        Vector<String> results = new Vector<String>();

        String path = MMBaseContext.getConfigPath() + File.separator + "databases" + File.separator;
        // new code checks all the *.xml files in builder dir
        File bdir = new File(path);
        if (bdir.isDirectory()) {
            String files[] = bdir.list();
            if (files == null)
                return results;
            for (String aname : files) {
                if (aname.endsWith(".xml")) {
                    String name = aname;
                    String sname = name.substring(0, name.length() - ".xml".length());
                    results.add(sname);

                    results.add("0");
                    results.add("yes");
                    results.add("mmbase.org");
                }
            }
        }
        return results;
    }

    /**
     * @javadoc
     */
    private String getBuilderField(String builderName, String fieldName, String key) {
        MMObjectBuilder bul = getBuilder(builderName);
        if (bul != null) {
            CoreField def = bul.getField(fieldName);
            if (def == null) throw new RuntimeException("No such field '" + fieldName + "' in builder '" + builderName + "'");
            if (key.equals("dbkey")) {
                if (def.isUnique()) {
                    return "true";
                } else {
                    return "false";
                }
            } else if (key.equals("dbnotnull")) {
                if (def.isRequired()) {
                    return "true";
                } else {
                    return "false";
                }
            } else if (key.equals("dbname")) {
                return def.getName();
            } else if (key.equals("dbsize")) {
                int size = def.getMaxLength();
                if (size != -1) {
                    return "" + size;
                } else {
                    return "fixed";
                }
            } else if (key.equals("dbstate")) {
                return Fields.getStateDescription(def.getState());
            } else if (key.equals("dbmmbasetype")) {
                return Fields.getTypeDescription(def.getType());
            } else if (key.equals("editorinput")) {
                int pos = def.getEditPosition();
                if (pos == -1) {
                    return "not shown";
                } else {
                    return "" + pos;
                }
            } else if (key.equals("editorsearch")) {
                int pos = def.getSearchPosition();
                if (pos == -1) {
                    return "not shown";
                } else {
                    return "" + pos;
                }
            } else if (key.equals("editorlist")) {
                int pos = def.getListPosition();
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
    private Vector<String> getISOGuiNames(String buildername, String fieldname) {
        Vector<String> results = new Vector<String>();
        MMObjectBuilder bul = getBuilder(buildername);
        if (bul != null) {
            CoreField def = bul.getField(fieldname);
            Map<Locale,String> guinames = def.getLocalizedGUIName().asMap();
            for (Entry<Locale, String> me : guinames.entrySet()) {
                results.add(me.getKey().getLanguage());
                results.add(me.getValue());
            }
        }
        return results;
    }

    /**
     * @javadoc
     */
    private Vector<String> getISODescriptions(String buildername, String fieldname) {
        Vector<String> results = new Vector<String>();
        MMObjectBuilder bul = getBuilder(buildername);
        if (bul != null) {
            CoreField def = bul.getField(fieldname);
            Map<Locale,String> guinames = def.getLocalizedDescription().asMap();
            for (Entry<Locale, String> me : guinames.entrySet()) {
                results.add(me.getKey().getLanguage());
                results.add(me.getValue());
            }
        }
        return results;
    }

    /**
     * @javadoc
     */
    private String getGuiNameValue(String buildername, String fieldname, String lang) {
        MMObjectBuilder bul = getBuilder(buildername);
        if (bul != null) {
            CoreField def = bul.getField(fieldname);
            String value = def.getGUIName(new Locale(lang, ""));
            if (value != null) {
                return value;
            }
        }
        return "";
    }

    /**
     * @javadoc
     */
    private String getDescription(String buildername, String fieldname, String lang) {
        MMObjectBuilder bul = getBuilder(buildername);
        if (bul != null) {
            CoreField def = bul.getField(fieldname);
            String value = def.getDescription(new Locale(lang, ""));
            if (value != null) {
                return value;
            }
        }
        return "";
    }

    /**
     * @javadoc
     */
    public void doModulePosts(String command, Hashtable<String,Object> cmds, Hashtable<String,Object> vars) {
        if (command.equals("SETPROPERTY")) {
            setModuleProperty(vars);
        }
    }

    /**
     * @javadoc
     */
    public void doBuilderPosts(String command, Hashtable<String,Object> cmds, Hashtable<String,Object> vars) {
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
            setBuilderDBField(vars);
        } else if (command.equals("SETSTATE")) {
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
    public void setBuilderGuiName(Hashtable<String,Object> vars) {
        if (kioskmode) {
            log.warn("refused gui name set, am in kiosk mode");
            return;
        }
        String builder = (String)vars.get("BUILDER");
        String fieldname = (String)vars.get("FIELDNAME");
        String country = (String)vars.get("COUNTRY");
        String value = (String)vars.get("VALUE");

        MMObjectBuilder bul = getBuilder(builder);
        CoreField def = bul.getField(fieldname);
        if (def != null) {
            def.setGUIName(value, new Locale(country, ""));
        }
    }

    /**
     * @javadoc
     * @since MMBase-1.7
     */
    public void setBuilderDescription(Hashtable<String,Object> vars) {
        if (kioskmode) {
            log.warn("refused gui name set, am in kiosk mode");
            return;
        }
        String builder = (String)vars.get("BUILDER");
        String fieldname = (String)vars.get("FIELDNAME");
        String country = (String)vars.get("COUNTRY");
        String value = (String)vars.get("VALUE");

        MMObjectBuilder bul = getBuilder(builder);
        CoreField def = bul.getField(fieldname);
        if (def != null) {
            def.setDescription(value, new Locale(country, ""));
        }
        // need to be rerouted syncBuilderXML(bul, builder);
    }

    /**
     * @javadoc
     */
    public void setBuilderGuiType(Hashtable<String,Object> vars) {
        if (kioskmode) {
            log.warn("refused gui type set, am in kiosk mode");
            return;
        }
        String builder = (String)vars.get("BUILDER");
        String fieldName = (String)vars.get("FIELDNAME");
        String guiType = (String)vars.get("VALUE");

        MMObjectBuilder bul = getBuilder(builder);
        CoreField def = bul.getField(fieldName);
        if (def != null) {
            DataType<? extends Object> dataType;
            int type = def.getType();
            if (type == Field.TYPE_LIST) {
                dataType = DataTypes.getDataTypeInstance(guiType, def.getListItemType());
            } else {
                dataType = DataTypes.getDataTypeInstance(guiType, type);
            }
            def.setDataType(dataType);
            def.finish();
        }
        // need to be rerouted syncBuilderXML(bul, builder);
    }

    /**
     * @javadoc
     */
    public void setBuilderEditorInput(Hashtable<String,Object> vars) {
        if (kioskmode) {
            log.warn("refused editor input set, am in kiosk mode");
            return;
        }
        String builder = (String)vars.get("BUILDER");
        String fieldname = (String)vars.get("FIELDNAME");
        String value = (String)vars.get("VALUE");

        MMObjectBuilder bul = getBuilder(builder);
        CoreField def = bul.getField(fieldname);
        if (def != null) {
            try {
                int i = Integer.parseInt(value);
                def.setEditPosition(i);
            } catch (Exception e) {}
        }
        // need to be rerouted syncBuilderXML(bul, builder);
    }

    /**
     * @javadoc
     */
    public void setBuilderEditorList(Hashtable<String,Object> vars) {
        if (kioskmode) {
            log.warn("refused editor list set, am in kiosk mode");
            return;
        }
        String builder = (String)vars.get("BUILDER");
        String fieldname = (String)vars.get("FIELDNAME");
        String value = (String)vars.get("VALUE");

        MMObjectBuilder bul = getBuilder(builder);
        CoreField def = bul.getField(fieldname);
        if (def != null) {
            try {
                int i = Integer.parseInt(value);
                def.setListPosition(i);
            } catch (Exception e) {}
        }
        // need to be rerouted syncBuilderXML(bul, builder);
    }

    /**
     * @javadoc
     */
    public void setBuilderEditorSearch(Hashtable<String,Object> vars) {
        if (kioskmode) {
            log.warn("refused editor pos set, am in kiosk mode");
            return;
        }
        String builder = (String)vars.get("BUILDER");
        String fieldname = (String)vars.get("FIELDNAME");
        String value = (String)vars.get("VALUE");

        MMObjectBuilder bul = getBuilder(builder);
        CoreField def = bul.getField(fieldname);
        if (def != null) {
            try {
                int i = Integer.parseInt(value);
                def.setSearchPosition(i);
            } catch (Exception e) {}
        }
        // need to be rerouted syncBuilderXML(bul, builder);
    }

    /**
     * @javadoc
     */
    public void setBuilderDBSize(Hashtable<String,Object> vars) {
        if (kioskmode) {
            log.warn("Refused set DBSize field, am in kiosk mode");
            return;
        }
        String builder = (String)vars.get("BUILDER");
        String fieldname = (String)vars.get("FIELDNAME");
        String value = (String)vars.get("VALUE");

        MMObjectBuilder bul = getBuilder(builder);
        CoreField def = bul.getField(fieldname);
        if (def != null) {
            int oldSize = def.getMaxLength();
            try {
                int newSize = Integer.parseInt(value);
                if (newSize != oldSize) {
                    def.rewrite();
                    try {
                        def.setMaxLength(newSize);
                        // make change in storage
                        mmb.getStorageManager().change(def);
                    } catch (StorageException se) {
                        def.setMaxLength(oldSize);
                        throw se;
                    } finally {
                        def.finish();
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
    public void setBuilderDBField(Hashtable<String,Object> vars) {
        if (kioskmode) {
            log.warn("Refused set setDBField field, am in kiosk mode");
            return;
        }
        String builder = (String)vars.get("BUILDER");
        String fieldname = (String)vars.get("FIELDNAME");
        String value = (String)vars.get("VALUE");

        MMObjectBuilder bul = getBuilder(builder);
        CoreField def = bul.getField(fieldname);
        if (def != null) {
            int oldType = def.getType();
            int newType = Fields.getType(value);
            if (oldType != newType) {
                def.rewrite();
                def.setType(newType);
                try {
                    // make change in storage
                    mmb.getStorageManager().change(def);
                    // need to be rerouted syncBuilderXML(bul, builder);
                } catch (StorageException se) {
                    def.setType(oldType);
                    throw se;
                } finally {
                    def.finish();
                }
            }
        }
    }

    /**
     * @javadoc
     */
    public void setBuilderDBState(Hashtable<String,Object> vars) {
        if (kioskmode) {
            log.warn("Refused set DBState field, am in kiosk mode");
            return;
        }
    log.info("SET DBDSTATE");
        String builder = (String)vars.get("BUILDER");
        String fieldname = (String)vars.get("FIELDNAME");
        String value = (String)vars.get("VALUE");

        MMObjectBuilder bul = getBuilder(builder);
        CoreField def = bul.getField(fieldname);
        if (def != null) {
            int oldState = def.getState();
            int newState = Fields.getState(value);
            if (oldState != newState) {
                boolean oldInStorage = def.inStorage();
                def.rewrite();
                def.setState(newState);
                try {
                    // add field if it was not persistent before
                    if (def.inStorage() && !oldInStorage) {
                        // make change in storage
                        mmb.getStorageManager().create(def);
                        // only then add to builder
                        bul.addField(def);
                        // need to be rerouted syncBuilderXML(bul, builder);
                    }
                } catch (StorageException se) {
                    def.setState(oldState);
                    throw se;
                } finally {
                    def.finish();
                }
            }
        }
    }

    /**
     * @javadoc
     */
    public void setBuilderDBKey(Hashtable<String,Object> vars) {
        if (kioskmode) {
            log.warn("Refused set dbkey field, am in kiosk mode");
            return;
        }
    log.info("SET DBKEY");
        String builder = (String)vars.get("BUILDER");
        String fieldname = (String)vars.get("FIELDNAME");
        String value = (String)vars.get("VALUE");

        MMObjectBuilder bul = getBuilder(builder);
        CoreField def = bul.getField(fieldname);
        if (def != null) {
            def.rewrite();
            if (value.equals("true")) {
                def.setUnique(true);
            } else {
                def.setUnique(false);
            }
            def.finish();
        }
        // TODO: when changing key, should call CHANGE
        // need to be rerouted syncBuilderXML(bul, builder);
    }

    /**
     * @javadoc
     */
    public void setBuilderDBNotNull(Hashtable<String,Object> vars) {
        if (kioskmode) {
            log.warn("Refused set NotNull field, am in kiosk mode");
            return;
        }
        String builder = (String)vars.get("BUILDER");
        String fieldname = (String)vars.get("FIELDNAME");
        String value = (String)vars.get("VALUE");

        MMObjectBuilder bul = getBuilder(builder);
        CoreField def = bul.getField(fieldname);
        if (def != null) {
            boolean oldNotNull = def.isRequired();
            boolean newNotNull = value.equals("true");
            if (oldNotNull != newNotNull) {
                def.rewrite();
                def.getDataType().setRequired(newNotNull);
                try {
                    // make change in storage
                    mmb.getStorageManager().change(def);
                    // need to be rerouted syncBuilderXML(bul, builder);
                } catch (StorageException se) {
                    def.getDataType().setRequired(oldNotNull);
                    throw se;
                } finally {
                    def.finish();
                }
            }
        }
    }

    /**
     * @javadoc
     */
    public void addBuilderField(Map<String,Object> vars) {
        if (kioskmode) {
            log.warn("Refused add builder field, am in kiosk mode");
            return;
        }
        String builder = (String)vars.get("BUILDER");
        MMObjectBuilder bul = getBuilder(builder);
        if (bul != null) {
            // Determine position of new field.
            // This should be the number of the last field as denied in the builder xml,
            // as the DBPos field is incremented for each field in that file.
            int pos = bul.getFields(NodeManager.ORDER_CREATE).size() + 1;


            String fieldName = (String)vars.get("dbname");
            String guiType = (String)vars.get("guitype");
            int type = Fields.getType((String)vars.get("mmbasetype"));
            int itemListType = Fields.getType((String)vars.get("mmbasetype"));
            int state = Fields.getState((String)vars.get("dbstate"));

            log.service("Adding field " + fieldName);
            DataType<? extends Object> dataType;
            if (type ==  Field.TYPE_LIST) {
                dataType = DataTypes.getListDataTypeInstance(guiType, itemListType);
            } else {
                dataType = DataTypes.getDataTypeInstance(guiType, type);
            }
            log.debug("Found datatype " + dataType);

            CoreField def = Fields.createField(fieldName, type, itemListType, state, dataType);
            def.setListPosition(pos);
            def.setEditPosition(pos);

            def.setParent(bul);
            def.setStoragePosition(pos);

            String value = (String)vars.get("dbnotnull");
            def.getDataType().setRequired(value.equals("true"));

            value = (String)vars.get("dbkey");
            def.setUnique(value.equals("true"));


            value = (String)vars.get("dbsize");
            try {
                int i = Integer.parseInt(value);
                def.setMaxLength(i);
            } catch (Exception e) {
                log.debug("dbsize had invalid value, not setting size");
            }

            log.debug("Found field definition " + def);

            log.trace("Adding to storage");
            // make change in storage
            mmb.getStorageManager().create(def);
            log.trace("Adding to builder");
            // only then add to builder
            bul.addField(def);
            def.finish();
        } else {
            log.service("Cannot add field to builder " + builder + " because it could not be found");
        }
    }

    /**
     * @javadoc
     */
    public void removeBuilderField(Hashtable<String,Object> vars) {
        if (kioskmode) {
            log.warn("Refused remove builder field, am in kiosk mode");
            return;
        }
        String builder = (String) vars.get("BUILDER");
        String fieldname = (String) vars.get("FIELDNAME");
        String value = (String) vars.get("SURE");

        MMObjectBuilder bul = getBuilder(builder);
        if (bul != null && value != null && value.equals("Yes")) {

            CoreField def = bul.getField(fieldname);
            // make change in storage
            mmb.getStorageManager().delete(def);
            // only then delete in builder
            bul.removeField(fieldname);

            def.finish();
        }
    }

    /**
     * @javadoc
     */
    public void syncBuilderXML(MMObjectBuilder bul, String builder) {
        String savepath =
            MMBaseContext.getConfigPath() + "/builders/" + builder + ".xml";
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
    public Vector<String> getMultilevelCacheEntries() {
        Vector<String> results = new Vector<String>();
        for (Map.Entry<org.mmbase.storage.search.SearchQuery, List<MMObjectNode>> entry : MultilevelCache.getCache().entrySet()) {
            /*
            StringTagger tagger=en.getTagger();
            Vector type=tagger.Values("TYPE");
            Vector where=tagger.Values("WHERE");
            Vector dbsort=tagger.Values("DBSORT");
            Vector dbdir=tagger.Values("DBDIR");
            Vector fields=tagger.Values("FIELDS");
            results.add(""+en.getKey());
            results.add(""+type);
            results.add(""+fields);
            if (where!=null) {
                results.add(where.toString());
            } else {
                results.add("");
            }
            if (dbsort!=null) {
                results.add(dbsort.toString());
            } else {
                results.add("");
            }
            if (dbdir!=null) {
                results.add(dbdir.toString());
            } else {
                results.add("");
            }
            results.add(tagger.ValuesString("ALL"));
            */
            results.add(entry.getKey().toString());
            results.add("" + MultilevelCache.getCache().getCount(entry.getKey()));
        }
        return results;
    }

    /**
     * @javadoc
     */
    public Vector<String> getNodeCacheEntries() {
        Vector<String> results = new Vector<String>();
        for (MMObjectNode node :  NodeCache.getCache().values()) {
            results.add("" + NodeCache.getCache().getCount(node.getIntegerValue("number")));
            results.add("" + node.getIntValue("number"));
            results.add(node.getStringValue("owner"));
            results.add(mmb.getTypeDef().getValue(node.getIntValue("otype")));
        }
        return results;
    }

}
