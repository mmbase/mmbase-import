/*
 * Created on 6-okt-2005
 *
 * This software is OSI Certified Open Source Software.
 * OSI Certified is a certification mark of the Open Source Initiative.
 *
 * The license (Mozilla version 1.0) can be read at the MMBase site.
 * See http://www.MMBase.org/license
 */
package org.mmbase.clustering;

import org.mmbase.core.event.EventManager;
import org.mmbase.module.WatchedReloadableModule;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * @since MMBase-1.8
 */
public class ClusteringModule extends WatchedReloadableModule {
    
    private ClusterManager clusterManager = null;
    private static final  Logger log = Logging.getLoggerInstance(ClusteringModule.class);

    /* (non-Javadoc)
     * @see org.mmbase.module.Module#init()
     */
    public void init() {
        String clusterManagerClassName = getInitParameter("ClusterManagerImplementation");
        if(clusterManagerClassName != null){
            try {
                clusterManager = (ClusterManager)findInstance(clusterManagerClassName);
                EventManager.getInstance().addEventListener(clusterManager);
            } catch (ClassCastException e) {
                log.error("Instance of Class with name " + clusterManagerClassName +
                          "could not be successfully cast to type ClusterManager.");
            }
        }else{
            log.error("Parameter 'ClusterManagerImplementation' is missing from config file. can not load clustering");
        }
        
        if(clusterManager == null){
            log.error("ClusterManager loading failed.");
        }else{
            log.service("ClusterManager loaded successfull");
        }
    }

    private static Object findInstance(String className) {
        if (className == null || "".equals(className)) return null;
        try {
            Class aClass = Class.forName(className);
            Object newInstance = aClass.newInstance();
            return newInstance;
            
        } catch (ClassNotFoundException e) {
            log.error("could not find class with name " + className);
            log.error(e);
        } catch (InstantiationException e) {
            log.error("could not instantiate class with name" + className);
            log.error(e);
        } catch (IllegalAccessException e) {
            log.error("the constructor of " + className + " is not accessible");
            log.error(e);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.mmbase.module.Module#shutdown()
     */
    protected void shutdown() {
        if(clusterManager != null){
            clusterManager.shutdown();
            EventManager.getInstance().removeEventListener(clusterManager);
            clusterManager = null;
        }
    }

    public void reload() {
        shutdown();
        init();
    }

}
