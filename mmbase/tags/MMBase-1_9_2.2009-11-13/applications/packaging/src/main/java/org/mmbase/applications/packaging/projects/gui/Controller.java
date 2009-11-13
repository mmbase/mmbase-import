/*
 *  This software is OSI Certified Open Source Software.
 *  OSI Certified is a certification mark of the Open Source Initiative.
 *  The license (Mozilla version 1.0) can be read at the MMBase site.
 *  See http://www.MMBase.org/license
 */
package org.mmbase.applications.packaging.projects.gui;

import java.util.*;

import org.mmbase.applications.packaging.InstallManager;
import org.mmbase.applications.packaging.PackageManager;
import org.mmbase.applications.packaging.Person;
import org.mmbase.applications.packaging.ProjectManager;
import org.mmbase.applications.packaging.packagehandlers.PackageInterface;
import org.mmbase.applications.packaging.projects.IncludedPackage;
import org.mmbase.applications.packaging.projects.PackageDepend;
import org.mmbase.applications.packaging.projects.Project;
import org.mmbase.applications.packaging.projects.Target;
import org.mmbase.applications.packaging.projects.packageStep;
import org.mmbase.applications.packaging.projects.creators.CreatorInterface;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.CloudContext;
import org.mmbase.bridge.LocalContext;
import org.mmbase.bridge.NodeManager;
import org.mmbase.module.core.MMBase;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.module.core.VirtualBuilder;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @author     Daniel Ockeloen
 * @created    July 20, 2004
 * @version    $Id$
 */
public class Controller {

    private static Logger log = Logging.getLoggerInstance(Controller.class);
    private static Cloud cloud;
    NodeManager manager;
    CloudContext context;


    /**
     *Constructor for the Controller object
     */
    public Controller() {
        cloud = LocalContext.getCloudContext().getCloud("mmbase");

        // hack needs to be solved
        manager = cloud.getNodeManager("typedef");
        if (manager == null) {
            log.error("Can't access builder typedef");
        }
        context = LocalContext.getCloudContext();
        if (!InstallManager.isRunning()) {
            InstallManager.init();
        }
    }


    /**
     *  Gets the projectBundleTargets attribute of the Controller object
     *
     * @param  name  Description of the Parameter
     * @return       The projectBundleTargets value
     */
    public List<MMObjectNode> getProjectBundleTargets(String name) {
        List<MMObjectNode> list = new ArrayList<MMObjectNode>();
        Project p = ProjectManager.getProject(name);
        if (p != null) {
            Iterator<Target> targets = p.getBundleTargets();

            VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
            while (targets.hasNext()) {
                Target t = targets.next();
                MMObjectNode virtual = builder.getNewNode("admin");
                virtual.setValue("name", t.getName());
                virtual.setValue("type", t.getType());
                virtual.setValue("path", t.getPath());
                virtual.setValue("syntaxerrors", t.hasSyntaxErrors());
                list.add(virtual);
            }
        }
        return list;
    }


    /**
     *  Gets the projectPackageTargets attribute of the Controller object
     *
     * @param  name  Description of the Parameter
     * @return       The projectPackageTargets value
     */
    public List<MMObjectNode> getProjectPackageTargets(String name) {
        List<MMObjectNode> list = new ArrayList<MMObjectNode>();
        Project p = ProjectManager.getProject(name);
        if (p != null) {
            Iterator<Target> targets = p.getPackageTargets();

            VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
            while (targets.hasNext()) {
                Target t = targets.next();
                MMObjectNode virtual = builder.getNewNode("admin");
                virtual.setValue("name", t.getName());
                virtual.setValue("type", t.getType());
                virtual.setValue("path", t.getPath());
                virtual.setValue("syntaxerrors", t.hasSyntaxErrors());
                list.add(virtual);
            }
        }
        return list;
    }


    /**
     *  Gets the projectTargets attribute of the Controller object
     *
     * @param  name  Description of the Parameter
     * @return       The projectTargets value
     */
    public List<MMObjectNode> getProjectTargets(String name) {
        List<MMObjectNode> list = new ArrayList<MMObjectNode>();
        Project p = ProjectManager.getProject(name);
        if (p != null) {
            Iterator<Target> targets = p.getTargets();

            VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
            while (targets.hasNext()) {
                Target t = targets.next();
                MMObjectNode virtual = builder.getNewNode("admin");
                virtual.setValue("name", t.getName());
                virtual.setValue("depends", t.getDepends());
                list.add(virtual);
            }
        }
        return list;
    }


