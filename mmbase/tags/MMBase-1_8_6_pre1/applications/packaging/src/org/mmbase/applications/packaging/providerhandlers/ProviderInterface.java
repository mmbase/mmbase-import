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

/**
 * Interface for all the provider handlers
 *
 * @author     danielockeloen
 */
public interface ProviderInterface {

    /**
     *  Gets the name attribute of the ProviderInterface object
     *
     * @return    The name value
     */
    public String getName();


    /**
     *  Gets the method attribute of the ProviderInterface object
     *
     * @return    The method value
     */
    public String getMethod();


    /**
     *  Gets the path attribute of the ProviderInterface object
     *
     * @return    The path value
     */
    public String getPath();


    /**
     *  Gets the state attribute of the ProviderInterface object
     *
     * @return    The state value
     */
    public String getState();


    /**
     *  Gets the maintainer attribute of the ProviderInterface object
     *
     * @return    The maintainer value
     */
    public String getMaintainer();


    /**
     *  Gets the description attribute of the ProviderInterface object
     *
     * @return    The description value
     */
    public String getDescription();


    /**
     *  Sets the maintainer attribute of the ProviderInterface object
     *
     * @param  maintainer  The new maintainer value
     */
    public void setMaintainer(String maintainer);


    /**
     *  Sets the path attribute of the ProviderInterface object
     *
     * @param  path  The new path value
     */
    public void setPath(String path);

    /**
     *  Sets the installStep attribute of the ProviderInterface object
     *
     * @param  step  The installstep
     */
    public void setInstallStep(installStep step);

    public void setPackageStep(packageStep step);

    /**
     *  Sets the description attribute of the ProviderInterface object
     *
     * @param  description  The new description value
     */
    public void setDescription(String description);


    /**
     *  Sets the state attribute of the ProviderInterface object
     *
     * @param  state  The new state value
     */
    public void setState(String state);


    /**
     *  Sets the account attribute of the ProviderInterface object
     *
     * @param  account  The new account value
     */
    public void setAccount(String account);


    /**
     *  Sets the password attribute of the ProviderInterface object
     *
     * @param  password  The new password value
     */
    public void setPassword(String password);


    /**
     *  Gets the baseScore attribute of the ProviderInterface object
     *
     * @return    The baseScore value
     */
    public int getBaseScore();


    /**
     *  Gets the jarFile attribute of the ProviderInterface object
     *
     * @param  path     Description of the Parameter
     * @param  id       Description of the Parameter
     * @param  version  Description of the Parameter
     * @return          The jarFile value
     */
    public JarFile getJarFile(String path, String id, String version);


    /**
     *  Gets the includedPackageJarFile attribute of the ProviderInterface object
     *
     * @param  path            Description of the Parameter
     * @param  id              Description of the Parameter
     * @param  version         Description of the Parameter
     * @param  packageid       Description of the Parameter
     * @param  packageversion  Description of the Parameter
     * @return                 The includedPackageJarFile value
     */
    public JarFile getIncludedPackageJarFile(String path, String id, String version, String packageid, String packageversion);


    /**
     *  Gets the jarStream attribute of the ProviderInterface object
     *
     * @param  path  Description of the Parameter
     * @return       The jarStream value
     */
    public BufferedInputStream getJarStream(String path);


    /**
     *  Description of the Method
     */
    public void signalUpdate();


    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public long lastSeen();


    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public boolean close();


    /**
     *  Gets the packages attribute of the ProviderInterface object
     */
    public void getPackages();


    /**
     *  Description of the Method
     *
     * @param  n           Description of the Parameter
     * @param  name        Description of the Parameter
     * @param  method      Description of the Parameter
     * @param  maintainer  Description of the Parameter
     */
    public void init(org.w3c.dom.Node n, String name, String method, String maintainer);


    /**
     *  Description of the Method
     *
     * @param  name        Description of the Parameter
     * @param  method      Description of the Parameter
     * @param  maintainer  Description of the Parameter
     * @param  path        Description of the Parameter
     */
    public void init(String name, String method, String maintainer, String path);

    public boolean publish(CreatorInterface creator, BundleInterface bundle,String sharepassword);

    public boolean publish(CreatorInterface creator, PackageInterface p,String sharepassword);
}

