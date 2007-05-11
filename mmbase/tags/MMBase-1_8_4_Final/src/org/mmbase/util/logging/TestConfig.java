/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.logging;

import org.mmbase.util.ResourceLoader;

/**
 * Class TestConfig
 *
 * @javadoc
 */

public class TestConfig {
    public static void main(String[] args) {
        String configuration = args[0];
        String category      = args[1];
        Logging.configure(ResourceLoader.getConfigurationRoot(), configuration);
        Logger log = Logging.getLoggerInstance(category);

        log.trace("a trace message");
        log.debug("a debug message");
        log.info("an info message");
        log.service("a service message");
        log.warn("a warn message");
        log.error("an error message");

        Logging.shutdown();

    }
}
