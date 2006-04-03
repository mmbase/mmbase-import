/*
 * Tests for org.mmbase.util.transformers.XmlField
 * Currently only tests a small part of the XmlField functionality.
 * TODO: implement complete test.
 *
 * @author Simon Groenewolt (simon@submarine.nl)
 */

package org.mmbase.util.transformers;
import org.mmbase.util.*;
import junit.framework.TestCase;


/**
 *
 * @author Administrator
 */
public class XmlFieldTest  extends TestCase {

    private String result;
    private XmlField xmlField;

    /** Creates a new instance of XmlFieldTest */
    public XmlFieldTest() {
        xmlField = new XmlField();
    }

    public void testStripNewLines() {
        assertEquals("", stripNewlinesAndReturns("\n\n\n\r\r\n\r\n"));
    }

    /**
     *
     */
    public void testWikiToXML() {
        XmlField xmlField = new XmlField();
        result = xmlField.wikiToXML("hallo");
        assertEquals("<p>hallo</p>", result);
    }

    /**
     * test HTML_BLOCK_BR - richToHTMLBlock(r, true, true)
     * an empty string schould return an empty p element
     */
    public void testRichToHTMLBlock1() {

        result = xmlField.richToHTMLBlock("");
        assertEquals("<p></p>", result);
    }


    public void testRichToHTMLBlock1a() {

        result = xmlField.richToHTMLBlock("hallo");
        assertEquals("<p>hallo</p>", result);
    }

    public void testRichToHTMLBlock2() {
        result = xmlField.richToHTMLBlock("hallo\n\nhallo");
        assertEquals("<p>hallo</p><p>hallo</p>", result);
    }

    public void testRichToHTMLBlock3() {
        // input:
        // hallo
        // -eending
        // -nogeending
        // hallo
        result = xmlField.richToHTMLBlock("hallo\n-eending\n-nogeending\nhallo");

//        System.out.println("\nresultaat: " + result);
//        System.out.println("\ngewenst  : " + "<p>hallo</p><ul><li>eending</li><li>nogeending</li></ul><p>hallo</p>");
        assertEquals("<p>hallo</p><ul><li>eending</li><li>nogeending</li></ul><p>hallo</p>", result);
    }

    private String stripNewlinesAndReturns(String s) {
        StringBuffer buf;
        buf = new StringBuffer(s);
        while (true) {
            int i = buf.indexOf("\n");
            if (i > -1 ) {
               buf.deleteCharAt(i);
            } else {
                break;
            }
        }
        while (true) {
            int i = buf.indexOf("\r");
            if (i > -1 ) {
               buf.deleteCharAt(i);
            } else {
                break;
            }
        }
        return buf.toString();
    }

    public void testRichToHTMLBlock4() {
        // input:
        // hallo
        //
        // -eending
        // -nogeending
        //
        // hallo
        result = xmlField.richToHTMLBlock("hallo\n\n-eending\n-nogeending\n\nhallo");
        // test voor bug #6741
        // bovenstaande leverde
        // "<p>hallo<br /><ul><li>eending</li><li>nogeending</li></ul></p><p>hallo</p>"
        // op - dat is geen valid html (wel valid xhtm2)
        assertEquals("<p>hallo</p><ul><li>eending</li><li>nogeending</li><ul><p>hallo</p>", result);
    }

    public void testRichToHTMLBlock5() {
        // input:
        // hallo
        // *eending
        // *nogeending
        // hallo
        result = xmlField.richToHTMLBlock("hallo\n*eending\n*nogeending\nhallo");
        // System.out.println(result);
        assertEquals("<p>hallo</p><ol><li>eending</li><li>nogeending</li></ol><p>hallo</p>", result);
    }

    /**
     * Tests handling lists only
     */
    public void testHandleList() {
        StringObject in = new StringObject("-a\n-b\n-c");
        XmlField.handleList(in);
        assertEquals("\n<ul>\n<li>a</li>\n<li>b</li>\n<li>c</li>\n</ul>\n", in.toString().replaceAll("\r", "\n"));
    }
    public void testHandleList2() {
        StringObject in = new StringObject("Hallo\n-a\n-b\n-c\nhallo");
        XmlField.handleList(in);
        assertEquals("Hallo\n<ul>\n<li>a</li>\n<li>b</li>\n<li>c</li>\n</ul>\nhallo", in.toString().replaceAll("\r", "\n"));
    }


}
