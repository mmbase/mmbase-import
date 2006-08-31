/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.jsp.taglib.typehandler;

import org.mmbase.bridge.jsp.taglib.*;

/**
 * @javadoc
 *
 * @author Gerard van de Looi
 * @author Michiel Meeuwissen
 * @since  MMBase-1.6
 * @version $Id: ByteHandler.java,v 1.27 2006-04-27 09:45:10 michiel Exp $
 * @deprecated Use BinaryHandler
 */

public class ByteHandler extends BinaryHandler {

    public ByteHandler(FieldInfoTag tag) {
        super(tag);
    }
}
