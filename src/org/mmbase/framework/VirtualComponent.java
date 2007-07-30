/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework;

import java.util.*;
import org.w3c.dom.*;
import java.net.URI;
import org.mmbase.util.LocalizedString;
import org.mmbase.util.functions.Parameter;
import org.mmbase.util.logging.*;

/**
 * A Virtual component is a component which is  only mentioned as a dependency of another component.
 *
 * @author Michiel Meeuwissen
 * @version $Id: VirtualComponent.java,v 1.1 2007-07-30 23:01:42 michiel Exp $
 * @since MMBase-1.9
 */
public class VirtualComponent implements Component {
    private static final Logger log = Logging.getLoggerInstance(VirtualComponent.class);


    private final String name;
    private int version = -1;


    public VirtualComponent(String name, int v) {
        this.name = name;
        this.version = v;
    }

    public String getName() {
        return name;
    }
    public URI getUri() {
        return null;
    }
    public int getVersion() {
        return version;
    }


    public LocalizedString getDescription() {
        return null;
    }

    public void configure(Element el) {
        throw new UnsupportedOperationException();
    }

    public Collection<Block> getBlocks() {
        return null;
    }
    public Block getBlock(String name) {
        return null;
    }
    public Block getDefaultBlock() {
        return null;
    }

    public String toString() {
        return getName() + ":" + getVersion();
    }

    public String getBundle() {
        return null;
    }

    public Collection<Setting<?>> getSettings() {
        return null;
    }

    public Setting<?> getSetting(String name) {
        return null;
    }

    public Collection<Component> getDependencies() {
        return null;
    }

    public Collection<VirtualComponent> getUnsatisfiedDependencies() {
        return null;
    }
    public void resolve(VirtualComponent vc, Component comp) {
        throw new UnsupportedOperationException();
    }
}
