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
 * A Block is a representation of a page within a component. It consists of 3 views, 
 * a 'head', 'body' and 'process' view. 
 *
 * @author Johannes Verelst
 * @version $Id: Block.java,v 1.12 2006-10-19 17:31:48 michiel Exp $
 * @since MMBase-1.9
 */
public class Block {
    private final Map<Renderer.Type, Renderer> renderers = new EnumMap<Renderer.Type, Renderer>(Renderer.Type.class);
    Processor processor;

    private final String name;
    private final String mimetype;
    private final Component parent;
    private final LocalizedString description;

    public Block(String name, String mimetype, Component parent) {
        if (name == null) throw new IllegalArgumentException();
        this.name = name;
        this.mimetype = mimetype;
        this.parent = parent;
        this.description = new LocalizedString(name);
    }

    /**
     * Name for this block. Never <code>null</code>
     */
    public String getName() {
        return name;
    }

    /**
     * Description for this block. Never <code>null</code>
     */
    public LocalizedString getDescription() {
        return description;
    }

    /**
     * All renderers assiociated with this Block. This is not a public method (it is used to create
     * the block). Use {@link #getRenderer(Renderer.Type}).
     */
    Map<Renderer.Type, Renderer> getRenderers() {
        return renderers;
    }

    /**
     * @return A renderer for the given Render type. Never <code>null</code>
     */
    public Renderer getRenderer(Renderer.Type type) {
        Renderer rend = renderers.get(type);
        return rend == null ? type.getEmpty(this) : rend;
    }

    /**
     * @return The processor associated with this block. Never <code>null</code>
     */
    public Processor getProcessor() {
        return processor == null ? Processor.EMPTY : processor;
    }


    /**
     * @return the Component from which this block is a part.
     */
    public Component getComponent() {
        return parent;
    }

    public String toString() {
        return getName();
    }
}
