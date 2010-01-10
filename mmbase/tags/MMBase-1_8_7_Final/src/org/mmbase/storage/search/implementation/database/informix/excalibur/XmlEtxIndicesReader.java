/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search.implementation.database.informix.excalibur;

import java.util.*;
import org.mmbase.util.xml.DocumentReader;
import org.w3c.dom.*;
import org.xml.sax.*;

/**
 *
 * @author Rob van Maris
 * @version $Id$
 * @since MMBase-1.7
 */
public class XmlEtxIndicesReader extends DocumentReader {

    /** Creates a new instance of XmlEtxIndicesReader */
    public XmlEtxIndicesReader(InputSource source) {
        super(source, true, XmlEtxIndicesReader.class);
    }

    /**
     * Gets <code>sbspace</code> elements.
     *
     * @return <code>sbspace<code> elements.
     */
    public Iterator getSbspaceElements() {
        return getChildElements("etxindices", "sbspace");
    }

    /**
     * Gets value of <code>name</code> attribute of <code>sbspace</code> element.
     *
     * @param sbspace The <code>sbspace</code> element.
     * @return Value of <code>name</code> attribute.
     */
    public String getSbspaceName(Element sbspace) {
        return getElementAttributeValue(sbspace, "name");
    }

    /**
     * Gets <code>etxindex</code> child elements of <code>sbspace</code> element.
     *
     * @param sbspace The <code>sbspace</element>
     * @return <code>etxindex</code> elements.
     */
    public Iterator getEtxindexElements(Element sbspace) {
        return getChildElements(sbspace, "etxindex");
    }

    /**
     * Gets value of <code>table</code> attribute of <code>etxindex</code> element.
     *
     * @param etxindex The <code>etxindex</code> element.
     * @return Value of <code>table</code> attribute.
     */
    public String getEtxindexTable(Element etxindex) {
        return getElementAttributeValue(etxindex, "table");
    }

    /**
     * Gets value of <code>field</code> attribute of <code>etxindex</code> element.
     *
     * @param etxindex The <code>etxindex</code> element.
     * @return Value of <code>field</code> attribute.
     */
    public String getEtxindexField(Element etxindex) {
        return getElementAttributeValue(etxindex, "field");
    }

    /**
     * Gets name of <code>etxindex</code> element.
     *
     * @param etxindex The <code>etxindex</code> element.
     * @return Value of <code>etxindex</code> element.
     */
    public String getEtxindexValue(Element etxindex) {
        return getElementValue(etxindex);
    }
}
