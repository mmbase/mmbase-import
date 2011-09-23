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
 * This is a place holder for the result of a transcoder which is not to be done, because production
 * of its source was skipped already, or because the source does not match the mime type.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */

class SkippedResult extends Result {
    SkippedResult(JobDefinition def, URI in) {
        super(def, in);
    }
    public Node getDestination() {
        return null;
    }
    public URI getOut() {
        return null;
    }
    public MimeType getMimeType() {
        return null;
    }
    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public String toString() {
        return "SKIPPED[" + definition + "]";
    }
}
