/* 

  license to be added 

*/
package nl.didactor.metadata.handlers;

import org.mmbase.module.core.MMObjectNode;

/**
 * The MetadataHandler interface must be implemented by classes who are responsible
 * for automatically getting metadata from the cloud
 * @author Gerard van Enk <gvenk@millionpieces.nl>
 * @version $Id: MetadataHandler.java,v 1.1 2004-11-01 12:52:45 jdiepenmaat Exp $
 */
public interface MetadataHandler {

    /**
     * Gets the metadata value
     * @param learnobject the learnobject of which the metadata must be returned
     * @param metadefinition the definition which must be used to get the value
     * @return A List with String values
     */
    public Object getMetadata (MMObjectNode learnobject, MMObjectNode metadefinition);

}
