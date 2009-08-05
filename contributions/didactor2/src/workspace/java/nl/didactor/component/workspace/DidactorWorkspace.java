package nl.didactor.component.workspace;
import nl.didactor.builders.DidactorBuilder;
import nl.didactor.component.Component;
import nl.didactor.component.core.*;
import org.mmbase.bridge.Cloud;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.storage.search.RelationStep;
import org.mmbase.storage.search.SearchQueryException;
import org.mmbase.storage.search.implementation.NodeSearchQuery;
import org.mmbase.util.logging.*;

import java.util.*;

public class DidactorWorkspace extends Component {
    private static final Logger log = Logging.getLoggerInstance(DidactorWorkspace.class);
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
        return "workspace";
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
        
        // This is experimental code: 
        // See also applications/DidactorWorkspace.xml
        MMObjectBuilder chatlogs = mmbase.getBuilder("chatlogs");
        if (chatlogs != null) {
            //<relation from="folders"     to="chatlogs"    type="related" />
            TypeRel typeRel = mmbase.getTypeRel();
            RelDef  relDef = mmbase.getRelDef();
            int related = relDef.getNumberByName("related");
            MMObjectBuilder folders = mmbase.getBuilder("folders");
            MMObjectBuilder portfoliopermissions = mmbase.getBuilder("portfoliopermissions");
            if (!typeRel.contains(folders.getObjectType(), chatlogs.getObjectType(), related)) {
                log.info("No relation folders-related->chatlogs. Creating now");
                MMObjectNode n = typeRel.getNewNode("system");
                n.setValue("snumber", folders.getObjectType());
                n.setValue("dnumber", chatlogs.getObjectType());
                n.setValue("rnumber", related);
                n.setValue("max", -1);
                int id = typeRel.insert("system", n);
            }
        }
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
     * Returns an array of components this component depends on.
     * This should always contain at least one component: 'DidactorCore'
     */
    public Component[] dependsOn() {
        Component[] components = new Component[0];
        return components;
    }

    /**
     * This method is called when a new object is added to Didactor. If the component
     * needs to insert objects for this object, it can do so. 
     */
    public boolean postInsert(MMObjectNode node) {
        if (node.getBuilder().getTableName().equals("people")) {
            return createUser(node);
        }

        if (node.getBuilder().getTableName().equals("classes")) {
            return createClass(node);
        }

        if (node.getBuilder().getTableName().equals("workgroups")) {
            return createWorkgroup(node);
        }

        return true;
    }

    /**
     * This method is called when a new object is removed from Didactor.
     */
    public boolean preDelete(MMObjectNode node) {
        if (node.getBuilder().getTableName().equals("people")) {
            return deleteObject(node);
        }

        if (node.getBuilder().getTableName().equals("classes")) {
            return deleteObject(node);
        }

        if (node.getBuilder().getTableName().equals("workgroups")) {
            return deleteObject(node);
        }

        return true;
    }

    /**
     * Create a personal workspace for the user.
     */
    private boolean createUser(MMObjectNode user) {
        MMBase mmb = user.getBuilder().getMMBase();
        String username = user.getStringValue("username");
        MMObjectBuilder workspaces = mmb.getBuilder("workspaces");
        InsRel insrel = mmb.getInsRel();
        int related = mmb.getRelDef().getNumberByName("related");

        MMObjectNode workspace = workspaces.getNewNode(username);
        workspace.setValue("name", "Persoonlijke werkruimte van " + username);
        workspaces.insert(username, workspace);
        MMObjectNode relation = insrel.getNewNode(username);
        relation.setValue("snumber", user.getNumber());
        relation.setValue("dnumber", workspace.getNumber());
        relation.setValue("rnumber", related);
        insrel.insert(username, relation);

        return true;
    }

    /**
     * Create a class workspace.
     */
    private boolean createClass(MMObjectNode cls) {
        MMBase mmb = cls.getBuilder().getMMBase();
        String username = "system";
        MMObjectBuilder workspaces = mmb.getBuilder("workspaces");
        InsRel insrel = mmb.getInsRel();
        int related = mmb.getRelDef().getNumberByName("related");

        MMObjectNode workspace = workspaces.getNewNode(username);
        workspace.setValue("name", "Werkruimte van klas '" + cls.getStringValue("name") + "'");
        workspaces.insert(username, workspace);
        MMObjectNode relation = insrel.getNewNode(username);
        relation.setValue("snumber", cls.getNumber());
        relation.setValue("dnumber", workspace.getNumber());
        relation.setValue("rnumber", related);
        insrel.insert(username, relation);

        return true;
    }

    /**
     * Create a workgroup workspace.
     */
    private boolean createWorkgroup(MMObjectNode cls) {
        MMBase mmb = cls.getBuilder().getMMBase();
        String username = "system";
        MMObjectBuilder workspaces = mmb.getBuilder("workspaces");
        InsRel insrel = mmb.getInsRel();
        int related = mmb.getRelDef().getNumberByName("related");

        MMObjectNode workspace = workspaces.getNewNode(username);
        workspace.setValue("name", "Werkruimte van werkgroep '" + cls.getStringValue("name") + "'");
        workspaces.insert(username, workspace);
        MMObjectNode relation = insrel.getNewNode(username);
        relation.setValue("snumber", cls.getNumber());
        relation.setValue("dnumber", workspace.getNumber());
        relation.setValue("rnumber", related);
        insrel.insert(username, relation);

        return true;
    }

    /**
     * This method deletes the workspace for the user, class or workgroup
     */
    private boolean deleteObject(MMObjectNode object) {
        Vector workspaces = object.getRelatedNodes("workspaces", "related", RelationStep.DIRECTIONS_DESTINATION);

        // Iterate the workspaces, to remove them all
        for (int i=0; i<workspaces.size(); i++) {
            MMObjectNode workspace = (MMObjectNode)workspaces.get(i);
            Vector folders = workspace.getRelatedNodes("folders", "posrel", RelationStep.DIRECTIONS_DESTINATION);

            for (int j=0; j<folders.size(); j++) {
                MMObjectNode folder = (MMObjectNode)folders.get(j);

                // WARNING: NEED TO CHECK IF WE MAY DELETE ALL THESE!!
                String[] otypes = new String[]{"attachments", "chatlogs", "pages", "urls"};
                for (int k=0; k<otypes.length; k++) {
                    Vector objs = folder.getRelatedNodes(otypes[k], "related", RelationStep.DIRECTIONS_DESTINATION);

                    // Iterate the attachments etc., to remove them all
                    for (int l=0; l<objs.size(); l++) {
                        MMObjectNode obj = (MMObjectNode)objs.get(l);
                        obj.getBuilder().removeRelations(obj);
                        obj.getBuilder().removeNode(obj);
                    }
                }
                folder.getBuilder().removeRelations(folder);
                folder.getBuilder().removeNode(folder);
            }

            workspace.getBuilder().removeRelations(workspace);
            workspace.getBuilder().removeNode(workspace);
        }
        return true;
    }
}
