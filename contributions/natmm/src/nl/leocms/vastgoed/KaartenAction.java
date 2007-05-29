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
* @version $Id: KaartenAction.java,v 1.1 2007-05-29 11:52:28 ieozden Exp $
*
* @struts:action name="KaartenForm"
*                path="/vastgoed/KaartenAction"
*                scope="request"
*                validate="false"
*
* @struts:action-forward name="success" path="/nmintra/includes/vastgoed/bestelformulier.jsp"
*/

public class KaartenAction  extends Action {
	private static final Logger log = Logging.getLoggerInstance(KaartenAction.class);
//	private ShoppingBasket basket;
	private ArrayList basket;
	
	
	 public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
	      log.info("KaartenAction - execute()");
	      
	      // shopping cart
	      basket = ShoppingBasketHelper.getShoppingBasket(request);
	      
	      // actionForm is passed from Struts
	      // kaartenForm is the form acquired from the basket
	      KaartenForm actionForm = (KaartenForm) form;
	      KaartenForm kaartenForm;
	      
	      // shopping_cart param steps over the new item entry - goes direct to cart 
	      if(request.getParameter("shopping_cart") == null) {
	    	  log.debug("KaartenAction - create item - store into basket/ OR update");
	    	  //update or create
	    	  String number = request.getParameter("number");
	    	  if (!number.equals("null")) {
	    		  // updating the existing kart item
	    		  log.debug("KaartenAction - updating existing cart item " + number);
		    	  
	    		  // NEW CODE
//	    		  kaartenForm = (KaartenForm) basket.getItem(number);
//	    		  if (kaartenForm != null) {
//	    		   // populating parameters
//	    			kaartenForm.copyValuesFrom(actionForm);
//	    		  }
	    		  //OLD CODE
	    		  try {
			    	kaartenForm = (KaartenForm) basket.get(Integer.parseInt(number)); //throws 2 exceptions!!!
			    	kaartenForm.copyValuesFrom(actionForm);
	    		  } catch(Exception e) {
		    		  log.debug("Exception in trying to update by non-existing cart item index"); 
		    	  }	
	    		  
	    		  
	    	  } else {
	    		  // adding out form as a new entry
	    		  //NEW CODE
//	    		  basket.addItem(form);
	    		  //OLD CODE
	    		  basket.add(form);
	    	  }

	      }
	      
	      return mapping.findForward("success");
	   }
}