    /**
     *  Gets the targetPackageSteps attribute of the Controller object
     *
     * @param  project  Description of the Parameter
     * @param  target   Description of the Parameter
     * @param  logid    Description of the Parameter
     * @return          The targetPackageSteps value
     */
    public List<MMObjectNode> getTargetPackageSteps(String project, String target, int logid) {
        List<MMObjectNode> list = new ArrayList<MMObjectNode>();
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
        Project p = ProjectManager.getProject(project);
        if (p != null) {
            Target t = p.getTarget(target);
            if (t != null) {
                Iterator<packageStep> steps = null;
                if (logid == -1) {
                    steps = t.getPackageSteps();
                } else {
                    steps = t.getPackageSteps(logid);
                }
                if (steps != null) {
                    while (steps.hasNext()) {
                        packageStep step = steps.next();
                        MMObjectNode virtual = builder.getNewNode("admin");
                        virtual.setValue("userfeedback", step.getUserFeedBack());
                        virtual.setValue("timestamp", step.getTimeStamp());
                        virtual.setValue("errorcount", step.getErrorCount());
                        virtual.setValue("warningcount", step.getWarningCount());
                        virtual.setValue("id", step.getId());
                        virtual.setValue("parent", step.getParent());
                        if (step.hasChilds()) {
                            virtual.setValue("haschilds", "true");
                        } else {
                            virtual.setValue("haschilds", "false");
                        }
                        list.add(virtual);
                    }
                }
            }
        }
        return list;
    }


    /**
     *  Description of the Method
     *
     * @param  project  Description of the Parameter
     * @param  target   Description of the Parameter
     * @return          Description of the Return Value
     */
    public List<MMObjectNode> haveTargetLog(String project, String target) {
        List<MMObjectNode> list = new ArrayList<MMObjectNode>();
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
        MMObjectNode virtual = builder.getNewNode("admin");
        Project p = ProjectManager.getProject(project);
        if (p != null) {
            Target t = p.getTarget(target);
            if (t != null) {
                Iterator<packageStep> steps = t.getPackageSteps();

                if (steps != null) {
                    virtual.setValue("log", "true");
                } else {
                    virtual.setValue("log", "false");
                }
		virtual.setValue("state",t.getState());
		virtual.setValue("createprogressbarvalue",t.getProgressBarValue());
		virtual.setValue("createsubprogressbarvalue",t.getSubProgressBarValue());
            }
            list.add(virtual);
        }
        return list;
    }


    /**
     *  Gets the targetPackageDepends attribute of the Controller object
     *
     * @param  project  Description of the Parameter
     * @param  target   Description of the Parameter
     * @return          The targetPackageDepends value
     */
    public List<MMObjectNode> getTargetPackageDepends(String project, String target) {
        List<MMObjectNode> list = new ArrayList<MMObjectNode>();
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
        Project pr = ProjectManager.getProject(project);
        if (pr != null) {
            Target t = pr.getTarget(target);
            if (t != null) {
                ArrayList<PackageDepend> in = t.getPackageDepends();
                if (in != null) {
                    for (PackageDepend p : in) {
                        MMObjectNode virtual = builder.getNewNode("admin");
                        virtual.setValue("name", p.getName());
                        virtual.setValue("maintainer", p.getMaintainer());
                        virtual.setValue("version", p.getVersion());
                        virtual.setValue("versionmode", p.getVersionMode());
                        virtual.setValue("type", p.getType());
                        virtual.setValue("id", p.getId());
                        list.add(virtual);
                    }
                }
            }
        }
        return list;
    }


