/*
 *  This software is OSI Certified Open Source Software.
 *  OSI Certified is a certification mark of the Open Source Initiative.
 *  The license (Mozilla version 1.0) can be read at the MMBase site.
 *  See http://www.MMBase.org/license
 */
package org.mmbase.applications.packaging.packagehandlers;

import java.io.BufferedInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarFile;

import org.mmbase.applications.packaging.bundlehandlers.BundleInterface;
import org.mmbase.applications.packaging.installhandlers.installStep;
import org.mmbase.applications.packaging.providerhandlers.ProviderInterface;
import org.mmbase.applications.packaging.sharehandlers.ShareInfo;

/**
 * The package container, this is a class that might confuse you at first
 * its goal is to be a 'alias' for the 'best' available version of a package
 * and a access point to all other versions of the package it has access too.
 * This is the reason why it also implements the packageInterface to users
 * can really use it as if it was a package. The reason for also keeping track
 * of older or dubble versions of a package is that we can use this while
 * upgrading (generate diffs) or having multiple 'download' places for a package * when a disk is broken or a server is down.
 *
 * @author     Daniel Ockeloen (MMBased)
 */
public class PackageContainer implements PackageInterface {

    private ShareInfo shareinfo;

    private PackageInterface activePackage;

    private HashMap<String, PackageVersionContainer> versions = new HashMap<String, PackageVersionContainer>();


    /**
     *Constructor for the PackageContainer object
     *
     * @param  p  Description of the Parameter
     */
    public PackageContainer(PackageInterface p) {
        // its the first one so it has to be the best
        this.activePackage = p;

        // also the first version so add it
        PackageVersionContainer pvc = new PackageVersionContainer(p);
        versions.put(p.getVersion(), pvc);
    }


    /**
     *  Description of the Method
     *
     * @param  version   Description of the Parameter
     * @param  provider  Description of the Parameter
     * @return           Description of the Return Value
     */
    public boolean contains(String version, ProviderInterface provider) {
        PackageVersionContainer vc = versions.get(version);
        if (vc != null) {
            return (vc.contains(provider));
        }
        return (false);
    }


    /**
     *  Description of the Method
     *
     * @param  p  Description of the Parameter
     * @return    Description of the Return Value
     */
    public boolean removePackage(PackageInterface p) {
        versions.remove(p.getVersion());
        return true;
    }


    /**
     *  Gets the packageCount attribute of the PackageContainer object
     *
     * @return    The packageCount value
     */
    public int getPackageCount() {
        return versions.size();
    }


    /**
     *  Adds a feature to the Package attribute of the PackageContainer object
     *
     * @param  p  The feature to be added to the Package attribute
     * @return    Description of the Return Value
     */
    public boolean addPackage(PackageInterface p) {
        PackageVersionContainer vc = versions.get(p.getVersion());
        // we allready have this verion, so maybe its a different provider
        if (vc != null) {
            vc.addPackage(p);
        } else {
            PackageVersionContainer pvc = new PackageVersionContainer(p);
            versions.put(p.getVersion(), pvc);
        }

        // figure out if we have a new best version of this package
        try {
            int oldversion = Integer.parseInt(activePackage.getVersion());
            int newversion = Integer.parseInt(p.getVersion());
            if (newversion > oldversion) {
                // so we have a newer version, make that the active one
                activePackage = p;
            } else if (newversion == oldversion) {
                int oldbaseScore = activePackage.getProvider().getBaseScore();
                int newbaseScore = p.getProvider().getBaseScore();
                if (newbaseScore > oldbaseScore) {
                    activePackage = p;
                }
            }
        } catch (Exception e) {}
        ;

        return true;
    }


    /**
     *  Gets the relatedPeople attribute of the PackageContainer object
     *
     * @param  type  Description of the Parameter
     * @return       The relatedPeople value
     */
    public List<Object> getRelatedPeople(String type) {
        return activePackage.getRelatedPeople(type);
    }


    /**
     *  Gets the name attribute of the PackageContainer object
     *
     * @return    The name value
     */
    public String getName() {
        return activePackage.getName();
    }


    /**
     *  Gets the description attribute of the PackageContainer object
     *
     * @return    The description value
     */
    public String getDescription() {
        return activePackage.getDescription();
    }


