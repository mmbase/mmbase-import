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
import org.mmbase.applications.packaging.bundlehandlers.*;
import org.mmbase.applications.packaging.providerhandlers.*;
import org.mmbase.applications.packaging.installhandlers.*;
import org.mmbase.applications.packaging.sharehandlers.*;

import java.io.*;
import java.util.*;
import java.util.jar.*;

import org.w3c.dom.*;
import org.xml.sax.*;

/**
 * BasicPackage, base class for packages
 *
 * @author Daniel Ockeloen (MMBased)
 */
public class BasicPackage implements PackageInterface {
    private static Logger log = Logging.getLoggerInstance(BasicPackage.class);
    
    private String name;
    private String id;
    private String maintainer;
    private String version;
    private String date;
    private String type="unknown/unknown";
    private String state="not installed";
    private String path;
    private String description=null;
    private String releasenotes="";
    private String installationnotes="";
    private String licensename="";
    private String licensetype="";
    private String licenseversion="";
    private String licensebody="";
    private ProviderInterface provider;
    private ShareInfo shareinfo;
    private installStep bundlestep;
    private boolean dependsfailed=false;
    private BundleInterface parentbundle=null;
    private ArrayList initiators,supporters,contacts,developers;
    private float progressbar=0;
    private float progressstep=1;


    // the install manager keeps track of what happend during a install
    // of a package or bundle. These are called steps because they not
    // only can provide log info but also possible fixed, feedback, stats
    // etc etc. Each step in itself can have steps again providing for things
    // like three style logging and feedback
    private Vector installsteps;

    private long lastupdated;

    /** DTD resource filename of the packagedepends DTD version 1.0 */
    public static final String DTD_PACKAGEDEPENDS_1_0 = "packagedepends_1_0.dtd";
    public static final String DTD_PACKAGE_1_0 = "package_1_0.dtd";

    /** Public ID of the packagedepends DTD version 1.0 */
    public static final String PUBLIC_ID_PACKAGEDEPENDS_1_0 = "-//MMBase//DTD packagedepends config 1.0//EN";
    public static final String PUBLIC_ID_PACKAGE_1_0 = "-//MMBase//DTD package config 1.0//EN";



    /**
     * Register the Public Ids for DTDs used by DatabaseReader
     * This method is called by XMLEntityResolver.
     */
    public static void registerPublicIDs() {
        XMLEntityResolver.registerPublicID(PUBLIC_ID_PACKAGEDEPENDS_1_0, DTD_PACKAGEDEPENDS_1_0, ShareManager.class);
        XMLEntityResolver.registerPublicID(PUBLIC_ID_PACKAGE_1_0, DTD_PACKAGE_1_0, DiskProvider.class); 
    }


    public BasicPackage() {
    }

    public void init(org.w3c.dom.Node n,ProviderInterface provider,String name,String type,String maintainer,String version, String date,String path) {
	this.name=name;
	this.version=version;
	this.date=date;
	this.maintainer=maintainer;
	this.provider=provider;
	this.type=type;
	this.id=name+"@"+maintainer+"_"+type;
	this.id=this.id.replace(' ','_');
	this.id=this.id.replace('/','_');
	this.path=path;
	if (n!=null) {
		addMetaInfo(n);
	}
    }

    public String getId() {
	if (id==null) return "";
	return id;
    }

    public String getName() {
	if (name==null) return"";
	return name;
    }

