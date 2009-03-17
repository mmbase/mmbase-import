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

import java.util.*;

public class Order
{
   private int orderId = 0;
   private String externId = "";
   private int quantity = 0;
   private String acquisitionId = "";
   private Date orderDate = new Date();
   private String paymentType = "";
   private CustomerInformation customerInformation = new CustomerInformation();
   private String extraInformation = "";

   public Order()
   {
   }

   public void setOrderId(int orderId)
   {
      this.orderId = orderId;
   }

   public void setExternId(String externId)
   {
      this.externId = externId;
   }

   public void setQuantity(int quantity)
   {
      this.quantity = quantity;
   }

   public void setAcquisitionId(String acquisitionId)
   {
      this.acquisitionId = acquisitionId;
   }

   public void setOrderDate(Date orderDate)
   {
      this.orderDate = orderDate;
   }

   public void setPaymentType(String paymentType)
   {
      this.paymentType = paymentType;
   }

   public void setCustomerInformation(CustomerInformation customerInformation)
   {
      this.customerInformation = customerInformation;
   }

   public void setExtraInformation(String extraInformation) {
      this.extraInformation = extraInformation;
   }

   public int getOrderId()
   {
      return orderId;
   }

   public String getExternId()
   {
      return externId;
   }

   public int getQuantity()
   {
      return quantity;
   }

   public String getAcquisitionId()
   {
      return acquisitionId;
   }

   public Date getOrderDate()
   {
      return orderDate;
   }

   public String getPaymentType()
   {
      return paymentType;
   }

   public CustomerInformation getCustomerInformation()
   {
      return customerInformation;
   }

   public String getExtraInformation() {
      return extraInformation;
   }

}
