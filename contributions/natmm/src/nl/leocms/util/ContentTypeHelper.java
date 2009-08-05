/*
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is LeoCMS.
 *
 * The Initial Developer of the Original Code is
 * 'De Gemeente Leeuwarden' (The dutch municipality Leeuwarden).
 *
 * See license.txt in the root of the LeoCMS directory for the full license.
 */
package nl.leocms.util;

import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NotFoundException;

import com.finalist.mmbase.util.CloudFactory;

import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA.
 * User: Gerard van de Weerd
 * Date: 29-okt-2003
 * Time: 12:17:54
 * To change this template use Options | File Templates.
 */
public class ContentTypeHelper {

   public final static String CONTENTELEMENT = "contentelement";

   public final static String AFBEELDING = "images";
   public final static String ARTIKEL = "artikel";
   public final static String BIJLAGE = "attachments";
   public final static String EVENEMENT = "evenement";
   public final static String FLASH = "flash";
   public final static String LINK = "link";   
   public final static String NATUURGEBIEDEN = "natuurgebieden";   
   public final static String ORGANISATIE = "organisatie";
   public final static String PAGINA = "pagina";
   public final static String PANNO = "panno";
   public final static String PARAGRAAF = "paragraaf";
   public final static String PERSOON = "persoon";
   public final static String POLL = "poll";
   public final static String PROVINCIES = "provincies";
   public final static String SHORTY = "shorty";
   public final static String TEASER = "teaser";
   public final static String VACATURE = "vacature";
   public final static String VGV = "vgv";


   /** Is element from one of the content types
    *
    * @param element node to check
    * @return is content type
    */
   public static boolean isContentElement(Node element) {
      NodeManager nm = element.getNodeManager();
      return isContentType(nm);
   }

   /** Is type of content type
    *
    * @param type to check
    * @return is content type
    */
   public static boolean isContentType(String type) {
      StringTokenizer st = new StringTokenizer(type, ",");
      boolean isContentType = false;
      while (st.hasMoreTokens()) {
         String contentType = st.nextToken();
         NodeManager nm = CloudFactory.getCloud().getNodeManager(contentType);
         isContentType = isContentType(nm);
         if (!isContentType) {
            return false;
         }
      }
      return isContentType;
   }

   /** Is ModeManager on of the content types
    *
    * @param nm NodeManager to check
    * @return is content type
    */
   public static boolean isContentType(NodeManager nm) {
      if (AFBEELDING.equals(nm.getName()) || BIJLAGE.equals(nm.getName())) {
         return true; 
      }
      try {
         while(!CONTENTELEMENT.equals(nm.getName())) {
            nm = nm.getParent();
         }
         return true;
      }
      catch(NotFoundException nfe) {
         // Ran out of NodeManager parents
      }
      return false;
   }

   /** Is type of artikel
    *
    * @param type to check
    * @return is content type
    */
   public static boolean isArtikel(Node content) {
      return isContentOfType(content, ARTIKEL);
   }

   /** Is type of vacature
    *
    * @param type to check
    * @return is content type
    */
   public static boolean isVacature(Node content) {
      return isContentOfType(content, VACATURE);
   }

   
   /** Is type of pagina
    *
    * @param type to check
    * @return is content type
    */
   public static boolean isPagina(Node content) {
      return isContentOfType(content, PAGINA);
   }

   /** Is type of poll
    *
    * @param type to check
    * @return is content type
    */
   public static boolean isPoll(Node content) {
      return isContentOfType(content, POLL);
   }
   
   /** Is content of type
    *
    * @param node to check
    * @param type to check
    * @return is content type
    */
   public static boolean isContentOfType(Node content, String type) {
      return type.equalsIgnoreCase(content.getNodeManager().getName());
   }

   /** Is element from one of the workflor types
    *
    * @param element node to check
    * @return is workflow type
    */
   public static boolean isWorkflowElement(Node element) {
      return isWorkflowType(element.getNodeManager().getName());
   }

   /** Is type of workflor type
    *
    * @param type to check
    * @return is workflow type
    */
   public static boolean isWorkflowType(String type) {
      if (ARTIKEL.equals(type)
         || EVENEMENT.equals(type)
         || VACATURE.equals(type)
         || POLL.equals(type)
         || VGV.equals(type)) {
         return true;
      }
      return false;
   }

   /** Is ModeManager on of the workflor types
    *
    * @param nm NodeManager to check
    * @return is workflow type
    */
   public static boolean isWorkflowType(NodeManager nm) {
      return isWorkflowType(nm.getName());
   }
   
}