    public String getDescription() {
	if (description==null) {
		delayedMetaInfo();
	}
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

    public String getVersion() {
	if (version==null) return("");
	return version;
    }

    public String getCreationDate() {
	if (date==null) return "";
	return date;
    }

    public String getMaintainer() {
	if (maintainer==null) return "";
	return maintainer;
    }

    public String getState() {
	if (InstallManager.isActive()) {
		if (this==InstallManager.getInstallingPackage()) {
			return "installing";
		}	
	}

        if (UninstallManager.isActive()) {
                if (this==UninstallManager.getUnInstallingPackage()) {
                        return "uninstalling";
                }
        }


	if (PackageManager.isInstalledVersion(this)) {
		return "installed";
	}


	if (state==null) return "";
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
	this.state=state;
	return true;
    }

    public boolean install() {
	log.error("this package doesn't implement the install() call");
	return false;
    }

    public boolean install(installStep step) {
	bundlestep=step;
	return install();
    }

    public boolean uninstall() {
	log.error("this package doesn't implement the uninstall() call");
	return false;
    }

    public boolean uninstall(installStep step) {
	bundlestep=step;
	return uninstall();
    }

    public installStep getNextInstallStep() {
	installStep step=null;
	if (bundlestep!=null) {
		step=bundlestep.getNextInstallStep();
	} else {
		// create new step
		step=new installStep();
	}
	if (installsteps==null) {
		installsteps=new Vector();
		installsteps.addElement(step);
		return step;
	} else {
		installsteps.addElement(step);
		return step;
	}
    }

    public Enumeration getInstallSteps() {
	if (installsteps!=null) {
		return installsteps.elements();
	} else {
		return null;
	}
    }


	/*
    public Enumeration getInstallSteps(int logid) {
    	Enumeration e=getInstallSteps();
	while (e.hasMoreElements()) {
		installStep step=(installStep)e.nextElement();
		if (step.getId()==logid) {
			return step.getInstallSteps();
		}
	}
	return null;
    }
	*/

    public Enumeration getInstallSteps(int logid) {
	// well maybe its one of my subs ?
    	Enumeration e=getInstallSteps();
	while (e.hasMoreElements()) {
		installStep step=(installStep)e.nextElement();
		Object o=step.getInstallSteps(logid);
		if (o!=null) {
			return (Enumeration)o;
		}
	}
	return null;
    }

    public void clearInstallSteps() {
	installsteps=null;
    }

    public JarFile getJarFile() {
	if (provider!=null) {
		if (parentbundle!=null) {
			return parentbundle.getIncludedPackageJarFile(getId(),getVersion());
		} else {
			return provider.getJarFile(getPath(),getId(),getVersion());
		}
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
	return PackageManager.updateRegistryInstalled(this);
    }

    public boolean updateRegistryUninstalled() {
	return PackageManager.updateRegistryUninstalled(this);
    }

    public void signalUpdate() {
	lastupdated=System.currentTimeMillis();
    }

    public long lastSeen() {
	return lastupdated;
    }

    public boolean dependsInstalled(JarFile jf,installStep step) {
        Versions versions = (Versions) MMBase.getMMBase().getMMObject("versions");
	dependsfailed=false;
        if(versions==null) {
            log.error("Versions builder not installed.");
	    return false;
	}
	try {
   	   	JarEntry je = jf.getJarEntry("depends.xml");
		if (je!=null) {
			InputStream input = jf.getInputStream(je);
          		XMLBasicReader reader = new XMLBasicReader(new InputSource(input),BasicPackage.class);
       			 for(Enumeration ns=reader.getChildElements("packagedepends","package");ns.hasMoreElements(); ) {
            		    Element n=(Element)ns.nextElement();
           		    String name=n.getAttribute("name");
           		    String type=n.getAttribute("type");
           		    String version=n.getAttribute("version");
           		    String versionmode=n.getAttribute("versionmode");
           		    String maintainer=n.getAttribute("maintainer");
			    //log.info("depends name "+name+" "+type+" "+version+" "+versionmode+" "+maintainer);
		             installStep substep=step.getNextInstallStep();
               		     substep.setUserFeedBack("checking package : "+name+" ("+type+") version : "+version+" ("+versionmode+") from : "+maintainer);

			    String id=name+"@"+maintainer+"_"+type;
		  	    id=id.replace(' ','_');
			    id=id.replace('/','_');
	    		    int installedversion=versions.getInstalledVersion(id,"package");	
			    // if not installed at all then well for sure
			    // a negative
			    if (installedversion==-1) {
               		    	substep.setUserFeedBack("depends failed package : "+name+" ("+type+") version : "+version+" ("+versionmode+") from : "+maintainer);
				substep.setType(installStep.TYPE_ERROR);
				dependsfailed=true;
				return false;
			    }
			}
		} else {
			return true;
		}
	} catch (Exception e) {
		return false;
	}
	return true;
    }

    public boolean getDependsFailed() {
	return dependsfailed;
    }


    public BundleInterface getParentBundle() {
	return parentbundle;
    }

    public void setParentBundle(BundleInterface parent) {
	parentbundle=parent;
    }

   private void delayedMetaInfo() {
	// needs to be smarter now i need to unzip in anyway :(
	// to get the package.xml to get the meta info
	JarFile jf=getJarFile();
	// open the jar to read the input xml
	try {
       		JarEntry je = jf.getJarEntry("package.xml");
		if (je!=null) {
			InputStream input = jf.getInputStream(je);
            		XMLBasicReader reader = new XMLBasicReader(new InputSource(input),DiskProvider.class);
            		if(reader!=null) {
				Element e=reader.getElementByPath("package");
				addMetaInfo(e);
			}
		}
	} catch(Exception e) {
		e.printStackTrace();
	}

    }

	
   private void addMetaInfo(org.w3c.dom.Node n) {
       org.w3c.dom.Node n2=n.getFirstChild();
       while (n2!=null) {
	String type=n2.getNodeName();
	if (type!=null) {
         if (type.equals("description")) {
              org.w3c.dom.Node n3=n2.getFirstChild();
	      if (n3!=null) description=n3.getNodeValue();
         } else if (type.equals("releasenotes")) {
              org.w3c.dom.Node n3=n2.getFirstChild();
	      if (n3!=null) releasenotes=n3.getNodeValue();
         } else if (type.equals("installationnotes")) {
              org.w3c.dom.Node n3=n2.getFirstChild();
	      if (n3!=null) installationnotes=n3.getNodeValue();
         } else if (type.equals("license")) {
              org.w3c.dom.Node n3=n2.getFirstChild();
	      if (n3!=null) {
	      	licensebody=n3.getNodeValue();
	      }
              NamedNodeMap nm=n2.getAttributes();
              if (nm!=null) {
			// decode name
                        org.w3c.dom.Node n4=nm.getNamedItem("name");
                       	if (n4!=null) {
				licensename=n4.getNodeValue();
			}
			// decode type
                        n4=nm.getNamedItem("type");
                       	if (n4!=null) {
				licensetype=n4.getNodeValue();
			}
			// decode version
                        n4=nm.getNamedItem("version");
                       	if (n4!=null) {
				licenseversion=n4.getNodeValue();
			}
	      }
         } else if (type.equals("initiators")) {
		initiators=decodeRelatedPeople(n2,"initiator");
         } else if (type.equals("supporters")) {
		supporters=decodeRelatedPeople(n2,"supporter");
         } else if (type.equals("contacts")) {
		contacts=decodeRelatedPeople(n2,"contact");
         } else if (type.equals("developers")) {
		developers=decodeRelatedPeople(n2,"developer");
	 }
       }
       n2=n2.getNextSibling();
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
	ArrayList list=new ArrayList();
        org.w3c.dom.Node n2=n.getFirstChild();
        while (n2!=null) {
         	if (n2.getNodeName().equals(type)) {
			Person p=new Person();
              		NamedNodeMap nm=n2.getAttributes();
              		if (nm!=null) {
                        	org.w3c.dom.Node n3=nm.getNamedItem("name");
                       		if (n3!=null) p.setName(n3.getNodeValue());
                        	n3=nm.getNamedItem("company");
                       		if (n3!=null) p.setCompany(n3.getNodeValue());
                        	n3=nm.getNamedItem("reason");
                       		if (n3!=null) p.setReason(n3.getNodeValue());
                        	n3=nm.getNamedItem("mailto");
                       		if (n3!=null) p.setMailto(n3.getNodeValue());
			}
			list.add(p);
		}
         	n2=n2.getNextSibling();
	}
	return list;
   }

    public void setProgressBar(int stepcount) {
        progressbar=1;
        progressstep=100/(float)stepcount;
    }

    public void increaseProgressBar() {
        increaseProgressBar(1);
    }

    public void increaseProgressBar(int stepcount) {
        progressbar+=(stepcount*progressstep);
    }

   public int getProgressBarValue() {
        return (int)progressbar;
   }


}
