/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.datatypes.resources;


/**
 * States, as used in the 'state' field of the {@link org.mmbase.module.builders.MMServers}
 * builders. Used in test-cases too.
 *
 * @author  Michiel Meeuwissen
 * @version $Id$
 * @since   MMBase-1.8.1
 */
public interface StateConstants {

    static final int UNKNOWN = -1;
    static final int ACTIVE = 1;
    static final int INACTIVE = 2;
    static final int ERROR = 3;

}

