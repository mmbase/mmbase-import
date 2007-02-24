/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import java.util.Map;

/**
 * Some Transformers implement more than one transformation. The instance can be configured.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.7
 */
public abstract class ConfigurableReaderTransformer extends ReaderTransformer implements ConfigurableTransformer {

    protected int to;

    public ConfigurableReaderTransformer() {
        super();
    }

    public ConfigurableReaderTransformer(int conf) {
        super();
        configure(conf);
    }

    public void configure(int t) {
        //log.info("Setting config to " + t);
        to = t;
    }
    abstract public Map<String,Config> transformers();
    abstract public String getEncoding();

    public String toString() {
        try {
            return getEncoding();
        } catch (Exception e) {
            return "UNCONFIGURED " + super.toString();
        }
    }
}
