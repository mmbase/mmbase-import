/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;
import org.mmbase.module.Module;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**

 */
public class PasswordGeneratorModule extends Module implements PasswordGeneratorInterface {

    // logger
    private static Logger log = Logging.getLoggerInstance(PasswordGeneratorModule.class);

    private final PasswordGenerator pw;

    public PasswordGeneratorModule() {
        pw = new PasswordGenerator();
    }

    /**
     * Called when the module is loaded.
     * Not used.
     */
    public void onload() {
    }

    /**
     * Called when the module is reloaded.
     * Tries to retrieve a default template for a password from the
     * template property from the module configuration file.
     * Not used.
     */
    public void reload() {
        pw.defaulttemplate = getInitParameter("template");
        if (pw.defaulttemplate == null) pw.defaulttemplate = "SSSSSS";
    }

    /**
     * Initializes the module.
     * Tries to retrieve a default template for a password from the
     * template property from the module configuration file.
     */
    public void init() {
        pw.defaulttemplate = getInitParameter("template");
        if (pw.defaulttemplate == null) pw.defaulttemplate = "SSSSSS";
    }

    /**
     * Called when the module is unloaded.
     * Not used.
     */
    public void unload() {
    }

    /**
     * Called when the module is shut down (removed).
     * Not used.
     */
    public void shutdown() {
    }


    public String getPassword() {
        return pw.getPassword();
    }
    public String getPassword(String t) {
        return pw.getPassword(t);
    }

}