    /**
     *  Gets the projectTargetInfo attribute of the Controller object
     *
     * @param  project  Description of the Parameter
     * @param  target   Description of the Parameter
     * @return          The projectTargetInfo value
     */
    public MMObjectNode getProjectTargetInfo(String project, String target) {
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
        MMObjectNode virtual = builder.getNewNode("admin");
        Project p = ProjectManager.getProject(project);
        if (p != null) {
            Target t = p.getTarget(target);
            if (t != null) {
                virtual.setValue("lastversion", t.getLastVersion());
                virtual.setValue("nextversion", t.getNextVersion());
                virtual.setValue("lastdate", "" + t.getLastDate());
                virtual.setValue("description", t.getDescription());
                virtual.setValue("maintainer", t.getMaintainer());
                virtual.setValue("syntaxerrors", t.hasSyntaxErrors());
                virtual.setValue("publishprovider", t.getPublishProvider());
                virtual.setValue("publishstate", t.getPublishState());
                virtual.setValue("publishsharepassword", t.getPublishSharePassword());
                if (t.getRelatedPeople("initiators") == null || t.getRelatedPeople("initiators").size() == 0) {
                    virtual.setValue("haveinitiators", "false");
                } else {
                    virtual.setValue("haveinitiators", "true");
                }
                if (t.getRelatedPeople("developers") == null || t.getRelatedPeople("developers").size() == 0) {
                    virtual.setValue("havedevelopers", "false");
                } else {
                    virtual.setValue("havedevelopers", "true");
                }
                if (t.getRelatedPeople("contacts") == null || t.getRelatedPeople("contacts").size() == 0) {
                    virtual.setValue("havecontacts", "false");
                } else {
                    virtual.setValue("havecontacts", "true");
                }
                virtual.setValue("name", t.getName());
                if (t.isBundle()) {
                    virtual.setValue("bundlename", t.getPackageName());
                } else {
                    virtual.setValue("packagename", t.getPackageName());
                }
                virtual.setValue("type", t.getType());
                virtual.setValue("licensetype", t.getLicenseType());
                virtual.setValue("releasenotes", t.getReleaseNotes());
                virtual.setValue("installationnotes", t.getInstallationNotes());
                virtual.setValue("licensename", t.getLicenseName());
                virtual.setValue("licenseversion", t.getLicenseVersion());

		// get info
            }
        }
        return virtual;
    }


    /**
     *  Gets the projectInfo attribute of the Controller object
     *
     * @param  project  Description of the Parameter
     * @return          The projectInfo value
     */
    public MMObjectNode getProjectInfo(String project) {
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
        MMObjectNode virtual = builder.getNewNode("admin");
        Project p = ProjectManager.getProject(project);
        if (p != null) {
            virtual.setValue("name", p.getName());
            virtual.setValue("path", "" + p.getPath());
            virtual.setValue("dir", "" + p.getDir());
        }
        return virtual;
    }


    /**
     *  Gets the targetIncludedPackages attribute of the Controller object
     *
     * @param  project  Description of the Parameter
     * @param  target   Description of the Parameter
     * @return          The targetIncludedPackages value
     */
    public List<MMObjectNode> getTargetIncludedPackages(String project, String target) {
        List<MMObjectNode> list = new ArrayList<MMObjectNode>();
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
        Project p = ProjectManager.getProject(project);
        if (p != null) {
            Target t = p.getTarget(target);
            if (t != null) {
                ArrayList<IncludedPackage> in = t.getIncludedPackages();
                if (in != null) {
                    for (IncludedPackage ip : in) {
                        MMObjectNode virtual = builder.getNewNode("admin");
                        virtual.setValue("name", ip.getName());
                        virtual.setValue("maintainer", ip.getMaintainer());
                        virtual.setValue("version", ip.getVersion());
			PackageInterface pa = PackageManager.getPackage(ip.getId());
			if (pa != null) {
                        	virtual.setValue("forcednew", "false");
                        	virtual.setValue("lastversion", pa.getVersion());
				// weird way	
				try {
	                		virtual.setValue("nextversion", Integer.parseInt(pa.getVersion())+1);
				} catch (Exception e) {
					log.info("Can't create a new version string : "+pa.getVersion());
				}
			} else {
                        	virtual.setValue("forcednew", "true");
                        	virtual.setValue("lastversion", ip.getVersion());
				try {
                        		virtual.setValue("nextversion", Integer.parseInt(ip.getVersion())+1);
				} catch (Exception e) {}
			}
                        virtual.setValue("type", ip.getType());
                        virtual.setValue("id", ip.getId());
                        virtual.setValue("included", ip.getIncluded());

			// add the target name if this is a created inside
			// the current project.
			Target t2 = p.getTargetById(ip.getId());
			if (t2 != null ) {
                        	virtual.setValue("target", t2.getName());
			} else {
                        	virtual.setValue("target", "");
			}
                        list.add(virtual);
                    }
                }
            }
        }
        return list;
    }


    /**
     *  Gets the targetPeople attribute of the Controller object
     *
     * @param  project  Description of the Parameter
     * @param  target   Description of the Parameter
     * @param  type     Description of the Parameter
     * @param  subtype  Description of the Parameter
     * @return          The targetPeople value
     */
    public List<MMObjectNode> getTargetPeople(String project, String target, String type, String subtype) {
        List<MMObjectNode> list = new ArrayList<MMObjectNode>();
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
        Project p = ProjectManager.getProject(project);
        if (p != null) {
            Target t = p.getTarget(target);
            if (t != null) {
                List<Person> people = t.getRelatedPeople(type);
                if (people != null) {
                    for (Person pr : people) {
                        MMObjectNode virtual = builder.getNewNode("admin");
                        virtual.setValue("name", pr.getName());
                        virtual.setValue("company", pr.getCompany());
                        virtual.setValue("reason", pr.getReason());
                        virtual.setValue("mailto", pr.getMailto());
                        list.add(virtual);
                    }
                }
            }
        }
        return list;
    }


