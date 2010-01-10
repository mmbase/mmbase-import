/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security;

import org.mmbase.util.HashCodeUtil;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import java.util.*;

/**
 * This class defines Rank objects which are used in security implementation. Ranks can be
 * associated with users. Every Rank has an unique integer 'height' (so every rank is higher or
 * lower than any other rank) and a String which can be used to identify it.
 *
 * Possible Ranks are maintained by static methods in this class. Generally the 'anonymous', 'basic
 * user' and 'adminstrator' ranks should always be available, and only be delete with good reason.
 *
 *
 * @author Eduard Witteveen
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @version $Id$
 */
public final class Rank implements Comparable, java.io.Serializable {

    private static Logger log = Logging.getLoggerInstance(Rank.class);

    private static final int serialVersionUID = 1; // increase this if object chages.

    /** int value for the anonymous Rank*/
    public final static int ANONYMOUS_INT = 0;

    /** int value for the basic user Rank*/
    public final static int BASICUSER_INT = 100;

    /** int value for the anonymous Rank*/
    public final static int ADMIN_INT = 73059;

    /** Identifier for anonymous rank*/
    public final static Rank ANONYMOUS = new Rank(ANONYMOUS_INT, "anonymous");

    /** Identifier for basic user rank*/
    public final static Rank BASICUSER = new Rank(BASICUSER_INT, "basic user");

    /** Identifier for admin rank*/
    public final static Rank ADMIN = new Rank(ADMIN_INT, "administrator");

    private static Map ranks = new HashMap();


    static {
        registerRank(ANONYMOUS); 
        registerRank(BASICUSER); 
        registerRank(ADMIN); 
    }

    /**
     *	constructor
     */
    protected Rank(int rank, String description) {
        this.rank = rank;
        this.description = description;
    }

    /**
     *	This method gives back the internal int value of the rank
     *	which can be used in switch statements
     *	@return the internal int value
     */
    public int getInt(){
        return rank;
    }

    /**
     *	@return a string containing the description of the rank
     */
    public String toString() {
        return description;
    }

    /** the int value of the instance */
    private int rank;

    /** the description of this rank */
    private String description;

    public static Rank getRank(String rankDesc) {
        return (Rank) ranks.get(rankDesc);
    }

    /**
     * @since MMBase-1.6.4
     */
    protected static Rank registerRank(Rank rank) {
        Rank prev = (Rank) ranks.put(rank.toString(), rank);
        if (prev == null) {
            log.service("Registered rank " + rank);
        } else {
            log.service("Replaced rank " + rank);
        }
        return prev;
    }

    /**
     * Creates and adds a new Rank for the security system.
     *
     * @since MMBase-1.6.4
     */

    public static Rank createRank(int rank, String rankDesc) {
        Rank rankObject = new Rank(rank, rankDesc);
        registerRank(rankObject);
        return rankObject;
    }

    /**
     * Removes a rank from the security system.
     * @since MMBase-1.6.4
     */

    public static Rank deleteRank(String rankDesc) {
        return (Rank) ranks.remove(rankDesc);
    }

    /**
     * Returns all ranks currently known by the security implemetation.  Default and to start with there
     * are three ranks available: 'anonymous', 'basic user' and 'administrator'.  You probably
     * should never remove them.
     * @since MMBase-1.6.4
     */
    public static SortedSet getRanks() {
        return new TreeSet(ranks.values());
    }

    /**
     * @since MMBase-1.6.4
     */
    // see javadoc of Object
    public boolean equals(Object o) {
        if (o instanceof Rank) {
            Rank r = (Rank) o;
            return r.rank == rank && r.description.equals(description);
        } else {
            return false;
        }
    }

    
    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        int result = 0;
        result = HashCodeUtil.hashCode(result, rank);
        result = HashCodeUtil.hashCode(result, description);
        return result;
    }
    
    /**
     * @since MMBase-1.6.4
     */
    // see javadoc of Comparable
    public int compareTo (Object o) {
        Rank r = (Rank) o;
        return rank - r.rank;
    }
}
