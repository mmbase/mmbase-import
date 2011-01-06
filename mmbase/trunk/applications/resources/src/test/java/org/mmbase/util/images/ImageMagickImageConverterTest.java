/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.images;

import java.io.*;
import java.util.regex.*;

import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Michiel Meeuwissen
 */

public class ImageMagickImageConverterTest {


    public void imageMagickVersion(String version, int major, int minor, int patch) {
       Matcher m = ImageMagickImageConverter.IM_VERSION_PATTERN.matcher(version);
       assert(m.matches());
       assertEquals(major, Integer.parseInt(m.group(2)));
       assertEquals(minor, Integer.parseInt(m.group(3)));
       assertEquals(patch, Integer.parseInt(m.group(4)));
    }

    @Test
    public void imageMagickVersion() throws IOException {
        imageMagickVersion("Version: ImageMagick 6.3.7 03/20/08 Q16 http://www.imagemagick.org", 6, 3, 7);
        imageMagickVersion("Version: ImageMagick 6.5.1-0 2009-08-27 Q16 OpenMP http://www.imagemagick.org", 6, 5, 1);
        imageMagickVersion("GraphicsMagick 1.1.11 2008-02-23 Q8 http://www.GraphicsMagick.org/", 1, 1, 11); // will be supposed to be like 6.1.11 (See MMB-1906)
    }

}
