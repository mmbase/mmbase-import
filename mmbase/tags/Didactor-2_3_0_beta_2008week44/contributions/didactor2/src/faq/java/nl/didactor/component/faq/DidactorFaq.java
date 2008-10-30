package nl.didactor.component.faq;

import nl.didactor.component.Component;
import nl.didactor.component.core.*;
import org.mmbase.bridge.Cloud;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.storage.search.implementation.NodeSearchQuery;
import java.util.Map;
import nl.didactor.component.portalpages.DidactorPortalPages;

public class DidactorFaq extends Component {
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
        return "faq";
    }

    /**
     * Returns an array of components this component depends on.
     */
    public Component[] dependsOn() {
        Component[] components = new Component[2];
        components[0] = new DidactorCore();
        components[1] = new DidactorPortalPages();
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
        initRelations();
    }

    public void initRelations() {
        org.mmbase.module.core.MMBase mmb = (org.mmbase.module.core.MMBase) org.mmbase.module.Module.getModule("mmbaseroot");
        String username = "system";
        RelDef reldef = mmb.getRelDef();
        TypeRel typerel = mmb.getTypeRel();
        TypeDef typedef = mmb.getTypeDef();
        int related = reldef.getNumberByName("related");
        int faqnodes = typedef.getIntValue("faqnodes");
        int faqitems = typedef.getIntValue("faqitems");
        int roles = typedef.getIntValue("roles");
        int educations = typedef.getIntValue("educations");
        int faqcontainersnb = typedef.getIntValue("faqcontainers");

        MMObjectBuilder faqcontainers = mmb.getBuilder("faqcontainers");
        try{
            if(faqcontainers.count(new NodeSearchQuery(faqcontainers)) < 1){
                MMObjectNode faqcontainer = faqcontainers.getNewNode(username);
                faqcontainer.setValue("name", "rootcontainer");
                faqcontainers.insert(username, faqcontainer);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if(!typerel.contains(faqcontainersnb,faqnodes,related)){
            MMObjectNode relation = typerel.getNewNode(username);
            relation.setValue("snumber", faqcontainersnb);
            relation.setValue("dnumber", faqnodes);
            relation.setValue("rnumber", related);
            typerel.insert(username, relation);
        }

        if(!typerel.contains(faqnodes,faqitems,related)){
            MMObjectNode relation = typerel.getNewNode(username);
            relation.setValue("snumber", faqnodes);
            relation.setValue("dnumber", faqitems);
            relation.setValue("rnumber", related);
            typerel.insert(username, relation);
        }

        if(!typerel.contains(faqnodes,roles,related)){
            MMObjectNode relation = typerel.getNewNode(username);
            relation.setValue("snumber", faqnodes);
            relation.setValue("dnumber", roles);
            relation.setValue("rnumber", related);
            typerel.insert(username, relation);
        }

        if(!typerel.contains(faqnodes,educations,related)){
            MMObjectNode relation = typerel.getNewNode(username);
            relation.setValue("snumber", faqnodes);
            relation.setValue("dnumber", educations);
            relation.setValue("rnumber", related);
            typerel.insert(username, relation);
        }

    }


}
