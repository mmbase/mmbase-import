package org.mmbase.util.transformers;
import org.mmbase.util.*;
import org.mmbase.util.functions.*;
import java.util.*;
import junit.framework.TestCase;

/**
 * @author Michiel Meeuwissen
 * @version $Id$
 */
public class TagStripperTest  extends TestCase {

    private static TagStripperFactory factory = new TagStripperFactory();




    public void testSimple() {
        Parameters params = factory.createParameters();
        CharTransformer stripper = factory.createTransformer(params);

        assertEquals("aaa", stripper.transform("<p>aaa</p>").trim());
        assertEquals("aaa", stripper.transform("<p>aaa\n</p>").trim());
        assertEquals("aaa", stripper.transform("<p>aaa").trim());
        assertEquals("aaa", stripper.transform("<p>aaa").trim());
        assertEquals("aaa", stripper.transform("<p>aaa").trim());
        assertEquals("aaa <p />", stripper.transform("<p>aaa\n&lt;p /&gt;</p> ").trim());


    }
}
