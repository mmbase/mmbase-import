/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import org.mmbase.module.core.*;

/**
 * The resources builder can be used by {@link org.mmbase.util.ResourceLoader} to load resources from
 * (configuration files, classes, resourcebundles).
 *
 * @author Michiel Meeuwissen
 * @version $Id: Resources.java,v 1.2 2005-01-30 16:46:38 nico Exp $
 * @since   MMBase-1.8
 */
public class Resources extends Attachments {

    // these should perhaps be configurable:
    public static final String    RESOURCENAME_FIELD  = "name";
    public static final String    TYPE_FIELD          = "type";
    public static final String    FILENAME_FIELD      = "filename";
    public static final String    HANDLE_FIELD        = "handle";
    public static final String    LASTMODIFIED_FIELD  = "lastmodified";
    public static final String    DEFAULT_CONTEXT     = "admin";


    public static final int   TYPE_CONFIG  = 0;
    public static final int   TYPE_WEB     = 1;

    /**
     * Registers this builder in the ResourceLoader.
     * {@inheritDoc}
     */
    public boolean init() {
        boolean res = super.init();
        if (res) {
            org.mmbase.util.ResourceLoader.setResourceBuilder(this);
        } 
        return res;

    }

    /**
     * Implements virtual filename field.
     * {@inheritDoc}
     */
    public Object getValue(MMObjectNode node, String field) {
        if (field.equals(FILENAME_FIELD)) {
            String s = node.getStringValue(RESOURCENAME_FIELD);
            int i = s.lastIndexOf("/");
            if (i > 0) {
                return s.substring(i + 1);
            } else {
                return s;
            }
        } else {
            return super.getValue(node, field);
        }
    }

}
