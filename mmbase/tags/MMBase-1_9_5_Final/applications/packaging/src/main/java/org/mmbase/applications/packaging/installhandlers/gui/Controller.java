/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.packaging.installhandlers.gui;

import org.mmbase.applications.packaging.BundleManager;
import org.mmbase.applications.packaging.InstallManager;
import org.mmbase.applications.packaging.PackageManager;
import org.mmbase.applications.packaging.UninstallManager;
import org.mmbase.applications.packaging.bundlehandlers.BundleInterface;
import org.mmbase.applications.packaging.packagehandlers.PackageInterface;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.CloudContext;
import org.mmbase.bridge.LocalContext;
import org.mmbase.bridge.NodeManager;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


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
			p=PackageManager.getPackage(id);
		} else {
			// ok lets decode the version and provider we want
			p=PackageManager.getPackage(id,wv,wp);
		}
		if (p!=null) {
			UninstallManager.uninstallPackage(p);
		}
		return true;
	}


	public boolean uninstallBundle(String id,String wv,String wb) {
		BundleInterface b=null;
		if (wv.equals("best")) {
			b=BundleManager.getBundle(id);
		} else {
			// ok lets decode the version and provider we want
			b=BundleManager.getBundle(id,wv,wb);
		}
		if (b!=null) {
			UninstallManager.uninstallBundle(b);
		}
		return true;
	}


	public boolean installPackage(String id,String wv,String wp) {
		PackageInterface p=null;
		if (wv.equals("best")) {
			p=PackageManager.getPackage(id);
		} else {
			// ok lets decode the version and provider we want
			p=PackageManager.getPackage(id,wv,wp);
		}
		if (p!=null) {
			InstallManager.installPackage(p);
		}
		return true;
	}


	public boolean installBundle(String id,String wv,String wb) {
		BundleInterface b=null;
		if (wv.equals("best")) {
			b=BundleManager.getBundle(id);
		} else {
			// ok lets decode the version and provider we want
			b=BundleManager.getBundle(id,wv,wb);
		}
		if (b!=null) {
			InstallManager.installBundle(b);
		}
		return true;
	}

}
