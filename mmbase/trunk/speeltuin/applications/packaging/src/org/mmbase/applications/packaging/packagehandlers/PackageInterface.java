/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.applications.packaging.packagehandlers;

import org.mmbase.applications.packaging.providerhandlers.*;
import org.mmbase.applications.packaging.sharehandlers.*;
import org.mmbase.applications.packaging.bundlehandlers.*;
import org.mmbase.applications.packaging.installhandlers.*;
import java.io.*;
import java.util.*;
import java.util.jar.*;
import org.w3c.dom.*;
import org.xml.sax.*;

/**
 * Interface for all the package handlers
 */
public interface PackageInterface {
        public void init(org.w3c.dom.Node n,ProviderInterface provider,String name,String type,String maintainer,String version, String date,String path);
	public String getId();
	public String getName();
	public String getType();
	public String getVersion();
	public String getCreationDate();
	public String getMaintainer();
	public String getState();
	public String getDescription();
	public String getInstallationNotes();
	public String getReleaseNotes();
        public String getLicenseType();
        public String getLicenseName();
        public String getLicenseVersion();
        public String getLicenseBody();
        public List getRelatedPeople(String type);

	public BundleInterface getParentBundle();
	public void setParentBundle(BundleInterface parent);
	public boolean setState(String state);

	public boolean install();
	public boolean uninstall();

	public boolean install(installStep step);
	public boolean uninstall(installStep step);

	public ProviderInterface getProvider();
	public Enumeration getInstallSteps();
	public Enumeration getInstallSteps(int logid);
	public void clearInstallSteps();

	public JarFile getJarFile();
	public BufferedInputStream getJarStream();

	public String getPath();
        public boolean getDependsFailed();

}
