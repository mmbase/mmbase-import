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
package nl.leocms.pagina.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionError;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;
/**
 * @author Gerard van de Weerd $Date: 2006-03-05 21:43:58 $
 * 
 * @struts:form name="PaginaForm"
 */
public class PaginaForm extends ActionForm {
   private String parent;
   private String username;
   private String titel;
   private String titel_fra;
   private String titel_eng;
   private String titel_de;   
   private String kortetitel;
   private String kortetitel_fra;
   private String kortetitel_eng;
   private String kortetitel_de;
   private String omschrijving;
   private String omschrijving_fra;
   private String omschrijving_eng;
   private String omschrijving_de;
   private String urlfragment;
   private String notitie;
   private String metatags;
   private int verloopdatumdag;
   private int verloopdatummaand;
   private int verloopdatumjaar;
   private int embargodag;
   private int embargomaand;
   private int embargojaar;
   private String node;
   private String paginatemplate;
   
   private long verloopdatum;
   private long embargo;
   
   final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
   
   /**
    * @param username The username to set.
    */
   public void setUsername(String username) {
      this.username = username;
   }
   
   /**
    * @param titel The titel to set.
    */
   public void setTitel(String titel) {
      this.titel = titel;
   }

   /**
    * @param titel_fra The titel_fra to set.
    */
   public void setTitel_fra(String titel_fra) {
      this.titel_fra = titel_fra;
   }
   
   /**
    * @param titel_de The titel_de to set.
    */
   public void setTitel_de(String titel_de) {
      this.titel_de = titel_de;
   }
   
   /**
    * @param titel_eng The titel_eng to set.
    */
   public void setTitel_eng(String titel_eng) {
      this.titel_eng = titel_eng;
   }
   
   /**
    * @param kortetitel The kortetitel to set.
    */
   public void setKortetitel(String kortetitel) {
      this.kortetitel = kortetitel;
   }
   
   /**
    * @param kortetitel The kortetitel to set.
    */
   public void setKortetitel_fra(String kortetitel_fra) {
      this.kortetitel_fra = kortetitel_fra;
   }
   
   /**
    * @param kortetitel The kortetitel to set.
    */
   public void setKortetitel_eng(String kortetitel_eng) {
      this.kortetitel_eng = kortetitel_eng;
   }
   
   /**
    * @param kortetitel The kortetitel to set.
    */
   public void setKortetitel_de(String kortetitel_de) {
      this.kortetitel_de = kortetitel_de;
   }
   
   /**
    * @param omschrijving The omschrijving to set.
    */
   public void setOmschrijving(String omschrijving) {
      this.omschrijving = omschrijving;
   }

   /**
    * @param omschrijving_fra The omschrijving_fra to set.
    */
   public void setOmschrijving_fra(String omschrijving_fra) {
      this.omschrijving_fra = omschrijving_fra;
   }
   
   /**
    * @param omschrijving_de The omschrijving_de to set.
    */
   public void setOmschrijving_de(String omschrijving_de) {
      this.omschrijving_de = omschrijving_de;
   }
   
   /**
    * @param omschrijving_eng The omschrijving_eng to set.
    */
   public void setOmschrijving_eng(String omschrijving_eng) {
      this.omschrijving_eng = omschrijving_eng;
   }
   
   /**
    * @param string
    */
   public void setMetatags(String metatags) {
      this.metatags = metatags;
   }

   /**
    * @param string
    */
   public void setVerloopdatumdag(int verloopdatumdag) {
      this.verloopdatumdag = verloopdatumdag;
   }
   
   /**
    * @param string
    */
   public void setVerloopdatummaand(int verloopdatummaand) {
      this.verloopdatummaand = verloopdatummaand;
   }
   
   /**
    * @param string
    */
   public void setVerloopdatumjaar(int verloopdatumjaar) {
      this.verloopdatumjaar = verloopdatumjaar;
   }
   
  
   /**
    * @param string
    */
   public void setEmbargodag(int embargodag) {
      this.embargodag = embargodag;
   }
   
   /**
    * @param string
    */
   public void setEmbargomaand(int embargomaand) {
      this.embargomaand = embargomaand;
   }
   
   /**
    * @param string
    */
   public void setEmbargojaar(int embargojaar) {
      this.embargojaar = embargojaar;
   }
     
   /**
    * @param urlfragment
    */
   public void setUrlfragment(String urlfragment) {
      this.urlfragment = urlfragment;
   }
   
   /**
    * @param string
    */
   public void setNotitie(String notitie) {
      this.notitie = notitie;
   }
   
   /**
    * @param node The node to set.
    */
   public void setNode(String node) {
      this.node = node;
   }
   
