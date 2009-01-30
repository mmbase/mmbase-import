package nl.leocms.vastgoed;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.struts.action.ActionForm;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

import com.sun.xml.bind.RIElement;

/**
 * @author
 * @version $Id: KaartenForm.java,v 1.12 2007-08-02 09:51:57 evdberg Exp $
 *
 * @struts:form name="KaartenForm"
 */

public class KaartenForm extends ActionForm{
   private static final Logger log = Logging.getLoggerInstance(KaartenForm.class);
   
   private String aantal;
   private String rad_Schaal;
   private String schaal;
   private String formaat;
   private String rad_Gevouwen;
   private String[] sel_Kaart;
   private String rad_Gebied;
   private String sel_Beheereenheden;
   private String[] sel_NatGeb;
   private String sel_gebieden;
   private String[] sel_Areaal;
   private String linksX;
   private String linksY;
   private String rechtsX;
   private String rechtsY;
   private String opmerkingen;
   //data structures
   private Map natGebMap;
   private Map gebiedMap;
   private Map selKaartMap;
   // constants
   // these keys should be same in jsp templates and java forms and classes
   private static String natuurgebiedKey = "Natuurgebied";
   private static String eenheidKey = "Eenheid";
   private static String nederlandKey = "Nederland";
   private static String coordinatenKey = "Coordinaten";
   
   public KaartenForm() {
      //init for first load
      aantal="1";
      rad_Schaal = "schaal";
      schaal = "1:5.000";
      formaat = "A4";
      rad_Gevouwen = "gevouwen";
      rad_Gebied = natuurgebiedKey;
      //
      resetMaps();
   }
   
   
   
   //this is to use private values to set booleans in maps. making ready for jsp
   public void updateMapValues(){
      resetMaps();
      
      //
      if (rad_Gebied.equals(natuurgebiedKey)) {
         try {
            Map targetMap = (TreeMap) natGebMap.get(sel_Beheereenheden);
            for(int i = 0; i < sel_NatGeb.length; i++) {
               Set keySet = targetMap.keySet();
               Iterator keysIterator = keySet.iterator();
               if (targetMap.containsKey(sel_NatGeb[i])) {
                  targetMap.put(sel_NatGeb[i], new Boolean(true));
               }
            }
         } catch (Exception e) {
            log.info("updateMapValues - no entry for key: " + sel_Beheereenheden);
         }
      }
      
      if (rad_Gebied.equals(eenheidKey)) {
         try {
            Map targetMap = (TreeMap) gebiedMap.get(sel_gebieden);
            for(int i = 0; i < sel_Areaal.length; i++) {
               Set keySet = targetMap.keySet();
               Iterator keysIterator = keySet.iterator();
               if (targetMap.containsKey(sel_Areaal[i])) {
                  targetMap.put(sel_Areaal[i], new Boolean(true));
               }
            }
         } catch (Exception e) {
            log.debug("updateMapValues - no entry for key: " + sel_gebieden);
         }
      }
      
      if (!rad_Gebied.equals(coordinatenKey)) {
         linksX = "";
         linksY = "";
         rechtsX = "";
         rechtsY = "";
      }
      
      // the kart type selection map to be filled with values from last for submit. under the corresponding kart type key
      if (sel_Kaart != null) {
         for(int i = 0; i < sel_Kaart.length; i++) {
            String kartType = sel_Kaart[i];
            if (rad_Gebied.equals(natuurgebiedKey)) {
               ((ArrayList) selKaartMap.get(natuurgebiedKey)).add(kartType);
            }
            if (rad_Gebied.equals(eenheidKey)) {
               ((ArrayList) selKaartMap.get(eenheidKey)).add(kartType);
            }
            if (rad_Gebied.equals(nederlandKey)) {
               ((ArrayList) selKaartMap.get(nederlandKey)).add(kartType);
            }
            if (rad_Gebied.equals(coordinatenKey)) {
               ((ArrayList) selKaartMap.get(coordinatenKey)).add(kartType);
            }
         }
      }
   }
   
   
   // Shopping Cart Getters
   
   public String getAantal() {
      return this.aantal;
   }
   
   public String getSchaalOfFormaat() {
      if ("schaal".equals(rad_Schaal)) {
         return this.schaal;
      } else if ("formaat".equals(rad_Schaal)) {
         return this.formaat;
      } else {return "unknown format";}
   }
   
   public String getGevouwenOfOpgerold() {
      return this.rad_Gevouwen;
   }
   
   //sel_Kaart will contain node numbers not kart names so has to be directly retrieved in jsp and processed with mm tags
//	public String getKaartSoort() {
//		String kaartSoort = "";
//		for(int i=0; ((sel_Kaart != null) && (i<sel_Kaart.length)); i++) {
//			kaartSoort += (i!=0) ? ", " + sel_Kaart[i] :  sel_Kaart[i];
//		}
//		return kaartSoort;
//	}
   
   //kart type for jsp forms
   public String getKaartType() {
      String kartType  = rad_Gebied;
      if (kartType == null) {
         return "";
      }
      if (kartType.equals(natuurgebiedKey)) {
         kartType += "";
      } else if (kartType.equals(eenheidKey)) {
         kartType += "";
      } else if (kartType.equals(coordinatenKey)) {
         kartType += " (" + linksX + ":" + linksY + " " + rechtsX + ":" + rechtsY + ")";
      }
      return kartType;
   }
   
