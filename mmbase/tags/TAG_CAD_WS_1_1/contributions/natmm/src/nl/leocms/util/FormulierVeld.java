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
package nl.leocms.util;

/*
 * This class holds certain data of a formulierveld
 * @author Ronald Kramp
 */
public class FormulierVeld {
   private Integer fieldType;
   private String label;
   private Integer verplicht;
   
   /** contructor */
   public FormulierVeld(Integer fieldType, String label, Integer verplicht) {
      this.fieldType = fieldType;
      this.label = label;
      this.verplicht = verplicht;
   }
   
   /** 
    * Returns the field type as an Integer
    * 1=file
    * 2= 
    * 3=
    * 4=
    * 5=
    * 6=
    * The field will be used in an html form
    * @return the fieldtype as Integer
    */
   public Integer getFieldType() {
      return this.fieldType;
   }
   
   /** 
    * Returns the label/text for the field
    * @return the text for the field
    */
   public String getLabel() {
      return this.label;
   }
   
   /** 
    * Returns true or false if the field is mandatory
    * @return true or false
    */
   public boolean isVerplicht() {
      return (this.verplicht.intValue() == 1);
   }
}