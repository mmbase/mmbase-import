package org.mmbase.security;

/**
 *  This class is somekinda enumeration of the ranks possible within 
 *  the security context
 */
public final class Rank {
    /** int value for the anonymous Rank*/
    public final static int ANONYMOUS_INT = 0;
    
    /** int value for the anonymous Rank*/
    public final static int ADMIN_INT = 73059;

    /** Identifier for anonymous rank*/    
    public final static Rank ANONYMOUS = new Rank(ANONYMOUS_INT, "anonymous");
    

    /** Identifier for admin rank*/    
    public final static Rank ADMIN = new Rank(ADMIN_INT, "administrator");

    /**
     *	Private constructor, to prevent creation of new Ranks
     */
    private Rank(int rank, String description) {
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
    public String toString(){
    	return description;
    }
  
    /** the int value of the instance */
    private int rank;
    
    /** the description of this rank */    
    private String description;
}
