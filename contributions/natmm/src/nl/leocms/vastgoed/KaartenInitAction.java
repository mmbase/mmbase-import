package nl.leocms.vastgoed;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import org.mmbase.util.logging.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.*;

public class KaartenInitAction extends Action {
	
	private static final Logger log = Logging.getLoggerInstance(KaartenInitAction.class);
//	private ShoppingBasket basket;
	private ArrayList basket;
	
/**
* @param mapping
* @param form
* @param request
* @param response
* @return
* @throws Exception
*/
public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
  log.info("KaartenInitAction.execute(" + request.getParameter("number") + ")");
  
  KaartenForm kaartenForm = (KaartenForm) form;
  KaartenForm basketForm;
  
  // shopping cart
  basket = ShoppingBasketHelper.getShoppingBasket(request);
  
  //populate with values
  log.debug("KaartenInitAction - before populate check");
  String number = request.getParameter("number");
  log.debug("KaartenInitAction - populating " + number);
  if (number != null) {
	  log.debug("KaartenInitAction - inside populate");
	  
	  //NEW CODE
//	  basketForm = (KaartenForm) basket.getItem(number);
//	  if (basketForm != null) {
//	   // populating parameters
//		kaartenForm.copyValuesFrom(basketForm);
//	  }
	  //OLD CODE
	  try {
		  basketForm = (KaartenForm) basket.get(Integer.parseInt(number)); //throws 2 exceptions!!!
		  kaartenForm.copyValuesFrom(basketForm);
	  } catch(Exception e) {
		  log.debug("Exception in trying to populate by non-existing cart item index"); 
	  }
	  
  }
	  log.debug("KaartenInitAction - now forwarding.");
	  return mapping.findForward("success");
  
}

} 

