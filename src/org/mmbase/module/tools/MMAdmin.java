/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.tools;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.util.Date;
import java.io.*;
import java.sql.*;

import org.mmbase.util.*;
import org.mmbase.module.*;
import org.mmbase.module.core.*;
import org.mmbase.module.builders.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.database.*;
import org.mmbase.module.database.support.*;
import org.mmbase.module.tools.MMAppTool.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * aaarararaagagaggaggghhhhhh no javadocs!
 *
 * @author Daniel Ockeloen
 * @author Pierre van Rooden
 * @version 11 May 2001
 */
public class MMAdmin extends ProcessorModule {

    // logging routines
    private static Logger log = Logging.getLoggerInstance(MMAdmin.class.getName());

    // reference to MMBase

    MMBase mmb=null;
    MMAdminProbe probe=null;
    String lastmsg="";
    private boolean restartwanted=false;
    private boolean kioskmode=false;

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
     */
     public Vector getList(scanpage sp,StringTagger tagger, String value) throws ParseException {
        String line = Strip.DoubleQuote(value,Strip.BOTH);
        StringTokenizer tok = new StringTokenizer(line,"-\n\r");
        if (tok.hasMoreTokens()) {
            String cmd=tok.nextToken();
            if (cmd.equals("APPLICATIONS")) return getApplicationsList();
            if (cmd.equals("BUILDERS")) return getBuildersList(tok);
            if (cmd.equals("FIELDS")) return getFields(tok.nextToken());
            if (cmd.equals("MODULEPROPERTIES")) return getModuleProperties(tok.nextToken());
            if (cmd.equals("ISOGUINAMES")) return getISOGuiNames(tok.nextToken(),tok.nextToken());
            if (cmd.equals("MODULES")) return getModulesList();
            if (cmd.equals("DATABASES")) return getDatabasesList();
        }
        return null;
    }

