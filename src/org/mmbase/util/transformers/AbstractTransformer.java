package org.mmbase.util.transformers;

import java.util.HashMap;

/**
 *
 * @author Michiel Meeuwissen
 */

public abstract class AbstractTransformer implements Transformer {
 
    protected int to;

    public void configure(int t) {
        to = t;
    }
    public abstract HashMap transformers();

}
