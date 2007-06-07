package nl.leocms.vastgoed;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import org.mmbase.util.logging.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class KaartenInitAction extends Action {
	
	private static final Logger log = Logging.getLoggerInstance(KaartenInitAction.class);
	private ShoppingBasket basket;
	
/**
* @param mapping
* @param form
* @param request
* @param response
* @return
* @throws Exception
*/
public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
  log.debug("execute with number(" + request.getParameter("number") + ")");
  
  // actionForm is passed from Struts
  // basketForm is the form acquired from the basket
  KaartenForm actionForm = (KaartenForm) form;
  KaartenForm basketForm;
  
  // shopping cart
  basket = ShoppingBasketHelper.getShoppingBasket(request);
  
  //populate with values
  String number = request.getParameter("number");
  if (number != null) {
	  log.debug("populating with values from cart item " + number);
	  
	  basketForm = (KaartenForm) basket.getItem(number);
	  if (basketForm != null) {
		// populating parameter values
		actionForm.copyValuesFrom(basketForm);
      actionForm.updateMapValues();
	  }
  }
	  log.debug("forwarding...");
	  return mapping.findForward("success");
}

} 

