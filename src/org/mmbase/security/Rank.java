/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security;

import java.util.*;

/**
 * This class is somekinda enumeration of the ranks possible within
 * the security context
 * @javadoc
 * @author Eduard Witteveen
 * @version $Id: Rank.java,v 1.8 2003-06-16 17:25:46 michiel Exp $
 */
public final class Rank {
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

    protected static void registerRank(Rank rank) {
        ranks.put(rank.toString(), rank);
    }

    public static Rank registerRank(int rank, String rankDesc) {
        Rank rankObject = new Rank(rank, rankDesc);
        registerRank(rankObject);
        return rankObject;
    }

    public static Set getRanks() {
        return new HashSet(ranks.values());
    }

    public boolean equals(Object o) {
        if (o instanceof Rank) {
            Rank r = (Rank) o;
            return r.rank == rank;
        } else {
            return false;
        }
    }
}
