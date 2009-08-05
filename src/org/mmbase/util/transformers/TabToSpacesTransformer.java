/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import java.io.Reader;
import java.io.Writer;

import org.mmbase.util.logging.*;

/**
 * Replace every tab by n spaces.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.7
 */

public class TabToSpacesTransformer extends ReaderTransformer implements CharTransformer {

    private static final Logger log = Logging.getLoggerInstance(TabToSpacesTransformer.class);

    private int spaceNum;
    private String tab;

    public TabToSpacesTransformer() {
        this(3);
    }

    public TabToSpacesTransformer(int spaces) {
        spaceNum = spaces;
        {
            StringBuilder buf = new StringBuilder();
            for (int i = 0; i < spaceNum; i++) {
                buf.append(' ');
            }
            tab = buf.toString();
        }
    }

    public Writer transform(Reader r, Writer w) {
        try {
            while (true) {
                int c = r.read();
                if (c == -1) break;
                if (c == '\t') {
                    w.write(tab);
                    continue;
                }
                w.write(c);
            }
        } catch (java.io.IOException e) {
            log.error(e.toString());
        }
        return w;
    }
    public Writer transformBack(Reader r, Writer w) {
        int spaces = 1;
        try {
            while (true) {
                int c = r.read();
                if (c == -1) break;
                if (c == ' ') {
                    if (spaces == spaceNum) {
                        w.write('\t');
                        spaces = 1;
                    } else {
                        spaces++;
                    }
                } else {
                    while(spaces > 1) { --spaces; w.write(' '); }
                    w.write(c);
                }

            }
        } catch (java.io.IOException e) {
            log.error(e.toString());
        }
        return w;

    }


    public String toString() {
        return "tab2spacestransformer";
    }
}
