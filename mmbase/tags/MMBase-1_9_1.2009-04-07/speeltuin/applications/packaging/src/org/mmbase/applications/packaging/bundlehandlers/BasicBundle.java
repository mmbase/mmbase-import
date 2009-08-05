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
import org.mmbase.applications.packaging.installhandlers.*;
import org.mmbase.applications.packaging.sharehandlers.*;

import java.io.*;
import java.util.*;
import java.util.jar.*;

import org.w3c.dom.*;

/**
 * BasicBundle, base class for bundles
 *
 * @author Daniel Ockeloen (MMBased)
 */
public class BasicBundle implements BundleInterface {
    private static Logger log = Logging.getLoggerInstance(BasicBundle.class);
    
    private String name;
    private String id;
    private String maintainer;
    private String version;
    private String date;
    private String type = "unknown/unknown";
    private String state = "not installed";
    private String path;
    private String description = "";
    private String releasenotes = "";
    private String installationnotes = "";
    private String licensename = "";
    private String licensetype = "";
    private String licenseversion = "";
    private String licensebody = "";
    private ProviderInterface provider;
    private ShareInfo shareinfo;
    private HashMap neededpackages = new HashMap();
    private ArrayList initiators,supporters,contacts,developers;
    private float progressbar = 0;
    private float progressstep = 1;
    private PackageInterface pkg = null;

    // the install manager keeps track of what happend during a install
    // of a package or bundle. These are called steps because they not
    // only can provide log info but also possible fixed, feedback, stats
    // etc etc. Each step in itself can have steps again providing for things
    // like three style logging and feedback
    private ArrayList installsteps;

    private long lastupdated;

    public BasicBundle() {
    }

    public BasicBundle(org.w3c.dom.Node n,ProviderInterface provider,String name,String type,String maintainer,String version, String date,String path) {
        this.name = name;
        this.version = version;
        this.date = date;
        this.maintainer = maintainer;
        this.provider = provider;
        this.type = type;
        this.id = name+"@"+maintainer+"_"+type;
        this.id = this.id.replace(' ','_');
        this.id = this.id.replace('/','_');
        this.path = path;

        decodeNeededPackages(n);
        if (n != null) {
            addMetaInfo(n);
        }
    }

    public String getId() {
        if (id==null) return "";
        return id;
    }

    public String getName() {
        if (name == null) return"";
        return name;
    }

    public Iterator getNeededPackages() {
        return neededpackages.values().iterator();
    }
    
    public String getVersion() {
        if (version == null) return("");
        return version;
    }

    public String getCreationDate() {
        if (date == null) return "";
        return date;
    }

    public String getMaintainer() {
        if (maintainer == null) return "";
        return maintainer;
    }

    public String getState() {
        if (InstallManager.isActive()) {
            if (this == InstallManager.getInstallingBundle()) {
                return "installing";
            }    
        }

        if (UninstallManager.isActive()) {
            if (this == UninstallManager.getUnInstallingBundle()) {
                return "uninstalling";
            }    
        }

        if (BundleManager.isInstalledVersion(this)) {
            return "installed";
        }

        if (state == null) return "";
        return state;
    }

    public String getType() {
        if (type==null) return "";
        return type;
    }

    public ProviderInterface getProvider() {
        return provider;
    }

    public boolean setState(String state) {
        this.state = state;
        return true;
    }


