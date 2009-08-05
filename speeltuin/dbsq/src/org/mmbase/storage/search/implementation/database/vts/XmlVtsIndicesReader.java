/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search.implementation.database.vts;

import java.util.*;
import org.mmbase.util.XMLBasicReader;
import org.w3c.dom.*;
import org.xml.sax.*;

/**
 *
 * @author Rob van Maris
 * @version $Id: XmlVtsIndicesReader.java,v 1.4 2003-12-23 10:59:07 robmaris Exp $
 * @since MMBase-1.7
 */
public class XmlVtsIndicesReader extends XMLBasicReader {
    
    /** Creates a new instance of XmlVtsIndicesReader */
    public XmlVtsIndicesReader(InputSource source) {
        super(source, true, XmlVtsIndicesReader.class);
    }
    
    /**
     * Gets value of <code>nolog</code> attribute.
     *
     * @return Value of <code>nolog</code> attribute.
     */
    public boolean getNolog() {
        String stringValue = getElementAttributeValue("vtsindices", "nolog");
        return Boolean.valueOf(stringValue).booleanValue();
    }
    
    /**
     * Gets <code>sbspace</code> elements.
     *
     * @return <code>sbspace<code> elements.
     */
    public Enumeration getSbspaceElements() {
        return getChildElements("vtsindices", "sbspace");
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
     * Gets <code>vtsindex</code> child elements of <code>sbspace</code> element.
     *
     * @param sbspace The <code>sbspace</element>
     * @return <code>vtsindex</code> elements.
     */
    public Enumeration getVtsindexElements(Element sbspace) {
        return getChildElements(sbspace, "vtsindex");
    }
    
    /**
     * Gets value of <code>table</code> attribute of <code>vtsindex</code> element.
     *
     * @param vtsindex The <code>vtsindex</code> element.
     * @return Value of <code>table</code> attribute.
     */
    public String getVtsindexTable(Element vtsindex) {
        return getElementAttributeValue(vtsindex, "table");
    }
    
    /**
     * Gets value of <code>field</code> attribute of <code>vtsindex</code> element.
     *
     * @param vtsindex The <code>vtsindex</code> element.
     * @return Value of <code>field</code> attribute.
     */
    public String getVtsindexField(Element vtsindex) {
        return getElementAttributeValue(vtsindex, "field");
    }
    
    /**
     * Gets name of <code>vtsindex</code> element.
     *
     * @param vtsindex The <code>vtsindex</code> element.
     * @param Value of <code>vtsindex</code> element.
     */
    public String getVtsindexValue(Element vtsindex) {
        return getElementValue(vtsindex);
    }
}
