/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import org.mmbase.util.functions.*;
import java.io.Reader;
import java.io.Writer;


/**
 * Factories {@link CharTransformer}'s which mirror the input, but only between 'from' and 'to'
 * parameters. So, those transformers work like {@link java.lang.String#substring}.
 *
 * @author Michiel Meeuwissen 
 * @since MMBase-1.8
 * @version $Id: SubstringFactory.java,v 1.3 2005-05-09 22:07:56 michiel Exp $
 */

public class SubstringFactory implements ParameterizedTransformerFactory {

    
    protected final static Parameter[] PARAMS = {
        new Parameter("from", Integer.class, new Integer(0)),
        new Parameter("to"  , Integer.class, new Integer(-1))
    };

    public  Transformer createTransformer(Parameters parameters) {
        return new Substring((Integer) parameters.get("from"), (Integer) parameters.get("to"));
    }
    public Parameters createParameters() {
        return new ParametersImpl(PARAMS);
    }
    public Parameters createParameters(java.util.List values) {
        return new ParametersImpl(PARAMS, values);
    }


    protected class Substring extends ReaderTransformer {
        private int from = 0;
        private int to   = -1;
        
        Substring(Integer f, Integer t) {
            from = f.intValue(); to = t.intValue();
        }


        // implementation, javadoc inherited
        public Writer transform(Reader r, Writer w) {
            if (from < 0 || to < -1) throw new UnsupportedOperationException("When using streams, it is not possible to use negative values.");
            int current = 0;
            try {
                while (true) {
                    int c = r.read();
                    if (c == -1) break;
                    if (current >= from) {
                        w.write(c);
                    }
                    current++;
                    if (to > -1 && current >= to) break;
                }
            } catch (java.io.IOException e) {
                throw new RuntimeException(e);
            }
            return w;        
        } 
             
        public String toString() {
            return "SUBSTRING(" + from + "," + to + ")";
        }
    }

}
