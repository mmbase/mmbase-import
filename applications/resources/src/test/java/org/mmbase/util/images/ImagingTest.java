/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.images;

import java.io.*;
import java.util.*;
import org.mmbase.util.*;

import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

/**
 *
 * @author Michiel Meeuwissen
 */

@RunWith(Parameterized.class)
public class ImagingTest {

    private static final int JAI = 1 << 0;
    private static final int IM  = 1 << 1;

    private boolean I_understand_how_to_use_JAI_in_JUNIT_Test = false;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        Object[][] templates = {
            new Object[] {"s(100x60)+f(jpeg)", IM | JAI},
            new Object[] {"part(10x10x30x50)", IM | JAI},
            new Object[] {"part(10x10x2000x2000)", IM | JAI},
            new Object[] {"s(10000@)", IM},
            new Object[] {"s(100x100@)", IM},
            new Object[] {"s(10000x2000>)", IM | JAI},
            new Object[] {"s(100000x2000<)", IM | JAI},
            new Object[] {"s(4x5<)", IM | JAI},
            new Object[] {"s(4x5>)", IM | JAI},
            new Object[] {"r(90)", IM},
            new Object[] {"r(45)", IM},
            new Object[] {"r(198)", IM},
            new Object[] {"r(-30)", IM},
            new Object[] {"border(5)", IM},
            new Object[] {"border(5x8)", IM},
            new Object[] {"r(45)+border(10x20)", IM},
            new Object[] {"flip", IM | JAI},
            new Object[] {"s(100)", IM | JAI},
            new Object[] {"s(x100)", IM | JAI},
            new Object[] {"s(10x70)", IM | JAI},
            new Object[] {"s(70x10)", IM | JAI},
            new Object[] {"s(60x70!)", IM},
            new Object[] {"s(80%x150%)", IM},
            new Object[] {"s(100x100>)", IM},
            //new Object[]  "s(100x100&gt;)", Fails
            new Object[] {"s(x100)", IM | JAI},
            new Object[] {"s(100)+f(png)+r(20)+s(400x400)", IM}
        };
        File dir = new File("src" + File.separator + "test" + File.separator + "images");
        List<Object[]> data = new ArrayList<Object[]>();
        for (File f : dir.listFiles()) {
            if (f.canRead() && f.isFile()) {
                for (Object[] t : templates) {
                    data.add(new Object[] { f, t[0], t[1]});
                }
            }
        }
        return data;
    }

    private static final ImageMagickImageConverter imageMagick = new ImageMagickImageConverter();
    @BeforeClass
    public static void setUp() {
        Map<String, String> settings = new HashMap<String, String>();
        imageMagick.init(settings);
    }

    private final String template;
    private final File file;
    private final int  converters;
    public ImagingTest(File f, String t, int c) {
        file = f;
        template = t;
        converters = c;
    }

    public String info() {
        return file + ":" + template;
    }
    public byte[] bytes() throws IOException {
        FileInputStream input = new FileInputStream(file);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        IOUtil.copy(input, bytes);
        input.close();
        return  bytes.toByteArray();
    }

    protected void test(ImageConverter converter) throws IOException {
        byte[] ba = bytes();

        ImageInformer   informer   = new ImageMagickImageInformer();
        Dimension originalSize = informer.getDimension(ba);

        final List<String> params = Imaging.parseTemplate(template);
        Dimension predictedSize = Imaging.predictDimension(originalSize, params);

        byte[] converted = converter.convertImage(ba, null, params);
        assertNotNull(info(), converted);
        assertNotEquals(info(), 0, converted.length);
        Dimension convertSize  = informer.getDimension(converted);
        assertTrue(info() + ":" + originalSize + "->" + predictedSize + " != " + convertSize,
                   predictedSize.equalsIgnoreRound(convertSize, 2));
    }
    @Test
    public void jai() throws IOException {
        assumeTrue(I_understand_how_to_use_JAI_in_JUNIT_Test);
        assumeTrue((converters & JAI) > 0);
        test(new JAIImageConverter());
    }

    @Test
    public void imageMagick() throws IOException  {
        assumeTrue((converters & IM) > 0);
        assumeTrue(imageMagick.validFormats.size() > 0);
        test(imageMagick);
    }

}
