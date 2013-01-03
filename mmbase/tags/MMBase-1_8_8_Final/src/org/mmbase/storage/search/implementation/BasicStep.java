/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search.implementation;

import java.util.*;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.storage.search.*;

/**
 * Basic implementation.
 * The step alias is not set on default.
 *
 * @author Rob van Maris
 * @version $Id$
 * @since MMBase-1.7
 */
public class BasicStep implements Step {

    /** Associated builder. */
    protected MMObjectBuilder builder = null;
    /** Alias property. */
    protected String alias = null;
    /**
     * Nodenumber set for nodes to be included (ordered
     * using integer comparison).
     */
    protected SortedSet nodes = new TreeSet();
    /**
     * Constructor.
     *
     * @param builder The builder.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     */
    // package visibility!
    BasicStep(MMObjectBuilder builder) {
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
        if (alias != null && alias.trim().length() == 0) {
            throw new IllegalArgumentException(
            "Invalid alias value: " + alias);
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
    public BasicStep addNode(int nodeNumber) {
        if (nodeNumber < 0) {
            throw new IllegalArgumentException("Invalid nodeNumber value: " + nodeNumber);
        }
        nodes.add(new Integer(nodeNumber));
        return this;
    }

    /**
     * Gets the associated builder.
     *
     * @return The builder.
     */
    public MMObjectBuilder getBuilder() {
        return builder;
    }

    // javadoc is inherited
    public String getTableName() {
        return builder.getTableName();
    }

    // javadoc is inherited
    public String getAlias() {
        return alias;
    }

    // javadoc is inherited
    public SortedSet getNodes() {
        return Collections.unmodifiableSortedSet(nodes);
    }

    // javadoc is inherited
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Step && !(obj instanceof RelationStep)) {
            Step step = (Step) obj;
            return getTableName().equals(step.getTableName())
                && (alias == null ? step.getAlias() == null : alias.equals(step.getAlias()))
                && nodes.equals(step.getNodes());
        } else {
            return false;
        }
    }

    // javadoc is inherited
    public int hashCode() {
        return 41 * builder.getTableName().hashCode()
        + (alias == null? 0: 43 * alias.hashCode()) + 47 * nodes.hashCode();
    }

    // javadoc is inherited
    public String toString() {
        StringBuffer sb = new StringBuffer("Step(tablename:").
        append(getTableName()).
        append(", alias:").append(alias).
        append(", nodes:").append(nodes).
        append(")");
        return sb.toString();
    }

}
