/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.packaging.projects.gui;

import java.io.*;
import java.net.URL;
import java.text.DateFormat;
import java.util.*;
import java.util.jar.*;

import org.mmbase.bridge.*;
import org.mmbase.bridge.implementation.*;
import org.mmbase.applications.packaging.*;
import org.mmbase.applications.packaging.packagehandlers.*;
import org.mmbase.applications.packaging.bundlehandlers.*;
import org.mmbase.applications.packaging.installhandlers.*;
import org.mmbase.applications.packaging.providerhandlers.*;
import org.mmbase.applications.packaging.sharehandlers.*;
import org.mmbase.applications.packaging.projects.creators.*;
import org.mmbase.applications.packaging.projects.*;
import org.mmbase.util.logging.*;
import org.mmbase.module.core.*;


/**
 * @author Daniel Ockeloen
 * @version $Id: guiController.java
 */
public class Controller {

	private static Logger log = Logging.getLoggerInstance(Controller.class);
	private static Cloud cloud;
       	NodeManager manager;
	CloudContext context;


	public Controller() {
		cloud=LocalContext.getCloudContext().getCloud("mmbase");

		// hack needs to be solved
        	manager=cloud.getNodeManager("typedef");
		if (manager==null) log.error("Can't access builder typedef");
		context=LocalContext.getCloudContext();
		if (!InstallManager.isRunning()) InstallManager.init();
	}


	public List getProjectBundleTargets(String name) {
                List list = new ArrayList();
		Project p=ProjectManager.getProject(name);
		if (p!=null) {
			Enumeration targets=p.getBundleTargets();

	                VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
			while (targets.hasMoreElements()) {
				Target  t=(Target)targets.nextElement();
                	       	MMObjectNode virtual = builder.getNewNode("admin");
				virtual.setValue("name",t.getName());
				virtual.setValue("type",t.getType());
				virtual.setValue("path",t.getPath());
				virtual.setValue("syntaxerrors",t.hasSyntaxErrors());
				list.add(virtual);
			}
		}
		return list;
	}


	public List getProjectPackageTargets(String name) {
                List list = new ArrayList();
		Project p=ProjectManager.getProject(name);
		if (p!=null) {
			Enumeration targets=p.getPackageTargets();

	                VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
			while (targets.hasMoreElements()) {
				Target  t=(Target)targets.nextElement();
                	       	MMObjectNode virtual = builder.getNewNode("admin");
				virtual.setValue("name",t.getName());
				virtual.setValue("type",t.getType());
				virtual.setValue("path",t.getPath());
				virtual.setValue("syntaxerrors",t.hasSyntaxErrors());
				list.add(virtual);
			}
		}
		return list;
	}


	public List getProjectTargets(String name) {
                List list = new ArrayList();
		Project p=ProjectManager.getProject(name);
		if (p!=null) {
			Enumeration targets=p.getTargets();

	                VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
			while (targets.hasMoreElements()) {
				Target  t=(Target)targets.nextElement();
                	       	MMObjectNode virtual = builder.getNewNode("admin");
				virtual.setValue("name",t.getName());
				virtual.setValue("depends",t.getDepends());
				list.add(virtual);
			}
		}
		return list;
	}


	public List getTargetPackageSteps(String project,String target,int logid) {
                List list = new ArrayList();
                VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
                Project p=ProjectManager.getProject(project);
                if (p!=null) {
                        Target t=p.getTarget(target);
			if (t!=null) {
				Enumeration steps=null;
				if (logid==-1) {
					steps=t.getPackageSteps();
				} else {
					steps=t.getPackageSteps(logid);
				}
				if (steps!=null) {
					while (steps.hasMoreElements()) {
						packageStep step=(packageStep)steps.nextElement();
               		       		  	MMObjectNode virtual = builder.getNewNode("admin");
						virtual.setValue("userfeedback",step.getUserFeedBack());
						virtual.setValue("timestamp",step.getTimeStamp());
						virtual.setValue("errorcount",step.getErrorCount());
						virtual.setValue("warningcount",step.getWarningCount());
						virtual.setValue("id",step.getId());
						virtual.setValue("parent",step.getParent());
						if (step.hasChilds()) {
							virtual.setValue("haschilds","true");
						} else {
							virtual.setValue("haschilds","false");
						}
						list.add(virtual);
					}
				}
			}
		}
		return list;
	}


