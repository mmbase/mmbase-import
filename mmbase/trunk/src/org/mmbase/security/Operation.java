package org.mmbase.security;

/**
 *  This class is somekinda enumeration of the operations possible within 
 *  the security context
 */
public final class Operation {
    /** int value for the read Operation*/
    public final static int READ_INT = 0;
    
    /** int value for the write Operation*/    
    public final static int WRITE_INT = 1;
    
    /** int value for the create Operation*/    
    public final static int CREATE_INT = 2;
    
    /** int value for the link Operation */    
    public final static int LINK_INT = 3;
       
    /** int value for the remove Operation */    
    public final static int REMOVE_INT = 4;

    /** Identifier for read operation, which is used for reading information*/    
    public final static Operation READ = new Operation(READ_INT);
    
    /** Identifier for write operation, which is used for writing information*/        
    public final static Operation WRITE = new Operation(WRITE_INT);
    
    /** 
     *	Identifier for create operation, which is used for creating a new node.
     *	This only applies on NodeManagers (builders)
     */            
    public final static Operation CREATE = new Operation(CREATE_INT);
    
    /** 
     *	Identifier for link operation, which is used when creating a relation 
     *	between 2 nodes.
     */                
    public final static Operation LINK = new Operation(LINK_INT);
    
    /** Identifier for remove operation, which is used when removing a node */                    
    public final static Operation REMOVE = new Operation(REMOVE_INT);


    /**
     *	Private constructor, to prevent creation of new Operations
     */
    private Operation(int level) {
    	this.level = level;
    }

    /**
     *	This method gives back the internal int value of the Operation,
     *	which can be used in switch statements
     *	@return the internal int value
     */
    public int getInt(){
    	return level;
    }
  
    /** the int value of the instance */
    private int level;
}
