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
 * The CloudLayout Element handles: web pages, editors, tools, documents.
 *
 * @author Rob Vermeulen (VPRO)
 */
public class CloudLayoutElement extends ComponentElement implements ElementInterface {
    
    private CloudLayoutComponent cloudlayoutcomponent = null;
    private MD5Diff md5diff = null;
    private MMBase mmbase = null;
    
    CloudLayoutElement(Hashtable info, CloudLayoutComponent oc, Application app) {
        super(info,app,"cloudlayout");
        cloudlayoutcomponent = oc;
        mmbase = oc.mmbase;
        
        File file = new File(getElementPath()+"description.xml");
        if (file.exists()) {
            reader = new XMLBasicReader(getElementPath()+"description.xml");
            description = reader.getElementValue("cloudlayout.description");
            version = Integer.parseInt(reader.getElementAttributeValue("cloudlayout","version"));
            creationdate = reader.getElementAttributeValue("cloudlayout","create-date");
        } else {
            setCreationDate();
            //setVersion("1");
            setDescription("");
            neededbuilders = new Vector();
            neededreldefs = new Vector();
            neededrelations = new Vector();
            dependencies = new Vector();
        }
        
        
        
    }
    
    /**
     * set the version for this component if all conditiones for this component are satsified
     */
    void checkVersion() {
        // ### and all reldef and typerels exist
        if(areBuildersLoaded()) {
            setVersion();
        }
    }
    
    /**
     * are all builders loaded?
     * @return true if all builders are loaded
     */
    public boolean areBuildersLoaded() {
        boolean allbuildersloaded = true;
        for(Enumeration e = getBuilders().elements();e.hasMoreElements();) {
            String buildername = (String)e.nextElement();
            if(!isBuilderLoaded(buildername)) {
                allbuildersloaded = false;
            }
        }
        return allbuildersloaded;
    }
    
    public void autoDeploy() throws Exception {
        if (getAutoDeploy().equals("true"))
            if (getInstalledVersion()==-1 || getConfigurationVersion()>getInstalledVersion()) {
                log.debug("Autodeploying "+getName());
                install();
            }
    }
    
    /**
     * install and upgrade all buildes, reldefs, and typerels
     */
    public void install() throws Exception {
        if (ApplicationManager.getKioskmode()) {
            throw new InstallationException("MMBase runs in Kiosk mode, it's not possible to install applications");
        }
        
        // If a version already exists, than the versionnumber has to be bigger of the one to install.
        if (getInstalledVersion()!=-1 && getConfigurationVersion()<=getInstalledVersion()) {
            log.info("Skipping installing CloudLayout Element '"+getName()+" version"+getConfigurationVersion()+"', because it (or a newer version) is already installed.");
            return;
        }
        
        checkDependencies();
        
        Vector builders = getBuilders();
        
        for (Enumeration e = builders.elements();e.hasMoreElements();) {
            String buildername = (String)e.nextElement();
            if(!isBuilderLoaded(buildername)) {
                installBuilder(buildername);
            }
        }
        installRelDefs();
        installTypeRels();
        
        checkVersion();
    }
    
    /**
     * save the CloudLayout Element. The cloudlayout element description file and the needed builders will be saved.
     */
    public void save() throws Exception {
        if (ApplicationManager.getKioskmode()) {
            throw new InstallationException("MMBase runs in Kiosk mode, it's not possible to install applications");
        }
        
        File file = new File(getElementPath());
        if(!file.exists()) {
            file.mkdirs();
        }
        
        // Create directory to story the builders.
        file = new File(getElementPath()+"builders");
        if (!file.exists()) {
            file.mkdir();
        }
        
        // Copy all needed builders from the MMBase builder directory to the application builder directory
        Vector builders = getBuilders();
        for (Enumeration e = builders.elements(); e.hasMoreElements();) {
            String buildername = (String)e.nextElement()+".xml";
            String MMBaseBuilder = MMBaseContext.getConfigPath()+File.separator+"builders"+File.separator+buildername;
            String ApplicationBuilder = getElementPath()+"builders"+File.separator+buildername;
            
            FileUtils.copyFile(MMBaseBuilder, ApplicationBuilder);
        }
        
        // Save the description file
        CloudLayoutElementWriter dscw = new CloudLayoutElementWriter(getElementPath()+"description.xml",this);
        
    }
    
    
    /**
     * deinstall all builders, reldefs, and typerels if possible
     */
    public void uninstall() {
    }
    
    /**
     * get all builders of this CloudLayout element
     */
    public Vector getBuilders() {
        Vector result = new Vector();
        
        Vector builders = getNeededBuilders();
        for (Enumeration e = builders.elements();e.hasMoreElements();) {
            Hashtable builder = (Hashtable)e.nextElement();
            String name = (String)builder.get("name");
            result.addElement(name);
        }
        return result;
    }
    
