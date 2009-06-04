/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.images;

import java.io.*;

/**
 * Abstract implementation of a ImageConverter
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.9
 */

import java.util.List;
import java.util.Map;

public abstract class AbstractImageConverter implements ImageConverter {

    /**
     * @see org.mmbase.util.images.ImageConverter#init(java.util.Map)
     */
    public void init(Map<String, String> params) {
    }

    /**
     * @see org.mmbase.util.images.ImageConverter#convertImage(byte[], java.lang.String, java.util.List)
     */
    public abstract byte[] convertImage(byte[] input, String sourceFormat, List<String> commands);

    /**
     * @see org.mmbase.util.images.ImageConverter#convertImage(java.io.InputStream, java.lang.String, java.io.OutputStream, java.util.List)
     */
    public int convertImage(InputStream input, String sourceFormat, OutputStream out, List<String> commands) throws IOException {
        byte[] bytes;
        if (input instanceof BytesInputStream) {
            bytes = ((BytesInputStream) input).getBuffer();
        } else {
            ByteArrayOutputStream in = new ByteArrayOutputStream();
            BufferedOutputStream bufStream = new BufferedOutputStream(in);
            byte[] buf = new byte[1024];
            int b = 0;
            while ((b = input.read(buf)) != -1) {
                bufStream.write(buf, 0, b);
            }
            bufStream.flush();
            bytes = in.toByteArray();
        }
        byte[] result = convertImage(bytes, sourceFormat, commands);
        out.write(result);
        return result.length;
    }

    public class MyStream extends ByteArrayInputStream {
        MyStream(byte[] b) {
            super(b);
        }
        byte[] getBuffer() {
            return buf;
        }
    }


}
