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

import java.net.URI;

import org.mmbase.bridge.Node;
import org.mmbase.util.MimeType;


/**
 * Result of a recognizer JobDefinition, just recognizes the type of stream etc.
 * Does not transcode. The result out is the same as in, same for mimetype.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */
class RecognizerResult extends Result {
    final Node source;
    RecognizerResult(JobDefinition def, Node source, URI in) {
        super(def, in);
        this.source = source;
    }
    public Node getSource() {
        return source;
    }
    public Node getDestination() {
        return null;
    }
    public URI getOut() {
        return getIn();
    }
    public MimeType getMimeType() {
        return new MimeType(getSource().getStringValue("mimetype"));
    }
    @Override
    public void ready() {
        super.ready();
        if (definition.getLabel() != null && source.getNodeManager().hasField("label")) {
            source.setStringValue("label", definition.getLabel());
        }

    }
    @Override
    public String toString() {
        return "R-RESULT " + source.getNumber() + "[" + definition + "]";
    }
}
