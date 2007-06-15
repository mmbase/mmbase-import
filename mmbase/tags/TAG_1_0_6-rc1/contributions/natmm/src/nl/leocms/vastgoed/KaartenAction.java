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

/**
 *
 * @author
 * @version $Id: KaartenAction.java,v 1.3 2007-06-06 10:12:27 evdberg Exp $
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
   private ShoppingBasket basket;
   
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
      log.debug("execute()");
      
      // shopping cart
      basket = ShoppingBasketHelper.getShoppingBasket(request);
      
      // actionForm is passed from Struts
      // basketForm is the form acquired from the basket
      KaartenForm actionForm = (KaartenForm) form;
      KaartenForm basketForm;
      
      // shopping_cart param means step over entering a new item - go direct to cart
      if(request.getParameter("shopping_cart") == null) {
         log.debug("create item and store into basket OR update existing item");
         //update or create
         String number = request.getParameter("number");
         if (!number.equals("null")) {
            // updating the existing kart item
            log.debug("updating existing cart item: " + number);
            
            basketForm = (KaartenForm) basket.getItem(number);
            if (basketForm != null) {
               // populating parameters
               basketForm.copyValuesFrom(actionForm);
            }
         } else {
            // no shopping_cart param - adding the passed form as a new cart entry
            basket.addItem(form);
         }
      }
      
      // forwarding
      return mapping.findForward("success");
   }
}
