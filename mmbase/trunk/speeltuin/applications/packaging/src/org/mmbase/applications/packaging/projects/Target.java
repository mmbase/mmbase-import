/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.applications.packaging.projects;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

import org.mmbase.applications.packaging.*;
import org.mmbase.applications.packaging.projects.creators.*;
import org.mmbase.util.*;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

import org.w3c.dom.*;

/**
 * @author Daniel Ockeloen
 * 
 */
public class Target {
 
    // logger
    static private Logger log = Logging.getLoggerInstance(Target.class.getName()); 

   String name;
   String depends;
   String path;
   String basedir="";
   String type;
   boolean isbundle;
   HashMap items=new HashMap();
   XMLBasicReader reader;

   CreatorInterface creator;

   public Target(String name) {
	this.name=name;
	depends="";
	type="basic/macro";
	isbundle=false;
   }

   public XMLBasicReader getReader() {
	return reader;
   }

   public String getName() {
	return name;
   }

   public String getDepends() {
	return depends;
   }

   public String getPath() {
	return path;
   }

   public String getType() {
	return type;
   }

   public void setBundle(boolean state) {
	isbundle=state;
   }

  public boolean isBundle() {
	return isbundle;
  }


   public void setCreator(CreatorInterface cr) {
	creator=cr;	
	//log.info("BASEDIR="+basedir+path);
        File file = new File(basedir+path);
        if (file.exists()) {
               reader = new XMLBasicReader(basedir+path,creator.getClass());
		cr.decodeItems(this);
        }
   }

   public void setDefaults() {
	creator.setDefaults(this);
   }

   public void setDepends(String depends) {
	this.depends=depends;
   }

   public void setBaseDir(String basedir) {
	this.basedir=basedir;
   }

   public String getBaseDir() {
	return basedir;
   }

   public void setPath(String path) {
	this.path=path;
   }

   public void setType(String type) {
	this.type=type;
   }

   public int getLastVersion() {
	return creator.getLastVersion(this);
   }
	
   public int getNextVersion() {
	return creator.getNextVersion(this);
   }

   public String getLastDate() {
	return creator.getLastDate(this);
   }

   public boolean createPackage(int version) {
	return creator.createPackage(this,version);
   }

   public boolean addPackageDepends(String packageid,String version) {
        return creator.addPackageDepends(this,packageid,version);
   }

   public boolean delPackageDepends(String packageid,String version,String versionmode) {
        return creator.delPackageDepends(this,packageid,version,versionmode);
   }


   public boolean setPackageDepends(String packageid,String oldversion,String oldversionmode,String newversion,String newversionmode) {
        return creator.setPackageDepends(this,packageid,oldversion,oldversionmode,newversion,newversionmode);
   }


   public Enumeration getPackageSteps() {
	return creator.getPackageSteps();
   }

   public Enumeration getPackageSteps(int logid) {
	return creator.getPackageSteps(logid);
   }

   public void clearPackageSteps() {
	creator.clearPackageSteps();
   }

   public String getMaintainer() {
	return creator.getMaintainer(this);
   }

   public String getDescription() {
	return creator.getDescription(this);
   }

   public boolean setDescription(String newdescription) {
	return creator.setDescription(this,newdescription);
   }

   public boolean setName(String newname) {
	return creator.setName(this,newname);
   }

   public boolean setMaintainer(String newmaintainer) {
	return creator.setMaintainer(this,newmaintainer);
   }


   public boolean setLicenseVersion(String newlicenseversion) {
	return creator.setLicenseVersion(this,newlicenseversion);
   }

   public boolean setLicenseType(String newlicensetype) {
	return creator.setLicenseType(this,newlicensetype);
   }

   public boolean addPackageInitiator(String newname,String newcompany) {
	return creator.addPackageInitiator(this,newname,newcompany);
   }

   public boolean setPackageInitiator(String oldname,String newname,String oldcompany,String newcompany) {
	return creator.setPackageInitiator(this,oldname,newname,oldcompany,newcompany);
   }

   public boolean delPackageInitiator(String oldname,String oldcompany) {
	return creator.delPackageInitiator(this,oldname,oldcompany);
   }


