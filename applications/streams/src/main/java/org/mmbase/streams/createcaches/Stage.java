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

/**
 * This enum can contain the 'stage' of the transcoding process
 * @author Michiel Meeuwissen
 * @version $Id$
 */

public enum Stage {
    /**
     * Nothing happend yet.
     */
    UNSTARTED,
    /**
     * Currently busy with a recognizer
     */
    RECOGNIZER,
    /**
     * Currently busy with a transcoder
     */
    TRANSCODER,
    /**
     * Everything's done!
     */
    READY;
}

