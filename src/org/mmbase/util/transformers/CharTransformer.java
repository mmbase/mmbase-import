package org.mmbase.util.transformers;

import java.util.HashMap;
import java.io.Reader;
import java.io.Writer;

/**
 * Interface for transformations.
 *
 * @author Michiel Meeuwissen
 */

public interface CharTransformer extends Transformer {

    public Writer transform(Reader r); 
    public Writer transformBack(Reader r);

    public String transform(String r); 
    public String transformBack(String r);

}
