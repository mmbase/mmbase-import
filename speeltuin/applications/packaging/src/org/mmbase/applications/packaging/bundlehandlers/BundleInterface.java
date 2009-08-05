/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.applications.packaging.bundlehandlers;

import org.mmbase.applications.packaging.providerhandlers.*;
import org.mmbase.applications.packaging.sharehandlers.*;
import java.io.*;
import java.util.*;
import java.util.jar.*;

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
	public Iterator getInstallSteps();
	public Iterator getInstallSteps(int logid);
	public void clearInstallSteps();
        public String getDescription();
        public String getInstallationNotes();
        public String getReleaseNotes();
        public String getLicenseType();
        public String getLicenseName();
        public String getLicenseVersion();
        public String getLicenseBody();
        public List getRelatedPeople(String type);

	public JarFile getJarFile();
	public JarFile getIncludedPackageJarFile(String packageid,String packageversion);

	public BufferedInputStream getJarStream();

        public Iterator getNeededPackages();

	public String getPath();

        public int getProgressBarValue();
        public int getPackageProgressBarValue();
        public void setProgressBar(int stepcount); 
        public void increaseProgressBar();
        public void increaseProgressBar(int stepcount);

}
