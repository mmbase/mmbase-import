package org.mmbase.util.transformers;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Michiel Meeuwissen
 * @version $Id$
 */
public class TagStripperTest   {

    private static final TagStripperFactory FACTORY = new TagStripperFactory();


    protected CharTransformer getXSS() {
        CharTransformer transformer = TagStripper.createXSSStripper().addBrs(false).escapeAmps(true);
        return transformer;
    }

    @Test
    public void simple() {
        CharTransformer stripper = TagStripper.createAllStripper();
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
        CharTransformer stripper = TagStripper.createAllStripper().addBrs(true);
        assertEquals("aaa<br class='auto' />bbb", stripper.transform("<p>aaa\nbbb</p>"));
        //assertEquals("aaa<br class='auto' />bbb", stripper.transform("aaa\nbbb<"));
    }

    @Test
    public void addNewlines() {
        CharTransformer stripper = TagStripper.createAllStripper().addNewlines(true);
        assertEquals("aaa\n\nbbb", stripper.transform("<p>aaa</p><p>bbb</p>"));
        assertEquals("aaa\nbbb", stripper.transform("<p>aaa<br />bbb</p>"));
        assertEquals("aaa\nbbb", stripper.transform("<p>aaa<br>bbb</p>"));
        assertEquals("aaa\nbbb", stripper.transform("aaa\nbbb"));

    }

    @Test
    public void conserveNewLines() {
        CharTransformer stripper = TagStripper.createAllStripper().conserveNewlines(true);
        assertEquals("aaa\nbbb", stripper.transform("<p>aaa\nbbb</p>"));
        assertEquals("aaa\nbbb", stripper.transform("aaa\nbbb"));
    }


    @Test
    public void conserveNewLinesAndAddNewLInes() {
        CharTransformer stripper = TagStripper.createAllStripper().conserveNewlines(true).addNewlines(true);
        assertEquals("aaa\nbbb", stripper.transform("<p>aaa\nbbb</p>"));
        assertEquals("aaa\nbbb", stripper.transform("aaa\nbbb"));
    }
}
