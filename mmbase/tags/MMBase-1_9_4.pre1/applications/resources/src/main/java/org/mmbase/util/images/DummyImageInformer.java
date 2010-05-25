/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.images;

import java.util.Map;
import java.io.*;

/**
 * The `Dummy' ImageInformer can inform about an image without any external packages. It depends
 * upon {@link org.mmbase.util.images.ImageInfo}
 *
 * @since MMBase 1.7.4
 * @author Michiel Meeuwissen
 * @version $Id$
 */
public class DummyImageInformer implements ImageInformer {

    public void init(Map<String,String> params) {
    }

    public Dimension getDimension(byte[] data) {
        ImageInfo imageInfo = new ImageInfo();
        imageInfo.setInput(new ByteArrayInputStream(data));
        imageInfo.check();
        return new Dimension(imageInfo.getWidth(), imageInfo.getHeight());
    }
    public Dimension getDimension(InputStream data) {
        ImageInfo imageInfo = new ImageInfo();
        imageInfo.setInput(data);
        imageInfo.check();
        return new Dimension(imageInfo.getWidth(), imageInfo.getHeight());
    }
}
