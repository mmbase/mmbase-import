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
import org.mmbase.bridge.Node;
import org.mmbase.bridge.util.MapNode;
import org.mmbase.util.logging.Level;
import org.mmbase.util.logging.AbstractSimpleImpl;

/**
 * Low level tests of Regexps and stuff.
 * @author Michiel Meeuwissen
 */

public abstract class AnalyzerTestLogger  extends AbstractSimpleImpl {

    static Node getMockNode() {
        Map<String, Object> mock = new HashMap<String, Object>();
        mock.put("mimetype", null);
        mock.put("length", null);
        mock.put("bitrate", null);
        mock.put("state", null);
        return new MapNode(mock) {
            public void commit() {
                // ok
            }
        };
    }

    public boolean success = false;


    protected Node source      = getMockNode();
    protected Node destination = getMockNode();


    protected final AnalyzerUtils util = new AnalyzerUtils();

    public AnalyzerTestLogger() {
        setLevel(Level.DEBUG);
        util.setUpdateSource(true);
    }


    protected abstract void log(String s);

    @Override
    protected void log(String s, Level level) {
        log(s);
    }




}


