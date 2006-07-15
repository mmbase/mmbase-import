/*
 * xmlbs
 *
 * Copyright (C) 2002  R.W. van 't Veer
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA.
 */

package xmlbs;

/**
 * Interface for writing document structure descriptions.
 *
 * @author R.W. van 't Veer
 * @version $Revision: 1.1 $
 */
public interface DocumentStructure {
    /**
     * Set ignore case flag for matching tagnames, attributes and
     * entities.
     * @param icase true where character case should be ignored
     */
    void setIgnoreCase (boolean icase);

    /**
     * Get ignore case flag.
     * @return true where character case should be ignored
     */
    boolean getIgnoreCase ();

    /**
     * Get tag name.
     * Ignoring character case if needed.
     * @param name tag name to lookup
     * @return tag name in proper case
     */
    String getTagName (String name);

    /**
     * Get attribute name.
     * Ignoring character case if needed.
     * @param name tag name to lookup
     * @param attr attribute name to lookup
     * @return attribute name in proper case
     */
    String getTagAttribute (String name, String attr);

    /**
     * Get entity reference.
     * Ignoring character case if needed.
     * @param name entity reference name
     * @return entity reference name in proper case or
     * <tt>null</tt> if no such entity exists
     */
    String getEntityRef (String name);

    /**
     * Determine if tag is known.
     * @param tag tag token to lookup
     * @return true if tag is known
     */
    boolean isKnownTag (TagToken tag);

    /**
     * Retain only known attributes.
     * <P><EM>TODO return number of modifications?</EM></P>
     * @param tag tag token to handle
     */
    void retainKnownAttributes (TagToken tag);

    /**
     * Determine if tag can be placed into other tag.
     * @param parent top tag
     * @param child sub tag
     * @return true when allowed
     */
    boolean canContain (TagToken parent, Token child);
}
