/*
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.logging;

/**
 * MDC stands for <em>mapped diagnostic contexts</em> See also <a href="http://logging.apache.org/log4j/1.2/apidocs/org/apache/log4j/MDC.html">log4j.MDC</a>
 * The implementation depends on the Logger implementation. An instance can be obtained with {@link Logging#getMDC}.
 * @since MMBase-1.9.2
 */

public interface MDC {

    void put(String key, Object value);
    Object get(String key);
    //void remove(String key);
}