	public List haveTargetLog(String project,String target) {
	        List list = new ArrayList();
         	VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
                MMObjectNode virtual = builder.getNewNode("admin");
                Project p=ProjectManager.getProject(project);
                if (p!=null) {
                        Target t=p.getTarget(target);
                        if (t!=null) {
				Enumeration steps=t.getPackageSteps();
			
				if (steps!=null) {
					virtual.setValue("log","true");
				} else {
					virtual.setValue("log","false");
				}
			}
			list.add(virtual);
		}
		return list;
	}


	public List getTargetPackageDepends(String project,String target) {
	        List list = new ArrayList();
         	VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
                Project pr=ProjectManager.getProject(project);
                if (pr!=null) {
                        Target t=pr.getTarget(target);
                        if (t!=null) {
				ArrayList in=t.getPackageDepends();
				if (in!=null) {
            				for (Iterator i = in.iterator(); i.hasNext();) {
						PackageDepend p=(PackageDepend)i.next();
         					MMObjectNode virtual = builder.getNewNode("admin");
						virtual.setValue("name",p.getName());
						virtual.setValue("maintainer",p.getMaintainer());
						virtual.setValue("version",p.getVersion());
						virtual.setValue("versionmode",p.getVersionMode());
						virtual.setValue("type",p.getType());
						virtual.setValue("id",p.getId());
						list.add(virtual);
					}
				}
			}
		}
		return list;
	}


	public MMObjectNode getProjectTargetInfo(String project,String target) {
         	VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
                MMObjectNode virtual = builder.getNewNode("admin");
		Project p=ProjectManager.getProject(project);
		if (p!=null) {
			Target t=p.getTarget(target);
			if (t!=null) {
				virtual.setValue("lastversion",t.getLastVersion());
				virtual.setValue("nextversion",t.getNextVersion());
				virtual.setValue("lastdate",""+t.getLastDate());
				virtual.setValue("description",t.getDescription());
				virtual.setValue("maintainer",t.getMaintainer());
				virtual.setValue("syntaxerrors",t.hasSyntaxErrors());
        			if (t.getRelatedPeople("initiators")==null || t.getRelatedPeople("initiators").size()==0) {
					virtual.setValue("haveinitiators","false");
				} else {
					virtual.setValue("haveinitiators","true");
				}
        			if (t.getRelatedPeople("developers")==null || t.getRelatedPeople("developers").size()==0) {
					virtual.setValue("havedevelopers","false");
				} else {
					virtual.setValue("havedevelopers","true");
				}
        			if (t.getRelatedPeople("contacts")==null || t.getRelatedPeople("contacts").size()==0) {
					virtual.setValue("havecontacts","false");
				} else {
					virtual.setValue("havecontacts","true");
				}
				virtual.setValue("name",t.getName());
				if (t.isBundle()) {
					virtual.setValue("bundlename",t.getPackageName());
				} else {
					virtual.setValue("packagename",t.getPackageName());
				}
				virtual.setValue("type",t.getType());
				virtual.setValue("licensetype",t.getLicenseType());
				virtual.setValue("releasenotes",t.getReleaseNotes());
				virtual.setValue("installationnotes",t.getInstallationNotes());
				virtual.setValue("licensename",t.getLicenseName());
				virtual.setValue("licenseversion",t.getLicenseVersion());
			}
		}
		return virtual;
	}


