/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.application;

import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.builders.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;

import java.util.*;
import java.io.*;
import java.lang.*;

import org.w3c.dom.*;

/**
 * @author Rob Vermeulen (VPRO)
 */
public class DataSetElement extends ComponentElement implements ElementInterface {
    
    private DataSetComponent datasetComponent = null;
    private MMBase mmbase = null;
    
    DataSetElement(Hashtable info, DataSetComponent dc, Application app) {
        super(info,app,"dataset");
        datasetComponent = dc;
        mmbase = dc.mmbase;
        
        File file = new File(getElementPath()+"description.xml");
        if (file.exists()) {
            reader = new XMLBasicReader(getElementPath()+"description.xml");
            description = reader.getElementValue("dataset.description");
            version = Integer.parseInt(reader.getElementAttributeValue("dataset","version"));
            creationdate = reader.getElementAttributeValue("dataset","create-date");
        } else {
            setCreationDate();
            //setVersion("1");
            setDescription("");
            dependencies = new Vector();
            relationsources = new Vector();
            objectsources = new Vector();
            backupmethods = new Vector();
        }
    }
    
    
    public void autoDeploy() throws Exception {
        if (getAutoDeploy().equals("true"))
            if (getInstalledVersion()==-1 || getConfigurationVersion()>getInstalledVersion()) {
                log.debug("Autodeploying "+getName());
                install();
            }
    }
    
    /**
     * Installs all data (objects and relations) of the application.
     */
    public void install() throws Exception {
        if (ApplicationManager.getKioskmode()) {
            throw new InstallationException("MMBase runs in Kiosk mode, it's not possible to install applications");
        }
        
        // If a version already exists, than the versionnumber has to be bigger of the one to install.
        if (getInstalledVersion()!=-1 && getConfigurationVersion()<=getInstalledVersion()) {
            log.info("Skipping installing DataSet Element '"+getName()+" version"+getConfigurationVersion()+"', because it (or a newer version) is already installed.");
            return;
        }
        
        checkDependencies();
        
        if(getInstalledVersion()==-1) {
            ApplicationManager.getVersionBuilder().setInstalledVersion(application.getName()+"/"+getName(),"application/dataset","test",getConfigurationVersion());
        } else {
            ApplicationManager.getVersionBuilder().updateInstalledVersion(application.getName()+"/"+getName(),"application/dataset","test",getConfigurationVersion());
        }
        
        installDataSources();
        installRelationSources();
    }
    
    public void uninstall() throws Exception {
        // asjemenou
    }
    
    private void installDataSources() throws InstallationException {
        MMObjectBuilder syncbul=mmbase.getMMObject("syncnodes");
        if (syncbul==null) {
            throw new InstallationException("syncnodes builder is not loaded.");
        }
        
        for (Enumeration importfiles = getObjectSources().elements();importfiles.hasMoreElements();) {
            Hashtable importfile=(Hashtable)importfiles.nextElement();
            
            String file=importfile.get("nodemanager")+".xml";
            String path=getElementPath()+"data"+File.separator;
            
            if (!fileExists(path+file)) {
                throw new InstallationException("Datafile '"+path+file+"' is not present.");
            }
            
            XMLNodeReader nodereader=new XMLNodeReader(path+file,path,mmbase);
            
            String exportsource=nodereader.getExportSource();
            int timestamp=nodereader.getTimeStamp();
            
            Vector importednodes= new Vector();
            for (Enumeration n = nodereader.getNodes(mmbase).elements();n.hasMoreElements();) {
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
        }
    }
    
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
    
    private boolean fileExists(String path) {
        File f=new File(path);
        if (f.exists() && f.isFile()) {
            return true;
        } else {
            return false;
        }
    }
    
    private void installRelationSources() throws Exception{
        for (Enumeration h = getRelationSources().elements();h.hasMoreElements();) {
            Hashtable bh=(Hashtable)h.nextElement();
            String file=(String)bh.get("nodemanager")+".xml";
            String path=getElementPath()+"data"+File.separator+file;
            if (!fileExists(path)) {
                throw new Exception("cannot find "+path);
            }
            
            XMLRelationNodeReader nodereader=new XMLRelationNodeReader(path,mmbase);
            
            String exportsource=nodereader.getExportSource();
            int timestamp=nodereader.getTimeStamp();
            
            MMObjectBuilder syncbul=mmbase.getMMObject("syncnodes");
            if (syncbul==null) {
                throw new Exception("syncbuilders are not loaded");
            }
            for (Enumeration n = (nodereader.getNodes(mmbase)).elements();n.hasMoreElements();) {
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
        }
    }
    
    public void save() throws Exception {
        try {
            if (ApplicationManager.getKioskmode()) {
                throw new InstallationException("MMBase runs in Kiosk mode, it's not possible to install applications");
            }
            
            
            File file = new File(getElementPath());
            if(!file.exists()) {
                file.mkdirs();
            }
           
	    // Only take first backup method
	   Enumeration e = getBackupMethods().elements();
	   Hashtable method = (Hashtable) e.nextElement();
	   String path = (String)method.get("path");
	   Vector resultmsgs = new Vector();

	   // Read the description file how to save the data
	    XMLContextDepthReader capp=new XMLContextDepthReader(getElementPath()+path);
	    // Save all data
	    XMLContextDepthWriterII.writeContext(this,capp,mmbase,resultmsgs);
	    // Save the description file how to save data
	    XMLContextDepthWriterII.writeContextXML(capp,getElementPath()+path);


            
            // write config file
            DataSetElementWriter dscw = new DataSetElementWriter(getElementPath()+"description.xml",this);
            
            
        } catch (Exception e) {
            log.error("hier mag ik niets afvangen"+e+"\n"+Logging.stackTrace(e));
        }
    }
    
    /**
     * save the dataset element. This will create a jarfile in the applications directory
     */
    public void save(String targetpath,String goal) throws Exception {
        if(ApplicationManager.getKioskmode()) {
            throw new Exception("Cannot save dataset element because MMBase is in kioskmode");
        }
    }
}
