/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.applications.packaging;

import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;
import org.mmbase.util.*;
import org.mmbase.module.builders.Versions;
import org.mmbase.applications.packaging.packagehandlers.*;
import org.mmbase.applications.packaging.providerhandlers.*;
import org.mmbase.applications.packaging.*;
import org.mmbase.storage.search.SearchQueryException;

import java.io.File;
import java.util.*;

import org.w3c.dom.*;

/**
 * package manager, access point for all packages available to this cloud
 *
 * @author Daniel Ockeloen (MMBased)
 */
public class PackageManager {
    private static Logger log = Logging.getLoggerInstance(PackageManager.class);

    // Contains all packages key=packagename/maintainer value=reference to application
    private static HashMap packages = new HashMap();

    // state of this manager
    private static boolean state = false;
    private static HashMap packagehandlers;

    public static final String DTD_PACKAGEHANDLERS_1_0 = "packagehandlers_1_0.dtd";
    public static final String PUBLIC_ID_PACKAGEHANDLERS_1_0 = "-//MMBase//DTD packagehandlers config 1.0//EN"; 

    /**
     * Register the Public Ids for DTDs used by XMLBasicReader
     * This method is called by XMLEntityResolver.
     */
    public static void registerPublicIDs() {
        XMLEntityResolver.registerPublicID(PUBLIC_ID_PACKAGEHANDLERS_1_0, DTD_PACKAGEHANDLERS_1_0, PackageManager.class);
    }
    /**
    * init(), starts the package manager mostly start the
    * package discovery system.
    */
    public static synchronized void init() {
        readPackageHandlers();
        state = true;
    }

    public static boolean isRunning() {
        return state;
    }

    /**
     * return all packages based on the input query
     * @return all packages
     */
    public static Iterator getPackages() {
        return packages.values().iterator();
    }
    
    /**
     * return all packages based 
     * @return all packages
     */
    public static Iterator getPackageVersions(String id) {
        Object o = packages.get(id);
        if (o != null) {
            PackageContainer pc = (PackageContainer)o;
            return pc.getVersions();
        }
        return null;
    }


    /**
     * return a list of version numbers of this package
     */
    public static Iterator getPackageVersionNumbers(String id) {
        Object o = packages.get(id);
        if (o != null) {
            PackageContainer pc = (PackageContainer)o;
            return pc.getVersionNumbers();
        }
        return null;
    }

    /**
     * return all packages based on the input query
     * @return all packages
     */
    public static PackageInterface getPackage(String id) {
        Object o = packages.get(id);
        if (o != null) {
            return (PackageInterface)o;
        }
        log.error("package with id = "+id+" not found");
        return null;
    }


    /**
     * return all packages based on the input query
     * @return all packages
     */
    public static PackageInterface getPackage(String id,String wv,String wp) {
        Object o = packages.get(id);
        if (o != null) {
            PackageContainer pc = (PackageContainer)o;
            ProviderInterface provider = (ProviderInterface)ProviderManager.get(wp);
            if (provider != null) {
                PackageInterface p = pc.getVersion(wv,provider);
                if (p != null) {
                    return p;
                }
            }
        }
        log.error("package with id = "+id+" not found");
        return null;
    }


    /**
     * return all packages based on the input query
     * @return all packages
     */
    public static PackageInterface getPackage(String id,String wv) {
        Object o = packages.get(id);
        if (o != null) {
            PackageContainer pc = (PackageContainer)o;
            PackageInterface p = pc.getPackageByScore(wv);
            if (p != null) {
                return p;
            }
        }
        log.error("package with id = "+id+" not found");
        return null;
    }

    /**
     * called by Providers with found packages
     * they are checked and if new put into the
     * package pool.
     */
    public static PackageInterface foundPackage(ProviderInterface provider,org.w3c.dom.Element n,String name,String type,String maintainer,String version,String date,String path) {

        // create its id (name+maintainer)
        String id = name+"@"+maintainer+"_"+type;
        id = id.replace(' ','_');
        id = id.replace('/','_');
    
        // check if we allready have a package container for this
        PackageContainer pc = (PackageContainer)packages.get(id);

        boolean found = false;
        if (pc != null) {
            // we allready have a container check if we allready
            // have this one
            found = pc.contains(version,provider);
        }

        if (!found) {    
            // so we don't have this package refernce yet, then
            // create and store it

            // try to create this handler
            String classname = (String)packagehandlers.get(type);
            if (classname != null) {
                try {
                    Class newclass = Class.forName(classname);
                    PackageInterface newpackage = (PackageInterface)newclass.newInstance();
                    newpackage.init(n,provider,name,type,maintainer,version,date,path);
                    if (pc == null) {
                        pc = new PackageContainer(newpackage);
                        // since this is a new container store it
                        packages.put(id,pc);
                    } else {
                        pc.addPackage(newpackage);
                    }
                    ((BasicPackage)newpackage).signalUpdate();
                    return newpackage;
                } catch(Exception e) {
                    log.error("Can't create packagehandler : "+classname);
                    e.printStackTrace();
                }
            } else {
                log.error("package type : "+type+" not supported (no handler)");
            }
        } else {
            // get the package to update its available time
            BasicPackage oldp = (BasicPackage)pc.getVersion(version,provider);
            if (oldp != null) oldp.signalUpdate();
        }
        return null;
    }


