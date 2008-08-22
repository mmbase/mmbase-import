package nl.didactor.component.news;

import nl.didactor.component.Component;
import nl.didactor.component.core.*;
import org.mmbase.bridge.Cloud;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.storage.search.implementation.NodeSearchQuery;
import java.util.Map;
import nl.didactor.component.portalpages.DidactorPortalPages;

/**
 * @javadoc
 */
public class DidactorNews extends Component {
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
        return "news";
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
        int posrel = reldef.getNumberByName("posrel");
        int newsnodes = typedef.getIntValue("newsnodes");
        int simplecontents = typedef.getIntValue("simplecontents");
        int images = typedef.getIntValue("images");
        int roles = typedef.getIntValue("roles");
        int educations = typedef.getIntValue("educations");
        int newscontainersnb = typedef.getIntValue("newscontainers");

        MMObjectBuilder newscontainers = mmb.getBuilder("newscontainers");
        try{
            if(newscontainers.count(new NodeSearchQuery(newscontainers)) < 1){
                MMObjectNode newscontainer = newscontainers.getNewNode(username);
                newscontainer.setValue("name", "rootcontainer");
                newscontainers.insert(username, newscontainer);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if(!typerel.contains(newscontainersnb,newsnodes,related)){
            MMObjectNode relation = typerel.getNewNode(username);
            relation.setValue("snumber", newscontainersnb);
            relation.setValue("dnumber", newsnodes);
            relation.setValue("rnumber", related);
            typerel.insert(username, relation);
        }

        if(!typerel.contains(newsnodes,simplecontents,related)){
            MMObjectNode relation = typerel.getNewNode(username);
            relation.setValue("snumber", newsnodes);
            relation.setValue("dnumber", simplecontents);
            relation.setValue("rnumber", related);
            typerel.insert(username, relation);
        }

        if(!typerel.contains(simplecontents,images,posrel)){
            MMObjectNode relation = typerel.getNewNode(username);
            relation.setValue("snumber", simplecontents);
            relation.setValue("dnumber", images);
            relation.setValue("rnumber", posrel);
            typerel.insert(username, relation);
        }

        if(!typerel.contains(newsnodes,roles,related)){
            MMObjectNode relation = typerel.getNewNode(username);
            relation.setValue("snumber", newsnodes);
            relation.setValue("dnumber", roles);
            relation.setValue("rnumber", related);
            typerel.insert(username, relation);
        }

        if(!typerel.contains(newsnodes,educations,related)){
            MMObjectNode relation = typerel.getNewNode(username);
            relation.setValue("snumber", newsnodes);
            relation.setValue("dnumber", educations);
            relation.setValue("rnumber", related);
            typerel.insert(username, relation);
        }

    }

}
