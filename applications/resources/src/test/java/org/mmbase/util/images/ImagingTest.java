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

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        String[] templates = {
            "s(100x60)+f(jpeg)",
            "part(10x10x30x50)",
            "part(10x10x2000x2000)",
            "s(10000@)",  "s(100x100@)",
            "s(10000x2000>)", "s(100000x2000<)",
            "s(4x5<)", "s(4x5>)",
            "r(90)", "r(45)", "r(198)", "r(-30)",
            "border(5)", "border(5x8)",
            "r(45)+border(10x20)",
            "flip",
            "s(100)", "s(x100)", "s(10x70)", "s(70x10)",  "s(60x70!)", "s(80%x150%)", 
            "s(100x100>)",
            // "s(100x100&gt;)", Fails
            "s(x100)",
            "s(100)+f(png)+r(20)+s(400x400)"
        };
        File dir = new File("src" + File.separator + "test" + File.separator + "images");
        List<Object[]> data = new ArrayList<Object[]>();
        for (File f : dir.listFiles()) {
            if (f.canRead() && f.isFile()) {
                for (String t : templates) {
                    data.add(new Object[] { f, t});
                }
            }
        }
        return data;
    }
     
    private static ImageMagickImageConverter imageMagick = new ImageMagickImageConverter();
    @BeforeClass
    public static void setUp() {
        Map<String, String> settings = new HashMap<String, String>();
        imageMagick.init(settings);
    }

    private final String template;
    private final File file;
    public ImagingTest(File f, String t) {
        file = f;
        template = t;
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
        assertFalse(info(), converted.length == 0);
        Dimension convertSize  = informer.getDimension(converted);
        assertTrue(info() + ":" + originalSize + "->" + predictedSize + " != " + convertSize, 
                   predictedSize.equalsIgnoreRound(convertSize, 1));
    }
    //@Test
    public void jai() throws IOException {
        test(new JAIImageConverter());
    }

    @Test
    public void imageMagick() throws IOException  {
        assumeTrue(imageMagick.validFormats.size() > 0);
        test(imageMagick);
    }
    
}
