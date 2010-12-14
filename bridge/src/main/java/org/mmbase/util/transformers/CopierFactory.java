/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import org.mmbase.util.functions.Parameters;
import org.mmbase.util.functions.AutodefiningParameters;

/**
 * A place holder factory, which only produces copiers and accepts every parameter for that.
 * @author Michiel Meeuwissen
 * @since MMBase-1.9.6
 * @version $Id: ParameterizedTransformerFactory.java 37706 2009-08-12 09:37:14Z michiel $
 */

public class CopierFactory implements  ParameterizedTransformerFactory<CharTransformer>  {

    public CharTransformer createTransformer(Parameters parameters) {
        return CopyCharTransformer.INSTANCE;
    }

    /**
     */
    public Parameters createParameters() {
        return new AutodefiningParameters();
    }

}
