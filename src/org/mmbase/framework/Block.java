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
 * A Block is a representation of a page within a component. It has 2 renderers,
 * 'head' and 'body', and a  processor.
 *
 * @author Johannes Verelst
 * @author Michiel Meeuwissen
 * @version $Id: Block.java,v 1.30 2008-02-20 17:44:07 michiel Exp $
 * @since MMBase-1.9
 */
public class Block {

    private static final Logger log = Logging.getLoggerInstance(Block.class);

    private final Map<Renderer.Type, Renderer> renderers = new EnumMap<Renderer.Type, Renderer>(Renderer.Type.class);
    Processor processor;

    protected Parameter.Wrapper specific;

    private final String name;
    private final String mimetype;
    private final Component parent;
    private final LocalizedString description;
    private final Type[] classification;

    public Block(String name, String mimetype, Component parent, Type[] cla) {
        if (name   == null) throw new IllegalArgumentException();
        if (parent == null) throw new IllegalArgumentException();
        if (cla    == null) throw new IllegalArgumentException();
        this.name = name;
        this.parent = parent;
        this.mimetype = mimetype; // can this be null?
        this.classification = cla;
        for (Type t : classification) {
            t.blocks.add(this);
        }
        this.description = new LocalizedString(name);
    }

    /**
     * Name for this block. Never <code>null</code>
     */
    public String getName() {
        return name;
    }


    /**
     * Mimetype for this block. E.g. "text/html".
     */
    public String getMimeType() {
        return mimetype;
    }

    /**
     * Returns the 'classification' of this block. For example the blocks
     * classified as 'mmbase.admin' are presented in the mmbase admin-pages.
     */
    public Type[] getClassification() {
        return classification;
    }

    /**
     * @todo This method is not yet implemented.
     */
    public Type[] getClassification(String filter) {
        String[] parts = filter.split("\\s*?[,\\s]\\s*");
        for (Type type : classification) {

        }
        return null;
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


    void addParameters(Parameter ... params) {
        List<Parameter> help = new ArrayList<Parameter>();
        if (specific != null) {
            help.addAll(Arrays.asList(specific.getArguments()));
        }
        for (Parameter p : params) {
            help.add(p);
        }
        specific = new Parameter.Wrapper(help.toArray(Parameter.emptyArray()));
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

    /**
     * Every block can be assigned a hierarchal 'Type', which can classify it.
     */
    public static class Type {
        public static final Type ROOT = new Type("ROOT");
        /**
         * All unclassified blocks are of this type
         */
        public static final Type NO   = new Type("");

        /**
         * @javadoc
         */
        public static Type[] getClassification(String p, boolean create) {
            if (p == null || "".equals(p)) return new Type[] {NO};
            List<Type> r = new ArrayList<Type>();
            PARTS:
            for (String part : p.split("\\s*?[,\\s]\\s*")) {
                Type t = ROOT;
                for (String e : part.split("\\.")) {
                    Type proposal = new Type(e, t);
                    int i = t.subs.indexOf(proposal);
                    if (i == -1) {
                        if (create) {
                            t.subs.add(proposal);
                        } else {
                            continue PARTS;
                        }
                    } else {
                        proposal = t.subs.get(i);
                    }
                    t = proposal;
                }
                r.add(t);
            }
            return r.toArray(new Type[] {});
        }

        private final String name;
        private final Type parent;
        final List<Type>  subs   = new ArrayList<Type>();
        final List<Block> blocks = new ArrayList<Block>();
        private Type(String n) {
            name = n;
            parent = null;
        }
        protected Type(String n, Type p) {
            if (n == null) throw new IllegalArgumentException();
            if (p == null) throw new IllegalArgumentException();
            name = n;
            parent = p;
        }
        public List<Type> getSubTypes() {
            return Collections.unmodifiableList(subs);
        }

        public List<Block> getBlocks() {
            return Collections.unmodifiableList(blocks);
        }
        public String getName() {
            return name;
        }

        /**
         * @todo
         */
        public boolean contains(String test) {
            return false;
        }

        Type getParent() {
            return parent;
        }
        public boolean equals(Object o) {
            if (o instanceof Type) {
                Type t = (Type) o;
                return name.equals(t.name) && parent == t.parent;
            } else {
                return false;
            }
        }
        public int hashCode() {
            return name.hashCode();
        }
        public String toString() {
            return name + subs.toString();
        }
    }


}
