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
import java.text.DateFormat;

import org.w3c.dom.*;

/**
 * A Component Element is the superclass for all Elements.
 *
 * @author Rob Vermeulen
 */
public class ComponentElement {
    protected static Logger log = Logging.getLoggerInstance(Application.class.getName());
    protected String name="";
    protected String creationdate ="";
    protected String MD5="";
    protected String autodeploy="";
    protected String description="";
    protected int version=0;
    protected Application application=null;
    protected String elementtype=null;
    
    protected XMLBasicReader reader = null;
    protected Vector dependencies = null;
    protected Vector neededbuilders = null;
    protected Vector neededreldefs = null;
    protected Vector neededrelations = null;
    Hashtable neededRelDefs = null;
    Hashtable neededRelations = null;
    
    ComponentElement(Hashtable info,Application app, String et) {
        application = app;
        elementtype = et;
        name = (String)info.get("name");
        creationdate = (String)info.get("creationTime");
        MD5 = (String)info.get("MD5");
        autodeploy = (String)info.get("autoDeploy");
        
    }
    
    /**
     * delete this element
     **/
    public void delete() throws Exception {
        FileUtils.deleteFiles(getElementPath());
    }
    
    /**
     * get the path (direcotiry) of this element
     * @return string the path
     **/
    String getElementPath() {
        return application.getApplicationLocation()+elementtype+"s"+File.separator+getName()+File.separator;
    }
    
    /**
     * get the name of the element
     * @return the name of the element
     */
    public String getName() {
        return name;
    }
    
    /**
     * get the creation date of the element
     * @return the date of creation of the element
     */
    public String getCreationDate() {
        return creationdate;
    }
    
    /**
     * set the current time as creation date
     * @return the new creationdate
     */
    public void  setCreationDate() {
        DateFormat df = DateFormat.getDateTimeInstance();
        creationdate = df.format(new Date());
    }
    
    /**
     * get the MD5 print of the element
     * @return the MD5 print of the element
     */
    public String getMD5() {
        return MD5;
    }
    
    /**
     * get information about auto deploying this element
     */
    public String getAutoDeploy() {
        return autodeploy;
    }
    
    /**
     * set the auto deploy value
     */
    public void setAutoDeploy(String newautodeploy) {
        autodeploy = newautodeploy;
        application.writeConfiguration();
    }
    
    /**
     * get the description of the element
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * set the description of the element
     */
    public void setDescription(String newdescription) {
        description = newdescription;
        application.writeConfiguration();
    }
    
    /**
     * get version of the element from the config file
     */
    public int getConfigurationVersion() {
        return version;
    }
    
    /**
     * give the version of the (installed) application.
     * @return the version, -1 if the application is not installed
     */
    public int getInstalledVersion() {
        return ApplicationManager.getVersionBuilder().getInstalledVersion(application.getName()+"/"+getName(),"application/"+elementtype);
    }
    
    public void setVersion() {
        Versions ver=ApplicationManager.getVersionBuilder();
        int installedversion=getInstalledVersion();
        int version=getConfigurationVersion();
        if (installedversion==-1 || version>installedversion) {
            if (installedversion==-1) {
                ver.setInstalledVersion(application.getName()+"/"+getName(),"application/"+elementtype,"maintainer",version);
            } else {
                ver.updateInstalledVersion(application.getName()+"/"+getName(),"application/"+elementtype,"maintainer",version);
            }
        }
    }
    
    /**
     * Get the Builders needed for this application
     */
    public Vector getNeededBuilders() {
        if(neededbuilders==null) {
            neededbuilders=new Vector();
            for(Enumeration ns=reader.getChildElements("cloudlayout.neededbuilderlist","builder");ns.hasMoreElements(); ) {
                Element n3=(Element)ns.nextElement();
                Hashtable bset=new Hashtable();
                bset.put("name",reader.getElementValue(n3));
                addAttribute(bset,n3,"maintainer");
                addAttribute(bset,n3,"version");
                neededbuilders.addElement(bset);
            }
        }
        return neededbuilders;
    }
    
