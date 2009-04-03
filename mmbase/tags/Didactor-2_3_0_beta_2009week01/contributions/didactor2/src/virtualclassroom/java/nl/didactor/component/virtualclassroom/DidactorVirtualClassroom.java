package nl.didactor.component.virtualclassroom;

import nl.didactor.component.Component;
import nl.didactor.component.core.*;
import org.mmbase.bridge.Cloud;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.storage.search.implementation.BasicFieldValueConstraint;
import org.mmbase.storage.search.implementation.NodeSearchQuery;
import org.mmbase.storage.search.*;
import org.mmbase.bridge.*;
import java.util.*;
import nl.didactor.component.workspace.DidactorWorkspace;

/**
 * This class implements the Component interface, to make sure that
 * all needed relations and values are available to the component.
 * @author Sasa Vender s.vender@levi9.com
 *
 */

public class DidactorVirtualClassroom extends Component {
    /**
     * Returns the version of the component
     */
    public String getVersion() {
        return "0.1";
    } /**
       * Returns the name of the component
       */
    public String getName() {
        return "virtualclassroom";
    }

    /**
     * Returns an array of components this component depends on.
     */
    public Component[] dependsOn() {
        Component[] components = new Component[2];
        components[0] = new DidactorCore();
        components[1] = new DidactorWorkspace();
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

    /**
     * Initialize component.
     */
    public void init() {
        super.init();
        initRelations();
    }

    /**
     * Initialize relations that component needs.
     */
    public void initRelations() {
        MMBase mmb = (MMBase) org.mmbase.module.Module.getModule("mmbaseroot");
        String username = "system";
        String admin = "admin";
        RelDef reldef = mmb.getRelDef();
        TypeRel typerel = mmb.getTypeRel();
        TypeDef typedef = mmb.getTypeDef();
        int related = reldef.getNumberByName("related");
        int posrel = reldef.getNumberByName("posrel");
        int educations = typedef.getIntValue("educations");
        int virtualclassroomsessions = typedef.getIntValue("virtualclassroomsessions");
        int videotapes = typedef.getIntValue("videotapes");
        int attachments = typedef.getIntValue("attachments");
        int editcontexts = typedef.getIntValue("editcontexts");

        //System.out.println(">>>>>>"+educations+"/"+virtualclassroomsessions+"/"+videotapes+"/"+attachments);

        MMObjectBuilder editcontextsbuilder = mmb.getBuilder("editcontexts");
        try{
            NodeSearchQuery nsQuery = new NodeSearchQuery(editcontextsbuilder);
            StepField nameField = nsQuery.getField(editcontextsbuilder.getField("name"));
            BasicFieldValueConstraint constraint = new BasicFieldValueConstraint(nameField, "virtualclassroom");
            nsQuery.setConstraint(constraint);
            List editcontextList = editcontextsbuilder.getNodes(nsQuery);
            if(editcontextList.size()<1){

            	//create entry for virtualclassroom in editcontext
                MMObjectNode editcontextsnode = editcontextsbuilder.getNewNode(admin);
                editcontextsnode.setValue("name", "virtualclassroom");
                editcontextsnode.setValue("otype", editcontexts);
                editcontextsbuilder.insert(admin, editcontextsnode);

                //find number of virtualclassroom editcontext
                NodeSearchQuery eQuery = new NodeSearchQuery(editcontextsbuilder);
                StepField eNameField = eQuery.getField(editcontextsbuilder.getField("name"));
                BasicFieldValueConstraint eConstraint = new BasicFieldValueConstraint(eNameField, "virtualclassroom");
                eQuery.setConstraint(eConstraint);
                editcontextList = editcontextsbuilder.getNodes(eQuery);
                if (editcontextList.size()>0){
                    editcontextsnode  = (MMObjectNode) editcontextList.get(0);
            	    int virtualclassroomNb = editcontextsnode.getNumber();

                    //find number of systemadministrator role
                    MMObjectBuilder rolesbuilder = mmb.getBuilder("roles");
                    NodeSearchQuery rQuery = new NodeSearchQuery(rolesbuilder);
                    StepField rNameField = rQuery.getField(rolesbuilder.getField("name"));
                    BasicFieldValueConstraint rConstraint = new BasicFieldValueConstraint(rNameField, "systemadministrator");
                    rQuery.setConstraint(rConstraint);
                    List roleList = rolesbuilder.getNodes(rQuery);
                    if (roleList.size()>0){
            	        MMObjectNode systAdmin  = (MMObjectNode) roleList.get(0);
            	        int systAdminNb = systAdmin.getNumber();

                        //crete relation from systemadministrator role to virtualclassrom editcontext
                        MMObjectBuilder posrelbuilder = mmb.getBuilder("posrel");
                        MMObjectNode relation = posrelbuilder.getNewNode(username);
                        relation.setValue("snumber", virtualclassroomNb);
                        relation.setValue("dnumber", systAdminNb);
                        relation.setValue("rnumber", posrel);
                        relation.setValue("pos", 3);
                        posrelbuilder.insert(username, relation);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if(!typerel.contains(educations,virtualclassroomsessions,related)){
            MMObjectNode relation = typerel.getNewNode(username);
            relation.setValue("snumber", educations);
            relation.setValue("dnumber", virtualclassroomsessions);
            relation.setValue("rnumber", related);
            typerel.insert(username, relation);
        }

        if(!typerel.contains(virtualclassroomsessions,videotapes,related)){
            MMObjectNode relation = typerel.getNewNode(username);
            relation.setValue("snumber", virtualclassroomsessions);
            relation.setValue("dnumber", videotapes);
            relation.setValue("rnumber", related);
            typerel.insert(username, relation);
        }

        if(!typerel.contains(virtualclassroomsessions,attachments,related)){
            MMObjectNode relation = typerel.getNewNode(username);
            relation.setValue("snumber", virtualclassroomsessions);
            relation.setValue("dnumber", attachments);
            relation.setValue("rnumber", related);
            typerel.insert(username, relation);
        }
    }
}
