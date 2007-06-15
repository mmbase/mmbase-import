package nl.leocms.vastgoed;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionError;

import javax.servlet.http.HttpServletRequest;

/**
 * @author
 * @version $Id: BestelForm.java,v 1.4 2007-06-15 13:08:08 ieozden Exp $
 *
 * @struts:form name="BestelForm"
 */

public class BestelForm extends ActionForm{
   
   private String naam;
   private String email;
   private String eendheid;
   private String bezorgadres;
   
   
   public String getEendheid() {
      return eendheid;
   }
   public void setEendheid(String eendheid) {
      this.eendheid = eendheid;
   }
   public String getEmail() {
      return email;
   }
   public void setEmail(String email) {
      this.email = email;
   }
   public String getNaam() {
      return naam;
   }
   public void setNaam(String naam) {
      this.naam = naam;
   }
   public String getBezorgadres() {
      return bezorgadres;
   }
   public void setBezorgadres(String bezorgadres) {
      this.bezorgadres = bezorgadres;
   }
   
   //INTERVENES WITH THE DELETE ACTION
//   public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
//       if (naam == null || naam.length() <1) {
//           ActionErrors errors = new ActionErrors();
//           errors.add("naam", new ActionError("mijnleocms.required.name"));
//           return errors;
//       }
//       if (email == null || email.length() <1) {
//           ActionErrors errors = new ActionErrors();
//           errors.add("email", new ActionError("mijnleocms.required.email"));
//           return errors;
//       }
//       return null;
//   }
   
}
