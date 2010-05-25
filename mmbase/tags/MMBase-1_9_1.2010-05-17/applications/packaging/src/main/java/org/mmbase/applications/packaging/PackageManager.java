/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */

package org.mmbase.applications.packaging;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.mmbase.applications.packaging.packagehandlers.BasicPackage;
import org.mmbase.applications.packaging.packagehandlers.PackageContainer;
import org.mmbase.applications.packaging.packagehandlers.PackageInterface;
import org.mmbase.applications.packaging.packagehandlers.PackageVersionContainer;
import org.mmbase.applications.packaging.providerhandlers.ProviderInterface;
import org.mmbase.applications.packaging.util.ExtendedDocumentReader;
import org.mmbase.module.builders.Versions;
import org.mmbase.module.core.MMBase;
import org.mmbase.storage.search.SearchQueryException;
import org.mmbase.util.ResourceLoader;
import org.mmbase.util.xml.EntityResolver;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

/**
 * package manager, access point for all packages available to this cloud
 *
 * @author Daniel Ockeloen (MMBased)
 */
public class PackageManager {
    private static Logger log = Logging.getLoggerInstance(PackageManager.class);

    // Contains all packages key=packagename/maintainer value=reference to application
    private static HashMap<String, PackageContainer> packages = new HashMap<String, PackageContainer>();

    // state of this manager
    private static boolean state = false;
    private static HashMap<String, String> packagehandlers;

    public static final String DTD_PACKAGEHANDLERS_1_0 = "packagehandlers_1_0.dtd";
    public static final String PUBLIC_ID_PACKAGEHANDLERS_1_0 = "-//MMBase//DTD packagehandlers config 1.0//EN";

    /**
     * Register the Public Ids for DTDs used by XMLBasicReader
     * This method is called by EntityResolver.
     */
    public static void registerPublicIDs() {
        EntityResolver.registerPublicID(PUBLIC_ID_PACKAGEHANDLERS_1_0, DTD_PACKAGEHANDLERS_1_0, PackageManager.class);
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
    public static Iterator<PackageContainer> getPackages() {
        return packages.values().iterator();
    }

    /**
     * return all packages based
     * @return all packages
     */
    public static Iterator<PackageVersionContainer> getPackageVersions(String id) {
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
    public static Iterator<String> getPackageVersionNumbers(String id) {
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
            ProviderInterface provider = ProviderManager.get(wp);
            if (provider != null) {
                PackageInterface p = pc.getVersion(wv,provider);
                if (p != null) {
                    return p;
                }
            }
        }
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
        PackageContainer pc = packages.get(id);

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
            String classname = packagehandlers.get(type);
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
                    log.error("Can't create packagehandler : "+classname,e);
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
            log.debug("error while checking if a version"+ ((p != null)?p.getVersion():"(p == null")+" of the package "+ p +" is installed:" + e.getMessage() , e);
            return false;
        }
        return false;
    }


    public static boolean upgradeAvailable(PackageInterface p) {
        try {
            int newversion = Integer.parseInt(p.getVersion());
            int oldversion = getInstalledVersion(p.getId());
        if (oldversion!=-1 && newversion > oldversion) {
        return true;
        }
        } catch(Exception e) {
            log.debug("error while checking if a version"+ ((p != null)?p.getVersion():"(p == null")+" of the package "+ p +" is installed:" + e.getMessage() , e);
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
            log.debug("error while updating versions for version "+ ((p != null)?p.getVersion():"(p == null")+" of the package "+ p + ":" + e.getMessage() , e);
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
            log.debug("error while updating the installed status to 0 of the package "+ p + ":" + e.getMessage() , e);
            return false;
        }
    }

    public static boolean removeOfflinePackages(ProviderInterface wantedprov) {
        // this checks all the packages if they are still found at their
        // providers, this is done by checking the last provider update
        // against the last package update
        Iterator<PackageContainer> e = ((HashMap<String,PackageContainer>)packages.clone()).values().iterator();
        while (e.hasNext()) {
            PackageContainer pc = e.next();
            Iterator<PackageVersionContainer> e2 = pc.getVersions();
            while (e2.hasNext()) {
                PackageVersionContainer pvc = e2.next();
                Iterator<PackageInterface> e3 = pvc.getPackages();
                while (e3.hasNext()) {
                    PackageInterface p = e3.next();
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
        packagehandlers = new HashMap<String, String>();
        String filename = getConfigPath()+File.separator+"packaging"+File.separator+"packagehandlers.xml";

        File file = new File(filename);
        if(file.exists()) {

            ExtendedDocumentReader reader = new ExtendedDocumentReader(filename,PackageManager.class);
            if(reader != null) {
                for (Element n: reader.getChildElements("packagehandlers", "packagehandler")) {
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

    public static HashMap<String, String> getPackageHandlers() {
        return packagehandlers;
    }

    public static String getConfigPath() {
        List<File> files =  ResourceLoader.getConfigurationRoot().getFiles("");
        if (files.size() == 0) {
            return null;
        } else {
            return files.get(0).getAbsolutePath();
        }
    }

}
