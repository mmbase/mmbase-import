package org.mmbase.util.transformers;

import java.util.HashMap;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

/**
 * Interface for transformations.
 *
 * @author Michiel Meeuwissen
 */

public interface ByteToCharTransformer extends Transformer {

    public Writer transform(InputStream r); 
    public OutputStream transformBack(Reader r);

    public String transform(byte[] r); 
    public byte[] transformBack(String r);

}
