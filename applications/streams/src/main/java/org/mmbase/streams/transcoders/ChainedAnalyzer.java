/*

This file is part of the MMBase Streams application, 
which is part of MMBase - an open source content management system.
    Copyright (C) 2009 André van Toly, Michiel Meeuwissen

MMBase Streams is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

MMBase Streams is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with MMBase. If not, see <http://www.gnu.org/licenses/>.

*/

package org.mmbase.streams.transcoders;

import org.mmbase.bridge.*;
import java.util.*;


/**
 * Makes it possible to chain analyzers one after another.
 * 
 * @author Michiel Meeuwissen
 * @version $Id$
 */

public class ChainedAnalyzer implements Analyzer {

    private List<Analyzer> analyzers = new ArrayList<Analyzer>();
    private int maxLines = 0;
    private int line = 0;

    public ChainedAnalyzer() {
    }

    public void addThrowable(Throwable t) {
        for (Analyzer a : analyzers) {
            a.addThrowable(t);
        }
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
    @Override
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
