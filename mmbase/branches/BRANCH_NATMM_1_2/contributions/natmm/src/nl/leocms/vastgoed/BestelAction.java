package nl.leocms.vastgoed;

import nl.leocms.util.DoubleDateNode;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import com.finalist.mmbase.util.CloudFactory;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.RelationManager;
import org.mmbase.bridge.Relation;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

import com.finalist.mmbase.util.CloudFactory;
import nl.leocms.applications.NMIntraConfig;

import java.util.ArrayList;
import java.text.DateFormat;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author
 * @version $Id: BestelAction.java,v 1.19 2007-11-21 12:54:05 evdberg Exp $
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
         log.debug("deleting cart item: " + deleteAction);
         
         basket.removeItem(deleteAction);
         
         // delete action returns to cart
         log.debug("deleted now mapping forward delete.");
         return mapping.findForward("delete");
      }
      
      // processing the purchase and forwarding send
      log.debug("processing purchase");
      
      // if kart is empty then no processing will be made
      if (basket.getItems().size() == 0) {
         log.debug("cart is empty. forwarding send without processing.");
         // 'order processed' message is supressed. a quick fix using attributes.
         request.setAttribute("empty", "true");
         return mapping.findForward("send");
      }
      
      // process the shopping cart as a purchase
      BestelForm bestelForm = (BestelForm) form;
      StringBuffer messagePlain = new StringBuffer();
      StringBuffer messageHtml = new StringBuffer();
      Cloud cloud = CloudFactory.getCloud();
      // entry of this kart sale into DB
      
      
      try {
         //Store order in database:
         Node bestelNode = cloud.getNodeManager("bestelling_vastgoed").createNode();
         bestelNode.setValue("naam", bestelForm.getNaam());
         bestelNode.setValue("email", bestelForm.getEmail());
         bestelNode.setValue("eenheid", bestelForm.getEendheid());
         bestelNode.setValue("alternatief_bezorgadres", bestelForm.getBezorgadres());
         bestelNode.commit();

         // mail message: kart values for buyer
         addLineToMessage(messagePlain, messageHtml, "Bestelformulier");
         addLineToMessage(messagePlain, messageHtml, "---------------");
         addLineToMessage(messagePlain, messageHtml, "Datum: " + DateFormat.getInstance().format(new Date()));
         addLineToMessage(messagePlain, messageHtml, "Naam: " + bestelForm.getNaam());
         addLineToMessage(messagePlain, messageHtml, "Email: " + bestelForm.getEmail());
         addLineToMessage(messagePlain, messageHtml, "Eenheid: " + bestelForm.getEendheid());
         addLineToMessage(messagePlain, messageHtml, "Bezorgadres: " + bestelForm.getBezorgadres());
         addLineToMessage(messagePlain, messageHtml, "");
         addLineToMessage(messagePlain, messageHtml, "Kaart");
         addLineToMessage(messagePlain, messageHtml, "---------------");
         ArrayList items = (ArrayList) basket.getItems();
         // kart items

         for(int i = 0; (items != null) && (i < items.size()); i++) {
            KaartenForm item = (KaartenForm) items.get(i);
            
            // kaartsoort string from nodenumbers in sel_Kaart
            String kaartsoort = "";
            Node currentNode = null;
            String[] kartNodes = item.getSel_Kaart();
            
            for (int j = 0; (kartNodes != null) && (j < kartNodes.length); j++) {
               String nodeNumber = kartNodes[j];
               currentNode = cloud.getNode(nodeNumber);
               kaartsoort = currentNode.getStringValue("naam");
               
               RelationManager remoteRelationManager = cloud.getRelationManager("bestelling_vastgoed", "thema_plot_kaart", "kaart_bestel_regel");
               
               if (item.getRad_Gebied().equals("Natuurgebied")) { //n:m (of j:k in dit geval)
                  String[] natuurgebiedList = item.getSel_NatGeb();

                  for (int k = 0; (natuurgebiedList != null) && (k < natuurgebiedList.length); k++) {
                     addLineToMessage(messagePlain, messageHtml, "Item:");
                     addLineToMessage(messagePlain, messageHtml, "-----");

                     addLineToMessage(messagePlain, messageHtml, "kaartsoort: " + kaartsoort);
                     //other item elements
                     addLineToMessage(messagePlain, messageHtml, "schaal of formaat: " + item.getSchaalOfFormaat());
                     addLineToMessage(messagePlain, messageHtml, "aantal: " + item.getAantal());
                     addLineToMessage(messagePlain, messageHtml, "gerold of gevouwen: " + item.getGevouwenOfOpgerold());
                     addLineToMessage(messagePlain, messageHtml, "natuurgebied,eenheid,regio,coordinaten etc.: " + item.getKaartType());
                     //kart type details for natuurgebied and eenheid
                     addLineToMessage(messagePlain, messageHtml, "detail: " + natuurgebiedList[k]);

                     addLineToMessage(messagePlain, messageHtml, "opmerkingen: " + item.getOpmerkingen());
                     addLineToMessage(messagePlain, messageHtml, "");

                     Relation bestelRegel = remoteRelationManager.createRelation(bestelNode, currentNode);
                     bestelRegel.setValue("schaal_formaat",item.getSchaalOfFormaat());
                     bestelRegel.setValue("aantal",item.getAantal());
                     bestelRegel.setValue("gevouwd_gerold",item.getGevouwenOfOpgerold());
                     bestelRegel.setValue("opmerkingen",item.getOpmerkingen());
                     bestelRegel.setValue("details",natuurgebiedList[k]);
                     bestelRegel.setValue("timestamp",new Integer((int)(System.currentTimeMillis()/1000)));
                     bestelRegel.commit();
                  }
               }
               else { //1:n
                  addLineToMessage(messagePlain, messageHtml, "Item:");
                  addLineToMessage(messagePlain, messageHtml, "-----");

                  addLineToMessage(messagePlain, messageHtml, "kaartsoort: " + kaartsoort);
                  //other item elements
                  addLineToMessage(messagePlain, messageHtml, "schaal of formaat: " + item.getSchaalOfFormaat());
                  addLineToMessage(messagePlain, messageHtml, "aantal: " + item.getAantal());
                  addLineToMessage(messagePlain, messageHtml, "gerold of gevouwen: " + item.getGevouwenOfOpgerold());
                  addLineToMessage(messagePlain, messageHtml, "natuurgebied,eenheid,regio,coordinaten etc.: " + item.getKaartType());
                  //kart type details for natuurgebied and eenheid
                  addLineToMessage(messagePlain, messageHtml, "detail: " + item.getKaartTypeDetail());

                  addLineToMessage(messagePlain, messageHtml, "opmerkingen: " + item.getOpmerkingen());
                  addLineToMessage(messagePlain, messageHtml, "");

                  Relation bestelRegel = remoteRelationManager.createRelation(bestelNode, currentNode);
                  bestelRegel.setValue("schaal_formaat",item.getSchaalOfFormaat());
                  bestelRegel.setValue("aantal",item.getAantal());
                  bestelRegel.setValue("gevouwd_gerold",item.getGevouwenOfOpgerold());
                  bestelRegel.setValue("opmerkingen",item.getOpmerkingen());
                  bestelRegel.setValue("details",item.getKaartTypeDetail());
                  bestelRegel.setValue("timestamp",new Integer((int)(System.currentTimeMillis()/1000)));
                  bestelRegel.commit();
               }

               log.debug("saved bestelling to database: " + bestelNode.getStringValue("number"));
            }
         }

         // send mail
         Node emailNode = cloud.getNodeManager("email").createNode();
         emailNode.setValue("from",NMIntraConfig.getFromEmailAddress());
         emailNode.setValue("to", NMIntraConfig.getGisEmailAddress());
         emailNode.setValue("subject", "Nieuwe kaarten besteld - " + bestelForm.getNaam());
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
         emailNode.setValue("to", bestelForm.getEmail());
         messagePlain = new StringBuffer("Details van uw bestelling: \n\n").append(messagePlain);
         messageHtml = new StringBuffer("Details van uw bestelling: <br/><br/>").append(messageHtml);
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
      } catch (Exception e) {
         // here should be rollback of database
         log.error("Something went wrong while processing order");
      }
      
      // here we reset basket and bestelforms
      ShoppingBasketHelper.removeShoppingBasket(request);
      // NMCMS-327. Customer requests these fields not erased after a shopping cart submit. Hence commented-out.
      //bestelForm.setNaam(null);
      //bestelForm.setEmail(null);
      //bestelForm.setEendheid(null);
      //bestelForm.setBezorgadres(null);
      
      log.debug("processed purchase now forwarding send");
      return mapping.findForward("send");
   }
   
   //
   private void addLineToMessage(StringBuffer messagePlain, StringBuffer messageHtml, String newLine) {
      messagePlain.append(newLine + "\n");
      messageHtml.append(newLine + "<br/>");
   }
   
}
