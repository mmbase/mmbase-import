/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.images;

import java.util.Map;
import java.io.*;
import javax.media.jai.*;

import com.sun.media.jai.codec.ByteArraySeekableStream;
import com.sun.media.jai.codec.MemoryCacheSeekableStream;

/**
 * Informs about a image using JAI.
 *
 * @author Michiel Meeuwissen
 * @version $Id: JAIImageInformer.java,v 1.1 2006-10-25 14:10:55 michiel Exp $
 * @since MMBase-1.8
 */
public class JAIImageInformer implements ImageInformer {

    public void init(Map params) {
    }

    public Dimension getDimension(byte[] input) throws IOException { 
        ByteArraySeekableStream bin = new ByteArraySeekableStream(input);
        PlanarImage img = JAI.create("stream", bin);
        return new Dimension(img.getWidth(), img.getHeight());
    }
    public Dimension getDimension(InputStream input) throws IOException {
        MemoryCacheSeekableStream bin = new MemoryCacheSeekableStream(input);
        PlanarImage img = JAI.create("stream", bin);
        return new Dimension(img.getWidth(), img.getHeight());
    }
}
