package nl.didactor.builders;
import nl.didactor.component.Component;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMObjectNode;

/**
 * This class provides extra functionality for those builders
 * which want to signal the components that a new object was
 * created. By extending this builder all inserts will
 * automatically be captured and will trigger a notify.
 * There is one static method which allows this class to
 * act as a delegate in case you need multiple inheritance
 * @author Johannes Verelst &lt;johannes.verelst@eo.nl&gt;
 */
public class CreationNotifyBuilder extends MMObjectBuilder {

    /**
     * Override the insert() method to allow the components
     * to be signalled.
     */
    public int insert(String owner, MMObjectNode node) {
        int number = super.insert(owner, node);
        if (number > 0) {
            MMObjectNode object = getNode(number);
            signalComponents(object);
        }
        return number;
    }

    public static void signalComponents(MMObjectNode node) {
        Component[] components = Component.getComponents();
        for (int i=0; i<components.length; i++) {
            if (components[i] != null) {
                components[i].notifyCreate(node);
            }
        }
    }
}

