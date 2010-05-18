/*
 * This software is OSI Certified Open Source Software.
 * OSI Certified is a certification mark of the Open Source Initiative. The
 * license (Mozilla version 1.0) can be read at the MMBase site. See
 * http://www.MMBase.org/license
 */
package org.mmbase.core.event;

import java.io.*;
import java.util.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 *
 * @author  Michiel Meeuwissen
 * @since   MMBase-1.9.3
 * @version $Id: TransactionEvent.java 41369 2010-03-15 20:54:45Z michiel $
 */
public abstract class SystemEvent extends Event {
    private static final Logger LOG = Logging.getLoggerInstance(SystemEvent.class);

    public SystemEvent() {
    }

    /**
     * A SystemEvent that is also Collectable will be collected by the EventManger and also issued to EventListeners which are added after the event
     * happened.
     */
    public static abstract class  Collectable extends SystemEvent {
    }

    /**
     * Notifies that the local MMBase is now fully up and running
     */
    public static class Up extends Collectable {
    }


    /**
     * An BuilderReader system event is and should be issued after one or more builders are added to the system.
     */
    public static class BuildersRead extends Collectable {
        private final String uri;
        private final String name;
        public BuildersRead() {
            this(org.mmbase.bridge.ContextProvider.getDefaultCloudContext());
        }
        public BuildersRead(org.mmbase.bridge.CloudContext cc){
            this(cc.getUri(), cc.getCloudNames().get(0));
        }
        public BuildersRead(String u, String n) {
            uri  = u;
            name = n;
        }
        public String getUri() {
            return uri;
        }
        public String getName() {
            return name;
        }

    }


    public static class ServletContext extends Collectable  {
        private final javax.servlet.ServletContext servletContext;
        public ServletContext(javax.servlet.ServletContext sc) {
            servletContext = sc;
        }
        public javax.servlet.ServletContext getServletContext() {
            return servletContext;
        }
    }


    static {
        SystemEventListener logger = new SystemEventListener() {
                public void notify(SystemEvent s) {
                    LOG.service(" Received " + s);
                }
                @Override
                public String toString() {
                    return "SystemEventLogger";
                }
            };
        EventManager.getInstance().addEventListener(logger);
    }

}
