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

import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.util.logging.*;


/**
 * @author Michiel Meeuwissen
 * @version $Id$
 */

public class MockAnalyzer implements Analyzer {


    private static final Logger LOG = Logging.getLoggerInstance(MockAnalyzer.class);

    private final ChainedLogger log = new ChainedLogger(LOG);
    protected final AnalyzerUtils util = new AnalyzerUtils(log);
    {
        util.setUpdateSource(true);
    }

    protected int x = -1;
    protected int y = -1;

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }


    private List<Throwable> errors = new ArrayList<Throwable>();

    public void addThrowable(Throwable t) {
        errors.add(t);
    }

    public void addLogger(Logger logger) {
        log.addLogger(logger);
    }

    public int getMaxLines() {
        return Integer.MAX_VALUE;
    }

    public void analyze(String l, Node source, Node des) {

    }

    public void ready(Node sourceNode, Node destNode) {
        synchronized(util) {
            if (destNode != null) {
                destNode.setValue("height", y);
                destNode.setValue("width", x);
                destNode.commit();
            }
            util.toVideo(sourceNode, destNode);
            sourceNode.commit();
            log.info("READY for " + sourceNode.getNodeManager().getName() + " " + sourceNode.hashCode() + " " + sourceNode.getNumber());
        }

    }
    @Override
    public MockAnalyzer clone() {
        try {
            return (MockAnalyzer) super.clone();
        } catch (CloneNotSupportedException cnfe) {
            // doesn't happen
            return null;
        }
    }

}
