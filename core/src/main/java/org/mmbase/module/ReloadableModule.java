/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module;

import org.mmbase.util.xml.ModuleReader;
import org.mmbase.util.functions.*;
import org.mmbase.util.logging.*;

/**
 * A Reloadable Module has a 'reload' method. You can extend your own modules from this. If you need
 * to happen the reload automaticly, then {@link WatchedReloadableModule}.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.8
 * @version $Id$
 */
public abstract class ReloadableModule extends Module {

    private static final Logger log = Logging.getLoggerInstance(ReloadableModule.class);

    public ReloadableModule() {
    }
    public ReloadableModule(String name) {
        super(name);
    }

    /**
     * Reloads the configuration file.
     *
     * The module cannot change class, so if you change that in the XML, an error is logged, and nothing will
     * happen.
     *
     * This method should be called from your extension if and when the configuration must be reloaded.
     *
     * @return Whether successful.
     */
    protected boolean reloadConfiguration() {
        ModuleReader parser = getModuleReader();
        if (parser == null) {
            log.error("Configuration missing for module " + getName() + " with path '" + configurationPath + "': Canceling reload");
            return false;
        } else {
            return reloadConfiguration(parser);
        }
    }

    protected boolean reloadConfiguration(ModuleReader parser) {
        if (parser.getStatus().equals("inactive")) {
            log.error("Cannot set module to inactive. " + parser.getSystemId() + " Canceling reload");
            return false;
        }
        String className = parser.getClassName();
        if (! className.equals(getClass().getName())) {
            log.error("Cannot change the class of a module. " + className + " != " + getClass().getName() + " " + parser.getSystemId()  + ". Canceling reload.");
            return false;
        }

        setMaintainer(parser.getMaintainer());
        setVersion(parser.getVersion());
        properties = parser.getProperties();
        parser.getLocalizedDescription(getLocalizedDescription());
        parser.getLocalizedGUIName(getLocalizedGUIName());
        loadInitParameters();
        return true;
    }

    /**
     * This method should be called when the module should be reloaded.
     */
    public abstract void reload();

    {
        addFunction(new AbstractFunction<Void>("reload") {
                public Void getFunctionValue(Parameters arguments) {
                    ReloadableModule.this.reload();
                    return null;
                }
            });
    }

}
