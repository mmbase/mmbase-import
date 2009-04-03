package nl.didactor.component.versioning;

import nl.didactor.component.Component;
import nl.didactor.builders.DidactorBuilder;
import nl.didactor.component.core.*;
import nl.didactor.versioning.VersioningController;

import org.mmbase.bridge.*;
import org.mmbase.module.core.MMBase;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMObjectNode;

public class DidactorVersioning extends Component {
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
        return "versioning";
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
        String[] builders = new String[]{"flashpages","htmlpages","learnblocks","learnobjects","pages","questions","paragraphs","educations"};
        for (int i=0; i<builders.length; i++) {
            DidactorBuilder build = (DidactorBuilder)mmbase.getBuilder(builders[i]);
            build.registerPreCommitComponent(this, 10);
        }
    }

    public boolean preCommit(MMObjectNode node) {
        String builderName = node.getBuilder().getTableName();
        if (builderName.equals("paragraphs")) {
            preCommitParagraph(node);
        } else if (builderName.equals("urls")) {
            preCommitSimple(node);
        } else if (builderName.equals("images")) {
            preCommitSimple(node);
        } else if (builderName.equals("attachments")) {
            preCommitSimple(node);
        } else {
            // "flashpages","htmlpages","learnblocks","learnobjects","pages","questions","educations"
            preCommitLO(node); 
        }
        return true;
    }

    private void preCommitLO(MMObjectNode node) {
        Cloud cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase", "class", null);
        VersioningController.addLOVersion(cloud.getNode(node.getNumber()));
    }

    private void preCommitSimple(MMObjectNode node) {
        Cloud cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase", "class", null);
        VersioningController.addSimpleVersion(cloud.getNode(node.getNumber()));
    }

    private void preCommitParagraph(MMObjectNode node) {
        Cloud cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase", "class", null);
        Node originalNode = cloud.getNode(node.getNumber());
        NodeList learnobjects = learnobjects = originalNode.getRelatedNodes("learnobjects");
        for(int i=0;i<learnobjects.size();i++) {
          VersioningController.addLOVersion(learnobjects.getNode(i));
        }
        NodeList educations = originalNode.getRelatedNodes("educations");
        for(int i=0;i<educations.size();i++) {
          VersioningController.addLOVersion(educations.getNode(i));
        }
    }
}
