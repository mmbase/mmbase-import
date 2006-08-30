/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework;
import org.mmbase.util.LocalizedString;

/**
 * A component is a piece of pluggable functionality that typically has dependencies on other
 * components, and may be requested several views.
 *
 * @author Michiel Meeuwissen
 * @version $Id: Component.java,v 1.2 2006-08-30 20:46:05 michiel Exp $
 * @since MMBase-1.9
 */
public interface Component {

    String getName();

    LocalizedString getDescription();

    void configure(org.w3c.dom.Document doc);

    // something like this?

    // View getAdminView();
    // View getView();
    // Collection<Component> getDependencies();
}
