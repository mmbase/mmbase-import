/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;


/**
 * You need only to implement transform(Reader) you have the simplest
 * kind of tranformer. The name becoming your class name.
 *
 * @author Michiel Meeuwissen 
 * @since MMBase-1.7
 */

public abstract class AbstractCharTransformer implements CharTransformer {

    public void configure(int t) {
        // not configurable
    }
    public Map transformers() {
        HashMap h = new HashMap();
        String name = getClass().getName();
        h.put(name, new Config(getClass(), 0, "A character string transformer"));
        return h;
    }

    public String transform(String r) {
        Writer sw = transform(new StringReader(r));
        return sw.toString();
    }

    public String transformBack(String r) {
        Writer sw = transformBack(new StringReader(r));
        return sw.toString();
    }

    public String getEncoding() {
        return getClass().getName();
    }

    
}
