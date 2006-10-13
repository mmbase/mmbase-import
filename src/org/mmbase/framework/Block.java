/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework;
import java.util.*;


/**
 * A Block is a representation of a page within a component. It consists of 3 views, 
 * a 'head', 'body' and 'process' view. 
 *
 * @author Johannes Verelst
 * @version $Id: Block.java,v 1.6 2006-10-13 23:00:03 johannes Exp $
 * @since MMBase-1.9
 */
public class Block {
    private final Map<Renderer.Type, Renderer> renderers = new HashMap();
    Processor processor;

    private final String name;
    private final String mimetype;
    private final Component parent;

    public Block(String name, String mimetype, Component parent) {
        this.name = name;
        this.mimetype = mimetype;
        this.parent = parent;
    }

    Map<Renderer.Type, Renderer> getRenderers() {
        return renderers;
    }

    public Renderer getRenderer(Renderer.Type type) {
        Renderer rend = renderers.get(type);
        return rend == null ? type.getEmpty() : rend;
    }

    public Processor getProcessor() {
        return processor;
    }

    public Component getComponent() {
        return parent;
    }
}
