/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.logging.commons.impl;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogConfigurationException;
import org.apache.commons.logging.LogFactory;
import org.mmbase.util.logging.Logging;

/**
 * LogFactory for jakarta commons-logging who when used creates MMBase logging backed Log implementations.<br/>
 * <br/>
 * <b>Goal:</b> To provide a single log configuration for applications that use both commons-logging and mmbase logging.<br/>
 * <b>Achievement:</b> By providing a commons-logging factory that uses mmbase-logging.<br/>
 * <br/>
 * MMBaseLoggingFactory is a LogFactory for the <a href="http://jakarta.apache.org/commons/logging/">jakarta commons-logging logging</a> api.
 * MMBaseLoggingFactory uses the MMBase logging mechanism found in <a href="http://www.mmbase.org/api/org/mmbase/util/logging/package-summary.html">org.mmbase.util.logging.Logging</a>
 *  to provide the actual logging.
 *
 *
 * @author Kees Jongenburger
 * @version $Id$
 */
public class MMBaseLoggingFactory extends LogFactory {

    /**
     * The configuration attributes for this {@link LogFactory}.
     */
    private Map<String, Object> attributes = new Hashtable<String, Object>();

    // Previously returned instances, to avoid creation of proxies
    private Map<Object, Log> instances = new Hashtable<Object, Log>();

    // --------------------------------------------------------- Public Methods

    /**
     * @param name Name of the attribute to return
     * @return the configuration attribute with the specified name (if any),
     * or <code>null</code> if there is no such attribute.
     *
     */
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    /**
     * @return an array containing the names of all currently defined
     * configuration attributes.  If there are no such attributes, a zero
     * length array is returned.
     */
    public String[] getAttributeNames() {
        Vector<String> names = new Vector<String>();
        names.addAll(attributes.keySet());
        String results[] = new String[names.size()];
        for (int i = 0; i < results.length; i++) {
            results[i] = names.elementAt(i);
        }
        return results;
    }

    /**
     * This method first first looks in it's internal cache if there is a  existing Log for the given class. If that is not the case
     * the method uses org.mmbase.Version to determine the version of mmbase used and depending on the version
     * create a {@link MMBaseLogger};
     *
     * @param clazz the class for witch to create a logger
     * @return a mmbase backed Log implementationfor the given class
     */
    public Log getInstance(Class clazz) throws LogConfigurationException {
        Log instance = instances.get(clazz);
        if (instance != null)
            return instance;

        instance = new MMBaseLogger(Logging.getLoggerInstance(clazz));
        instances.put(clazz, instance);
        return instance;
    }

    /**
     * This method first first looks in it's internal cache if there is a  existing Log with the given name. If that is not the case
     * the method uses org.mmbase.Version to determine the version of mmbase used and depending on the version
     * create a {@link MMBaseLogger};
     *
     * @return a mmbase backed Log implementation for the given log
     */

    public Log getInstance(String category) throws LogConfigurationException {
        Log instance = instances.get(category);
        if (instance != null)
            return instance;

        instance = new MMBaseLogger(Logging.getLoggerInstance(category));
        instances.put(category, instance);
        return instance;
    }

    /**
      * Release any internal references to previously created {@link Log}
      * instances returned by this factory.  This is useful in environments
      * like servlet containers, which implement application reloading by
      * throwing away a ClassLoader.  Dangling references to objects in that
      * class loader would prevent garbage collection.
      */
    public void release() {

        instances.clear();
        Logging.shutdown();
    }

    /**
     * Remove any configuration attribute associated with the specified name.
     * If there is no such attribute, no action is taken.
     *
     * @param name Name of the attribute to remove
     */
    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    /**
     * Set the configuration attribute with the specified name.  Calling
     * this with a <code>null</code> value is equivalent to calling
     * <code>removeAttribute(name)</code>.
     *
     * @param name Name of the attribute to set
     * @param value Value of the attribute to set, or <code>null</code>
     *  to remove any setting for this attribute
     */
    public void setAttribute(String name, Object value) {
        if (value == null) {
            attributes.remove(name);
        } else {
            attributes.put(name, value);
        }
    }
}
