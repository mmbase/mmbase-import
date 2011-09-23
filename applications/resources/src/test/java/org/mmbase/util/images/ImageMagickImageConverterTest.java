/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.images;

import java.io.*;
import java.util.*;
import java.util.regex.*;
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

public class ImageMagickImageConverterTest {


    public void imageMagickVersion(String version, int major, int minor, int patch) {
       Matcher m = ImageMagickImageConverter.IM_VERSION_PATTERN.matcher(version);
       assert(m.matches());
       assertEquals(major, Integer.parseInt(m.group(1)));
       assertEquals(minor, Integer.parseInt(m.group(2)));
       assertEquals(patch, Integer.parseInt(m.group(3)));
    }

    @Test
    public void imageMagickVersion() throws IOException {
        imageMagickVersion("Version: ImageMagick 6.3.7 03/20/08 Q16 http://www.imagemagick.org", 6, 3, 7);
        imageMagickVersion("Version: ImageMagick 6.5.1-0 2009-08-27 Q16 OpenMP http://www.imagemagick.org", 6, 5, 1);
    }

}
