/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.applications.packaging.bundlehandlers;

import java.io.BufferedInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarFile;

import org.mmbase.applications.packaging.installhandlers.installStep;
import org.mmbase.applications.packaging.providerhandlers.ProviderInterface;
import org.mmbase.applications.packaging.sharehandlers.ShareInfo;

/**
 * The bundle container, this is a class that might confuse you at first
 * its goal is to be a 'alias' for the 'best' available version of a bundle
 * and a access point to all other versions of the bundle it has access too.
 * This is the reason why it also implements the BundleInterface to users
 * can really use it as if it was a bundle. The reason for also keeping track
 * of older or dubble versions of a bundle is that we can use this while
 * upgrading (generate diffs) or having multiple 'download' places for a bundle
 * when a disk is broken or a server is down.
 *
 * @author Daniel Ockeloen (MMBased)
 */
public class BundleContainer implements BundleInterface {

    private ShareInfo shareinfo;

    private BundleInterface activeBundle;

    private HashMap<String, BundleVersionContainer> versions = new HashMap<String, BundleVersionContainer>();
    
    public BundleContainer(BundleInterface b) {
        // its the first one so it has to be the best
        this.activeBundle = b;

        // also the first version so add it 
        BundleVersionContainer bvc=new BundleVersionContainer(b);
        versions.put(b.getVersion(),bvc);
    }


    public boolean contains(String version,ProviderInterface provider) {
        BundleVersionContainer vc=versions.get(version);
        if (vc!=null) {
            return vc.contains(provider);
        }
        return false;
    }


    public boolean removeBundle(BundleInterface b) {
        versions.remove(b.getVersion());
        return true;
    }

    public int getBundleCount() {
        return versions.size();
    }

    public boolean addBundle(BundleInterface b) {
        BundleVersionContainer vc = versions.get(b.getVersion());
        // we allready have this verion, so maybe its a different provider
        if (vc != null) {
            vc.addBundle(b);
        } else {
            BundleVersionContainer bvc = new BundleVersionContainer(b);
            versions.put(b.getVersion(),bvc);
        }

        // figure out if we have a new best version of this bundle
        try {
            int oldversion = Integer.parseInt(activeBundle.getVersion());
            int newversion = Integer.parseInt(b.getVersion());
            if (newversion > oldversion) {
                // so we have a newer version, make that the active one
                activeBundle = b;
            } else if (newversion == oldversion) {
                int oldbaseScore = activeBundle.getProvider().getBaseScore();
                int newbaseScore = b.getProvider().getBaseScore();
                if (newbaseScore > oldbaseScore) {
                    activeBundle = b;
                }
            }
        } catch(Exception e) {};
        return true;
    }

    public Iterator<HashMap<String, String>> getNeededPackages() {
        return activeBundle.getNeededPackages();
    }

    public List<Object> getRelatedPeople(String type) {
        return activeBundle.getRelatedPeople(type);
    }

    public List<Object> getScreenshots() {
        return activeBundle.getScreenshots();
    }

    public List<Object> getStarturls() {
        return activeBundle.getStarturls();
    }

    public String getName() {
        return activeBundle.getName();
    }
    
    public String getVersion() {
        return activeBundle.getVersion();
    }

    public String getState() {
        return activeBundle.getState();
    }

    public boolean setState(String state) {
        return activeBundle.setState(state);
    }

    public boolean install() {
        return activeBundle.install();
    }

    public boolean uninstall() {
        return activeBundle.uninstall();
    }

    public String getCreationDate() {
        return activeBundle.getCreationDate();
    }

    public String getMaintainer() {
        return activeBundle.getMaintainer();
    }

    public String getType() {
        return activeBundle.getType();
    }

    public String getId() {
        return activeBundle.getId();
    }

    public String getPath() {
        return activeBundle.getPath();
    }

    public ProviderInterface getProvider() {    
        return activeBundle.getProvider();
    }

    public Iterator<BundleVersionContainer> getVersions() {
        return ((HashMap<String, BundleVersionContainer>)versions.clone()).values().iterator();
    }

    public BundleInterface getVersion(String version,ProviderInterface provider) {
        BundleVersionContainer bvc = versions.get(version);
        if (bvc != null) {
            BundleInterface b = (BundleInterface)bvc.get(provider);
            if (b != null) {
                return b;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }


    public BundleInterface getBundleByScore(String version) {
        BundleVersionContainer bvc=versions.get(version);
        if (bvc!=null) {
            return bvc.getBundleByScore();
        }
        return null;
    }

    public Iterator<installStep> getInstallSteps() {
        return activeBundle.getInstallSteps();
    }

    public Iterator<installStep> getInstallSteps(int logid) {
        return activeBundle.getInstallSteps(logid);
    }

    public void clearInstallSteps() {
         activeBundle.clearInstallSteps();
    }

    public JarFile getJarFile() {    
        return activeBundle.getJarFile();
    }

    public JarFile getIncludedPackageJarFile(String packageid,String packageversion) {    
        return activeBundle.getIncludedPackageJarFile(packageid,packageversion);
    }

    public BufferedInputStream getJarStream() {    
        return activeBundle.getJarStream();
    }

    public boolean isShared() {    
        if (shareinfo != null) {
            return true;
        }
        return false;
    }

    public ShareInfo getShareInfo() {    
        return shareinfo;
    }

    public void setShareInfo(ShareInfo shareinfo) {
        this.shareinfo = shareinfo;
    }

    public void removeShare() {
        this.shareinfo = null;
    }


    public String getDescription() {
        return activeBundle.getDescription();
    }
    
    public String getReleaseNotes() {
        return activeBundle.getReleaseNotes();
    }

    public String getInstallationNotes() {
        return activeBundle.getInstallationNotes();
    }


    public String getLicenseType() {
        return activeBundle.getLicenseType();
    }

    public String getLicenseName() {
        return activeBundle.getLicenseName();
    }

    public String getLicenseVersion() {
        return activeBundle.getLicenseVersion();
    }

    public String getLicenseBody() {
        return activeBundle.getLicenseBody();
    }

    public void setProgressBar(int stepcount) {
        activeBundle.setProgressBar(stepcount);
    }

    public void increaseProgressBar() {
        activeBundle.increaseProgressBar();
    }

    public void increaseProgressBar(int stepcount) {
        activeBundle.increaseProgressBar(stepcount);
    }

   public int getProgressBarValue() {
       return activeBundle.getProgressBarValue();
   }

   public int getPackageProgressBarValue() {
       return activeBundle.getPackageProgressBarValue();
   }

    public long lastSeen() {
    
        return activeBundle.lastSeen();
    }

}
