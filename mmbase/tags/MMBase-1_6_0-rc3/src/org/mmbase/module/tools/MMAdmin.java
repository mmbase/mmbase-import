/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.tools;

import java.util.*;
import java.io.File;

import org.mmbase.util.*;
import org.mmbase.util.xml.BuilderWriter;
import org.mmbase.util.xml.ModuleWriter;
import org.mmbase.module.*;
import org.mmbase.cache.MultilevelCacheHandler;
import org.mmbase.cache.MultilevelCacheEntry;
import org.mmbase.module.core.*;
import org.mmbase.module.builders.Versions;
import org.mmbase.module.builders.Message; // dependency, needs to be removed
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.tools.MMAppTool.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * @javadoc
 *
 * @author Daniel Ockeloen
 * @author Pierre van Rooden
 * @version $Id: MMAdmin.java,v 1.54.2.1 2002-11-25 08:46:42 pierre Exp $
 */
public class MMAdmin extends ProcessorModule {

    // logging routines
    private static Logger log = Logging.getLoggerInstance(MMAdmin.class.getName());

    /**
     * reference to MMBase
     * @scope private
     */
    MMBase mmb=null;
    /**
     * @javadoc
     * @scope private
     */
    MMAdminProbe probe=null;
    /**
     * @javadoc
     * @scope private
     */
    String lastmsg="";
    /**
     * @javadoc
     */
    private boolean restartwanted=false;
    /**
     * @javadoc
     */
    private boolean kioskmode=false;

    /**
     * @javadoc
     */
    public void init() {
        String dtmp=System.getProperty("mmbase.kiosk");
        if (dtmp!=null && dtmp.equals("yes")) {
            kioskmode=true;
            log.info("*** Server started in kiosk mode ***");
        }
        mmb=(MMBase)getModule("MMBASEROOT");
        probe = new MMAdminProbe(this);
    }

    /**
     * @javadoc
     */
    public MMAdmin() {
    }

    /**
     * Retrieves a specified builder.
     * The builder's name can be extended with the subpath of that builder's configuration file.
     * i.e. 'core/typedef' or 'basic/images'. The subpath part is ignored.
     * @param name The path of the builder to retrieve
     * @return a <code>MMObjectBuilder</code> is found, <code>null</code> otherwise
     */
    public MMObjectBuilder getMMObject(String path) {
        int pos=path.lastIndexOf(File.separator);
        if (pos!=-1) {
            path=path.substring(pos+1);
        }
        return mmb.getMMObject(path);
    }

