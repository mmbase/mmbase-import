/*
 *  This software is OSI Certified Open Source Software.
 *  OSI Certified is a certification mark of the Open Source Initiative.
 *  The license (Mozilla version 1.0) can be read at the MMBase site.
 *  See http://www.MMBase.org/license
 */
package org.mmbase.applications.packaging.packagehandlers;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.mmbase.applications.packaging.InstallManager;
import org.mmbase.applications.packaging.PackageManager;
import org.mmbase.applications.packaging.Person;
import org.mmbase.applications.packaging.ShareManager;
import org.mmbase.applications.packaging.UninstallManager;
import org.mmbase.applications.packaging.bundlehandlers.BundleInterface;
import org.mmbase.applications.packaging.installhandlers.installStep;
import org.mmbase.applications.packaging.providerhandlers.DiskProvider;
import org.mmbase.applications.packaging.providerhandlers.ProviderInterface;
import org.mmbase.applications.packaging.util.ExtendedDocumentReader;
import org.mmbase.module.builders.Versions;
import org.mmbase.module.core.MMBase;
import org.mmbase.util.xml.EntityResolver;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.InputSource;

/**
 * BasicPackage, base class for packages
 *
 * @author     Daniel Ockeloen (MMBased)
 */
public class BasicPackage implements PackageInterface {
    private static Logger log = Logging.getLoggerInstance(BasicPackage.class);

    private String name;
    private String id;
    private String maintainer;
    private String version;
    private String date;
    private String type = "unknown/unknown";
    private String state = "not installed";
    private String path;
    private String description = null;
    private String releasenotes = "";
    private String installationnotes = "";
    private String licensename = "";
    private String licensetype = "";
    private String licenseversion = "";
    private String licensebody = "";
    private ProviderInterface provider;
    private installStep bundlestep;
    private boolean dependsfailed = false;
    private BundleInterface parentbundle = null;
    private ArrayList<Object> initiators, supporters, contacts, developers;
    private float progressbar = 0;
    private float progressstep = 1;

    // the install manager keeps track of what happend during a install
    // of a package or bundle. These are called steps because they not
    // only can provide log info but also possible fixed, feedback, stats
    // etc etc. Each step in itself can have steps again providing for things
    // like three style logging and feedback
    private ArrayList<installStep> installsteps;

    private long lastupdated;

    /**
     * DTD resource filename of the packagedepends DTD version 1.0
     */
    public final static String DTD_PACKAGEDEPENDS_1_0 = "packagedepends_1_0.dtd";
    /**
     *  Description of the Field
     */
    public final static String DTD_PACKAGE_1_0 = "package_1_0.dtd";

    /**
     * Public ID of the packagedepends DTD version 1.0
     */
    public final static String PUBLIC_ID_PACKAGEDEPENDS_1_0 = "-//MMBase//DTD packagedepends config 1.0//EN";
    /**
     *  Description of the Field
     */
    public final static String PUBLIC_ID_PACKAGE_1_0 = "-//MMBase//DTD package config 1.0//EN";



    /**
     * Register the Public Ids for DTDs used by DatabaseReader
     * This method is called by EntityResolver.
     */
    public static void registerPublicIDs() {
        EntityResolver.registerPublicID(PUBLIC_ID_PACKAGEDEPENDS_1_0, DTD_PACKAGEDEPENDS_1_0, ShareManager.class);
        EntityResolver.registerPublicID(PUBLIC_ID_PACKAGE_1_0, DTD_PACKAGE_1_0, DiskProvider.class);
    }


    /**
     *Constructor for the BasicPackage object
     */
    public BasicPackage() { }


    /**
     *  Description of the Method
     *
     * @param  n           Description of the Parameter
     * @param  provider    Description of the Parameter
     * @param  name        Description of the Parameter
     * @param  type        Description of the Parameter
     * @param  maintainer  Description of the Parameter
     * @param  version     Description of the Parameter
     * @param  date        Description of the Parameter
     * @param  path        Description of the Parameter
     */
    public void init(org.w3c.dom.Node n, ProviderInterface provider, String name, String type, String maintainer, String version, String date, String path) {
        this.name = name;
        this.version = version;
        this.date = date;
        this.maintainer = maintainer;
        this.provider = provider;
        this.type = type;
        this.id = name + "@" + maintainer + "_" + type;
        this.id = this.id.replace(' ', '_');
        this.id = this.id.replace('/', '_');
        this.path = path;
        if (n != null) {
            addMetaInfo(n);
        }
    }


    /**
     *  Gets the id attribute of the BasicPackage object
     *
     * @return    The id value
     */
    public String getId() {
        if (id == null) {
            return "";
        }
        return id;
    }