   //kart type detail string for email
   public String getKaartTypeDetail() {
      String kartTypeDetail  = "";
      if (rad_Gebied.equals(natuurgebiedKey)) {
         kartTypeDetail += sel_Beheereenheden + " (";
         for(int i=0; (sel_NatGeb!= null) && (i<sel_NatGeb.length) ; i++) {
            kartTypeDetail += sel_NatGeb[i];
            if (i != sel_NatGeb.length -1) {
               kartTypeDetail += ", ";
            }
         }
         kartTypeDetail += ")";
      }
      if (rad_Gebied.equals(eenheidKey)) {
         kartTypeDetail += sel_gebieden + "(";
         for(int i=0; (sel_Areaal!= null) && (i<sel_Areaal.length) ; i++) {
            kartTypeDetail += sel_Areaal[i];
            if (i != sel_Areaal.length -1) {
               kartTypeDetail += ", ";
            }
         }
         kartTypeDetail += ")";
      }
      
      return kartTypeDetail;
      
   }
   
   // getters and setters
   
   public String getFormaat() {
      return formaat;
   }
   
   public void setFormaat(String formaat) {
      this.formaat = formaat;
   }
   
   public String getRad_Gevouwen() {
      return rad_Gevouwen;
   }
   
   public void setRad_Gevouwen(String rad_Gevouwen) {
      this.rad_Gevouwen = rad_Gevouwen;
   }
   
   public String getRad_Schaal() {
      return rad_Schaal;
   }
   
   public void setRad_Schaal(String rad_Schaal) {
      this.rad_Schaal = rad_Schaal;
   }
   
   public String getSchaal() {
      return schaal;
   }
   
   public void setSchaal(String schaal) {
      this.schaal = schaal;
   }
   
   public String[] getSel_Kaart() {
      return sel_Kaart;
   }
   
   public void setSel_Kaart(String[] sel_Kaart) {
      this.sel_Kaart = sel_Kaart;
   }
   
   public void setAantal(String aantal) {
      this.aantal = aantal;
   }
   
   public String getLinksX() {
      return linksX;
   }
   
   public void setLinksX(String linksX) {
      this.linksX = linksX;
   }
   
   public String getLinksY() {
      return linksY;
   }
   
   public void setLinksY(String linksY) {
      this.linksY = linksY;
   }
   
   public String getRad_Gebied() {
      return rad_Gebied;
   }
   
   public void setRad_Gebied(String rad_Gebied) {
      this.rad_Gebied = rad_Gebied;
   }
   
   public String getRechtsX() {
      return rechtsX;
   }
   
   public void setRechtsX(String rechtsX) {
      this.rechtsX = rechtsX;
   }
   
   public String getRechtsY() {
      return rechtsY;
   }
   
   public void setRechtsY(String rechtsY) {
      this.rechtsY = rechtsY;
   }
   
   public String[] getSel_Areaal() {
      return sel_Areaal;
   }
   
   public void setSel_Areaal(String[] sel_Areaal) {
      this.sel_Areaal = sel_Areaal;
   }
   
   public String getSel_Beheereenheden() {
      return sel_Beheereenheden;
   }
   
   public void setSel_Beheereenheden(String sel_Beheereenheden) {
      this.sel_Beheereenheden = sel_Beheereenheden;
   }
   
   public String getSel_gebieden() {
      return sel_gebieden;
   }
   
   public void setSel_gebieden(String sel_gebieden) {
      this.sel_gebieden = sel_gebieden;
   }
   
   public String[] getSel_NatGeb() {
      return sel_NatGeb;
   }
   
   public void setSel_NatGeb(String[] sel_NatGeb) {
      this.sel_NatGeb = sel_NatGeb;
   }
   
   // copying values from another KaartenForm object
   public void copyValuesFrom(KaartenForm copyForm) {
      this.aantal = copyForm.getAantal();
      this.rad_Schaal = copyForm.getRad_Schaal();
      this.schaal = copyForm.getSchaal();
      this.formaat = copyForm.getFormaat();
      this.rad_Gevouwen = copyForm.getRad_Gevouwen();
      this.sel_Kaart = copyForm.getSel_Kaart();
      this.rad_Gebied = copyForm.getRad_Gebied();
      this.sel_Beheereenheden = copyForm.getSel_Beheereenheden();
      this.sel_NatGeb = copyForm.getSel_NatGeb();
      this.sel_gebieden = copyForm.getSel_gebieden();
      this.sel_Areaal = copyForm.getSel_Areaal();
      this.linksX = copyForm.getLinksX();
      this.linksY = copyForm.getLinksY();
      this.rechtsX = copyForm.getRechtsX();
      this.rechtsY = copyForm.getRechtsY();
      this.opmerkingen = copyForm.getOpmerkingen();
   }
   
   private void resetMaps() {
      NelisReader nelis = NelisReader.getInstance();
      natGebMap = nelis.getNatGebMap();
      gebiedMap = nelis.getGebiedMap();
      
      //kaart type
      selKaartMap = new TreeMap();
      selKaartMap.put(natuurgebiedKey, new ArrayList());
      selKaartMap.put(eenheidKey, new ArrayList());
      selKaartMap.put(nederlandKey, new ArrayList());
      selKaartMap.put(coordinatenKey, new ArrayList());
      
   }
   
   public Set getGebiedList() {
      return natGebMap.keySet();
   }
   
   public Map getNatGebMap() {
      return natGebMap;
   }
   
   public Map getGebiedMap() {
      return gebiedMap;
   }
   
   
   
   public String getOpmerkingen() {
      return opmerkingen;
   }
   
   
   
   public void setOpmerkingen(String opmerkingen) {
      this.opmerkingen = opmerkingen;
   }
   
   
   
   public Map getSelKaartMap() {
      return selKaartMap;
   }
   
   
   
   public void setGebiedMap(Map gebiedMap) {
      this.gebiedMap = gebiedMap;
   }
}
