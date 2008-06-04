package org.mmbase.util.transformers;
import org.mmbase.util.*;
import java.util.*;
import junit.framework.TestCase;

/**
 * Tests for org.mmbase.util.transformers.XmlField
 * Currently only tests a small part of the XmlField functionality.
 *
 * @author Simon Groenewolt (simon@submarine.nl)
 * @author Michiel Meeuwissen
 * @version $Id: XmlFieldTest.java,v 1.10 2008-06-04 14:46:14 michiel Exp $
 */
public class XmlFieldTest  extends TestCase {

    private String result;
    private String expectedResult;
    private String expectedListResult;
    private String comment;
    private String listData;
    private boolean leaveExtraNewLines;
    private boolean surroundingP;
    private boolean placeListsInsideP;

    /** Creates a new instance of XmlFieldTest */
    public XmlFieldTest() {
    }

    protected String ignoreNL(StringObject in) {
        return in.toString().replaceAll("\r", "").replaceAll("\n", "");
    }

    protected String showNL(StringObject in) {
        return in.toString().replaceAll("\r", "R").replaceAll("\n", "N");
    }

    public void testRichToHTMLBlock1() {

        result = XmlField.richToHTMLBlock("");
        expectedResult = "<p></p>";
        assertTrue("\n" + expectedResult + "\n!=\n" + result, expectedResult.equals(result));
    }

    public void testRichToHTMLBlock1a() {

        result = XmlField.richToHTMLBlock("hallo");
        expectedResult = "<p>hallo</p>";
        assertTrue("\n" + expectedResult + "\n!=\n" + result, expectedResult.equals(result));
    }

    public void testRichToHTMLBlock2() {
        result = XmlField.richToHTMLBlock("hallo\n\nhallo");
        expectedResult = "<p>hallo</p><p>hallo</p>";
        assertTrue("\n" + expectedResult + "\n!=\n" + result, expectedResult.equals(result));
    }

    public void testRichToHTMLBlock3() {
        // input:
        // hallo
        // -eending
        // -nogeending
        // hallo
//        result = xmlField.richToHTMLBlock("hallo\n-eending\n-nogeending\nhallo");
        StringObject in = new StringObject("hallo\n- eending\n- nogeending\nhallo");
        XmlField.handleRich(in,
                            XmlField.NO_SECTIONS,
                            XmlField.REMOVE_NEWLINES,
                            XmlField.SURROUNDING_P,
                            XmlField.LISTS_INSIDE_P);
        result = ignoreNL(in);
        expectedResult = "<p>hallo<ul><li>eending</li><li>nogeending</li></ul>hallo</p>";
        assertTrue("\n" + expectedResult + "\n!=\n" + result, expectedResult.equals(result));
    }

    public void testRichToHTMLBlock4() {
        // input:
        // hallo
        //
        // -eending
        // -nogeending
        //
        // hallo
        result = XmlField.richToHTMLBlock("hallo\n\n- eending\n- nogeending\n\nhallo");
        expectedResult = "<p>hallo</p><p><ul><li>eending</li><li>nogeending</li></ul></p><p>hallo</p>";
        assertTrue("\n" + expectedResult + "\n!=\n" + result, expectedResult.equals(result));
    }

    public void testRichToHTMLBlock5() {
        // input:
        // hallo
        // *eending
        // *nogeending
        // hallo
        result = XmlField.richToHTMLBlock("hallo\n* eending\n* nogeending\nhallo");
        expectedResult = "<p>hallo<ol><li>eending</li><li>nogeending</li></ol>hallo</p>";
        assertTrue("\n" + expectedResult + "\n!=\n" + result, expectedResult.equals(result));
    }

