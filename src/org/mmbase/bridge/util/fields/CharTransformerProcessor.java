/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.util.fields;

import org.mmbase.bridge.Node;
import org.mmbase.util.transformers.CharTransformer;

/**
 * A processor based on a chartransformer (works only for Strings). This gives easy access to all kind of
 * string transformations.
 *
 * @author Michiel Meeuwissen
 * @version $Id: CharTransformerProcessor.java,v 1.2 2003-12-09 22:26:17 michiel Exp $
 * @since MMBase-1.7
 * @see org.mmbase.util.transformers.CharTransformer
 */

public class CharTransformerProcessor implements Processor {

    private CharTransformer charTransformer;

    public CharTransformerProcessor(CharTransformer ct) {
        charTransformer = ct;
    }

    public final Object process(Node node, Object value) {
        return charTransformer.transform((String) value);
    }        
}
