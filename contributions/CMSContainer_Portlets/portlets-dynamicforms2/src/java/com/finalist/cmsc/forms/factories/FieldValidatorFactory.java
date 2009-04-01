/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.forms.factories;

import org.xml.sax.Attributes;

import com.finalist.cmsc.forms.validation.FieldValidator;


public class FieldValidatorFactory extends AbstractFormCreationFactory {

   public FieldValidatorFactory(){
      super("fieldvalidators");
   }
   
   @Override
   public Object createObject(Attributes attributes) throws Exception {
      FieldValidator object = (FieldValidator) super.createObject(attributes);
      
      String message = attributes.getValue("message");
      object.setErrorMessage(message);
      return object;
   }
}
