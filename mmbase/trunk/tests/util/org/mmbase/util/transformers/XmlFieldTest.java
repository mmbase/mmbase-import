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

    protected String ignoreNL(StringObject in) {
        return in.toString().replaceAll("\r", "").replaceAll("\n", "");
    }


    public void testWikiToXML() {
        XmlField xmlField = new XmlField();
        result = xmlField.wikiToXML("hallo");
        assertEquals("<p>hallo</p>", result);
    }


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
        assertTrue("" + in, "<ul><li>a</li><li>b</li><li>c</li></ul>".equals(ignoreNL(in)));
        XmlField.handleParagraphs(in, true, true);
        assertTrue("" + in, "<p><ul><li>a</li><li>b</li><li>c</li></ul></p>".equals(ignoreNL(in)));
        //XmlField.placeListsOutParagraphs(in);
        // assertTrue("" + in, "<ul><li>a</li><li>b</li><li>c</li></ul>".equals(ignoreNL(in)));
    }

    public void testHandleList2() {
        StringObject in = new StringObject("Hallo\n-x\n-y\n-z\nhallo");
        XmlField.handleList(in);
        assertEquals("Hallo<ul><li>x</li><li>y</li><li>z</li></ul>hallo", ignoreNL(in));
        XmlField.handleParagraphs(in, true, true);
        assertTrue("" + in, "<p>Hallo<ul><li>x</li><li>y</li><li>z</li></ul>hallo</p>".equals(ignoreNL(in)));
        //XmlField.placeListsOutParagraphs(in);
        //assertTrue("" + in, "<p>Hallo</p><ul><li>x</li><li>y</li><li>z</li></ul><p>hallo</p>".equals(ignoreNL(in)));
    }

}