	public MMObjectNode getProjectInfo(String project) {
         	VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
                MMObjectNode virtual = builder.getNewNode("admin");
		Project p=ProjectManager.getProject(project);
		if (p!=null) {
			virtual.setValue("name",p.getName());
			virtual.setValue("path",""+p.getPath());
		}
		return virtual;
	}


	public List getTargetIncludedPackages(String project,String target) {
	        List list = new ArrayList();
         	VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
                Project p=ProjectManager.getProject(project);
                if (p!=null) {
                        Target t=p.getTarget(target);
                        if (t!=null) {
				ArrayList in=t.getIncludedPackages();
				if (in!=null) {
            				for (Iterator i = in.iterator(); i.hasNext();) {
						IncludedPackage ip=(IncludedPackage)i.next();
         					MMObjectNode virtual = builder.getNewNode("admin");
						virtual.setValue("name",ip.getName());
						virtual.setValue("maintainer",ip.getMaintainer());
						virtual.setValue("version",ip.getVersion());
						virtual.setValue("type",ip.getType());
						virtual.setValue("id",ip.getId());
						virtual.setValue("included",ip.getIncluded());
						list.add(virtual);
					}
				}
			}
		}
		return list;
	}


	public List getTargetPeople(String project,String target,String type,String subtype) {
	        List list = new ArrayList();
         	VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
                Project p=ProjectManager.getProject(project);
                if (p!=null) {
                        Target t=p.getTarget(target);
                        if (t!=null) {
				List people=t.getRelatedPeople(type);
				if (people!=null) {
            				for (Iterator i = people.iterator(); i.hasNext();) {
						Person pr=(Person)i.next();
         					MMObjectNode virtual = builder.getNewNode("admin");
						virtual.setValue("name",pr.getName());
						virtual.setValue("company",pr.getCompany());
						virtual.setValue("reason",pr.getReason());
						virtual.setValue("mailto",pr.getMailto());
						list.add(virtual);
					}
				}
			}
		}
		return list;
	}


	public boolean packageTarget(String project,String target,int newversion) {
         	VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
                MMObjectNode virtual = builder.getNewNode("admin");
		Project p=ProjectManager.getProject(project);
		if (p!=null) {
			Target t=p.getTarget(target);
			t.createPackage(newversion);
		}
		return true;
	}


	public String getPackageValue(String project,String target,String name) {
		Project p=ProjectManager.getProject(project);
		if (p!=null) {
			Target t=p.getTarget(target);
			Object o=t.getItem(name);
			if (o!=null) return (String)o;
		}
		return "";
	}


	public boolean setPackageValue(String project,String target,String newname,String newvalue) {
		Project p=ProjectManager.getProject(project);
		if (p!=null) {
			Target t=p.getTarget(target);
			t.setItem(newname,newvalue);
			t.save();
			return true;
		}
		return false;
	}


	public boolean changeProjectSettings(String project,String newname,String newpath) {
		return ProjectManager.changeProjectSettings(project,newname,newpath);
	}


	public boolean setIncludedVersion(String project,String target,String id,String newversion) {
         	VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
		Project p=ProjectManager.getProject(project);
		if (p!=null) {
			Target t=p.getTarget(target);
			t.setIncludedVersion(id,newversion);
		}
		return true;
	}


	public boolean setPackageDescription(String project,String target,String newdescription) {
         	VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
		Project p=ProjectManager.getProject(project);
		if (p!=null) {
			Target t=p.getTarget(target);
			t.setDescription(newdescription);
		}
		return true;
	}


	public boolean setPackageName(String project,String target,String newname) {
		Project p=ProjectManager.getProject(project);
		if (p!=null) {
			Target t=p.getTarget(target);
			t.setName(newname);
		}
		return true;
	}


	public boolean setPackageMaintainer(String project,String target,String newmaintainer) {
		Project p=ProjectManager.getProject(project);
		if (p!=null) {
			Target t=p.getTarget(target);
			t.setMaintainer(newmaintainer);
		}
		return true;
	}


