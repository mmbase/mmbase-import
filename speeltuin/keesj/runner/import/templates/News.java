
import org.mmbase.bridge.*;
import java.util.*;

public class Convert extends AbstractImport{
	public Convert(){
		super();
	}
	
	 public void doImport(){
              String OLDITEM = "news";
              String NEWITEM = "news";
		NodeList oldItemList = null;
int counter = 0;

     System.out.println("--- START convert "+OLDITEM+" to "+NEWITEM+" ---------");
      oldItemList = oldCloud.getNodeManager(OLDITEM).getList(null,null,null);
      System.out.println("Found: "+oldItemList.size()+" elements");
      for (int x = 0 ; x < oldItemList.size()  ; x++){
         Node oldItemNode = oldItemList.getNode(x);

         if (syncNode.isImported(IMPORT_SOURCE_NAME,oldItemNode.getNumber())==-1){
            System.out.println("create a "+NEWITEM);
            Node newItemNode   = newCloud.getNodeManager(NEWITEM).createNode();
	        FieldIterator  iter = newItemNode.getNodeManager().getFields(NodeManager.ORDER_CREATE).fieldIterator();
		while ( iter.hasNext() ) {
			Field field= iter.nextField();
			String fieldName = field.getName();
			if ( fieldName.equals("number") || fieldName.equals("otype") || fieldName.equals("owner")){

			} else {

			   if (field.getType() == Field.TYPE_BYTE){
			      newItemNode.setByteValue(fieldName,oldItemNode.getByteValue(fieldName));
			   } else {
				newItemNode.setStringValue(fieldName,oldItemNode.getStringValue(fieldName));
			        System.err.println (field.getName() +":"+ oldItemNode.getStringValue(fieldName));
			   }
			};
		}
		newItemNode.commit();
		newItemNode.setContext("import");
	
            syncNode.add(IMPORT_SOURCE_NAME,oldItemNode.getNumber(),newItemNode.getNumber());
            counter++;
         }else{
            System.out.println(""+NEWITEM+" with name already converted.");
         }
      }
      System.out.println("Converted "+counter+" items.");
      System.out.println("--- END convert "+OLDITEM+" to "+NEWITEM+" -----------");

		

        }

	public static void main(String[] argv){
		new Convert().doImport();
	}
}
