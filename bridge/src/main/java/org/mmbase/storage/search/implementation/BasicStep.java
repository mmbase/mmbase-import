/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search.implementation;

import java.util.*;
import org.mmbase.storage.search.*;

/**
 * Basic implementation.
 * The step alias is not set on default.
 *
 * @author Rob van Maris
 * @version $Id$
 * @since MMBase-1.7
 */
public class BasicStep implements Step, java.io.Serializable {
    private static final long serialVersionUID = 1L;

    protected final String builder;

    protected String alias = null;
    /**
     * Nodenumber set for nodes to be included (ordered
     * using integer comparison).
     */
    protected SortedSet<Integer> nodes = null;

    protected boolean modifiable = true;
    /**
     * Constructor.
     *
     * @param builder The builder.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     */
    // package visibility!
    BasicStep(String builder) {
        if (builder == null) {
            throw new IllegalArgumentException("Invalid builder value: " + builder);
        }
        this.builder = builder;
    }

    /**
     * Sets alias property.
     *
     * @param alias The alias property.
     * @return This <code>BasicStep</code> instance.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     */
    public BasicStep setAlias(String alias) {
        if (! modifiable) throw new IllegalStateException();
        if (alias != null && alias.trim().length() == 0) {
            throw new IllegalArgumentException("Invalid alias value: " + alias);
        }
        this.alias = alias;
        return this;
    }

    /**
     * Adds node to nodes.
     *
     * @param nodeNumber The nodenumber of the node.
     * @return This <code>BasicStep</code> instance.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     */
    @Override
    public Step addNode(int nodeNumber) {
        if (! modifiable) throw new IllegalStateException();
        if (nodes == null) {
            nodes =  new TreeSet<Integer>();
        }
        nodes.add(nodeNumber);
        return this;
    }

    /**
     * Gets the associated builder.
     *
     * @return The builder.
     */
    /*
    public MMObjectBuilder getBuilder() {
        return org.mmbase.module.core.MMBase.getMMBase().getBuilder(builder);
    }
    */

    @Override
    public String getTableName() {
        return builder;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public SortedSet<Integer> getNodes() {
        return nodes == null ? null : (modifiable ? nodes : Collections.unmodifiableSortedSet(nodes));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Step && !(obj instanceof RelationStep)) {
            Step step = (Step) obj;
            return getTableName().equals(step.getTableName())
                && (alias == null ? step.getAlias() == null : alias.equals(step.getAlias()))
                && (nodes == null ? step.getNodes() == null : nodes.equals(step.getNodes()));
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return 41 * builder.hashCode()
            + (alias == null? 0: 43 * alias.hashCode()) + 47 * (nodes == null ? 1 : nodes.hashCode());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Step(tablename:").append(getTableName()).
        append(", alias:").append(alias).
        append(", nodes:").append(nodes).
        append(")");
        return sb.toString();
    }

    @Override
    public void setUnmodifiable() {
        modifiable = false;
    }




}