    /**
     *  Gets the releaseNotes attribute of the PackageContainer object
     *
     * @return    The releaseNotes value
     */
    public String getReleaseNotes() {
        return activePackage.getReleaseNotes();
    }


    /**
     *  Gets the installationNotes attribute of the PackageContainer object
     *
     * @return    The installationNotes value
     */
    public String getInstallationNotes() {
        return activePackage.getInstallationNotes();
    }


    /**
     *  Gets the licenseType attribute of the PackageContainer object
     *
     * @return    The licenseType value
     */
    public String getLicenseType() {
        return activePackage.getLicenseType();
    }


    /**
     *  Gets the licenseName attribute of the PackageContainer object
     *
     * @return    The licenseName value
     */
    public String getLicenseName() {
        return activePackage.getLicenseName();
    }


    /**
     *  Gets the licenseVersion attribute of the PackageContainer object
     *
     * @return    The licenseVersion value
     */
    public String getLicenseVersion() {
        return activePackage.getLicenseVersion();
    }


    /**
     *  Gets the licenseBody attribute of the PackageContainer object
     *
     * @return    The licenseBody value
     */
    public String getLicenseBody() {
        return activePackage.getLicenseBody();
    }


    /**
     *  Gets the version attribute of the PackageContainer object
     *
     * @return    The version value
     */
    public String getVersion() {
        return activePackage.getVersion();
    }


    /**
     *  Gets the state attribute of the PackageContainer object
     *
     * @return    The state value
     */
    public String getState() {
        return activePackage.getState();
    }


    /**
     *  Sets the state attribute of the PackageContainer object
     *
     * @param  state  The new state value
     * @return        Description of the Return Value
     */
    public boolean setState(String state) {
        return activePackage.setState(state);
    }


    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public boolean install() {
        return activePackage.install();
    }


    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public boolean uninstall() {
        return activePackage.uninstall();
    }


    /**
     *  Description of the Method
     *
     * @param  step  Description of the Parameter
     * @return       Description of the Return Value
     */
    public boolean install(installStep step) {
        return activePackage.install(step);
    }


    /**
     *  Description of the Method
     *
     * @param  step  Description of the Parameter
     * @return       Description of the Return Value
     */
    public boolean uninstall(installStep step) {
        return activePackage.uninstall(step);
    }


    /**
     *  Gets the creationDate attribute of the PackageContainer object
     *
     * @return    The creationDate value
     */
    public String getCreationDate() {
        return activePackage.getCreationDate();
    }


    /**
     *  Gets the maintainer attribute of the PackageContainer object
     *
     * @return    The maintainer value
     */
    public String getMaintainer() {
        return activePackage.getMaintainer();
    }


    /**
     *  Gets the type attribute of the PackageContainer object
     *
     * @return    The type value
     */
    public String getType() {
        return activePackage.getType();
    }


    /**
     *  Gets the id attribute of the PackageContainer object
     *
     * @return    The id value
     */
    public String getId() {
        return activePackage.getId();
    }


    /**
     *  Gets the path attribute of the PackageContainer object
     *
     * @return    The path value
     */
    public String getPath() {
        return activePackage.getPath();
    }


    /**
     *  Gets the provider attribute of the PackageContainer object
     *
     * @return    The provider value
     */
    public ProviderInterface getProvider() {
        return activePackage.getProvider();
    }


    /**
     *  Gets the versions attribute of the PackageContainer object
     *
     * @return    The versions value
     */
    public Iterator<PackageVersionContainer> getVersions() {
        return ((HashMap<String, PackageVersionContainer>)versions.clone()).values().iterator();
    }


    /**
     *  Gets the versionNumbers attribute of the PackageContainer object
     *
     * @return    The versionNumbers value
     */
    public Iterator<String> getVersionNumbers() {
        ArrayList<String> list = new ArrayList<String>();
        // loop all versions and filter the uniq numbers
        Iterator<PackageVersionContainer> e = getVersions();
        while (e.hasNext()) {
            PackageVersionContainer pvc = e.next();
            String ver = pvc.getVersion();
            list.add(ver);
        }
        return list.iterator();
    }