    /**
     * add builder needed for this element
     */
    public void addNeededBuilder(String name) {
        Hashtable bset = new Hashtable();
        bset.put("name",name);
        bset.put("maintainer","mmbase.org ###");
        bset.put("version",""+getBuilderVersion(name));
        neededbuilders.addElement(bset);
    }
    
    
    /**
     * Get the RelDefs needed for this application
     */
    public Vector getNeededRelDefs() {
        if(neededreldefs==null) {
            neededreldefs=new Vector();
            int index = 0;
            
            // special hashtable
            neededRelDefs = new Hashtable();
            for(Enumeration ns=reader.getChildElements("cloudlayout.neededreldeflist","reldef");ns.hasMoreElements(); ) {
                index++;
                Element n3=(Element)ns.nextElement();
                Hashtable bset=new Hashtable();
                addAttribute(bset,n3,"source");
                addAttribute(bset,n3,"target");
                addAttribute(bset,n3,"direction");
                addAttribute(bset,n3,"guisourcename");
                addAttribute(bset,n3,"guitargetname");
                addAttribute(bset,n3,"builder");
                bset.put("id",""+index);
                neededRelDefs.put(""+index,bset);
                neededreldefs.addElement(bset);
            }
        }
        return neededreldefs;
    }
    
    /**
     * add reldef needed
     */
    public void addNeededRelDef(String sname, String dname, String dir, String sguiname, String dguiname, String builder) {
        Hashtable bset = new Hashtable();
        bset.put("source",sname);
        bset.put("target",dname);
        bset.put("direction",dir);
        bset.put("guisourcename",sguiname);
        bset.put("guitargetname",dguiname);
        bset.put("builder",builder);
        neededreldefs.addElement(bset);
    }
    
    /**
     * Get allowed relations for this application
     */
    public Vector getNeededRelations() {
        if(neededrelations==null) {
            neededrelations=new Vector();
            
            // special hashtable
            neededRelations = new Hashtable();
            int index=0;
            for(Enumeration ns=reader.getChildElements("cloudlayout.allowedrelationlist","relation");ns.hasMoreElements(); ) {
                index++;
                Element n3=(Element)ns.nextElement();
                Hashtable bset=new Hashtable();
                addAttribute(bset,n3,"from");
                addAttribute(bset,n3,"to");
                addAttribute(bset,n3,"type");
                addAttribute(bset,n3,"count");
                bset.put("id",""+index);
                neededRelations.put(""+index,bset);
                neededrelations.addElement(bset);
            }}
        return neededrelations;
    }
    
    /**
     * add needed relations
     */
    public void addNeededRelation(String from, String to, String type, String count) {
        Hashtable bset = new Hashtable();
        bset.put("from",from);
        bset.put("to",to);
        bset.put("type",type);
        bset.put("count",count);
        neededrelations.addElement(bset);
    }
    
    // ### move to subclasses
    protected Vector objectsources = null;
    /**
     * get object sources
     */
    public Vector getObjectSources() {
        if(objectsources==null) {
            objectsources=new Vector();
            for(Enumeration ns=reader.getChildElements("dataset.objects","source");ns.hasMoreElements(); ) {
                Element n3=(Element)ns.nextElement();
                Hashtable bset=new Hashtable();
                addAttribute(bset,n3,"nodemanager");
                addAttribute(bset,n3,"md5");
                objectsources.addElement(bset);
            }
        }
        return objectsources;
    }
    
    // ### move to subclass
    protected Vector relationsources = null;
    /**
     * get relation sources
     */
    public Vector getRelationSources() {
        if(relationsources==null){
            relationsources=new Vector();
            for(Enumeration ns=reader.getChildElements("dataset.relations","source");ns.hasMoreElements(); ) {
                Element n3=(Element)ns.nextElement();
                Hashtable bset=new Hashtable();
                addAttribute(bset,n3,"nodemanager");
                addAttribute(bset,n3,"md5");
                relationsources.addElement(bset);
            }
        }
        return relationsources;
    }
    