    /**
     * tell if the builder is loaded. ### in the near future this method has also to check the version of the builder
     * @return false if a needed builder is not loaded
     * @return true if all needed builders are loaded
     */
    public boolean isBuilderLoaded(String name) {
        
        MMObjectBuilder bul=mmbase.getMMObject(name);
        if (bul==null) {
            return false;
        } else {
            return true;
        }
    }
    
    /**
     * install all builders
     */
    public void installBuilders() throws Exception {
        Vector v = getBuilders();
        for (Enumeration e = v.elements();e.hasMoreElements();) {
            String buildername = (String)e.nextElement();
            installBuilder(buildername);
        }
    }
    
    /**
     * install one builder
     * @param buildername the name of the vuilder to install
     */
    public void installBuilder(String buildername) throws Exception {
        if (ApplicationManager.getKioskmode()) {
            throw new InstallationException("MMBase runs in Kiosk mode, it's not possible to install applications");
        }
        log.info("Installing builder: "+buildername);
        String sourceBuilder      = getElementPath()+"builders"+File.separator+buildername+".xml";
        String destinationBuilder = ApplicationManager.getMMBaseBuilderPath()+buildername+".xml";
        
        // Copy the builderfile to the MMBase config dir.
        FileUtils.copyFile(sourceBuilder, destinationBuilder);
        
        // Load builder
        MMObjectBuilder mob = mmbase.loadBuilder(buildername,"");
        
        // Initialize builder
        mob.init();
        
        // Asjemenou
        mmbase.TypeDef.loadTypeDef(buildername);
        
        // check and update versions if needed
        checkBuilderVersion(mob,buildername,destinationBuilder);
        
        // check if all builders for component are there, if yes set version for component.
        checkVersion();
    }
    
    /**
     * Checks the builder version and, if needed, updates the version table.
     * Queries the xml files instead of the builder itself (?)
     * @return always <code>true</code>.
     */
    private boolean checkBuilderVersion(MMObjectBuilder mob, String buildername, String builderfile) {
        
        Versions ver=ApplicationManager.getVersionBuilder();
        //String builderfile = builderpath + mob.getXMLPath() + buildername + ".xml";
        XMLBuilderReader bapp=new XMLBuilderReader(builderfile);
        if (bapp!=null) {
            int version=bapp.getBuilderVersion();
            String maintainer=bapp.getBuilderMaintainer();
            int installedversion=ver.getInstalledVersion(buildername,"builder");
            if (installedversion==-1 || version>installedversion) {
                if (installedversion==-1) {
                    ver.setInstalledVersion(buildername,"builder",maintainer,version);
                } else {
                    ver.updateInstalledVersion(buildername,"builder",maintainer,version);
                }
            }
        }
        return true;
    }
    
    /**
     * install all needed reldefs
     */
    public void installRelDefs() throws Exception {
        for (Enumeration h = getNeededRelDefs().elements();h.hasMoreElements();) {
            Hashtable bh=(Hashtable)h.nextElement();
            installRelDef(bh);
        }
    }
    
    /**
     * install one reldef
     * @param id the identifier of the reldef
     */
    public void installRelDefById(String id) throws Exception {
        Hashtable reldeftable = (Hashtable)neededRelDefs.get(id);
        installRelDef(reldeftable);
    }
    
    /**
     * install one reldef
     */
    private void installRelDef(Hashtable reldeftable) throws Exception {
        if (ApplicationManager.getKioskmode()) {
            throw new InstallationException("MMBase runs in Kiosk mode, it's not possible to install applications");
        }
        String sourcename =	(String)reldeftable.get("source");
        String destinationname = (String)reldeftable.get("target");
        String direction = (String)reldeftable.get("direction");
        String sourceguiname = (String)reldeftable.get("guisourcename");
        String destinationguiname =	(String)reldeftable.get("guitargetname");
        String buildername = (String)reldeftable.get("builder");
        
        RelDef reldef=mmbase.getRelDef();
        if (reldef==null) {
            // throw exception
            log.error("RelDef builder is not loaded");
        }
        
        // retrieve builder info
        int builder=-1;
        if (mmbase.getRelDef().usesbuilder) {
            // if no 'builder' attribute is present (old format), use source name as builder name
            if (buildername==null) {
                buildername=sourcename;
            }
            builder=mmbase.getTypeDef().getIntValue(buildername);
        }
        // is not explicitly set to unidirectional, direction is assumed to be bidirectional
        int dir=2;
        if ("unidirectional".equals(direction)) {
            dir=1;
        }
        
        Vector res=reldef.searchVector("sname=='"+sourcename+"'+dname=='"+destinationname+"'");
        if (res!=null && res.size()>0) {
            log.warn("not installing refdef ("+sourcename+","+destinationname+") because it is allready installed");
        } else {
            MMObjectNode node=reldef.getNewNode("system");
            node.setValue("sname",sourcename);
            node.setValue("dname",destinationname);
            node.setValue("dir",dir);
            node.setValue("sguiname",sourceguiname);
            node.setValue("dguiname",destinationguiname);
            if (reldef.usesbuilder) {
                // if builder is unknown (falsely specified), use the InsRel builder
                if (builder<=0) {
                    builder=mmbase.getInsRel().oType;
                }
                node.setValue("builder",builder);
            }
            int id=reldef.insert("system",node);
            if (id!=-1) {
                // log.warn("RefDef ("+sname+","+dname+") installed");
            }
        }
    }
    
