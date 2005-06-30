/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.util.xml.query;

import java.util.Collection;
import org.w3c.dom.*;

import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;

/**
 * Defines options for a field to index.
 *
 * @author Pierre van Rooden
 * @version $Id: FieldDefinition.java,v 1.1 2005-06-30 12:37:54 pierre Exp $
 **/
public class FieldDefinition {

    /**
     * Name of the field
     */
    public String fieldName = null;

    /**
     * Reference to (a) definition of this field in the query.
     */
    public StepField stepField = null;

    public FieldDefinition(QueryDefinition queryDefinition, Element fieldElement) {
        fieldName = fieldElement.getAttribute("name");
        try {
            stepField = queryDefinition.query.createStepField(fieldName);
        } catch (IllegalArgumentException iae) {
            // the field did not exist in the database.
            // this is possible if the field is, for instance, a bytefield that is stored on disc.
            stepField = null;
        }
    }

}

