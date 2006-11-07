/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework;
import java.util.*;
import org.mmbase.util.functions.*;
import org.mmbase.util.LocalizedString;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * A Block is a representation of a page within a component. It consists of 3 views,
 * a 'head', 'body' and 'process' view.
 *
 * @author Johannes Verelst
 * @version $Id: Block.java,v 1.17 2006-11-07 18:55:03 michiel Exp $
 * @since MMBase-1.9
 */
public class Block {

    private static final Logger log = Logging.getLoggerInstance(Block.class);

    public enum Type {
        ADMIN, FRONTEND
    }

    private final Map<Renderer.Type, Renderer> renderers = new EnumMap<Renderer.Type, Renderer>(Renderer.Type.class);

    Processor processor;
    protected Parameter.Wrapper specific;


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
     * Mimetype for this block.
     */
    public String getMimeType() {
        return mimetype;
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
        return processor == null ? AbstractProcessor.getEmpty(this) : processor;
    }


    void addParameters(Parameter<?>... params) {
        List<Parameter> help = new ArrayList<Parameter>();
        if (specific != null) {
            help.addAll(Arrays.asList(specific.getArguments()));
        }
        for (Parameter p : params) {
            help.add(p);
        }
        specific = new Parameter.Wrapper(help.toArray(Parameter.EMPTY));
        log.debug("Set parameters of " + this + " to " + help);
    }


    /**
     * Before rendering, it may have to be fed with certain parameters. Obtain a parameters
     * object which this method, fill it, and feed it back into {@link Renderer#render}.
     */
    public Parameters createParameters() {
        if (specific == null) {
            return new AutodefiningParameters();
        } else {
            return new Parameters(specific,
                                  new Parameter.Wrapper(getRenderer(Renderer.Type.HEAD).getParameters()),
                                  new Parameter.Wrapper(getRenderer(Renderer.Type.BODY).getParameters()),
                                  new Parameter.Wrapper(getProcessor().getParameters()));
        }
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
