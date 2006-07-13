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
package nl.leocms.connectors.UISconnector.output.orders.model;

public class BusinessInformation
{
   private String companyName = "";
   private String vatNo = "";
   private String telephoneNo = "";
   private String emailAddress = "";

   public BusinessInformation()
   {
   }

   public void setCompanyName(String companyName)
   {
      this.companyName = companyName;
   }

   public void setVatNo(String vatNo)
   {
      this.vatNo = vatNo;
   }

   public void setTelephoneNo(String telephoneNo)
   {
      this.telephoneNo = telephoneNo;
   }

   public void setEmailAddress(String emailAddress)
   {
      this.emailAddress = emailAddress;
   }

   public String getCompanyName()
   {
      return companyName;
   }

   public String getVatNo()
   {
      return vatNo;
   }

   public String getTelephoneNo()
   {
      return telephoneNo;
   }

   public String getEmailAddress()
   {
      return emailAddress;
   }
}
