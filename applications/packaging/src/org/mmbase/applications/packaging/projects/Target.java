/*
 *  This software is OSI Certified Open Source Software.
 *  OSI Certified is a certification mark of the Open Source Initiative.
 *  The license (Mozilla version 1.0) can be read at the MMBase site.
 *  See http://www.MMBase.org/license
 */
package org.mmbase.applications.packaging.projects;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.mmbase.applications.packaging.*;
import org.mmbase.applications.packaging.bundlehandlers.BundleInterface;
import org.mmbase.applications.packaging.packagehandlers.PackageInterface;
import org.mmbase.applications.packaging.projects.creators.CreatorInterface;
import org.mmbase.applications.packaging.providerhandlers.ProviderInterface;
import org.mmbase.applications.packaging.util.ExtendedDocumentReader;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @author     Daniel Ockeloen
 *
 */
public class Target {

    // logger
    private static Logger log = Logging.getLoggerInstance(Target.class.getName());

    String name;
    String depends;
    String path;
    String basedir = "";
    String type;
    String publishprovider;
    String publishsharepassword;
    boolean publishstate;
    boolean isbundle;
    private ArrayList<String> screenshots = new ArrayList<String>();
    private ArrayList<String> starturls = new ArrayList<String>();
    HashMap<String, Object> items = new HashMap<String, Object>();
    ExtendedDocumentReader reader;  
    Project parent;

    CreatorInterface creator;


    /**
     *Constructor for the Target object
     *
     * @param  name  Description of the Parameter
     */
    public Target(Project parent,String name) {
        this.name = name;
	this.parent =  parent;
        depends = "";
        type = "basic/macro";
        isbundle = false;
    }


    /**
     *  Gets the reader attribute of the Target object
     *
     * @return    The reader value
     */
    public ExtendedDocumentReader getReader() {
        return reader;
    }


    /**
     *  Gets the name attribute of the Target object
     *
     * @return    The name value
     */
    public String getName() {
        return name;
    }


    /**
     *  Gets the depends attribute of the Target object
     *
     * @return    The depends value
     */
    public String getDepends() {
        return depends;
    }

    public ArrayList<String> getScreenshots() {
        return screenshots;
    }

    public ArrayList<String> getStarturls() {
        return starturls;
    }

    /**
     *  Gets the path attribute of the Target object
     *
     * @return    The path value
     */
    public String getPath() {
        return path;
    }

    public String getFullPath() {
        return basedir+path;
    }


    /**
     *  Gets the type attribute of the Target object
     *
     * @return    The type value
     */
    public String getType() {
        return type;
    }


    /**
     *  Sets the bundle attribute of the Target object
     *
     * @param  state  The new bundle value
     */
    public void setBundle(boolean state) {
        isbundle = state;
    }

    public String getState() {
	return creator.getState();
    }


    /**
     *  Gets the bundle attribute of the Target object
     *
     * @return    The bundle value
     */
    public boolean isBundle() {
        return isbundle;
    }


    /**
     *  Sets the creator attribute of the Target object
     *
     * @param  cr  The new creator value
     */
    public void setCreator(CreatorInterface cr) {
        creator = cr;
        File file = new File(basedir + path);
        if (file.exists()) {
            reader = new ExtendedDocumentReader(basedir + path, creator.getClass());
            cr.decodeItems(this);
        }
    }

    public CreatorInterface getCreator() {
	return creator;
    }


    /**
     *  Sets the defaults attribute of the Target object
     */
    public void setDefaults() {
        creator.setDefaults(this);
    }


    /**
     *  Sets the depends attribute of the Target object
     *
     * @param  depends  The new depends value
     */
    public void setDepends(String depends) {
        this.depends = depends;
    }


    /**
     *  Sets the baseDir attribute of the Target object
     *
     * @param  basedir  The new baseDir value
     */
    public void setBaseDir(String basedir) {
        this.basedir = basedir;
    }


    /**
     *  Gets the baseDir attribute of the Target object
     *
     * @return    The baseDir value
     */
    public String getBaseDir() {
        return basedir;
    }


    /**
     *  Sets the path attribute of the Target object
     *
     * @param  path  The new path value
     */
    public void setPath(String path) {
	// map to internal path
	path=path.replace('\\',File.separatorChar);
	path=path.replace('/',File.separatorChar);
        this.path = path;
    }


