/*
 *  This software is OSI Certified Open Source Software.
 *  OSI Certified is a certification mark of the Open Source Initiative.
 *  The license (Mozilla version 1.0) can be read at the MMBase site.
 *  See http://www.MMBase.org/license
 */
package org.mmbase.applications.packaging.packagehandlers;

import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;
import org.mmbase.util.*;
import org.mmbase.module.builders.Versions;
import org.mmbase.applications.packaging.*;
import org.mmbase.applications.packaging.packagehandlers.*;
import org.mmbase.applications.packaging.providerhandlers.*;
import org.mmbase.applications.packaging.sharehandlers.*;

import java.io.File;
import java.util.*;

import org.w3c.dom.*;

/**
 * The package version container, keeps all track of all the packages with
 * the same version (but multiple providers)
 *
 * @author     Daniel Ockeloen (MMBased)
 * @created    July 20, 2004
 */
public class PackageVersionContainer {
    private static Logger log = Logging.getLoggerInstance(PackageVersionContainer.class);

    private ShareInfo shareinfo;
    private String version;

    private Hashtable packages = new Hashtable();


    /**
     *Constructor for the PackageVersionContainer object
     *
     * @param  p  Description of the Parameter
     */
    public PackageVersionContainer(PackageInterface p) {
        packages.put(p.getProvider(), p);
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
    public Iterator getPackages() {
        return packages.values().iterator();
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
        Enumeration e = packages.elements();
        while (e.hasMoreElements()) {
            PackageInterface p = (PackageInterface) e.nextElement();
            if (winner == null) {
                winner = p;
            } else if (p.getProvider().getBaseScore() > winner.getProvider().getBaseScore()) {
                winner = p;
            }
        }
        return winner;
    }

}

