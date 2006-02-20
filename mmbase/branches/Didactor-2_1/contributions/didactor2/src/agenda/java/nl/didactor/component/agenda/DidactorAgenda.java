package nl.didactor.component.agenda;
import nl.didactor.builders.DidactorBuilder;
import nl.didactor.component.Component;
import nl.didactor.component.core.*;
import org.mmbase.bridge.Cloud;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.storage.search.RelationStep;
import org.mmbase.storage.search.SearchQueryException;
import org.mmbase.storage.search.implementation.NodeSearchQuery;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Enumeration;

public class DidactorAgenda extends Component {
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
        return "agenda";
    }

    /**
     * Returns an array of components this component depends on.
     * This should always contain at least one component: 'DidactorCore'
     */
    public Component[] dependsOn() {
        Component[] components = new Component[0];
        return components;
    }

    public void init() {
        super.init();
        MMBase mmbase = MMBase.getMMBase();
        DidactorBuilder people = (DidactorBuilder)mmbase.getBuilder("people");
        people.registerPostInsertComponent(this, 10);
        people.registerPreDeleteComponent(this, 10);
        DidactorBuilder classes = (DidactorBuilder)mmbase.getBuilder("classes");
        classes.registerPostInsertComponent(this, 10);
        classes.registerPreDeleteComponent(this, 10);
        DidactorBuilder workgroups = (DidactorBuilder)mmbase.getBuilder("workgroups");
        workgroups.registerPostInsertComponent(this, 10);
        workgroups.registerPreDeleteComponent(this, 10);
    }

    public void install() {
        MMBase mmbase = MMBase.getMMBase();
        DidactorBuilder people = (DidactorBuilder)mmbase.getBuilder("people");
        DidactorBuilder classes = (DidactorBuilder)mmbase.getBuilder("classes");
        DidactorBuilder workgroups = (DidactorBuilder)mmbase.getBuilder("workgroups");
        try {
            List nodes = people.getNodes(new NodeSearchQuery(people));
            for (int i=0; i<nodes.size(); i++) {
                postInsert((MMObjectNode)nodes.get(i));
            }
            nodes = classes.getNodes(new NodeSearchQuery(classes));
            for (int i=0; i<nodes.size(); i++) {
                postInsert((MMObjectNode)nodes.get(i));
            }
            nodes = workgroups.getNodes(new NodeSearchQuery(workgroups));
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
        if (node.getBuilder().getTableName().equals("people"))
            return createUser(node);

        if (node.getBuilder().getTableName().equals("classes"))
            return createClass(node);

        if (node.getBuilder().getTableName().equals("workgroups")) {
            return createWorkgroup(node);
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

        if (node.getBuilder().getTableName().equals("classes")) {
            return deleteClass(node);
        }

        if (node.getBuilder().getTableName().equals("workgroups")) {
            return deleteWorkgroup(node);
        }

        return true;
    }


    /**
     * Create a personal agenda for the user.
     */
    private boolean createUser(MMObjectNode user) {
        MMBase mmb = user.getBuilder().getMMBase();
        String username = user.getStringValue("username");
        MMObjectBuilder agendas = mmb.getBuilder("agendas");
        InsRel insrel = mmb.getInsRel();
        int related = mmb.getRelDef().getNumberByName("related");

        MMObjectNode agenda = agendas.getNewNode(username);
        agenda.setValue("name", "Persoonlijke Agenda van " + username);
        agendas.insert(username, agenda);
        MMObjectNode relation = insrel.getNewNode(username);
        relation.setValue("snumber", user.getNumber());
        relation.setValue("dnumber", agenda.getNumber());
        relation.setValue("rnumber", related);
        insrel.insert(username, relation);

        return true;
    }


    /**
     * Create a class agenda.
     */
    private boolean createClass(MMObjectNode cls) {
        MMBase mmb = cls.getBuilder().getMMBase();
        String username = "system";
        MMObjectBuilder agendas = mmb.getBuilder("agendas");
        InsRel insrel = mmb.getInsRel();
        int related = mmb.getRelDef().getNumberByName("related");

        MMObjectNode agenda = agendas.getNewNode(username);
        agenda.setValue("name", "Agenda van klas '" + cls.getStringValue("name") + "'");
        agendas.insert(username, agenda);
        MMObjectNode relation = insrel.getNewNode(username);
        relation.setValue("snumber", cls.getNumber());
        relation.setValue("dnumber", agenda.getNumber());
        relation.setValue("rnumber", related);
        insrel.insert(username, relation);

        return true;
    }

    /**
     * Create a workgroup agenda.
     */
    private boolean createWorkgroup(MMObjectNode workgroup) {
        MMBase mmb = workgroup.getBuilder().getMMBase();
        String username = "system";
        MMObjectBuilder agendas = mmb.getBuilder("agendas");
        InsRel insrel = mmb.getInsRel();
        int related = mmb.getRelDef().getNumberByName("related");

        MMObjectNode agenda = agendas.getNewNode(username);
        agenda.setValue("name", "Agenda van werkgroep '" + workgroup.getStringValue("name") + "'");
        agendas.insert(username, agenda);
        MMObjectNode relation = insrel.getNewNode(username);
        relation.setValue("snumber", workgroup.getNumber());
        relation.setValue("dnumber", agenda.getNumber());
        relation.setValue("rnumber", related);
        insrel.insert(username, relation);

        return true;
    }

    private void deleteAgenda(MMObjectNode agenda) {
        Vector items = agenda.getRelatedNodes("items", "related", RelationStep.DIRECTIONS_DESTINATION);

        // Iterate the items , to remove them all
        for (int j=0; j<items.size(); j++) {
            MMObjectNode item = (MMObjectNode)items.get(j);

            // Remove the invitations from this item to other people
            Enumeration irels = item.getRelations("invitationrel");
            while (irels.hasMoreElements()) {
                MMObjectNode irel = (MMObjectNode)irels.nextElement();
                irel.getBuilder().removeNode(irel);
            }
  
            item.getBuilder().removeRelations(item);
            item.getBuilder().removeNode(item);
        }
        agenda.getBuilder().removeRelations(agenda);
        agenda.getBuilder().removeNode(agenda);
    }

    private boolean deleteUser(MMObjectNode user) {
        Vector agendas = user.getRelatedNodes("agendas", "related", RelationStep.DIRECTIONS_DESTINATION);

        // Iterate the agendas, to remove them all
        for (int i=0; i<agendas.size(); i++) {
            deleteAgenda((MMObjectNode)agendas.get(i));
        }

        // Delete al invitations to this user
        Enumeration irels = user.getRelations("invitationrel");
        while (irels.hasMoreElements()) {
            MMObjectNode irel = (MMObjectNode)irels.nextElement();
            irel.getBuilder().removeNode(irel);
        }
        
        return true;
    }

    private boolean deleteClass(MMObjectNode cls) {
        Vector agendas = cls.getRelatedNodes("agendas", "related", RelationStep.DIRECTIONS_DESTINATION);

        // Iterate the agendas, to remove them all
        for (int i=0; i<agendas.size(); i++) {
            deleteAgenda((MMObjectNode)agendas.get(i));
        }

        return true;
    }

    private boolean deleteWorkgroup(MMObjectNode workgroup) {
        Vector agendas = workgroup.getRelatedNodes("agendas", "related", RelationStep.DIRECTIONS_DESTINATION);

        // Iterate the agendas, to remove them all
        for (int i=0; i<agendas.size(); i++) {
            deleteAgenda((MMObjectNode)agendas.get(i));
        }
        
        return true;
    }
}
