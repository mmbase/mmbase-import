package nl.leocms.content;

import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.util.logging.*;
import com.finalist.mmbase.util.CloudFactory;
import nl.leocms.util.*;
import nl.leocms.authorization.AuthorizationHelper;
import nl.leocms.authorization.UserRole;
import nl.leocms.authorization.Roles;

public class UpdateUnusedElements implements Runnable {

   private static final Logger log = Logging.getLoggerInstance(UpdateUnusedElements.class);
   private static final ServerUtil su = new ServerUtil();

   ContentHelper ch;
   ApplicationHelper ap;
   Cloud cloud;

	/*
	* Returns the list of unused objects for a rubriek
	*/
	public ArrayList getUnusedItemsForRubriek(String rubriekNumber) {
		ArrayList alUnusedItems = new ArrayList();

		NodeList nlElements = cloud.getList(rubriekNumber,"rubriek,creatierubriek,contentelement","contentelement.number",null,null,null,null,false);
		for (int k = 0; k < nlElements.size(); k++){
      String node =  nlElements.getNode(k).getStringValue("contentelement.number");
			if (ch.usedInItems(node)==null){
        log.debug(cloud.getNode(node).getNodeManager().getName() + " " + node + " belongs to rubriek " + rubriekNumber + " and is not used");
				alUnusedItems.add(node);
			}
		}
		log.info("found " + alUnusedItems.size() + " unused items for rubriek " + rubriekNumber);
		return alUnusedItems;
	}

	/*
	* Returns a HashMap with for each rubriek an ArrayList of unused objects
	*/
	public HashMap getUnusedItemsForAllRubriek() {
		HashMap hmUnusedItems = new HashMap();
		NodeList nlRubrieks = cloud.getNodeManager("rubriek").getList(null,null,null);
		for(int j = 0; j < nlRubrieks.size(); j++){
			String rubriekNumber = nlRubrieks.getNode(j).getStringValue("number");
			hmUnusedItems.put(rubriekNumber,getUnusedItemsForRubriek(rubriekNumber));
		}
		return hmUnusedItems;
	}

  
  /*
	* Deletes all unused objects of nodeType i.e. objects that do not have a parent
	*/
	public void deleteUnusedObjects(String nodeType) {
    
    ArrayList cTypes = ap.getContentTypes(true);
		NodeList nl = cloud.getNodeManager(nodeType).getList(null,null,null);

		for(Iterator it = nl.iterator(); it.hasNext();){
      Node node = (Node) it.next();
      NodeManager thisNodeManager = node.getNodeManager();
      
      int iParents = 0;
      for(int ct=0; ct < cTypes.size() && iParents==0; ct++) {
         String relatedType = (String) cTypes.get(ct);
         if (thisNodeManager.getAllowedRelations(relatedType, null, "SOURCE").size() > 0) {
           iParents += node.getRelatedNodes(relatedType,null,"SOURCE").size();
         }
      }
      if(iParents==0) {
        log.info("deleting " + nodeType + " " + node.getStringValue(ch.getTitleField(thisNodeManager)) + " (" + node.getNumber() + ")"); 
        node.delete(true);
      }
		}
	}
  
  /*
	* Checks whether each object has at most 3 archived copies, and deletes all other copies
	*/
	public void cleanUpArchive() {
    
    HashSet set = new HashSet();
    
    String sMax =  (new DateUtil()).getObjectNumber(cloud, (new Date()));
    
    int batchSize = 10000;
    int batchStart = (new Integer(sMax)).intValue()-batchSize;
    int copiesDeleted = 0;
    while(batchStart+batchSize > 0) {
      NodeList nl = cloud.getList("","archief",
          "archief.original_node,archief.number",
          "archief.number >= '" + batchStart + "' AND archief.number < '" + (batchStart+batchSize) + "'",
          "archief.number","DOWN",null,false);
    
      log.debug("checking batch [" + batchStart + "," + (batchStart+batchSize) + ">, number of archief nodes in batch is " + nl.size()); 
      for(Iterator it = nl.iterator(); it.hasNext();) {
        Node node = (Node) it.next();
        if(set.contains(node.getStringValue("archief.original_node"))) {
          log.debug("deleting copy of node " + node.getStringValue("archief.original_node"));
          cloud.getNode((String) node.getStringValue("archief.number")).delete();
          copiesDeleted++;
        } else {
          set.add(node.getStringValue("archief.original_node"));
        }
      }
      batchStart -= batchSize;
    }
    log.info("cleaned up archive an deleted " + copiesDeleted + " copies, total size of archive is now " + set.size());
	}
  
	/*
	* For all users add the list of unused objects to users.unused_items field
	*/
   public void getUnusedItems() {

      ArrayList containers = ap.getContainerTypes();
      for (int i = 0; i < containers.size(); i++){
        deleteUnusedObjects((String) containers.get(i));
      }

      AuthorizationHelper authHelper = new AuthorizationHelper(cloud);
      HashMap hmUnusedItemsForAllRubriek = getUnusedItemsForAllRubriek();
      
      NodeList nlUsers = cloud.getNodeManager("users").getList(null,null,null);
      for (int i = 0; i < nlUsers.size(); i++){
        ArrayList alUnusedItems = new ArrayList();
        String account = nlUsers.getNode(i).getStringValue("account");
        for (Iterator it = hmUnusedItemsForAllRubriek.keySet().iterator(); it.hasNext(); ) {
          String rubriekNumber = (String) it.next();
          UserRole userRole = authHelper.getRoleForUser(authHelper.getUserNode(account), cloud.getNode(rubriekNumber));
          if (userRole.getRol() >= Roles.SCHRIJVER) {
            alUnusedItems.addAll((ArrayList) hmUnusedItemsForAllRubriek.get(rubriekNumber));
          }
        }
        if(alUnusedItems.size()>0) {
          log.info("adding " + alUnusedItems.size() + " unused items for user " + account);
          String user_number = nlUsers.getNode(i).getStringValue("number");
          Node nUser = cloud.getNode(user_number);
          nUser.setStringValue("unused_items",alUnusedItems.toString().substring(1,alUnusedItems.toString().length()-1));
          nUser.commit();
        }
      }
      cleanUpArchive();
   }

   private Thread getKicker(){
    Thread  kicker = Thread.currentThread();
    if(kicker.getName().indexOf("UpdateUsedElementsThread")==-1) {
       kicker.setName("UpdateUsedElementsThread / " + su.getDateTimeString());
       kicker.setPriority(Thread.MIN_PRIORITY+1); // *** does this help ?? ***
    }
    return kicker;
   }

   public void run () {
      this.cloud = CloudFactory.getCloud();
      this.ch = new ContentHelper(cloud);
      this.ap = new ApplicationHelper(cloud);
      
      Thread kicker = getKicker();
      log.info("run(): " + kicker + su.jvmSize());
      getUnusedItems();
		  log.info("done" + su.jvmSize());
   }
}
