package org.mmbase.util.transformers;

import java.util.HashMap;

/**
 * Interface for transformations.
 *
 * @author Michiel Meeuwissen
 */

public interface Transformer {

    /**
     * If a transformer can handle more then one destination
     * format, it can be configured with this.
     *
     * There must be a default, since to can be null.
     */

    public void configure(int to);

    /**
     * Returns which transformations can be done by an object of this class.
     *
     * @return An HashMap with String Integer/Class pairs.
     */
    public HashMap transformers();

}
