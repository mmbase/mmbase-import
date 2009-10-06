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

        assertEquals("aaa", stripper.transform("<p>aaa</p>").trim());
        assertEquals("aaa", stripper.transform("<p>aaa\n</p>").trim());
        assertEquals("aaa", stripper.transform("<p>aaa").trim());
        assertEquals("aaa", stripper.transform("<p>aaa").trim());
        assertEquals("aaa", stripper.transform("<p>aaa").trim());
        assertEquals("aaa <p />", stripper.transform("<p>aaa\n&lt;p /&gt;</p> ").trim());


    }


    @Test
    public void xss() {
        CharTransformer xss = getXSS();
        assertEquals("<p style=\"nanana\">allow this <b>and this</b></p>", xss.transform("<p style=\"nanana\">allow this <b>and this</b></p>").trim());
        assertEquals("<p>allow this <b>and this</b></p>", xss.transform("<p onclick=\"nanana\">allow this <b>and this</b></p>").trim());
        assertEquals("<p>allow this</p>", xss.transform("<p>allow this<script language='text/javascript'>bj aja </script>\n</p>").trim());
        assertEquals("<p>allow this<a>foobar</a></p>", xss.transform("<p>allow this<a href=\"javascript:alert('hoi');\">foobar</a></p>").trim());


        /*
        assertEquals("<p style=\"nanana\">hoi hoi\n<br><table width=\"45\" height=99 border='1\"' font=\"bold\" styLe=\"\n\\\"one\">\nbla bla bla</table></p>",
                     transformer.transform("<P sTyle=\"nanana\">hoi hoi\n<br><table WIDTH=\"45\" height=99 border='1\"' fONt=bold styLe=\"\none\">\nbla bla bla</table></p>").trim());
        */

    }

}