	public boolean setPackageLicenseVersion(String project,String target,String newlicenseversion) {
		Project p=ProjectManager.getProject(project);
		if (p!=null) {
			Target t=p.getTarget(target);
			t.setLicenseVersion(newlicenseversion);
		}
		return true;
	}


	public boolean setPackageLicenseName(String project,String target,String newlicensename) {
		Project p=ProjectManager.getProject(project);
		if (p!=null) {
			Target t=p.getTarget(target);
			t.setLicenseName(newlicensename);
		}
		return true;
	}


	public boolean setPackageLicenseType(String project,String target,String newlicensetype) {
         	VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
		Project p=ProjectManager.getProject(project);
		if (p!=null) {
			Target t=p.getTarget(target);
			t.setLicenseType(newlicensetype);
		}
		return true;
	}


	public boolean setPackageInitiator(String project,String target,String oldname,String newname,String oldcompany,String newcompany) {
         	VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
		Project p=ProjectManager.getProject(project);
		if (p!=null) {
			Target t=p.getTarget(target);
			t.setPackageInitiator(oldname,newname,oldcompany,newcompany);
		}
		return true;
	}


	public boolean addPackageInitiator(String project,String target,String newname,String newcompany) {
		Project p=ProjectManager.getProject(project);
		if (p!=null) {
			Target t=p.getTarget(target);
			t.addPackageInitiator(newname,newcompany);
		}
		return true;
	}


	public boolean addPackageDepends(String project,String target,String packageid,String version) {
		Project p=ProjectManager.getProject(project);
		if (p!=null) {
			Target t=p.getTarget(target);
			t.addPackageDepends(packageid,version);
		}
		return true;
	}


	public boolean delPackageDepends(String project,String target,String packageid,String version,String versionmode) {
		Project p=ProjectManager.getProject(project);
		if (p!=null) {
			Target t=p.getTarget(target);
			t.delPackageDepends(packageid,version,versionmode);
		}
		return true;
	}


	public boolean delTarget(String project,String target) {
		Project p=ProjectManager.getProject(project);
		if (p!=null) {
			Target t=p.getTarget(target);
			p.deleteTarget(t.getName());
		}
		return true;
	}


	public boolean delProject(String project) {
		return ProjectManager.deleteProject(project);
	}


	public boolean setPackageDepends(String project,String target,String packageid,String oldversion,String oldversionmode,String newversion,String newversionmode) {
		Project p=ProjectManager.getProject(project);
		if (p!=null) {
			Target t=p.getTarget(target);
			t.setPackageDepends(packageid,oldversion,oldversionmode,newversion,newversionmode);
		}
		return true;
	}


	public boolean addBundleTarget(String project,String name,String type,String path) {
		Project p=ProjectManager.getProject(project);
		if (p!=null) {
			p.addBundleTarget(name,type,path);
		}
		return true;
	}

	public boolean addPackageTarget(String project,String name,String type,String path) {
		Project p=ProjectManager.getProject(project);
		if (p!=null) {
			p.addPackageTarget(name,type,path);
		}
		return true;
	}

	public boolean delPackageInitiator(String project,String target,String oldname,String oldcompany) {
         	VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
		Project p=ProjectManager.getProject(project);
		if (p!=null) {
			Target t=p.getTarget(target);
			t.delPackageInitiator(oldname,oldcompany);
		}
		return true;
	}


	public boolean setPackageDeveloper(String project,String target,String oldname,String newname,String oldcompany,String newcompany) {
         	VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
		Project p=ProjectManager.getProject(project);
		if (p!=null) {
			Target t=p.getTarget(target);
			t.setPackageDeveloper(oldname,newname,oldcompany,newcompany);
		}
		return true;
	}


	public boolean addPackageDeveloper(String project,String target,String newname,String newcompany) {
         	VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
		Project p=ProjectManager.getProject(project);
		if (p!=null) {
			Target t=p.getTarget(target);
			t.addPackageDeveloper(newname,newcompany);
		}
		return true;
	}


