/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import org.mmbase.util.functions.*;
import java.util.zip.Checksum;
import java.io.*; 

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Creates ByteToCharTransformers, creating a 'checksum' string of a byte-arrays. Parameterized by
 * the checksum implementation (defaults to java.util.zip.Adler32).
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.8
 * @version $Id$
 */

public class ChecksumFactory implements ParameterizedTransformerFactory  {
    private static final Logger log = Logging.getLoggerInstance(ChecksumFactory.class);

    public static final Parameter[] PARAMS = new Parameter[] {
        new Parameter<String>("implementation", String.class, java.util.zip.Adler32.class.getName())
    };

    public Parameters createParameters() {
        return new Parameters(PARAMS);
    }

    /**
     * Creates a parameterized transformer.
     */
    public Transformer createTransformer(Parameters parameters) {
        String impl = (String) parameters.get("implementation");
        try {
            Class<?> clazz = Class.forName(impl);
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
        private static final long serialVersionUID = 1L;
        private Checksum checksum;
        ChecksumTransformer(Checksum c) {
            checksum = c;
        }
        public String transform(byte[] bytes) {
            if (bytes == null) return null;
            synchronized(checksum) {
                checksum.reset();
                checksum.update(bytes, 0, bytes.length);
                return "" + checksum.getValue();
            }
        }

        @Override
        public String toString() {
            return "checksum(" + checksum + ")";
        }

        // implementation of serializable
        private void writeObject(ObjectOutputStream out) throws IOException {
            if (checksum instanceof Serializable) {
                out.writeObject(checksum);
            } else {
                out.writeObject(checksum.getClass());
            }
        }
        // implementation of serializable
        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            Object cs = in.readObject();
            if (cs instanceof Class) {
                try {
                    checksum = (Checksum) ((Class<?>) cs).newInstance();
                } catch (InstantiationException e) {
                    throw new IOException(e.getMessage());
               } catch (IllegalAccessException e) {
                    throw new IOException(e.getMessage());
                }
            } else {
                checksum = (Checksum) cs;
            }
        }

    }

    public static void main(String argv[]) {
        ChecksumFactory fact = new ChecksumFactory();
        Parameters params = fact.createParameters();
        params.set("implementation", java.util.zip.Adler32.class.getName());
        CharTransformer transformer = new ByteCharTransformer((ByteToCharTransformer) fact.createTransformer(params));
        System.out.println("" + transformer.transform("hoi"));
    }

}
