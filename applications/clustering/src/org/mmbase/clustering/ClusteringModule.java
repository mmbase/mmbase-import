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
import org.mmbase.util.functions.*;


/**
 * This module bootstraps and configures MMBase clustering.
 *
 * @since MMBase-1.8
 * @version $Id: ClusteringModule.java,v 1.14 2008-08-04 22:34:35 michiel Exp $
 */
public class ClusteringModule extends WatchedReloadableModule {

    private ClusterManager clusterManager = null;
    private static final  Logger log = Logging.getLoggerInstance(ClusteringModule.class);

    public ClusteringModule(String name) {
        super(name);
    }

    /* (non-Javadoc)
     * @see org.mmbase.module.Module#init()
     */
    public void init() {
        // first start MMBase!
        org.mmbase.module.core.MMBase.getMMBase();
        // then initialize the rest
        String clusterManagerClassName = getInitParameter("ClusterManagerImplementation");
        if(clusterManagerClassName != null){
            clusterManager = findInstance(clusterManagerClassName);
            EventManager.getInstance().addEventListener(clusterManager);
        }else{
            log.error("Parameter 'ClusterManagerImplementation' is missing from config file. can not load clustering");
        }

        if(clusterManager == null){
            log.error("ClusterManager loading failed.");
        }else{
            log.service("ClusterManager loaded successful");
            String compat17 = getInitParameter("mmbase17.compatible");
            clusterManager.compatible17 = "true".equals(compat17);
            if (clusterManager.compatible17) {
                log.info("Sending MMBase 1.7 compatible messages.");
            }
        }
    }

    private static ClusterManager findInstance(String className) {
        if (className == null || "".equals(className)) return null;
        try {
            Class<?> aClass = Class.forName(className);
            ClusterManager newInstance = (ClusterManager) aClass.newInstance();
            return newInstance;
        } catch (ClassNotFoundException e) {
            log.error("could not find class with name " + className, e);
        } catch (InstantiationException e) {
            log.error("could not instantiate class with name" + className, e);
        } catch (IllegalAccessException e) {
            log.error("the constructor of " + className + " is not accessible", e);
        } catch (ClassCastException e) {
            log.error("Instance of Class with name " + className + "could not be successfully cast to type ClusterManager.", e);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.mmbase.module.Module#shutdown()
     */
    protected void shutdown() {
        if(clusterManager != null) {
            clusterManager.shutdown();
            EventManager.getInstance().removeEventListener(clusterManager);
            clusterManager = null;
        }
    }


    public void reload() {
        try {
            shutdown();
        } catch (Exception e) {
            log.error(e);
        }
        init();
    }

    /**
     * @since MMBase-1.8.1
     */
    {
        addFunction(new AbstractFunction<Statistics>("send", Parameter.emptyArray(), new ReturnType<Statistics>(Statistics.class, "Stat-structure")) {
                public Statistics getFunctionValue(Parameters arguments) {
                    return clusterManager == null ? new Statistics() : clusterManager.send;
                }
            });
    }
    /**
     * @since MMBase-1.8.1
     */
    {
        addFunction(new AbstractFunction<Statistics>("receive", Parameter.emptyArray(), new ReturnType<Statistics>(Statistics.class, "Stat-structure")) {
                public Statistics getFunctionValue(Parameters arguments) {
                    return clusterManager == null ? new Statistics() : clusterManager.receive;
                }
            });
    }

    /**
     * @since MMBase-1.8.1
     */
    {
        addFunction(new AbstractFunction<Integer>("numbertosend", Parameter.emptyArray(), ReturnType.INTEGER) {
                public Integer getFunctionValue(Parameters arguments) {
                    return new Integer(clusterManager == null ? -1 : clusterManager.nodesToSend.size());
                }
            });
    }
    /**
     * @since MMBase-1.8.1
     */
    {
        addFunction(new AbstractFunction<Integer>("numbertoreceive", Parameter.emptyArray(), ReturnType.INTEGER) {
                public Integer getFunctionValue(Parameters arguments) {
                    return new Integer(clusterManager == null ? -1 : clusterManager.nodesToSpawn.size());
                }
            });
    }

    /**
     * @since MMBase-1.8.1
     */
    {
        addFunction(new AbstractFunction<Void>("shutdown", Parameter.emptyArray(), ReturnType.VOID) {
                public Void getFunctionValue(Parameters arguments) {
                    shutdown();
                    return null;
                }
            });
    }
    /**
     * @since MMBase-1.8.1
     */
    {
        addFunction(new AbstractFunction<Void>("start", Parameter.emptyArray(), ReturnType.VOID) {
                public Void getFunctionValue(Parameters arguments) {
                    init();
                    return null;
                }
            });
    }
    /**
     * @since MMBase-1.8.1
     */
    {
        addFunction(new AbstractFunction<Boolean>("active", Parameter.emptyArray(), ReturnType.BOOLEAN) {
                public Boolean getFunctionValue(Parameters arguments) {
                    return Boolean.valueOf(clusterManager != null && clusterManager.kicker != null);
                }
            });
    }
    {
        addFunction(new AbstractFunction<ClusterManager>("clusterManager", Parameter.emptyArray(), new ReturnType<ClusterManager>(ClusterManager.class, "cluster manager")) {
                public ClusterManager getFunctionValue(Parameters arguments) {
                    return clusterManager;
                }
            });
    }


}
