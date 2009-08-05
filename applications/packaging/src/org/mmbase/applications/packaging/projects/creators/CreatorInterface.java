/*
 *  This software is OSI Certified Open Source Software.
 *  OSI Certified is a certification mark of the Open Source Initiative.
 *  The license (Mozilla version 1.0) can be read at the MMBase site.
 *  See http://www.MMBase.org/license
 */
package org.mmbase.applications.packaging.projects.creators;

import java.util.ArrayList;
import java.util.Iterator;

import org.mmbase.applications.packaging.Person;
import org.mmbase.applications.packaging.projects.*;

/**
 * Interface for all the creators
 *
 * @author     danielockeloen
 */
public interface CreatorInterface {
    /**
     *  Gets the type attribute of the CreatorInterface object
     *
     * @return    The type value
     */
    public String getType();

    public String getState();

    /**
     *  Sets the type attribute of the CreatorInterface object
     *
     * @param  type  The new type value
     */
    public void setType(String type);


    /**
     *  Gets the lastVersion attribute of the CreatorInterface object
     *
     * @param  target  Description of the Parameter
     * @return         The lastVersion value
     */
    public int getLastVersion(Target target);


    /**
     *  Gets the nextVersion attribute of the CreatorInterface object
     *
     * @param  target  Description of the Parameter
     * @return         The nextVersion value
     */
    public int getNextVersion(Target target);


    /**
     *  Gets the lastDate attribute of the CreatorInterface object
     *
     * @param  target  Description of the Parameter
     * @return         The lastDate value
     */
    public String getLastDate(Target target);


    /**
     *  Description of the Method
     *
     * @param  target      Description of the Parameter
     * @param  newversion  Description of the Parameter
     * @return             Description of the Return Value
     */
    public boolean createPackage(Target target, int newversion);

    public void createPackageThreaded(Target target, int newversion);

    /**
     *  Gets the packageSteps attribute of the CreatorInterface object
     *
     * @return    The packageSteps value
     */
    public Iterator<packageStep> getPackageSteps();


    /**
     *  Gets the packageSteps attribute of the CreatorInterface object
     *
     * @param  logid  Description of the Parameter
     * @return        The packageSteps value
     */
    public Iterator<packageStep> getPackageSteps(int logid);


    /**
     *  Description of the Method
     */
    public void clearPackageSteps();


    /**
     *  Gets the maintainer attribute of the CreatorInterface object
     *
     * @param  target  Description of the Parameter
     * @return         The maintainer value
     */
    public String getMaintainer(Target target);


    /**
     *  Gets the description attribute of the CreatorInterface object
     *
     * @param  target  Description of the Parameter
     * @return         The description value
     */
    public String getDescription(Target target);


    /**
     *  Gets the name attribute of the CreatorInterface object
     *
     * @param  target  Description of the Parameter
     * @return         The name value
     */
    public String getName(Target target);


    /**
     *  Gets the licenseType attribute of the CreatorInterface object
     *
     * @param  target  Description of the Parameter
     * @return         The licenseType value
     */
    public String getLicenseType(Target target);


    /**
     *  Gets the licenseVersion attribute of the CreatorInterface object
     *
     * @param  target  Description of the Parameter
     * @return         The licenseVersion value
     */
    public String getLicenseVersion(Target target);


    /**
     *  Gets the licenseName attribute of the CreatorInterface object
     *
     * @param  target  Description of the Parameter
     * @return         The licenseName value
     */
    public String getLicenseName(Target target);


    /**
     *  Sets the description attribute of the CreatorInterface object
     *
     * @param  target          The new description value
     * @param  newdescription  The new description value
     * @return                 Description of the Return Value
     */
    public boolean setDescription(Target target, String newdescription);


    /**
     *  Sets the licenseType attribute of the CreatorInterface object
     *
     * @param  target          The new licenseType value
     * @param  newlicensetype  The new licenseType value
     * @return                 Description of the Return Value
     */
    public boolean setLicenseType(Target target, String newlicensetype);


    /**
     *  Sets the licenseName attribute of the CreatorInterface object
     *
     * @param  target          The new licenseName value
     * @param  newlicensename  The new licenseName value
     * @return                 Description of the Return Value
     */
    public boolean setLicenseName(Target target, String newlicensename);


    /**
     *  Sets the name attribute of the CreatorInterface object
     *
     * @param  target   The new name value
     * @param  newname  The new name value
     * @return          Description of the Return Value
     */
    public boolean setName(Target target, String newname);


