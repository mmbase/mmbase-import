package org.mmbase.util.transformers;
import org.mmbase.util.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;

/**

 * @author Michiel Meeuwissen
 * @version $Id$
 */
public class SpaceReducerTest   {


    protected static SpaceReducer reducer = new SpaceReducer();


    @Test
    public void basics() {
        assertEquals("a\nb", reducer.transform("a\n\nb"));
        assertEquals("  a\n  b", reducer.transform("  a\n\n  b"));
    }

    protected static void test(String line, boolean opened, boolean closed){
        SpaceReducer.Tag tag = new SpaceReducer.Tag("pre");
        tag.setLine(line);
        if (opened) assertTrue(tag.hasOpened());
        if (closed) assertTrue(tag.hasClosed());
    }

    @Test
    public void findPre() {
        test("bladie hallo<pre> en nog wat<pre>daarna", true, false);
        test("bladie hallo<pre> en nog wat< / pre><   pre> <p>jaja</p> <a href=\"nogwat\">jaja</a>", true, false);
        test("jaja</pre>", false, true);
        test("jaja</pre> <pre> hoera</pre><p>test</p>", false, true);
        test("jaja<pre>bla <pre /></pre>filter out bodyless tags", false, false);
    }

    @Test
    public void pre() {
        assertEquals("a\nb<pre>\n\nc\n\nd</pre>\ne", reducer.transform("a\n\nb<pre>\n\nc\n\nd</pre>\n\ne"));
    }
    @Test
    public void textarea() {
        assertEquals("a\nb<textarea>\n\nc\n\nd</textarea>\ne", reducer.transform("a\n\nb<textarea>\n\nc\n\nd</textarea>\n\ne"));
    }

}
