/*
 *  This software is OSI Certified Open Source Software.
 *  OSI Certified is a certification mark of the Open Source Initiative.
 *  The license (Mozilla version 1.0) can be read at the MMBase site.
 *  See http://www.MMBase.org/license
 */
package org.mmbase.applications.packaging.packagehandlers;

import java.io.BufferedInputStream;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarFile;

import org.mmbase.applications.packaging.bundlehandlers.BundleInterface;
import org.mmbase.applications.packaging.installhandlers.installStep;
import org.mmbase.applications.packaging.providerhandlers.ProviderInterface;

/**
 * Interface for all the package handlers
 *
 * @author     danielockeloen
 */
public interface PackageInterface {
    /**
     *  Description of the Method
     *
     * @param  n           Description of the Parameter
     * @param  provider    Description of the Parameter
     * @param  name        Description of the Parameter
     * @param  type        Description of the Parameter
     * @param  maintainer  Description of the Parameter
     * @param  version     Description of the Parameter
     * @param  date        Description of the Parameter
     * @param  path        Description of the Parameter
     */
    public void init(org.w3c.dom.Node n, ProviderInterface provider, String name, String type, String maintainer, String version, String date, String path);


    /**
     *  Gets the id attribute of the PackageInterface object
     *
     * @return    The id value
     */
    public String getId();


    /**
     *  Gets the name attribute of the PackageInterface object
     *
     * @return    The name value
     */
    public String getName();


    /**
     *  Gets the type attribute of the PackageInterface object
     *
     * @return    The type value
     */
    public String getType();


    /**
     *  Gets the version attribute of the PackageInterface object
     *
     * @return    The version value
     */
    public String getVersion();


    /**
     *  Gets the creationDate attribute of the PackageInterface object
     *
     * @return    The creationDate value
     */
    public String getCreationDate();


    /**
     *  Gets the maintainer attribute of the PackageInterface object
     *
     * @return    The maintainer value
     */
    public String getMaintainer();


    /**
     *  Gets the state attribute of the PackageInterface object
     *
     * @return    The state value
     */
    public String getState();


    /**
     *  Gets the description attribute of the PackageInterface object
     *
     * @return    The description value
     */
    public String getDescription();


    /**
     *  Gets the installationNotes attribute of the PackageInterface object
     *
     * @return    The installationNotes value
     */
    public String getInstallationNotes();


    /**
     *  Gets the releaseNotes attribute of the PackageInterface object
     *
     * @return    The releaseNotes value
     */
    public String getReleaseNotes();


    /**
     *  Gets the licenseType attribute of the PackageInterface object
     *
     * @return    The licenseType value
     */
    public String getLicenseType();


    /**
     *  Gets the licenseName attribute of the PackageInterface object
     *
     * @return    The licenseName value
     */
    public String getLicenseName();


    /**
     *  Gets the licenseVersion attribute of the PackageInterface object
     *
     * @return    The licenseVersion value
     */
    public String getLicenseVersion();


    /**
     *  Gets the licenseBody attribute of the PackageInterface object
     *
     * @return    The licenseBody value
     */
    public String getLicenseBody();


    /**
     *  Gets the relatedPeople attribute of the PackageInterface object
     *
     * @param  type  Description of the Parameter
     * @return       The relatedPeople value
     */
    public List<Object> getRelatedPeople(String type);


    /**
     *  Gets the parentBundle attribute of the PackageInterface object
     *
     * @return    The parentBundle value
     */
    public BundleInterface getParentBundle();


    /**
     *  Sets the parentBundle attribute of the PackageInterface object
     *
     * @param  parent  The new parentBundle value
     */
    public void setParentBundle(BundleInterface parent);


    /**
     *  Sets the state attribute of the PackageInterface object
     *
     * @param  state  The new state value
     * @return        Description of the Return Value
     */
    public boolean setState(String state);


    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public boolean install();


    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public boolean uninstall();


    /**
     *  Description of the Method
     *
     * @param  step  Description of the Parameter
     * @return       Description of the Return Value
     */
    public boolean install(installStep step);


    /**
     *  Description of the Method
     *
     * @param  step  Description of the Parameter
     * @return       Description of the Return Value
     */
    public boolean uninstall(installStep step);


    /**
     *  Gets the provider attribute of the PackageInterface object
     *
     * @return    The provider value
     */
    public ProviderInterface getProvider();


    /**
     *  Gets the installSteps attribute of the PackageInterface object
     *
     * @return    The installSteps value
     */
    public Iterator<installStep> getInstallSteps();


    /**
     *  Gets the installSteps attribute of the PackageInterface object
     *
     * @param  logid  Description of the Parameter
     * @return        The installSteps value
     */
    public Iterator<installStep> getInstallSteps(int logid);


    /**
     *  Description of the Method
     */
    public void clearInstallSteps();


    /**
     *  Gets the jarFile attribute of the PackageInterface object
     *
     * @return    The jarFile value
     */
    public JarFile getJarFile();


    /**
     *  Gets the jarStream attribute of the PackageInterface object
     *
     * @return    The jarStream value
     */
    public BufferedInputStream getJarStream();


    /**
     *  Gets the path attribute of the PackageInterface object
     *
     * @return    The path value
     */
    public String getPath();


    /**
     *  Gets the dependsFailed attribute of the PackageInterface object
     *
     * @return    The dependsFailed value
     */
    public boolean getDependsFailed();


    /**
     *  Gets the progressBarValue attribute of the PackageInterface object
     *
     * @return    The progressBarValue value
     */
    public int getProgressBarValue();


    /**
     *  Sets the progressBar attribute of the PackageInterface object
     *
     * @param  stepcount  The new progressBar value
     */
    public void setProgressBar(int stepcount);


    /**
     *  Description of the Method
     */
    public void increaseProgressBar();


    /**
     *  Description of the Method
     *
     * @param  stepcount  Description of the Parameter
     */
    public void increaseProgressBar(int stepcount);


    public long lastSeen();

}

