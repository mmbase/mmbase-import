package org.mmbase.util.transformers;
import org.mmbase.util.*;
import java.util.*;
import junit.framework.TestCase;

/**

 * @author Michiel Meeuwissen
 * @version $Id: SpaceReducerTest.java,v 1.2 2008-11-15 10:43:57 michiel Exp $
 */
public class SpaceReducerTest  extends TestCase {


    public SpaceReducerTest() {
    }


    protected static SpaceReducer reducer = new SpaceReducer();



    public void testBasics() {
        assertEquals("a\nb", reducer.transform("a\n\nb"));
        assertEquals("  a\n  b", reducer.transform("  a\n\n  b"));
    }

    public static void test(String line, boolean opened, boolean closed){
        SpaceReducer.Tag tag = new SpaceReducer.Tag("pre");
        tag.setLine(line);
        if (opened) assertTrue(tag.hasOpened());
        if (closed) assertTrue(tag.hasClosed());
    }

    public void testFindPre() {
        test("bladie hallo<pre> en nog wat<pre>daarna", true, false);
        test("bladie hallo<pre> en nog wat< / pre><   pre> <p>jaja</p> <a href=\"nogwat\">jaja</a>", true, false);
        test("jaja</pre>", false, true);
        test("jaja</pre> <pre> hoera</pre><p>test</p>", false, true);
        test("jaja<pre>bla <pre /></pre>filter out bodyless tags", false, false);
    }

    public void testPre() {
        assertEquals("a\nb<pre>\n\nc\n\nd</pre>\ne", reducer.transform("a\n\nb<pre>\n\nc\n\nd</pre>\n\ne"));
    }
    public void testTextarea() {
        assertEquals("a\nb<textarea>\n\nc\n\nd</textarea>\ne", reducer.transform("a\n\nb<textarea>\n\nc\n\nd</textarea>\n\ne"));
    }

}
