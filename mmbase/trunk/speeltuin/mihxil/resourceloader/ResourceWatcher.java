/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util;

import java.io.File;
import java.net.URL;
import java.util.*;


import org.mmbase.module.core.MMBaseObserver;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.module.core.MMBase;
import org.mmbase.module.builders.Resources;

import org.mmbase.util.logging.*;

/**
 *  Like {@link #FileWatcher} but for Resources. If (one of the) file(s) to which the resource resolves
 *  to is added or changed, it onChange will be triggered, if not a 'more important' wil was
 *  existing already. If a file is removed, and was the most important one, it will be removed from the filewatcher.
 
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.8
 * @version $Id: ResourceWatcher.java,v 1.4 2004-10-12 19:34:54 michiel Exp $
 * @see    FileWatcher
 * @see    ResourceLoader
 */
public abstract class ResourceWatcher implements MMBaseObserver {
    private static final Logger log = Logging.getLoggerInstance(ResourceWatcher.class);

    /**
     * All instantiated ResourceWatchers. Only used until setResourceBuilder is called. Then it
     * is set to null, and not used any more (also used in ResourceLoader).
     * 
     */
    static  Set resourceWatchers = new HashSet();

    /**
     * Considers all resource-watchers. Perhaps onChange
     */
    static void setResourceBuilder() {
        Iterator i = resourceWatchers.iterator();
        while (i.hasNext()) {
            ResourceWatcher rw = (ResourceWatcher) i.next();
            rw.addObservers();
            Iterator j = rw.resources.iterator();
            while (j.hasNext()) {
                String resource = (String) j.next();
                URL u = rw.resourceLoader.findResource(resource);
                if (u == null) continue;
                if (u.toExternalForm().startsWith(ResourceLoader.NODE_URL_CONTEXT.toExternalForm())) {
                    // it would resolve to a  node!!
                    log.service("ResourceBuilder is available now. Resource " + resource + " must be reloaded.");
                    rw.onChange(resource);
                }                
            }
        }
        resourceWatchers = null; // no need to store those any more.
    }

    private long delay = -1;

    protected SortedSet resources = new TreeSet();
    protected Map       nodeNumberToResourceName = new HashMap();

    private boolean running = false;

    /**
     * Wrapped FileWatcher for watching the file-resources
     */
    protected Map fileWatchers = new HashMap();

    protected ResourceLoader resourceLoader;

    protected ResourceWatcher(ResourceLoader rl) {
        resourceLoader = rl;
        if (resourceWatchers != null) {
            resourceWatchers.add(this);
        }
    }
    protected ResourceWatcher() {
        this(ResourceLoader.getRoot());
    }


    public boolean nodeRemoteChanged(String machine, String number, String builder, String ctype) {
        return nodeChanged(number, ctype);
    }
    public boolean nodeLocalChanged(String machine, String number, String builder, String ctype) {
        return nodeChanged(number, ctype);
    }

    protected boolean nodeChanged(String number, String ctype) {       
        if (ctype.equals("d")) { 
            // hard..
            String name = (String) nodeNumberToResourceName.get(number);
            if (name != null && resources.contains(name)) {
                nodeNumberToResourceName.remove(number);
                log.service("Resource " + name + " changed (node removed)");
                onChange(name);
            }
        } else {
            MMObjectNode node = ResourceLoader.resourceBuilder.getNode(number);
            String name = node.getStringValue(Resources.RESOURCENAME_FIELD);
            if (resources.contains(name)) {
                if (ctype.equals("n")) {
                    log.service("Resource " + name + " changed (node added)");
                    nodeNumberToResourceName.put(number, name);
                } else {
                    log.service("Resource " + name + " changed (node changed)");
                }
                onChange(name);
            }
        }

        return true;
    }


    /**
     * @return unmodifiable set of String of watched resources 
     */
    public Set getResources() {
        return Collections.unmodifiableSortedSet(resources);
    }

    /**
     * The associated ResourceLoader
     */
    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