    /**
     *  Gets the name attribute of the BasicPackage object
     *
     * @return    The name value
     */
    public String getName() {
        if (name == null) {
            return "";
        }
        return name;
    }


    /**
     *  Gets the description attribute of the BasicPackage object
     *
     * @return    The description value
     */
    public String getDescription() {
        if (description == null) {
            delayedMetaInfo();
        }
        return description;
    }


    /**
     *  Gets the installationNotes attribute of the BasicPackage object
     *
     * @return    The installationNotes value
     */
    public String getInstallationNotes() {
        return installationnotes;
    }


    /**
     *  Gets the releaseNotes attribute of the BasicPackage object
     *
     * @return    The releaseNotes value
     */
    public String getReleaseNotes() {
        return releasenotes;
    }


    /**
     *  Gets the licenseType attribute of the BasicPackage object
     *
     * @return    The licenseType value
     */
    public String getLicenseType() {
        return licensetype;
    }


    /**
     *  Gets the licenseName attribute of the BasicPackage object
     *
     * @return    The licenseName value
     */
    public String getLicenseName() {
        return licensename;
    }


    /**
     *  Gets the licenseVersion attribute of the BasicPackage object
     *
     * @return    The licenseVersion value
     */
    public String getLicenseVersion() {
        return licenseversion;
    }


    /**
     *  Gets the licenseBody attribute of the BasicPackage object
     *
     * @return    The licenseBody value
     */
    public String getLicenseBody() {
        return licensebody;
    }


    /**
     *  Gets the version attribute of the BasicPackage object
     *
     * @return    The version value
     */
    public String getVersion() {
        if (version == null) {
            return ("");
        }
        return version;
    }


    /**
     *  Gets the creationDate attribute of the BasicPackage object
     *
     * @return    The creationDate value
     */
    public String getCreationDate() {
        if (date == null) {
            return "";
        }
        return date;
    }


    /**
     *  Gets the maintainer attribute of the BasicPackage object
     *
     * @return    The maintainer value
     */
    public String getMaintainer() {
        if (maintainer == null) {
            return "";
        }
        return maintainer;
    }


    /**
     *  Gets the state attribute of the BasicPackage object
     *
     * @return    The state value
     */
    public String getState() {
        if (InstallManager.isActive()) {
            if (this == InstallManager.getInstallingPackage()) {
                return "installing";
            }
        }

        if (UninstallManager.isActive()) {
            if (this == UninstallManager.getUnInstallingPackage()) {
                return "uninstalling";
            }
        }

        if (PackageManager.isInstalledVersion(this)) {
            return "installed";
        }

        if (PackageManager.upgradeAvailable(this)) {
            return "upgrade available";
        }

        if (state == null) {
            return "";
        }
        return state;
    }


    /**
     *  Gets the type attribute of the BasicPackage object
     *
     * @return    The type value
     */
    public String getType() {
        if (type == null) {
            return "";
        }
        return type;
    }


    /**
     *  Gets the provider attribute of the BasicPackage object
     *
     * @return    The provider value
     */
    public ProviderInterface getProvider() {
        return provider;
    }


    /**
     *  Sets the state attribute of the BasicPackage object
     *
     * @param  state  The new state value
     * @return        Description of the Return Value
     */
    public boolean setState(String state) {
        this.state = state;
        return true;
    }


    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public boolean install() {
        log.error("this package doesn't implement the install() call");
        return false;
    }


    /**
     *  Description of the Method
     *
     * @param  step  Description of the Parameter
     * @return       Description of the Return Value
     */
    public boolean install(installStep step) {
        bundlestep = step;
        return install();
    }


    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public boolean uninstall() {
        log.error("this package doesn't implement the uninstall() call");
        return false;
    }


    /**
     *  Description of the Method
     *
     * @param  step  Description of the Parameter
     * @return       Description of the Return Value
     */
    public boolean uninstall(installStep step) {
        bundlestep = step;
        return uninstall();
    }


    /**
     *  Gets the nextInstallStep attribute of the BasicPackage object
     *
     * @return    The nextInstallStep value
     */
    public installStep getNextInstallStep() {
        installStep step = null;
        if (bundlestep != null) {
            step = bundlestep.getNextInstallStep();
        } else {
            // create new step
            step = new installStep();
        }
        if (installsteps == null) {
            installsteps = new ArrayList<installStep>();
            installsteps.add(step);
            return step;
        } else {
            installsteps.add(step);
            return step;
        }
    }


    /**
     *  Gets the installSteps attribute of the BasicPackage object
     *
     * @return    The installSteps value
     */
    public Iterator<installStep> getInstallSteps() {
        if (installsteps != null) {
            return installsteps.iterator();
        } else {
            return null;
        }
    }


