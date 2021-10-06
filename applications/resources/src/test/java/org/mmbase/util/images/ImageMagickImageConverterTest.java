/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.images;

import java.io.IOException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author Michiel Meeuwissen
 */

public class ImageMagickImageConverterTest {


    public void imageMagickVersion(String version, int major, int minor, int patch) {
       ImageMagickImageConverter.Version v = ImageMagickImageConverter.Version.parse(version);
       assert(v.matches());
       assertEquals(major, v.getMajor());
       assertEquals(minor, v.getMinor());
       assertEquals(patch, v.getPatch());
    }

    @Test
    public void imageMagickVersion() throws IOException {
        imageMagickVersion("Version: ImageMagick 6.3.7 03/20/08 Q16 http://www.imagemagick.org", 6, 3, 7);
        imageMagickVersion("Version: ImageMagick 6.5.1-0 2009-08-27 Q16 OpenMP http://www.imagemagick.org", 6, 5, 1);
    }

}
