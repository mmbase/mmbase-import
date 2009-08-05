/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.applications.packaging;

import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;
import org.mmbase.applications.packaging.providerhandlers.*;
import org.mmbase.applications.packaging.bundlehandlers.*;
import org.mmbase.module.builders.Versions;

import java.util.*;

/**
 * Bundle manager, keeps track of all the bundles (of packages) available to this
 * this MMBase. Since bundles themselfs are also packages it can become a little
 * confusing thats why we have to managers (bundle and packages).
 *
 * @author Daniel Ockeloen (MMBased)
 */
public class BundleManager {

    // create a logger for this class
    private static Logger log = Logging.getLoggerInstance(BundleManager.class);

    // state if this manager is running or not.
    private static boolean state = false;
    
    // Contains all bundles key=bundlename/maintainer value=reference to bundle
    private static HashMap<String, BundleContainer> bundles = new HashMap<String, BundleContainer>();

    /**
    * init this manager
    */
    public static synchronized void init() {
        state = true;
    }


    /**
    * is this manager running
    */
    public static boolean isRunning() {
        return state;
    }

    /**
     * get all the bundles available to this MMBase
     * @return bundle list
     */
    public static Iterator<BundleContainer> getBundles() {
        return bundles.values().iterator();
    }

    /**
     * get a bundle based on its id
     *
     * return Bundle (interface) or null if not found
     */
    public static BundleInterface getBundle(String id) {
        Object o = bundles.get(id);
        if (o != null) {
            return (BundleInterface)o;
        }
        log.debug("bundle with id = "+id+" not found");
        return null;
    }


    /**
    * get a bundle (interface) based on it and wanted version/provider
    *
    * return Bundle (interface) or null if not found
    */
    public static BundleInterface getBundle(String id,String wv,String wp) {
        Object o = bundles.get(id);
        if (o != null) {
            BundleContainer bc = (BundleContainer)o;
            ProviderInterface provider = ProviderManager.get(wp);
            if (provider != null) {
                BundleInterface b = bc.getVersion(wv,provider);
                if (b != null) {
                    return b;
                }
            }
        }
        log.debug("bundle with id = "+id+" not found");
        return null;
    }


    public static BundleInterface getBundle(String id,String wv) {
        Object o = bundles.get(id);
        if (o != null) {
            BundleContainer pc = (BundleContainer)o;
            BundleInterface p = pc.getBundleByScore(wv);
            if (p != null) {
                return p;
            }
        }
        log.debug("bundle with id = "+id+" not found");
        return null;
    }


    public static BundleInterface foundBundle(ProviderInterface provider,org.w3c.dom.Element n,String name,String type,String maintainer,String version,String date,String path) {
        // create its id (name+maintainer)
        String id = name+"@"+maintainer+"_"+type;
        id = id.replace(' ','_');
        id = id.replace('/','_');
    
        // check if we allready have a bundle container for this
        BundleContainer bc = bundles.get(id);

        boolean found = false;
        if (bc != null) {
            // we allready have a container check if we allready
            // have this one
            found = bc.contains(version,provider);
        }

        if (!found) {    
            // so we don't have this bundle refernce yet, then
            // create and store it, should be a config file
            BundleInterface newbundle = null;

            // hardcoded handlers, need to be loaded using a xml def.
            if (type.equals("bundle/basic")) {
                newbundle = new BasicBundle(n,provider,name,type,maintainer,version,date,path);
            } 
            if (bc == null) {
                bc = new BundleContainer(newbundle);
                // since this is a new container store it
                bundles.put(id,bc);
            } else {
                bc.addBundle(newbundle);
            }
            ((BasicBundle)newbundle).signalUpdate();
        } else {
            // get the package to update its available time
            BasicBundle oldb = (BasicBundle)bc.getVersion(version,provider);
            if (oldb != null) oldb.signalUpdate();
        }
        return bc;
    }

    
    public static boolean updateRegistryInstalled(BundleInterface b) { 
        try {
            Versions versions = (Versions) MMBase.getMMBase().getMMObject("versions");
            if(versions == null) {
                log.error("Versions builder not installed.");
                return false;
            } 
            int newversion = Integer.parseInt(b.getVersion());
            int oldversion = getInstalledVersion(b.getId());
            if (oldversion == -1) {
                versions.setInstalledVersion(b.getId(),"bundle",b.getMaintainer(),newversion);
            } else if (oldversion != newversion) {
                versions.updateInstalledVersion(b.getId(),"bundle",b.getMaintainer(),newversion);
            }
            return true;
        } catch(Exception e) {
        	log.debug("error while updating  " + b + ":" + e.getMessage()  , e);
            return false;
        } 
    }


