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

    /**
     * The key of a transcoder uniquely identifies it. It can be compared to the 'ckey' which we use in image conversions.
     * Together with the node number of the source media object, this defines what the transcoded object actually is.
     * Ideally this key can be parsed to redo the transcodation.
     * @return A String. E.g. containing the actual class name plus parameters.
     */
    String getKey();

    /**
     * The format of the result.
     */
    Format getFormat();

    /**
     * The codec of the result.
     */
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
     * @param logger Progress is written to this logger
     * @exception If transcoding was unsucessfull, an <code>Error</code> may be thrown.
     */
    void transcode(URI in, URI out, Logger logger) throws Exception;

    /**
     * {#link transcode} is feed with input. If it was called, you can ask what is was.
     */
    URI getIn();

    /**
     * {#link transcode} is feed with a destination. If it was called, you can ask what is was.
     */
    URI getOut();

}
