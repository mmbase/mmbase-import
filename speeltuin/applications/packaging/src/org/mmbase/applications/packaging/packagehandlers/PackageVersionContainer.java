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
import org.mmbase.applications.packaging.providerhandlers.*;
import org.mmbase.applications.packaging.sharehandlers.*;

import java.io.File;
import java.util.*;

import org.w3c.dom.*;

/**
 * The package version container, keeps all track of all the packages with
 * the same version (but multiple providers)
 *
 * @author Daniel Ockeloen (MMBased)
 */
public class PackageVersionContainer  {
    private static Logger log = Logging.getLoggerInstance(PackageVersionContainer.class);

    private ShareInfo shareinfo;
    private String version;

    private Hashtable packages=new Hashtable();

    public PackageVersionContainer(PackageInterface p) {
	packages.put(p.getProvider(),p);
	version=p.getVersion();
    }

    public Object addPackage(PackageInterface p) {
	Object o=packages.put(p.getProvider(),p);
	if (o!=null) {
		return(o);
	}
	return(null);
    }


    public boolean removePackage(PackageInterface p) {
	packages.remove(p.getProvider());
	return true;
    }

    public int getPackageCount() {
	return packages.size();
    }

    public Object get(ProviderInterface provider) {
	Object o=packages.get(provider);
	if (o!=null) {
		return(o);
	}
	return(null);
    } 

   public Enumeration getPackages() {
	return packages.elements();
   }


    public boolean contains(ProviderInterface provider) {
	if (packages.get(provider)!=null) {
		return(true);
	} else {
		return(false);
	}
    }
   
    public boolean isShared() {
	if (shareinfo!=null) {
		return true;
	}
	return false;

    }

    public String getVersion() {
	return version;
    }

    public ShareInfo getShareInfo() {
	return shareinfo;
    }

    public PackageInterface getPackageByScore() {
	PackageInterface winner=null;
	Enumeration e=packages.elements();
	while (e.hasMoreElements()) {
		PackageInterface p=(PackageInterface)e.nextElement();
		if (winner==null) {
			winner=p;
		} else if (p.getProvider().getBaseScore()>winner.getProvider().getBaseScore()) {
			winner=p;
		}
	}
	return winner;
    }

}
