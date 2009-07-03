/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.streams.transcoders;

import org.mmbase.util.logging.*;
import org.mmbase.bridge.*;
import java.util.*;


/**

 * @author Michiel Meeuwissen
 */

public class ChainedAnalyzer implements Analyzer {

    private List<Analyzer> analyzers = new ArrayList<Analyzer>();
    private int maxLines = 0;
    private int line = 0;

    public ChainedAnalyzer() {
    }
    public void add(Analyzer a) {
        analyzers.add(a);
        if (a.getMaxLines() > maxLines) {
            maxLines = a.getMaxLines();
        }
    }
    public void addLogger(org.mmbase.util.logging.Logger l) {
        for (Analyzer a : analyzers) {
            a.addLogger(l);
        }
    }

    public    int  getMaxLines() {
        return maxLines;
    }

    public void analyze(String l, Node source, Node dest) {
        for (Analyzer a : analyzers) {
            line++;
            if (line < a.getMaxLines()) {
                a.analyze(l, source, dest);
            }
        }
    }

    public void ready(Node source, Node dest) {
        for (Analyzer a : analyzers) {
            a.ready(source, dest);
        }
    }
    public ChainedAnalyzer clone() {
        try {
            ChainedAnalyzer clone = (ChainedAnalyzer) super.clone();
            clone.analyzers.clear();
            for (Analyzer a : analyzers) {
                clone.analyzers.add(a.clone());
            }
            return clone;
        } catch (CloneNotSupportedException cnse) {
            throw new RuntimeException(cnse);
        }

    }

}