    /**
     *  Sets the maintainer attribute of the CreatorInterface object
     *
     * @param  target         The new maintainer value
     * @param  newmaintainer  The new maintainer value
     * @return                Description of the Return Value
     */
    public boolean setMaintainer(Target target, String newmaintainer);


    /**
     *  Sets the licenseVersion attribute of the CreatorInterface object
     *
     * @param  target             The new licenseVersion value
     * @param  newlicenseversion  The new licenseVersion value
     * @return                    Description of the Return Value
     */
    public boolean setLicenseVersion(Target target, String newlicenseversion);


    /**
     *  Gets the relatedPeople attribute of the CreatorInterface object
     *
     * @param  type    Description of the Parameter
     * @param  target  Description of the Parameter
     * @return         The relatedPeople value
     */
    public ArrayList<Person> getRelatedPeople(String type, Target target);


    /**
     *  Gets the releaseNotes attribute of the CreatorInterface object
     *
     * @param  target  Description of the Parameter
     * @return         The releaseNotes value
     */
    public String getReleaseNotes(Target target);


    /**
     *  Gets the installationNotes attribute of the CreatorInterface object
     *
     * @param  target  Description of the Parameter
     * @return         The installationNotes value
     */
    public String getInstallationNotes(Target target);


    /**
     *  Gets the xMLFile attribute of the CreatorInterface object
     *
     * @param  target  Description of the Parameter
     * @return         The xMLFile value
     */
    public String getXMLFile(Target target);


    /**
     *  Gets the includedPackages attribute of the CreatorInterface object
     *
     * @param  target  Description of the Parameter
     * @return         The includedPackages value
     */
    public ArrayList<IncludedPackage> getIncludedPackages(Target target);


    /**
     *  Sets the includedVersion attribute of the CreatorInterface object
     *
     * @param  target      The new includedVersion value
     * @param  id          The new includedVersion value
     * @param  newversion  The new includedVersion value
     * @return             Description of the Return Value
     */
    public boolean setIncludedVersion(Target target, String id, String newversion);


    /**
     *  Description of the Method
     *
     * @param  target  Description of the Parameter
     * @param  id      Description of the Parameter
     * @return         Description of the Return Value
     */
    public boolean delIncludedPackage(Target target, String id);


    /**
     *  Description of the Method
     *
     * @param  target  Description of the Parameter
     * @return         Description of the Return Value
     */
    public boolean decodeItems(Target target);


    /**
     *  Adds a feature to the Package attribute of the CreatorInterface object
     *
     * @param  target      The feature to be added to the Package attribute
     * @param  newpackage  The feature to be added to the Package attribute
     * @return             Description of the Return Value
     */
    public boolean addPackage(Target target, String newpackage);


    /**
     *  Adds a feature to the PackageInitiator attribute of the CreatorInterface object
     *
     * @param  target      The feature to be added to the PackageInitiator attribute
     * @param  newname     The feature to be added to the PackageInitiator attribute
     * @param  newcompany  The feature to be added to the PackageInitiator attribute
     * @return             Description of the Return Value
     */
    public boolean addPackageInitiator(Target target, String newname, String newcompany);


    /**
     *  Description of the Method
     *
     * @param  target      Description of the Parameter
     * @param  oldname     Description of the Parameter
     * @param  oldcompany  Description of the Parameter
     * @return             Description of the Return Value
     */
    public boolean delPackageInitiator(Target target, String oldname, String oldcompany);


    /**
     *  Sets the packageInitiator attribute of the CreatorInterface object
     *
     * @param  target      The new packageInitiator value
     * @param  oldname     The new packageInitiator value
     * @param  newname     The new packageInitiator value
     * @param  oldcompany  The new packageInitiator value
     * @param  newcompany  The new packageInitiator value
     * @return             Description of the Return Value
     */
    public boolean setPackageInitiator(Target target, String oldname, String newname, String oldcompany, String newcompany);


    /**
     *  Adds a feature to the PackageDeveloper attribute of the CreatorInterface object
     *
     * @param  target      The feature to be added to the PackageDeveloper attribute
     * @param  newname     The feature to be added to the PackageDeveloper attribute
     * @param  newcompany  The feature to be added to the PackageDeveloper attribute
     * @return             Description of the Return Value
     */
    public boolean addPackageDeveloper(Target target, String newname, String newcompany);


    /**
     *  Description of the Method
     *
     * @param  target      Description of the Parameter
     * @param  oldname     Description of the Parameter
     * @param  oldcompany  Description of the Parameter
     * @return             Description of the Return Value
     */
    public boolean delPackageDeveloper(Target target, String oldname, String oldcompany);


