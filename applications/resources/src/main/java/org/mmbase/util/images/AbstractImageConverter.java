/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.images;

import java.io.*;
import java.util.List;
import java.util.Map;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * Abstract implementation of a ImageConverter. Override either {@link convertImage(byte[], String,
 * List)} or {@link convertImage(InputStream, String, OutputStream, List)}.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.9
 */


public abstract class AbstractImageConverter implements ImageConverter {
    private static final Logger log = Logging.getLoggerInstance(AbstractImageConverter.class);

    /**
     * @see org.mmbase.util.images.ImageConverter#init(java.util.Map)
     */
    public void init(Map<String, String> params) {
    }

    /**
     * @see org.mmbase.util.images.ImageConverter#convertImage(byte[], java.lang.String, java.util.List)
     */
    public byte[] convertImage(byte[] input, String sourceFormat, List<String> commands) {
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(input);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            convertImage(in, sourceFormat, out, commands);
            return out.toByteArray();
        } catch (IOException ioe) {
            log.error(ioe);
            return null;
        }
    }

    /**
     * @see org.mmbase.util.images.ImageConverter#convertImage(java.io.InputStream, java.lang.String, java.io.OutputStream, java.util.List)
     */
    public int convertImage(InputStream input, String sourceFormat, OutputStream out, List<String> commands) throws IOException {
        byte[] bytes;
        if (input instanceof BytesInputStream) {
            bytes = ((BytesInputStream) input).getBuffer();

        } else {
            ByteArrayOutputStream in = new ByteArrayOutputStream();
            org.mmbase.util.IOUtil.copy(input, in);
            bytes = in.toByteArray();
        }
        byte[] result = convertImage(bytes, sourceFormat, commands);
        out.write(result);
        return result.length;
    }




}
