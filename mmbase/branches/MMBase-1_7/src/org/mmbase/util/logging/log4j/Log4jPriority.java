/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

/*
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.logging.log4j;

/** 
 *
 * @author Michiel Meeuwissen
 * @deprecated use Log4jLevel
 **/

public class Log4jPriority extends Log4jLevel {

    protected  Log4jPriority(int level, String strLevel, int syslogEquiv) {
        super(level, strLevel, syslogEquiv);
    }

}
