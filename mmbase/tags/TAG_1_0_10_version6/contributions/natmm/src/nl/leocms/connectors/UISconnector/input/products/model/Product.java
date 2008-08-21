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
   public static final int PRODUCT_TYPE_EVENT = 0;
   public static final int PRODUCT_TYPE_ITEM = 1;
   public static final int PRODUCT_TYPE_SUBSCRIPTION = 2;


   private String externID = null;
   private double price = 0.0;
   private ArrayList paymentTypes = new ArrayList();
   private boolean membershipRequired;
   private Date embargoDate = new Date();
   private Date expireDate = new Date();
   private ArrayList properties;
   private String description;
   private int productType = -1;

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

   public void setDescription(String description)
   {
      this.description = description;
   }

   public void setProductType(int productType)
   {
      this.productType = productType;
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

   public String getDescription()
   {
      return description;
   }

   public int getProductType()
   {
      return productType;
   }

   public String getProductTypeName()
   {
      String productTypeName = "";
      switch(productType){
         case PRODUCT_TYPE_EVENT:{
            productTypeName = "Evenementen";
            break;
         }
         case PRODUCT_TYPE_ITEM:{
            productTypeName = "Producten";
            break;
         }
         case PRODUCT_TYPE_SUBSCRIPTION:{
            productTypeName = "Abonnementen";
            break;
         }
         default:{
            productTypeName = "Unsupported product type";
            break;
         }
      }
      return productTypeName;
   }
}
