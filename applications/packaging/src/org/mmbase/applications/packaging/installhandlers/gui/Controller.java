/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.packaging.installhandlers.gui;

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


	public boolean uninstallPackage(String id,String wv,String wp) {
		PackageInterface p=null;
		if (wv.equals("best")) {
			p=(PackageInterface)PackageManager.getPackage(id);
		} else {
			// ok lets decode the version and provider we want
			p=(PackageInterface)PackageManager.getPackage(id,wv,wp);
		}
		if (p!=null) {
			UninstallManager.uninstallPackage(p);
		}
		return true;
	}


	public boolean uninstallBundle(String id,String wv,String wb) {
		BundleInterface b=null;
		if (wv.equals("best")) {
			b=(BundleInterface)BundleManager.getBundle(id);
		} else {
			// ok lets decode the version and provider we want
			b=(BundleInterface)BundleManager.getBundle(id,wv,wb);
		}
		if (b!=null) {
			UninstallManager.uninstallBundle(b);
		}
		return true;
	}


	public boolean installPackage(String id,String wv,String wp) {
		PackageInterface p=null;
		if (wv.equals("best")) {
			p=(PackageInterface)PackageManager.getPackage(id);
		} else {
			// ok lets decode the version and provider we want
			p=(PackageInterface)PackageManager.getPackage(id,wv,wp);
		}
		if (p!=null) {
			InstallManager.installPackage(p);
		}
		return true;
	}


	public boolean installBundle(String id,String wv,String wb) {
		BundleInterface b=null;
		if (wv.equals("best")) {
			b=(BundleInterface)BundleManager.getBundle(id);
		} else {
			// ok lets decode the version and provider we want
			b=(BundleInterface)BundleManager.getBundle(id,wv,wb);
		}
		if (b!=null) {
			InstallManager.installBundle(b);
		}
		return true;
	}

}
