/*
 *  This software is OSI Certified Open Source Software.
 *  OSI Certified is a certification mark of the Open Source Initiative.
 *  The license (Mozilla version 1.0) can be read at the MMBase site.
 *  See http://www.MMBase.org/license
 */
package org.mmbase.applications.packaging.providerhandlers.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.mmbase.applications.packaging.InstallManager;
import org.mmbase.applications.packaging.ProviderManager;
import org.mmbase.applications.packaging.providerhandlers.HttpProvider;
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
     *  Gets the providerHandlers attribute of the Controller object
     *
     * @return    The providerHandlers value
     */
    public List<MMObjectNode> getProviderHandlers() {
        // get the current provider handlers we have installed
        HashMap<String, String> providerhandlers = ProviderManager.getProviderHandlers();

        // create a result list
        List<MMObjectNode> list = new ArrayList<MMObjectNode>();
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

        Iterator<String> e = providerhandlers.keySet().iterator();
        while (e.hasNext()) {
            String key = e.next();
            String value = providerhandlers.get(key);

            MMObjectNode virtual = builder.getNewNode("admin");
            virtual.setValue("name", key);
            virtual.setValue("value", value);
            list.add(virtual);
        }
        return list;
    }


    /**
     *  Description of the Method
     *
     * @param  name        Description of the Parameter
     * @param  maintainer  Description of the Parameter
     * @param  account     Description of the Parameter
     * @param  password    Description of the Parameter
     * @param  path        Description of the Parameter
     * @return             Description of the Return Value
     */
    public boolean changeProviderSettings(String name, String maintainer, String account, String password, String path) {

        ProviderInterface provider = ProviderManager.get(name);
        if (provider != null) {
            String method = provider.getMethod();
            provider.setMaintainer(maintainer);
            provider.setPath(path);
            if (method.equals("http")) {
                ((HttpProvider) provider).setAccount(account);
                ((HttpProvider) provider).setPassword(password);
            }
        }
        ProviderManager.writeProviderFile();
        return true;
    }


    /**
     *  Description of the Method
     *
     * @param  name  Description of the Parameter
     * @return       Description of the Return Value
     */
    public boolean delProvider(String name) {
        if (ProviderManager.delete(name)) {
            // handle ok
        } else {
            // handle a problem
            return false;
        }
        ProviderManager.writeProviderFile();
        return true;
    }


    /**
     *  Adds a feature to the SubscribeProvider attribute of the Controller object
     *
     * @param  url  The feature to be added to the SubscribeProvider attribute
     * @return      Description of the Return Value
     */
    public List<MMObjectNode> addSubscribeProvider(String url) {
        List<MMObjectNode> list = new ArrayList<MMObjectNode>();
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
        MMObjectNode virtual = builder.getNewNode("admin");

        String result = ProviderManager.addSubscribeProvider(url);
        if (result != null) {
            virtual.setValue("feedback", result);
        }
        ProviderManager.writeProviderFile();

        list.add(virtual);
        return list;
    }


    /**
     *  Adds a feature to the DiskProvider attribute of the Controller object
     *
     * @param  name  The feature to be added to the DiskProvider attribute
     * @param  path  The feature to be added to the DiskProvider attribute
     * @return       Description of the Return Value
     */
    public List<MMObjectNode> addDiskProvider(String name, String path) {
        List<MMObjectNode> list = new ArrayList<MMObjectNode>();
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
        MMObjectNode virtual = builder.getNewNode("admin");

        String result = ProviderManager.addDiskProvider(name, path);
        if (result != null) {
            virtual.setValue("feedback", result);
        }
        ProviderManager.writeProviderFile();
        list.add(virtual);
        return list;
    }


    /**
     *  Gets the providers attribute of the Controller object
     *
     * @return    The providers value
     */
    public List<MMObjectNode> getProviders() {
        // signal action to for package discovery
        ProviderManager.resetSleepCounter();

        // get the current providers
        Iterator<ProviderInterface> providers = ProviderManager.getProviders();

        List<MMObjectNode> list = new ArrayList<MMObjectNode>();
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

        while (providers.hasNext()) {
            ProviderInterface provider = providers.next();
            MMObjectNode virtual = builder.getNewNode("admin");
            virtual.setValue("method", provider.getMethod());
            virtual.setValue("name", provider.getName());
            virtual.setValue("maintainer", provider.getMaintainer());
            virtual.setValue("state", provider.getState());
            list.add(virtual);
        }
        return list;
    }


    /**
     *  Gets the providerInfo attribute of the Controller object
     *
     * @param  name  Description of the Parameter
     * @return       The providerInfo value
     */
    public List<MMObjectNode> getProviderInfo(String name) {
        // get the current providers
        ProviderInterface provider = ProviderManager.get(name);

        List<MMObjectNode> list = new ArrayList<MMObjectNode>();
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

        MMObjectNode virtual = builder.getNewNode("admin");

        if (provider != null) {
            virtual.setValue("method", provider.getMethod());
            virtual.setValue("name", provider.getName());
            virtual.setValue("maintainer", provider.getMaintainer());
            virtual.setValue("state", provider.getState());
            virtual.setValue("path", provider.getPath());
            if (provider.getMethod().equals("http")) {
                virtual.setValue("account", ((HttpProvider) provider).getAccount());
                virtual.setValue("password", ((HttpProvider) provider).getPassword());
            }
        }
        list.add(virtual);
        return list;
    }


}

