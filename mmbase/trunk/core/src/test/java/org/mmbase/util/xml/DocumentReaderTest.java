/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.xml;

import org.xml.sax.*;
import org.w3c.dom.*;
import java.io.*;
import junit.framework.TestCase;

/**
 * 
 * @author Michiel Meeuwissen
 * @verion $Id$
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

    private void testToDocument(String s) {
        Element parent1 =  getElement(s);
        Element parent2 = DocumentReader.toDocument(parent1).getDocumentElement();
        String res1 = XMLWriter.write(parent1, false, true);
        String res2 = XMLWriter.write(parent2, false, true);
        assertTrue(res1 + "!=" + res2, res1.equals(res2));
    }
    public void testToDocument() {
        String[] cases = { "<a><b /></a>", 
                           "<a xml:lang='nl'><b /></a>",
                           "<a xml:lang='nl' c='d'><b /></a>",
                           "<a xml:lang='nl' c='d'><b /><b /></a>",
                           "<a xml:lang='nl' c='d'><b /><!-- hoi --><b /></a>",
                           "<a xml:lang='nl'><b>abc</b><b c='d'>hoi<c/></b></a>"};
        for (String element : cases) {
            testToDocument(element);
        }
    }
}
