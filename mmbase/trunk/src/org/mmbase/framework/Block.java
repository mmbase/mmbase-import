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
 * @version $Id: Block.java,v 1.4 2006-10-13 14:40:00 michiel Exp $
 * @since MMBase-1.9
 */
public class Block {
    private final Map<Renderer.Type, Renderer> renderers = new HashMap();
    Processor processor;

    String name;
    String mimetype;

    public Block(String name, String mimetype) {
        this.name = name;
        this.mimetype = mimetype;
    }
    Map<Renderer.Type, Renderer> getRenderers() {
        return renderers;
    }
    public Renderer getRenderer(Renderer.Type type) {
        return renderers.get(type);
    }

    public Processor getProcessor() {
        return processor;
    }

}
