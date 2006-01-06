/**
 * Component description interface.
 */
package nl.didactor.component.address;
import nl.didactor.component.Component;
import nl.didactor.builders.DidactorBuilder;
import nl.didactor.component.core.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.storage.search.RelationStep;
import org.mmbase.storage.search.SearchQueryException;
import org.mmbase.storage.search.implementation.NodeSearchQuery;
import java.util.List;
import java.util.Vector;

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
        return "address";
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
        super.init();
        MMBase mmbase = MMBase.getMMBase();
        DidactorBuilder people = (DidactorBuilder)mmbase.getBuilder("people");
        people.registerPostInsertComponent(this, 10);
        people.registerPreDeleteComponent(this, 10);
    }

    public void install() {
        MMBase mmbase = MMBase.getMMBase();
        DidactorBuilder people = (DidactorBuilder)mmbase.getBuilder("people");
        try {
            List nodes = people.getNodes(new NodeSearchQuery(people));
            for (int i=0; i<nodes.size(); i++) {
                postInsert((MMObjectNode)nodes.get(i));
            }
        } catch (SearchQueryException e) {
        }
    }


    /**
     * This method is called when a new object is added to Didactor. If the component
     * needs to insert objects for this object, it can do so. 
     */
    public boolean postInsert(MMObjectNode node) {
        if (node.getBuilder().getTableName().equals("people")) {
            return createUser(node);
        }
        return true;
    }

    /**
     * This method is called just before an object is removed from MMBase.
     */
    public boolean preDelete(MMObjectNode node) {
        if (node.getBuilder().getTableName().equals("people")) {
            return deleteUser(node);
        }
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

    /**
     * Delete the addressbook of the user.
     */
    private boolean deleteUser(MMObjectNode user) {
        Vector addressbooks = user.getRelatedNodes("addressbooks", "related", RelationStep.DIRECTIONS_DESTINATION);

        // Iterate the addressbooks, to remove them all
        for (int i=0; i<addressbooks.size(); i++) {
            MMObjectNode book = (MMObjectNode)addressbooks.get(i);
            Vector contacts = book.getRelatedNodes("contacts", "related", RelationStep.DIRECTIONS_DESTINATION);

            // Iterate the contacts, to remove them all
            for (int j=0; j<contacts.size(); j++) {
                MMObjectNode contact = (MMObjectNode)contacts.get(j);
                contact.getBuilder().removeRelations(contact);
                contact.getBuilder().removeNode(contact);
            }
            book.getBuilder().removeRelations(book);
            book.getBuilder().removeNode(book);
        }
        return true;
    }
}
