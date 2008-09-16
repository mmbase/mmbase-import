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

import nl.leocms.applications.NatMMConfig;

public class UISconfig {

   private static String baseUrl = "http://mcweb2/"; // url of WSS server

   public UISconfig() {
   }

   public static boolean isUISconnected() {
      return NatMMConfig.isUISconnected;
   }

   public static String postOrderUrl(){
      return  baseUrl + "mmdemo/api/postOrders.jsp";
   }

   public static String getProductUrl(){
      return baseUrl + "mmdemo/api/getProducts.jsp";
      //return "file:///Z:/in.xml";
   }

   public static String getProductPropertiesURL(){
      return baseUrl + "mmdemo/api/getProductPropertyList.jsp";
      //return "file:///Z:/in2.xml";
   }
   public static String getCustomerPropertiesURL(){
      return baseUrl + "mmdemo/api/getCustomerPropertyList.jsp";
      //return "file:///Z:/in2.xml";
   }

   public static String getCustomersURL(String sUserName, String sPassword){
      return baseUrl + "mmdemo/api/getCustomerInformation.jsp?username=" + sUserName + "&pwd=" + sPassword;
      //return "file:///Z:/" +sUserName + ".xml";
   }
}