    /**
     *  Sets the type attribute of the Target object
     *
     * @param  type  The new type value
     */
    public void setType(String type) {
        this.type = type;
    }


    /**
     *  Gets the lastVersion attribute of the Target object
     *
     * @return    The lastVersion value
     */
    public int getLastVersion() {
        return creator.getLastVersion(this);
    }


    /**
     *  Gets the nextVersion attribute of the Target object
     *
     * @return    The nextVersion value
     */
    public int getNextVersion() {
        return creator.getNextVersion(this);
    }


    /**
     *  Gets the lastDate attribute of the Target object
     *
     * @return    The lastDate value
     */
    public String getLastDate() {
        return creator.getLastDate(this);
    }

    public void createPackageThreaded(int version) {
	creator.createPackageThreaded(this, version);
	ProviderManager.resetSleepCounter();
    }

    /**
     *  Description of the Method
     *
     * @param  version  Description of the Parameter
     * @return          Description of the Return Value
     */
    public boolean createPackage(int version) {
	boolean result=creator.createPackage(this, version);
	if (result) {
		ProviderManager.resetSleepCounter();
	}
	return result;
    }


    /**
     *  Adds a feature to the PackageDepends attribute of the Target object
     *
     * @param  packageid  The feature to be added to the PackageDepends attribute
     * @param  version    The feature to be added to the PackageDepends attribute
     * @return            Description of the Return Value
     */
    public boolean addPackageDepends(String packageid, String version) {
        return creator.addPackageDepends(this, packageid, version);
    }

    public void addScreenshot(String name, String file, String description) {
	screenshots.add(name);
	screenshots.add(file);
	screenshots.add(description);
    }


    public void addStarturl(String name, String link, String description) {
	starturls.add(name);
	starturls.add(link);
	starturls.add(description);
    }



    /**
     *  Description of the Method
     *
     * @param  packageid    Description of the Parameter
     * @param  version      Description of the Parameter
     * @param  versionmode  Description of the Parameter
     * @return              Description of the Return Value
     */
    public boolean delPackageDepends(String packageid, String version, String versionmode) {
        return creator.delPackageDepends(this, packageid, version, versionmode);
    }


    /**
     *  Sets the packageDepends attribute of the Target object
     *
     * @param  packageid       The new packageDepends value
     * @param  oldversion      The new packageDepends value
     * @param  oldversionmode  The new packageDepends value
     * @param  newversion      The new packageDepends value
     * @param  newversionmode  The new packageDepends value
     * @return                 Description of the Return Value
     */
    public boolean setPackageDepends(String packageid, String oldversion, String oldversionmode, String newversion, String newversionmode) {
        return creator.setPackageDepends(this, packageid, oldversion, oldversionmode, newversion, newversionmode);
    }


    /**
     *  Gets the packageSteps attribute of the Target object
     *
     * @return    The packageSteps value
     */
    public Iterator<packageStep> getPackageSteps() {
        return creator.getPackageSteps();
    }


    /**
     *  Gets the packageSteps attribute of the Target object
     *
     * @param  logid  Description of the Parameter
     * @return        The packageSteps value
     */
    public Iterator<packageStep> getPackageSteps(int logid) {
        return creator.getPackageSteps(logid);
    }


    /**
     *  Description of the Method
     */
    public void clearPackageSteps() {
        creator.clearPackageSteps();
    }


    /**
     *  Gets the maintainer attribute of the Target object
     *
     * @return    The maintainer value
     */
    public String getMaintainer() {
        return creator.getMaintainer(this);
    }


    /**
     *  Gets the description attribute of the Target object
     *
     * @return    The description value
     */
    public String getDescription() {
        return creator.getDescription(this);
    }


    /**
     *  Sets the description attribute of the Target object
     *
     * @param  newdescription  The new description value
     * @return                 Description of the Return Value
     */
    public boolean setDescription(String newdescription) {
        return creator.setDescription(this, newdescription);
    }


    /**
     *  Sets the name attribute of the Target object
     *
     * @param  newname  The new name value
     * @return          Description of the Return Value
     */
    public boolean setName(String newname) {
        return creator.setName(this, newname);
    }


    /**
     *  Sets the maintainer attribute of the Target object
     *
     * @param  newmaintainer  The new maintainer value
     * @return                Description of the Return Value
     */
    public boolean setMaintainer(String newmaintainer) {
        return creator.setMaintainer(this, newmaintainer);
    }


