package org.mmbase.util.transformers;

import java.util.HashMap;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Interface for transformations.
 *
 * @author Michiel Meeuwissen
 */

public interface ByteTransformer extends Transformer {

    public OutputStream transform(InputStream r); 
    public OutputStream transformBack(InputStream r);

    public byte[] transform(byte[] r); 
    public byte[] transformBack(byte[]  r);
}
