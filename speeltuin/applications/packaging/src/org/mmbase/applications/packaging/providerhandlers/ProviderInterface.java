/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.applications.packaging.providerhandlers;

import java.util.jar.*;
import java.io.*;
import org.mmbase.applications.packaging.packagehandlers.*;

/**
 * Interface for all the provider handlers
 */
public interface ProviderInterface {

	public String getName();
	public String getMethod();
	public String getPath();
	public String getState();
	public String getMaintainer();
	public String getDescription();
	public void setMaintainer(String maintainer);
	public void setPath(String path);
	public void setDescription(String description);
	public void setState(String state);
	public void setAccount(String account);
	public void setPassword(String password);
	public int getBaseScore();
	public JarFile getJarFile(String path,String id,String version);
	public JarFile getIncludedPackageJarFile(String path,String id,String version,String packageid,String packageversion);
	public BufferedInputStream getJarStream(String path);
	public void signalUpdate();
	public long lastSeen();
	public boolean close();
	public void getPackages();
	public void init(org.w3c.dom.Node n,String name,String method,String maintainer);
    	public void init(String name,String method,String maintainer,String path);
}
