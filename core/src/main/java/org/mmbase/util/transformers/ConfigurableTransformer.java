/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import java.util.Map;

/**
 * Interface for transformations. The 'configurable' version can be configured with an integer, so
 * the transformer can work in a limited number of ways. This is to avoid a wild growth of class,
 * because you can influence the behaviour a bit by such a setting.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.7
 */

public interface ConfigurableTransformer extends Transformer {

    /**
     * If a transformer can handle more then one destination
     * format, it can be configured with this.
     *
     * There must be a default, since <code>to</code> can be null.
     */

    void configure(int to);

    /**
     * Returns which transformations can be done by an object of this class.
     *
     * @return A Map with String Integer/Class pairs.
     */
    Map<String,Config> transformers();

    /**
     * Returns the encoding that is currently active
     *
     * @return An String representing the coding that is currently used.
     */
    String getEncoding();

}
