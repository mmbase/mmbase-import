/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.applications.packaging.projects.creators;

import org.mmbase.applications.packaging.projects.*;
import java.io.*;
import java.util.*;
import java.util.jar.*;
import org.w3c.dom.*;
import org.xml.sax.*;

/**
 * Interface for all the creators
 */
public interface CreatorInterface {
	public String getType();
	public void setType(String type);
        public int getLastVersion(Target target);
        public int getNextVersion(Target target);
        public String getLastDate(Target target);
	public boolean createPackage(Target target,int newversion);
        public Enumeration getPackageSteps();
        public Enumeration getPackageSteps(int logid);
        public void clearPackageSteps();
	public String getMaintainer(Target target);
	public String getDescription(Target target);
	public String getName(Target target);
	public String getLicenseType(Target target);
	public String getLicenseVersion(Target target);
	public String getLicenseName(Target target);
	public boolean setDescription(Target target,String newdescription);
	public boolean setLicenseType(Target target,String newlicensetype);
	public boolean setLicenseName(Target target,String newlicensename);
	public boolean setName(Target target,String newname);
	public boolean setMaintainer(Target target,String newmaintainer);
	public boolean setLicenseVersion(Target target,String newlicenseversion);
   	public ArrayList getRelatedPeople(String type,Target target);
   	public String getReleaseNotes(Target target);
   	public String getInstallationNotes(Target target);
   	public String getXMLFile(Target target);
	public ArrayList getIncludedPackages(Target target);
        public boolean setIncludedVersion(Target target,String id,String newversion);
        public boolean delIncludedPackage(Target target,String id);
	public boolean decodeItems(Target target);
        public boolean addPackage(Target target,String newpackage);
        public boolean addPackageInitiator(Target target,String newname,String newcompany);
        Public boolean delPackageInitiator(Target target,String oldname,String oldcompany);
        public boolean setPackageInitiator(Target target,String oldname,String newname,String oldcompany,String newcompany);
        public boolean addPackageDeveloper(Target target,String newname,String newcompany);
        public boolean delPackageDeveloper(Target target,String oldname,String oldcompany);
        public boolean setPackageDeveloper(Target target,String oldname,String newname,String oldcompany,String newcompany);
        public boolean addPackageSupporter(Target target,String newcompany);
        public boolean delPackageSupporter(Target target,String oldcompany);
        public boolean setPackageSupporter(Target target,String oldcompany,String newcompany);
        public boolean addPackageContact(Target target,String newreason,String newname,String newemail);
        public boolean delPackageContact(Target target,String oldreason, String oldname,String oldemail);
        public boolean setPackageContact(Target target,String oldreason,String newreason, String oldname,String newname,String oldemail,String newemail);
        public boolean addPackageDepends(Target target,String packageid,String version);
        public boolean delPackageDepends(Target target,String packageid,String version,String versionmode);
        public boolean setPackageDepends(Target target,String packageid,String oldversion,String oldversionmode,String newversion,String newversionmode);
        public ArrayList getPackageDepends(Target target);
        public void setDefaults(Target target);
}