    /**
     * Execute the commands provided in the form values
     */
    public boolean process(scanpage sp, Hashtable cmds,Hashtable vars) {
        String cmdline,token;

        for (Enumeration h = cmds.keys();h.hasMoreElements();) {
            cmdline=(String)h.nextElement();
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
                        XMLModuleWriter.writeXMLFile(savepath,mod);
                        lastmsg="Writing finished, no problems.<BR><BR>\n";
                        lastmsg+="A clean copy of "+modulename+".xml can be found at : "+savepath+"<BR><BR>\n";
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
                        XMLBuilderWriter.writeXMLFile(savepath,bul);
                        lastmsg="Writing finished, no problems.<BR><BR>\n";
                        lastmsg+="A clean copy of "+buildername+".xml can be found at : "+savepath+"<BR><BR>\n";
                    }
                }
            }

        }
        return false;
    }

    /**
    *    Handle a $MOD command
    */
    public String replace(scanpage sp, String cmds) {
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
            }
        }
        return "No command defined";
    }

    int getVersion(String appname) {
        String path=MMBaseContext.getConfigPath()+File.separator+"applications"+File.separator;
        XMLApplicationReader app=new XMLApplicationReader(path+appname+".xml");
        if (app!=null) {
            return app.getApplicationVersion();
        }
        return -1;
    }


    int getBuilderVersion(String appname) {
        String path=MMBaseContext.getConfigPath()+File.separator+"builders"+File.separator;
        XMLBuilderReader app=new XMLBuilderReader(path+appname+".xml");
        if (app!=null) {
            return app.getBuilderVersion();
        }
        return -1;
    }

    String getBuilderClass(String bulname) {
        String path=MMBaseContext.getConfigPath()+File.separator+"builders"+File.separator;
        XMLBuilderReader bul=new XMLBuilderReader(path+bulname+".xml");
        if (bul!=null) {
            return bul.getClassFile();
        }
        return "";
    }


    String getModuleClass(String modname) {
        String path=MMBaseContext.getConfigPath()+File.separator+"modules"+File.separator;
        XMLModuleReader mod=new XMLModuleReader(path+modname+".xml");
        if (mod!=null) {
            return mod.getClassFile();
        }
        return "";
    }

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



    String getDescription(String appname) {
        String path=MMBaseContext.getConfigPath()+File.separator+"applications"+File.separator;
        XMLApplicationReader app=new XMLApplicationReader(path+appname+".xml");
        if (app!=null) {
            return app.getDescription();
        }
        return "";
    }


    String getBuilderDescription(String appname) {
        String path=MMBaseContext.getConfigPath()+File.separator+"builders"+File.separator;
        XMLBuilderReader app=new XMLBuilderReader(path+appname+".xml");
        if (app!=null) {
            Hashtable desc=app.getDescriptions();
            String us=(String)desc.get("us");
            if (us!=null) {
                return us;
            }
        }
        return "";
    }

    String getModuleDescription(String modulename) {
        /*
        String path=MMBaseContext.getConfigPath()+File.separator+"modules"+File.separator;
        XMLModuleReader app=new XMLModuleReader(path+modulename+".xml");
        if (app!=null) {
            Hashtable desc=app.getDescriptions();
            String us=(String)desc.get("us");
            if (us!=null) {
                return us;
            }
        }
        */
        return "";
    }

    public void maintainance() {
    }

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

    private boolean installApplication(String applicationname) {
        String path=MMBaseContext.getConfigPath()+File.separator+"applications"+File.separator;
        XMLApplicationReader app=new XMLApplicationReader(path+applicationname+".xml");
        if (app!=null) {
            if (areBuildersLoaded(app.getNeededBuilders())) {
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
                for (Enumeration n = (nodereader.getNodes(mmb)).elements();n.hasMoreElements();) {
                    MMObjectNode newnode=(MMObjectNode)n.nextElement();
                    int exportnumber=newnode.getIntValue("number");
                    String query="exportnumber=="+exportnumber+"+exportsource=='"+exportsource+"'";
                    Enumeration b=syncbul.search(query);
                    if (b.hasMoreElements()) {
                        // XXX To do : we may want to load the node and check/change the fields
                        MMObjectNode syncnode=(MMObjectNode)b.nextElement();
                        //log.warn("node allready installed : "+exportnumber);
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

    private int doKeyMergeNode(MMObjectNode newnode) {
        MMObjectBuilder bul=newnode.parent;
        if (bul!=null) {
            String checkQ="";
            Vector vec=bul.getFields();
            for (Enumeration h = vec.elements();h.hasMoreElements();) {
                FieldDefs def=(FieldDefs)h.nextElement();
                if (def.isKey) {
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
                        //log.warn("node allready installed : "+exportnumber);
                    } else {
                        newnode.setValue("number",-1);

                        // The following code determines the 'actual' (synced) numbers for the destination and source nodes
                        // This will normally work well, however:
                        // It is _theoretically_ possible that one or both nodes are _themselves_ relation nodes.
                        // (since relations are nodes).
                        // Due to the order in which syncing takles place, it is possible that such strcutures will fail
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

    boolean areBuildersLoaded(Vector neededbuilders) {
        for (Enumeration h = neededbuilders.elements();h.hasMoreElements();) {
            Hashtable bh=(Hashtable)h.nextElement();
            String name=(String)bh.get("name");
            MMObjectBuilder bul=getMMObject(name);
            if (bul==null) {
                log.error("Application installer error : builder '"+name+"' not loaded");
                return false;
            }
        }
        return true;
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
            Vector res=reldef.searchVector("sname=='"+sname+"'+dname=='"+dname+"'");
            if (res!=null && res.size()>0) {
                //log.warn("RefDef ("+sname+","+dname+") allready installed");
            } else {
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
                    // log.warn("RefDef ("+sname+","+dname+") installed");
                }
            }
        } else {
            log.warn("can't get reldef builder");
        }
    }


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
            int rnumber=reldef.getGuessedNumber(rname);
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
                if (id!=-1) {
                    //log.warn("TypeRel ("+sname+","+dname+","+rname+") installed");
                }
            }
        } else {
            log.warn("can't get typerel builder");
        }
    }

    // not used ?
    // should be dropped ?
    private void checkRelation(int snumber, int dnumber, String rname, int dir) {
        InsRel insrel=mmb.getInsRel();
        if (insrel!=null) {
            RelDef reldef=mmb.getRelDef();
            if (reldef==null) {
                log.warn("can't get reldef builder");
            }
            // figure out rnumber
            int rnumber=reldef.getGuessedNumber(rname);
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
                // if (id!=-1) {
                //  log.info("Relation installed");
                // }
            }
        } else {
            log.warn("can't get insrel builder");
        }
    }


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

    Vector getBuildersList() {
        return getBuildersList(null);
    }

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

    Vector getBuildersList(String configpath, String subpath, Versions ver) {
        Vector results=new Vector();
        File bdir = new File(configpath+subpath);
        if (bdir.isDirectory()) {
            if (!"".equals(subpath)) {
                subpath=subpath+File.separator;
            }
            String files[] = bdir.list();

            for (int i=0;i<files.length;i++) {
                String aname=files[i];
                if (aname.endsWith(".xml")) {
                    String name=aname;
                    String sname=name.substring(0,name.length()-4);
                    XMLBuilderReader app=new XMLBuilderReader(configpath+subpath+aname);
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

    Vector getFields(String buildername) {
        Vector results=new Vector();
        String path=MMBaseContext.getConfigPath()+File.separator+"builders"+File.separator;
        XMLBuilderReader bul=new XMLBuilderReader(path+buildername+".xml");
        if (bul!=null) {
            Vector defs=bul.getFieldDefs();
            for (Enumeration h = defs.elements();h.hasMoreElements();) {
                FieldDefs def=(FieldDefs)h.nextElement();
                //log.debug("DEFS="+def);
                results.addElement(""+def.DBPos);
                results.addElement(""+def.DBName);
                int type=def.DBType;
                switch (type) {
                    case FieldDefs.TYPE_STRING:
                        results.addElement("STRING");
                    break;
                    case FieldDefs.TYPE_INTEGER:
                        results.addElement("INTEGER");
                        break;
                    case FieldDefs.TYPE_LONG:
                        results.addElement("LONG");
                        break;
                    case FieldDefs.TYPE_FLOAT:
                        results.addElement("FLOAT");
                        break;
                    case FieldDefs.TYPE_DOUBLE:
                        results.addElement("DOUBLE");
                        break;
                    case FieldDefs.TYPE_BYTE:
                        results.addElement("BYTE");
                        break;
                }
                int size=def.DBSize;
                if (size==-1) {
                    results.addElement("fixed");
                } else {
                    results.addElement(""+def.DBSize);
                }
            }

        }
        return results;
    }


    Vector getModulesList() {
        /*
        Versions ver=(Versions)mmb.getMMObject("versions");
        if (ver==null) {
            log.warn("Versions builder not installed, Can't get to builders");
            return null;
        }
        */
        Vector results=new Vector();

        String path=MMBaseContext.getConfigPath()+File.separator+"modules"+File.separator;
        // new code checks all the *.xml files in builder dir
            File bdir = new File(path);
        if (bdir.isDirectory()) {
            String files[] = bdir.list();
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
            for (int i=0;i<files.length;i++) {
                String aname=files[i];
                if (aname.endsWith(".xml")) {
                    String name=aname;
                    String sname=name.substring(0,name.length()-4);
                    XMLBuilderReader app=new XMLBuilderReader(path+aname);
                    results.addElement(sname);

                    results.addElement("0");
                    results.addElement("yes");
                    results.addElement("mmbase.org");
                    /*
                    results.addElement(""+app.getBuilderVersion());
                    int installedversion=ver.getInstalledVersion(sname,"builder");
                    if (installedversion==-1) {
                        results.addElement("no");
                    } else {
                        results.addElement("yes");
                    }
                    results.addElement(app.getBuilderMaintainer());
                    */
                }
            }
        }
        return results;
    }


    private boolean fileExists(String path) {
        File f=new File(path);
        if (f.exists() && f.isFile()) {
            return true;
        } else {
            return false;
        }
    }

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
                int state=def.getDBState();
                switch (state) {
                    case FieldDefs.DBSTATE_VIRTUAL: return "virtual";
                    case FieldDefs.DBSTATE_PERSISTENT: return "persistent";
                    case FieldDefs.DBSTATE_SYSTEM: return "system";
                    case FieldDefs.DBSTATE_UNKNOWN: return "unknown";
                }
            } else if (key.equals("dbmmbasetype")) {
                int type=def.getDBType();
                switch (type) {
                    case FieldDefs.TYPE_STRING: return "STRING";
                    case FieldDefs.TYPE_INTEGER: return "INTEGER";
                    case FieldDefs.TYPE_BYTE: return "BYTE";
                    case FieldDefs.TYPE_FLOAT: return "FLOAT";
                    case FieldDefs.TYPE_DOUBLE: return "DOUBLE";
                    case FieldDefs.TYPE_LONG: return "LONG";
                    case FieldDefs.TYPE_UNKNOWN: return "UNKNOWN";
                }
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

    private Vector getISOGuiNames(String buildername, String fieldname) {
        Vector results=new Vector();
        MMObjectBuilder bul=getMMObject(buildername);
        if (bul!=null) {
            FieldDefs def=bul.getField(fieldname);
            Hashtable guinames=def.getGUINames();
            for (Enumeration h = guinames.keys();h.hasMoreElements();) {
                String key=(String)h.nextElement();
                String value=(String)guinames.get(key);
                results.addElement(key);
                results.addElement(value);
            }
        }
        return results;
    }


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


    public void doModulePosts(String command,Hashtable cmds,Hashtable vars) {
        if (command.equals("SETPROPERTY")) {
            setModuleProperty(vars);
        }
    }

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
            if (value.equals("STRING")) {
                def.setDBType(FieldDefs.TYPE_STRING);
            } else if (value.equals("INTEGER")) {
                def.setDBType(FieldDefs.TYPE_INTEGER);
            } else if (value.equals("BYTE")) {
                def.setDBType(FieldDefs.TYPE_BYTE);
            } else if (value.equals("FLOAT")) {
                def.setDBType(FieldDefs.TYPE_FLOAT);
            } else if (value.equals("DOUBLE")) {
                def.setDBType(FieldDefs.TYPE_DOUBLE);
            } else if (value.equals("LONG")) {
                def.setDBType(FieldDefs.TYPE_LONG);
            }
        }
        if (mmb.getDatabase().changeField(bul,fieldname)) {
            syncBuilderXML(bul,builder);
        }
    }


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
            if (value.equals("virtual")) {
                def.setDBState(FieldDefs.DBSTATE_VIRTUAL);
            } else if (value.equals("persistent")) {
                def.setDBState(FieldDefs.DBSTATE_PERSISTENT);
            } else if (value.equals("system")) {
                def.setDBState(FieldDefs.DBSTATE_SYSTEM);
            }
        }
        if (mmb.getDatabase().changeField(bul,fieldname)) {
            syncBuilderXML(bul,builder);
        }
    }

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

    public void addBuilderField(Hashtable vars) {
        if (kioskmode) {
            log.warn("Refused add builder field, am in kiosk mode");
            return;
        }
        String builder=(String)vars.get("BUILDER");
        MMObjectBuilder bul=getMMObject(builder);
        if (bul!=null) {
            int pos=bul.getFields().size();

            FieldDefs def=new FieldDefs();
            def.setDBPos(pos);

            def.setGUIPos(pos);
            def.setGUIList(-1);
            def.setGUISearch(pos);

            String value=(String)vars.get("dbname");
            def.setDBName(value);
            def.setGUIName("us",value);

            value=(String)vars.get("mmbasetype");
            if (value.equals("STRING")) {
                def.setDBType(FieldDefs.TYPE_STRING);
            } else if (value.equals("INTEGER")) {
                def.setDBType(FieldDefs.TYPE_INTEGER);
            } else if (value.equals("BYTE")) {
                def.setDBType(FieldDefs.TYPE_BYTE);
            } else if (value.equals("FLOAT")) {
                def.setDBType(FieldDefs.TYPE_FLOAT);
            } else if (value.equals("DOUBLE")) {
                def.setDBType(FieldDefs.TYPE_DOUBLE);
            } else if (value.equals("LONG")) {
                def.setDBType(FieldDefs.TYPE_LONG);
            }


            value=(String)vars.get("dbstate");
            if (value.equals("virtual")) {
                def.setDBState(FieldDefs.DBSTATE_VIRTUAL);
            } else if (value.equals("persistent")) {
                def.setDBState(FieldDefs.DBSTATE_PERSISTENT);
            } else if (value.equals("system")) {
                def.setDBState(FieldDefs.DBSTATE_SYSTEM);
            }

            value=(String)vars.get("dbnotnull");
            if (value.equals("true")) {
                def.setDBNotNull(true);
            } else {
                def.setDBNotNull(false);
            }

            value=(String)vars.get("dbkey");
            if (value.equals("true")) {
                def.setDBKey(true);
            } else {
                def.setDBKey(false);
            }

            value=(String)vars.get("dbsize");
            try {
                int i=Integer.parseInt(value);
                def.setDBSize(i);
            } catch (Exception e) {}

            value=(String)vars.get("guitype");
            def.setGUIType(value);

            bul.addField(def);
            if (mmb.getDatabase().addField(bul,def.getDBName())) {
                syncBuilderXML(bul,builder);
            }
        }
    }

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

    public void syncBuilderXML(MMObjectBuilder bul,String builder) {
        String savepath=MMBaseContext.getConfigPath()+File.separator+"builders"+File.separator+builder+".xml";
        XMLBuilderWriter.writeXMLFile(savepath,bul);
    }

    public void syncModuleXML(Module mod,String modname) {
        String savepath=MMBaseContext.getConfigPath()+File.separator+"modules"+File.separator+modname+".xml";
        XMLModuleWriter.writeXMLFile(savepath,mod);
    }
}
