/**
 * Component description interface.
 */
package nl.didactor.component.address;
import nl.didactor.component.Component;
import nl.didactor.builders.DidactorBuilder;
import nl.didactor.component.core.*;
import org.mmbase.bridge.Cloud;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import java.util.Map;

public class DidactorAddress extends Component {
    /**
     * Returns the version of the component
     */
    public String getVersion() {
        return "2.0";
    }

    /**
     * Returns the name of the component
     */
    public String getName() {
        return "DidactorSources";
    }

    /**
     * Returns an array of components this component depends on.
     * This should always contain at least one component: 'DidactorCore'
     */
    public Component[] dependsOn() {
        Component[] components = new Component[1];
        components[0] = new DidactorCore();
        return components;
    }

    public void init() {
        MMBase mmbase = MMBase.getMMBase();
        DidactorBuilder people = (DidactorBuilder)mmbase.getBuilder("people");
        people.registerPostInsertComponent(this, 10);
    }

    /**
     * This method is called when a new object is added to Didactor. If the component
     * needs to insert objects for this object, it can do so. 
     */
    public boolean postInsert(MMObjectNode node) {
        if (node.getBuilder().getTableName().equals("people"))
            return createUser(node);
        return true;
    }

    /**
     * Create the addressbook for this user
     */
    private boolean createUser(MMObjectNode user) {
        MMBase mmb = user.getBuilder().getMMBase();
        String username = user.getStringValue("username");
        MMObjectBuilder addressbooks = mmb.getBuilder("addressbooks");
        InsRel insrel = mmb.getInsRel();
        int related = mmb.getRelDef().getNumberByName("related");

        MMObjectNode addressbook = addressbooks.getNewNode(username);
        addressbook.setValue("name", "Adresboek van " + username);
        addressbooks.insert(username, addressbook);
        
        MMObjectNode relation = insrel.getNewNode(username);
        relation.setValue("snumber", user.getNumber());
        relation.setValue("dnumber", addressbook.getNumber());
        relation.setValue("rnumber", related);
        insrel.insert(username, relation);
       
        return true;
    }
}