    /**
     *
     * @param resourceName The resource to be monitored.
     */
    public synchronized void add(String resourceName) { 
        resources.add(resourceName);
        if (running) {
            createFileWatchers(resourceName);
            ResourceLoader.Resolver resolver = resourceLoader.getResolver(resourceName);
            mapNodeNumber(resolver, resourceName);
        }
    }

    protected synchronized void createFileWatchers(String resource) {
        FileWatcher fileWatcher = new ResourceFileWatcher(resource);
        if (delay != -1) {
            fileWatcher.setDelay(delay);
        }
        fileWatcher.getFiles().addAll(resourceLoader.getFiles(resource));
        fileWatcher.start(); // filewatchers are only created on start, so must always be started themselves.
        fileWatchers.put(resources, fileWatcher);
    }
    protected synchronized void mapNodeNumber(ResourceLoader.Resolver resolver, String resource) {
        MMObjectNode node = resolver.getResourceNode();
        if (node != null) {
            nodeNumberToResourceName.put("" + node.getNumber(), resource);            
        }
            
    }


    protected synchronized void addObservers() {
        ResourceLoader.resourceBuilder.addLocalObserver(this);
        ResourceLoader.resourceBuilder.addRemoteObserver(this);
    }


    public synchronized void start() {
        // create and start all filewatchers.
        Iterator i = resources.iterator();
        while (i.hasNext()) {
            String resource = (String) i.next();
            ResourceLoader.Resolver resolver = resourceLoader.getResolver(resource);
            resolver.checkShadowedNewerResources();
            mapNodeNumber(resolver, resource);
            createFileWatchers(resource);     

        }

        if (ResourceLoader.resourceBuilder != null) {
            addObservers();
        } else {
            log.info("No rseource-builder to register to.");
        } 
        running = true;
    }


    /**
     * Put here the stuff that has to be executed, when a file has been changed.
     * @param resourceName The resource that was changed.
     */
    abstract public void onChange(String resourceName);

    /**
     * Set the delay to observe between each check of the file changes.
     */
    public synchronized void setDelay(long delay) {
        this.delay = delay;
        Iterator i = fileWatchers.values().iterator();
        while (i.hasNext()) {
            FileWatcher fw = (FileWatcher) i.next();
            fw.setDelay(delay);
        }
    }


    /**
     */
    public synchronized void remove(String resourceName) {
        boolean wasRunning = running;
        if (running) { // it's simplest like this.
            exit();
        }
        resources.remove(resourceName);
        if (wasRunning) {
            start();
        }
    }

    /**
     * Removes all resources. 
     */
    public synchronized  void clear() {
        if (running) {
            exit();
            resources.clear();
            start();
        } else {
            resources.clear();
        }
    }

    /**
     * Completely restarts this ResourceWatcher.
     */
    protected synchronized void restart() {
        if (running) {
            log.service("Restarting " + this);
            exit();
        }
        start();
    }

    /**
     * Stops watching. Stops all filewatchers, removes observers.
     */
    public synchronized void exit() {
        Iterator i = fileWatchers.values().iterator();
        while (i.hasNext()) {
            FileWatcher fw = (FileWatcher) i.next();
            fw.exit();
            i.remove();
        }
        if (ResourceLoader.resourceBuilder != null) {
            ResourceLoader.resourceBuilder.removeLocalObserver(this);
            ResourceLoader.resourceBuilder.removeRemoteObserver(this);
        } 
        running = false;
    }


    /**
     * Shows the 'contents' of the filewatcher. It shows a list of files/last modified timestamps.
     */
    public String toString() {
        return "" + resources + " " + fileWatchers;
    }

    /**
     * 
     */

    protected class ResourceFileWatcher extends FileWatcher {
        private String resource;
        ResourceFileWatcher(String resource) {
            this.resource = resource;
        }
        protected void onChange(File f) {
            URL shadower = resourceLoader.getResolver(resource).shadowed(f);
            if (shadower == null) {
                ResourceWatcher.this.onChange(resource);
            } else {
                log.warn("File " + f + " changed, but it is shadowed by " + shadower);
            }
        }
    }
            
 
}