    /**
     * Generate a list of values from a command to the processor
     * @javadoc
     */
    public Vector getList(scanpage sp,StringTagger tagger, String value) throws ParseException {
        String line = Strip.DoubleQuote(value,Strip.BOTH);
        StringTokenizer tok = new StringTokenizer(line,"-\n\r");
        if (tok.hasMoreTokens()) {
            String cmd=tok.nextToken();
            if(!checkUserLoggedOn(sp,cmd,false)) return new Vector();
            if (cmd.equals("APPLICATIONS")) {
                tagger.setValue("ITEMS","5");
                return getApplicationsList();
            }
            if (cmd.equals("BUILDERS")) {
                tagger.setValue("ITEMS","4");
                return getBuildersList(tok);
            }
            if (cmd.equals("FIELDS")) {
                tagger.setValue("ITEMS","4");
                return getFields(tok.nextToken());
            }
            if (cmd.equals("MODULEPROPERTIES")) {
                tagger.setValue("ITEMS","2");
                return getModuleProperties(tok.nextToken());
            }
            if (cmd.equals("ISOGUINAMES")) {
                tagger.setValue("ITEMS","2");
                return getISOGuiNames(tok.nextToken(),tok.nextToken());
            }
            if (cmd.equals("MODULES")) {
                tagger.setValue("ITEMS","4");
                return getModulesList();
            }
            if (cmd.equals("DATABASES")) {
                tagger.setValue("ITEMS","4");
                return getDatabasesList();
            }
            if (cmd.equals("MULTILEVELCACHEENTRIES")) {
                tagger.setValue("ITEMS","8");
                return getMultilevelCacheEntries();
            }
            if (cmd.equals("NODECACHEENTRIES")) {
                tagger.setValue("ITEMS","4");
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
            user=HttpAuth.getAuthorization(sp.req,sp.res,"www","Basic");
        } catch (javax.servlet.ServletException e) { }
        boolean authorized=(user!=null) && (!adminonly || "admin".equals(user));
        if (!authorized) {
            lastmsg="Unauthorized access : "+cmd+" by "+user;
            log.info(lastmsg);
        }
        return authorized;
    }

    /**
     * Execute the commands provided in the form values
     * @javadoc
     */
    public boolean process(scanpage sp, Hashtable cmds,Hashtable vars) {
        String cmdline,token;

        for (Enumeration h = cmds.keys();h.hasMoreElements();) {
            cmdline=(String)h.nextElement();
            if(!checkAdmin(sp,cmdline)) return false;
            StringTokenizer tok = new StringTokenizer(cmdline,"-\n\r");
            token = tok.nextToken();
            if (token.equals("SERVERRESTART")) {
                String user=(String)cmds.get(cmdline);
                doRestart(user);
            } else if (token.equals("LOAD") && !kioskmode) {
                Versions ver=(Versions)mmb.getMMObject("versions");
                String appname=(String)cmds.get(cmdline);
                String path=MMBaseContext.getConfigPath()+File.separator+"applications"+File.separator;
                XMLApplicationReader app=new XMLApplicationReader(path+appname+".xml");
                if (app!=null) {
                    String name=app.getApplicationName();
                    String maintainer=app.getApplicationMaintainer();
                    int version=app.getApplicationVersion();
                    int installedversion=ver.getInstalledVersion(name,"application");
                    if (installedversion==-1 || version>installedversion) {
                        if (installedversion==-1) {
                            log.info("Installing application : "+name);
                        } else {
                            log.info("installing application : "+name+" new version from "+installedversion+" to "+version);
                        }
                        if (installApplication(name)) {
                            lastmsg="Application loaded oke<BR><BR>\n";
                            lastmsg+="The application has the following install notice for you : <BR><BR>\n";
                            lastmsg+=app.getInstallNotice();
                            if (installedversion==-1) {
                                ver.setInstalledVersion(name,"application",maintainer,version);
                            } else {
                                ver.updateInstalledVersion(name,"application",maintainer,version);
                            }
                        } else {
                            log.warn("Problem installing application : "+name);
                        }
                    } else {
                            lastmsg="Application was allready loaded (or a higher version)<BR><BR>\n";
                            lastmsg+="To remind you here is the install notice for you again : <BR><BR>\n";
                            lastmsg+=app.getInstallNotice();
                    }
                } else {
                    lastmsg="Install error can't find xml file";
                }
            } else if (token.equals("SAVE")) {
                String appname=(String)cmds.get(cmdline);
                String savepath=(String)vars.get("PATH");
                String goal=(String)vars.get("GOAL");
                log.info("APP="+appname+" P="+savepath+" G="+goal);
                writeApplication(appname,savepath,goal);
            } else if (token.equals("APPTOOL")) {
                String appname=(String)cmds.get(cmdline);
                startAppTool(appname);
            } else if (token.equals("BUILDER")) {
                doBuilderPosts(tok.nextToken(),cmds,vars);
            } else if (token.equals("MODULE")) {
                doModulePosts(tok.nextToken(),cmds,vars);
            } else if (token.equals("MODULESAVE")) {
                if (kioskmode) {
                    log.warn("MMAdmin> refused to write module, am in kiosk mode");
                } else {
                    String modulename=(String)cmds.get(cmdline);
                    String savepath=(String)vars.get("PATH");
                    Module mod=(Module)getModule(modulename);
                    if (mod!=null) {
                        try {
                            ModuleWriter moduleOut=new ModuleWriter(mod);
                            moduleOut.setIncludeComments(true);
                            moduleOut.writeToFile(savepath);
                        } catch (Exception e) {
                            log.error(Logging.stackTrace(e));
                            lastmsg="Writing finished, problems occurred<br /><br />\n"+
                                    "Error encountered="+e.getMessage()+"<br /><br />\n";
                            return false;
                        }
                        lastmsg="Writing finished, no problems.<br /><br />\n"+
                                "A clean copy of "+modulename+".xml can be found at : "+savepath+"<br /><br />\n";
                    }
                }
            } else if (token.equals("BUILDERSAVE")) {
                if (kioskmode) {
                    log.warn("MMAdmin> refused to write builder, am in kiosk mode");
                } else {
                    String buildername=(String)cmds.get(cmdline);
                    String savepath=(String)vars.get("PATH");
                    MMObjectBuilder bul=getMMObject(buildername);
                    if (bul!=null) {
                        try {
                            BuilderWriter builderOut=new BuilderWriter(bul);
                            builderOut.setIncludeComments(true);
                            builderOut.setExpandBuilder(true);
                            builderOut.writeToFile(savepath);
                        } catch (Exception e) {
                            log.error(Logging.stackTrace(e));
                            lastmsg="Writing finished, problems occurred<br /><br />\n"+
                                    "Error encountered="+e.getMessage()+"<br /><br />\n";
                            return false;
                        }
                        lastmsg="Writing finished, no problems.<br /><br />\n"+
                                "A clean copy of "+buildername+".xml can be found at : "+savepath+"<br /><br />\n";
                    }
                }
            }

        }
        return false;
    }

    /**
     * Handle a $MOD command
     * @javadoc
     */
    public String replace(scanpage sp, String cmds) {
        if(!checkUserLoggedOn(sp,cmds,false)) return "";
        StringTokenizer tok = new StringTokenizer(cmds,"-\n\r");
        if (tok.hasMoreTokens()) {
            String cmd=tok.nextToken();
            if (cmd.equals("VERSION")) {
                return ""+getVersion(tok.nextToken());
            } else if (cmd.equals("DESCRIPTION")) {
                return getDescription(tok.nextToken());
            } else if (cmd.equals("LASTMSG")) {
                return lastmsg;
            } else if (cmd.equals("BUILDERVERSION")) {
                return ""+getBuilderVersion(tok.nextToken());
            } else if (cmd.equals("BUILDERCLASSFILE")) {
                return ""+getBuilderClass(tok.nextToken());
            } else if (cmd.equals("BUILDERDESCRIPTION")) {
                return ""+getBuilderDescription(tok.nextToken());
            } else if (cmd.equals("GETGUINAMEVALUE")) {
                return getGuiNameValue(tok.nextToken(),tok.nextToken(),tok.nextToken());
            } else if (cmd.equals("GETBUILDERFIELD")) {
                return getBuilderField(tok.nextToken(),tok.nextToken(),tok.nextToken());
            } else if (cmd.equals("GETMODULEPROPERTY")) {
                return getModuleProperty(tok.nextToken(),tok.nextToken());
            } else if (cmd.equals("MODULEDESCRIPTION")) {
                return ""+getModuleDescription(tok.nextToken());
            } else if (cmd.equals("MODULECLASSFILE")) {
                return ""+getModuleClass(tok.nextToken());
            } else if (cmd.equals("MULTILEVELCACHEHITS")) {
                return(""+MultilevelCacheHandler.getCache().getHits());
            } else if (cmd.equals("MULTILEVELCACHEMISSES")) {
                return(""+MultilevelCacheHandler.getCache().getMisses());
            } else if (cmd.equals("MULTILEVELCACHEREQUESTS")) {
                return(""+(MultilevelCacheHandler.getCache().getHits()+MultilevelCacheHandler.getCache().getMisses()));
            } else if (cmd.equals("MULTILEVELCACHEPERFORMANCE")) {
                return(""+(MultilevelCacheHandler.getCache().getRatio()*100));
            } else if (cmd.equals("MULTILEVELCACHESTATE")) {
                if (tok.hasMoreTokens()) {
                    String state=tok.nextToken();
                    if (state.equalsIgnoreCase("On")) {
                        MultilevelCacheHandler.getCache().setActive(true);
                        log.info("turned multilevelcache on");
                    } else if (state.equalsIgnoreCase("Off")) {
                        MultilevelCacheHandler.getCache().setActive(false);
                        log.info("turned multilevelcache off");
                    }
                } else {
                    if (MultilevelCacheHandler.getCache().isActive()) {
                        return "On";
                    } else {
                        return "Off";
                    }
                }
            } else if (cmd.equals("MULTILEVELCACHESIZE")) {
                return(""+(MultilevelCacheHandler.getCache().getSize()));
            } else if (cmd.equals("NODECACHEHITS")) {
                return(""+MMObjectBuilder.nodeCache.getHits());
            } else if (cmd.equals("NODECACHEMISSES")) {
                return(""+MMObjectBuilder.nodeCache.getMisses());
            } else if (cmd.equals("NODECACHEREQUESTS")) {
                return(""+(MMObjectBuilder.nodeCache.getHits()+MMObjectBuilder.nodeCache.getMisses()));
            } else if (cmd.equals("NODECACHEPERFORMANCE")) {
                return(""+(MMObjectBuilder.nodeCache.getRatio()*100));
            } else if (cmd.equals("NODECACHESIZE")) {
                return(""+(MMObjectBuilder.nodeCache.getSize()));
            } else if (cmd.equals("TEMPORARYNODECACHESIZE")) {
                return(""+(MMObjectBuilder.TemporaryNodes.size()));
            } else if (cmd.equals("RELATIONCACHEHITS")) {
                return(""+MMObjectNode.getRelationCacheHits());
            } else if (cmd.equals("RELATIONCACHEMISSES")) {
                return(""+MMObjectNode.getRelationCacheMiss());
            } else if (cmd.equals("RELATIONCACHEREQUESTS")) {
                return(""+(MMObjectNode.getRelationCacheHits()+MMObjectNode.getRelationCacheMiss()));
            } else if (cmd.equals("RELATIONCACHEPERFORMANCE")) {

            return(""+(1.0*MMObjectNode.getRelationCacheHits())/(MMObjectNode.getRelationCacheHits()+MMObjectNode.getRelationCacheMiss()+0.0000000001)*100);
            }
        }
        return "No command defined";
    }

    /**
     * @javadoc
     */
    int getVersion(String appname) {
        String path=MMBaseContext.getConfigPath()+File.separator+"applications"+File.separator;
        XMLApplicationReader app=new XMLApplicationReader(path+appname+".xml");
        if (app!=null) {
            return app.getApplicationVersion();
        }
        return -1;
    }

    /**
     * @javadoc
     */
    int getBuilderVersion(String appname) {
        String path=MMBaseContext.getConfigPath()+File.separator+"builders"+File.separator;
        XMLBuilderReader app=new XMLBuilderReader(path+appname+".xml",mmb);
        if (app!=null) {
            return app.getBuilderVersion();
        }
        return -1;
    }

    /**
     * @javadoc
     */
    String getBuilderClass(String bulname) {
        String path=MMBaseContext.getConfigPath()+File.separator+"builders"+File.separator;
        XMLBuilderReader bul=new XMLBuilderReader(path+bulname+".xml",mmb);
        if (bul!=null) {
            return bul.getClassFile();
        }
        return "";
    }

    /**
     * @javadoc
     */
    String getModuleClass(String modname) {
        String path=MMBaseContext.getConfigPath()+File.separator+"modules"+File.separator;
        XMLModuleReader mod=new XMLModuleReader(path+modname+".xml");
        if (mod!=null) {
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
        String modname=(String)vars.get("MODULE");
        String key=(String)vars.get("PROPERTYNAME");
        String value=(String)vars.get("VALUE");
        Module mod=(Module)getModule(modname);
        log.debug("MOD="+mod);
        if (mod!=null) {
            mod.setInitParameter(key,value);
            syncModuleXML(mod,modname);
        }

    }

    /**
     * @javadoc
     * @todo should obtain data from the configuration file
     */
    String getModuleProperty(String modname,String key) {
        /*
        String path=MMBaseContext.getConfigPath()+File.separator+"modules"+File.separator;
        XMLModuleReader mod=new XMLModuleReader(path+modname+".xml");
        if (mod!=null) {
            Hashtable props=mod.getProperties();
            String value=(String)props.get(key);
            return value;
        }
        */
        Module mod=(Module)getModule(modname);
        if (mod!=null) {
            String value=mod.getInitParameter(key);
            if (value!=null) return value;
        }
        return "";

    }

    /**
     * @javadoc
     */
    String getDescription(String appname) {
        String path=MMBaseContext.getConfigPath()+File.separator+"applications"+File.separator;
        XMLApplicationReader app=new XMLApplicationReader(path+appname+".xml");
        if (app!=null) {
            return app.getDescription();
        }
        return "";
    }

    /**
     * @javadoc
     */
    String getBuilderDescription(String appname) {
        String path=MMBaseContext.getConfigPath()+File.separator+"builders"+File.separator;
        XMLBuilderReader app=new XMLBuilderReader(path+appname+".xml",mmb);
        if (app!=null) {
            Hashtable desc=app.getDescriptions();
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
        Module mod=(Module)getModule(modulename);
        if (mod!=null) {
            String value=mod.getModuleInfo();
            if (value!=null) return value;
        }
        return "";
    }

    /**
     * @javadoc
     */
    public void maintainance() {
    }

    /**
     * @javadoc
     * @bad-literal time for MMAdminProbe should be a constant or configurable
     */
    public void doRestart(String user) {
        if (kioskmode) {
            log.warn("MMAdmin> refused to reset the server, am in kiosk mode");
            return;
        }
        lastmsg="Server Reset requested by '"+user+"' Restart in 3 seconds<BR><BR>\n";
        log.info("Server Reset requested by '"+user+"' Restart in 3 seconds");
        restartwanted=true;
        probe = new MMAdminProbe(this,3*1000);
    }

    /**
     * @javadoc
     */
    private boolean startAppTool(String appname) {
        if (kioskmode) {
            log.warn("refused starting app tool, am in kiosk mode");
            return false;
        }

        String path=MMBaseContext.getConfigPath()+File.separator+"applications"+File.separator;
        log.info("Starting apptool with : "+path+File.separator+appname+".xml");
        MMAppTool app=new MMAppTool(path+File.separator+appname+".xml");
        lastmsg="Started a instance of the MMAppTool with path : <BR><BR>\n";
        lastmsg+=path+File.separator+appname+".xml<BR><BR>\n";
        return true;
    }

    /**
     * @javadoc
     */
    private boolean installApplication(String applicationname) {
        String path=MMBaseContext.getConfigPath()+File.separator+"applications"+File.separator;
        XMLApplicationReader app=new XMLApplicationReader(path+applicationname+".xml");
        if (app!=null) {
            if (areBuildersLoaded(app.getNeededBuilders(), path + applicationname)) {
                if (checkRelDefs(app.getNeededRelDefs())) {
                    if (checkAllowedRelations(app.getAllowedRelations())) {
                        if (installDataSources(app.getDataSources(),applicationname)) {
                            if (installRelationSources(app.getRelationSources())) {
                            } else {
                                log.warn("Application installer stopped : can't install relationsources");
                                return false;
                            }
                        } else {
                            log.warn("Application installer stopped : can't install datasources");
                            return false;
                        }
                    } else {
                        log.warn("Application installer stopped : can't install allowed relations");
                        return false;
                    }
                } else {
                    log.warn("Application installer stopped : can't install reldefs");
                    return false;
                }
            } else {
                log.warn("Application installer stopped : not all needed builders present");
                return false;
            }
        } else {
            log.warn("Can't install application : "+path+applicationname+".xml");
        }
        return true;
    }

    /**
     * @javadoc
     */
    boolean installDataSources(Vector ds,String appname) {
        for (Enumeration h = ds.elements();h.hasMoreElements();) {
            Hashtable bh=(Hashtable)h.nextElement();
            String path=(String)bh.get("path");
            String prepath=MMBaseContext.getConfigPath()+File.separator+"applications"+File.separator;

            if (fileExists(prepath+path)) {
            XMLNodeReader nodereader=new XMLNodeReader(prepath+path,prepath+appname+File.separator,mmb);

            String exportsource=nodereader.getExportSource();
            int timestamp=nodereader.getTimeStamp();

            MMObjectBuilder syncbul=mmb.getMMObject("syncnodes");
            if (syncbul!=null) {
                Vector importednodes= new Vector();
                for (Enumeration n = nodereader.getNodes(mmb).elements();n.hasMoreElements();) {
                    MMObjectNode newnode=(MMObjectNode)n.nextElement();
                    int exportnumber=newnode.getIntValue("number");
                    String query="exportnumber=="+exportnumber+"+exportsource=='"+exportsource+"'";
                    Enumeration b=syncbul.search(query);
                    if (b.hasMoreElements()) {
                        // XXX To do : we may want to load the node and check/change the fields
                        MMObjectNode syncnode=(MMObjectNode)b.nextElement();
                        log.debug("node allready installed : "+exportnumber);
                    } else {
                        newnode.setValue("number",-1);
                        int localnumber=doKeyMergeNode(newnode);
                        if (localnumber!=-1) {
                            MMObjectNode syncnode=syncbul.getNewNode("import");
                            syncnode.setValue("exportsource",exportsource);
                            syncnode.setValue("exportnumber",exportnumber);
                            syncnode.setValue("timestamp",timestamp);
                            syncnode.setValue("localnumber",localnumber);
                            syncnode.insert("import");
                            if ((localnumber==newnode.getNumber()) &&
                                (newnode.parent instanceof Message)) {
                                importednodes.add(newnode);
                            }
                        }
                    }
                }
                for (Enumeration n = importednodes.elements();n.hasMoreElements();) {
                    MMObjectNode importnode=(MMObjectNode)n.nextElement();
                    log.info(importnode.toString());
                    int exportnumber=importnode.getIntValue("thread");
                    int localnumber=-1;
                    Enumeration b=syncbul.search("exportnumber=="+exportnumber+"+exportsource=='"+exportsource+"'");
                    if (b.hasMoreElements()) {
                        MMObjectNode n2=(MMObjectNode)b.nextElement();
                        localnumber=n2.getIntValue("localnumber");
                    }
                    importnode.setValue("thread",localnumber);
                    importnode.commit();
                }
            } else {
                log.warn("Application installer : can't reach syncnodes builder");
            }
            }
        }
        return true;
    }

    /**
     * @javadoc
     */
    private int doKeyMergeNode(MMObjectNode newnode) {
        MMObjectBuilder bul=newnode.parent;
        if (bul!=null) {
            String checkQ="";
            Vector vec=bul.getFields();
            for (Enumeration h = vec.elements();h.hasMoreElements();) {
                FieldDefs def=(FieldDefs)h.nextElement();
                if (def.isKey()) {
                    int type=def.getDBType();
                    String name=def.getDBName();
                    if (type==FieldDefs.TYPE_STRING) {
                        String value=newnode.getStringValue(name);
                        if (checkQ.equals("")) {
                            checkQ+=name+"=='"+value+"'";
                        } else {
                            checkQ+="+"+name+"=='"+value+"'";
                        }
                    }
                }
            }
            if (!checkQ.equals("")) {
                Enumeration r=bul.search(checkQ);
                if (r.hasMoreElements()) {
                    MMObjectNode oldnode=(MMObjectNode)r.nextElement();
                    return oldnode.getIntValue("number");
                } else {
                    // so no dub
                    int localnumber=newnode.insert("import");
                    return localnumber;
                }

            } else {
                int localnumber=newnode.insert("import");
                return localnumber;
            }
        } else {
            log.warn("Application installer can't find builder for : "+newnode);
        }
        return -1;
    }

    /**
     * @javadoc
     */
    boolean installRelationSources(Vector ds) {
        for (Enumeration h = ds.elements();h.hasMoreElements();) {
            Hashtable bh=(Hashtable)h.nextElement();
            String path=(String)bh.get("path");
            path=MMBaseContext.getConfigPath()+File.separator+"applications"+File.separator+path;
            if (fileExists(path)) {
            XMLRelationNodeReader nodereader=new XMLRelationNodeReader(path,mmb);

            String exportsource=nodereader.getExportSource();
            int timestamp=nodereader.getTimeStamp();

            MMObjectBuilder syncbul=mmb.getMMObject("syncnodes");
            if (syncbul!=null) {
                for (Enumeration n = (nodereader.getNodes(mmb)).elements();n.hasMoreElements();) {
                    MMObjectNode newnode=(MMObjectNode)n.nextElement();
                    int exportnumber=newnode.getIntValue("number");
                    Enumeration b=syncbul.search("exportnumber=="+exportnumber+"+exportsource=='"+exportsource+"'");
                    if (b.hasMoreElements()) {
                        // XXX To do : we may want to load the relation node and check/change the fields
                        MMObjectNode syncnode=(MMObjectNode)b.nextElement();
                        log.debug("node allready installed : "+exportnumber);
                    } else {
                        newnode.setValue("number",-1);
                        // The following code determines the 'actual' (synced) numbers for the destination and source nodes
                        // This will normally work well, however:
                        // It is _theoretically_ possible that one or both nodes are _themselves_ relation nodes.
                        // (since relations are nodes).
                        // Due to the order in which syncing takles place, it is possible that such structures will fail
                        // to get imported.
                        // ye be warned.

                        // find snumber

                        int snumber=newnode.getIntValue("snumber");
                        b=syncbul.search("exportnumber=="+snumber+"+exportsource=='"+exportsource+"'");
                        if (b.hasMoreElements()) {
                            MMObjectNode n2=(MMObjectNode)b.nextElement();
                            snumber=n2.getIntValue("localnumber");
                        } else {
                            snumber=-1;
                        }

                        // find dnumber
                        int dnumber=newnode.getIntValue("dnumber");
                        b=syncbul.search("exportnumber=="+dnumber+"+exportsource=='"+exportsource+"'");
                        if (b.hasMoreElements()) {
                            MMObjectNode n2=(MMObjectNode)b.nextElement();
                            dnumber=n2.getIntValue("localnumber");
                        } else {
                            dnumber=-1;
                        }

                        newnode.setValue("snumber",snumber);
                        newnode.setValue("dnumber",dnumber);
                        int localnumber=-1;
                        if (snumber!=-1 && dnumber!=-1) {
                            localnumber=newnode.insert("import");
                            if (localnumber!=-1) {
                                MMObjectNode syncnode=syncbul.getNewNode("import");
                                syncnode.setValue("exportsource",exportsource);
                                syncnode.setValue("exportnumber",exportnumber);
                                syncnode.setValue("timestamp",timestamp);
                                syncnode.setValue("localnumber",localnumber);
                                syncnode.insert("import");
                            }
                        } else {
                            log.warn("Cannot sync relation (exportnumber=="+exportnumber+", snumber:"+snumber+", dnumber:"+dnumber+")");
                        }
                    }
                }
            } else {
                log.warn("Application installer : can't reach syncnodes builder");
            }
            }
        }
        return true;
    }

    /**
     * Checks needed relation definitions.
     * Retrieves, for each reldef entry, the attributes, and passe sthese on to {@link #checkRelDef}
     * @param reldefs a list of hashtables. Each hashtable represents a reldef entry, and contains a list of name-value
     *      pairs (the reldef attributes).
     * @return Always <code>true</code> (?)
     */
    boolean checkRelDefs(Vector reldefs) {
        for (Enumeration h = reldefs.elements();h.hasMoreElements();) {
            Hashtable bh=(Hashtable)h.nextElement();
            String source=(String)bh.get("source");
            String target=(String)bh.get("target");
            String direction=(String)bh.get("direction");
            String guisourcename=(String)bh.get("guisourcename");
            String guitargetname=(String)bh.get("guitargetname");
            // retrieve builder info
            int builder=-1;
            if (mmb.getRelDef().usesbuilder) {
                String buildername=(String)bh.get("builder");
                // if no 'builder' attribute is present (old format), use source name as builder name
                if (buildername==null) {
                    buildername=(String)bh.get("source");
                }
                builder=mmb.getTypeDef().getIntValue(buildername);
            }
            // is not explicitly set to unidirectional, direction is assumed to be bidirectional
            if ("unidirectional".equals(direction)) {
                checkRelDef(source,target,1,guisourcename,guitargetname,builder);
            } else {
                checkRelDef(source,target,2,guisourcename,guitargetname,builder);
            }
        }
        return true;
    }

    /**
     * @javadoc
     */
    boolean checkAllowedRelations(Vector relations) {
        for (Enumeration h = relations.elements();h.hasMoreElements();) {
            Hashtable bh=(Hashtable)h.nextElement();
            String from=(String)bh.get("from");
            String to=(String)bh.get("to");
            String type=(String)bh.get("type");
            checkTypeRel(from,to,type,-1);
        }
        return true;
    }

    /**
     * @javadoc
     */
    boolean areBuildersLoaded(Vector neededbuilders, String applicationRoot) {
	boolean succes = true;

        for (Enumeration h = neededbuilders.elements();h.hasMoreElements();) {
            Hashtable bh= (Hashtable) h.nextElement();
            String name = (String) bh.get("name");
            MMObjectBuilder bul = getMMObject(name);
            // if builder not loaded
            if (bul==null) {
                // if 'inactive' in the config/builder path, we dont know what to do (i dont like inactive builders)
                String path = mmb.getBuilderPath(name, "");
                if(path != null) {
                    log.error("builder '" + name + "' was already on our system, but inactive. To install this application, make the builder '" + path + java.io.File.separator + name +  ".xml" + "' active");
                    succes = false;
		    continue;
                }
                // well we try to open the %application%/ from inside our application dir...
                File appFile = new File(applicationRoot);
                if(!appFile.exists()) {
                    log.error("could not find application dir :  '" + appFile + "'(builder '" + name + "' )");
                    return false;
                }
                // well we try to open the %application%/builders/ from inside our application dir...
                appFile = new File(appFile.getAbsolutePath() + java.io.File.separator + "builders");
                if(!appFile.exists()) {
                    log.error("could not find builder's dir inside the application :  '" + appFile + "'(builder '" + name + "' )");
                    succes = false;
		    continue;
                }
                // well we will try to open the %application%/builders/%buildername%.xml from inside our application dir...
                appFile = new File(appFile.getAbsolutePath() + java.io.File.separator + name + ".xml");
                if(!appFile.exists()) {
                    log.error("could not find the builderfile :  '" + appFile + "'(builder '" + name + "')");
		    succes = false;
		    continue;
                }
                // we now have the location,.....
                MMObjectBuilder objectTypes = getMMObject("typedef");
                if(objectTypes == null) {
                    log.error("could not find builder typedef");
                    succes = false;
		    continue;
                }
                // try to add a node to typedef, same as adding a builder...
                MMObjectNode type = objectTypes.getNewNode("system");
                // fill the name....
                type.setValue("name", name);

                // fill the config...
                org.w3c.dom.Document config = null;
                try {
                    config =  org.mmbase.util.XMLBasicReader.getDocumentBuilder(org.mmbase.util.XMLBuilderReader.class).parse(appFile);
                }
                catch(org.xml.sax.SAXException se) {
                    String msg = "builder '" + name + "':\n" + se.toString() + "\n" + Logging.stackTrace(se);
                    log.error(msg);
                    succes = false;
		    continue;
                }
                catch(java.io.IOException ioe) {
                    String msg = "builder '" + name + "':\n" + ioe.toString() + "\n" + Logging.stackTrace(ioe);
                    log.error(msg);
                    succes = false;
		    continue;
                }
                type.setValue("config", config);
                // insert into mmbase
                objectTypes.insert("system", type);
                // we now made the builder active.. look for other builders...
            }
        }
        return succes;
    }


    /**
     * Checks whether a given relation definition exists, and if not, creates that definition.
     * @param sname source name of the relation definition
     * @param dname destination name of the relation definition
     * @param dir directionality (uni or bi)
     * @param sguiname source GUI name of the relation definition
     * @param dguiname destination GUI name of the relation definition
     * @param builder references the builder to use (only in new format)
     */
    private void checkRelDef(String sname, String dname, int dir,String sguiname, String dguiname, int builder) {
        RelDef reldef=mmb.getRelDef();
        if (reldef!=null) {
            Enumeration res = reldef.search("WHERE sname='"+sname+"' AND dname='"+dname+"'");
            if (!res.hasMoreElements()) {
                MMObjectNode node=reldef.getNewNode("system");
                node.setValue("sname",sname);
                node.setValue("dname",dname);
                node.setValue("dir",dir);
                node.setValue("sguiname",sguiname);
                node.setValue("dguiname",dguiname);
                if (reldef.usesbuilder) {
                    // if builder is unknown (falsely specified), use the InsRel builder
                    if (builder<=0) {
                        builder=mmb.getInsRel().oType;
                    }
                    node.setValue("builder",builder);
                }
                int id=reldef.insert("system",node);
                if (id!=-1) {
                    log.debug("RefDef ("+sname+","+dname+") installed");
                }
            }
        } else {
            log.warn("can't get reldef builder");
        }
    }

    /**
     * @javadoc
     */
    private void checkTypeRel(String sname, String dname, String rname, int count) {
        TypeRel typerel=mmb.getTypeRel();
        if (typerel!=null) {
            TypeDef typedef=mmb.getTypeDef();
            if (typedef==null) {
                log.warn("can't get typedef builder");
                return;
            }
            RelDef reldef=mmb.getRelDef();
            if (reldef==null) {
                log.warn("can't get reldef builder");
                return;
            }

            // figure out rnumber
            int rnumber=reldef.getNumberByName(rname);
            if (rnumber==-1) {
                log.warn("no reldef : "+rname+" defined");
                return;
            }

            // figure out snumber
            int snumber=typedef.getIntValue(sname);
            if (snumber==-1) {
                log.warn("no object : "+sname+" defined");
                return;
            }

            // figure out dnumber
            int dnumber=typedef.getIntValue(dname);
            if (dnumber==-1) {
                log.warn("no object : "+dname+" defined");
                return;
            }

            if (!typerel.reldefCorrect(snumber,dnumber,rnumber) ) {
                MMObjectNode node=typerel.getNewNode("system");
                node.setValue("snumber",snumber);
                node.setValue("dnumber",dnumber);
                node.setValue("rnumber",rnumber);
                node.setValue("max",count);
                int id=typerel.insert("system",node);
                // should throw error if id!=-1?
                if (id!=-1) {
                    log.debug("TypeRel ("+sname+","+dname+","+rname+") installed");
                }
            }
        } else {
            log.warn("can't get typerel builder");
        }
    }

    /**
     * @javadoc
     * @deprecated-now not used (?)
     */
    private void checkRelation(int snumber, int dnumber, String rname, int dir) {
        InsRel insrel=mmb.getInsRel();
        if (insrel!=null) {
            RelDef reldef=mmb.getRelDef();
            if (reldef==null) {
                log.warn("can't get reldef builder");
            }
            // figure out rnumber
            int rnumber=reldef.getNumberByName(rname);
            if (rnumber==-1) {
                log.warn("no reldef : "+rname+" defined");
                return;
            }

            MMObjectNode node=insrel.getRelation(snumber,dnumber,rnumber);
            if (node==null) {
                node=insrel.getNewNode("system");
                node.setValue("snumber",snumber);
                node.setValue("dnumber",dnumber);
                node.setValue("rnumber",rnumber);
                if (insrel.usesdir) {
                    if (dir<=0) {
                        // have to get dir value form reldef
                        MMObjectNode relnode = reldef.getNode(rnumber);
                        dir = relnode.getIntValue("dir");
                    }
                    // correct if value is invalid
                    if (dir<=0) dir=2;
                    node.setValue("dir",dir);
                }
                int id=insrel.insert("system",node);
            }
        } else {
            log.warn("can't get insrel builder");
        }
    }

    /**
     * @javadoc
     */
    public void probeCall() {
        if (restartwanted) {
            System.exit(0);
        }
        Versions ver=(Versions)mmb.getMMObject("versions");
        if (ver==null) {
            log.warn("Versions builder not installed, Can't auto deploy apps");
            return;
        }
        String path=MMBaseContext.getConfigPath()+File.separator+"applications"+File.separator;
        // new code checks all the *.xml files in builder dir
        File bdir = new File(path);
        if (bdir.isDirectory()) {
            String files[] = bdir.list();
            if (files == null) return;
            for (int i=0;i<files.length;i++) {
                String aname=files[i];
                if (aname.endsWith(".xml")) {
                    XMLApplicationReader app=new XMLApplicationReader(path+aname);
                    if (app!=null && app.getApplicationAutoDeploy()) {
                        String name=app.getApplicationName();
                        String maintainer=app.getApplicationMaintainer();
                        int version=app.getApplicationVersion();
                        int installedversion=ver.getInstalledVersion(name,"application");
                        if (installedversion==-1 || version>installedversion) {
                            if (installedversion==-1) {
                                log.info("Auto deploy application : "+aname+" started");
                            } else {
                                log.info("Auto deploy application : "+aname+" new version from "+installedversion+" to "+version);
                            }
                            if (installApplication(aname.substring(0,aname.length()-4))) {
                                if (installedversion==-1) {
                                    ver.setInstalledVersion(name,"application",maintainer,version);
                                } else {
                                    ver.updateInstalledVersion(name,"application",maintainer,version);
                                }
                                log.info("Auto deploy application : "+aname+" done");
                            } else {
                                log.error("Problem installing application : "+name);
                            }
                        }

                    }
                }
            }
        }
    }

    /**
     * @javadoc
     */
    private boolean    writeApplication(String appname,String targetpath,String goal) {
        if (kioskmode) {
            log.warn("refused to write application, am in kiosk mode");
            return false;
        }
        String path=MMBaseContext.getConfigPath()+File.separator+"applications"+File.separator;
        XMLApplicationReader app=new XMLApplicationReader(path+appname+".xml");
        Vector savestats=XMLApplicationWriter.writeXMLFile(app,targetpath,goal,mmb);
        lastmsg="Application saved oke<BR><BR>\n";
        lastmsg+="Some statistics on the save : <BR><BR>\n";
        for (Enumeration h = savestats.elements();h.hasMoreElements();) {
            String result=(String)h.nextElement();
            lastmsg+=result+"<BR><BR>\n";
        }
        return true;
    }

    /**
     * @javadoc
     */
    Vector getApplicationsList() {
        Versions ver=(Versions)mmb.getMMObject("versions");
        if (ver==null) {
            log.warn("Versions builder not installed, Can't get to apps");
            return null;
        }
        Vector results=new Vector();

        String path=MMBaseContext.getConfigPath()+File.separator+"applications"+File.separator;
        // new code checks all the *.xml files in builder dir
        File bdir = new File(path);
        if (bdir.isDirectory()) {
            String files[] = bdir.list();
            if (files == null) return results;
            for (int i=0;i<files.length;i++) {
                String aname=files[i];
                if (aname.endsWith(".xml")) {
                    XMLApplicationReader app=new XMLApplicationReader(path+aname);
                    String name=app.getApplicationName();
                    results.addElement(name);
                    results.addElement(""+app.getApplicationVersion());
                    int installedversion=ver.getInstalledVersion(name,"application");
                    if (installedversion==-1) {
                        results.addElement("no");
                    } else {
                        results.addElement("yes (ver : "+installedversion+")");
                    }
                    results.addElement(app.getApplicationMaintainer());
                    boolean autodeploy=app.getApplicationAutoDeploy();
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
        String subpath="";
        if ((tok!=null) && (tok.hasMoreTokens())) {
            subpath=tok.nextToken();
        }
        Versions ver=(Versions)mmb.getMMObject("versions");
        if (ver==null) {
            log.warn("Versions builder not installed, Can't get to builders");
            return null;
        }
        String path=MMBaseContext.getConfigPath()+File.separator+"builders"+File.separator;
        return getBuildersList(path, subpath, ver);
    }

    /**
     * @javadoc
     */
    Vector getBuildersList(String configpath, String subpath, Versions ver) {
        Vector results=new Vector();
        File bdir = new File(configpath+subpath);
        if (bdir.isDirectory()) {
            if (!"".equals(subpath)) {
                subpath=subpath+File.separator;
            }
            String files[] = bdir.list();
            if (files == null) return results;
            for (int i=0;i<files.length;i++) {
                String aname=files[i];
                if (aname.endsWith(".xml")) {
                    String name=aname;
                    String sname=name.substring(0,name.length()-4);
                    XMLBuilderReader app=new XMLBuilderReader(configpath+subpath+aname,mmb);
                    results.addElement(subpath+sname);
                    results.addElement(""+app.getBuilderVersion());
                    int installedversion=ver.getInstalledVersion(sname,"builder");
                    if (installedversion==-1) {
                        results.addElement("no");
                    } else {
                        results.addElement("yes");
                    }
                    results.addElement(app.getBuilderMaintainer());
                } else {
                    results.addAll(getBuildersList(configpath,subpath+aname,ver));
                }
            }
        }
        return results;
    }

    /**
     * @javadoc
     */
    Vector getModuleProperties(String modulename) {
        Vector results=new Vector();
        String path=MMBaseContext.getConfigPath()+File.separator+"modules"+File.separator;
        XMLModuleReader mod=new XMLModuleReader(path+modulename+".xml");
        if (mod!=null) {
            Hashtable props=mod.getProperties();
            for (Enumeration h = props.keys();h.hasMoreElements();) {
                String key=(String)h.nextElement();
                String value=(String)props.get(key);
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
        Vector results=new Vector();
        String path=MMBaseContext.getConfigPath()+File.separator+"builders"+File.separator;
        XMLBuilderReader bul = new XMLBuilderReader(path+buildername+".xml", mmb);
        if (bul!=null) {
            Vector defs=bul.getFieldDefs();
            for (Enumeration h = defs.elements();h.hasMoreElements();) {
                FieldDefs def=(FieldDefs)h.nextElement();
                results.addElement(""+def.getDBPos());
                results.addElement(""+def.getDBName());
                results.addElement(def.getDBTypeDescription());
                int size=def.getDBSize();
                if (size==-1) {
                    results.addElement("fixed");
                } else {
                    results.addElement(""+size);
                }
            }

        }
        return results;
    }

    /**
     * @javadoc
     */
    Vector getModulesList() {
        Vector results=new Vector();

        String path=MMBaseContext.getConfigPath()+File.separator+"modules"+File.separator;
        // new code checks all the *.xml files in builder dir
        File bdir = new File(path);
        if (bdir.isDirectory()) {
            String files[] = bdir.list();
            if (files == null) return results;
            for (int i=0;i<files.length;i++) {
                String aname=files[i];
                if (aname.endsWith(".xml")) {
                    String name=aname;
                    String sname=name.substring(0,name.length()-4);
                    XMLModuleReader app=new XMLModuleReader(path+aname);
                    results.addElement(sname);

                    results.addElement(""+app.getModuleVersion());
                    String status=app.getStatus();
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
        Versions ver=(Versions)mmb.getMMObject("versions");
        if (ver==null) {
            log.warn("Versions builder not installed, Can't get to builders");
            return null;
        }
        Vector results=new Vector();

        String path=MMBaseContext.getConfigPath()+File.separator+"databases"+File.separator;
        // new code checks all the *.xml files in builder dir
        File bdir = new File(path);
        if (bdir.isDirectory()) {
            String files[] = bdir.list();
            if (files == null) return results;
            for (int i=0;i<files.length;i++) {
                String aname=files[i];
                if (aname.endsWith(".xml")) {
                    String name=aname;
                    String sname=name.substring(0,name.length()-4);
                    XMLBuilderReader app=new XMLBuilderReader(path+aname,mmb);
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
        File f=new File(path);
        if (f.exists() && f.isFile()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @javadoc
     */
    private String getBuilderField(String buildername,String fieldname, String key) {
        MMObjectBuilder bul=getMMObject(buildername);
        if (bul!=null) {
            FieldDefs def=bul.getField(fieldname);
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
                int size=def.getDBSize();
                if (size!=-1) {
                    return ""+size;
                } else {
                    return "fixed";
                }
            } else if (key.equals("dbstate")) {
                return def.getDBStateDescription();
            } else if (key.equals("dbmmbasetype")) {
                return def.getDBTypeDescription();
            } else if (key.equals("editorinput")) {
                int pos=def.getGUIPos();
                if (pos==-1) {
                    return "not shown";
                } else {
                    return ""+pos;
                }
            } else if (key.equals("editorsearch")) {
                int pos=def.getGUISearch();
                if (pos==-1) {
                    return "not shown";
                } else {
                    return ""+pos;
                }
            } else if (key.equals("editorlist")) {
                int pos=def.getGUIList();
                if (pos==-1) {
                    return "not shown";
                } else {
                    return ""+pos;
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
        Vector results=new Vector();
        MMObjectBuilder bul=getMMObject(buildername);
        if (bul!=null) {
            FieldDefs def=bul.getField(fieldname);
            Map guinames=def.getGUINames();
            for (Iterator h = guinames.entrySet().iterator();h.hasNext();) {
                Map.Entry me=(Map.Entry)h.next();
                results.addElement(me.getKey());
                results.addElement(me.getValue());
            }
        }
        return results;
    }

    /**
     * @javadoc
     */
    private String getGuiNameValue(String buildername, String fieldname,String key) {
        MMObjectBuilder bul=getMMObject(buildername);
        if (bul!=null) {
            FieldDefs def=bul.getField(fieldname);
            String value=def.getGUIName(key);
            if (value!=null) {
                return value;
            }
        }
        return "";
    }

    /**
     * @javadoc
     */
    public void doModulePosts(String command,Hashtable cmds,Hashtable vars) {
        if (command.equals("SETPROPERTY")) {
            setModuleProperty(vars);
        }
    }

    /**
     * @javadoc
     */
    public void doBuilderPosts(String command,Hashtable cmds,Hashtable vars) {
        if (command.equals("SETGUINAME")) {
            setBuilderGuiName(vars);
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
        String builder=(String)vars.get("BUILDER");
        String fieldname=(String)vars.get("FIELDNAME");
        String country=(String)vars.get("COUNTRY");
        String value=(String)vars.get("VALUE");

        MMObjectBuilder bul=getMMObject(builder);
        FieldDefs def=bul.getField(fieldname);
        if (def!=null) {
            def.setGUIName(country,value);
        }
        syncBuilderXML(bul,builder);
    }

    /**
     * @javadoc
     */
    public void setBuilderGuiType(Hashtable vars) {
        if (kioskmode) {
            log.warn("refused gui type set, am in kiosk mode");
            return;
        }
        String builder=(String)vars.get("BUILDER");
        String fieldname=(String)vars.get("FIELDNAME");
        String value=(String)vars.get("VALUE");

        MMObjectBuilder bul=getMMObject(builder);
        FieldDefs def=bul.getField(fieldname);
        if (def!=null) {
            def.setGUIType(value);
        }
        syncBuilderXML(bul,builder);
    }

    /**
     * @javadoc
     */
    public void setBuilderEditorInput(Hashtable vars) {
        if (kioskmode) {
            log.warn("refused editor input set, am in kiosk mode");
            return;
        }
        String builder=(String)vars.get("BUILDER");
        String fieldname=(String)vars.get("FIELDNAME");
        String value=(String)vars.get("VALUE");

        MMObjectBuilder bul=getMMObject(builder);
        FieldDefs def=bul.getField(fieldname);
        if (def!=null) {
            try {
                int i=Integer.parseInt(value);
                def.setGUIPos(i);
            } catch (Exception e) {}
        }
        syncBuilderXML(bul,builder);
    }

    /**
     * @javadoc
     */
    public void setBuilderEditorList(Hashtable vars) {
        if (kioskmode) {
            log.warn("refused editor list set, am in kiosk mode");
            return;
        }
        String builder=(String)vars.get("BUILDER");
        String fieldname=(String)vars.get("FIELDNAME");
        String value=(String)vars.get("VALUE");

        MMObjectBuilder bul=getMMObject(builder);
        FieldDefs def=bul.getField(fieldname);
        if (def!=null) {
            try {
                int i=Integer.parseInt(value);
                def.setGUIList(i);
            } catch (Exception e) {}
        }
        syncBuilderXML(bul,builder);
    }

    /**
     * @javadoc
     */
    public void setBuilderEditorSearch(Hashtable vars) {
        if (kioskmode) {
            log.warn("refused editor pos set, am in kiosk mode");
            return;
        }
        String builder=(String)vars.get("BUILDER");
        String fieldname=(String)vars.get("FIELDNAME");
        String value=(String)vars.get("VALUE");

        MMObjectBuilder bul=getMMObject(builder);
        FieldDefs def=bul.getField(fieldname);
        if (def!=null) {
            try {
                int i=Integer.parseInt(value);
                def.setGUISearch(i);
            } catch (Exception e) {}
        }
        syncBuilderXML(bul,builder);
    }

    /**
     * @javadoc
     */
    public void setBuilderDBSize(Hashtable vars) {
        if (kioskmode) {
            log.warn("Refused set DBSize field, am in kiosk mode");
            return;
        }
        String builder=(String)vars.get("BUILDER");
        String fieldname=(String)vars.get("FIELDNAME");
        String value=(String)vars.get("VALUE");

        MMObjectBuilder bul=getMMObject(builder);
        FieldDefs def=bul.getField(fieldname);
        if (def!=null) {
            try {
                int i=Integer.parseInt(value);
                def.setDBSize(i);
            } catch (Exception e) {}
        }
        if (mmb.getDatabase().changeField(bul,fieldname)) {
            syncBuilderXML(bul,builder);
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
        String builder=(String)vars.get("BUILDER");
        String fieldname=(String)vars.get("FIELDNAME");
        String value=(String)vars.get("VALUE");

        MMObjectBuilder bul=getMMObject(builder);
        FieldDefs def=bul.getField(fieldname);
        if (def!=null) {
            def.setDBType(value);
        }
        if (mmb.getDatabase().changeField(bul,fieldname)) {
            syncBuilderXML(bul,builder);
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
        String builder=(String)vars.get("BUILDER");
        String fieldname=(String)vars.get("FIELDNAME");
        String value=(String)vars.get("VALUE");

        MMObjectBuilder bul=getMMObject(builder);
        FieldDefs def=bul.getField(fieldname);
        if (def!=null) {
            def.setDBState(value);
        }
        if (mmb.getDatabase().changeField(bul,fieldname)) {
            syncBuilderXML(bul,builder);
        }
    }

    /**
     * @javadoc
     * @deprecation contains code taht is commented-out, can be removed (?)
     */
    public void setBuilderDBKey(Hashtable vars) {
        if (kioskmode) {
            log.warn("Refused set dbkey field, am in kiosk mode");
            return;
        }
        String builder=(String)vars.get("BUILDER");
        String fieldname=(String)vars.get("FIELDNAME");
        String value=(String)vars.get("VALUE");

        MMObjectBuilder bul=getMMObject(builder);
        FieldDefs def=bul.getField(fieldname);
        if (def!=null) {
            if (value.equals("true")) {
                def.setDBKey(true);
            } else {
                def.setDBKey(false);
            }
        }
        /* not needed at the moment since keys
           are not done in the database layer
        if (mmb.getDatabase().changeField(bul,fieldname)) {
            syncBuilderXML(bul,builder);
        }
        */
        syncBuilderXML(bul,builder);
    }

    /**
     * @javadoc
     */
    public void setBuilderDBNotNull(Hashtable vars) {
        if (kioskmode) {
            log.warn("Refused set NotNull field, am in kiosk mode");
            return;
        }
        String builder=(String)vars.get("BUILDER");
        String fieldname=(String)vars.get("FIELDNAME");
        String value=(String)vars.get("VALUE");

        MMObjectBuilder bul=getMMObject(builder);
        FieldDefs def=bul.getField(fieldname);
        if (def!=null) {
            if (value.equals("true")) {
                def.setDBNotNull(true);
            } else {
                def.setDBNotNull(false);
            }
        }
        if (mmb.getDatabase().changeField(bul,fieldname)) {
            syncBuilderXML(bul,builder);
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
        String builder=(String)vars.get("BUILDER");
        MMObjectBuilder bul=getMMObject(builder);
        if (bul!=null) {
            // Determine position of new field.
            // This should be the number of the last field as denied in the builder xml,
            // as the DBPos field is incremented for each field in that file.
            int pos=bul.getFields(FieldDefs.ORDER_CREATE).size()+1;

            FieldDefs def=new FieldDefs();
            def.setDBPos(pos);

            def.setGUIPos(pos);
            def.setGUIList(-1);
            def.setGUISearch(pos);

            String value=(String)vars.get("dbname");
            def.setDBName(value);
            def.setGUIName("en",value);

            log.service("Adding field " + value);

            value=(String)vars.get("mmbasetype");
            def.setDBType(value);

            value=(String)vars.get("dbstate");
            def.setDBState(value);

            value=(String)vars.get("dbnotnull");
            def.setDBNotNull(value.equals("true"));

            value=(String)vars.get("dbkey");
            def.setDBKey(value.equals("true"));

            value=(String)vars.get("dbsize");
            try {
                int i=Integer.parseInt(value);
                def.setDBSize(i);
            } catch (Exception e) {
                log.debug("dbsize had invalid value, not setting size");
            }

            value=(String)vars.get("guitype");
            def.setGUIType(value);

            bul.addField(def);
            if (mmb.getDatabase().addField(bul, def.getDBName())) {
                syncBuilderXML(bul,builder);
            } else {
                log.warn("Could not sync builder XML because addField returned false (tablesizeprotection?)");
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
        String builder=(String)vars.get("BUILDER");
        String fieldname=(String)vars.get("FIELDNAME");
        String value=(String)vars.get("SURE");

        MMObjectBuilder bul=getMMObject(builder);
        if (bul!=null && value!=null && value.equals("Yes")) {
            FieldDefs def=bul.getField(fieldname);
            int dbpos=def.getDBPos();
            bul.removeField(fieldname);
            if (mmb.getDatabase().removeField(bul,def.getDBName())) {
                syncBuilderXML(bul,builder);
            } else {
                bul.addField(def);
            }
        }
    }

    /**
     * @javadoc
     */
    public void syncBuilderXML(MMObjectBuilder bul,String builder) {
        String savepath=MMBaseContext.getConfigPath()+File.separator + "builders" + File.separator + builder + ".xml";
        log.service("Syncing builder xml (" + savepath + ") for builder " + builder);
        try {
            BuilderWriter builderOut=new BuilderWriter(bul);
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
    public void syncModuleXML(Module mod,String modname) {
        String savepath=MMBaseContext.getConfigPath()+File.separator+"modules"+File.separator+modname+".xml";
        try {
            ModuleWriter moduleOut=new ModuleWriter(mod);
            moduleOut.setIncludeComments(false);
            moduleOut.writeToFile(savepath);
        } catch (Exception e) {
            log.error(Logging.stackTrace(e));
        }
    }

    /**
     * @javadoc
     */
    public Vector  getMultilevelCacheEntries() {
        Vector results=new Vector();
        Enumeration res=MultilevelCacheHandler.getCache().getOrderedElements();
        while (res.hasMoreElements()) {
            MultilevelCacheEntry en=(MultilevelCacheEntry)res.nextElement();
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
            results.addElement(""+MultilevelCacheHandler.getCache().getCount(en.getKey()));
        }
        return results;
    }

    /**
     * @javadoc
     */
    public Vector getNodeCacheEntries() {
        Vector results=new Vector();
        Enumeration res=MMObjectBuilder.nodeCache.getOrderedElements();
        while (res.hasMoreElements()) {
            MMObjectNode node=(MMObjectNode)res.nextElement();
            results.addElement(""+MMObjectBuilder.nodeCache.getCount(node.getIntegerValue("number")));
            results.addElement(""+node.getIntValue("number"));
            results.addElement(node.getStringValue("owner"));
            results.addElement(mmb.getTypeDef().getValue(node.getIntValue("otype")));
        }
        return results;
    }


}
