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

public class Address
{
   private String addressType = "";
   private String countryId = "";
   private int houseNumber = 0;
   private String houseNumberExtension = "";
   private String streetName = "";
   private String extraInfo = "";
   private String zipCode = "";
   private String city = "";

   public Address()
   {
   }

   public void setAddressType(String addressType)
   {
      this.addressType = addressType;
   }

   public void setCountryId(String countryId)
   {
      this.countryId = countryId;
   }

   public void setHouseNumber(int houseNumber)
   {
      this.houseNumber = houseNumber;
   }

   public void setHouseNumberExtension(String houseNumberExtension)
   {
      this.houseNumberExtension = houseNumberExtension;
   }

   public void setStreetName(String streetName)
   {
      this.streetName = streetName;
   }

   public void setExtraInfo(String extraInfo)
   {
      this.extraInfo = extraInfo;
   }

   public void setZipCode(String zipCode)
   {
      this.zipCode = zipCode;
   }

   public void setCity(String city)
   {
      this.city = city;
   }

   public String getAddressType()
   {
      return addressType;
   }

   public String getCountryId()
   {
      return countryId;
   }

   public int getHouseNumber()
   {
      return houseNumber;
   }

   public String getHouseNumberExtension()
   {
      return houseNumberExtension;
   }

   public String getStreetName()
   {
      return streetName;
   }

   public String getExtraInfo()
   {
      return extraInfo;
   }

   public String getZipCode()
   {
      return zipCode;
   }

   public String getCity()
   {
      return city;
   }
}