	public boolean delPackageDeveloper(String project,String target,String oldname,String oldcompany) {
         	VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
		Project p=ProjectManager.getProject(project);
		if (p!=null) {
			Target t=p.getTarget(target);
			t.delPackageDeveloper(oldname,oldcompany);
		}
		return true;
	}


	public boolean setPackageContact(String project,String target,String oldreason,String newreason,String oldname,String newname,String oldemail,String newemail) {
         	VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
		Project p=ProjectManager.getProject(project);
		if (p!=null) {
			Target t=p.getTarget(target);
			t.setPackageContact(oldreason,newreason,oldname,newname,oldemail,newemail);
		}
		return true;
	}


	public boolean addPackageContact(String project,String target,String newreason,String newname,String newemail) {
         	VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
		Project p=ProjectManager.getProject(project);
		if (p!=null) {
			Target t=p.getTarget(target);
			t.addPackageContact(newreason,newname,newemail);
		}
		return true;
	}


	public boolean delPackageContact(String project,String target,String oldreason,String oldname,String oldemail) {
         	VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
		Project p=ProjectManager.getProject(project);
		if (p!=null) {
			Target t=p.getTarget(target);
			t.delPackageContact(oldreason,oldname,oldemail);
		}
		return true;
	}


	public boolean setPackageSupporter(String project,String target,String oldcompany,String newcompany) {
         	VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
		Project p=ProjectManager.getProject(project);
		if (p!=null) {
			Target t=p.getTarget(target);
			t.setPackageSupporter(oldcompany,newcompany);
		}
		return true;
	}


	public boolean addPackageSupporter(String project,String target,String newcompany) {
         	VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
		Project p=ProjectManager.getProject(project);
		if (p!=null) {
			Target t=p.getTarget(target);
			t.addPackageSupporter(newcompany);
		}
		return true;
	}


	public boolean delPackageSupporter(String project,String target,String oldcompany) {
         	VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
		Project p=ProjectManager.getProject(project);
		if (p!=null) {
			Target t=p.getTarget(target);
			t.delPackageSupporter(oldcompany);
		}
		return true;
	}


	public boolean delIncludedPackage(String project,String target,String id) {
         	VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
		Project p=ProjectManager.getProject(project);
		if (p!=null) {
			Target t=p.getTarget(target);
			t.delIncludedPackage(id);
		}
		return true;
	}


	public boolean addTargetPackage(String project,String target,String newpackage) {
		Project p=ProjectManager.getProject(project);
		if (p!=null) {
			Target t=p.getTarget(target);
			t.addPackage(newpackage);
		}
		return true;
	}


	public boolean addProject(String newprojectname,String newprojectpath) {
		ProjectManager.addProject(newprojectname,newprojectpath);
		return true;
	}


	public List getProjects() {
		// get the current best packages
		Enumeration projects=ProjectManager.getProjects();

                List list = new ArrayList();
                VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

		while (projects.hasMoreElements()) {
			Project  p=(Project)projects.nextElement();
                        MMObjectNode virtual = builder.getNewNode("admin");
			virtual.setValue("name",p.getName());
			virtual.setValue("path",p.getPath());
			virtual.setValue("syntaxerrors",p.hasSyntaxErrors());
			list.add(virtual);
		}
		return list;
	}


	public List getCreators() {
		// get the current creators we have installed
		Hashtable creators=ProjectManager.getCreators();
                List list = new ArrayList();
                VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

		Enumeration e=creators.keys();
		while (e.hasMoreElements()) {
			String key=(String)e.nextElement();
			CreatorInterface cr=(CreatorInterface)creators.get(key);

                        MMObjectNode virtual = builder.getNewNode("admin");
			virtual.setValue("name",key);
			virtual.setValue("value",cr.getClass());
			list.add(virtual);
		}
		return list;
	}

}
