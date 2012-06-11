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
import java.lang.annotation.*;

/**
 * This annotation can be used on a Transcoder class, and provides a list of 'properties' on that
 * class. Those properties are then interpreted as actually varying thing, and are included in the
 * default implementation of {@link Transcoder#getKey} (in {@ink AbstractTranscoder}).
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Settings {
    String[] value();

}
