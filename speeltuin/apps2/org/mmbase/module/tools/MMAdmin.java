/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */
package org.mmbase.module.tools;

import java.net.*;
import java.lang.*;
import java.util.*;
import java.util.Date;
import java.io.*;
import java.sql.*;

import org.mmbase.util.*;
import org.mmbase.application.*;
import org.mmbase.module.*;
import org.mmbase.module.core.*;
import org.mmbase.module.builders.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.gui.html.*;
import org.mmbase.module.database.*;
import org.mmbase.module.database.support.*;
import org.mmbase.module.tools.MMAppTool.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 *
 * @author Daniel Ockeloen
 * @author Pierre van Rooden
 * @author Rob Vermeulen
 */
public class MMAdmin extends ProcessorModule {
    
    // logging routines
    private static Logger log = Logging.getLoggerInstance(MMAdmin.class.getName());
    
    private 	sessionsInterface 	sessions;
    // reference to MMBase
    
    MMBase mmb=null;
    
    // The manager that handles all applications
    private ApplicationManager applicationManager = null;
    MMAdminProbe probe=null;
    HtmlBase htmlbase=null;
    
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
        htmlbase=(HtmlBase)getModule("MMBASE");
        probe = new MMAdminProbe(this);
        sessions = (sessionsInterface) getModule("SESSION");
        if (sessions == null) {
            log.debug("Could not find session module, proceeding without sessions");
        }
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
            if(!checkUserLoggedOn(sp,cmd,false)) return new Vector();
            if (cmd.equals("APPLICATIONS")) {
                tagger.setValue("ITEMS","5");
                return getApplicationsList();
            }
            if (cmd.equals("COMPONENTS")) {
               		tagger.setValue("ITEMS","1");
		    return getComponents(tok.nextToken());
	    }
            if (cmd.equals("ELEMENTS")) {
               		tagger.setValue("ITEMS","1");
		    return getElements(tok.nextToken(),tok.nextToken());
	    }
            if (cmd.equals("APPLICATIONBUILDERS")) {
               		tagger.setValue("ITEMS","1");
		    return getAppBuilders(tok.nextToken(),tok.nextToken(),tok.nextToken());
	    }
            if (cmd.equals("ELEMENT")) {
               		tagger.setValue("ITEMS","6");
		    return getElement(tok.nextToken(),tok.nextToken(),tok.nextToken());
	    }
            if (cmd.equals("RELATIONDEFINITIONS")) {
               		tagger.setValue("ITEMS","8");
		    return getRelationDefinitions(tok.nextToken(),tok.nextToken(),tok.nextToken());
	    }
            if (cmd.equals("RELATIONTYPES")) {
               		tagger.setValue("ITEMS","5");
		    return getRelationTypes(tok.nextToken(),tok.nextToken(),tok.nextToken());
	    }
            if (cmd.equals("ADDEDFILES")) {
               		tagger.setValue("ITEMS","1");
		    return getAddedFiles(tok.nextToken(),tok.nextToken(),tok.nextToken());
	    }
            if (cmd.equals("DELETEDFILES")) {
               		tagger.setValue("ITEMS","1");
		    return getDeletedFiles(tok.nextToken(),tok.nextToken(),tok.nextToken());
	    }
            if (cmd.equals("CHANGEDFILES")) {
               		tagger.setValue("ITEMS","1");
		    return getChangedFiles(tok.nextToken(),tok.nextToken(),tok.nextToken());
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
    
    private boolean checkAdmin(scanpage sp, String cmd) {
        return checkUserLoggedOn(sp, cmd, true);
    }
    
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
                
                //ROB CUT a lot of code
                String appname=(String)cmds.get(cmdline);
                log.info("LOADING "+appname);
                Application application = getApplicationManager().getApplication(appname);
                application.install();
                
                
            } else if (token.equals("SAVEDATASET")) {
                
                String appname=(String)cmds.get(cmdline);
                String savepath=(String)vars.get("PATH");
                String goal=(String)vars.get("GOAL");
                log.info("APP="+appname+" P="+savepath+" G="+goal);
                Application application = getApplicationManager().getApplication(appname);
		try {
                	application.save();
		} catch (Exception ee) {
			log.error("exception in mmadmin in SAVEDATASET" +ee);
		}
                
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
        if(!checkUserLoggedOn(sp,cmds,false)) return "";
        StringTokenizer tok = new StringTokenizer(cmds,"-\n\r");
        if (tok.hasMoreTokens()) {
            String cmd=tok.nextToken();
            if (cmd.equals("VERSION")) {
                return ""+getVersion(tok.nextToken());
            } else if (cmd.equals("CREATETASK")) {
		return(""+createTask(tok.nextToken()));
            } else if (cmd.equals("TASKMSG")) {
		return(handleTaskMsg(tok));
            } else if (cmd.equals("DESCRIPTION")) {
                return getDescription(tok.nextToken());
            } else if (cmd.equals("CREATEAPPLICATION")) {
                
                String step=(String)tok.nextToken();
        	sessionInfo session=getSession(sp);

		if(step.equals("1")) {
			//log.info("Creating application (mmadmin)");
			String appname = sessions.getValue(session,"name");
			Application application = getApplicationManager().getApplication(appname);
			if(application==null) {
				application = getApplicationManager().createApplication(appname);
			}
                	application.setDescription(sessions.getValue(session,"description"));
			if(sessions.getValue(session,"autodeploy").equals("yes")) {
                		application.setAutoDeploy(true);
			} else {
                		application.setAutoDeploy(false);
			}
                	application.setMaintainerName(sessions.getValue(session,"namemaintainer"));
                	application.setMaintainerCompany(sessions.getValue(session,"companymaintainer"));
                	application.setMaintainerInfo(sessions.getValue(session,"infomaintainer"));
                	application.setMaintainerEmail(sessions.getValue(session,"emailmaintainer"));
			application.writeConfiguration();
		}
		if(step.equals("2")) {
			//log.info("Creating display element (mmadmin)");
			Application application = getApplicationManager().getApplication(""+tok.nextToken());
			DisplayComponent component = (DisplayComponent)application.getComponent("Displays");
			DisplayElement display = component.createElement(sessions.getValue(session,"displayname"));

                	display.setDescription(sessions.getValue(session,"displaydescription"));
                	display.setDefaultWebPath(sessions.getValue(session,"displaydefaultwebpath"));
			if(sessions.getValue(session,"displayautodeploy").equals("yes")) {
                		display.setAutoDeploy("true");
			} else {
                		display.setAutoDeploy("false");
			}
			display.save();
		}
		if(step.equals("3")) {
			//log.info("Creating cloudlayout element (mmadmin)");
			Application application = getApplicationManager().getApplication(""+tok.nextToken());
			CloudLayoutComponent component = (CloudLayoutComponent)application.getComponent("CloudLayouts");
			ElementInterface element = component.createElement(sessions.getValue(session,"cloudlayoutname"));

                	element.setDescription(sessions.getValue(session,"cloudlayoutdescription"));
			if(sessions.getValue(session,"cloudlayoutautodeploy").equals("yes")) {
                		element.setAutoDeploy("true");
			} else {
                		element.setAutoDeploy("false");
			}
			try {
			element.save();
			} catch (Exception excp) {
				log.error("ROB AU "+excp);
			}
		}
		if(step.equals("3a")) { 
			Application application = getApplicationManager().getApplication(""+tok.nextToken());
			Component component = application.getComponent("CloudLayouts");
			CloudLayoutElement cl = (CloudLayoutElement)component.getElement(""+tok.nextToken());
			cl.addNeededBuilder(""+tok.nextToken());
			try {
			cl.save();
			} catch (Exception e) {
				log.error("ROB -------");
			}
		}
		if(step.equals("3b")) { 
			Application application = getApplicationManager().getApplication(""+tok.nextToken());
			Component component = application.getComponent("CloudLayouts");
			CloudLayoutElement cl = (CloudLayoutElement)component.getElement(""+tok.nextToken());

			cl.addNeededRelDef(sessions.getValue(session,"sname"), sessions.getValue(session,"dname"), sessions.getValue(session,"dir"), sessions.getValue(session,"sguiname"), sessions.getValue(session,"dguiname"), sessions.getValue(session,"builder"));
			try {
			cl.save();
			} catch (Exception e) {
				log.error("ROB save reldefs");
			}
		}
		if(step.equals("3c")) { 
			log.info("ROB add relations");
			Application application = getApplicationManager().getApplication(""+tok.nextToken());
			Component component = application.getComponent("CloudLayouts");
			CloudLayoutElement cl = (CloudLayoutElement)component.getElement(""+tok.nextToken());

			cl.addNeededRelation(sessions.getValue(session,"from"), sessions.getValue(session,"to"), sessions.getValue(session,"type"), sessions.getValue(session,"count"));
			try {
			cl.save();
			} catch (Exception e) {
				log.error("ROB save reldefs");
			}
		}
		if(step.equals("4")) {
			//log.info("Creating dataset element (mmadmin)");
			Application application = getApplicationManager().getApplication(""+tok.nextToken());
			DataSetComponent component = (DataSetComponent) application.getComponent("DataSets");
			DataSetElement dataset = component.createElement(sessions.getValue(session,"datasetname"));

                	dataset.setDescription(sessions.getValue(session,"datasetdescription"));
			if(sessions.getValue(session,"datasetautodeploy").equals("yes")) {
                		dataset.setAutoDeploy("true");
			} else {
                		dataset.setAutoDeploy("false");
			}
			try {
			dataset.save();
			} catch (Exception e) {
				log.error("ROB save reldefs");
			}

		}

		return "";
            } else if (cmd.equals("DELETEDISPLAY")) {
			try {
				Application application = getApplicationManager().getApplication(""+tok.nextToken());
				String componentname = tok.nextToken();
				ComponentInterface component = (ComponentInterface)application.getComponent(componentname);
				component.deleteElement(""+tok.nextToken());
			} catch (Exception brr) {
				log.error("exception in deletedisplay "+brr);
			}
			return "";
            } else if (cmd.equals("SAVEAPPLICATION")) {
			try {
				Application application = getApplicationManager().getApplication(""+tok.nextToken());
				application.save();
			} catch (Exception brr) {
				log.error("exception in saving application (mmadmin) "+brr);
			}
			return "";
            } else if (cmd.equals("DELETEAPPLICATION")) {
			getApplicationManager().removeApplication(""+tok.nextToken());
			return "";
            } else if (cmd.equals("INSTALLDATASET")) {
                String a= tok.nextToken();
                String c= tok.nextToken();
                String e= tok.nextToken();
        	Application application = getApplicationManager().getApplication(a);
		Component component = application.getComponent(c);
		DataSetElement element = (DataSetElement)component.getElement(e);
		try{
                element.install();
		} catch (Exception excep ) {
			log.error("Exception in saving app "+excep);
		}
		return "";
            } else if (cmd.equals("DEINSTALL")) {
                String a= tok.nextToken();
                String c= tok.nextToken();
                String e= tok.nextToken();
        	Application application = getApplicationManager().getApplication(a);
		Component component = application.getComponent(c);
		DisplayElement element = (DisplayElement)component.getElement(e);
                element.uninstall();
		return "";
            } else if (cmd.equals("SAVE")) {
                String a= tok.nextToken();
                String c= tok.nextToken();
                String e= tok.nextToken();
        	Application application = getApplicationManager().getApplication(a);
		Component component = application.getComponent(c);
		try{ 
		if(c.equals("Displays")) {
			DisplayElement element = (DisplayElement)component.getElement(e);
                	element.save();
		}
		if(c.equals("CloudLayouts")) {
			CloudLayoutElement element = (CloudLayoutElement)component.getElement(e);
                	element.save();
		}
		if(c.equals("DataSets")) {
			DataSetElement element = (DataSetElement)component.getElement(e);
                	element.save();
		}
		} catch (Exception exc) {
		}
		return "";
            } else if (cmd.equals("DEFAULTWEBPATH")) {
                String a= tok.nextToken();
                String c= tok.nextToken();
                String e= tok.nextToken();
        	Application application = getApplicationManager().getApplication(a);
		Component component = application.getComponent(c);
		DisplayElement element = (DisplayElement)component.getElement(e);
                return element.getDefaultWebPath();
            } else if (cmd.equals("CHECKUPDATE")) {
                String a= tok.nextToken();
                String c= tok.nextToken();
                String e= tok.nextToken();
        	Application application = getApplicationManager().getApplication(a);
		Component component = application.getComponent(c);
		DisplayElement element = (DisplayElement)component.getElement(e);
                return ""+element.checkUpdate();
            } else if (cmd.equals("ISBUILDERLOADED")) {
                String a= tok.nextToken();
                String c= tok.nextToken();
                String e= tok.nextToken();
                String b= tok.nextToken();
        	Application application = getApplicationManager().getApplication(a);
		Component component = application.getComponent(c);
		CloudLayoutElement element = (CloudLayoutElement)component.getElement(e);
                return ""+element.isBuilderLoaded(b);
            } else if (cmd.equals("NEWBUILDERVERSION")) {
                String a= tok.nextToken();
                String c= tok.nextToken();
                String b= tok.nextToken();
        	Application application = getApplicationManager().getApplication(a);
		CloudLayoutComponent component = (CloudLayoutComponent)application.getComponent(c);
                return ""+component.getBuilderVersion(b);
            } else if (cmd.equals("LASTMSG")) {
                return lastmsg;
            } else if (cmd.equals("STATUS")) {
        	Application application = getApplicationManager().getApplication(tok.nextToken());

        	return getHTML(application.getStatus(tok.nextToken()));
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
                if (htmlbase==null)
                    return "N.A.";
                else
                    return(""+htmlbase.getMultilevelCacheHandler().getHits());
            } else if (cmd.equals("MULTILEVELCACHEMISSES")) {
                if (htmlbase==null)
                    return "N.A.";
                else
                    return(""+htmlbase.getMultilevelCacheHandler().getMisses());
            } else if (cmd.equals("MULTILEVELCACHEREQUESTS")) {
                if (htmlbase==null)
                    return "N.A.";
                else
                    return(""+(htmlbase.getMultilevelCacheHandler().getHits()+htmlbase.getMultilevelCacheHandler().getMisses()));
            } else if (cmd.equals("MULTILEVELCACHEPERFORMANCE")) {
                if (htmlbase==null)
                    return "N.A.";
                else
                    return(""+(htmlbase.getMultilevelCacheHandler().getRatio()*100));
            } else if (cmd.equals("MULTILEVELCACHESIZE")) {
                if (htmlbase==null)
                    return "N.A.";
                else
                    return(""+(htmlbase.getMultilevelCacheHandler().getSize()));
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
   
    // ROB CHANGED METHOD
    int getVersion(String appname) {
        Application application = getApplicationManager().getApplication(appname);
        return application.getInstalledVersion();
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
    
    
    // ROB changed method
    String getDescription(String appname) {
        Application application = getApplicationManager().getApplication(appname);
        return application.getDescription();
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
    
    // ROB removed method installApplication
    
    // ROB removed method installDataSources
    
    // ROB removed method doKeyMergeNode
    
    // ROB removed method installRElationSources
    
    // ROB removed method checkRelDefs
    
    // ROB removed method checkAllowedRelations
    
    // ROB removed method areBuildersLoaded
    
    // ROB removed method checkRelDef
    
    // ROB removed method checkTypeRel
    
    // ROB removed method checkRelation
    
    
    public void probeCall() {
        if (restartwanted) {
            System.exit(0);
        }
        
        // ROB removed lot of code
    }
    
    //ROB remoced method writeapplication
    
    private ApplicationManager getApplicationManager() {
        if(applicationManager==null) {
            applicationManager = MMBase.getApplicationManager();
        }
        return applicationManager;
    }
    
    // ROB changed method
    Vector getDeletedFiles(String app,String comp, String element) {
        
        Application application = getApplicationManager().getApplication(app);
	Component c = application.getComponent(comp);
	DisplayElement ce = (DisplayElement)c.getElement(element);
        return ce.getDeletedFiles();
    }
    
    // ROB changed method
    Vector getAddedFiles(String app,String comp, String element) {
        
        Application application = getApplicationManager().getApplication(app);
	Component c = application.getComponent(comp);
	DisplayElement ce = (DisplayElement)c.getElement(element);
        return ce.getAddedFiles();
    }
    
    // ROB changed method
    Vector getChangedFiles(String app,String comp, String element) {
        
        Application application = getApplicationManager().getApplication(app);
	Component c = application.getComponent(comp);
	DisplayElement ce = (DisplayElement)c.getElement(element);
        return ce.getChangedFiles();
    }

    // ROB changed method
    Vector getAppBuilders(String app,String comp, String element) {
        
        Application application = getApplicationManager().getApplication(app);
	Component c = application.getComponent(comp);
	CloudLayoutElement oe = (CloudLayoutElement)c.getElement(element);
        return oe.getBuilders();
    }
    
    // ROB changed method
    Vector getElement(String app,String comp, String element) {
        
        Application application = getApplicationManager().getApplication(app);
	Component c = application.getComponent(comp);
	ComponentElement ce = (ComponentElement)c.getElement(element);
        Vector results=new Vector();
        
        results.addElement(ce.getName());
	if(ce.getInstalledVersion()<=0) {
        	results.addElement("not installed");
	} else {
        	results.addElement(""+ce.getInstalledVersion());
	}
        results.addElement(ce.getCreationDate());
        results.addElement(ce.getAutoDeploy());
        results.addElement(ce.getMD5());
        results.addElement(ce.getDescription());
        return results;
    }
    
    // ROB changed method
    Vector getRelationTypes(String app,String comp, String element) {
        Vector results=new Vector();

        Application application = getApplicationManager().getApplication(app);
	CloudLayoutComponent c = (CloudLayoutComponent)application.getComponent(comp);
	CloudLayoutElement ce = (CloudLayoutElement)c.getElement(element);
       
	Vector nrd = ce.getNeededRelations();
	for (Enumeration e = nrd.elements();e.hasMoreElements();) {
		Hashtable rd = (Hashtable)e.nextElement();

        	results.addElement(rd.get("from"));
        	results.addElement(rd.get("to"));
        	results.addElement(rd.get("type"));
		try {
		results.addElement(""+ce.isTypeDefLoaded(rd));

		} catch (Exception ee) {}
        	results.addElement(rd.get("id"));
	}
        return results;
    }
    
    // ROB changed method
    Vector getRelationDefinitions(String app,String comp, String element) {
        Vector results=new Vector();

        Application application = getApplicationManager().getApplication(app);
	CloudLayoutComponent c = (CloudLayoutComponent)application.getComponent(comp);
	CloudLayoutElement ce = (CloudLayoutElement)c.getElement(element);
       
	Vector nrd = ce.getNeededRelDefs();
	for (Enumeration e = nrd.elements();e.hasMoreElements();) {
		Hashtable rd = (Hashtable)e.nextElement();

        	results.addElement(rd.get("source"));
        	results.addElement(rd.get("target"));
        	results.addElement(rd.get("direction"));
        	results.addElement(rd.get("guisourcename"));
        	results.addElement(rd.get("guitargetname"));
        	results.addElement(rd.get("builder"));
		results.addElement(""+ce.isRelDefLoaded(rd));
        	results.addElement(rd.get("id"));
		
	}
        return results;
    }
    
    // ROB changed method
    Vector getElements(String app,String comp) {
        
        Application application = getApplicationManager().getApplication(app);
	Component c = application.getComponent(comp);
        Vector results=new Vector();
        
        for (Enumeration e = c.getElements().keys();e.hasMoreElements();) {
            results.addElement(""+e.nextElement());
        }
        return results;
    }
    
    // ROB changed method
    Vector getComponents(String app) {
        Application application = getApplicationManager().getApplication(app);
        Vector results=new Vector();
        
        for (Enumeration e = application.getComponents().keys();e.hasMoreElements();) {
            results.addElement(""+e.nextElement());
        }
        return results;
    }
    
    // ROB changed method
    Vector getApplicationsList() {
        
        Hashtable applications = getApplicationManager().getApplications();
        Vector results=new Vector();
        
        for (Enumeration e = applications.elements();e.hasMoreElements();) {
            Application application = (Application)e.nextElement();
            results.addElement(application.getName());
            results.addElement(""+application.getConfigurationVersion());
            int installedversion=application.getInstalledVersion();
            if (installedversion==-1) {
                results.addElement("no");
            } else {
                results.addElement("yes (version: "+installedversion+")");
            }
            results.addElement(application.getDescription());
            boolean autodeploy=application.getAutoDeploy();
            if (autodeploy) {
                results.addElement("yes");
            } else {
                results.addElement("no");
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
            if (files == null) return results;
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
                results.addElement(""+def.getDBPos());
                results.addElement(""+def.getDBName());
                int type=def.getDBType();
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
                    XMLBuilderReader app=new XMLBuilderReader(path+aname);
                    results.addElement(sname);
                    
                    results.addElement("0");
                    results.addElement("yes");
                    results.addElement("mmbase.org");
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
    
    public Vector  getMultilevelCacheEntries() {
        Vector results=new Vector();
        if (htmlbase!=null) {
            Enumeration res=htmlbase.getMultilevelCacheHandler().getOrderedElements();
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
                results.addElement(""+htmlbase.getMultilevelCacheHandler().getCount(en.getKey()));
            }
        }
        return(results);
    }
    
    public Vector  getNodeCacheEntries() {
        Vector results=new Vector();
        Enumeration res=MMObjectBuilder.nodeCache.getOrderedElements();
        while (res.hasMoreElements()) {
            MMObjectNode node=(MMObjectNode)res.nextElement();
            results.addElement(""+MMObjectBuilder.nodeCache.getCount(node.getIntegerValue("number")));
            results.addElement(""+node.getIntValue("number"));
            results.addElement(node.getStringValue("owner"));
            results.addElement(mmb.getTypeDef().getValue(node.getIntValue("otype")));
        }
        return(results);
    }
   



    private String handleTaskMsg(StringTokenizer tok) {
	// get the applicationTaskManager
	ApplicationTaskManager atm=getApplicationManager().getApplicationTaskManager();

	// convert tokenized string to xml line
	if (tok.hasMoreTokens()) {
		String taskidstring=tok.nextToken();
		try {
			int taskid=Integer.parseInt(taskidstring);
			ApplicationTask atask=atm.getTask(taskid);
			if (tok.hasMoreTokens()) {
				String cmd=tok.nextToken();
				if (cmd.equals("SETFIELD")) {
					if (tok.hasMoreTokens()) {
						String name=tok.nextToken();
						if (tok.hasMoreTokens()) {
							String value=tok.nextToken();
							atask.setField(name,value);
						}	
					}
				} else if (cmd.equals("GETFIELD")) {
					if (tok.hasMoreTokens()) {
						String name=tok.nextToken();
						String value=atask.getField(name);
						if (value!=null) return(value);
					}
				} else if (cmd.equals("ADDLISTVALUE")) {
					if (tok.hasMoreTokens()) {
						String name=tok.nextToken();
						if (tok.hasMoreTokens()) {
							String value=tok.nextToken();
							atask.addListValue(name,value);
						}	
					}
				} else if (cmd.equals("SETSTATE")) {
					if (tok.hasMoreTokens()) {
						String value=tok.nextToken();
						atask.setState(value,"scan driven");
					}
				} else if (cmd.equals("GETSTATE")) {
					String value=atask.getState();
					if (value!=null) return(value);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	return("");
    }

    private int createTask(String type) {
	// get the applicationTaskManager
	ApplicationTaskManager atm=getApplicationManager().getApplicationTaskManager();
	if (atm!=null) {
		int taskid=atm.createTask(type);
		return(taskid);
	} else {
		log.error("Can't access ApplicationTaskManager");
		return(-1);
	}
    }
 
    /**
     * Returns a HTML-version of a string.
     * This replaces a number of tokens with HTML sequences.
     * The default output does not match well with the new xhtml standards (ugly html), nor does it replace all tokens.
     *
     * Default replacements can be overridden by setting the builder properties in your <builder>.xml:
     *
     * html.alinea
     * html.endofline
     *
     * Example:
     * <properties>
     *   <property name="html.alinea"> &lt;br /&gt; &lt;br /&gt;</property>
     *   <property name="html.endofline"> &lt;br /&gt; </property>
     * </properties>
     *
     * @param body text to convert
     * @return the convert text
     */
    protected String getHTML(String body) {
        String rtn="";
        if (body!=null) {
            StringObject obj=new StringObject(body);
            obj.replace("<","&lt;");
            obj.replace(">","&gt;");
            obj.replace("$","&#36;");

            String alinea=getInitParameter("html.alinea");
            String endofline=getInitParameter("html.endofline");

            if (alinea!=null) {
                obj.replace("\r\n\r\n",alinea);
                obj.replace("\n\n",alinea);
            } else {
                obj.replace("\r\n\r\n","<p> </p>");
                obj.replace("\n\n","<p> </p>");
            }

            if (endofline!=null) {
                obj.replace("\r\n",endofline);
                obj.replace("\n",endofline);
            } else {
                obj.replace("\r\n","<br />");
                obj.replace("\n","<br />");
            }

            rtn=obj.toString();
        }
        return rtn;
    }
    private sessionInfo getSession(scanpage sp) {
        if (sessions==null)
            return null;
        else
            return sessions.getSession(sp,sp.sname);
    }

}
 


