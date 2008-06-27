package nl.didactor.component.metadata.autofill;

import org.mmbase.bridge.*;


public interface HandlerInterface {
    /**
     * Add metadata to object
     * @param node Node object's node
     */
    public void addMetaData(Node nodeMetaDefinition, Node nodeObject);


    /**
     * Checks wheither or not metadata correct
     * @param node Node object's node
     * @return boolean result
     */
    public boolean checkMetaData(Node nodeMetaDefinition, Node nodeObject);

}
