import org.mmbase.bridge.*;
import java.util.*;
/**
 * @author aldo babucci
 * @version $Id: ImportRelations.java,v 1.1.1.1 2004-07-08 10:32:15 keesj Exp $
 */
public class ImportPosrel extends AbstractImport{
    public ImportPosrel(){
   	super();
    }
    
    public void doImport(){
   	String OLDITEM = "insrel";
        String NEWITEM = "relation";
        NodeList oldItemList = null;
        int counter = 0;
	
	System.out.println("--- START convert "+OLDITEM+" to "+NEWITEM+" ---------");
	oldItemList = oldCloud.getNodeManager(OLDITEM).getList(null,null,null);
	System.out.println("Found: "+oldItemList.size()+" elements");
	for (int x = 0 ; x < oldItemList.size() ; x++){
	    Relation oldItemNode = cloud.getRelation(x);
	    int snumber = oldItemNode.getIntValue("snumber");
	    int dnumber = oldItemNode.getIntValue("dnumber");
	    int rnumber = oldItemNode.getIntValue("rnumber");
	    
	    //if the relation was not imported
	    if (syncNode.isImported(IMPORT_SOURCE_NAME,oldItemNode.getNumber())==-1){
		int newSnumber = syncNode.isImported(IMPORT_SOURCE_NAME, snumber);
		int newDnumber = syncNode.isImported(IMPORT_SOURCE_NAME, dnumber);
		//but both nodes are imported
		if(newSnumber != -1 && newDnumber != -1){
		    Node newSourceNode = newCloud.getNode(newSnumber);
		    Node newDestinatoinNode = newCloud.getNode(newDnumber);
		    RelationManager relM = newCloud.getRelationManager(newSourceNode,newDestinatoinNode,oldItemNode.getRole());
		    /**
		    try{
			Relation rel = relM.createRelation(newSourceNode,newDestinatoinNode);
			rel.setIntValue("dir", dir);
			rel.commit();
			syncNode.add(IMPORT_SOURCE_NAME,oldItemNode.getNumber(),rel.getNumber());
			counter++;
			System.out.println("created a related relation between node {"+newSnumber+"} and node {"+newDnumber+"}.");
		    } catch (Exception e){
			System.out.println("error to create related relation between node {"+newSnumber+"} and node {"+newDnumber+"}...skipping");
		    }
			**/
		}
		
	    } else {
		System.out.println(""+NEWITEM+" already converted.");
	    }
	}
	System.out.println("Converted "+counter+" relations.");
	System.out.println("--- END convert "+OLDITEM+" to "+NEWITEM+" -----------");
    }
}