    /**
     *  Gets the targetScreenshots attribute of the Controller object
     *
     * @param  project  Description of the Parameter
     * @param  target   Description of the Parameter
     * @return          The Screenshots value
     */
    public List<MMObjectNode> getTargetScreenshots(String project, String target) {
        List<MMObjectNode> list = new ArrayList<MMObjectNode>();
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
        Project p = ProjectManager.getProject(project);
        if (p != null) {
            Target t = p.getTarget(target);
            if (t != null) {
                List<String> screenshots = t.getScreenshots();
                if (screenshots != null) {
                    for (String string : screenshots) {
                        MMObjectNode virtual = builder.getNewNode("admin");
                        virtual.setValue("name", string);
                        virtual.setValue("link", string);
                        virtual.setValue("description", string);
                        list.add(virtual);
                    }
                }
            }
        }
        return list;
    }


    /**
     *  Description of the Method
     *
     * @param  project     Description of the Parameter
     * @param  target      Description of the Parameter
     * @param  newversion  Description of the Parameter
     * @return             Description of the Return Value
     */
    public boolean packageTarget(String project, String target, int newversion,String latest,String createnew,String publishprovider,String publishstate,String publishsharepassword) {
        Project p = ProjectManager.getProject(project);
        if (p != null) {
	// this first part handles auto includes and updates (mostly for bundles)
		if (!latest.equals("")) {
			StringTokenizer tok = new StringTokenizer(latest,",\n\t");
			while (tok.hasMoreTokens()) {
				String pid = tok.nextToken();
				PackageInterface pa = PackageManager.getPackage(pid);
				if (pa!=null) {
    					setIncludedVersion(project,target,pid,pa.getVersion());
				}
			}
		}

                Target t = p.getTarget(target);

		if (!createnew.equals("")) {
			StringTokenizer tok = new StringTokenizer(createnew,",\n\t");
			while (tok.hasMoreTokens()) {
				String tid = tok.nextToken();
				Target t2 = p.getTarget(tid);
				if (t2 != null) {
					t.addRelatedTargetsCreate(t2);
				}
			}
		}

            // set publish changes
	    t.setPublishProvider(publishprovider);
	    t.setPublishSharePassword(publishsharepassword);
	    if (publishstate.equals("true")) {
	    	t.setPublishState(true);
	    } else {
	    	t.setPublishState(false);
	    }
	    
            t.createPackageThreaded(newversion);
        }
        return true;
    }


    /**
     *  Gets the packageValue attribute of the Controller object
     *
     * @param  project  Description of the Parameter
     * @param  target   Description of the Parameter
     * @param  name     Description of the Parameter
     * @return          The packageValue value
     */
    public String getPackageValue(String project, String target, String name) {
        Project p = ProjectManager.getProject(project);
        if (p != null) {
            Target t = p.getTarget(target);
            Object o = t.getItem(name);
            if (o != null) {
                return (String) o;
            }
        }
        return "";
    }


    /**
     *  Turn publish mode on for this target
     *
     * @return          result true/false
     */
    public boolean setPublishModeOn(String project, String target,String publishprovider,String sharepassword) {
	log.info("PROJECT="+project);
        Project p = ProjectManager.getProject(project);
        if (p != null) {
	    log.info("TARGET="+target);
            Target t = p.getTarget(target);
            if (t != null) {
	    	log.info("PROVIDER="+publishprovider);
		t.setPublishProvider(publishprovider);	
		t.setPublishState(true);	
		t.setPublishSharePassword(sharepassword);	
		t.save();
		return true;
            }
        }
        return false;
    }


    /**
     *  Turn publish mode off for this target
     *
     * @return          result true/false
     */
    public boolean setPublishModeOff(String project, String target) {
        Project p = ProjectManager.getProject(project);
        if (p != null) {
            Target t = p.getTarget(target);
            if (t != null) {
		t.setPublishProvider("");	
		t.setPublishState(false);	
		t.setPublishSharePassword("");	
		t.save();
		return true;
            }
        }
        return false;
    }


    /**
     *  Sets the packageValue attribute of the Controller object
     *
     * @param  project   The new packageValue value
     * @param  target    The new packageValue value
     * @param  newname   The new packageValue value
     * @param  newvalue  The new packageValue value
     * @return           Description of the Return Value
     */
    public boolean setPackageValue(String project, String target, String newname, String newvalue) {
        Project p = ProjectManager.getProject(project);
        if (p != null) {
            Target t = p.getTarget(target);
            t.setItem(newname, newvalue);
            t.save();
            return true;
        }
        return false;
    }


