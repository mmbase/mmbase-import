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
 * A Block is a representation of a page within a component. It has two renderers,
 * 'head' and 'body', and a processor.
 *
 * Blocks can be configured in a {@link Component}. A typical block, this one is the
 * 'applications' block (the former 'Applications' admin page) which is one of the
 * core components, looks like this:
 *
 * <p>
 *    &lt;block name="applications" classification="mmbase.admin" mimetype="text/html"&gt;<br />
 *     &lt;title xml:lang="en"&gt;Applications&lt;/title&gt;<br />
 *     &lt;title xml:lang="nl"&gt;Applicaties&lt;/title&gt;<br />
 *     &lt;description xml:lang="en"&gt;Shows an overview of all MMBase apps1 applications&lt;/description&gt;<br />
 *     &lt;body jsp="applications.jspx"&gt;<br />
 *       &lt;param name="application" type="String" /&gt;<br />
 *     &lt;/body&gt;<br />
 *   &lt;/block&gt;
 * </p>
 *
 *
 * @author Johannes Verelst
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9
 */
public class Block {

    private static final Logger log = Logging.getLoggerInstance(Block.class);

    private final Map<Renderer.Type, Renderer> renderers = new EnumMap<Renderer.Type, Renderer>(Renderer.Type.class);
    private Processor processor;

    protected Parameter[] specific;
    protected Parameter[] paramDefinition = null;

    private final String name;
    private final String mimetype;
    private final Component parent;
    private final LocalizedString description;
    private final LocalizedString title;
    private final Type[] classification;

    public Block(String name, String mimetype, Component parent, String cla) {
        if (name   == null) throw new IllegalArgumentException();
        if (parent == null) throw new IllegalArgumentException();
        if (cla    == null) throw new IllegalArgumentException();
        this.name = name;
        this.parent = parent;
        this.mimetype = mimetype; // can this be null?
        this.classification = Block.Type.getClassification(cla, false);
        for (BlockContainer bc : Block.Type.getWeightedClassification(this, cla)) {
            bc.getType().blocks.add(bc);
            Collections.sort(bc.getType().blocks);
        }
        this.description = new LocalizedString(name);
        this.title       = new LocalizedString(name);
    }

    /**
     * Name for this block. Never <code>null</code>. The name identifies the block uniquely (between
     * the blocks of its component).
     */
    public String getName() {
        return name;
    }