    public boolean install() {
        // step1
        installStep step = getNextInstallStep();
        step.setUserFeedBack("bundle/basic installer started");

        setProgressBar(1000); // lets have 100 steps;
        step = getNextInstallStep();
        step.setUserFeedBack("getting the mmb bundle... ");
        JarFile jf = getJarFile();
        if (jf != null) {
            step.setUserFeedBack("getting the mmb bundle...done ");
            increaseProgressBar(100); // downloading is 25%

            int pbs = 0; 
            // figure out how many packages we have for the progressbar
            Iterator d = getNeededPackages();
            while (d.hasNext()) {
                pbs++;
                d.next();
            }
            int pss = 800/pbs; // guess the progressbar per installed package

            boolean changed = true; // signals something was installed
            while (changed) {
                Iterator e = getNeededPackages();
                changed = false;
                while (e.hasNext()) {
                    Object o=e.next();
        	    if (o instanceof HashMap) {
	            } else {
    	            }
                    HashMap np = (HashMap)o;
                    String tmp=(String)np.get("id");
                    pkg = PackageManager.getPackage(tmp);
                    if (pkg != null) {
                        String state = pkg.getState();
                        String name = pkg.getName();
                        if (!state.equals("installed")) {
                            step = getNextInstallStep();
                            step.setUserFeedBack("calling package installer "+name+"..");
                            boolean ins = pkg.install(step);
                            if (ins) {
                                step.setUserFeedBack("calling package installer "+name+"...done");
                                increaseProgressBar(pss);
                                changed = true;
                            } else {
                                if (pkg.getDependsFailed()) {
                                    removeInstallStep(step);
                                } else {
                                    step.setUserFeedBack("calling package installer "+name+"...failed");
                                    return false;
                                }
                            }
                        }
                    } else {
                        log.error("Missing package on +"+np.get("id"));
                    }
                }            
            }

            step = getNextInstallStep();
            step.setUserFeedBack("updating mmbase registry ..");
            updateRegistryInstalled();
            increaseProgressBar(100);
            step.setUserFeedBack("updating mmbase registry ... done");
        } else {
            step.setUserFeedBack("getting the mmb bundle...failed (server down or removed disk ? )");
            step.setType(installStep.TYPE_ERROR);
            try {
                Thread.sleep(2000);
            } catch(Exception ee) {}
        }

        step=getNextInstallStep();
        step.setUserFeedBack("bundle/basic installer ended");
        return true;
    }

    public boolean uninstall() {
        try {
            // step1
            installStep step = getNextInstallStep();
            step.setUserFeedBack("bundle/basic uninstaller started");

            Iterator e = getNeededPackages();
                while (e.hasNext()) {
                    HashMap np = (HashMap)e.next();
                    PackageInterface pkg = PackageManager.getPackage((String)np.get("id"));
                    if (pkg != null) {
                        String state = pkg.getState();
                        String name = pkg.getName();
                        step=getNextInstallStep();
                        step.setUserFeedBack("calling package uninstaller "+name+"..");
                        if (pkg.uninstall(step)) {
                            step.setUserFeedBack("calling package uninstaller "+name+"...done");
                        } else {
                            step.setUserFeedBack("calling package uninstaller "+name+"...failed");
                        }
                    } else {
                        log.error("Missing package on +"+np.get("id"));
                    }
               }            

               // step 3
               step = getNextInstallStep();
               step.setUserFeedBack("updating mmbase registry ..");
               updateRegistryUninstalled();
               step.setUserFeedBack("updating mmbase registry ... done");

               // step 4
               step = getNextInstallStep();
               step.setUserFeedBack("bundle/basic installer ended");

           } catch (Exception e) {
               log.error("install crash on : "+this);
               return false;
           }
           return true;
    }

    public installStep getNextInstallStep() {
        // create new step
        installStep step=new installStep();
        if (installsteps==null) {
            installsteps=new ArrayList();
            installsteps.add(step);
            return step;
        } else {
            installsteps.add(step);
            return step;
        }
    }

    public boolean removeInstallStep(installStep step) {
        if (installsteps.contains(step)) {
            installsteps.remove(step);
            return true;
        } else {
            return false;
        }
    }

    public Iterator getInstallSteps() {
        if (installsteps != null) {
                return installsteps.iterator();
        } else {
                return null;
        }
    }

    public Iterator getInstallSteps(int logid) {
        // well maybe its one of my subs ?
        Iterator e = getInstallSteps();
        while (e.hasNext()) {
            installStep step = (installStep)e.next();
            Object o = step.getInstallSteps(logid);
            if (o != null) {
                return (Iterator)o;
            } 
        }
        return null;
    }

    public void clearInstallSteps() {
        installsteps = null;
    }

    public JarFile getJarFile() {
        if (provider != null) {
            JarFile jf=provider.getJarFile(getPath(),getId(),getVersion());
            return jf;
        }
        return null;    
    }