    /**
     *  Description of the Method
     *
     * @param  project  Description of the Parameter
     * @param  newname  Description of the Parameter
     * @param  newpath  Description of the Parameter
     * @return          Description of the Return Value
     */
    public boolean changeProjectSettings(String project, String newname, String newpath) {
        return ProjectManager.changeProjectSettings(project, newname, newpath);
    }


    /**
     *  Sets the includedVersion attribute of the Controller object
     *
     * @param  project     The new includedVersion value
     * @param  target      The new includedVersion value
     * @param  id          The new includedVersion value
     * @param  newversion  The new includedVersion value
     * @return             Description of the Return Value
     */
    public boolean setIncludedVersion(String project, String target, String id, String newversion) {
        Project p = ProjectManager.getProject(project);
        if (p != null) {
            Target t = p.getTarget(target);
            t.setIncludedVersion(id, newversion);
        }
        return true;
    }


    /**
     *  Sets the packageDescription attribute of the Controller object
     *
     * @param  project         The new packageDescription value
     * @param  target          The new packageDescription value
     * @param  newdescription  The new packageDescription value
     * @return                 Description of the Return Value
     */
    public boolean setPackageDescription(String project, String target, String newdescription) {
        Project p = ProjectManager.getProject(project);
        if (p != null) {
            Target t = p.getTarget(target);
            t.setDescription(newdescription);
        }
        return true;
    }


    /**
     *  Sets the packageName attribute of the Controller object
     *
     * @param  project  The new packageName value
     * @param  target   The new packageName value
     * @param  newname  The new packageName value
     * @return          Description of the Return Value
     */
    public boolean setPackageName(String project, String target, String newname) {
        Project p = ProjectManager.getProject(project);
        if (p != null) {
            Target t = p.getTarget(target);
            t.setName(newname);
        }
        return true;
    }


    /**
     *  Sets the packageMaintainer attribute of the Controller object
     *
     * @param  project        The new packageMaintainer value
     * @param  target         The new packageMaintainer value
     * @param  newmaintainer  The new packageMaintainer value
     * @return                Description of the Return Value
     */
    public boolean setPackageMaintainer(String project, String target, String newmaintainer) {
        Project p = ProjectManager.getProject(project);
        if (p != null) {
            Target t = p.getTarget(target);
            t.setMaintainer(newmaintainer);
        }
        return true;
    }


    /**
     *  Sets the packageLicenseVersion attribute of the Controller object
     *
     * @param  project            The new packageLicenseVersion value
     * @param  target             The new packageLicenseVersion value
     * @param  newlicenseversion  The new packageLicenseVersion value
     * @return                    Description of the Return Value
     */
    public boolean setPackageLicenseVersion(String project, String target, String newlicenseversion) {
        Project p = ProjectManager.getProject(project);
        if (p != null) {
            Target t = p.getTarget(target);
            t.setLicenseVersion(newlicenseversion);
        }
        return true;
    }


    /**
     *  Sets the packageLicenseName attribute of the Controller object
     *
     * @param  project         The new packageLicenseName value
     * @param  target          The new packageLicenseName value
     * @param  newlicensename  The new packageLicenseName value
     * @return                 Description of the Return Value
     */
    public boolean setPackageLicenseName(String project, String target, String newlicensename) {
        Project p = ProjectManager.getProject(project);
        if (p != null) {
            Target t = p.getTarget(target);
            t.setLicenseName(newlicensename);
        }
        return true;
    }


    /**
     *  Sets the packageLicenseType attribute of the Controller object
     *
     * @param  project         The new packageLicenseType value
     * @param  target          The new packageLicenseType value
     * @param  newlicensetype  The new packageLicenseType value
     * @return                 Description of the Return Value
     */
    public boolean setPackageLicenseType(String project, String target, String newlicensetype) {
        Project p = ProjectManager.getProject(project);
        if (p != null) {
            Target t = p.getTarget(target);
            t.setLicenseType(newlicensetype);
        }
        return true;
    }


    /**
     *  Sets the packageInitiator attribute of the Controller object
     *
     * @param  project     The new packageInitiator value
     * @param  target      The new packageInitiator value
     * @param  oldname     The new packageInitiator value
     * @param  newname     The new packageInitiator value
     * @param  oldcompany  The new packageInitiator value
     * @param  newcompany  The new packageInitiator value
     * @return             Description of the Return Value
     */
    public boolean setPackageInitiator(String project, String target, String oldname, String newname, String oldcompany, String newcompany) {
        Project p = ProjectManager.getProject(project);
        if (p != null) {
            Target t = p.getTarget(target);
            t.setPackageInitiator(oldname, newname, oldcompany, newcompany);
        }
        return true;
    }