    public static int getInstalledVersion(String id) throws SearchQueryException {
        // Get the versions builder
        Versions versions = (Versions) MMBase.getMMBase().getMMObject("versions");
        if(versions == null) {
            log.error("Versions builder not installed.");
            return -1;
        } else {
            return versions.getInstalledVersion(id,"package");    
        }
    }

    public static boolean isInstalledVersion(PackageInterface p) {
        try {
            int newversion = Integer.parseInt(p.getVersion());
            if (getInstalledVersion(p.getId()) == newversion) {
                return true;
            } 
        } catch(Exception e) {
            return false;
        }
        return false;
    }



    public static boolean updateRegistryInstalled(PackageInterface p) { 
        try {
            Versions versions = (Versions) MMBase.getMMBase().getMMObject("versions");
            if (versions == null) {
                log.error("Versions builder not installed.");
                return false;
            } 
            int newversion = Integer.parseInt(p.getVersion());
            int oldversion = getInstalledVersion(p.getId());
            if (oldversion == -1) {
                versions.setInstalledVersion(p.getId(),"package",p.getMaintainer(),newversion);
            } else if (oldversion != newversion) {
                versions.updateInstalledVersion(p.getId(),"package",p.getMaintainer(),newversion);
            }
            return true;
        } catch(Exception e) {
            return false;
        }
    }


    public static boolean updateRegistryUninstalled(PackageInterface p) { 
        try {
            Versions versions = (Versions) MMBase.getMMBase().getMMObject("versions");
            if(versions == null) {
                log.error("Versions builder not installed.");
                return false;
            } 
            versions.updateInstalledVersion(p.getId(),"package",p.getMaintainer(),0);
            return true;
        } catch(Exception e) {
            return false;
        }
    }
   
    public static boolean removeOfflinePackages(ProviderInterface wantedprov) {
        // this checks all the packages if they are still found at their
        // providers, this is done by checking the last provider update
        // against the last package update
        Iterator e = packages.values().iterator();
        while (e.hasNext()) {
            PackageContainer pc = (PackageContainer)e.next();
            Iterator e2 = pc.getVersions();
            while (e2.hasNext()) {
                PackageVersionContainer pvc = (PackageVersionContainer)e2.next();
                Iterator e3 = pvc.getPackages();
                while (e3.hasNext()) {
                    BasicPackage p = (BasicPackage)e3.next();
                    ProviderInterface prov = p.getProvider();
                    if (wantedprov == prov) {
                        long providertime = p.getProvider().lastSeen();
                        long packagetime = p.lastSeen();
                        if (providertime > packagetime) {
                            pvc.removePackage(p);
                            if (pvc.getPackageCount() == 0) {
                                pc.removePackage(p);
                                if (pc.getPackageCount() == 0) {
                                    packages.remove(pc.getId());
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    public static void readPackageHandlers() {
        packagehandlers = new HashMap();
        String filename = MMBaseContext.getConfigPath()+File.separator+"packaging"+File.separator+"packagehandlers.xml";

        File file = new File(filename);
        if(file.exists()) {
            XMLBasicReader reader = new XMLBasicReader(filename,PackageManager.class);
            if(reader != null) {
                for(Enumeration ns = reader.getChildElements("packagehandlers","packagehandler");ns.hasMoreElements(); ) {
                    Element n = (Element)ns.nextElement();
                    NamedNodeMap nm = n.getAttributes();
                    if (nm != null) {
                        String type = null;
                        String classname = null;

                        // decode type
                        org.w3c.dom.Node n2 = nm.getNamedItem("type");
                        if (n2 != null) {
                            type = n2.getNodeValue();
                        }

                        // decode the class
                        n2 = nm.getNamedItem("class");
                        if (n2 != null) {
                            classname = n2.getNodeValue();
                        }
                        packagehandlers.put(type,classname);
                    }
                }
            }
        } else {
            log.error("missing packagehandler file : "+filename);
        }
    }

    public static HashMap getPackageHandlers() { 
        return packagehandlers;
    }
}
