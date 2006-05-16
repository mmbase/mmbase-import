/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.xml;

import java.util.*;
import org.xml.sax.*;
import org.w3c.dom.*;
import java.io.*;
import junit.framework.TestCase;

/**
 * 
 * @author Michiel Meeuwissen
 * @verion $Id: DocumentReaderTest.java,v 1.2 2006-05-16 21:07:47 michiel Exp $
 */
public class DocumentReaderTest extends TestCase {


    private Element getElement(String s) {
        DocumentReader reader = new DocumentReader(new InputSource(new StringReader(s)), false); 
        return reader.getRootElement();
    }

    public void testAppendChild() {
        Element parent = getElement("<a />");
        {
            String res = XMLWriter.write(parent, false, true);
            assertEquals(res + " != <a/>", res, "<a/>");
        }

        Element b = parent.getOwnerDocument().createElement("b");
        DocumentReader.appendChild(parent, b, "b");
        {
            String res = XMLWriter.write(parent, false, true);
            assertEquals(res, res, "<a><b/></a>");
        }
    }
    public void testAppendChild2() {
        Element parent =  getElement("<a><b /></a>");
        Element c = parent.getOwnerDocument().createElement("c");
        DocumentReader.appendChild(parent, c, "b,c");
        String res = XMLWriter.write(parent, false, true);
        assertEquals(res, res, "<a><b/><c/></a>");
    }
    public void testAppendChild3() {
        Element parent =  getElement("<a></a>");
        Element c = parent.getOwnerDocument().createElement("c");
        DocumentReader.appendChild(parent, c, "b,c");
        String res = XMLWriter.write(parent, false, true);
        assertEquals(res, res, "<a><c/></a>");
    }
    public void testAppendChild4() {
        Element parent =  getElement("<a><b /><b /></a>");
        Element c = parent.getOwnerDocument().createElement("c");
        DocumentReader.appendChild(parent, c, "b,c");
        String res = XMLWriter.write(parent, false, true);
        assertEquals(res, res, "<a><b/><b/><c/></a>");
    }
    public void testAppendChild5() {
        Element parent =  getElement("<a><b /><b /><d /></a>");
        Element c = parent.getOwnerDocument().createElement("c");
        DocumentReader.appendChild(parent, c, "b,c");
        String res = XMLWriter.write(parent, false, true);
        assertEquals(res, res, "<a><b/><b/><c/><d/></a>");
    }
    public void testAppendChild6() {
        Element parent =  getElement("<a><b /><b /><d /></a>");
        Element c = parent.getOwnerDocument().createElement("c");
        DocumentReader.appendChild(parent, c, "b");
        String res = XMLWriter.write(parent, false, true);
        assertEquals(res, res, "<a><b/><b/><c/><d/></a>");
    }
    public void testAppendChild7() {
        Element parent =  getElement("<a><b /><b /><c id='t' /><d /></a>");
        Element c = parent.getOwnerDocument().createElement("c");
        DocumentReader.appendChild(parent, c, "b");
        String res = XMLWriter.write(parent, false, true);
        assertEquals(res, res, "<a><b/><b/><c/><c id=\"t\"/><d/></a>");
    }
    public void testAppendChild8() {
        Element parent =  getElement("<a><b /><b /><c id='t' /><d /></a>");
        Element c = parent.getOwnerDocument().createElement("c");
        DocumentReader.appendChild(parent, c, "b,c");
        String res = XMLWriter.write(parent, false, true);
        assertEquals(res, res, "<a><b/><b/><c id=\"t\"/><c/><d/></a>");
    }
    public void testAppendChild9() {
        Element parent =  getElement("<a><x /></a>");
        Element c = parent.getOwnerDocument().createElement("c");
        DocumentReader.appendChild(parent, c, "(x|y),c");
        String res = XMLWriter.write(parent, false, true);
        assertEquals(res, res, "<a><x/><c/></a>");
    }
    public void testAppendChild10() {
        Element parent =  getElement("<a><q /></a>");
        Element c = parent.getOwnerDocument().createElement("c");
        DocumentReader.appendChild(parent, c, "(x|y),c");
        String res = XMLWriter.write(parent, false, true);
        assertEquals(res, res, "<a><c/><q/></a>");
    }
}
