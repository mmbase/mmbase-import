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

import java.net.URI;

import org.mmbase.util.MimeType;
import org.mmbase.util.logging.Logger;


/**
 * A Recognizer is a bit like a {@link Transcoder}, but it does not transcode anything, it only produces output to the
 * logger, which can be analyzed using an {@link AnalyzerLogger}.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */
public interface Recognizer extends org.mmbase.util.PublicCloneable<Recognizer>, java.io.Serializable {


    MimeType getMimeType();
    void setMimeType(String s);

    void analyze(URI in, Logger logger) throws Exception;


}