    /**
     *  Gets the version attribute of the PackageContainer object
     *
     * @param  version   Description of the Parameter
     * @param  provider  Description of the Parameter
     * @return           The version value
     */
    public PackageInterface getVersion(String version, ProviderInterface provider) {
        PackageVersionContainer pvc = versions.get(version);
        if (pvc != null) {
            PackageInterface p = (PackageInterface) pvc.get(provider);
            if (p != null) {
                return p;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }


    /**
     *  Gets the packageByScore attribute of the PackageContainer object
     *
     * @param  version  Description of the Parameter
     * @return          The packageByScore value
     */
    public PackageInterface getPackageByScore(String version) {
        PackageVersionContainer pvc = versions.get(version);
        if (pvc != null) {
            return pvc.getPackageByScore();
        }
        return null;
    }


    /**
     *  Gets the installSteps attribute of the PackageContainer object
     *
     * @return    The installSteps value
     */
    public Iterator<installStep> getInstallSteps() {
        return activePackage.getInstallSteps();
    }


    /**
     *  Gets the installSteps attribute of the PackageContainer object
     *
     * @param  logid  Description of the Parameter
     * @return        The installSteps value
     */
    public Iterator<installStep> getInstallSteps(int logid) {
        return activePackage.getInstallSteps(logid);
    }


    /**
     *  Description of the Method
     */
    public void clearInstallSteps() {
        activePackage.clearInstallSteps();
    }


    /**
     *  Gets the jarFile attribute of the PackageContainer object
     *
     * @return    The jarFile value
     */
    public JarFile getJarFile() {
        return activePackage.getJarFile();
    }


    /**
     *  Gets the jarStream attribute of the PackageContainer object
     *
     * @return    The jarStream value
     */
    public BufferedInputStream getJarStream() {
        return activePackage.getJarStream();
    }


    /**
     *  Gets the parentBundle attribute of the PackageContainer object
     *
     * @return    The parentBundle value
     */
    public BundleInterface getParentBundle() {
        return activePackage.getParentBundle();
    }


    /**
     *  Sets the parentBundle attribute of the PackageContainer object
     *
     * @param  parentbundle  The new parentBundle value
     */
    public void setParentBundle(BundleInterface parentbundle) {
        activePackage.setParentBundle(parentbundle);
    }


    /**
     *  Gets the shared attribute of the PackageContainer object
     *
     * @return    The shared value
     */
    public boolean isShared() {
        if (shareinfo != null) {
            return (true);
        }
        return false;
    }


    /**
     *  Gets the shareInfo attribute of the PackageContainer object
     *
     * @return    The shareInfo value
     */
    public ShareInfo getShareInfo() {
        return shareinfo;
    }


    /**
     *  Sets the shareInfo attribute of the PackageContainer object
     *
     * @param  shareinfo  The new shareInfo value
     */
    public void setShareInfo(ShareInfo shareinfo) {
        this.shareinfo = shareinfo;
    }


    /**
     *  Description of the Method
     */
    public void removeShare() {
        this.shareinfo = null;
    }


    /**
     *  Gets the dependsFailed attribute of the PackageContainer object
     *
     * @return    The dependsFailed value
     */
    public boolean getDependsFailed() {
        return activePackage.getDependsFailed();
    }


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
    public void init(org.w3c.dom.Node n, ProviderInterface provider, String name, String type, String maintainer, String version, String date, String path) {
        activePackage.init(n, provider, name, type, maintainer, version, date, path);
    }


    /**
     *  Sets the progressBar attribute of the PackageContainer object
     *
     * @param  stepcount  The new progressBar value
     */
    public void setProgressBar(int stepcount) {
        activePackage.setProgressBar(stepcount);
    }


    /**
     *  Description of the Method
     */
    public void increaseProgressBar() {
        activePackage.increaseProgressBar();
    }


    /**
     *  Description of the Method
     *
     * @param  stepcount  Description of the Parameter
     */
    public void increaseProgressBar(int stepcount) {
        activePackage.increaseProgressBar(stepcount);
    }


    /**
     *  Gets the progressBarValue attribute of the PackageContainer object
     *
     * @return    The progressBarValue value
     */
    public int getProgressBarValue() {
        return activePackage.getProgressBarValue();
    }


    public long lastSeen() {
        return activePackage.lastSeen();
    }

}

