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
 * @version $Id: ReloadableModule.java,v 1.1 2004-03-26 14:48:22 michiel Exp $
 */
public abstract class ReloadableModule extends Module {

    private static final Logger log = Logging.getLoggerInstance(ReloadableModule.class);

    private FileWatcher fileWatcher = new FileWatcher() {
            public void onChange(File file) {
                // reload parameters
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
     * This method is called when the module should be reloaded. It happens on a change of the
     * module's XML, but you can also call it from other places.
     *
     * The module cannot change class, so if you change that in the XML, an error is logged, and nothing will
     * happen.
     *
     * If more needs to happen, then you can override this method, and call super.reload() in it.
     */
    
    public void reload() {
        File dir = new File(MMBaseContext.getConfigPath(), "modules");        
        File file = new File(dir, getName() + ".xml");
        XMLModuleReader parser  = new XMLModuleReader(file.getAbsolutePath());
        if (parser.getStatus().equals("inactive")) {
            log.warn("Cannot set module to inactive. " + file);
            return;
        }
        String className = parser.getClassFile();
        if (! className.equals(getClass().getName())) {
            log.error("Cannot change the class of a module. " + className + " != " + getClass().getName() + " " + file);
        }
            
        properties =  parser.getProperties();        
        setMaintainer(parser.getModuleMaintainer());
        setVersion(parser.getModuleVersion());
    }


}
