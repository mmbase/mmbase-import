package nl.didactor.component.portalpages;

import nl.didactor.component.Component;
import nl.didactor.component.core.*;
import org.mmbase.bridge.Cloud;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.storage.search.implementation.NodeSearchQuery;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import java.util.Map;

/**
 * @javadoc
 */

public class DidactorPortalPages extends Component {
    private static final Logger log = Logging.getLoggerInstance(DidactorPortalPages.class);

    /**
     * Returns the version of the component
     */

    public String getVersion() {
        return "0.1";
    }
    /**
     * Returns the name of the component
     */
    public String getName() {
        return "portalpages";
    }

    /**
     * Returns an array of components this component depends on.
     */
    public Component[] dependsOn() {
        Component[] components = new Component[0];
        return components;
    }

    /**
     * This method is called when a new object is added to Didactor. If the component
     * needs to insert objects for this object, it can do so.
     */
    public boolean notifyCreate(MMObjectNode node) {
        /*
        if (node.getBuilder().getTableName().equals("classes"))
            return createClass(node);
        */
        return true;
    }
    public void init() {
        super.init();
        try{
            initRelations();
        } catch (Exception ex) {
            log.warn(ex);
        }
    }

    /**
     * I think this method is a giant WTF, and what it tries to do already happens in a few lines
     * in DidactorPortalPages.xml.

     * WTF WTF WTF TODO TODO
     */
    protected void initRelations() {
        org.mmbase.module.core.MMBase mmb = (org.mmbase.module.core.MMBase) org.mmbase.module.Module.getModule("mmbaseroot");
        String username = "system";
        RelDef reldef = mmb.getRelDef();
        TypeRel typerel = mmb.getTypeRel();
        TypeDef typedef = mmb.getTypeDef();
        int related = reldef.getNumberByName("related");
        int posrel = reldef.getNumberByName("posrel");
        int portalpagesnodes = typedef.getIntValue("portalpagesnodes");
        int simplecontents = typedef.getIntValue("simplecontents");
        int portalpagescontainers = typedef.getIntValue("portalpagescontainers");
        int images = typedef.getIntValue("images");


        MMObjectBuilder portalcontainers = mmb.getBuilder("portalpagescontainers");
        try{
            if(portalcontainers.count(new NodeSearchQuery(portalcontainers)) < 1){
                MMObjectNode portalcontainer = portalcontainers.getNewNode(username);
                portalcontainer.setValue("name", "rootcontainer");
                portalcontainers.insert(username, portalcontainer);
            }
        } catch (Exception ex) {
            log.warn(ex);
        }

        if(!typerel.contains(portalpagescontainers,portalpagesnodes,related)){
            MMObjectNode relation = typerel.getNewNode(username);
            relation.setValue("snumber", portalpagescontainers);
            relation.setValue("dnumber", portalpagesnodes);
            relation.setValue("rnumber", related);
            typerel.insert(username, relation);
        }

        if(!typerel.contains(portalpagesnodes,simplecontents,related)){
            MMObjectNode relation = typerel.getNewNode(username);
            relation.setValue("snumber", portalpagesnodes);
            relation.setValue("dnumber", simplecontents);
            relation.setValue("rnumber", related);
            typerel.insert(username, relation);
        }

        if(reldef.getNumberByName("childppnn")< 0){
            MMObjectNode relation = reldef.getNewNode(username);
            relation.setValue("sname", "childppnn");
            relation.setValue("dname", "parentppnn");
            relation.setValue("dir", 1);
            relation.setValue("sguiname", "Child");
            relation.setValue("dguiname", "Parent");
            relation.setValue("builder", mmb.getTypeDef().getIntValue("insrel"));
            reldef.insert(username, relation);
        }

        int child = reldef.getNumberByName("childppnn");
        if(!typerel.contains(portalpagesnodes,portalpagesnodes,child)){
            MMObjectNode relation = typerel.getNewNode(username);
            relation.setValue("snumber", portalpagesnodes);
            relation.setValue("dnumber", portalpagesnodes);
            relation.setValue("rnumber", child);
            typerel.insert(username, relation);
        }
        if(!typerel.contains(simplecontents,images,posrel)){
            MMObjectNode relation = typerel.getNewNode(username);
            relation.setValue("snumber", simplecontents);
            relation.setValue("dnumber", images);
            relation.setValue("rnumber", posrel);
            typerel.insert(username, relation);
        }

    }

}
