/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.util.*;
import java.io.*;
import java.sql.*;

import org.mmbase.module.builders.*;
import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;

/**
 *
 * images holds the images and provides ways to insert, retract and
 * search on them.
 *
 * @author Michiel Meeuwissen
 * @version $Id: AbstractImages.java,v 1.1 2002-03-05 15:30:39 michiel Exp $
 * @since   MMBase-1.6
 */
public abstract class AbstractImages extends MMObjectBuilder {
    private static Logger log = Logging.getLoggerInstance(AbstractImages.class.getName());

    /**
     * An image's gui-indicator is of course some <img src>, but it depends on what kind of image
     * (cached, original) what excactly it must be.
     **/

    abstract protected String getGUIIndicatorWithAlt(MMObjectNode node, String title);

    /**
     * Gui indicator of an whole node.
     */
    public String getGUIIndicator(MMObjectNode node) {
        return getGUIIndicatorWithAlt(node, "*");
    }

    public String getGUIIndicator(String field, MMObjectNode node) {
        if (field.equals("handle")) { 
            return getGUIIndicatorWithAlt(node, "*");
        }
        // other fields can be handled by the gui function...
        return null;
    }
    abstract public String getImageMimeType(Vector params);
    abstract public byte[] getImageBytes(Vector params);
}
        
