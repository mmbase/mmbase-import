/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.applications.packaging.bundlehandlers;

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
 * The bundle version container, keeps all track of all the bundles with
 * the same version (but multiple providers)
 *
 * @author Daniel Ockeloen (MMBased)
 */
public class BundleVersionContainer  {
    private static Logger log = Logging.getLoggerInstance(BundleVersionContainer.class);

    private ShareInfo shareinfo;

    private Hashtable bundles=new Hashtable();

    public BundleVersionContainer(BundleInterface b) {
	bundles.put(b.getProvider(),b);
    }

    public Object addBundle(BundleInterface b) {
	Object o=bundles.put(b.getProvider(),b);
	if (o!=null) {
		return(o);
	}
	return(null);
    }


    public boolean removeBundle(BundleInterface b) {
	bundles.remove(b.getProvider());
	return true;
    }

    public int getBundleCount() {
	return bundles.size();
    }

    public Object get(ProviderInterface provider) {
	Object o=bundles.get(provider);
	if (o!=null) {
		return(o);
	}
	return(null);
    } 

   public Enumeration getBundles() {
	return bundles.elements();
   }


    public boolean contains(ProviderInterface provider) {
	if (bundles.get(provider)!=null) {
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

    public ShareInfo getShareInfo() {
	return shareinfo;
    }

    public BundleInterface getBundleByScore() {
	BundleInterface winner=null;
	Enumeration e=bundles.elements();
	while (e.hasMoreElements()) {
		BundleInterface b=(BundleInterface)e.nextElement();
		if (winner==null) {
			winner=b;
		} else if (b.getProvider().getBaseScore()>winner.getProvider().getBaseScore()) {
			winner=b;
		}
	}
	return winner;
    }

}
