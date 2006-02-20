package nl.didactor.component.portalpages;

import nl.didactor.component.Component;
import nl.didactor.component.core.*;
import org.mmbase.bridge.Cloud;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.storage.search.implementation.NodeSearchQuery;
import java.util.Map;

public class DidactorPortalPages extends Component {
    /**
     * Returns the version of the component
     */
    public String getVersion() {
        return "0.1";
    } /**
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
     * Permission framework: indicate whether or not a given operation may be done, with the
     * given arguments. The return value is a list of 2 booleans; the first boolean indicates
     * whether or not the operation is allowed, the second boolean indicates whether or not
     * this result may be cached.
     */
    public boolean[] may (String operation, Cloud cloud, Map context, String[] arguments) {
        return new boolean[]{true, true};
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
    	try{
        initRelations();
      } catch (Exception ex) {
         ex.printStackTrace();
      }
    }
      
    public void initRelations() {
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
	         ex.printStackTrace();
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
    
    /**
     * 
     */
    /*
    private boolean createClass(MMObjectNode cls) {       
        MMBase mmb = cls.getBuilder().getMMBase();
        
        String classname = cls.getStringValue("name");
        String username = "system";
        
        MMObjectBuilder chatchannels = mmb.getBuilder("chatchannels");
        InsRel insrel = mmb.getInsRel();
        int related = mmb.getRelDef().getNumberByName("related");

        MMObjectNode chatchannel = chatchannels.getNewNode(username);
		    String chatchannelName = classname.replaceAll(" ","-").toLowerCase();
		
        chatchannel.setValue("name", chatchannelName);
        chatchannels.insert(username, chatchannel);
        
        MMObjectNode relation = insrel.getNewNode(username);
        
        relation.setValue("snumber", cls.getNumber());
        relation.setValue("dnumber", chatchannel.getNumber());
        relation.setValue("rnumber", related);
        
        insrel.insert(username, relation);

        return true;
    }
   */
}
