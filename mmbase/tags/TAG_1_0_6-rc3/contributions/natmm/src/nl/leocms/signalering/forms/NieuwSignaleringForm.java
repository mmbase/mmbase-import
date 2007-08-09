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
package nl.leocms.signalering.forms;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionError;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import java.text.SimpleDateFormat;
import java.text.ParseException;

/**
 * Form bean for the NieuwSignaleringForm page.
 *
 * @author Ronald Kramp
 * @version $Revision: 1.2 $, $Date: 2006-03-08 22:23:51 $
 *
 * @struts:form name="NieuwSignaleringForm"
 */

public class NieuwSignaleringForm extends ActionForm {

   private static final Logger log = Logging.getLoggerInstance(NieuwSignaleringForm.class);
   
   final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
   
   private String pagenumber;
   private String usernumber;
   private String afzender;
   private String contentelement;
   private String datumVanDag;
   private String datumVanMaand;
   private String datumVanJaar;
   private String notitie;
   private boolean emailnotificatie;
   private String herhaling;

   /**
    * Actual validate function
    * @param mapping
    * @param request
    * @return
    */
   public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
      ActionErrors errors = new ActionErrors();

      if (getUsernumber() == null || getUsernumber().length() == 0) {
         errors.add("usernumber", new ActionError("missing.usernumber"));
      }

      
      String date = getDatumVanDag() + "-" + getDatumVanMaand() + "-" + getDatumVanJaar();
      try {
         DATE_FORMAT.setLenient(false);
         DATE_FORMAT.parse(date);
         // if we are here the date is valid
      }
      catch (ParseException e) {
          errors.add("datumVanDag", new ActionError("novalid.deadline"));
      }

      return errors;
   }

   /**
    * Getter for property usernumber.
    * @return property usernumber
    */
   public String getUsernumber() {
      return usernumber;
   }

   /**
    * Setter for property usernumber.
    * @param usernumber property usernumber
    */
   public void setUsernumber(String usernumber) {
      this.usernumber = usernumber;
   }
   
   /**
    * Getter for property afzender.
    * @return property afzender
    */
   public String getAfzender() {
      return afzender;
   }

   /**
    * Setter for property usernumber.
    * @param usernumber property usernumber
    */
   public void setAfzender(String afzender) {
      this.afzender = afzender;
   }

   /**
    * Getter for property pagenumber.
    * @return property pagenumber.
    */
   public String getPagenumber() {
      return pagenumber;
   }

   /**
    * Setter for property pagenumber.
    * @param pagenumber property pagenumber.
    */
   public void setPagenumber(String pagenumber) {
      this.pagenumber = pagenumber;
   }
   
   /**
    * @return contentelement
    */
   public String getContentelement() {
      return contentelement;
   }

   /**
    * @param contentelement
    */
   public void setContentelement(String contentelement) {
      this.contentelement = contentelement;
   }

   /**
    * @return datumVanDag
    */
   public String getDatumVanDag() {
      return datumVanDag;
   }

   /**
    * @param datumVanDag
    */
   public void setDatumVanDag(String datumVanDag) {
      this.datumVanDag = datumVanDag;
   }
   
   /**
    * @return datumVanMaand
    */
   public String getDatumVanMaand() {
      return datumVanMaand;
   }

   /**
    * @param datumVanMaand
    */
   public void setDatumVanMaand(String datumVanMaand) {
      this.datumVanMaand = datumVanMaand;
   }
   
   /**
    * @return datumVanJaar
    */
   public String getDatumVanJaar() {
      return datumVanJaar;
   }

   /**
    * @param datumVanJaar
    */
   public void setDatumVanJaar(String datumVanJaar) {
      this.datumVanJaar = datumVanJaar;
   }

   /**
    * @return
    */
   public String getNotitie() {
      return notitie;
   }

   /**
    * @param notitie
    */
   public void setNotitie(String notitie) {
      this.notitie = notitie;
   }

   /**
    * @return emailnotificatie
    */
   public boolean isEmailnotificatie() {
      return emailnotificatie;
   }

   /**
    * @param emailnotificatie
    */
   public void setEmailnotificatie(boolean emailnotificatie) {
      this.emailnotificatie = emailnotificatie;
   }


   /**
    * @return Returns the herhaling.
    */
   public String getHerhaling() {
      return herhaling;
   }

   /**
    * @param herhaling The herhaling to set.
    */
   public void setHerhaling(String herhaling) {
      this.herhaling = herhaling;
   }

}