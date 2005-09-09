/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import org.mmbase.util.functions.*;
import java.util.zip.Checksum;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Creates ByteToCharTransformers
 * @author Michiel Meeuwissen
 * @since MMBase-1.8
 */

public class ChecksumFactory implements ParameterizedTransformerFactory  {
    private static final Logger log = Logging.getLoggerInstance(ChecksumFactory.class);

    public static final Parameter[] PARAMS = new Parameter[] {
        new Parameter("implementation", String.class, java.util.zip.Adler32.class.getName())
    };

    public Parameters createParameters() {
        return new ParametersImpl(PARAMS);
    }

    /**
     * Creates a parameterized transformer.
     */
    public Transformer createTransformer(Parameters parameters) {
        String impl = (String) parameters.get("implementation");
        try {
            Class clazz = Class.forName(impl);
            if (Checksum.class.isAssignableFrom(clazz)) {
                Checksum checksum = (Checksum) clazz.newInstance();
                return new ChecksumTransformer(checksum);
            }
            if (impl.equalsIgnoreCase("md5")) {
                return new MD5();
            }
        } catch (Exception e) {
            log.error(e);
        }
        return Transformers.getCharTransformer(impl, "", "ChecksumFactory", false);

        
    }


    protected class ChecksumTransformer extends ByteArrayToCharTransformer implements ByteToCharTransformer {
        private Checksum checksum;
        ChecksumTransformer(Checksum c) {
            checksum = c;
        }
        public String transform(byte[] bytes) {
            checksum.reset();
            checksum.update(bytes, 0, bytes.length);
            return "" + checksum.getValue();
        }
    }

}
