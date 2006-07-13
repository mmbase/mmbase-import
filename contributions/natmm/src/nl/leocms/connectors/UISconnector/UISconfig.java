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
 * The Initial Developer of the Original Code is 'Media Competence'
 *
 * See license.txt in the root of the LeoCMS directory for the full license.
 */
package nl.leocms.connectors.UISconnector;

/* This class contains settings specific for the UISConnector
*/

public class UISconfig {

   public static boolean IS_ACTIVE = true;
   public static String PRODUCT_URL = "http://212.123.241.76/mmdemo/api/getProducts.jsp";
   // public static String ORDER_URL = "http://212.123.241.76/mmdemo/api/postOrders.jsp";
   public static String ORDER_URL = "http://mc018/mmdemo/api/postOrders.jsp"; // erwins machine

   public UISconfig() {
   }
   public static String geProductUrl(){
      return PRODUCT_URL;
   }
}
