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

package org.mmbase.streams.createcaches;

import org.mmbase.util.functions.*;
import org.mmbase.bridge.*;
import org.mmbase.util.logging.*;

/**
 * To be used in a template, indicates when transcoding of the uploaded file has started.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */

public class WaitUntilTranscodingFunction extends NodeFunction<Boolean> {
    private static final long serialVersionUID = 0L;

    private static final Logger LOG = Logging.getLoggerInstance(WaitUntilTranscodingFunction.class);
    public WaitUntilTranscodingFunction() {
        super("waitfor");
    }


    @Override
    protected Boolean getFunctionValue(final Node node, final Parameters parameters) {
        Job job = Processor.getJob(node);
        if (job != null) {
            try {
                job.waitUntil(Stage.TRANSCODER);
            } catch (InterruptedException ie) {
                return false;
            }
        }
        return true;

    }

}