    /**
     *  Sets the packageDeveloper attribute of the CreatorInterface object
     *
     * @param  target      The new packageDeveloper value
     * @param  oldname     The new packageDeveloper value
     * @param  newname     The new packageDeveloper value
     * @param  oldcompany  The new packageDeveloper value
     * @param  newcompany  The new packageDeveloper value
     * @return             Description of the Return Value
     */
    public boolean setPackageDeveloper(Target target, String oldname, String newname, String oldcompany, String newcompany);


    /**
     *  Adds a feature to the PackageSupporter attribute of the CreatorInterface object
     *
     * @param  target      The feature to be added to the PackageSupporter attribute
     * @param  newcompany  The feature to be added to the PackageSupporter attribute
     * @return             Description of the Return Value
     */
    public boolean addPackageSupporter(Target target, String newcompany);


    /**
     *  Description of the Method
     *
     * @param  target      Description of the Parameter
     * @param  oldcompany  Description of the Parameter
     * @return             Description of the Return Value
     */
    public boolean delPackageSupporter(Target target, String oldcompany);


    /**
     *  Sets the packageSupporter attribute of the CreatorInterface object
     *
     * @param  target      The new packageSupporter value
     * @param  oldcompany  The new packageSupporter value
     * @param  newcompany  The new packageSupporter value
     * @return             Description of the Return Value
     */
    public boolean setPackageSupporter(Target target, String oldcompany, String newcompany);


    /**
     *  Adds a feature to the PackageContact attribute of the CreatorInterface object
     *
     * @param  target     The feature to be added to the PackageContact attribute
     * @param  newreason  The feature to be added to the PackageContact attribute
     * @param  newname    The feature to be added to the PackageContact attribute
     * @param  newemail   The feature to be added to the PackageContact attribute
     * @return            Description of the Return Value
     */
    public boolean addPackageContact(Target target, String newreason, String newname, String newemail);


    /**
     *  Description of the Method
     *
     * @param  target     Description of the Parameter
     * @param  oldreason  Description of the Parameter
     * @param  oldname    Description of the Parameter
     * @param  oldemail   Description of the Parameter
     * @return            Description of the Return Value
     */
    public boolean delPackageContact(Target target, String oldreason, String oldname, String oldemail);


    /**
     *  Sets the packageContact attribute of the CreatorInterface object
     *
     * @param  target     The new packageContact value
     * @param  oldreason  The new packageContact value
     * @param  newreason  The new packageContact value
     * @param  oldname    The new packageContact value
     * @param  newname    The new packageContact value
     * @param  oldemail   The new packageContact value
     * @param  newemail   The new packageContact value
     * @return            Description of the Return Value
     */
    public boolean setPackageContact(Target target, String oldreason, String newreason, String oldname, String newname, String oldemail, String newemail);


    /**
     *  Adds a feature to the PackageDepends attribute of the CreatorInterface object
     *
     * @param  target     The feature to be added to the PackageDepends attribute
     * @param  packageid  The feature to be added to the PackageDepends attribute
     * @param  version    The feature to be added to the PackageDepends attribute
     * @return            Description of the Return Value
     */
    public boolean addPackageDepends(Target target, String packageid, String version);


    /**
     *  Description of the Method
     *
     * @param  target       Description of the Parameter
     * @param  packageid    Description of the Parameter
     * @param  version      Description of the Parameter
     * @param  versionmode  Description of the Parameter
     * @return              Description of the Return Value
     */
    public boolean delPackageDepends(Target target, String packageid, String version, String versionmode);


    /**
     *  Sets the packageDepends attribute of the CreatorInterface object
     *
     * @param  target          The new packageDepends value
     * @param  packageid       The new packageDepends value
     * @param  oldversion      The new packageDepends value
     * @param  oldversionmode  The new packageDepends value
     * @param  newversion      The new packageDepends value
     * @param  newversionmode  The new packageDepends value
     * @return                 Description of the Return Value
     */
    public boolean setPackageDepends(Target target, String packageid, String oldversion, String oldversionmode, String newversion, String newversionmode);


    /**
     *  Gets the packageDepends attribute of the CreatorInterface object
     *
     * @param  target  Description of the Parameter
     * @return         The packageDepends value
     */
    public ArrayList<PackageDepend> getPackageDepends(Target target);


    /**
     *  Sets the defaults attribute of the CreatorInterface object
     *
     * @param  target  The new defaults value
     */
    public void setDefaults(Target target);

    public String getDefaultTargetName();

    public void addRelatedTargetsCreate(Target t);

    public int getProgressBarValue();

    public int getSubProgressBarValue();
}

