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
 * @version $Id: BestelAction.java,v 1.3 2007-06-06 10:11:39 evdberg Exp $
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
   private ShoppingBasket basket;
   
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
      log.debug("execute()");
      
      // shopping cart
      basket = ShoppingBasketHelper.getShoppingBasket(request);
      
      // checking if delete action requested
      String deleteAction = request.getParameter("delete");
      if (deleteAction != null) {
         log.info("deleting cart item: " + deleteAction);
         
         basket.removeItem(deleteAction);
         
         // delete action returns to cart
         log.debug("deleted now mapping forward delete");
         return mapping.findForward("delete");
      }
      
      // processing the purchase and forwarding send
      log.debug("processing purchase");
      
      // process the shopping cart as a purchase
      
      log.debug("processed purchase now forwarding send");
      return mapping.findForward("send");
   }
   
}
