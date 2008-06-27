/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.packaging.bundlehandlers.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.mmbase.applications.packaging.*;
import org.mmbase.applications.packaging.bundlehandlers.*;
import org.mmbase.applications.packaging.packagehandlers.PackageInterface;
import org.mmbase.applications.packaging.providerhandlers.ProviderInterface;
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


    public List<MMObjectNode> getBundles() {
        // signal action to for package discovery
        ProviderManager.resetSleepCounter();

        // get the current best bundles
        Iterator<BundleContainer> bundles = BundleManager.getBundles();

        List<MMObjectNode> list = new ArrayList<MMObjectNode>();
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

        while (bundles.hasNext()) {
            BundleInterface  b = bundles.next();
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


    public List<MMObjectNode> getBundleVersions(String id) {
        // get the bundles of one id (all versions)
        Iterator<BundleVersionContainer> bundleversions = BundleManager.getBundleVersions(id);

        List<MMObjectNode> list = new ArrayList<MMObjectNode>();
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

        while (bundleversions.hasNext()) {
            BundleVersionContainer  bvc = bundleversions.next();

            Iterator<BundleInterface> bundles = bvc.getBundles();
            while (bundles.hasNext()) {
                BundleInterface  b = bundles.next();
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


    public List<MMObjectNode> getBundleNeededPackages(String id,String wv,String newuser) {
        // get the bundles of one id (all versions)
        BundleInterface bundle = BundleManager.getBundle(id);
        Iterator<HashMap<String, String>> neededpackages = bundle.getNeededPackages();
        List<MMObjectNode> list = new ArrayList<MMObjectNode>();
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

        while (neededpackages.hasNext()) {
            HashMap<String, String> np = neededpackages.next();

            MMObjectNode virtual = builder.getNewNode("admin");
            virtual.setValue("name",np.get("name"));
            virtual.setValue("id",np.get("id"));
            virtual.setValue("type",np.get("type"));
            virtual.setValue("maintainer",np.get("maintainer"));
            virtual.setValue("version",np.get("version"));
            PackageInterface fp = PackageManager.getPackage(np.get("id"));
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
                List<Object> l = fp.getRelatedPeople("initiators");
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


    public String getRelatedPeopleString(List<Object> people,String type) {
        String body = "";
        if (people != null) {
            for (Object object : people) {
            Person pr = (Person)object;
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
