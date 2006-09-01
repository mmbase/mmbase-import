package nl.leocms.content;

import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.util.logging.*;
import com.finalist.mmbase.util.CloudFactory;
import nl.leocms.util.ContentHelper;
import nl.leocms.util.ServerUtil;
import nl.leocms.authorization.AuthorizationHelper;
import nl.leocms.authorization.UserRole;
import nl.leocms.authorization.Roles;

public class UpdateUnusedElements implements Runnable {

   private static final Logger log = Logging.getLoggerInstance(UpdateUnusedElements.class);
   private static final ServerUtil su = new ServerUtil();

	/*
	* Returns the list of unused objects for a rubriek
	*/
	public ArrayList getUnusedItemsForRubriek(Cloud cloud, String rubriekNumber) {
		ContentHelper ch = new ContentHelper(cloud);
		ArrayList alUnusedItems = new ArrayList();

		NodeList nlElements = cloud.getList(rubriekNumber,"rubriek,creatierubriek,contentelement","contentelement.number",null,null,null,null,false);
		for (int k = 0; k < nlElements.size(); k++){
			if (ch.usedInItems(nlElements.getNode(k).getStringValue("contentelement.number"))==null){
				alUnusedItems.add(nlElements.getNode(k).getStringValue("contentelement.number"));
			}
		}
		log.info("found " + alUnusedItems.size() + " unused items for rubriek " + rubriekNumber);
		return alUnusedItems;
	}

	/*
	* Returns a HashMap with for each rubriek an ArrayList of unused objects
	*/
	public HashMap getUnusedItemsForAllRubriek(Cloud cloud) {
		HashMap hmUnusedItems = new HashMap();
		NodeList nlRubrieks = cloud.getNodeManager("rubriek").getList(null,null,null);
		for(int j = 0; j < nlRubrieks.size(); j++){
			String rubriekNumber = nlRubrieks.getNode(j).getStringValue("number");
			hmUnusedItems.put(rubriekNumber,getUnusedItemsForRubriek(cloud,rubriekNumber));
		}
		return hmUnusedItems;
	}

	/*
	* For all users add the list of unused objects to users.unused_items field
	*/
   public void getUnusedItems() {
      Cloud cloud = CloudFactory.getCloud();
      AuthorizationHelper authHelper = new AuthorizationHelper(cloud);

		  HashMap hmUnusedItemsForAllRubriek = getUnusedItemsForAllRubriek(cloud);

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
      Thread kicker = getKicker();
      log.info("run(): " + kicker + su.jvmSize());
      getUnusedItems();
		  log.info("done" + su.jvmSize());
   }
}
