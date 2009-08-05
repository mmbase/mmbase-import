/*
 *  This software is OSI Certified Open Source Software.
 *  OSI Certified is a certification mark of the Open Source Initiative.
 *  The license (Mozilla version 1.0) can be read at the MMBase site.
 *  See http://www.MMBase.org/license
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
 * @author     Daniel Ockeloen
 * @created    July 20, 2004
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
    public List getPackageHandlers() {
        // get the current package handlers we have installed
        HashMap packagehandlers = PackageManager.getPackageHandlers();
        List list = new ArrayList();
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

        Iterator e = packagehandlers.keySet().iterator();
        while (e.hasNext()) {
            String key = (String) e.next();
            String value = (String) packagehandlers.get(key);

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
    public List getPackages() {
        // signal action to for package discovery
        ProviderManager.resetSleepCounter();

        // get the current best packages
        Iterator packages = PackageManager.getPackages();

        List list = new ArrayList();
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

        while (packages.hasNext()) {
            PackageInterface p = (PackageInterface) packages.next();
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
    public List getPackageVersions(String id) {
        // get the packages of one id (all versions)
        Iterator packageversions = PackageManager.getPackageVersions(id);

        List list = new ArrayList();
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

        while (packageversions.hasNext()) {
            PackageVersionContainer pvc = (PackageVersionContainer) packageversions.next();
            Iterator packages = pvc.getPackages();
            while (packages.hasNext()) {
                PackageInterface p = (PackageInterface) packages.next();
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
    public List getPackageVersionNumbers(String id) {
        // get the packages of one id (all versions)
        Iterator verlist = PackageManager.getPackageVersionNumbers(id);

        List list = new ArrayList();
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
        while (verlist.hasNext()) {
            String version = (String) verlist.next();
            MMObjectNode virtual = builder.getNewNode("admin");
            virtual.setValue("version", version);
            list.add(virtual);
        }
        return list;
    }

}