   public boolean addPackageContact(String newreason,String newname,String newemail) {
	return creator.addPackageContact(this,newreason,newname,newemail);
   }

   public boolean setPackageContact(String oldreason,String newreason,String oldname,String newname,String oldemail,String newemail) {
	return creator.setPackageContact(this,oldreason,newreason,oldname,newname,oldemail,newemail);
   }

   public boolean delPackageContact(String oldreason,String oldname,String oldemail) {
	return creator.delPackageContact(this,oldreason,oldname,oldemail);
   }


   public boolean addPackageDeveloper(String newname,String newcompany) {
	return creator.addPackageDeveloper(this,newname,newcompany);
   }

   public boolean setPackageDeveloper(String oldname,String newname,String oldcompany,String newcompany) {
	return creator.setPackageDeveloper(this,oldname,newname,oldcompany,newcompany);
   }

   public boolean delPackageDeveloper(String oldname,String oldcompany) {
	return creator.delPackageDeveloper(this,oldname,oldcompany);
   }


   public boolean addPackageSupporter(String newcompany) {
	return creator.addPackageSupporter(this,newcompany);
   }

   public boolean setPackageSupporter(String oldcompany,String newcompany) {
	return creator.setPackageSupporter(this,oldcompany,newcompany);
   }

   public boolean delPackageSupporter(String oldcompany) {
	return creator.delPackageSupporter(this,oldcompany);
   }

   public boolean setLicenseName(String newlicensename) {
	return creator.setLicenseName(this,newlicensename);
   }

   public String getReleaseNotes() {
	return creator.getReleaseNotes(this);
   }

   public String getInstallationNotes() {
	return creator.getInstallationNotes(this);
   }

   public String getPackageName() {
		return creator.getName(this);
   }

   public String getLicenseType() {
	return creator.getLicenseType(this);
   }

   public String getLicenseName() {
	return creator.getLicenseName(this);
   }

   public String getLicenseVersion() {
	return creator.getLicenseVersion(this);
   }

   public ArrayList getRelatedPeople(String type) {
	return creator.getRelatedPeople(type,this);
   }

   public ArrayList getPackageDepends() {
	return creator.getPackageDepends(this);
   }

   public ArrayList getIncludedPackages() {
	if (isBundle()) {
		return creator.getIncludedPackages(this);
	}
	return null;
   }

   public boolean addPackage(String newpackage) {
        if (isBundle()) {
                return creator.addPackage(this,newpackage);
        }
        return false;
   }


   public boolean setIncludedVersion(String id,String newversion) {
      return creator.setIncludedVersion(this,id,newversion);
   }

   public boolean delIncludedPackage(String id) {
      return creator.delIncludedPackage(this,id);
   }

   public boolean setItem(String name,Object item) {
	items.put(name,item);
	return true;
   }

   public Object getItem(String name) {
	Object o=items.get(name);	
	return o;
   }

   public boolean save() {
        // check if the dirs are created, if not create them
        String dirsp=basedir+path.substring(0,path.lastIndexOf(File.separator));
        File dirs=new File(dirsp);
        if (!dirs.exists()) {
                dirs.mkdirs();
        }
	String body=creator.getXMLFile(this);
	// write back to disk	
        File sfile = new File(basedir+getPath());
        try {
            DataOutputStream scan = new DataOutputStream(new FileOutputStream(sfile));
            scan.writeBytes(body);
            scan.flush();
            scan.close();
        } catch(Exception e) {
            log.error(Logging.stackTrace(e));
        }
        return true;
    }

   public boolean hasSyntaxErrors() {
	// performs several syntax checks to signal
	// the users in the gui tools on possible problems
	if (getDescription().equals("")) return true;
	if (getMaintainer().equals("")) return true;
	if (getDescription().equals("")) return true;
	if (getRelatedPeople("initiators")==null || getRelatedPeople("initiators").size()==0) return true;
	if (getRelatedPeople("developers")==null || getRelatedPeople("developers").size()==0) return true;
	if (getRelatedPeople("contacts")==null || getRelatedPeople("contacts").size()==0) return true;
	return false;
   }
    

}
