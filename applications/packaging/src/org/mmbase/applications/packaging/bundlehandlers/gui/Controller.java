/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.packaging.bundlehandlers.gui;

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
        cloud = LocalContext.getCloudContext().getCloud("mmbase");

        // hack needs to be solved
         manager = cloud.getNodeManager("typedef");
        if (manager == null) log.error("Can't access builder typedef");
        context = LocalContext.getCloudContext();
        if (!InstallManager.isRunning()) InstallManager.init();
    }


    public List getBundles() {
        // signal action to for package discovery
        ProviderManager.resetSleepCounter();

        // get the current best bundles
        Iterator bundles = BundleManager.getBundles();

        List list = new ArrayList();
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

        while (bundles.hasNext()) {
            BundleInterface  b = (BundleInterface)bundles.next();
            MMObjectNode virtual = builder.getNewNode("admin");
            virtual.setValue("id",b.getId());
            virtual.setValue("name",b.getName());
            virtual.setValue("type",b.getType());
            virtual.setValue("maintainer",b.getMaintainer());
            virtual.setValue("version",b.getVersion());
            virtual.setValue("creation-date",b.getCreationDate());
            virtual.setValue("state",b.getState());
            list.add(virtual);
        }
        return list;
    }


    public List getBundleVersions(String id) {
        // get the bundles of one id (all versions)
        Iterator bundleversions = BundleManager.getBundleVersions(id);

        List list = new ArrayList();
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

        while (bundleversions.hasNext()) {
            BundleVersionContainer  bvc = (BundleVersionContainer)bundleversions.next();

            Iterator bundles = bvc.getBundles();
            while (bundles.hasNext()) {
                BundleInterface  b = (BundleInterface)bundles.next();
                MMObjectNode virtual = builder.getNewNode("admin");
                virtual.setValue("id",b.getId());
                virtual.setValue("name",b.getName());
                virtual.setValue("type",b.getType());
                virtual.setValue("maintainer",b.getMaintainer());
                virtual.setValue("version",b.getVersion());
                virtual.setValue("state",b.getState());
                virtual.setValue("creation-date",b.getCreationDate());
                ProviderInterface provider=b.getProvider();
                if (provider != null) {
                    virtual.setValue("provider",provider.getName());
                }
                list.add(virtual);
            }
        }
        return list;
    }


    public List getBundleNeededPackages(String id,String wv,String newuser) {
        // get the bundles of one id (all versions)
        BundleInterface bundle = BundleManager.getBundle(id);
        Iterator neededpackages = bundle.getNeededPackages();
        List list = new ArrayList();
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

        while (neededpackages.hasNext()) {
	    log.info("H1");
            HashMap np = (HashMap)neededpackages.next();
	    log.info("H2="+np);

            MMObjectNode virtual = builder.getNewNode("admin");
            virtual.setValue("name",(String)np.get("name"));
            virtual.setValue("id",(String)np.get("id"));
            virtual.setValue("type",(String)np.get("type"));
            virtual.setValue("maintainer",(String)np.get("maintainer"));
            virtual.setValue("version",(String)np.get("version"));
            PackageInterface fp = PackageManager.getPackage((String)np.get("id"));
            if (fp != null) {
                String state = fp.getState();
                String provider = (fp.getProvider()).getName();
                virtual.setValue("state",state);
                virtual.setValue("provider",provider);
                virtual.setValue("creation-date",fp.getCreationDate());
                virtual.setValue("description",fp.getDescription());
                virtual.setValue("releasenotes",fp.getReleaseNotes());
                virtual.setValue("installationnotes",fp.getInstallationNotes());
                virtual.setValue("licensename",fp.getLicenseName());
                virtual.setValue("licensetype",fp.getLicenseType());
                virtual.setValue("licenseversion",fp.getLicenseVersion());
                virtual.setValue("licensebody",fp.getLicenseBody());
                List l = fp.getRelatedPeople("initiators");
                if (l != null) virtual.setValue("initiators",getRelatedPeopleString(l,"initiators"));
                l = fp.getRelatedPeople("supporters");
                if (l != null) virtual.setValue("supporters",getRelatedPeopleString(l,"supporters"));
                l = fp.getRelatedPeople("developers");
                if (l != null) virtual.setValue("developers",getRelatedPeopleString(l,"developers"));
                l = fp.getRelatedPeople("contacts");
                if (l != null) virtual.setValue("contacts",getRelatedPeopleString(l,"contacts"));
            } else {
                virtual.setValue("state","missing");
                virtual.setValue("provider","");
            }
            list.add(virtual);
        }
        return list;
    }


    public String getRelatedPeopleString(List people,String type) {
        String body = "";
        if (people != null) {
            for (Iterator i = people.iterator(); i.hasNext();) {
            Person pr = (Person)i.next();
                if (type.equals("initiators")) {
                    body += "\t\t\t<initiator name=\""+pr.getName()+"\" company=\""+pr.getCompany()+"\" />\n";
                } else if (type.equals("developers")) {
                    body += "\t\t\t<developer name=\""+pr.getName()+"\" company=\""+pr.getCompany()+"\" mailto=\""+pr.getMailto()+"\" />\n";
                } else if (type.equals("contacts")) {
                    body += "\t\t\t<contact reason=\""+pr.getReason()+"\" name=\""+pr.getName()+"\" mailto=\""+pr.getMailto()+"\" />\n";
                } else if (type.equals("supporters")) {
                    body += "\t\t\t<supporter company=\""+pr.getCompany()+"\" />\n";
                }
            }
        }
        return body;
    }

}
