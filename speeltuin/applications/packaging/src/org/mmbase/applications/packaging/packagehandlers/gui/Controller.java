/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.packaging.packagehandlers.gui;

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

	private static Logger log = Logging.getLoggerInstance(Controller.class.getName());
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



	public List getPackageHandlers() {
		// get the current package handlers we have installed
		Hashtable packagehandlers=PackageManager.getPackageHandlers();
                List list = new ArrayList();
                VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

		Enumeration e=packagehandlers.keys();
		while (e.hasMoreElements()) {
			String key=(String)e.nextElement();
			String value=(String)packagehandlers.get(key);

                        MMObjectNode virtual = builder.getNewNode("admin");
			virtual.setValue("name",key);
			virtual.setValue("value",value);
			list.add(virtual);
		}
		return list;
	}

	public List getPackages() {
		// get the current best packages
		Enumeration packages=PackageManager.getPackages();

                List list = new ArrayList();
                VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

		while (packages.hasMoreElements()) {
			PackageInterface  p=(PackageInterface)packages.nextElement();
                        MMObjectNode virtual = builder.getNewNode("admin");
			virtual.setValue("id",p.getId());
			virtual.setValue("name",p.getName());
			virtual.setValue("type",p.getType());
			virtual.setValue("maintainer",p.getMaintainer());
			virtual.setValue("version",p.getVersion());
			virtual.setValue("creation-date",p.getCreationDate());
			virtual.setValue("state",p.getState());
			list.add(virtual);
		}

		return list;
	}



	public List getPackageVersions(String id) {
		// get the packages of one id (all versions)
		Enumeration packageversions=PackageManager.getPackageVersions(id);

                List list = new ArrayList();
                VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

		while (packageversions.hasMoreElements()) {
			PackageVersionContainer  pvc=(PackageVersionContainer)packageversions.nextElement();

			Enumeration packages=pvc.getPackages();
			while (packages.hasMoreElements()) {
				PackageInterface  p=(PackageInterface)packages.nextElement();
                        	MMObjectNode virtual = builder.getNewNode("admin");
				virtual.setValue("id",p.getId());
				virtual.setValue("name",p.getName());
				virtual.setValue("type",p.getType());
				virtual.setValue("maintainer",p.getMaintainer());
				virtual.setValue("version",p.getVersion());
				virtual.setValue("state",p.getState());
				virtual.setValue("creation-date",p.getCreationDate());
				ProviderInterface provider=p.getProvider();
				if (provider!=null) {
					virtual.setValue("provider",provider.getName());
				}
				list.add(virtual);
			}
		}

		return list;
	}


	public List getPackageVersionNumbers(String id) {
		// get the packages of one id (all versions)
		Enumeration verlist=PackageManager.getPackageVersionNumbers(id);

                List list = new ArrayList();
                VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
		while (verlist.hasMoreElements()) {
			String version=(String)verlist.nextElement();
                       	MMObjectNode virtual = builder.getNewNode("admin");
			virtual.setValue("version",version);
			list.add(virtual);
		}
		return list;
	}

}
