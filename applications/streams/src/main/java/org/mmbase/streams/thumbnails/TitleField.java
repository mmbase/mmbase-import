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

package org.mmbase.streams.thumbnails;
import org.mmbase.bridge.*;
import org.mmbase.datatypes.processors.*;
import org.mmbase.util.logging.*;

/**
 * The virtual title field of thumbnails. They take the title of the mediafragment to which they are associated.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */

public class TitleField implements Processor {

    private static final Logger LOG = Logging.getLoggerInstance(TitleField.class);

    @Override
    public Object process(final Node node, final Field field, Object value) {
        Node idNode = node.getNodeValue("id"); // a videsource
        if (idNode != null && idNode.getNodeManager().hasField("mediafragment")) {
            Node mediaFragment = idNode.getNodeValue("mediafragment");
            long time = node.getLongValue("time");
            int seconds = (int) (time / 1000);
            int hours = seconds / (60 * 60);
            seconds %= 60 * 60;
            int minutes = seconds / 60;
            seconds %=  60;
            String timeString = hours > 0 ?
                String.format("%02d:%02d:%02d", hours, minutes, seconds) :
                String.format("%d:%02d", minutes, seconds);
            return (mediaFragment != null ? mediaFragment.getStringValue("title") : node.getStringValue("id")) + " (" + timeString +")";
        }
        return "Thumbnail";
    }


}
