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

public class TransformingWriterTest   {


    @Test
    public void basics() throws IOException {
        Writer out = new StringWriter();
        ChainedCharTransformer t = new ChainedCharTransformer();
        t.add(new UpperCaser());
        t.add(new SpaceReducer());
        t.add(new Trimmer());
        t.add(new YUIJavaScriptCompressor());
        TransformingWriter writer = new TransformingWriter(out, t);
        String testString = "a=b;";
        writer.write(testString);
        writer.close();


    }

}
