/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.util;
import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.legacy.*;
import org.mmbase.bridge.implementation.*;
import org.mmbase.util.StringSplitter;
import org.mmbase.util.Encode;
import org.mmbase.util.logging.*;
import org.mmbase.module.core.ClusterBuilder;
import org.mmbase.module.core.MMBase;
import java.util.*;

/**
 * This class contains various utility methods for manipulating and creating query objecs. Most
 * essential methods are available on the Query object itself, but too specific or legacy-ish
 * methods are put here.
 *
 * @author Michiel Meeuwissen
 * @version $Id: Queries.java,v 1.1 2003-09-02 19:45:52 michiel Exp $
 * @see  org.mmbase.bridge.Query
 * @since MMBase-1.7
 */
public class Queries {
    private static final Logger log = Logging.getLoggerInstance(Queries.class);

    /**
     * Creates a Query object using arguments for {@link Cloud#getList} (this function is of course
     * implemented using this utility). This is usefull the convert (legacy) code which uses
     * getList, but you want to use new Query features without rewriting the complete thing.
     *
     * It can also be simply handy to specify things as Strings.
     */
    public static Query createQuery(Cloud cloud,                            
                                    String startNodes,
                                    String nodePath,
                                    String fields,
                                    String constraints,
                                    String orderby,
                                    String directions,
                                    String searchDir,
                                    boolean distinct) {

        
        {
            // the bridge test case say that you may also specifiy empty string (why?)
            if ("".equals(startNodes))
                startNodes = null;
            if ("".equals(fields))
                fields = null;
            if ("".equals(constraints))
                constraints = null;
            if ("".equals(searchDir))
                searchDir = null;
            // check invalid search command
            Encode encoder = new Encode("ESCAPE_SINGLE_QUOTE");
            // if(startNodes != null) startNodes = encoder.encode(startNodes);
            // if(nodePath != null) nodePath = encoder.encode(nodePath);
            // if(fields != null) fields = encoder.encode(fields);
            if (orderby != null)
                orderby = encoder.encode(orderby);
            if (directions != null)
                directions = encoder.encode(directions);
            if (searchDir != null)
                searchDir = encoder.encode(searchDir);
            if (constraints != null && !validConstraints(constraints)) {
                throw new BridgeException("invalid constraints:" + constraints);
            }
        }

        // create query object
        Query query;
        {
            ClusterBuilder clusterBuilder = MMBase.getMMBase().getClusterBuilder();
            int search = -1;
            if (searchDir != null) {
                search = ClusterBuilder.getSearchDir(searchDir);
            }

            List snodes = StringSplitter.split(startNodes);
            List tables = StringSplitter.split(nodePath);
            List f = StringSplitter.split(fields);
            List orderVec = StringSplitter.split(orderby);
            List d = StringSplitter.split(directions);
            try {
                // pitty that we can't use cloud.createQuery for this.
                // but all essential methods are on ClusterBuilder
                query = new BasicQuery(cloud, clusterBuilder.getMultiLevelSearchQuery(snodes, f, distinct ? "YES" : "NO", tables, constraints, orderVec, d, search));
            } catch (IllegalArgumentException iae) {
                throw new BridgeException(iae);
            }
        }
        return query;
    }


    /** returns false, when escaping wasnt closed, or when a ";" was found outside a escaped part (to prefent spoofing) */
    static boolean validConstraints(String constraints) {
        // first remove all the escaped "'" ('' occurences) chars...
        String remaining = constraints;
        while (remaining.indexOf("''") != -1) {
            int start = remaining.indexOf("''");
            int stop = start + 2;
            if (stop < remaining.length()) {
                String begin = remaining.substring(0, start);
                String end = remaining.substring(stop);
                remaining = begin + end;
            } else {
                remaining = remaining.substring(0, start);
            }
        }
        // assume we are not escaping... and search the string..
        // Keep in mind that at this point, the remaining string could contain different information
        // than the original string. This doesnt matter for the next sequence...
        // but it is important to realize!
        while (remaining.length() > 0) {
            if (remaining.indexOf('\'') != -1) {
                // we still contain a "'"
                int start = remaining.indexOf('\'');

                // escaping started, but no stop
                if (start == remaining.length()) {
                    log.warn("reached end, but we are still escaping(you should sql-escape the search query inside the jsp-page?)\noriginal:" + constraints);
                    return false;
                }

                String notEscaped = remaining.substring(0, start);
                if (notEscaped.indexOf(';') != -1) {
                    log.warn(
                        "found a ';' outside the constraints(you should sql-escape the search query inside the jsp-page?)\noriginal:"
                            + constraints
                            + "\nnot excaped:"
                            + notEscaped);
                    return false;
                }

                int stop = remaining.substring(start + 1).indexOf('\'');
                if (stop < 0) {
                    log.warn(
                        "reached end, but we are still escaping(you should sql-escape the search query inside the jsp-page?)\noriginal:"
                            + constraints
                            + "\nlast escaping:"
                            + remaining.substring(start + 1));
                    return false;
                }
                // we added one to to start, thus also add this one to stop...
                stop = start + stop + 1;

                // when the last character was the stop of our escaping
                if (stop == remaining.length()) {
                    return true;
                }

                // cut the escaped part from the string, and continue with resting sting...
                remaining = remaining.substring(stop + 1);
            } else {
                if (remaining.indexOf(';') != -1) {
                    log.warn("found a ';' inside our constrain:" + constraints);
                    return false;
                }
                return true;
            }
        }
        return true;
    }


    public static Query addConstraints(NodeQuery query, String constraints) {

        if (constraints != null) {
            query.setConstraint(new ConstraintParser(query).toConstraint(constraints));
        }
        return query;

    }

    public static Query addSortOrders(NodeQuery query, String sorted, String directions) {
        // following code was copied from MMObjectBuilder.setSearchQuery (bit ugly)
        if (directions == null) {
            directions = "";
        }
        
        if (sorted != null) {
            NodeManager nodeManager = query.getNodeManager();

            StringTokenizer sortedTokenizer     = new StringTokenizer(sorted, ",");
            StringTokenizer directionsTokenizer = new StringTokenizer(directions, ",");
            
            while (sortedTokenizer.hasMoreElements()) {
                String    fieldName = sortedTokenizer.nextToken().trim();
                Field field = nodeManager.getField(fieldName);
                StepField stepField = query.getStepField(field);

                int dir = SortOrder.ORDER_ASCENDING;
                if (directionsTokenizer.hasMoreElements()) {
                    String direction = directionsTokenizer.nextToken().trim();
                    if (direction.equalsIgnoreCase("DOWN")) {
                        dir = SortOrder.ORDER_DESCENDING;
                    } else {
                        dir = SortOrder.ORDER_ASCENDING;
                    }
                }
                query.addSortOrder(stepField, dir);
            }
        }
        return query;
    }

    /**
     * Takes the query, and does a count with the same constraints.
     *
     */
    public static int count(Query query) {
        Cloud cloud = query.getCloud();       
        Query count = query.aggregatingClone();        
        Step step = (Step) (count.getSteps().get(0));
        count.addAggregatedField(step, cloud.getNodeManager(step.getTableName()).getField("number"), AggregatedField.AGGREGATION_TYPE_COUNT);
        
        Node result = (Node) cloud.getList(count).get(0);
        return result.getIntValue("number");
    }

}