    /**
     *  Adds a feature to the PackageInitiator attribute of the Controller object
     *
     * @param  project     The feature to be added to the PackageInitiator attribute
     * @param  target      The feature to be added to the PackageInitiator attribute
     * @param  newname     The feature to be added to the PackageInitiator attribute
     * @param  newcompany  The feature to be added to the PackageInitiator attribute
     * @return             Description of the Return Value
     */
    public boolean addPackageInitiator(String project, String target, String newname, String newcompany) {
        Project p = ProjectManager.getProject(project);
        if (p != null) {
            Target t = p.getTarget(target);
            t.addPackageInitiator(newname, newcompany);
        }
        return true;
    }


    /**
     *  Adds a feature to the PackageDepends attribute of the Controller object
     *
     * @param  project    The feature to be added to the PackageDepends attribute
     * @param  target     The feature to be added to the PackageDepends attribute
     * @param  packageid  The feature to be added to the PackageDepends attribute
     * @param  version    The feature to be added to the PackageDepends attribute
     * @return            Description of the Return Value
     */
    public boolean addPackageDepends(String project, String target, String packageid, String version) {
        Project p = ProjectManager.getProject(project);
        if (p != null) {
            Target t = p.getTarget(target);
            t.addPackageDepends(packageid, version);
        }
        return true;
    }


    /**
     *  Description of the Method
     *
     * @param  project      Description of the Parameter
     * @param  target       Description of the Parameter
     * @param  packageid    Description of the Parameter
     * @param  version      Description of the Parameter
     * @param  versionmode  Description of the Parameter
     * @return              Description of the Return Value
     */
    public boolean delPackageDepends(String project, String target, String packageid, String version, String versionmode) {
        Project p = ProjectManager.getProject(project);
        if (p != null) {
            Target t = p.getTarget(target);
            t.delPackageDepends(packageid, version, versionmode);
        }
        return true;
    }


    /**
     *  Description of the Method
     *
     * @param  project  Description of the Parameter
     * @param  target   Description of the Parameter
     * @return          Description of the Return Value
     */
    public boolean delTarget(String project, String target) {
        Project p = ProjectManager.getProject(project);
        if (p != null) {
            Target t = p.getTarget(target);
            p.deleteTarget(t.getName());
        }
        return true;
    }


    /**
     *  Description of the Method
     *
     * @param  project  Description of the Parameter
     * @return          Description of the Return Value
     */
    public boolean delProject(String project) {
        return ProjectManager.deleteProject(project);
    }


    /**
     *  Sets the packageDepends attribute of the Controller object
     *
     * @param  project         The new packageDepends value
     * @param  target          The new packageDepends value
     * @param  packageid       The new packageDepends value
     * @param  oldversion      The new packageDepends value
     * @param  oldversionmode  The new packageDepends value
     * @param  newversion      The new packageDepends value
     * @param  newversionmode  The new packageDepends value
     * @return                 Description of the Return Value
     */
    public boolean setPackageDepends(String project, String target, String packageid, String oldversion, String oldversionmode, String newversion, String newversionmode) {
        Project p = ProjectManager.getProject(project);
        if (p != null) {
            Target t = p.getTarget(target);
            t.setPackageDepends(packageid, oldversion, oldversionmode, newversion, newversionmode);
        }
        return true;
    }


    /**
     *  Adds a feature to the BundleTarget attribute of the Controller object
     *
     * @param  project  The feature to be added to the BundleTarget attribute
     * @param  name     The feature to be added to the BundleTarget attribute
     * @param  type     The feature to be added to the BundleTarget attribute
     * @param  path     The feature to be added to the BundleTarget attribute
     * @return          Description of the Return Value
     */
    public boolean addBundleTarget(String project, String name, String type, String path) {
        Project p = ProjectManager.getProject(project);
        if (p != null) {
            p.addBundleTarget(name, type, path);
        }
        return true;
    }


    /**
     *  Adds a feature to the PackageTarget attribute of the Controller object
     *
     * @param  project  The feature to be added to the PackageTarget attribute
     * @param  name     The feature to be added to the PackageTarget attribute
     * @param  type     The feature to be added to the PackageTarget attribute
     * @param  path     The feature to be added to the PackageTarget attribute
     * @return          Description of the Return Value
     */
    public boolean addPackageTarget(String project, String name, String type, String path) {
        Project p = ProjectManager.getProject(project);
        if (p != null) {
            p.addPackageTarget(name, type, path);
        }
        return true;
    }


