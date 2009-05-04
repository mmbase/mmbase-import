/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import java.util.regex.*;
import java.util.*;
import org.mmbase.util.Entry;

/**
 * Replaces certain 'forbidden' words by something more decent. Of course, censoring is evil, but
 * sometimes it can be amusing too. This is only an example implementation.
 *
 * @author Michiel Meeuwissen 
 * @since MMBase-1.7
 * @version $Id$
 */

public class Censor extends RegexpReplacer {

    protected static Collection forbidden = new ArrayList();
    
    static {        
        new Censor().readPatterns(forbidden);   
    }

    protected Collection getPatterns() {        
        return forbidden;
    }

    protected String getConfigFile() {
        return "censor.xml";
    }



    protected void readDefaultPatterns(Collection patterns) {
        patterns.add(new Entry(Pattern.compile("(?i)mmbase"),      "MMBase"));
        patterns.add(new Entry(Pattern.compile("(?i)microsoft"),   "Micro$soft"));
        patterns.add(new Entry(Pattern.compile("(?i)fuck"),        "****"));
    }
    

    public String toString() {
        return "CENSOR";
    }
}
