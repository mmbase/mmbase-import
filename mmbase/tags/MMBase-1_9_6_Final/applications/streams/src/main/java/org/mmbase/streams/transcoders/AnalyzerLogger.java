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

import org.mmbase.util.logging.*;
import org.mmbase.bridge.Node;


/**
 * A Logger which only wraps  a {@link Analyzer} to analyze the logs.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */

public class AnalyzerLogger  extends AbstractSimpleImpl implements java.io.Serializable {

    public static final Logger LOG = Logging.getLoggerInstance(AnalyzerLogger.class);

    protected  final Analyzer analyzer;
    protected final Node source;
    protected final Node destination;

    private int line = 0;
    private boolean commited = false;

    public AnalyzerLogger(Analyzer a, Node source, Node destination) {
        analyzer = a;
        this.source = source;
        this.destination = destination;
        setLevel(Level.SERVICE);
    }

    @Override
    protected void log(String s, Level level) {
        if (line++ < analyzer.getMaxLines()) {
            try {
                analyzer.analyze(s, source, destination);
            } catch (Exception e) {
                LOG.warn(e.getMessage(), e);
            }
        } else if (! commited) {
            LOG.service(" " + source.getNumber() + " " + source.getChanged());
            if (source.isChanged()) {
                source.commit();
            }
            if (destination.isChanged()) {
                destination.commit();
            }
            commited = true;
        }
    }
    public Analyzer getAnalyzer() {
        return analyzer;
    }

    @Override
    public String toString() {
        return super.toString() + " " + level;
    }
}