    public JarFile getIncludedPackageJarFile(String packageid,String packageversion) {
        if (provider != null) {
            return provider.getIncludedPackageJarFile(getPath(),getId(),getVersion(),packageid,packageversion);
        }
        return null;    
    }

    public BufferedInputStream getJarStream() {
        if (provider!=null) {
            return provider.getJarStream(getPath());
        }
        return null;    
    }

    public String getPath() {
        return path;
    }


    public boolean updateRegistryInstalled() {
        return BundleManager.updateRegistryInstalled(this);
    }

    public boolean updateRegistryUninstalled() {
        return BundleManager.updateRegistryUninstalled(this);
    }

    public void signalUpdate() {
        lastupdated=System.currentTimeMillis();
    }

    public long lastSeen() {
        return lastupdated;
    }

    private void decodeNeededPackages(org.w3c.dom.Node n) {
        org.w3c.dom.Node n2 = n.getFirstChild();
        while (n2 != null) {
            String name = n2.getNodeName();
            // this should me one way defined (remote or local)
            if (name.equals("neededpackages")) {
                org.w3c.dom.Node n3 = n2.getFirstChild();
                while (n3 != null) {
                    name = n3.getNodeName();
                    NamedNodeMap nm = n3.getAttributes();
                    if (nm != null) {
                        String maintainer = null;
                        String type = null;
                        String version = null;
                        String included = null;
             
                        // decode name
                        org.w3c.dom.Node n5 = nm.getNamedItem("name");
                        if (n5 != null) {
                            name = n5.getNodeValue();
                        }

                        // decode the type
                        n5 = nm.getNamedItem("type");
                        if (n5 != null) {
                            type = n5.getNodeValue();
                        }

                        // decode the maintainer 
                        n5 = nm.getNamedItem("maintainer");
                        if (n5 != null) {
                             maintainer = n5.getNodeValue();
                        }

                        // decode the version
                        n5 = nm.getNamedItem("version");
                        if (n5 != null) {
                            version = n5.getNodeValue();
                        }

                        String wid = name+"@"+maintainer+"_"+type;
                        wid = wid.replace(' ','_');
                        wid = wid.replace('/','_');

                        HashMap h = new HashMap();
                        h.put("name",name);
                        h.put("id",wid);
                        h.put("type",type);
                        h.put("maintainer",maintainer);
                        h.put("version",version);
                        neededpackages.put(wid,h);
                    }
                    n3 = n3.getNextSibling();
                }
            }
            // if in a remote file
            if (name.equals("includedpackages")) {
                org.w3c.dom.Node n3 = n2.getFirstChild();
                while (n3 != null) {
                    name = n3.getNodeName();
                    NamedNodeMap nm = n3.getAttributes();
                    if (nm != null) {
                        String maintainer = null;
                        String type = null;
                        String version = null;
                        boolean packed = false;
            
                        // decode name
                        org.w3c.dom.Node n5 = nm.getNamedItem("name");
                        if (n5 != null) {
                           name = n5.getNodeValue();
                        }

                        // decode the type
                        n5 = nm.getNamedItem("type");
                        if (n5 != null) {
                            type = n5.getNodeValue();
                        }

                        // decode the maintainer 
                        n5 = nm.getNamedItem("maintainer");
                        if (n5 != null) {
                            maintainer = n5.getNodeValue();
                        }

                        // decode the version
                        n5 = nm.getNamedItem("version");
                        if (n5 != null) {
                            version = n5.getNodeValue();
                        }

                        // decode the packed
                        n5 = nm.getNamedItem("packed");
                        if (n5 != null) {
                            if (n5.getNodeValue().equals("true")) packed = true;
                        }

                        String wid = name+"@"+maintainer+"_"+type;
                        wid = wid.replace(' ','_');
                        wid = wid.replace('/','_');

                        HashMap h = new HashMap();
                        h.put("name",name);
                        h.put("id",wid);
                        h.put("type",type);
                        h.put("maintainer",maintainer);
                        h.put("version",version);
                        neededpackages.put(wid,h);

                        // if its included add it to available packages
                    }
                    n3 = n3.getNextSibling();
                }
            }
            n2 = n2.getNextSibling();
        }
    }

