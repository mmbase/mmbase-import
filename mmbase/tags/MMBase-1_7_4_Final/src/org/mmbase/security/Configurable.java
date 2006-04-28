/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security;

import java.io.File;

import org.mmbase.util.FileWatcher;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Both Authorization and Authentication are configurable.
 * This class provides the shared functionality for that.
 *
 * @author Eduard Witteveen
 * @author Michiel Meeuwissen
 * @version $Id: Configurable.java,v 1.7 2004-03-26 15:48:26 michiel Exp $
 * @since MMBase-1.7
 */
public abstract class Configurable {
    private static final Logger log = Logging.getLoggerInstance(Configurable.class);

    /**
     * The SecurityManager, which created this instance
     */
    protected MMBaseCop manager;

    /**
     * This specific security configuration file. The file is absolute. Might be
     * null if the implementation does not have its own configuruation file.
     */
    protected File configFile;

    /**
     * This filewatcher checks the configuration file for changes.
     */
    protected FileWatcher fileWatcher;


    /**
     * The method which initialized an instance of this class. This method cannot be be overrided.
     * This methods sets the member variables of this object and then
     * calls the method load();
     * @param manager The class that created this instance.
     * @param fileWatcher checks the files
     * @param configPath The url which contains the config information for the authorization (e.g. context/config.xml). Or null (if configured to be "")
     * @see #load
     */
    public final void load(MMBaseCop manager, FileWatcher fileWatcher, String configPath) {
        if (log.isDebugEnabled()) {
            log.debug("Calling load() with as config file:" + configPath);
        }
        this.manager = manager;
        this.fileWatcher = fileWatcher;
        if(configPath != null) {
            this.configFile = new File(configPath).getAbsoluteFile();
        }
        fileWatcher.setDelay(10 * 1000);

        if (configFile != null) {
            fileWatcher.add(configFile); // add the file.
        }

        load();
    }

    /**
     * This method should be overrided by an extending class.  It should further initialize the
     * class. It can optionally retrieve settings from the general security configuration file
     * (available as the 'configFile' member). Security implementations with complicated
     * configuration would typically retrieve a path to their own configuration file only.
     */
    protected abstract void load();
}
