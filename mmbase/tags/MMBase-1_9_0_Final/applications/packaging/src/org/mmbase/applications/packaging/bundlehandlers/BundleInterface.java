/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.applications.packaging.bundlehandlers;

import java.io.BufferedInputStream;
import java.util.*;
import java.util.jar.JarFile;

import org.mmbase.applications.packaging.installhandlers.installStep;
import org.mmbase.applications.packaging.providerhandlers.ProviderInterface;

/**
 * Interface for all the bundle handlers
 */
public interface BundleInterface {

	public String getId();
	public String getName();
	public String getType();
	public String getVersion();
	public String getCreationDate();
	public String getMaintainer();
	public String getState();
	public boolean setState(String state);

	public boolean install();
	public boolean uninstall();

	public ProviderInterface getProvider();
	public Iterator<installStep> getInstallSteps();
	public Iterator<installStep> getInstallSteps(int logid);
	public void clearInstallSteps();
        public String getDescription();
        public String getInstallationNotes();
        public String getReleaseNotes();
        public String getLicenseType();
        public String getLicenseName();
        public String getLicenseVersion();
        public String getLicenseBody();
        public List<Object> getRelatedPeople(String type);
        public List<Object> getScreenshots();
        public List<Object> getStarturls();

	public JarFile getJarFile();
	public JarFile getIncludedPackageJarFile(String packageid,String packageversion);

	public BufferedInputStream getJarStream();

        public Iterator<HashMap<String, String>> getNeededPackages();

	public String getPath();

        public int getProgressBarValue();
        public int getPackageProgressBarValue();
        public void setProgressBar(int stepcount); 
        public void increaseProgressBar();
        public void increaseProgressBar(int stepcount);
        public long lastSeen();

}