    public static boolean isInstalledVersion(BundleInterface b) {
        try {
            int newversion = Integer.parseInt(b.getVersion());
            if (getInstalledVersion(b.getId()) == newversion) {
                return true;
            } 
        } catch(Exception e) {
			log.debug("error while checking if a version of bundle: " + b + " is installed:" + e.getMessage()  , e);
            return false;
        }
        return false;
    }


    public static boolean upgradeAvailable(BundleInterface b) {
        try {
            int newversion = Integer.parseInt(b.getVersion());
            int oldversion = getInstalledVersion(b.getId());
	    if (oldversion!=-1 && oldversion!=0 && newversion > oldversion) {
		return true;
	    }
        } catch(Exception e) {
			log.debug("error while checking if a version of bundle: " + b + " is installed:" + e.getMessage()  , e);
            return false;
        }
        return false;
    }

    public static int getInstalledVersion(String id) {
        // Get the versions builder
        Versions versions = (Versions) MMBase.getMMBase().getMMObject("versions");
        if(versions==null) {
            log.error("Versions builder not installed.");
            return -1;
        } else {
            return versions.getInstalledVersion(id,"bundle");    
        }
    }

    public static boolean updateRegistryUninstalled(BundleInterface b) { 
        try {
            Versions versions = (Versions) MMBase.getMMBase().getMMObject("versions");
            if(versions == null) {
                log.error("Versions builder not installed.");
                return false;
            } 
            versions.updateInstalledVersion(b.getId(),"bundle",b.getMaintainer(),0);
            return true;
        } catch(Exception e) {
			log.debug("error while updateing the versions builder to mark  " + b + " as uninstalled:" + e.getMessage()  , e);
            return false;
        }
    }


    /**
     * return all bundles versions of this id
     */
    public static Iterator<BundleVersionContainer> getBundleVersions(String id) {
        Object o = bundles.get(id);
        if (o != null) {
            BundleContainer bc = (BundleContainer)o;
            return bc.getVersions();
        }
        return null;
    }


    public static boolean removeOfflineBundles(ProviderInterface wantedprov) {
        // this checks all the bundles if they are still found at their
        // providers, this is done by checking the last provider update
        // against the last bundle update
	HashMap<String, BundleContainer> mm = (HashMap<String, BundleContainer>)bundles.clone();
        Iterator<BundleContainer> e = mm.values().iterator();
        while (e.hasNext()) {
            BundleContainer pc = e.next();
            Iterator<BundleVersionContainer> e2 = pc.getVersions();
            while (e2.hasNext()) {
               BundleVersionContainer pvc = e2.next();
               Iterator<BundleInterface> e3 = pvc.getBundles();
               while (e3.hasNext()) {
                   BundleInterface p = e3.next();
                   ProviderInterface prov = p.getProvider();
                   if (wantedprov == prov) {
                       long providertime = p.getProvider().lastSeen();
                       long packagetime = p.lastSeen();
                       if (providertime > packagetime) {
                           pvc.removeBundle(p);
                           if (pvc.getBundleCount() == 0) {
                               pc.removeBundle(p);
                               if (pc.getBundleCount() == 0) {
                                   bundles.remove(pc.getId());
                               }
                           }
                       }
                   }
               }
           }
        }
        return true;
    }


}
