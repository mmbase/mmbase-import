/*
 *  This software is OSI Certified Open Source Software.
 *  OSI Certified is a certification mark of the Open Source Initiative.
 *  The license (Mozilla version 1.0) can be read at the MMBase site.
 *  See http://www.MMBase.org/license
 */
package org.mmbase.applications.packaging.packagehandlers.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.mmbase.applications.packaging.InstallManager;
import org.mmbase.applications.packaging.PackageManager;
import org.mmbase.applications.packaging.ProviderManager;
import org.mmbase.applications.packaging.packagehandlers.*;
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
 * @author     Daniel Ockeloen
 * @version    $Id: guiController.java
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
     *  Gets the packageHandlers attribute of the Controller object
     *
     * @return    The packageHandlers value
     */
    public List<MMObjectNode> getPackageHandlers() {
        // get the current package handlers we have installed
        Map<String, String> packagehandlers = PackageManager.getPackageHandlers();
        List<MMObjectNode> list = new ArrayList<MMObjectNode>();
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

        Iterator<Map.Entry<String, String>> e = packagehandlers.entrySet().iterator();
        while (e.hasNext()) {
            Map.Entry<String,String> entry = e.next();
            String key = entry.getKey();
            String value = entry.getValue();

            MMObjectNode virtual = builder.getNewNode("admin");
            virtual.setValue("name", key);
            virtual.setValue("value", value);
            list.add(virtual);
        }
        return list;
    }


    /**
     *  Gets the packages attribute of the Controller object
     *
     * @return    The packages value
     */
    public List<MMObjectNode> getPackages() {
        // signal action to for package discovery
        ProviderManager.resetSleepCounter();

        // get the current best packages
        Iterator<PackageContainer> packages = PackageManager.getPackages();

        List<MMObjectNode> list = new ArrayList<MMObjectNode>();
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

        while (packages.hasNext()) {
            PackageInterface p = packages.next();
            MMObjectNode virtual = builder.getNewNode("admin");
            virtual.setValue("id", p.getId());
            virtual.setValue("name", p.getName());
            virtual.setValue("type", p.getType());
            virtual.setValue("maintainer", p.getMaintainer());
            virtual.setValue("version", p.getVersion());
            virtual.setValue("creation-date", p.getCreationDate());
            virtual.setValue("state", p.getState());
            list.add(virtual);
        }

        return list;
    }



    /**
     *  Gets the packageVersions attribute of the Controller object
     *
     * @param  id  Description of the Parameter
     * @return     The packageVersions value
     */
    public List<MMObjectNode> getPackageVersions(String id) {
        // get the packages of one id (all versions)
        Iterator<PackageVersionContainer> packageversions = PackageManager.getPackageVersions(id);

        List<MMObjectNode> list = new ArrayList<MMObjectNode>();
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

        while (packageversions.hasNext()) {
            PackageVersionContainer pvc = packageversions.next();
            Iterator<PackageInterface> packages = pvc.getPackages();
            while (packages.hasNext()) {
                PackageInterface p = packages.next();
                MMObjectNode virtual = builder.getNewNode("admin");
                virtual.setValue("id", p.getId());
                virtual.setValue("name", p.getName());
                virtual.setValue("type", p.getType());
                virtual.setValue("maintainer", p.getMaintainer());
                virtual.setValue("version", p.getVersion());
                virtual.setValue("state", p.getState());
                virtual.setValue("creation-date", p.getCreationDate());
                ProviderInterface provider = p.getProvider();
                if (provider != null) {
                    virtual.setValue("provider", provider.getName());
                }
                list.add(virtual);
            }
        }
        return list;
    }


    /**
     *  Gets the packageVersionNumbers attribute of the Controller object
     *
     * @param  id  Description of the Parameter
     * @return     The packageVersionNumbers value
     */
    public List<MMObjectNode> getPackageVersionNumbers(String id) {
        // get the packages of one id (all versions)
        Iterator<String> verlist = PackageManager.getPackageVersionNumbers(id);

        List<MMObjectNode> list = new ArrayList<MMObjectNode>();
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
        while (verlist.hasNext()) {
            String version = verlist.next();
            MMObjectNode virtual = builder.getNewNode("admin");
            virtual.setValue("version", version);
            list.add(virtual);
        }
        return list;
    }

}

