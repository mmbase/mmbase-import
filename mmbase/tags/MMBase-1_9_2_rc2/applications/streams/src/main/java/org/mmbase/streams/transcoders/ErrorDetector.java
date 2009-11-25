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
import java.util.regex.*;
/**
 * Reads log and watches for error patterns.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */

public class ErrorDetector extends AbstractSimpleImpl implements java.io.Serializable {

    public static final Logger LOG = Logging.getLoggerInstance(ErrorDetector.class);

    protected Pattern error;
    public ErrorDetector(Pattern p){
        error = p;
        setLevel(Level.SERVICE);
    }

    @Override
    protected void log(String s, Level level) {
        if (error.matcher(s).matches()) {
            throw new Error(s);
        }
    }

}