    /**
     * get dependencies
     */
    public Vector getDependencies() {
        if(dependencies==null) {
		dependencies = new Vector();
            for(Enumeration ns=reader.getChildElements(elementtype+".requires","require");ns.hasMoreElements(); ) {
                Element n3=(Element)ns.nextElement();
                Hashtable bset=new Hashtable();
                addAttribute(bset,n3,"component");
                addAttribute(bset,n3,"name");
                addAttribute(bset,n3,"create-date");
                addAttribute(bset,n3,"method");
                dependencies.addElement(bset);
            }
        }
        return dependencies;
    }
    
    private void addAttribute(Hashtable bset, Element n, String attribute) {
        String val=n.getAttribute(attribute);
        if (!val.equals("")) {
            bset.put(attribute,val);
        }
    }
    
    // ### moet naar subclass
    protected Vector backupmethods = null;
    public Vector getBackupMethods() {
        if(backupmethods==null) {
		backupmethods = new Vector();
            for(Enumeration ns=reader.getChildElements("dataset.backupmethod","contextsource");ns.hasMoreElements(); ) {
                Hashtable bset = new Hashtable();
                Element n3=(Element)ns.nextElement();
                addAttribute(bset,n3,"path");
                addAttribute(bset,n3,"type");
                addAttribute(bset,n3,"goal");
                backupmethods.addElement(bset);
            }
        }
        return backupmethods;
    }
    
    public void checkDependencies() throws Exception {
        log.debug("Checking dependencies for Element "+getName());
        Vector dependencies = getDependencies();
        for (Enumeration e = dependencies.elements();e.hasMoreElements();) {
            Hashtable dependency = (Hashtable)e.nextElement();
            String componentToCheck = (String)dependency.get("component");
            String elementToCheck = (String)dependency.get("name");
            String createTime = (String)dependency.get("create-date");
            
            Component component = application.getComponent(componentToCheck);
            ComponentElement ce = component.getElement(elementToCheck);
            if (component==null) {
                throw new Exception("require component "+componentToCheck+", cannot find component (name is case sensitive)");
            }
            if (ce==null) {
                throw new Exception("require element "+elementToCheck+", cannot find element within component "+componentToCheck+" (name is case sensitive)");
            }
            
            // if we have to check on dates.
            DateFormat df =DateFormat.getDateTimeInstance();
            Date dateToCheck = df.parse(createTime);
            Date dateOfElement =  df.parse(""+ce.getCreationDate());
            
            // find out which method to use (equals, at-least)
            String method = (String)dependency.get("method");
            if (method.equals("equals")) {
                if(!dateToCheck.equals(dateOfElement)) {
                    throw new Exception("Element '"+getName()+"' REQUIRES "+ componentToCheck+"/"+elementToCheck+
                    " with 'create-date="+dateToCheck+"'.");
                }
            }
            if (method.equals("at-least")) {
                if(dateToCheck.compareTo(dateOfElement)>0) {
                    throw new Exception("Element '"+getName()+"' REQUIRES "+ componentToCheck+"/"+elementToCheck+
                    " with 'create-date>="+dateToCheck+"'.");
                }
            }
            if(!method.equals("at-least") && !method.equals("equals")) {
                throw new Exception(elementtype+" "+getName()+" needs "+ elementToCheck+" of component "+
                componentToCheck+" with a valid method");
            }
        }
    }
    
    /**
     * give the version of a builder
     * @param buildername the name of the builder
     * @return the version, -1 if the application is not installed
     **/
    public int getBuilderVersion(String buildername) {
        return ApplicationManager.getVersionBuilder().getInstalledVersion(buildername,"builder");
    }
}
