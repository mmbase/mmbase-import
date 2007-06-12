package nl.leocms.vastgoed;

import nl.leocms.util.DoubleDateNode;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import com.finalist.mmbase.util.CloudFactory;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

import com.finalist.mmbase.util.CloudFactory;

import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author
 * @version $Id: BestelAction.java,v 1.5 2007-06-12 07:06:24 evdberg Exp $
 *
 * @struts:action name="BestelForm"
 *                path="/vastgoed/BestelAction"
 *                scope="request"
 *                validate="false"
 *
 * @struts:action-forward name="send" path="/nmintra/includes/vastgoed/kaartenformulier.jsp"
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
      BestelForm bestelForm = (BestelForm) form;
      StringBuffer messagePlain = new StringBuffer();
      StringBuffer messageHtml = new StringBuffer();
      
      addLineToMessage(messagePlain, messageHtml, "BestelFormulier");
      addLineToMessage(messagePlain, messageHtml, "---------------");
      addLineToMessage(messagePlain, messageHtml, "Naam: " + bestelForm.getNaam());
      addLineToMessage(messagePlain, messageHtml, "Email: " + bestelForm.getEmail());
      addLineToMessage(messagePlain, messageHtml, "Eendheid: " + bestelForm.getEendheid());
      addLineToMessage(messagePlain, messageHtml, "Bezorgadres: " + bestelForm.getBezorgadres());
      addLineToMessage(messagePlain, messageHtml, "");
      addLineToMessage(messagePlain, messageHtml, "Kaart");
      addLineToMessage(messagePlain, messageHtml, "---------------");
      ArrayList items = (ArrayList) basket.getItems();
      for(int i = 0; (items != null) && (i < items.size()); i++) {
          KaartenForm item = (KaartenForm) items.get(i);
          addLineToMessage(messagePlain, messageHtml, "Item:");
          
          addLineToMessage(messagePlain, messageHtml, "natuurgebied,eenheid,regio,coordinaten etc.: " + item.getKaartType());
          addLineToMessage(messagePlain, messageHtml, "schaal of formaat: " + item.getSchaalOfFormaat());
          addLineToMessage(messagePlain, messageHtml, "aantal: " + item.getAantal());
          addLineToMessage(messagePlain, messageHtml, "gerold of gevouwen: " + item.getGevouwenOfOpgerold());
          addLineToMessage(messagePlain, messageHtml, "");
         
          
          
          
      }
      
      
      //mail
      Cloud cloud = CloudFactory.getCloud();
      Node emailNode = cloud.getNodeManager("email").createNode();
      emailNode.setValue("from", "vastgoed-test@finalist.com");
      emailNode.setValue("to", "ibrahim@finalist.com");
      emailNode.setValue("subject", "test vastgoed");
      emailNode.setValue("replyto", "");
      emailNode.setValue("body",
                      "<multipart id=\"plaintext\" type=\"text/plain\" encoding=\"UTF-8\">"
                         + messagePlain.toString()
                      + "</multipart>"
                      + "<multipart id=\"htmltext\" alt=\"plaintext\" type=\"text/html\" encoding=\"UTF-8\">"
                        + "<html>"
                           + messageHtml.toString()
                        + "</html>"
                      + "</multipart>");
      emailNode.commit();
      emailNode.getValue("mail(oneshotkeep)");
      
      
      //
      // here we should set basket to null !!!
      
      
      
      
      log.debug("processed purchase now forwarding send");
      return mapping.findForward("send");
   }
   
   //
   private void addLineToMessage(StringBuffer messagePlain, StringBuffer messageHtml, String newLine) {
       messagePlain.append(newLine + "\n");
       messageHtml.append(newLine + "<br/>");
    }
   
}
