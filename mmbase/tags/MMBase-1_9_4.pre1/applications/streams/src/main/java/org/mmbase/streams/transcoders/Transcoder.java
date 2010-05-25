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

import java.net.*;
import org.mmbase.bridge.Node;
import org.mmbase.applications.media.*;
import org.mmbase.util.logging.*;

/**
 * Representation of one transcoding action. Instances should cloned before usage, so the transcoder
 * needs not be stateless.
 *
 * @author Michiel Meeuwissen
 * @version $Id$ 
 */

public interface Transcoder extends org.mmbase.util.PublicCloneable<Transcoder>, java.io.Serializable {

    String getKey();

    Format getFormat();

    Codec getCodec();

    /**
     * Init the transcoder and destination node with appropiate values.
     * @param destination   stream destination Node
     */
    void init(Node destination);
    
    /**
     * Transcode a file to another, follow the process with a logger.
     * @param in
     * @param out
     * @param logger
     * @exception If transcoding was unsucessfull, an <code>Error</code> may be thrown.
     */
    void transcode(URI in, URI out, Logger logger) throws Exception;

    URI getIn();
    URI getOut();

}