    /**
     *  Description of the Method
     *
     * @param  project     Description of the Parameter
     * @param  target      Description of the Parameter
     * @param  oldname     Description of the Parameter
     * @param  oldcompany  Description of the Parameter
     * @return             Description of the Return Value
     */
    public boolean delPackageInitiator(String project, String target, String oldname, String oldcompany) {
        Project p = ProjectManager.getProject(project);
        if (p != null) {
            Target t = p.getTarget(target);
            t.delPackageInitiator(oldname, oldcompany);
        }
        return true;
    }


    /**
     *  Sets the packageDeveloper attribute of the Controller object
     *
     * @param  project     The new packageDeveloper value
     * @param  target      The new packageDeveloper value
     * @param  oldname     The new packageDeveloper value
     * @param  newname     The new packageDeveloper value
     * @param  oldcompany  The new packageDeveloper value
     * @param  newcompany  The new packageDeveloper value
     * @return             Description of the Return Value
     */
    public boolean setPackageDeveloper(String project, String target, String oldname, String newname, String oldcompany, String newcompany) {
        Project p = ProjectManager.getProject(project);
        if (p != null) {
            Target t = p.getTarget(target);
            t.setPackageDeveloper(oldname, newname, oldcompany, newcompany);
        }
        return true;
    }


    /**
     *  Adds a feature to the PackageDeveloper attribute of the Controller object
     *
     * @param  project     The feature to be added to the PackageDeveloper attribute
     * @param  target      The feature to be added to the PackageDeveloper attribute
     * @param  newname     The feature to be added to the PackageDeveloper attribute
     * @param  newcompany  The feature to be added to the PackageDeveloper attribute
     * @return             Description of the Return Value
     */
    public boolean addPackageDeveloper(String project, String target, String newname, String newcompany) {
        Project p = ProjectManager.getProject(project);
        if (p != null) {
            Target t = p.getTarget(target);
            t.addPackageDeveloper(newname, newcompany);
        }
        return true;
    }


    /**
     *  Description of the Method
     *
     * @param  project     Description of the Parameter
     * @param  target      Description of the Parameter
     * @param  oldname     Description of the Parameter
     * @param  oldcompany  Description of the Parameter
     * @return             Description of the Return Value
     */
    public boolean delPackageDeveloper(String project, String target, String oldname, String oldcompany) {
        Project p = ProjectManager.getProject(project);
        if (p != null) {
            Target t = p.getTarget(target);
            t.delPackageDeveloper(oldname, oldcompany);
        }
        return true;
    }


    /**
     *  Sets the packageContact attribute of the Controller object
     *
     * @param  project    The new packageContact value
     * @param  target     The new packageContact value
     * @param  oldreason  The new packageContact value
     * @param  newreason  The new packageContact value
     * @param  oldname    The new packageContact value
     * @param  newname    The new packageContact value
     * @param  oldemail   The new packageContact value
     * @param  newemail   The new packageContact value
     * @return            Description of the Return Value
     */
    public boolean setPackageContact(String project, String target, String oldreason, String newreason, String oldname, String newname, String oldemail, String newemail) {
        Project p = ProjectManager.getProject(project);
        if (p != null) {
            Target t = p.getTarget(target);
            t.setPackageContact(oldreason, newreason, oldname, newname, oldemail, newemail);
        }
        return true;
    }


    /**
     *  Adds a feature to the PackageContact attribute of the Controller object
     *
     * @param  project    The feature to be added to the PackageContact attribute
     * @param  target     The feature to be added to the PackageContact attribute
     * @param  newreason  The feature to be added to the PackageContact attribute
     * @param  newname    The feature to be added to the PackageContact attribute
     * @param  newemail   The feature to be added to the PackageContact attribute
     * @return            Description of the Return Value
     */
    public boolean addPackageContact(String project, String target, String newreason, String newname, String newemail) {
        Project p = ProjectManager.getProject(project);
        if (p != null) {
            Target t = p.getTarget(target);
            t.addPackageContact(newreason, newname, newemail);
        }
        return true;
    }


    /**
     *  Description of the Method
     *
     * @param  project    Description of the Parameter
     * @param  target     Description of the Parameter
     * @param  oldreason  Description of the Parameter
     * @param  oldname    Description of the Parameter
     * @param  oldemail   Description of the Parameter
     * @return            Description of the Return Value
     */
    public boolean delPackageContact(String project, String target, String oldreason, String oldname, String oldemail) {
        Project p = ProjectManager.getProject(project);
        if (p != null) {
            Target t = p.getTarget(target);
            t.delPackageContact(oldreason, oldname, oldemail);
        }
        return true;
    }


