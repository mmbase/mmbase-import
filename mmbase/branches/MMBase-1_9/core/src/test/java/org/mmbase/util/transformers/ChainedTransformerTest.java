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
 * @version $Id: TagStripperTest.java 39716 2009-11-16 13:37:49Z michiel $
 */
public class ChainedTransformerTest   {

    @Test
    public void basic() {
        ChainedCharTransformer t = new ChainedCharTransformer().add(new UnicodeEscaper()).add(new SpaceReducer()).add(new UpperCaser()).add(new Trimmer());

        assertEquals("TEST TEST TEST TEST", t.transform("  test test    test test "));
        assertEquals("TEST TEST TEST TEST", t.transform(new StringReader("  test test    test test "), new StringWriter()).toString());

    }

    @Test
    public void javascript() {
        ChainedCharTransformer t = new ChainedCharTransformer().add(CopyCharTransformer.INSTANCE).add(new YUIJavaScriptCompressor());
        System.out.println(t.transform("function a() {}"));
        System.out.println(t.transform(new StringReader("function a() {}"), new StringWriter()).toString());
    }

}