    /**
     * A localized title for this block.
     */
    public LocalizedString getTitle() {
        return title;
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
        return Collections.unmodifiableMap(renderers);
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
            help.addAll(Arrays.asList(specific));
        }
        for (Parameter p : params) {
            help.add(p);
        }
        specific = help.toArray(Parameter.emptyArray());
        log.debug("Set parameters of " + this + " to " + help);
    }

    private void addParameters(List<Parameter> list, Parameter[] params) {
        for (Parameter p : params) {
            if (! (p instanceof PatternParameter)) {
                list.add(p);
            }
        }
    }
    private void addPatternParameters(List<Parameter> list, Parameter[] params) {
        for (Parameter p : params) {
            if (p instanceof PatternParameter) {
                list.add(p);
            }
        }
    }

    private synchronized void calculateParameterDefinition() {
        List<Parameter> list     = new ArrayList<Parameter>();

        addParameters(list, specific);
        addParameters(list, getRenderer(Renderer.Type.HEAD).getParameters());
        addParameters(list, getRenderer(Renderer.Type.BODY).getParameters());
        addParameters(list, getProcessor().getParameters());

        addPatternParameters(list, specific);
        addPatternParameters(list, getRenderer(Renderer.Type.HEAD).getParameters());
        addPatternParameters(list, getRenderer(Renderer.Type.BODY).getParameters());
        addPatternParameters(list, getProcessor().getParameters());

        paramDefinition = list.toArray(Parameter.emptyArray());

    }


    /**
     * Before rendering, it may have to be fed with certain parameters. Obtain a parameters
     * object which this method, fill it, and feed it back into {@link Renderer#render}.
     */
    public Parameters createParameters() {
        if (specific == null) {
            return new AutodefiningParameters();
        } else {
            if (paramDefinition == null) {
                calculateParameterDefinition();
            }
            return new Parameters(paramDefinition);
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

    Processor setProcessor(Processor p) {
        paramDefinition = null;
        Processor r = processor;
        processor = p;
        return r;
    }
    Renderer putRenderer(Renderer.Type type, Renderer renderer) {
        paramDefinition = null;
        return renderers.put(type, renderer);
    }

    /**
     * Every block can be assigned a hierarchal 'Type', which can classify it.
     */
    public static class Type implements Comparable<Type> {
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
            List<Type> result = new ArrayList<Type>();
            PARTS:
            for (String part : p.split("\\s*?[,\\s]\\s*")) {

                int colon = part.indexOf(":");
                if (colon > 0) {
                    part = part.substring(0, colon);
                }
                Type type = ROOT;
                for (String subpart : part.split("\\.")) {

                    Type proposal = new Type(subpart, type);
                    int index = type.subs.indexOf(proposal);
                    if (index == -1) {
                        if (create) {
                            type.subs.add(proposal);
                        } else {
                            continue PARTS;
                        }
                    } else {
                        proposal = type.subs.get(index);
                    }
                    Collections.sort(type.subs);
                    type = proposal;
                }
                result.add(type);
            }
            return result.toArray(new Type[] {});
        }

        static Collection<BlockContainer> getWeightedClassification(final Block b, final String p) {
            List<BlockContainer> result = new ArrayList<BlockContainer>();
            PARTS:
            for (String part : p.split("\\s*?[,\\s]\\s*")) {

                int weight;
                int colon = part.indexOf(":");
                if (colon > 0) {
                    weight = Integer.parseInt(part.substring(colon + 1));
                    part = part.substring(0, colon);
                } else {
                    weight = 100;
                }

                Type type = ROOT;
                for (String subpart : part.split("\\.")) {

                    Type proposal = new Type(subpart, type);
                    int index = type.subs.indexOf(proposal);
                    if (index == -1) {
                        continue PARTS;
                    } else {
                        proposal = type.subs.get(index);
                    }
                    type = proposal;
                }
                BlockContainer bc = new BlockContainer(b, type, weight);

                result.add(bc);
            }
            return result;
        }
        private final LocalizedString title;
        private final String name;
        private final Type parent;
        private int weight = 100;
        final List<Type>  subs   = new ArrayList<Type>();
        final List<BlockContainer> blocks = new ArrayList<BlockContainer>();
        private Type(String n) {
            name = n;
            parent = null;
            title = new LocalizedString(name);
        }
        protected Type(String n, Type p) {
            if (n == null) throw new IllegalArgumentException();
            if (p == null) throw new IllegalArgumentException();
            name = n;
            parent = p;
            title = new LocalizedString(name);
        }

        public List<Type> getSubTypes() {
            return Collections.unmodifiableList(subs);
        }

        public List<Block> getBlocks() {
            return new AbstractList<Block>() {
                public int size() {
                    return blocks.size();
                }
                public Block get(int i) {
                    return blocks.get(i).get();
                }
            };
        }
        public String getName() {
            return name;
        }

        public void setWeight(int w) {
            weight = w;
            if (parent != null) {
                Collections.sort(parent.subs);
            }
        }
        public int getWeight() {
            return weight;
        }

        /**
         * @todo
         */
        public boolean contains(String test) {
            return false;
        }

        public Type getParent() {
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

        public int compareTo(Type t) {
            int s = weight - t.weight;
            return s == 0 ? name.compareTo(t.name) : s;
        }

        public LocalizedString getTitle() {
            return title;
        }
    }


    /**
     * A wrapper class for a certain block in a type, which defined its weighted place in between
     * it's child block of the same type.
    */
    static class BlockContainer implements Comparable<BlockContainer> {
        final int weight;
        final Block block;
        final Block.Type type;
        BlockContainer(Block block, Block.Type type, int weight) {
            this.block  = block;
            this.weight = weight;
            this.type   = type;
        }

        Block get() {
            return block;
        }
        Block.Type getType() {
            return type;
        }
        public int compareTo(BlockContainer o) {
            int c =  weight - o.weight;
            if (c != 0) {
                return c;
            } else {
                return block.getName().compareTo(o.get().getName());
            }
        }
    }


}
