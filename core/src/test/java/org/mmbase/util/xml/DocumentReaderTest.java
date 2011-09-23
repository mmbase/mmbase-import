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

import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Michiel Meeuwissen
 * @verion $Id$
 */
public class DocumentReaderTest {


    private Element getElement(String s) {
        DocumentReader reader = new DocumentReader(new InputSource(new StringReader(s)), false);
        return reader.getRootElement();
    }

    @Test
    public void appendChild() {
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
    @Test
    public void appendChild2() {
        Element parent =  getElement("<a><b /></a>");
        Element c = parent.getOwnerDocument().createElement("c");
        DocumentReader.appendChild(parent, c, "b,c");
        String res = XMLWriter.write(parent, false, true);
        assertEquals(res, res, "<a><b/><c/></a>");
    }
    @Test
    public void appendChild3() {
        Element parent =  getElement("<a></a>");
        Element c = parent.getOwnerDocument().createElement("c");
        DocumentReader.appendChild(parent, c, "b,c");
        String res = XMLWriter.write(parent, false, true);
        assertEquals(res, res, "<a><c/></a>");
    }
    @Test
    public void appendChild4() {
        Element parent =  getElement("<a><b /><b /></a>");
        Element c = parent.getOwnerDocument().createElement("c");
        DocumentReader.appendChild(parent, c, "b,c");
        String res = XMLWriter.write(parent, false, true);
        assertEquals(res, res, "<a><b/><b/><c/></a>");
    }
    @Test
    public void appendChild5() {
        Element parent =  getElement("<a><b /><b /><d /></a>");
        Element c = parent.getOwnerDocument().createElement("c");
        DocumentReader.appendChild(parent, c, "b,c");
        String res = XMLWriter.write(parent, false, true);
        assertEquals(res, res, "<a><b/><b/><c/><d/></a>");
    }
    @Test
    public void appendChild6() {
        Element parent =  getElement("<a><b /><b /><d /></a>");
        Element c = parent.getOwnerDocument().createElement("c");
        DocumentReader.appendChild(parent, c, "b");
        String res = XMLWriter.write(parent, false, true);
        assertEquals(res, res, "<a><b/><b/><c/><d/></a>");
    }
    @Test
    public void appendChild7() {
        Element parent =  getElement("<a><b /><b /><c id='t' /><d /></a>");
        Element c = parent.getOwnerDocument().createElement("c");
        DocumentReader.appendChild(parent, c, "b");
        String res = XMLWriter.write(parent, false, true);
        assertEquals(res, res, "<a><b/><b/><c/><c id=\"t\"/><d/></a>");
    }
    @Test
    public void appendChild8() {
        Element parent =  getElement("<a><b /><b /><c id='t' /><d /></a>");
        Element c = parent.getOwnerDocument().createElement("c");
        DocumentReader.appendChild(parent, c, "b,c");
        String res = XMLWriter.write(parent, false, true);
        assertEquals(res, res, "<a><b/><b/><c id=\"t\"/><c/><d/></a>");
    }
    @Test
    public void appendChild9() {
        Element parent =  getElement("<a><x /></a>");
        Element c = parent.getOwnerDocument().createElement("c");
        DocumentReader.appendChild(parent, c, "(x|y),c");
        String res = XMLWriter.write(parent, false, true);
        assertEquals(res, res, "<a><x/><c/></a>");
    }
    @Test
    public void appendChild10() {
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
    @Test
    public void toDocument() {
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