    /**
     *  Sets the licenseVersion attribute of the Target object
     *
     * @param  newlicenseversion  The new licenseVersion value
     * @return                    Description of the Return Value
     */
    public boolean setLicenseVersion(String newlicenseversion) {
        return creator.setLicenseVersion(this, newlicenseversion);
    }


    /**
     *  Sets the licenseType attribute of the Target object
     *
     * @param  newlicensetype  The new licenseType value
     * @return                 Description of the Return Value
     */
    public boolean setLicenseType(String newlicensetype) {
        return creator.setLicenseType(this, newlicensetype);
    }


    /**
     *  Adds a feature to the PackageInitiator attribute of the Target object
     *
     * @param  newname     The feature to be added to the PackageInitiator attribute
     * @param  newcompany  The feature to be added to the PackageInitiator attribute
     * @return             Description of the Return Value
     */
    public boolean addPackageInitiator(String newname, String newcompany) {
        return creator.addPackageInitiator(this, newname, newcompany);
    }


    /**
     *  Sets the packageInitiator attribute of the Target object
     *
     * @param  oldname     The new packageInitiator value
     * @param  newname     The new packageInitiator value
     * @param  oldcompany  The new packageInitiator value
     * @param  newcompany  The new packageInitiator value
     * @return             Description of the Return Value
     */
    public boolean setPackageInitiator(String oldname, String newname, String oldcompany, String newcompany) {
        return creator.setPackageInitiator(this, oldname, newname, oldcompany, newcompany);
    }


    /**
     *  Description of the Method
     *
     * @param  oldname     Description of the Parameter
     * @param  oldcompany  Description of the Parameter
     * @return             Description of the Return Value
     */
    public boolean delPackageInitiator(String oldname, String oldcompany) {
        return creator.delPackageInitiator(this, oldname, oldcompany);
    }


    /**
     *  Adds a feature to the PackageContact attribute of the Target object
     *
     * @param  newreason  The feature to be added to the PackageContact attribute
     * @param  newname    The feature to be added to the PackageContact attribute
     * @param  newemail   The feature to be added to the PackageContact attribute
     * @return            Description of the Return Value
     */
    public boolean addPackageContact(String newreason, String newname, String newemail) {
        return creator.addPackageContact(this, newreason, newname, newemail);
    }


    /**
     *  Sets the packageContact attribute of the Target object
     *
     * @param  oldreason  The new packageContact value
     * @param  newreason  The new packageContact value
     * @param  oldname    The new packageContact value
     * @param  newname    The new packageContact value
     * @param  oldemail   The new packageContact value
     * @param  newemail   The new packageContact value
     * @return            Description of the Return Value
     */
    public boolean setPackageContact(String oldreason, String newreason, String oldname, String newname, String oldemail, String newemail) {
        return creator.setPackageContact(this, oldreason, newreason, oldname, newname, oldemail, newemail);
    }


    /**
     *  Description of the Method
     *
     * @param  oldreason  Description of the Parameter
     * @param  oldname    Description of the Parameter
     * @param  oldemail   Description of the Parameter
     * @return            Description of the Return Value
     */
    public boolean delPackageContact(String oldreason, String oldname, String oldemail) {
        return creator.delPackageContact(this, oldreason, oldname, oldemail);
    }


    /**
     *  Adds a feature to the PackageDeveloper attribute of the Target object
     *
     * @param  newname     The feature to be added to the PackageDeveloper attribute
     * @param  newcompany  The feature to be added to the PackageDeveloper attribute
     * @return             Description of the Return Value
     */
    public boolean addPackageDeveloper(String newname, String newcompany) {
        return creator.addPackageDeveloper(this, newname, newcompany);
    }


    /**
     *  Sets the packageDeveloper attribute of the Target object
     *
     * @param  oldname     The new packageDeveloper value
     * @param  newname     The new packageDeveloper value
     * @param  oldcompany  The new packageDeveloper value
     * @param  newcompany  The new packageDeveloper value
     * @return             Description of the Return Value
     */
    public boolean setPackageDeveloper(String oldname, String newname, String oldcompany, String newcompany) {
        return creator.setPackageDeveloper(this, oldname, newname, oldcompany, newcompany);
    }