    /*
     *  public Enumeration getInstallSteps(int logid) {
     *  Enumeration e=getInstallSteps();
     *  while (e.hasMoreElements()) {
     *  installStep step=(installStep)e.nextElement();
     *  if (step.getId()==logid) {
     *  return step.getInstallSteps();
     *  }
     *  }
     *  return null;
     *  }
     */
    /**
     *  Gets the installSteps attribute of the BasicPackage object
     *
     * @param  logid  Description of the Parameter
     * @return        The installSteps value
     */
    public Iterator<installStep> getInstallSteps(int logid) {
        // well maybe its one of my subs ?
        Iterator<installStep> e = getInstallSteps();
        while (e.hasNext()) {
            installStep step = e.next();
            Object o = step.getInstallSteps(logid);
            if (o != null) {
                return (Iterator<installStep>) o;
            }
        }
        return null;
    }


    /**
     *  Description of the Method
     */
    public void clearInstallSteps() {
        installsteps = null;
    }


    /**
     *  Gets the jarFile attribute of the BasicPackage object
     *
     * @return    The jarFile value
     */
    public JarFile getJarFile() {
        if (provider != null) {
            if (parentbundle != null) {
                return parentbundle.getIncludedPackageJarFile(getId(), getVersion());
            } else {
                return provider.getJarFile(getPath(), getId(), getVersion());
            }
        }
        return null;
    }


    /**
     *  Gets the jarStream attribute of the BasicPackage object
     *
     * @return    The jarStream value
     */
    public BufferedInputStream getJarStream() {
        if (provider != null) {
            return provider.getJarStream(getPath());
        }
        return null;
    }


    /**
     *  Gets the path attribute of the BasicPackage object
     *
     * @return    The path value
     */
    public String getPath() {
        return path;
    }


    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public boolean updateRegistryInstalled() {
        return PackageManager.updateRegistryInstalled(this);
    }


    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public boolean updateRegistryUninstalled() {
        return PackageManager.updateRegistryUninstalled(this);
    }


    /**
     *  Description of the Method
     */
    public void signalUpdate() {
        lastupdated = System.currentTimeMillis();
    }


    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public long lastSeen() {
        return lastupdated;
    }


