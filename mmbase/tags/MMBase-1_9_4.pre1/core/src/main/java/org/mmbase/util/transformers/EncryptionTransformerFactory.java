/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */

package org.mmbase.util.transformers;

import java.io.Reader;
import java.io.Writer;
import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.mmbase.util.functions.Parameter;
import org.mmbase.util.functions.Parameters;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This transformerfactory can encrypt and decrypt strings. The algorithm which is
 * used for encryption/decryption is parameterized, and defaults to AES. Since
 * these encryption algorithms return raw bytes, an encoding must be supplied
 * which is used to convert bytes to characters: either hexadecimal encoding
 * or base64.
 * @author Sander de Boer
 */

public class EncryptionTransformerFactory implements ParameterizedTransformerFactory<CharTransformer> {
    private static final Logger log = Logging.getLoggerInstance(EncryptionTransformerFactory.class);

    /**
     * Encode a given array of bytes to a string, using the given format.
     * This can be 'hex' or 'base64'.
     */
    public static String encode(byte input[], String format) {
        if ("hex".equalsIgnoreCase(format)) {
            Hex h = new Hex();
            String output = h.transform(input);
            return output;
        } else if ("base64".equalsIgnoreCase(format)) {
            Base64 b = new Base64();
            String output = b.transform(input);
            return output;
        }
        return "";
    }

    /**
     * Decode a given string to an array of bytes, using a given format.
     * This can throw an 'IllegalArgumentException' when the given input
     * string isn't correct according to the format.
     */
    public static byte[] decode(String input, String format) throws IllegalArgumentException{
        if ("hex".equalsIgnoreCase(format)) {
            Hex h = new Hex();
            byte[] output = h.transformBack(input);
            return output;
        } else if ("base64".equalsIgnoreCase(format)) {
            Base64 b = new Base64();
            byte[] output = b.transformBack(input);
            return output;
        }
        return new byte[0];
    }

    protected final static Parameter[] PARAMS = {
        new Parameter<String>("key", String.class, "1234567890abcdef"),
        new Parameter<String>("format", String.class, "hex"),
        new Parameter<String>("algorithm", String.class, "AES"),
        new Parameter<String>("direction", String.class, "encrypt")
    };

    public Parameters createParameters() {
        return new Parameters(PARAMS);
    }

    /**
     * Return a parameterized transformer, based on the given parameters. These can be the following:
     * <ul>
     *  <li>algorithm, defaults to 'AES'</li>
     *  <li>direction, can be 'encrypt' or 'decrypt'</li>
     *  <li>format, can be 'base64' or 'hex'</li>
     *  <li>key, defaults to '1234567890abcdef' (NOTE: the length of this key must match the requirements set by the algorithm</li>
     * </ul>
     */
    public CharTransformer createTransformer(Parameters parameters) {
        String direction = (String) parameters.get("direction");
        if ("encrypt".equalsIgnoreCase(direction)) {
            return new Encryption((String) parameters.get("key"), (String) parameters.get("format"), (String) parameters.get("algorithm"));
        } else if ("decrypt".equalsIgnoreCase(direction)) {
            return new Decryption((String) parameters.get("key"), (String) parameters.get("format"), (String) parameters.get("algorithm"));
        } else {
            throw new UnsupportedOperationException("Unknown value for attribute 'direction', (known are 'encrypt' and 'decrypt')");
        }
    }

    class Encryption extends ReaderTransformer {
        private static final long serialVersionUID = 0L;
        private final String key;
        private final String format;
        private final String algorithm;

        Encryption(String key, String format, String algorithm) {
            this.key = key;
            this.format = format;
            this.algorithm = algorithm;

            if ((!"base64".equalsIgnoreCase(format)) & (!"hex".equalsIgnoreCase(format))) {
                throw new UnsupportedOperationException("Unknown value for attribute 'format', (known are 'base64' and 'hex')");
            }
        }

        public Writer transform(Reader r, Writer w) {
            StringBuilder sb = new StringBuilder();

            try {
                int i;
                while ((i = r.read()) > -1) {
                    sb.append((char) i);
                }
                byte input[] = sb.toString().getBytes();

                byte[] secretKey = key.getBytes();
                SecretKeySpec skeySpec = new SecretKeySpec(secretKey, algorithm);

                Cipher cipher = Cipher.getInstance(algorithm);
                cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
                byte encrypted[] = cipher.doFinal(input);

                String output = encode(encrypted, format);

                w.write(output);
                return w;

            } catch (IllegalArgumentException h) {
                throw new UnsupportedOperationException(h.getMessage());
            } catch (SecurityException g) {
                throw new UnsupportedOperationException(g.getMessage());
            } catch (GeneralSecurityException f) {
                throw new UnsupportedOperationException(f.getMessage());
            } catch (Exception e) {
                log.error(e.getMessage() + Logging.stackTrace(e));
            }
            return w;
        }
    }

    class Decryption extends ReaderTransformer {
        private static final long serialVersionUID = 0L;
        private final String key;
        private final String format;
        private final String algorithm;

        Decryption(String key, String format, String algorithm) {
            this.key = key;
            this.format = format;
            this.algorithm = algorithm;

            if ((!"base64".equalsIgnoreCase(format)) & (!"hex".equalsIgnoreCase(format))) {
                throw new UnsupportedOperationException("Unknown value for attribute 'format', (known are 'base64' and 'hex')");
            }
        }

        public Writer transform(Reader r, Writer w) {
            StringBuilder sb = new StringBuilder();

            try {
                int i;
                while ((i = r.read()) > -1) {
                    sb.append((char) i);
                }

                byte[] input = decode(sb.toString(), format);

                byte[] secretKey = key.getBytes();
                SecretKeySpec skeySpec = new SecretKeySpec(secretKey, algorithm);

                Cipher cipher = Cipher.getInstance(algorithm);
                cipher.init(Cipher.DECRYPT_MODE, skeySpec);
                byte decrypted[] = cipher.doFinal(input);
                String output = new String(decrypted);
                w.write(output);
                return w;

            } catch (IllegalArgumentException h) {
                throw new UnsupportedOperationException(h.getMessage());
            } catch (SecurityException g) {
                throw new UnsupportedOperationException(g.getMessage());
            } catch (GeneralSecurityException f) {
                throw new UnsupportedOperationException(f.getMessage());
            } catch (Exception e) {
                log.error(e.getMessage() + Logging.stackTrace(e));
            }
            return w;
        }
    }
}
