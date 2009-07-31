/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import org.mmbase.datatypes.processors.*;
import org.mmbase.util.Casting;

/**

 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.9.1
 */

public class ProcessorCharTransformer extends StringTransformer {
    private final Processor wrapped;

    public ProcessorCharTransformer(Processor p) {
        wrapped = p;
    }
    public String transform(String r) {
        if (r == null) return null;
        return Casting.toString(wrapped.process(null, null, Casting.toString(r)));
    }

}
