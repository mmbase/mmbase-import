/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import java.io.Reader;
import java.io.Writer;

/**
 * Replace 1 or more spaces by 1 space, and 1 or more newlines by 1
 * newline. Any other combination of newlines and spaces is replaced
 * by one newline. Except if they are in between "<pre>" and
 * "</pre>". (Note: perhaps this last behaviour should be made
 * configurable.
 *
 * @author Michiel Meeuwissen 
 * @since MMBase-1.7
 */

public class SpaceReducer extends AbstractCharTransformer implements CharTransformer {


    public Writer transform(Reader r, Writer w) {
        int space = 0;
        int nl = 0;
        StringBuffer indent = new StringBuffer();
        int l = 0;
        try {
            while (true) {
                int c = r.read();
                if (c == -1) break;
                if (c == '\n') {
                    if (nl == 0) w.write(c);                    
                    space++;
                    nl++;
                    l++;
                } else if (Character.isWhitespace((char) c)) {
                    if (space == 0 && l > 0) w.write(' ');
                    if (l == 0) indent.append(c);
                    space ++;
                } else {                
                    if (l == 0 && space > 0) {
                        w.write(indent.toString());
                        indent.setLength(0);
                    }
                    space = 0; nl = 0; l++;
                    
                    w.write(c);
                }
            }            
        } catch (java.io.IOException e) {
            System.out.println("w" + e.toString());
        }
        return w;
    }


    public String toString() {
        return "SPACEREDUCER";
    }
}
