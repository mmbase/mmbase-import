/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.util.xml.query;

import org.mmbase.storage.search.StepField;
import org.w3c.dom.Element;

/**
 * Defines options for a field to index.
 *
 * @author Pierre van Rooden
 * @version $Id$
 * @since MMBase-1.8
 * @javadoc
 **/
public class FieldDefinition {

    /**
     * Name of the field
     * XXX: How is this different from stepField.getFieldName() ?
     */
    public String fieldName = null;

    /**
     * Reference to (a) definition of this field in the query.
     */
    public StepField stepField = null;

    public java.util.regex.Pattern optional = null;

    /**
     * Constructor
     */
    public FieldDefinition() {
    }

    /**
     * Configures the field definition, using data from a DOM element
     */
    public void configure(Element fieldElement) {
    }
}

