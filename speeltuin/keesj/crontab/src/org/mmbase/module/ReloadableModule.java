/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module;

import java.io.File;
import java.util.*;

import org.mmbase.module.core.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;


/**
 * A Reloadable Module has an abstract 'reload' method, and implements an onChange method which
 * calls it. You can extend your own modules from this.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.8
 * @version $Id: ReloadableModule.java,v 1.2 2004-03-29 15:13:24 michiel Exp $
 */
public abstract class ReloadableModule extends Module {

    private static final Logger log = Logging.getLoggerInstance(ReloadableModule.class);

    private FileWatcher fileWatcher = new FileWatcher() {
            public void onChange(File file) {
                reloadConfiguration(file);
                reload();
            }
        };

    /**
     * {@inheritDoc} 
     * On the onload of a reloadable module, a filewatcher is started. You should call
     * super.onload if you need to override this.
     */
    public void onload() {
        File dir = new File(MMBaseContext.getConfigPath(), "modules");        
        fileWatcher.setDelay(10 * 1000);
        fileWatcher.add(new File(dir, getName() + ".xml"));
        fileWatcher.start();
    }


    /**
     * Reloads the configuration file.
     *
     * The module cannot change class, so if you change that in the XML, an error is logged, and nothing will
     * happen.
     */
    protected void reloadConfiguration(File file) {
        // reload parameters
        XMLModuleReader parser  = new XMLModuleReader(file.getAbsolutePath());
        if (parser.getStatus().equals("inactive")) {
            log.error("Cannot set module to inactive. " + file + " Canceling reload");
            return;
        }
        String className = parser.getClassFile();
        if (! className.equals(getClass().getName())) {
            log.error("Cannot change the class of a module. " + className + " != " + getClass().getName() + " " + file + ". Canceling reload.");
            return;
        }
        
        properties =  parser.getProperties();        
        setMaintainer(parser.getModuleMaintainer());
        setVersion(parser.getModuleVersion());        
    }

    
    /**
     * This method is called when the module should be reloaded. It happens on a change of the
     * module's XML, but you can also call it from other places.
     *
     */
    
    public void reload() {
    }


}
