/*

This file is part of the MMBase Streams application,
which is part of MMBase - an open source content management system.
    Copyright (C) 2009-2010 André van Toly, Michiel Meeuwissen

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

package org.mmbase.streams;

import java.io.File;

import org.mmbase.servlet.FileServlet;
import org.mmbase.util.functions.*;
import org.mmbase.bridge.*;
import org.mmbase.util.logging.*;

/**
 * This function on streamsources and caches returns the associated file as an actual {@link #File} object.
 * @author Michiel Meeuwissen
 * @version $Id$
 */

public class FileFunction extends NodeFunction<File> {

    private static final Logger LOG = Logging.getLoggerInstance(FileFunction.class);

    public final static Parameter[] PARAMETERS = {
    };
    public FileFunction() {
        super("file", PARAMETERS);
    }



    @Override
    protected File getFunctionValue(final Node source, final Parameters parameters) {
        String url = source.getStringValue("url");
        if (url.length() > 0) {
            return new File(FileServlet.getDirectory(), url);
        } else {
            return null;
        }
    }
}
