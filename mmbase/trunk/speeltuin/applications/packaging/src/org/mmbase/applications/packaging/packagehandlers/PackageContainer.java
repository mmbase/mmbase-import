/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.applications.packaging.packagehandlers;

import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;
import org.mmbase.util.*;
import org.mmbase.module.builders.Versions;
import org.mmbase.applications.packaging.*;
import org.mmbase.applications.packaging.packagehandlers.*;
import org.mmbase.applications.packaging.sharehandlers.*;
import org.mmbase.applications.packaging.providerhandlers.*;
import org.mmbase.applications.packaging.bundlehandlers.*;
import org.mmbase.applications.packaging.installhandlers.*;

import java.io.*;
import java.util.*;
import java.util.jar.*;

import org.w3c.dom.*;

/**
 * The package container, this is a class that might confuse you at first
 * its goal is to be a 'alias' for the 'best' available version of a package
 * and a access point to all other versions of the package it has access too.
 * This is the reason why it also implements the packageInterface to users
 * can really use it as if it was a package. The reason for also keeping track
 * of older or dubble versions of a package is that we can use this while
 * upgrading (generate diffs) or having multiple 'download' places for a package * when a disk is broken or a server is down.
 *
 * @author Daniel Ockeloen (MMBased)
 */
public class PackageContainer implements PackageInterface {
    private static Logger log = Logging.getLoggerInstance(PackageContainer.class.getName());

    private ShareInfo shareinfo;

    private PackageInterface activePackage;

    private Hashtable versions=new Hashtable();
    
    public PackageContainer(PackageInterface p) {
	// its the first one so it has to be the best
	this.activePackage=p;

	// also the first version so add it 
	PackageVersionContainer pvc=new PackageVersionContainer(p);
	versions.put(p.getVersion(),pvc);
    }


    public boolean contains(String version,ProviderInterface provider) {
	PackageVersionContainer vc=(PackageVersionContainer)versions.get(version);
	if (vc!=null) {
		return(vc.contains(provider));
	}
	return(false);
    }


    public boolean removePackage(PackageInterface p) {
	versions.remove(p.getVersion());
	return true;
    }

    public int getPackageCount() {
	return versions.size();
    }

    public boolean addPackage(PackageInterface p) {
	PackageVersionContainer vc=(PackageVersionContainer)versions.get(p.getVersion());
	// we allready have this verion, so maybe its a different provider
	if (vc!=null) {
		vc.addPackage(p);
	} else {
		PackageVersionContainer pvc=new PackageVersionContainer(p);
		versions.put(p.getVersion(),pvc);
	}

	// figure out if we have a new best version of this package
	try {
		int oldversion=Integer.parseInt(activePackage.getVersion());
		int newversion=Integer.parseInt(p.getVersion());
		if (newversion>oldversion) {
			// so we have a newer version, make that the active one
			activePackage=p;
		} else if (newversion==oldversion) {
			int oldbaseScore=activePackage.getProvider().getBaseScore();
			int newbaseScore=p.getProvider().getBaseScore();
			if (newbaseScore>oldbaseScore) {
				activePackage=p;
			}
		}
	} catch(Exception e) {};

	return true;
    }

    public List getRelatedPeople(String type) {
        return activePackage.getRelatedPeople(type);
    }

    public String getName() {
	return activePackage.getName();
    }

    public String getDescription() {
	return activePackage.getDescription();
    }
    
    public String getReleaseNotes() {
	return activePackage.getReleaseNotes();
    }

    public String getInstallationNotes() {
	return activePackage.getInstallationNotes();
    }


    public String getLicenseType() {
	return activePackage.getLicenseType();
    }

    public String getLicenseName() {
	return activePackage.getLicenseName();
    }

    public String getLicenseVersion() {
	return activePackage.getLicenseVersion();
    }

    public String getLicenseBody() {
	return activePackage.getLicenseBody();
    }

    public String getVersion() {
	return activePackage.getVersion();
    }

    public String getState() {
	return activePackage.getState();
    }

    public boolean setState(String state) {
	return activePackage.setState(state);
    }

    public boolean install() {
	return activePackage.install();
    }

    public boolean uninstall() {
	return activePackage.uninstall();
    }

    public boolean install(installStep step) {
	return activePackage.install(step);
    }

    public boolean uninstall(installStep step) {
	return activePackage.uninstall(step);
    }

    public String getCreationDate() {
	return activePackage.getCreationDate();
    }

    public String getMaintainer() {
	return activePackage.getMaintainer();
    }

    public String getType() {
	return activePackage.getType();
    }

    public String getId() {
	return activePackage.getId();
    }

    public String getPath() {
	return activePackage.getPath();
    }

    public ProviderInterface getProvider() {	
	return activePackage.getProvider();
    }

    public Enumeration getVersions() {
	return versions.elements();
    }

    public Enumeration getVersionNumbers() {
	Vector list=new Vector();
	// loop all versions and filter the uniq numbers
	Enumeration e=getVersions();
	while (e.hasMoreElements()) {
		PackageVersionContainer pvc=(PackageVersionContainer)e.nextElement();
		String ver=pvc.getVersion();
		list.add(ver);		
	}
	return list.elements();
    }

    public PackageInterface getVersion(String version,ProviderInterface provider) {
	PackageVersionContainer pvc=(PackageVersionContainer)versions.get(version);
	if (pvc!=null) {
		PackageInterface p=(PackageInterface)pvc.get(provider);
		if (p!=null) {
			return p;
		} else {
			return null;
		}
	} else {
		return null;
	}
    }


    public PackageInterface getPackageByScore(String version) {
	PackageVersionContainer pvc=(PackageVersionContainer)versions.get(version);
	if (pvc!=null) {
		return pvc.getPackageByScore();
	}
	return null;
    }

    public Enumeration getInstallSteps() {
	return activePackage.getInstallSteps();
    }

    public Enumeration getInstallSteps(int logid) {
	return activePackage.getInstallSteps(logid);
    }

    public void clearInstallSteps() {
	activePackage.clearInstallSteps();
    }

    public JarFile getJarFile() {	
	return activePackage.getJarFile();
    }

    public BufferedInputStream getJarStream() {	
	return activePackage.getJarStream();
    }


    public BundleInterface getParentBundle() {
	return activePackage.getParentBundle();
    }

    public void setParentBundle(BundleInterface parentbundle) {
	activePackage.setParentBundle(parentbundle);
    }

    public boolean isShared() {	
	if (shareinfo!=null) {
		return(true);
	}
	return false;
    }

    public ShareInfo getShareInfo() {	
	return shareinfo;
    }

    public void setShareInfo(ShareInfo shareinfo) {
	this.shareinfo=shareinfo;
    }

    public void removeShare() {
	this.shareinfo=null;
    }

  public boolean getDependsFailed() { 
	return activePackage.getDependsFailed();
  }


  public void init(org.w3c.dom.Node n,ProviderInterface provider,String name,String type,String maintainer,String version, String date,String path) {
	activePackage.init(n,provider,name,type,maintainer,version,date,path);
  }

}