    /**
     *  Sets the packageSupporter attribute of the Controller object
     *
     * @param  project     The new packageSupporter value
     * @param  target      The new packageSupporter value
     * @param  oldcompany  The new packageSupporter value
     * @param  newcompany  The new packageSupporter value
     * @return             Description of the Return Value
     */
    public boolean setPackageSupporter(String project, String target, String oldcompany, String newcompany) {
        Project p = ProjectManager.getProject(project);
        if (p != null) {
            Target t = p.getTarget(target);
            t.setPackageSupporter(oldcompany, newcompany);
        }
        return true;
    }


    /**
     *  Adds a feature to the PackageSupporter attribute of the Controller object
     *
     * @param  project     The feature to be added to the PackageSupporter attribute
     * @param  target      The feature to be added to the PackageSupporter attribute
     * @param  newcompany  The feature to be added to the PackageSupporter attribute
     * @return             Description of the Return Value
     */
    public boolean addPackageSupporter(String project, String target, String newcompany) {
        Project p = ProjectManager.getProject(project);
        if (p != null) {
            Target t = p.getTarget(target);
            t.addPackageSupporter(newcompany);
        }
        return true;
    }


    /**
     *  Description of the Method
     *
     * @param  project     Description of the Parameter
     * @param  target      Description of the Parameter
     * @param  oldcompany  Description of the Parameter
     * @return             Description of the Return Value
     */
    public boolean delPackageSupporter(String project, String target, String oldcompany) {
        Project p = ProjectManager.getProject(project);
        if (p != null) {
            Target t = p.getTarget(target);
            t.delPackageSupporter(oldcompany);
        }
        return true;
    }


    /**
     *  Description of the Method
     *
     * @param  project  Description of the Parameter
     * @param  target   Description of the Parameter
     * @param  id       Description of the Parameter
     * @return          Description of the Return Value
     */
    public boolean delIncludedPackage(String project, String target, String id) {
        Project p = ProjectManager.getProject(project);
        if (p != null) {
            Target t = p.getTarget(target);
            t.delIncludedPackage(id);
        }
        return true;
    }


    /**
     *  Adds a feature to the TargetPackage attribute of the Controller object
     *
     * @param  project     The feature to be added to the TargetPackage attribute
     * @param  target      The feature to be added to the TargetPackage attribute
     * @param  newpackage  The feature to be added to the TargetPackage attribute
     * @return             Description of the Return Value
     */
    public boolean addTargetPackage(String project, String target, String newpackage) {
        Project p = ProjectManager.getProject(project);
        if (p != null) {
            Target t = p.getTarget(target);
            t.addPackage(newpackage);
        }
        return true;
    }


    /**
     *  Adds a feature to the Project attribute of the Controller object
     *
     * @param  newprojectname  The feature to be added to the Project attribute
     * @param  newprojectpath  The feature to be added to the Project attribute
     * @return                 Description of the Return Value
     */
    public boolean addProject(String newprojectname, String newprojectpath) {
        ProjectManager.addProject(newprojectname, newprojectpath);
        return true;
    }


    /**
     *  Gets the projects attribute of the Controller object
     *
     * @return    The projects value
     */
    public List<MMObjectNode> getProjects() {
        // get the current best packages
        Iterator<Project> projects = ProjectManager.getProjects();

        List<MMObjectNode> list = new ArrayList<MMObjectNode>();
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

        while (projects.hasNext()) {
            Project p = projects.next();
            MMObjectNode virtual = builder.getNewNode("admin");
            virtual.setValue("name", p.getName());
            virtual.setValue("path", p.getPath());
            virtual.setValue("syntaxerrors", p.hasSyntaxErrors());
            list.add(virtual);
        }
        return list;
    }


    /**
     *  Gets the creators attribute of the Controller object
     *
     * @return    The creators value
     */
    public List<MMObjectNode> getCreators() {
        // get the current creators we have installed
        Map<String, CreatorInterface> creators = ProjectManager.getCreators();
        List<MMObjectNode> list = new ArrayList<MMObjectNode>();
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

        Iterator<String> e = creators.keySet().iterator();
        while (e.hasNext()) {
            String key = e.next();
            CreatorInterface cr = creators.get(key);

            MMObjectNode virtual = builder.getNewNode("admin");
            virtual.setValue("name", key);
            virtual.setValue("value", cr.getClass());
            list.add(virtual);
        }
        return list;
    }

}
