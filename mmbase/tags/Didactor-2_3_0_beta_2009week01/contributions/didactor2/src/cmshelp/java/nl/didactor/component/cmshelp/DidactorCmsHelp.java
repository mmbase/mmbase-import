package nl.didactor.component.cmshelp;

import nl.didactor.component.Component;
import nl.didactor.component.core.*;
import org.mmbase.bridge.Cloud;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.storage.search.implementation.NodeSearchQuery;
import java.util.Map;
import nl.didactor.component.portalpages.DidactorPortalPages;

public class DidactorCmsHelp extends Component {
    /**
     * Returns the version of the component
     */
    public String getVersion() {
        return "0.1";
    } /**
     * Returns the name of the component
     */
    public String getName() {
        return "cmshelp";
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
	 * Of course, no matter what setting somebody wants to get,
	 * we say that it's  unknown since there isn't any settings
	 * in this chat.
	 */
    public String getSetting(String setting, Cloud cloud, Map context, String[] arguments) {
        throw new IllegalArgumentException("Unknown setting '" + setting + "'");
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
        int helpnodes = typedef.getIntValue("helpnodes");
        int simplecontents = typedef.getIntValue("simplecontents");
        int images = typedef.getIntValue("images");
        int roles = typedef.getIntValue("roles");
        int educations = typedef.getIntValue("educations");
        int helpcontainersnb = typedef.getIntValue("helpcontainers");

        MMObjectBuilder helpcontainers = mmb.getBuilder("helpcontainers");
        try{
		        if(helpcontainers.count(new NodeSearchQuery(helpcontainers)) < 1){
				        MMObjectNode helpcontainer = helpcontainers.getNewNode(username);
				        helpcontainer.setValue("name", "rootcontainer");
				        helpcontainers.insert(username, helpcontainer);
		        }
	      } catch (Exception ex) {
	         ex.printStackTrace();
	      }

        if(!typerel.contains(helpcontainersnb,helpnodes,related)){
		        MMObjectNode relation = typerel.getNewNode(username);
		        relation.setValue("snumber", helpcontainersnb);
		        relation.setValue("dnumber", helpnodes);
		        relation.setValue("rnumber", related);
		        typerel.insert(username, relation);
		    }

        if(!typerel.contains(helpnodes,simplecontents,related)){
		        MMObjectNode relation = typerel.getNewNode(username);
		        relation.setValue("snumber", helpnodes);
		        relation.setValue("dnumber", simplecontents);
		        relation.setValue("rnumber", related);
		        typerel.insert(username, relation);
		    }

		    if(reldef.getNumberByName("childhnn")< 0){
						MMObjectNode relation = reldef.getNewNode(username);
						relation.setValue("sname", "childhnn");
						relation.setValue("dname", "parenthnn");
						relation.setValue("dir", 1);
						relation.setValue("sguiname", "Child");
						relation.setValue("dguiname", "Parent");
						relation.setValue("builder", mmb.getTypeDef().getIntValue("insrel"));
						reldef.insert(username, relation);
				}

		    int child = reldef.getNumberByName("childhnn");
        if(!typerel.contains(helpnodes,helpnodes,child)){
		        MMObjectNode relation = typerel.getNewNode(username);
		        relation.setValue("snumber", helpnodes);
		        relation.setValue("dnumber", helpnodes);
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

        if(!typerel.contains(helpnodes,roles,related)){
		        MMObjectNode relation = typerel.getNewNode(username);
		        relation.setValue("snumber", helpnodes);
		        relation.setValue("dnumber", roles);
		        relation.setValue("rnumber", related);
		        typerel.insert(username, relation);
		    }

        if(!typerel.contains(helpnodes,educations,related)){
		        MMObjectNode relation = typerel.getNewNode(username);
		        relation.setValue("snumber", helpnodes);
		        relation.setValue("dnumber", educations);
		        relation.setValue("rnumber", related);
		        typerel.insert(username, relation);
		    }

    }

    /**
     *
     */

}
