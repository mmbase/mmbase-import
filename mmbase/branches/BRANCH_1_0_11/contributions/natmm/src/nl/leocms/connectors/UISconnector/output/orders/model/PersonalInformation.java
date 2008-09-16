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

public class PersonalInformation
{
   private String initials = "";
   private String firstName = "";
   private String suffix = "";
   private String lastName = "";
   private Date birthDate = new Date();
   private String gender = "";
   private String telephoneNo = "";
   private String emailAddress = "";

   public PersonalInformation()
   {
   }

   public void setInitials(String initials)
   {
      this.initials = initials;
   }

   public void setFirstName(String firstName)
   {
      this.firstName = firstName;
   }

   public void setSuffix(String suffix)
   {
      this.suffix = suffix;
   }

   public void setLastName(String lastName)
   {
      this.lastName = lastName;
   }

   public void setBirthDate(Date birthDate)
   {
      this.birthDate = birthDate;
   }

   public void setGender(String gender)
   {
      this.gender = gender;
   }

   public void setTelephoneNo(String telephoneNo)
   {
      this.telephoneNo = telephoneNo;
   }

   public void setEmailAddress(String emailAddress)
   {
      this.emailAddress = emailAddress;
   }

   public String getInitials()
   {
      return initials;
   }

   public String getFirstName()
   {
      return firstName;
   }

   public String getSuffix()
   {
      return suffix;
   }

   public String getLastName()
   {
      return lastName;
   }

   public Date getBirthDate()
   {
      return birthDate;
   }

   public String getGender()
   {
      return gender;
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
