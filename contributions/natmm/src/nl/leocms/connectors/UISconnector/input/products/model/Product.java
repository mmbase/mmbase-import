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
 package nl.leocms.connectors.UISconnector.input.products.model;

import java.util.*;

public class Product
{
   private String externID = null;
   private double price = 0.0;
   private ArrayList paymentTypes = new ArrayList();
   private boolean membershipRequired;
   private Date embargoDate = new Date();
   private Date expireDate = new Date();
   private ArrayList properties;

   public Product()
   {
   }

   public void setExternID(String externID)
   {
      this.externID = externID;
   }

   public void setPrice(double price)
   {
      this.price = price;
   }

   public void setPaymentTypes(ArrayList paymentTypes)
   {
      this.paymentTypes = paymentTypes;
   }

   public void setEmbargoDate(Date embargoDate)
   {
      this.embargoDate = embargoDate;
   }

   public void setExpireDate(Date expireDate)
   {
      this.expireDate = expireDate;
   }

   public void setMembershipRequired(boolean membershipRequired)
   {
      this.membershipRequired = membershipRequired;
   }

   public void setProperties(ArrayList properties)
   {
      this.properties = properties;
   }

   public String getExternID()
   {
      return externID;
   }

   public double getPrice()
   {
      return price;
   }

   public ArrayList getPaymentTypes()
   {
      return paymentTypes;
   }

   public Date getEmbargoDate()
   {
      return embargoDate;
   }

   public Date getExpireDate()
   {
      return expireDate;
   }

   public boolean isMembershipRequired()
   {
      return membershipRequired;
   }

   public ArrayList getProperties()
   {
      return properties;
   }

}