    /**
     *  Description of the Method
     *
     * @param  jf    Description of the Parameter
     * @param  step  Description of the Parameter
     * @return       Description of the Return Value
     */
    public boolean dependsInstalled(JarFile jf, installStep step) {
        Versions versions = (Versions) MMBase.getMMBase().getMMObject("versions");
        dependsfailed = false;
        if (versions == null) {
            log.error("Versions builder not installed.");
            return false;
        }
        try {
            JarEntry je = jf.getJarEntry("depends.xml");
            if (je != null) {
                InputStream input = jf.getInputStream(je);
                ExtendedDocumentReader reader = new ExtendedDocumentReader(new InputSource(input), BasicPackage.class);
                for (Element n: reader.getChildElements("packagedepends", "package")) {
                    String name = n.getAttribute("name");
                    String type = n.getAttribute("type");
                    String version = n.getAttribute("version");
                    String versionmode = n.getAttribute("versionmode");
                    String maintainer = n.getAttribute("maintainer");
                    //log.info("depends name "+name+" "+type+" "+version+" "+versionmode+" "+maintainer);
                    installStep substep = step.getNextInstallStep();
                    substep.setUserFeedBack("checking package : " + name + " (" + type + ") version : " + version + " (" + versionmode + ") from : " + maintainer);

                    String id = name + "@" + maintainer + "_" + type;
                    id = id.replace(' ', '_');
                    id = id.replace('/', '_');
                    int installedversion = versions.getInstalledVersion(id, "package");
                    // if not installed at all then well for sure
                    // a negative
                    if (installedversion == -1) {
                        substep.setUserFeedBack("depends failed package : " + name + " (" + type + ") version : " + version + " (" + versionmode + ") from : " + maintainer);
                        substep.setType(installStep.TYPE_ERROR);
                        dependsfailed = true;
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


    /**
     *  Gets the dependsFailed attribute of the BasicPackage object
     *
     * @return    The dependsFailed value
     */
    public boolean getDependsFailed() {
        return dependsfailed;
    }


    /**
     *  Gets the parentBundle attribute of the BasicPackage object
     *
     * @return    The parentBundle value
     */
    public BundleInterface getParentBundle() {
        return parentbundle;
    }


    /**
     *  Sets the parentBundle attribute of the BasicPackage object
     *
     * @param  parent  The new parentBundle value
     */
    public void setParentBundle(BundleInterface parent) {
        parentbundle = parent;
    }


    /**
     *  Description of the Method
     */
    private void delayedMetaInfo() {
        // needs to be smarter now i need to unzip in anyway :(
        // to get the package.xml to get the meta info
        JarFile jf = getJarFile();
        // open the jar to read the input xml
        try {
            JarEntry je = jf.getJarEntry("package.xml");
            if (je != null) {
                InputStream input = jf.getInputStream(je);
                ExtendedDocumentReader reader = new ExtendedDocumentReader(new InputSource(input), DiskProvider.class);
                if (reader != null) {
                    Element e = reader.getElementByPath("package");
                    addMetaInfo(e);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     *  Adds a feature to the MetaInfo attribute of the BasicPackage object
     *
     * @param  n  The feature to be added to the MetaInfo attribute
     */
    private void addMetaInfo(org.w3c.dom.Node n) {
        org.w3c.dom.Node n2 = n.getFirstChild();
        while (n2 != null) {
            String type = n2.getNodeName();
            if (type != null) {
                if (type.equals("description")) {
                    org.w3c.dom.Node n3 = n2.getFirstChild();
                    if (n3 != null) {
                        description = n3.getNodeValue();
                    }
                } else if (type.equals("releasenotes")) {
                    org.w3c.dom.Node n3 = n2.getFirstChild();
                    if (n3 != null) {
                        releasenotes = n3.getNodeValue();
                    }
                } else if (type.equals("installationnotes")) {
                    org.w3c.dom.Node n3 = n2.getFirstChild();
                    if (n3 != null) {
                        installationnotes = n3.getNodeValue();
                    }
                } else if (type.equals("license")) {
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
                } else if (type.equals("initiators")) {
                    initiators = decodeRelatedPeople(n2, "initiator");
                } else if (type.equals("supporters")) {
                    supporters = decodeRelatedPeople(n2, "supporter");
                } else if (type.equals("contacts")) {
                    contacts = decodeRelatedPeople(n2, "contact");
                } else if (type.equals("developers")) {
                    developers = decodeRelatedPeople(n2, "developer");
                }
            }
            n2 = n2.getNextSibling();
        }
    }


    /**
     *  Gets the relatedPeople attribute of the BasicPackage object
     *
     * @param  type  Description of the Parameter
     * @return       The relatedPeople value
     */
    public List<Object> getRelatedPeople(String type) {
        if (type.equals("initiators")) {
            return initiators;
        }
        if (type.equals("supporters")) {
            return supporters;
        }
        if (type.equals("developers")) {
            return developers;
        }
        if (type.equals("contacts")) {
            return contacts;
        }
        return null;
    }


    /**
     *  Description of the Method
     *
     * @param  n     Description of the Parameter
     * @param  type  Description of the Parameter
     * @return       Description of the Return Value
     */
    private ArrayList<Object> decodeRelatedPeople(org.w3c.dom.Node n, String type) {
        ArrayList<Object> list = new ArrayList<Object>();
        org.w3c.dom.Node n2 = n.getFirstChild();
        while (n2 != null) {
            if (n2.getNodeName().equals(type)) {
                Person p = new Person();
                NamedNodeMap nm = n2.getAttributes();
                if (nm != null) {
                    org.w3c.dom.Node n3 = nm.getNamedItem("name");
                    if (n3 != null) {
                        p.setName(n3.getNodeValue());
                    }
                    n3 = nm.getNamedItem("company");
                    if (n3 != null) {
                        p.setCompany(n3.getNodeValue());
                    }
                    n3 = nm.getNamedItem("reason");
                    if (n3 != null) {
                        p.setReason(n3.getNodeValue());
                    }
                    n3 = nm.getNamedItem("mailto");
                    if (n3 != null) {
                        p.setMailto(n3.getNodeValue());
                    }
                }
                list.add(p);
            }
            n2 = n2.getNextSibling();
        }
        return list;
    }


    /**
     *  Sets the progressBar attribute of the BasicPackage object
     *
     * @param  stepcount  The new progressBar value
     */
    public void setProgressBar(int stepcount) {
        progressbar = 1;
        progressstep = 100 / (float) stepcount;
    }


    /**
     *  Description of the Method
     */
    public void increaseProgressBar() {
        increaseProgressBar(1);
    }


    /**
     *  Description of the Method
     *
     * @param  stepcount  Description of the Parameter
     */
    public void increaseProgressBar(int stepcount) {
        progressbar += (stepcount * progressstep);
    }


    /**
     *  Gets the progressBarValue attribute of the BasicPackage object
     *
     * @return    The progressBarValue value
     */
    public int getProgressBarValue() {
        return (int) progressbar;
    }

    public installStep getBundleStep() {
    return bundlestep;
    }

}

