package nl.eo.batchupload.connector;

import java.util.Hashtable;
import java.util.Vector;

/**
 * Interface that describes the methods needed for an implementation class to
 * do operations on a database. One of the implementation classes provided
 * is 'DoveWrapper', which implements this interface and connects to 
 * MMBase using the 'Dove Servlet'.
 * @author Johannes Verelst <johannes@verelst.net>
 * @version $ID$
 */
public interface ConnectorInterface {
   	/**
   	 * Validate credentials against the storage server
   	 * @param username The username to validate
   	 * @param password the password to validate
   	 * @return empty string on success, error message otherwise
   	 */
    public String validate (String username, String password);

    /**
     * Upload an object to the storage server
     * @param newObject a hashtable with key-value pairs. All values that
     * start with an underscore are 'special' fields.
     * @return The object-id of the newly created object
     */
    public int create(Hashtable newObject);

	/**
	 * Search the storage server for objects matching the querystring.
	 * All objects will be places as Hashtable's in a Vector.
	 */
   	public Vector search(String querystring);
    
    /**
     * Create a relation from one object to another.
     * @param source The source object (id)
     * @param destination The destination object (id)
     * @param type The type of the relation (eg. insrel)
     */
    public void relate(int source, int destination, String type);
    
}
