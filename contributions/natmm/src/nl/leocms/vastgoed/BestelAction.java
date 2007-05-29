package nl.leocms.vastgoed;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

import com.finalist.mmbase.util.CloudFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

/**
*
* @author
* @version $Id: BestelAction.java,v 1.1 2007-05-29 11:52:28 ieozden Exp $
*
* @struts:action name="BestelForm"
*                path="/vastgoed/BestelAction"
*                scope="request"
*                validate="false"
*
* @struts:action-forward name="send" path="/nmintra/includes/vastgoed/kaartenformulier.jsp"
* @struts:action-forward name="back" path="nmintra/includes/vastgoed/kaartenformulier.jsp"
* @struts:action-forward name="delete" path="nmintra/includes/vastgoed/bestelformulier.jsp"
*/

public class BestelAction  extends Action {
	private static final Logger log = Logging.getLoggerInstance(BestelAction.class);
//	private ShoppingBasket basket;
	private ArrayList basket;
	
	 public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
	      log.info("BestelAction - execute()");

	      // shopping cart
	      basket = ShoppingBasketHelper.getShoppingBasket(request);
	      
	      // delete
	      log.debug ("BestelAction - before delete check");
	      String deleteAction = request.getParameter("delete");
	      log.debug("BestelAction - deleteAction" + deleteAction);
	      if (deleteAction != null) {
	    	  log.info("BestelAction - inside delete");
	    	  
	    	  // NEW CODE
//	    	  basket.removeItem(deleteAction);
	    	  //OLD CODE
	    	  try {
		    	  basket.remove(Integer.parseInt(deleteAction)); //throws 2 exceptions!!!
		    	  } catch(Exception e) {
		    		  log.debug("Exception in trying to remove by non-existing cart item index"); 
		    	  }
	    	  
	    	  log.debug("BestelAction - deleted now mapping forward delete");
	    	  return mapping.findForward("delete");
	    	  
	      }
	      
	      
	      log.debug("BestelAction - mapping forward back");
	      return mapping.findForward("back");
	   }
	   
}
