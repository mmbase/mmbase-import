/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.applications.packaging.providerhandlers;

import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;
import org.mmbase.util.*;
import org.mmbase.module.builders.Versions;
import org.mmbase.applications.packaging.*;
import org.mmbase.applications.packaging.packagehandlers.*;

import java.io.*;
import java.util.*;
import java.util.jar.*;

import org.w3c.dom.*;

/**
 * BasicProvider, Basic Handler for Providers. gets packages and bundles from
 * the provider and feeds them to the package and bundle managers.
 *
 * @author Daniel Ockeloen (MMBased)
 */
public class BasicProvider implements ProviderInterface {
    private static Logger log = Logging.getLoggerInstance(BasicProvider.class.getName());

    private long lastupdate;
    
    String name;
    String method;
    String maintainer;
    String account;
    String password;
    String path="";
    String description="";
    String state="down";

    org.w3c.dom.Node xmlnode;
    int baseScore=0;

    public void init(org.w3c.dom.Node n,String name,String method,String maintainer) {
	this.name=name;
	this.method=method;
	this.maintainer=maintainer;
	this.xmlnode=n;
    }


    public void init(String name,String method,String maintainer,String path) {
	this.name=name;
	this.method=method;
	this.maintainer=maintainer;
	this.path=path;
    }

    public String getName() {
	return(name);
    }
    
    public String getMethod() {
	return(method);
    }

    public void setAccount(String account) {
	this.account=account;
    }

    public void setPassword(String password) {
	this.password=password;
    }
    public void setMaintainer(String maintainer) {
	this.maintainer=maintainer;
    }

    public String getMaintainer() {
	return maintainer;
    }

    public void setPath(String path) {
	this.path=path;
    }

    public String getPath() {
	return path;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description=description;
    }

    public void getPackages() {
       log.error("getPackages called should be overridden");
    }


    public JarFile getJarFile(String path,String id,String version) {
		log.error("Provider not implementing call : getJarFile "+this);
		return null;
    }

    public JarFile getIncludedPackageJarFile(String path,String id,String version,String packageid,String packageversion) {
		log.error("Provider not implementing call : getIncludedPackageJarFile "+this);
		return null;
    }

    public BufferedInputStream getJarStream(String path) {
		return null;
    }

    public int getBaseScore() {
	return baseScore;
    }

    public void signalUpdate() {
	lastupdate=System.currentTimeMillis();
    }

    public long lastSeen() {
	return lastupdate;
    }

    public String getState() {
	return state;
    }
 
    public void setState(String state) {
	this.state=state;
    }

    public boolean close() {
	return true;
    }

}
