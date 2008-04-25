/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework;
import org.mmbase.util.logging.*;

/**
 * A Virtual Component is a component which is only mentioned as a dependency of another component.
 *
 * @author Michiel Meeuwissen
 * @version $Id: VirtualComponent.java,v 1.5 2008-04-25 14:31:39 andre Exp $
 * @since MMBase-1.9
 */
public class VirtualComponent {
    private static final Logger log = Logging.getLoggerInstance(VirtualComponent.class);


    private final String name;
    private final int version;


    public VirtualComponent(String name, int v) {
        this.name = name;
        this.version = v;
    }

    public String getName() {
        return name;
    }
    public int getVersion() {
        return version;
    }
}
