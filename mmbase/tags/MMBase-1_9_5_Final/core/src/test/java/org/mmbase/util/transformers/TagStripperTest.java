package org.mmbase.util.transformers;

import java.util.*;
import java.io.*;
import org.mmbase.util.*;
import org.mmbase.util.functions.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;

/**
 * @author Michiel Meeuwissen
 * @version $Id$
 */
public class TagStripperTest   {

    private static final TagStripperFactory FACTORY = new TagStripperFactory();


    protected CharTransformer getXSS() {
        Parameters params = FACTORY.createParameters();
        params.set(TagStripperFactory.TAGS, "XSS");
        params.set(TagStripperFactory.ADD_BRS, false);
        params.set(TagStripperFactory.ESCAPE_AMPS, true);
        CharTransformer transformer = FACTORY.createTransformer(params);
        return transformer;
    }

    @Test
    public void simple() {
        Parameters params = FACTORY.createParameters();
        CharTransformer stripper = FACTORY.createTransformer(params);
        assertEquals("aaa", stripper.transform("<p>aaa</p>"));
        assertEquals("aaa", stripper.transform("<p>aaa\n</p>"));
        assertEquals("aaa", stripper.transform("<p>aaa"));
        assertEquals("aaa", stripper.transform("<p>aaa"));
        assertEquals("aaa", stripper.transform("<p>aaa"));
        assertEquals("aaa", stripper.transform("<p><a>aaa</a></p>"));
        assertEquals("aaa <p />", stripper.transform("<p>aaa\n&lt;p /&gt;</p> "));


    }

    @Test
    public void xss() {
        CharTransformer xss = getXSS();
        assertEquals("<p style=\"nanana\">allow this <b>and this</b></p>", xss.transform("<p style=\"nanana\">allow this <b>and this</b></p>"));
        assertEquals("<p>allow this <b>and this</b></p>", xss.transform("<p onclick=\"nanana\">allow this <b>and this</b></p>"));
        assertEquals("<p>allow this</p>", xss.transform("<p>allow this<script language='text/javascript'>bj aja </script>\n</p>"));
        assertEquals("<p>allow this<a>foobar</a></p>", xss.transform("<p>allow this<a href=\"javascript:alert('hoi');\">foobar</a></p>"));

    }


    @Test
    public void addBrs() {
        Parameters params = FACTORY.createParameters();
        params.set(TagStripperFactory.ADD_BRS, true);
        CharTransformer stripper = FACTORY.createTransformer(params);
        assertEquals("aaa<br class='auto' />bbb", stripper.transform("<p>aaa\nbbb</p>"));
    }

    @Test
    public void addNewlines() {
        Parameters params = FACTORY.createParameters();
        params.set(TagStripperFactory.ADD_NEWLINES, true);
        CharTransformer stripper = FACTORY.createTransformer(params);
        assertEquals("aaa\n\nbbb", stripper.transform("<p>aaa</p><p>bbb</p>"));
        assertEquals("aaa\nbbb", stripper.transform("<p>aaa<br />bbb</p>"));
        assertEquals("aaa\nbbb", stripper.transform("<p>aaa<br>bbb</p>"));
    }

}
