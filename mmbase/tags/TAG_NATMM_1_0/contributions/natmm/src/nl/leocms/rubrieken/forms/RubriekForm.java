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
package nl.leocms.rubrieken.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionError;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;
/**
 * @author Edwin van der Elst Date :Oct 17, 2003
 *
 * @struts:form name="RubriekForm"
 */
public class RubriekForm extends ActionForm {
  
   private static final Logger log = Logging.getLoggerInstance(RubriekForm.class);
  
   private String parent;
   private String naam;
   private String naam_fra;
   private String naam_eng;
   private String naam_de;
   private String url;
   private String style;
   private String node;
   private String url_live;
   private String is_visible;
   private String issearchable;
   private int level;
   private boolean fra_active;
   private boolean eng_active;
   private boolean de_active;
   private boolean wholesubsite;

   /**
    * @return Returns the level.
    */
   public int getLevel() {
      return level;
   }

   /**
    * @param level The level to set.
    */
   public void setLevel(int level) {
      this.level = level;
   }

   /**
    * @return Returns the url_live.
    */
   public String getUrl_live() {
      return url_live;
   }

   /**
    * @param url_live The url
    */
   public void setUrl_live(String url_live) {
      this.url_live = url_live;
   }

   /**
    * @return Returns the naam.
    */
   public String getNaam() {
      return naam;
   }

   /**
    * @param naam The naam to set.
    */
   public void setNaam(String naam) {
      this.naam = naam;
   }

   /**
    * @return Returns the naam_de.
    */
   public String getNaam_de() {
      return naam_de;
   }

   /**
    * @param naam_de The naam_de to set.
    */
   public void setNaam_de(String naam_de) {
      this.naam_de = naam_de;
   }

   /**
    * @return Returns the naam_eng.
    */
   public String getNaam_eng() {
      return naam_eng;
   }

   /**
    * @param naam_eng The naam_eng to set.
    */
   public void setNaam_eng(String naam_eng) {
      this.naam_eng = naam_eng;
   }

   /**
    * @return Returns the naam_fra.
    */
   public String getNaam_fra() {
      return naam_fra;
   }

   /**
    * @param naam_fra The naam_fra to set.
    */
   public void setNaam_fra(String naam_fra) {
      this.naam_fra = naam_fra;
   }

   /**
    * @return Returns the parent.
    */
   public String getParent() {
      return parent;
   }

   /**
    * @param parent The parent to set.
    */
   public void setParent(String parent) {
      this.parent = parent;
   }

   /**
    * @return Returns the url.
    */
   public String getUrl() {
      return url;
   }

   /**
    * @param url The url to set.
    */
   public void setUrl(String url) {
      this.url = url;
   }
   
    /**
    * @return Returns the is_visible.
    */
   public String getIs_visible() {
      return is_visible;
   }

   /**
    * @param is_visible Whether or not this rubriek is visible
    */
   public void setIs_visible(String is_visible) {
      this.is_visible = is_visible;
   }


  /**
    * @return Returns the issearchable.
    */
   public String getIssearchable() {
      return issearchable;
   }

   /**
    * @param issearchable Whether or not this rubriek has searchable content
    */
   public void setIssearchable(String issearchable) {
      this.issearchable = issearchable;
   }

   /**
    * @return Returns the style.
    */
   public String getStyle() {
      return style;
   }

   /**
    * @param url The style to set.
    */
   public void setStyle(String style) {
      this.style = style;
   }

   /**
    * @return Returns the node.
    */
   public String getNode() {
      return node;
   }

   /**
    * @param node The node to set.
    */
   public void setNode(String node) {
      this.node = node;
   }

   public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
      ActionErrors errors = new ActionErrors();
      if ("".equals(this.getNaam()) || this.getNaam()==null) {
         errors.add("naam",new ActionError("rubrieken.naam.verplicht"));
      }
      if (this.getUrl()==null && this.getUrl().indexOf(' ')>=0) {
        errors.add("url",new ActionError("url.spaties"));
      }
      // hh: url is only used to create Google sitemaps now, so it is not required
      // if ("".equals(this.getUrl()) || this.getUrl()==null) {
      //   errors.add("url",new ActionError("rubrieken.url.verplicht"));
      // }
      return errors;
   }

   /**
    * Getter for property fra_active.
    * @return property fra_active.
    */
   public boolean isfra_active() {
      return fra_active;
   }

   /**
    * Setter for property fra_active.
    * @param fra_active property fra_active.
    */
   public void setfra_active(boolean fra_active) {
      this.fra_active = fra_active;
   }

   /**
    * Getter for property eng_active.
    * @return eng_active property eng_active.
    */
   public boolean isEng_active() {
      return eng_active;
   }

   /**
    * Setter for property eng_active.
    * @param eng_active property eng_active.
    */
   public void setEng_active(boolean eng_active) {
      this.eng_active = eng_active;
   }

   /**
    * Getter for property de_active.
    * @return property de_active.
    */
   public boolean isDe_active() {
      return de_active;
   }

   /**
    * Setter for property de_active.
    * @param de_active property de_active.
    */
   public void setDe_active(boolean de_active) {
      this.de_active = de_active;
   }

   /**
    * Getter for property wholesubsite.
    * @return property wholesubsite.
    */
   public boolean isWholesubsite() {
      return wholesubsite;
   }

   /**
    * Setter for property wholesubsite.
    * @param wholesubsite property wholesubsite.
    */
   public void setWholesubsite(boolean wholesubsite) {
      this.wholesubsite = wholesubsite;
   }
}

/**
 * $Log: not supported by cvs2svn $
 * Revision 1.4  2006/09/04 18:51:00  henk
 * Added issearchable
 *
 * Revision 1.3  2006/08/11 06:23:02  henk
 * Removed uppercase from getter/setter (does not work well with Struts)
 *
 * Revision 1.2  2006/08/10 14:23:00  henk
 * Added rubriek.isvisible
 *
 * Revision 1.1  2006/03/05 21:43:58  henk
 * First version of the NatMM contribution.
 *
 * Revision 1.8  2004/01/30 13:01:33  jeoffrey
 * fix for jira bug LEEUW-365
 *
 * Revision 1.7  2004/01/20 08:47:38  jeoffrey
 * rfc meertaligheid
 *
 * Revision 1.6  2003/11/28 13:01:21  edwin
 * check op spaties in url-fragment
 *
 */
