/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.lucene;

import java.util.*;
import org.mmbase.util.CloseableIterator;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.Cloud;
import org.apache.lucene.analysis.Analyzer;

/**
 * Defines a query and possible options for the fields to index.
 *
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @version $Id: IndexDefinition.java,v 1.12 2006-09-11 13:59:14 michiel Exp $
 **/
interface IndexDefinition {


    /**
     * Returns an Iterator over all {@link IndexEntry}'s defined by this index.
     */
    CloseableIterator<? extends IndexEntry> getCursor();

    /**
     * Returns an Iterator over all {@link IndexEntry}'s defined by this index, restricted by a
     * certain identifier. This is for use for 'subqueries'.
     */
    CloseableIterator<? extends IndexEntry> getSubCursor(String parentIdentifier);


    /**
     * Returns the entry(s) with the given identifier
     */
    CloseableIterator<? extends IndexEntry> getCursor(String identifier);

    /**
     * If this indexdefinition is a 'sub definition' then, a parent IndexEntry can be available...
     */
    IndexEntry getParent();
    /**
     * Per index a an Analyzer can be defined.
     */
    Analyzer getAnalyzer();

    /**
     * Defines how a Node for this index must be produced. For MMBase indices this is of course
     * quite straight-forward, but other indices may create virtual nodes here.
     */
    Node getNode(Cloud cloud, String identifier);


}

