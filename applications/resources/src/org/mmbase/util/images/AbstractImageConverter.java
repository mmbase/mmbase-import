/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.images;

import java.util.*;
import java.io.*;

/**
 * Abstract implemntatinof a Image
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.9
 */

import java.util.List;
import java.util.Map;

public class AbstractImageConverter implements ImageConverter {

    public void init(Map<String, String> params) {
    }

    public byte[] convertImage(byte[] input, String sourceFormat, List<String> commands) {
        try {
            InputStream in = new BytesInputStream(input);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            convertImage(in, sourceFormat, out, commands);
            return out.toByteArray();
        } catch (IOException ioe) {
            return null;
        }
    }

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