    /**
     * check if an reldef already exists
     * @param reldef the hashtable of the reldef to check
     * @return true, if the reldef exists, false otherwise
     */
    public boolean isRelDefLoaded(Hashtable relationdefinition) {
        String sourcename=(String)relationdefinition.get("source");
        String destinationname=(String)relationdefinition.get("target");
        
        RelDef reldef=mmbase.getRelDef();
        if (reldef!=null) {
            Vector res=reldef.searchVector("sname=='"+sourcename+"'+dname=='"+destinationname+"'");
            if (res!=null && res.size()>0) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * check if all needed reldef entries are available
     * @param reldefs the table with reldefs
     * @return true, if all needed reldefs are loaded, false otherwise
     */
    public boolean areRelDefsLoaded() {
        for (Enumeration e = getNeededRelDefs().elements();e.hasMoreElements();) {
            Hashtable reldef = (Hashtable)e.nextElement();
            if(!isRelDefLoaded(reldef)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * install all relations (reldef)
     */
    public void installTypeRels() throws Exception {
        for (Enumeration h = getNeededRelations().elements();h.hasMoreElements();) {
            Hashtable bh=(Hashtable)h.nextElement();
            installTypeRel(bh);
        }
    }
    
    /**
     * install a relation (typerel)
     * @param id identifier of the relation to install
     */
    public void installTypeRelById(String id) throws Exception{
        Hashtable relation = (Hashtable)neededRelations.get(id);
        installTypeRel(relation);
    }
    
    private void installTypeRel(Hashtable relation) throws Exception{
        if (ApplicationManager.getKioskmode()) {
            throw new InstallationException("MMBase runs in Kiosk mode, it's not possible to install applications");
        }
        String from=(String)relation.get("from");
        String to=(String)relation.get("to");
        String type=(String)relation.get("type");
        String count=(String)relation.get("count");
        if(count==null) {
            count="-1";
        }
        
        TypeRel typerel=mmbase.getTypeRel();
        if (typerel==null) {
            throw new Exception("typerel builder is not loaded");
        }
        TypeDef typedef=mmbase.getTypeDef();
        if (typedef==null) {
            throw new Exception("typedef builder is not loaded");
        }
        RelDef reldef=mmbase.getRelDef();
        if (reldef==null) {
            throw new Exception("reldef builder is not loaded");
        }
        
        // figure out rnumber
        int rnumber=reldef.getGuessedNumber(type);
        if (rnumber==-1) {
            throw new Exception("reldef instance "+type+" is not available");
        }
        
        // figure out snumber
        int snumber=typedef.getIntValue(from);
        if (snumber==-1) {
            throw new Exception("typedef instance "+from+" is not available");
        }
        
        // figure out dnumber
        int dnumber=typedef.getIntValue(to);
        if (dnumber==-1) {
            throw new Exception("typedef instance "+to+" is not available");
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
        } else {
            log.warn("TypeRel ("+from+","+to+","+type+") was already installed");
        }
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
    
    public boolean isTypeDefLoaded(Hashtable relation) throws Exception {
        String from=(String)relation.get("from");
        String to=(String)relation.get("to");
        String type=(String)relation.get("type");
        
        TypeRel typerel=mmbase.getTypeRel();
        if (typerel==null) {
            throw new Exception("typerel builder is not loaded");
        }
        TypeDef typedef=mmbase.getTypeDef();
        if (typedef==null) {
            throw new Exception("typedef builder is not loaded");
        }
        RelDef reldef=mmbase.getRelDef();
        if (reldef==null) {
            throw new Exception("reldef builder is not loaded");
        }
        
        // figure out rnumber
        int rnumber=reldef.getGuessedNumber(type);
        if (rnumber==-1) {
            log.warn("no reldef : "+type+" defined");
            return false;
        }
        
        // figure out snumber
        int snumber=typedef.getIntValue(from);
        if (snumber==-1) {
            log.warn("No node manager with name: "+from+" loaded");
            return false;
        }
        
        // figure out dnumber
        int dnumber=typedef.getIntValue(to);
        if (dnumber==-1) {
            log.warn("No node manager with name: "+to+" loaded");
            return false;
        }
        
        // Check if it already exists
        return typerel.reldefCorrect(snumber,dnumber,rnumber);
    }
    
    public boolean areTypeDefsLoaded() throws Exception {
        Vector allowedrelations = getNeededRelations();
        for (Enumeration e = allowedrelations.elements();e.hasMoreElements();) {
            Hashtable allowedrelation = (Hashtable)e.nextElement();
            if(!isTypeDefLoaded(allowedrelation)) {
                return false;
            }
        }
        return true;
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
        return mmbase.getMMObject(path);
    }
}

