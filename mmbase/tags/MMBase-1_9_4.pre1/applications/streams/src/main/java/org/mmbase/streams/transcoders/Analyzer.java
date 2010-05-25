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
import org.mmbase.bridge.*;


/**
 * The goal of implementations of this interface is to be wrapped by a {@link AnalyzerLogger} (which
 * itself probably is an entry in a {@link ChainedLogger}). It can use these log lines to store
 * information about a {@link Transcoder} process, which appears in its' logging, in the 2 involved nodes.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */
public interface Analyzer extends org.mmbase.util.PublicCloneable<Analyzer>, java.io.Serializable {

    void addThrowable(Throwable t);

    /**
     * How many lines of logging should be offered for {@link #analyze}ing at the most.
     */
    int  getMaxLines();

    void addLogger(Logger l);

    /**
     * Analyzes a line of logging of
     * @param l This line
     * @param source The node representing the original mediasource.
     * @param dest   The node representing the mediasource to which the transcoding result is being
     * written.
     */
    void analyze(String l, Node source, Node dest);

    void ready(Node source, Node dest);

}
