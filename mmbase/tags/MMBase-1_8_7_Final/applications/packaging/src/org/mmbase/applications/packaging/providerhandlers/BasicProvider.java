/*
 *  This software is OSI Certified Open Source Software.
 *  OSI Certified is a certification mark of the Open Source Initiative.
 *  The license (Mozilla version 1.0) can be read at the MMBase site.
 *  See http://www.MMBase.org/license
 */
package org.mmbase.applications.packaging.providerhandlers;

import java.io.BufferedInputStream;
import java.util.jar.JarFile;

import org.mmbase.applications.packaging.bundlehandlers.BundleInterface;
import org.mmbase.applications.packaging.installhandlers.installStep;
import org.mmbase.applications.packaging.packagehandlers.PackageInterface;
import org.mmbase.applications.packaging.projects.packageStep;
import org.mmbase.applications.packaging.projects.creators.CreatorInterface;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * BasicProvider, Basic Handler for Providers. gets packages and bundles from
 * the provider and feeds them to the package and bundle managers.
 *
 * @author     Daniel Ockeloen (MMBased)
 */
public class BasicProvider implements ProviderInterface {
    private static Logger log = Logging.getLoggerInstance(BasicProvider.class);

    private long lastupdate;
  
    private installStep step;
    private packageStep packagestep;

    String name;
    String method;
    String maintainer;
    String account;
    String password;
    String path = "";
    String description = "";
    String state = "down";

    org.w3c.dom.Node xmlnode;
    int baseScore = 0;


    /**
     *  Description of the Method
     *
     * @param  n           Description of the Parameter
     * @param  name        Description of the Parameter
     * @param  method      Description of the Parameter
     * @param  maintainer  Description of the Parameter
     */
    public void init(org.w3c.dom.Node n, String name, String method, String maintainer) {
        this.name = name;
        this.method = method;
        this.maintainer = maintainer;
        this.xmlnode = n;
    }


    /**
     *  Description of the Method
     *
     * @param  name        Description of the Parameter
     * @param  method      Description of the Parameter
     * @param  maintainer  Description of the Parameter
     * @param  path        Description of the Parameter
     */
    public void init(String name, String method, String maintainer, String path) {
        this.name = name;
        this.method = method;
        this.maintainer = maintainer;
        this.path = path;
    }


    /**
     *  Gets the name attribute of the BasicProvider object
     *
     * @return    The name value
     */
    public String getName() {
        return (name);
    }


    /**
     *  Gets the method attribute of the BasicProvider object
     *
     * @return    The method value
     */
    public String getMethod() {
        return (method);
    }


    /**
     *  Sets the account attribute of the BasicProvider object
     *
     * @param  account  The new account value
     */
    public void setAccount(String account) {
        this.account = account;
    }


    /**
     *  Sets the password attribute of the BasicProvider object
     *
     * @param  password  The new password value
     */
    public void setPassword(String password) {
        this.password = password;
    }


    /**
     *  Sets the maintainer attribute of the BasicProvider object
     *
     * @param  maintainer  The new maintainer value
     */
    public void setMaintainer(String maintainer) {
        this.maintainer = maintainer;
    }


    /**
     *  Gets the maintainer attribute of the BasicProvider object
     *
     * @return    The maintainer value
     */
    public String getMaintainer() {
        return maintainer;
    }


    /**
     *  Sets the path attribute of the BasicProvider object
     *
     * @param  path  The new path value
     */
    public void setPath(String path) {
        this.path = path;
    }


    /**
     *  Gets the path attribute of the BasicProvider object
     *
     * @return    The path value
     */
    public String getPath() {
        return path;
    }


    /**
     *  Gets the description attribute of the BasicProvider object
     *
     * @return    The description value
     */
    public String getDescription() {
        return description;
    }


    /**
     *  Sets the description attribute of the BasicProvider object
     *
     * @param  description  The new description value
     */
    public void setDescription(String description) {
        this.description = description;
    }


    /**
     *  Gets the packages attribute of the BasicProvider object
     */
    public void getPackages() {
        log.error("getPackages called should be overridden");
    }


    /**
     *  Gets the jarFile attribute of the BasicProvider object
     *
     * @param  path     Description of the Parameter
     * @param  id       Description of the Parameter
     * @param  version  Description of the Parameter
     * @return          The jarFile value
     */
    public JarFile getJarFile(String path, String id, String version) {
        log.error("Provider not implementing call : getJarFile " + this);
        return null;
    }


    /**
     *  Gets the includedPackageJarFile attribute of the BasicProvider object
     *
     * @param  path            Description of the Parameter
     * @param  id              Description of the Parameter
     * @param  version         Description of the Parameter
     * @param  packageid       Description of the Parameter
     * @param  packageversion  Description of the Parameter
     * @return                 The includedPackageJarFile value
     */
    public JarFile getIncludedPackageJarFile(String path, String id, String version, String packageid, String packageversion) {
        log.error("Provider not implementing call : getIncludedPackageJarFile " + this);
        return null;
    }


    /**
     *  Gets the jarStream attribute of the BasicProvider object
     *
     * @param  path  Description of the Parameter
     * @return       The jarStream value
     */
    public BufferedInputStream getJarStream(String path) {
        return null;
    }


    /**
     *  Gets the baseScore attribute of the BasicProvider object
     *
     * @return    The baseScore value
     */
    public int getBaseScore() {
        return baseScore;
    }


    /**
     *  Description of the Method
     */
    public void signalUpdate() {
        lastupdate = System.currentTimeMillis();
    }


    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public long lastSeen() {
        return lastupdate;
    }


    /**
     *  Gets the state attribute of the BasicProvider object
     *
     * @return    The state value
     */
    public String getState() {
        return state;
    }


    /**
     *  Sets the state attribute of the BasicProvider object
     *
     * @param  state  The new state value
     */
    public void setState(String state) {
        this.state = state;
    }


    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public boolean close() {
        return true;
    }

    public boolean publish(CreatorInterface creator,BundleInterface bundle,String sharepassword) {
	log.error("Publish (bundle) call not supported in this provider type : "+getName());
	return false;
    }

    public boolean publish(CreatorInterface creator,PackageInterface bundle,String sharepassword) {
	log.error("Publish (package) call not supported in this provider type : "+getName());
	return false;
    }

    public void setInstallStep(installStep step) {
	this.step = step;
    }    

    public void setPackageStep(packageStep packagestep) {
	this.packagestep = packagestep;
    }    

    public installStep getInstallStep() {
	return step; 
    }

    public packageStep getPackageStep() {
	return packagestep; 
    }

}

