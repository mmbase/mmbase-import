/*
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.logging.log4j2;

//import org.mmbase.util.logging.Logger;
//import org.mmbase.util.logging.Level;
//import org.mmbase.util.logging.Logging;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.xml.DOMConfigurator;

import java.io.PrintStream;
import java.io.File;
import java.util.Vector;

/**

 * @author Michiel Meeuwissen 
 */

public final class LoggerRepository extends org.apache.log4j.Hierarchy implements org.apache.log4j.spi.LoggerRepository {
    private LoggerFactory defaultFactory;

    public  LoggerRepository(Logger root) {
        super(root);
        defaultFactory = new MMCategoryFactory();
    }


    public Logger getLogger(String name) {
        return getLogger(name, defaultFactory);
    }

}

class MMCategoryFactory implements LoggerFactory {
    
    MMCategoryFactory() {
    }    
    
    public Logger makeNewLoggerInstance(String name) {
        return new Log4jImpl(name);
    }    
}
