import org.mmbase.bridge.*;
import java.util.*;
/**
 * @author aldo babucci
 * @version $Id: Relations.java,v 1.1 2004-07-08 10:56:37 keesj Exp $
 */
public class Convert extends AbstractImport{
    public Convert(){
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
	    try {
	    Relation oldItemNode = oldCloud.getRelation(oldItemList.getNode(x).getNumber());
	    int snumber = oldItemNode.getIntValue("snumber");
	    int dnumber = oldItemNode.getIntValue("dnumber");
	    int rnumber = oldItemNode.getIntValue("rnumber");
	    
	    //if the relation was not imported
	    if (syncNode.isImported(IMPORT_SOURCE_NAME,oldItemNode.getNumber())==-1){
		int newSnumber = syncNode.isImported(IMPORT_SOURCE_NAME, snumber);
		int newDnumber = syncNode.isImported(IMPORT_SOURCE_NAME, dnumber);
		String role = oldCloud.getRelationManager(rnumber).getStringValue("sname");
		//but both nodes are imported
		if(newSnumber != -1 && newDnumber != -1){
		    Node newSourceNode = newCloud.getNode(newSnumber);
		    Node newDestinatoinNode = newCloud.getNode(newDnumber);
                   
		    RelationManager relM = null;
		
		    try {
		    relM = newCloud.getRelationManager(newSourceNode.getNodeManager().getName(),newDestinatoinNode.getNodeManager().getName(),role);
		    } catch (Throwable e){
			if (role.equals("related")){
			        System.err.println("try posrel hack");
			        role = "posrel";
		    		relM = newCloud.getRelationManager(newSourceNode.getNodeManager().getName(),newDestinatoinNode.getNodeManager().getName(),"posrel");
			        System.err.println("try posrel suc6");
			}
		    }
		
		
		    try{
			Relation rel = relM.createRelation(newSourceNode,newDestinatoinNode);
               FieldIterator  iter = rel.getNodeManager().getFields(NodeManager.ORDER_CREATE).fieldIterator();
                while ( iter.hasNext() ) {
                        Field field= iter.nextField();
                        String fieldName = field.getName();
                        if ( fieldName.equals("number") || fieldName.equals("otype") || fieldName.equals("owner") || fieldName.equals("snumber") || fieldName.equals("dnumber") || fieldName.equals("rnumber") || fieldName.equals("dir")){

                        } else {

                           if (field.getType() == Field.TYPE_BYTE){
                              rel.setByteValue(fieldName,oldItemNode.getByteValue(fieldName));
                           } else {
                                rel.setStringValue(fieldName,oldItemNode.getStringValue(fieldName));
                                System.err.println (field.getName() +":"+ oldItemNode.getStringValue(fieldName));
                           }
                        };
                }

			rel.commit();
			syncNode.add(IMPORT_SOURCE_NAME,oldItemNode.getNumber(),rel.getNumber());
			counter++;
			System.out.println("created a related relation between node {"+newSnumber+"} and node {"+newDnumber+"}.");
		    } catch (Exception e){
			System.out.println("error to create related relation between node {"+newSnumber+"} and node {"+newDnumber+"}...skipping");
		    }
		}
		
	    } else {
		System.out.println(""+NEWITEM+" already converted.");
	         try {
			Node node = newCloud.getNode(syncNode.isImported(IMPORT_SOURCE_NAME,oldItemNode.getNumber()));
		    } catch (Throwable t){
			 syncNode.delete(IMPORT_SOURCE_NAME,oldItemNode.getNumber());
			 System.out.println(NEWITEM+" with FIX IMPORT");
		    }

	    }
	    } catch (RuntimeException e){
		System.out.println(""+NEWITEM+" runtime exception not converted.");
            };
	}
	System.out.println("Converted "+counter+" relations.");
	System.out.println("--- END convert "+OLDITEM+" to "+NEWITEM+" -----------");
    }

    public static void main(String [] argv){
		new Convert().doImport();
    }
}
