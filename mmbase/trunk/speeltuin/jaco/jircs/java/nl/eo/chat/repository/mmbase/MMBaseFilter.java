/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package nl.eo.chat.repository.mmbase;

import java.util.Iterator;
import java.util.Vector;
import java.util.StringTokenizer;

import nl.eo.chat.repository.Filter;

/**
 * This implementation of a filter uses MMBase to retrieve and store
 * information.
 *
 * @author Jaco de Groot
 */
public class MMBaseFilter implements Filter {
    Vector words = new Vector();
    
    public MMBaseFilter(String blackWordList) {
        StringTokenizer st = new StringTokenizer(blackWordList, "\n\r");
        while (st.hasMoreTokens()) {
            String word = st.nextToken();
            words.add(word);
        }
    }
    
    public boolean allow(String text) {
        Iterator i = words.iterator();
        while (i.hasNext()) {
            String word = (String)i.next();
            if (text.indexOf(word) != -1) {
                return false;
            }
        }
        return true;
    }
    
}

