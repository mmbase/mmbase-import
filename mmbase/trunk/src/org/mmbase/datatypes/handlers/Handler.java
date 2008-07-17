/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.datatypes.handlers;

import org.mmbase.bridge.*;
import org.mmbase.storage.search.Constraint;

/**
 * Handlers can be associated to DataTypes, but different Handler can be associated with different
 * content types. The main implementation will of course be one that produces HTML, like forms, and
 * post and things like that.
 *
 * @author Michiel Meeuwissen
 * @version $Id: Handler.java,v 1.2 2008-07-17 16:27:16 michiel Exp $
 * @since MMBase-1.9.1
 */

public interface Handler<C> extends java.io.Serializable {


    /**
     * Produces an form input field for the given Node, and Field.
     * @param search if true, then a search field is produced.
     */
    C input(Request request, Node node, Field field, boolean search);



}