    public static String[][] RICH_TO_XML_CASES = {

        {"$TITEL\nhallo\n* eending\n* nogeending\nhallo",
         "<section><h>TITEL</h><p>hallo<ol><li>eending</li><li>nogeending</li></ol>hallo</p></section>"},
        {"$TITEL\n\n$$SUBTITEL\nhallo\n* eending\n* nogeending\nhallo",
         "<section><h>TITEL</h><section><h>SUBTITEL</h><p>hallo<ol><li>eending</li><li>nogeending</li></ol>hallo</p></section></section>"},
        {"$TITEL\n\n$$SUBTITEL\n\n_test_\neenalinea\n\nnogeenalinea\n\nhallo",
         "<section><h>TITEL</h><section><h>SUBTITEL</h><p><em>test</em><br />eenalinea</p><p>nogeenalinea</p><p>hallo</p></section></section>"},
        {"$TITEL\n\n$$SUBTITEL\nhallo\n* eending\n* nogeending",
         "<section><h>TITEL</h><section><h>SUBTITEL</h><p>hallo<ol><li>eending</li><li>nogeending</li></ol></p></section></section>"},
        {"$TITEL\n\n$$SUBTITEL\nhallo\n* eending\n* nogeending\n\nbla bla",
         "<section><h>TITEL</h><section><h>SUBTITEL</h><p>hallo<ol><li>eending</li><li>nogeending</li></ol></p><p>bla bla</p></section></section>"},
        {"$TITEL\n\n$$SUBTITEL\n*hallo* hoe gaat het",
         "<section><h>TITEL</h><section><h>SUBTITEL</h><p><strong>hallo</strong> hoe gaat het</p></section></section>"},

        {"$TITEL\n\n$$SUBTITEL\n* a\n* b\n* c",
         "<section><h>TITEL</h><section><h>SUBTITEL</h><p><ol><li>a</li><li>b</li><li>c</li></ol></p></section></section>"} //MMB-1654
        ,
        {"$TITEL\n\n$$SUBTITEL\n\n* a\n* b\n* c",
         "<section><h>TITEL</h><section><h>SUBTITEL</h><p><ol><li>a</li><li>b</li><li>c</li></ol></p></section></section>"},
        {"$TITEL\n\n$$SUBTITEL\n\n* a\n* b\n* c\nbla",
         "<section><h>TITEL</h><section><h>SUBTITEL</h><p><ol><li>a</li><li>b</li><li>c</li></ol>bla</p></section></section>"},
        {"$TITEL\n\n$$SUBTITEL\n\n* a\n* b\n* c\n\nbloe",
         "<section><h>TITEL</h><section><h>SUBTITEL</h><p><ol><li>a</li><li>b</li><li>c</li></ol></p><p>bloe</p></section></section>"}

    };

    public void testRichToXML() {
        List<String> errors = new ArrayList<String>();
        for (String[] testCase : RICH_TO_XML_CASES) {
            StringObject in = new StringObject(testCase[0]);
            XmlField.handleRich(in,
                                XmlField.SECTIONS,
                                XmlField.LEAVE_NEWLINES,
                                XmlField.SURROUNDING_P,
                                XmlField.LISTS_INSIDE_P);
            XmlField.handleNewlines(in);
            result         = ignoreNL(in);
            expectedResult = testCase[1];
            if (! expectedResult.equals(result)) {
                errors.add("\n" + expectedResult + "\n!=\n" + result);
            }
        }
        assertTrue("" + errors, errors.size() == 0);

    }




    /**
     * Tests handling lists only
     */
    public void listTest() {
        StringObject in = new StringObject(listData);
        XmlField.handleList(in);
        String list = showNL(in);
        result =ignoreNL(in);
        assertTrue("\n"+ comment + listData + ":\n" + expectedListResult + "\nexpected, but found\n" + result, expectedListResult.equals(result));
        XmlField.handleParagraphs(in, leaveExtraNewLines, surroundingP, placeListsInsideP);
        result =ignoreNL(in);
        assertTrue("\n"+ comment + ":\n"+ listData + " (" + list + "):\n" +
           expectedResult + "\nexpected, but found\n" + result, expectedResult.equals(result));
    }