    /**
     *  Description of the Method
     *
     * @param  oldname     Description of the Parameter
     * @param  oldcompany  Description of the Parameter
     * @return             Description of the Return Value
     */
    public boolean delPackageDeveloper(String oldname, String oldcompany) {
        return creator.delPackageDeveloper(this, oldname, oldcompany);
    }


    /**
     *  Adds a feature to the PackageSupporter attribute of the Target object
     *
     * @param  newcompany  The feature to be added to the PackageSupporter attribute
     * @return             Description of the Return Value
     */
    public boolean addPackageSupporter(String newcompany) {
        return creator.addPackageSupporter(this, newcompany);
    }


    /**
     *  Sets the packageSupporter attribute of the Target object
     *
     * @param  oldcompany  The new packageSupporter value
     * @param  newcompany  The new packageSupporter value
     * @return             Description of the Return Value
     */
    public boolean setPackageSupporter(String oldcompany, String newcompany) {
        return creator.setPackageSupporter(this, oldcompany, newcompany);
    }


    /**
     *  Description of the Method
     *
     * @param  oldcompany  Description of the Parameter
     * @return             Description of the Return Value
     */
    public boolean delPackageSupporter(String oldcompany) {
        return creator.delPackageSupporter(this, oldcompany);
    }


    /**
     *  Sets the licenseName attribute of the Target object
     *
     * @param  newlicensename  The new licenseName value
     * @return                 Description of the Return Value
     */
    public boolean setLicenseName(String newlicensename) {
        return creator.setLicenseName(this, newlicensename);
    }


    /**
     *  Gets the releaseNotes attribute of the Target object
     *
     * @return    The releaseNotes value
     */
    public String getReleaseNotes() {
        return creator.getReleaseNotes(this);
    }


    /**
     *  Gets the installationNotes attribute of the Target object
     *
     * @return    The installationNotes value
     */
    public String getInstallationNotes() {
        return creator.getInstallationNotes(this);
    }


    /**
     *  Gets the packageName attribute of the Target object
     *
     * @return    The packageName value
     */
    public String getPackageName() {
        return creator.getName(this);
    }


    /**
     *  Gets the licenseType attribute of the Target object
     *
     * @return    The licenseType value
     */
    public String getLicenseType() {
        return creator.getLicenseType(this);
    }


    /**
     *  Gets the licenseName attribute of the Target object
     *
     * @return    The licenseName value
     */
    public String getLicenseName() {
        return creator.getLicenseName(this);
    }


    /**
     *  Gets the licenseVersion attribute of the Target object
     *
     * @return    The licenseVersion value
     */
    public String getLicenseVersion() {
        return creator.getLicenseVersion(this);
    }


    /**
     *  Gets the relatedPeople attribute of the Target object
     *
     * @param  type  Description of the Parameter
     * @return       The relatedPeople value
     */
    public ArrayList<Person> getRelatedPeople(String type) {
        return creator.getRelatedPeople(type, this);
    }


    /**
     *  Gets the packageDepends attribute of the Target object
     *
     * @return    The packageDepends value
     */
    public ArrayList<PackageDepend> getPackageDepends() {
        return creator.getPackageDepends(this);
    }


    /**
     *  Gets the includedPackages attribute of the Target object
     *
     * @return    The includedPackages value
     */
    public ArrayList<IncludedPackage> getIncludedPackages() {
        if (isBundle()) {
            return creator.getIncludedPackages(this);
        }
        return null;
    }


    /**
     *  Adds a feature to the Package attribute of the Target object
     *
     * @param  newpackage  The feature to be added to the Package attribute
     * @return             Description of the Return Value
     */
    public boolean addPackage(String newpackage) {
        if (isBundle()) {
            return creator.addPackage(this, newpackage);
        }
        return false;
    }


    /**
     *  Sets the includedVersion attribute of the Target object
     *
     * @param  id          The new includedVersion value
     * @param  newversion  The new includedVersion value
     * @return             Description of the Return Value
     */
    public boolean setIncludedVersion(String id, String newversion) {
        return creator.setIncludedVersion(this, id, newversion);
    }


    /**
     *  Description of the Method
     *
     * @param  id  Description of the Parameter
     * @return     Description of the Return Value
     */
    public boolean delIncludedPackage(String id) {
        return creator.delIncludedPackage(this, id);
    }