   /**
    * @param paginatemplate The paginatemplate to set.
    */
   public void setPaginatemplate(String paginatemplate) {
      this.paginatemplate = paginatemplate;
   }
   
   
   /**
    * @param parent The parent to set.
    */
   public void setParent(String parent) {
      this.parent = parent;
   }
   
   public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
      ActionErrors errors = new ActionErrors();
      if ("".equals(this.getTitel()) || this.getTitel() == null) {
         errors.add("titel",new ActionError("pagina.titel.verplicht"));
      }
      if ("".equals(this.getKortetitel()) || this.getKortetitel() == null) {
         errors.add("kortetitel",new ActionError("pagina.kortetitel.verplicht"));
      }
      if ("".equals(this.getUrlfragment()) || this.getUrlfragment() == null) {
         errors.add("urlfragment",new ActionError("pagina.urlfragment.verplicht"));
      } else {
         if (this.getUrlfragment().indexOf(' ')>=0) {
            errors.add("urlfragment",new ActionError("url.spaties"));
         }
      }
      
      String date = getVerloopdatumdag() + "-" + getVerloopdatummaand() + "-" + getVerloopdatumjaar();
      try {
         DATE_FORMAT.setLenient(false);
         Date tempDate = DATE_FORMAT.parse(date);
         // if we are here the date is valid
         verloopdatum = tempDate.getTime() /1000;
      }
      catch (ParseException e) {
          errors.add("verloopdatum", new ActionError("novalid.verloopdatum"));
      }

      date = getEmbargodag() + "-" + getEmbargomaand() + "-" + getEmbargojaar();
      try {
         DATE_FORMAT.setLenient(false);
         Date tempDate = DATE_FORMAT.parse(date);
         // if we are here the date is valid
         embargo = tempDate.getTime() /1000;
      }
      catch (ParseException e) {
          errors.add("embargo", new ActionError("novalid.embargo"));
      }
      if (errors.size() > 0) {
         request.setAttribute("paginatemplate", getPaginatemplate());
      }
      return errors;
   }   
   
   
   /**
    * @return Returns the parent.
    */
   public String getParent() {
      return this.parent;
   }
   
   /**
    * @return Returns the username.
    */
   public String getUsername() {
      return this.username;
   }

   
   /**
    * @return Returns the node.
    */
   public String getNode() {
      return this.node;
   }


   /**
    * @return
    */
   public String getTitel() {
      return this.titel;
   }

   /**
    * @return
    */
   public String getUrlfragment() {
      return this.urlfragment;
   }

  

   /**
    * @return
    */
   public String getNotitie() {
      return this.notitie;
   }

   /**
    * @return
    */
   public int getEmbargodag() {
      return this.embargodag;
   }
   
   /**
    * @return
    */
   public int getEmbargomaand() {
      return this.embargomaand;
   }
   
   /**
    * @return
    */
   public int getEmbargojaar() {
      return this.embargojaar;
   }

   /**
    * @return
    */
   public String getMetatags() {
      return this.metatags;
   }
   
   /**
    * @return
    */
   public long getEmbargo() {
      return this.embargo;
   }
   
   /**
    * @return
    */
   public long getVerloopdatum() {
      return this.verloopdatum;
   }
   
   /**
    * @return
    */
   public int getVerloopdatumdag() {
      return this.verloopdatumdag;
   }
   
   /**
    * @return
    */
   public int getVerloopdatummaand() {
      return this.verloopdatummaand;
   }
   
   /**
    * @return
    */
   public int getVerloopdatumjaar() {
      return this.verloopdatumjaar;
   }
   
   /**
    * @return
    */
   public String getOmschrijving() {
      return this.omschrijving;
   }
   
   /**
    * @return
    */
   public String getOmschrijving_de() {
      return this.omschrijving_de;
   }

   /**
    * @return
    */
   public String getOmschrijving_eng() {
      return this.omschrijving_eng;
   }

   /**
    * @return
    */
   public String getOmschrijving_fra() {
      return this.omschrijving_fra;
   }

   /**
    * @return
    */
   public String getTitel_de() {
      return this.titel_de;
   }

   /**
    * @return
    */
   public String getTitel_eng() {
      return this.titel_eng;
   }

   /**
    * @return
    */
   public String getTitel_fra() {
      return this.titel_fra;
   }
   
   /**
    * @return
    */
   public String getKortetitel() {
      return this.kortetitel;
   }
   
   /**
    * @return
    */
   public String getKortetitel_fra() {
      return this.kortetitel_fra;
   }
   
   /**
    * @return
    */
   public String getKortetitel_eng() {
      return this.kortetitel_eng;
   }
   
   /**
    * @return
    */
   public String getKortetitel_de() {
      return this.kortetitel_de;
   }
   
   /**
    * @return
    */
   public String getPaginatemplate() {
      return this.paginatemplate;
   }
}

