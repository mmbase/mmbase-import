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

public class CustomerInformation
{
   private PersonalInformation personalInformation = new PersonalInformation();
   private BusinessInformation businessInformation = new BusinessInformation();
   private CommonInformation commonInformation = new CommonInformation();
   private Address address = new Address();
   private ArrayList properties = new ArrayList();

   public CustomerInformation()
   {
   }

   public void setPersonalInformation(PersonalInformation personalInformation)
   {
      this.personalInformation = personalInformation;
   }

   public void setBusinessInformation(BusinessInformation businessInformation)
   {
      this.businessInformation = businessInformation;
   }

   public void setCommonInformation(CommonInformation commonInformation)
   {
      this.commonInformation = commonInformation;
   }

   public void setAddress(Address address)
   {
      this.address = address;
   }

   public void setProperties(ArrayList properties)
   {
      this.properties = properties;
   }

   public PersonalInformation getPersonalInformation()
   {
      return personalInformation;
   }

   public BusinessInformation getBusinessInformation()
   {
      return businessInformation;
   }

   public CommonInformation getCommonInformation()
   {
      return commonInformation;
   }

   public Address getAddress()
   {
      return address;
   }

   public ArrayList getProperties()
   {
      return properties;
   }

}
