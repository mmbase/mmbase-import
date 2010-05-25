/*
 *  This software is OSI Certified Open Source Software.
 *  OSI Certified is a certification mark of the Open Source Initiative.
 *  The license (Mozilla version 1.0) can be read at the MMBase site.
 *  See http://www.MMBase.org/license
 */
package org.mmbase.applications.packaging.packagehandlers;

import java.util.HashMap;
import java.util.Iterator;

import org.mmbase.applications.packaging.providerhandlers.ProviderInterface;
import org.mmbase.applications.packaging.sharehandlers.ShareInfo;

/**
 * The package version container, keeps all track of all the packages with
 * the same version (but multiple providers)
 *
 * @author     Daniel Ockeloen (MMBased)
 */
public class PackageVersionContainer {

    private ShareInfo shareinfo;
    private String version;

    private HashMap<ProviderInterface, PackageInterface> packages = new HashMap<ProviderInterface, PackageInterface>();


    /**
     *Constructor for the PackageVersionContainer object
     *
     * @param  p  Description of the Parameter
     */
    public PackageVersionContainer(PackageInterface p) {

        version = p.getVersion();
    }


    /**
     *  Adds a feature to the Package attribute of the PackageVersionContainer object
     *
     * @param  p  The feature to be added to the Package attribute
     * @return    Description of the Return Value
     */
    public Object addPackage(PackageInterface p) {
        Object o = packages.put(p.getProvider(), p);
        if (o != null) {
            return (o);
        }
        return (null);
    }


    /**
     *  Description of the Method
     *
     * @param  p  Description of the Parameter
     * @return    Description of the Return Value
     */
    public boolean removePackage(PackageInterface p) {
        packages.remove(p.getProvider());
        return true;
    }


    /**
     *  Gets the packageCount attribute of the PackageVersionContainer object
     *
     * @return    The packageCount value
     */
    public int getPackageCount() {
        return packages.size();
    }


    /**
     *  Description of the Method
     *
     * @param  provider  Description of the Parameter
     * @return           Description of the Return Value
     */
    public Object get(ProviderInterface provider) {
        Object o = packages.get(provider);
        if (o != null) {
            return (o);
        }
        return (null);
    }


    /**
     *  Gets the packages attribute of the PackageVersionContainer object
     *
     * @return    The packages value
     */
    public Iterator<PackageInterface> getPackages() {
        return ((HashMap<ProviderInterface, PackageInterface>)packages.clone()).values().iterator();
    }


    /**
     *  Description of the Method
     *
     * @param  provider  Description of the Parameter
     * @return           Description of the Return Value
     */
    public boolean contains(ProviderInterface provider) {
        if (packages.get(provider) != null) {
            return (true);
        } else {
            return (false);
        }
    }


    /**
     *  Gets the shared attribute of the PackageVersionContainer object
     *
     * @return    The shared value
     */
    public boolean isShared() {
        if (shareinfo != null) {
            return true;
        }
        return false;
    }


    /**
     *  Gets the version attribute of the PackageVersionContainer object
     *
     * @return    The version value
     */
    public String getVersion() {
        return version;
    }


    /**
     *  Gets the shareInfo attribute of the PackageVersionContainer object
     *
     * @return    The shareInfo value
     */
    public ShareInfo getShareInfo() {
        return shareinfo;
    }


    /**
     *  Gets the packageByScore attribute of the PackageVersionContainer object
     *
     * @return    The packageByScore value
     */
    public PackageInterface getPackageByScore() {
        PackageInterface winner = null;
        Iterator<PackageInterface> e = packages.values().iterator();
        while (e.hasNext()) {
            PackageInterface p = e.next();
            if (winner == null) {
                winner = p;
            } else if (p.getProvider().getBaseScore() > winner.getProvider().getBaseScore()) {
                winner = p;
            }
        }
        return winner;
    }

}

