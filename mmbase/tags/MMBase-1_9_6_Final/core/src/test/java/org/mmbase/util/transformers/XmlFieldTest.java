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
 * @version $Id$
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

    protected static UnicodeEscaper unicode = new UnicodeEscaper();
    static {
        unicode.setEscapeLow(true);
    }

    protected String showNL(StringObject in) {
        return unicode.transform(in.toString());
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
    public static int IN                      = 0;
    public static int AFTER_PREHANDLE_HEADERS = 1;
    public static int AFTER_HANDLE_LIST       = 2;
    public static int AFTER_HANDLE_TABLES     = 3;
    public static int AFTER_HANDLE_PARAGRAPHS = 4;
    public static int AFTER_HANDLE_HEADERS    = 5;
    public static int AFTER_HANDLE_EM         = 6;
    public static int AFTER_NEWLINES          = 7;

    public static String[][] RICH_TO_XML_CASES = {
        {"$TITEL\nhallo\n* eending\n* nogeending\nhallo",                                                //IN
         "$TITEL\nhallo\n* eending\n* nogeending\nhallo",                                                // PREHANDLE_HEADERS
         "$TITEL\nhallo\n<ol><li>eending</li><li>nogeending</li></ol>\nhallo",                           // LIST
         "$TITEL\nhallo\n<ol><li>eending</li><li>nogeending</li></ol>\nhallo",                           // TABLES
         "<p>$TITEL\nhallo<ol><li>eending</li><li>nogeending</li></ol>hallo</p>",                        // PARAGRAGPS
         "<section><h>TITEL</h><p>hallo<ol><li>eending</li><li>nogeending</li></ol>hallo</p></section>", // HEADERS
         null,
         null
        },

        {"$TITEL\n\n$$SUBTITEL\nhallo\n* eending\n* nogeending\nhallo",
         "$TITEL\n\n$$SUBTITEL\nhallo\n* eending\n* nogeending\nhallo",                                  // PRE_HANDLE
         "$TITEL\n\n$$SUBTITEL\nhallo\n<ol><li>eending</li><li>nogeending</li></ol>\nhallo",             // LIST
         "$TITEL\n\n$$SUBTITEL\nhallo\n<ol><li>eending</li><li>nogeending</li></ol>\nhallo",             // TABLES
         "<p>$TITEL</p><p>$$SUBTITEL\nhallo<ol><li>eending</li><li>nogeending</li></ol>hallo</p>",       // PARAGRAPHS
         "<section><h>TITEL</h><section><h>SUBTITEL</h><p>hallo<ol><li>eending</li><li>nogeending</li></ol>hallo</p></section></section>",   // HEADERS
         null,
         null
        },
        {"$TITEL\n\n$$SUBTITEL\n\n_test_\neenalinea\n\nnogeenalinea\n\nhallo",                            // IN
         "$TITEL\n\n$$SUBTITEL\n\n_test_\neenalinea\n\nnogeenalinea\n\nhallo",                            // PRE_HANDLE
         "$TITEL\n\n$$SUBTITEL\n\n_test_\neenalinea\n\nnogeenalinea\n\nhallo",                            // LIST
         "$TITEL\n\n$$SUBTITEL\n\n_test_\neenalinea\n\nnogeenalinea\n\nhallo",                            // TABLES
         "<p>$TITEL</p><p>$$SUBTITEL</p><p>_test_\neenalinea</p><p>nogeenalinea</p><p>hallo</p>",         // PARAGRAPHS
         "<section><h>TITEL</h><section><h>SUBTITEL</h><p>_test_\neenalinea</p><p>nogeenalinea</p><p>hallo</p></section></section>",         // HEADERS
         "<section><h>TITEL</h><section><h>SUBTITEL</h><p><em>test</em>\neenalinea</p><p>nogeenalinea</p><p>hallo</p></section></section>",
         "<section><h>TITEL</h><section><h>SUBTITEL</h><p><em>test</em><br />eenalinea</p><p>nogeenalinea</p><p>hallo</p></section></section>"
        },
        {"$TITEL\n\n$$SUBTITEL\nhallo\n* eending\n* nogeending",
         "$TITEL\n\n$$SUBTITEL\nhallo\n* eending\n* nogeending",                                          // PRE
         "$TITEL\n\n$$SUBTITEL\nhallo\n<ol><li>eending</li><li>nogeending</li></ol>",                     //LIST
         null,
         "<p>$TITEL</p><p>$$SUBTITEL\nhallo<ol><li>eending</li><li>nogeending</li></ol></p>",             //  PARAGRAPHS
         "<section><h>TITEL</h><section><h>SUBTITEL</h><p>hallo<ol><li>eending</li><li>nogeending</li></ol></p></section></section>",      // HEADERS
         "<section><h>TITEL</h><section><h>SUBTITEL</h><p>hallo<ol><li>eending</li><li>nogeending</li></ol></p></section></section>",
         null
        },

        {"$TITEL\n\n$$SUBTITEL\nhallo\n* eending\n* nogeending\n\nbla bla",
         "$TITEL\n\n$$SUBTITEL\nhallo\n* eending\n* nogeending\n\nbla bla",
         "$TITEL\n\n$$SUBTITEL\nhallo\n<ol><li>eending</li><li>nogeending</li></ol>\n\nbla bla",            // LIST
         null,
         "<p>$TITEL</p><p>$$SUBTITEL\nhallo<ol><li>eending</li><li>nogeending</li></ol></p><p>bla bla</p>", // PARAGRAPH
         "<section><h>TITEL</h><section><h>SUBTITEL</h><p>hallo<ol><li>eending</li><li>nogeending</li></ol></p><p>bla bla</p></section></section>", // HEADERS
         null,
         null
        },

        {"$TITEL\n\n$$SUBTITEL\n*hallo* hoe gaat het",
         "$TITEL\n\n$$SUBTITEL\n\n*hallo* hoe gaat het",                                             // EM starting the paragraph, is fixed by PRE
         null,
         null,
         "<p>$TITEL</p><p>$$SUBTITEL</p><p>*hallo* hoe gaat het</p>",                                // PARAGRAPH
         "<section><h>TITEL</h><section><h>SUBTITEL</h><p>*hallo* hoe gaat het</p></section></section>", // SECTION
         "<section><h>TITEL</h><section><h>SUBTITEL</h><p><strong>hallo</strong> hoe gaat het</p></section></section>", // EM
         null
        },

        {"$TITEL\n\n$$SUBTITEL\n* a\n* b\n* c",
         "$TITEL\n\n$$SUBTITEL\n\n* a\n* b\n* c",                                                    //MMB-1654
         "$TITEL\n\n$$SUBTITEL\n\n<ol><li>a</li><li>b</li><li>c</li></ol>",                          //LIST
         null,
         "<p>$TITEL</p><p>$$SUBTITEL</p><p><ol><li>a</li><li>b</li><li>c</li></ol></p>",             //P
         "<section><h>TITEL</h><section><h>SUBTITEL</h><p><ol><li>a</li><li>b</li><li>c</li></ol></p></section></section>",   //H
         "<section><h>TITEL</h><section><h>SUBTITEL</h><p><ol><li>a</li><li>b</li><li>c</li></ol></p></section></section>",
         null
        }
        ,
        {"$TITEL\n\n$$SUBTITEL\n\n* a\n* b\n* c",
         null,
         "$TITEL\n\n$$SUBTITEL\n\n<ol><li>a</li><li>b</li><li>c</li></ol>",   //L
         null,
         "<p>$TITEL</p><p>$$SUBTITEL</p><p><ol><li>a</li><li>b</li><li>c</li></ol></p>",   //P
         "<section><h>TITEL</h><section><h>SUBTITEL</h><p><ol><li>a</li><li>b</li><li>c</li></ol></p></section></section>",   //H
         "<section><h>TITEL</h><section><h>SUBTITEL</h><p><ol><li>a</li><li>b</li><li>c</li></ol></p></section></section>",
         null
        },
        {"$TITEL\n\n$$SUBTITEL\n\n* a\n* b\n* c\nbla",
         null,
         "$TITEL\n\n$$SUBTITEL\n\n<ol><li>a</li><li>b</li><li>c</li></ol>\nbla",  // L   TODO, I think the \n before bla is incorrect
         null,
         "<p>$TITEL</p><p>$$SUBTITEL</p><p><ol><li>a</li><li>b</li><li>c</li></ol>\nbla</p>",  // P
         "<section><h>TITEL</h><section><h>SUBTITEL</h><p><ol><li>a</li><li>b</li><li>c</li></ol>\nbla</p></section></section>",  // H
         "<section><h>TITEL</h><section><h>SUBTITEL</h><p><ol><li>a</li><li>b</li><li>c</li></ol>\nbla</p></section></section>",
         null
        },
        {"$TITEL\n\n$$SUBTITEL\n\n* a\n* b\n* c\n\nbloe",
         null,
         "$TITEL\n\n$$SUBTITEL\n\n<ol><li>a</li><li>b</li><li>c</li></ol>\n\nbloe", // L
         null,
         "<p>$TITEL</p><p>$$SUBTITEL</p><p><ol><li>a</li><li>b</li><li>c</li></ol></p><p>bloe</p>", // P
         "<section><h>TITEL</h><section><h>SUBTITEL</h><p><ol><li>a</li><li>b</li><li>c</li></ol></p><p>bloe</p></section></section>", // H
         "<section><h>TITEL</h><section><h>SUBTITEL</h><p><ol><li>a</li><li>b</li><li>c</li></ol></p><p>bloe</p></section></section>",
         null
        },
        {"* a\n* b\n* c\n*d",
         null,
         "<ol><li>a</li><li>b</li><li>c</li></ol>\n*d", // L
         null,                                         // T
         "<p><ol><li>a</li><li>b</li><li>c</li></ol>\n*d</p>", // P
         "<p><ol><li>a</li><li>b</li><li>c</li></ol>\n*d</p>", // H
         "<p><ol><li>a</li><li>b</li><li>c</li></ol>\n*d</p>", // EM
         "<p><ol><li>a</li><li>b</li><li>c</li></ol>*d</p>", // NL
        },
        {// Starting input with new list
            "* a\n* b\n* c\n*",
            null,
            "<ol><li>a</li><li>b</li><li>c</li></ol>\n*", // L
            null,                                         // T
            "<p><ol><li>a</li><li>b</li><li>c</li></ol>\n*</p>", // P
            "<p><ol><li>a</li><li>b</li><li>c</li></ol>\n*</p>", // H
            "<p><ol><li>a</li><li>b</li><li>c</li></ol>\n*</p>", // EM
            "<p><ol><li>a</li><li>b</li><li>c</li></ol>*</p>" // NL
        },
        {// List, starting with NL, UL-lists
            "\n- a\n- b\n- c",
            null,
            "\n<ul><li>a</li><li>b</li><li>c</li></ul>", // L
            null,                                         // T
            "<p><ul><li>a</li><li>b</li><li>c</li></ul></p>", // P
            "<p><ul><li>a</li><li>b</li><li>c</li></ul></p>", // H
            "<p><ul><li>a</li><li>b</li><li>c</li></ul></p>", // EM
            "<p><ul><li>a</li><li>b</li><li>c</li></ul></p>" // NL
        }
        ,
        {// Lists in lists
            "* a\n* b\n** b1\n** b2\n* c",
            null,
            "<ol><li>a</li><li>b<ol><li>b1</li><li>b2</li></ol></li><li>c</li></ol>", // L
            null,                                                                       // T
            "<p><ol><li>a</li><li>b<ol><li>b1</li><li>b2</li></ol></li><li>c</li></ol></p>", //P
            null, // H
            null, //EM
            "<p><ol><li>a</li><li>b<ol><li>b1</li><li>b2</li></ol></li><li>c</li></ol></p>" //NL
        },
        {// Lists in lists, two different ones
            "* b\n* b\n*- b1\n*- b2\n* c",
            null,
            "<ol><li>b</li><li>b<ul><li>b1</li><li>b2</li></ul></li><li>c</li></ol>", // L
            null,                                                                       // T
            "<p><ol><li>b</li><li>b<ul><li>b1</li><li>b2</li></ul></li><li>c</li></ol></p>", //P
            null, // H
            null, //EM
            "<p><ol><li>b</li><li>b<ul><li>b1</li><li>b2</li></ul></li><li>c</li></ol></p>" //NL
        }

    };



    protected StringObject testRich(List<String> errors, StringObject in, String expectedResult, String intro) {
        if (expectedResult != null && in != null) {
            String result = in.toString();
            if (! expectedResult.equals(result)) {
                errors.add("\n\n" + intro + "\nE:" + unicode.transform(expectedResult) + "\n!=\nR:" + unicode.transform(result) + "");
            }
            //return in;
        }
        return expectedResult != null ? new StringObject(expectedResult) : in;

    }

    public void testRichToXML() {
        List<String> errors = new ArrayList<String>();
        for (String[] testCase : RICH_TO_XML_CASES) {
            StringObject in = XmlField.prepareData(testCase[IN]);
            if (testCase.length == 8) {
                XmlField.preHandleHeaders(in);
                in = testRich(errors, in,  testCase[AFTER_PREHANDLE_HEADERS], "PRE");
                XmlField.handleList(in);
                in = testRich(errors, in,  testCase[AFTER_HANDLE_LIST], "LIST");
                XmlField.handleTables(in);
                in = testRich(errors, in, testCase[AFTER_HANDLE_TABLES], "TABLES");
                XmlField.handleParagraphs(in, XmlField.LEAVE_NEWLINES, XmlField.SURROUNDING_P, XmlField.LISTS_INSIDE_P);
                in = testRich(errors, in, testCase[AFTER_HANDLE_PARAGRAPHS], "PARAGRAPHS");
                XmlField.handleHeaders(in);
                in = testRich(errors, in, testCase[AFTER_HANDLE_HEADERS], "HEADERS");
                XmlField.handleEmph(in, '_', "em");
                XmlField.handleEmph(in, '*', "strong");
                testRich(errors, in, testCase[AFTER_HANDLE_EM], "EM");
                XmlField.handleNewlines(in);
                testRich(errors, in, testCase[AFTER_NEWLINES], "NL");
            } else {
                XmlField.handleRich(in,
                                    XmlField.SECTIONS,
                                    XmlField.LEAVE_NEWLINES,
                                    XmlField.SURROUNDING_P,
                                    XmlField.LISTS_INSIDE_P);
                result         = ignoreNL(in);
                expectedResult = testCase[1];
                if (! expectedResult.equals(result)) {
                    errors.add("\n" + expectedResult + "\n!=\n" + result);
                }
            }
                //XmlField.handleNewlines(in);

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
        result = ignoreNL(in);
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