   private void addMetaInfo(org.w3c.dom.Node n) {
       org.w3c.dom.Node n2 = n.getFirstChild();
       while (n2 != null) {
           if (n2.getNodeName().equals("description")) {
               org.w3c.dom.Node n3 = n2.getFirstChild();
               if (n3!=null) {
                   description = n3.getNodeValue();
               }
           } else if (n2.getNodeName().equals("releasenotes")) {
               org.w3c.dom.Node n3 = n2.getFirstChild();
               if (n3!=null) {
                   releasenotes = n3.getNodeValue();
               }
           } else if (n2.getNodeName().equals("installationnotes")) {
               org.w3c.dom.Node n3 = n2.getFirstChild();
               if (n3!=null) {
                   installationnotes = n3.getNodeValue();
               }
           } else if (n2.getNodeName().equals("license")) {
               org.w3c.dom.Node n3 = n2.getFirstChild();
               if (n3 != null) {
                   licensebody = n3.getNodeValue();
               }
               NamedNodeMap nm = n2.getAttributes();
               if (nm != null) {
                   // decode name
                   org.w3c.dom.Node n4 = nm.getNamedItem("name");
                   if (n4 != null) {
                       licensename = n4.getNodeValue();
                   }
                   // decode type
                   n4 = nm.getNamedItem("type");
                   if (n4 != null) {
                       licensetype = n4.getNodeValue();
                   }
                   // decode version
                   n4 = nm.getNamedItem("version");
                   if (n4 != null) {
                       licenseversion = n4.getNodeValue();
                   }
               }
           } else if (n2.getNodeName().equals("initiators")) {
               initiators = decodeRelatedPeople(n2,"initiator");
           } else if (n2.getNodeName().equals("supporters")) {
               supporters = decodeRelatedPeople(n2,"supporter");
           } else if (n2.getNodeName().equals("contacts")) {
               contacts = decodeRelatedPeople(n2,"contact");
           } else if (n2.getNodeName().equals("developers")) {
               developers = decodeRelatedPeople(n2,"developer");
           }
           n2 = n2.getNextSibling();
       }
   }

   public List getRelatedPeople(String type) {
      if (type.equals("initiators")) return initiators;
      if (type.equals("supporters")) return supporters;
      if (type.equals("developers")) return developers;
      if (type.equals("contacts")) return contacts;
      return null;
   }


   private ArrayList decodeRelatedPeople(org.w3c.dom.Node n,String type) {
       ArrayList list = new ArrayList();
       org.w3c.dom.Node n2 = n.getFirstChild();
       while (n2 != null) {
           if (n2.getNodeName().equals(type)) {
               Person p = new Person();
               NamedNodeMap nm = n2.getAttributes();
               if (nm != null) {
                   org.w3c.dom.Node n3 = nm.getNamedItem("name");
                   if (n3 != null) p.setName(n3.getNodeValue());
                   n3 = nm.getNamedItem("company");
                   if (n3 != null) p.setCompany(n3.getNodeValue());
                   n3 = nm.getNamedItem("reason");
                   if (n3 != null) p.setReason(n3.getNodeValue());
                   n3 = nm.getNamedItem("mailto");
                   if (n3 != null) p.setMailto(n3.getNodeValue());
               }
               list.add(p);
           }
           n2 = n2.getNextSibling();
       }
       return list;
   }


    public String getDescription() {
        return description;
    }
    
    public String getInstallationNotes() {
        return installationnotes;
    }
    
    public String getReleaseNotes() {
        return releasenotes;
    }

    public String getLicenseType() {
        return licensetype;
    }

    public String getLicenseName() {
        return licensename;
    }

    public String getLicenseVersion() {
        return licenseversion;
    }

    public String getLicenseBody() {
        return licensebody;
    }

    public void setProgressBar(int stepcount) {
        progressbar=1;
        progressstep=100/(float)stepcount;
    }

    public void increaseProgressBar() {
        increaseProgressBar(1);
    }

    public void increaseProgressBar(int stepcount) {
        progressbar += (stepcount*progressstep);
    }

   public int getProgressBarValue() {
       return (int)progressbar;
   }

   public int getPackageProgressBarValue() {
       if (pkg!=null) {
           return pkg.getProgressBarValue(); 
        }
        return 0;
   }
}
