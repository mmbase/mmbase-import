/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util;

import java.io.File;
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
 * @version $Id: ResourceWatcher.java,v 1.1 2004-10-02 17:36:08 michiel Exp $
 * @see    FileWatcher
 * @see    ResourceLoader
 */
public abstract class ResourceWatcher implements MMBaseObserver {
    private static final Logger log = Logging.getLoggerInstance(ResourceWatcher.class);


    protected SortedSet resources = new TreeSet();
    /**
     * Wrapped FileWatcher for watching the file-resources
     */
    protected Map fileWatchers = new HashMap();

    protected ResourceLoader resourceLoader;

    protected ResourceWatcher(ResourceLoader rl) {
        resourceLoader = rl;
        if (ResourceLoader.resourceBuilder != null) {
            // TODO what happens if resourceBuilder is still null when this ResourceWatcher is instantiated ?
            ResourceLoader.resourceBuilder.addLocalObserver(this);
            ResourceLoader.resourceBuilder.addRemoteObserver(this);

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
        if (ResourceLoader.resourceBuilder != null) {
            MMObjectNode node = ResourceLoader.resourceBuilder.getNode(number);
            String name = node.getStringValue(Resources.RESOURCENAME_FIELD);
            if (resources.contains(name)) {
                onChange(name);            
            } else {
                // ignore
            }
        }
        return true;
    }


    public Set getResources() {
        return Collections.unmodifiableSortedSet(resources);
    }
    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

    /**
     *
     * @param resourceName The resource to be monitored.
     */
    public void add(String resourceName) { 
        resources.add(resourceName);
    }

    /** 
     * Adds all files associated with all resources to fileWatcher, and all nodes to an MMBaseObserver.
     */
    protected void setUp() {
        Iterator i = resources.iterator();
        while (i.hasNext()) {
            String resource = (String) i.next();
            FileWatcher fileWatcher = new ResourceFileWatcher(resource);
            fileWatchers.put(resources, fileWatcher);
        }
        // TODO: implement MMBaseObserver stuff.
    }


    public void start() {       
        log.info("Start to watch resource " + resources);
        Iterator i = fileWatchers.values().iterator();
        while (i.hasNext()) {
            FileWatcher fw = (FileWatcher) i.next();
            fw.start();
        }        
    }
    /**
     * Put here the stuff that has to be executed, when a file has been changed.
     * @param resourceName The resource that was changed.
     */
    abstract public void onChange(String resourceName);

    /**
     * Set the delay to observe between each check of the file changes.
     */
    public void setDelay(long delay) {
        Iterator i = fileWatchers.values().iterator();
        while (i.hasNext()) {
            FileWatcher fw = (FileWatcher) i.next();
            fw.setDelay(delay);
        }
    }


    /**
     */
    public void remove(String resourceName) {
        //fileWatcher.getFiles().removeAll(resourceLoader.getFiles(resourceName));
        // TODO: MMBaseObserver stuff;
    }

    /**
     * Removes all files, this watcher will end up watching nothing.
     */
    public void clear() {
        resources.clear();
        fileWatchers.clear();
        exit();
        // TODO: MMBaseObser stuff

    }

    /**
     * Stops watching.
     */
    public void exit() {
        Iterator i = fileWatchers.values().iterator();
        while (i.hasNext()) {
            FileWatcher fw = (FileWatcher) i.next();
            fw.exit();
        }
        // TODO: MMBaseObser stuff
    }

    /**
     * Shows the 'contents' of the filewatcher. It shows a list of files/last modified timestamps.
     */
    public String toString() {
        return "" + resources;
    }

    public boolean equals(Object o) {
        if (!(o instanceof ResourceWatcher)) {
            return false;
        }
        ResourceWatcher f = (ResourceWatcher)o;
        return this.getClass().equals(f.getClass()) && this.resources.equals(f.resources);
    }

    protected class ResourceFileWatcher extends FileWatcher {
        private String resource;
        ResourceFileWatcher(String resource) {
            this.resource = resource;
        }
        protected void onChange(File f) {
            // a file has changed!
            
            // should determine here if this file is not 'shadowed', in which case a warning should be logged.

            ResourceWatcher.this.onChange(resource);
        }
    }
            
 
}
