/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package nl.eo.chat.repository.irc;

import nl.eo.chat.repository.Filter;

import java.util.Vector;
import java.util.StringTokenizer;
import java.util.Iterator;

/**
 * This implementation of the Filter uses a textfile to retrieve and stor information
 *
 * @author Jelle Katsman
 *
 * (c)Evangelische Omroep Mar 14, 2003
 */
public class IrcFilter implements Filter {
    Vector words = new Vector();

    public IrcFilter(String blackWordList) {
        StringTokenizer st = new StringTokenizer(blackWordList, "\n\r");
        while (st.hasMoreTokens()) {
            String word = st.nextToken();
            words.add(word);
        }
    }

    public boolean allow(String text) {
        text = text.toLowerCase();
        Iterator i = words.iterator();
        while (i.hasNext()) {
            String word = (String)i.next();
            word = word.toLowerCase();
            if (text.indexOf(word) != -1) {
                return false;
            }
        }
        return true;
    }

}
