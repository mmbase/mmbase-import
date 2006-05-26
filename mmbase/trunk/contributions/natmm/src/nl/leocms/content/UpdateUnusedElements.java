package nl.leocms.content;

import java.util.*;
import javax.servlet.*;
import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;
import com.finalist.mmbase.util.CloudFactory;
import nl.leocms.util.ContentHelper;
import nl.leocms.authorization.AuthorizationHelper;
import nl.leocms.authorization.UserRole;
import nl.leocms.authorization.Roles;

public class UpdateUnusedElements implements Runnable {

   private static final Logger log = Logging.getLoggerInstance(UpdateUnusedElements.class);
	
	/*
	* For all users add the list of unused contentelements to the application scope
	*/
   public void getUnusedItems(){
      Cloud cloud = CloudFactory.getCloud();
      AuthorizationHelper authHelper = new AuthorizationHelper(cloud);
      ContentHelper ch = new ContentHelper(cloud);
      
		HashMap hmUnusedItems = new HashMap();
      NodeList nlUsers = cloud.getNodeManager("users").getList(null,null,null);
      for (int i = 0; i < nlUsers.size(); i++){
         String account = nlUsers.getNode(i).getStringValue("account");
			// if(account.equals("admin")) { // for testing purposes, only restrict to admin
				ArrayList alUnusedItems = new ArrayList();
				NodeList nlRubrieks = cloud.getNodeManager("rubriek").getList(null,null,null);
				for(int j = 0; j < nlRubrieks.size(); j++){
					Node rubriek = nlRubrieks.getNode(j);
					UserRole userRole = authHelper.getRoleForUser(authHelper.getUserNode(account), rubriek);
					if (userRole.getRol() >= Roles.SCHRIJVER) {
						NodeList nlElements = cloud.getList(rubriek.getStringValue("number"),"rubriek,creatierubriek,contentelement","contentelement.number",null,null,null,null,false);
						for (int k = 0; k < nlElements.size(); k++){
							if (ch.usedInItems(nlElements.getNode(k).getStringValue("contentelement.number"))==null){
								alUnusedItems.add(nlElements.getNode(k).getStringValue("contentelement.number"));
							}
						}
					}
				}
				if(alUnusedItems.size()>0) {
					log.debug("adding unused items for user " + account);
					hmUnusedItems.put(account,alUnusedItems);
				}
			// }
      }
		
      MMBaseContext mc = new MMBaseContext();
      ServletContext application = mc.getServletContext();
      application.setAttribute("UnusedItems",hmUnusedItems);
   }

   private Thread getKicker(){
      Thread  kicker = Thread.currentThread();
      if(kicker.getName().indexOf("UpdateUsedElementsThread")==-1) {
           kicker.setName("UpdateUsedElementsThread / " + (new Date()));
           kicker.setPriority(Thread.MIN_PRIORITY+1); // *** does this help ?? ***
      }
      return kicker;
   }

   public void run () {
      Thread kicker = getKicker();
      log.info("run(): " + kicker);
      getUnusedItems();
   }
}
