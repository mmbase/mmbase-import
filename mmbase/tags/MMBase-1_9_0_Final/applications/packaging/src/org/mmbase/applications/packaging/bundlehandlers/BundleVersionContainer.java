/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.applications.packaging.bundlehandlers;

import java.util.HashMap;
import java.util.Iterator;

import org.mmbase.applications.packaging.providerhandlers.ProviderInterface;
import org.mmbase.applications.packaging.sharehandlers.ShareInfo;

/**
 * The bundle version container, keeps all track of all the bundles with
 * the same version (but multiple providers)
 *
 * @author Daniel Ockeloen (MMBased)
 */
public class BundleVersionContainer  {

    private ShareInfo shareinfo;

    private HashMap<ProviderInterface, BundleInterface> bundles=new HashMap<ProviderInterface, BundleInterface>();

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
        Object o = bundles.get(provider);
        if (o != null) {
            return o;
        }
        return null;
    } 

   public Iterator<BundleInterface> getBundles() {
       return ((HashMap<ProviderInterface, BundleInterface>)bundles.clone()).values().iterator();
   }


    public boolean contains(ProviderInterface provider) {
        if (bundles.get(provider) != null) {
            return true;
        } else {
            return false;
        }
    }
   
    public boolean isShared() {
        if (shareinfo != null) {
            return true;
        }
        return false;
    }

    public ShareInfo getShareInfo() {
        return shareinfo;
    }

    public BundleInterface getBundleByScore() {
        BundleInterface winner = null;
        Iterator<BundleInterface> e = bundles.values().iterator();
        while (e.hasNext()) {
            BundleInterface b = e.next();
            if (winner == null) {
                winner = b;
            } else if (b.getProvider().getBaseScore() > winner.getProvider().getBaseScore()) {
                winner = b;
            }
        }
        return winner;
    }

}
