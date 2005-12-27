/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.lucene;

import java.util.*;
import org.mmbase.util.CloseableIterator;
import org.mmbase.bridge.util.xml.query.QueryDefinition;
import org.mmbase.bridge.*;
import org.apache.lucene.analysis.Analyzer;

/**
 * Defines a query and possible options for the fields to index.
 *
 * @author Pierre van Rooden
 * @version $Id: IndexDefinition.java,v 1.5 2005-12-27 15:45:06 michiel Exp $
 **/
interface IndexDefinition {

    /**
     * Returns an Iterator over all {@link IndexEntry}'s defined by this index.
     */
    CloseableIterator getCursor();
    /**
     * Returns an Iterator over all {@link IndexEntry}'s defined by this index, restricted by a certain identifier.
     */
    CloseableIterator getSubCursor(String identifier);

    /**
     * If this indexdefinition is a 'sub definition' then, a parent IndexEntry can be available...
     */
    IndexEntry getParent();


    /**
     * Per index a an Analyzer can be defined.
     */
    Analyzer getAnalyzer();

    /**
     * Defines how.
     */
    Node getNode(Cloud cloud, String identifier);

}