    /**
     *  Sets the item attribute of the Target object
     *
     * @param  name  The new item value
     * @param  item  The new item value
     * @return       Description of the Return Value
     */
    public boolean setItem(String name, Object item) {
        items.put(name, item);
        return true;
    }


    /**
     *  Gets the item attribute of the Target object
     *
     * @param  name  Description of the Parameter
     * @return       The item value
     */
    public Object getItem(String name) {
        Object o = items.get(name);
        return o;
    }


    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public boolean save() {
        // check if the dirs are created, if not create them
        String dirsp = basedir + path.substring(0, path.lastIndexOf(File.separator));
        File dirs = new File(dirsp);
        if (!dirs.exists()) {
            dirs.mkdirs();
        }
        String body = creator.getXMLFile(this);
        // write back to disk
        File sfile = new File(basedir + getPath());
        try {
            DataOutputStream scan = new DataOutputStream(new FileOutputStream(sfile));
            scan.writeBytes(body);
            scan.flush();
            scan.close();
        } catch (Exception e) {
	    log.error("Target can't save updated packaging file : "+basedir+getPath());
        }
        return true;
    }


    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public boolean hasSyntaxErrors() {
        // performs several syntax checks to signal
        // the users in the gui tools on possible problems
        if (getDescription().equals("")) {
            return true;
        }
        if (getMaintainer().equals("")) {
            return true;
        }
        if (getDescription().equals("")) {
            return true;
        }
        if (getRelatedPeople("initiators") == null || getRelatedPeople("initiators").size() == 0) {
            return true;
        }
        if (getRelatedPeople("developers") == null || getRelatedPeople("developers").size() == 0) {
            return true;
        }
        if (getRelatedPeople("contacts") == null || getRelatedPeople("contacts").size() == 0) {
            return true;
        }
        return false;
    }

    public String getId() {
        String id = getPackageName() + "@" + getMaintainer() + "_" + getType();
        id = id.replace(' ', '_');
        id = id.replace('/', '_');
	return id;
    }

    public void setPublishProvider(String publishprovider) {
	this.publishprovider = publishprovider;
    }

    public void setPublishState(boolean publishstate) {
	this.publishstate = publishstate;
    }

    public void setPublishSharePassword(String publishsharepassword) {
	this.publishsharepassword = publishsharepassword;
    }

    public boolean getPublishState() {
	return publishstate;
    }

    public String getPublishProvider() {
	return publishprovider;
    }

    public String getPublishSharePassword() {
	return publishsharepassword;
    }

    public boolean publish(int version,packageStep step) {
	// extra delay to test the background threader
	if (isBundle()) {
		BundleInterface b = BundleManager.getBundle(getId(),""+version);
		while (b==null) {
			// again this is done because it might take the provider
			// discovery system a few seconds before it detects the
			// new package even if we made it ourselfs.
			try {
				Thread.sleep(1000);
			} catch(Exception e) {
				e.printStackTrace();
			}
			b = BundleManager.getBundle(getId(),""+version);
		}
		ProviderInterface o=ProviderManager.getProvider(publishprovider);
		if (o != null && b !=null) {
			o.setPackageStep(step);
			boolean result = o.publish(getCreator(),b,getPublishSharePassword());
			return result;
		}
	} else if (!isBundle()) {
		PackageInterface p = PackageManager.getPackage(getId(),""+version);
		while (p==null) {
			// again this is done because it might take the provider
			// discovery system a few seconds before it detects the
			// new package even if we made it ourselfs.
			try {
				Thread.sleep(1000);
			} catch(Exception e) {
				e.printStackTrace();
			}
			p = PackageManager.getPackage(getId(),""+version);
		}
		ProviderInterface o=ProviderManager.getProvider(publishprovider);
		if (o != null && p !=null) {
			o.setPackageStep(step);
			boolean result = o.publish(getCreator(),p,getPublishSharePassword());
			return result;
		}
	}
	return false; 
    }

    public Project getParent() {
	return parent;
    }

    public void addRelatedTargetsCreate(Target t) {
	creator.addRelatedTargetsCreate(t);
    }

    public int getProgressBarValue() {
        return creator.getProgressBarValue();
    }

    public int getSubProgressBarValue() {
        return creator.getSubProgressBarValue();
    }
	
}

