package nl.didactor.builders;
import nl.didactor.builders.*;
import org.mmbase.module.core.MMObjectNode;

/**
 * This class provides extra functionality for the Class builder. It calls
 * all components to notify that a new class was created, through the 
 * helperclass 'CreationNotifyBuilder'.
 * @author Johannes Verelst &lt;johannes.verelst@eo.nl&gt;
 */
public class ClassBuilder extends AbstractSmartpathBuilder {

    /**
     * Override the insert() method to allow the components
     * to be signalled.
     */
    public int insert(String owner, MMObjectNode node) {
        int number = super.insert(owner, node);
        if (number > 0) {
            MMObjectNode object = getNode(number);
            CreationNotifyBuilder.signalComponents(object);
        }
        return number;
    }
}