    /**
     * Tests handling lists only
     */
    public void testHandleListTTF() {
        comment = "HTML_BLOCK_LIST_BR";
        leaveExtraNewLines = true;
        surroundingP = true;
        placeListsInsideP = false;

        listData = "- a\n- b\n- c";
        expectedListResult = "<ul><li>a</li><li>b</li><li>c</li></ul>";
        expectedResult = "<ul><li>a</li><li>b</li><li>c</li></ul>";
        listTest();

        listData = "Hallo\n- x\n- y\n- z\nhallo";
        expectedListResult = "Hallo<ul><li>x</li><li>y</li><li>z</li></ul>hallo";
        expectedResult = "<p>Hallo</p><ul><li>x</li><li>y</li><li>z</li></ul><p>hallo</p>";
        listTest();

        listData = "\n\n- x\n- y\n- z\n\n";
        expectedListResult = "<ul><li>x</li><li>y</li><li>z</li></ul>";
        expectedResult = "<p></p><ul><li>x</li><li>y</li><li>z</li></ul><p></p>";
        listTest();
    }

    /**
     * Tests handling lists only
     */
    public void testHandleListTFF() {
        comment = "HTML_BLOCK_LIST_BR_NOSURROUNDINGP";
        leaveExtraNewLines = true;
        surroundingP = false;
        placeListsInsideP = false;

        listData = "- a\n- b\n- c";
        expectedListResult = "<ul><li>a</li><li>b</li><li>c</li></ul>";
        expectedResult = "</p><ul><li>a</li><li>b</li><li>c</li></ul><p>";
        listTest();

        listData = "Hallo\n- x\n- y\n- z\nhallo";
        expectedListResult = "Hallo<ul><li>x</li><li>y</li><li>z</li></ul>hallo";
        expectedResult = "Hallo</p><ul><li>x</li><li>y</li><li>z</li></ul><p>hallo";
        listTest();

        listData = "\n\n- x\n- y\n- z\n\n";
        expectedListResult = "<ul><li>x</li><li>y</li><li>z</li></ul>";
        expectedResult = "</p><ul><li>x</li><li>y</li><li>z</li></ul><p>";
        listTest();
    }

    /**
     * Tests handling lists only
     */
    public void testHandleListTTT() {
        comment = "HTML_BLOCK_BR";
        leaveExtraNewLines = true;
        surroundingP = true;
        placeListsInsideP = true;

        listData = "- a\n- b\n- c";
        expectedListResult = "<ul><li>a</li><li>b</li><li>c</li></ul>";
        expectedResult = "<p><ul><li>a</li><li>b</li><li>c</li></ul></p>";
//        listTest();

        listData = "Hallo\n- x\n- y\n- z\nhallo";
        expectedListResult = "Hallo<ul><li>x</li><li>y</li><li>z</li></ul>hallo";
        expectedResult = "<p>Hallo<ul><li>x</li><li>y</li><li>z</li></ul>hallo</p>";
        listTest();

        listData = "\n\n- x\n- y\n- z\n\n";
        expectedListResult = "<ul><li>x</li><li>y</li><li>z</li></ul>";
        expectedResult = "<p></p><p><ul><li>x</li><li>y</li><li>z</li></ul></p><p></p>";
        listTest();
    }

    /**
     * Tests handling lists only
     */
    public void testHandleListTFT() {
        comment = "HTML_BLOCK_BR_NOSURROUNDINGP";
        leaveExtraNewLines = true;
        surroundingP = false;
        placeListsInsideP = true;

        listData = "- a\n- b\n- c";
        expectedListResult = "<ul><li>a</li><li>b</li><li>c</li></ul>";
        expectedResult = "<ul><li>a</li><li>b</li><li>c</li></ul>";
        listTest();

        listData = "Hallo\n- x\n- y\n- z\nhallo";
        expectedListResult = "Hallo<ul><li>x</li><li>y</li><li>z</li></ul>hallo";
        expectedResult = "Hallo<ul><li>x</li><li>y</li><li>z</li></ul>hallo";
        listTest();

        listData = "\n\n- x\n- y\n- z\n\n";
        expectedListResult = "<ul><li>x</li><li>y</li><li>z</li></ul>";
        expectedResult = "</p><p><ul><li>x</li><li>y</li><li>z</li></ul></p><p>";
        listTest();
    }


}
