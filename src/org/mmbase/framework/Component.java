/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework;
import java.util.*;
import org.mmbase.util.LocalizedString;

/**
 * A component is a piece of pluggable functionality that typically has dependencies on other
 * components, and may be requested several views.
 *
 * @author Michiel Meeuwissen
 * @version $Id: Component.java,v 1.6 2006-10-24 17:16:32 michiel Exp $
 * @since MMBase-1.9
 */
public interface Component {

    /**
     * Every component has a (universally) unique name
     */
    String getName();

    /**
     * The description can contain further information about the component, mainly to be displayed
     * in pages about components generally.
     */
    LocalizedString getDescription();

    /**
     * Configures the component, by XML.
     * @param element A 'component' element from the 'components' XSD.
     */
    void configure(org.w3c.dom.Element element);

    /**
     * An unmodifiable collection of all blocks associated with the component
     */
    Collection<Block> getBlocks();

    /**
     * Gets a specific block
     */
    Block getBlock(String name);

    /**
     * Gets the one block that is the 'default' block of this component
     */
    Block getDefaultBlock();

}
