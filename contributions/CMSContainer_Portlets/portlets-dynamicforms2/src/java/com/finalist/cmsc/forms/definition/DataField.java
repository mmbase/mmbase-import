package com.finalist.cmsc.forms.definition;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import com.finalist.cmsc.forms.optionproviders.OptionProvider;
import com.finalist.cmsc.forms.validation.FieldValidator;
import com.finalist.cmsc.forms.validation.basic.RegexValidator;
import com.finalist.cmsc.forms.validation.basic.RequiredValidator;
import com.finalist.cmsc.util.XmlUtil;

public final class DataField {

   private String title;
   private String datapattern;
   private DataType datatype;
   private double max = Double.MAX_VALUE;
   private long maxlength = Long.MAX_VALUE;
   private double min = Double.MIN_VALUE;
   private long minlength = 0;
   private String name;
   private List<Option> options = new ArrayList<Option>();

   private boolean required;
   private List<FieldValidator> validators = new ArrayList<FieldValidator>();

   public void addOption(Option option) {
      options.add(option);
   }

   public void addOptionProvider(OptionProvider provider) {
      provider.fillOptions(options);
   }

   private void addValidator(FieldValidator validator) {
      validators.add(validator);
   }

   @Override
   public boolean equals(Object oth) {
      if (this == oth) {
         return true;
      }

      if (oth == null) {
         return false;
      }

      if (oth.getClass() != getClass()) {
         return false;
      }

      DataField other = (DataField) oth;
      if (this.name == null) {
         if (other.name != null) {
            return false;
         }
      }
      else {
         if (!this.name.equals(other.name)) {
            return false;
         }
      }
      return true;
   }

   public String getDatapattern() {
      return datapattern;
   }
   
   public double getMax() {
      return max;
   }

   public long getMaxlength() {
      return maxlength;
   }

   public double getMin() {
      return min;
   }

   public long getMinlength() {
      return minlength;
   }

   public String getName() {
      return name;
   }

   public List<Option> getOptions() {
      return options;
   }
   

   public DataType getType() {
      return datatype;
   }

   public String getTitle() {
      return title;
   }
   
   public List<FieldValidator> getValidators() {
      return validators;
   }

   @Override
   public int hashCode() {
      final int PRIME = 1000003;
      int result = 0;
      if (name != null) {
         result = PRIME * result + name.hashCode();
      }
      return result;
   }

   public boolean isRequired() {
      return required;
   }

   public void setDatapattern(String datapattern) {
      this.datapattern = datapattern;
      RegexValidator validator = new RegexValidator(datapattern);
      addValidator(validator);
   }

   public void setDatatype(String datatype) {
      this.datatype = DataType.valueOf(datatype);
      FieldValidator typeValidator = getType().getValidator();
      if (typeValidator != null) {
         addValidator(typeValidator);
      }
   }

   public void setMax(double max) {
      this.max = max;
   }

   public void setMaxlength(long maxlength) {
      this.maxlength = maxlength;
   }

   public void setMin(double min) {
      this.min = min;
   }


   public void setMinlength(long minlength) {
      this.minlength = minlength;
   }


   public void setName(String name) {
      this.name = name;
   }


   public void setRequired(boolean required) {
      this.required = required;
      if (required) {
         addValidator(new RequiredValidator());
      }
   }

   public void setTitle(String title) {
      this.title = title;
   }
   
   /**
    * @param field
    */
   public void toXml(Element field) {
      XmlUtil.createAttribute(field, "datatype", datatype.toString());
      XmlUtil.createAttribute(field, "required", required);
      XmlUtil.createAttribute(field, "min", min);
      XmlUtil.createAttribute(field, "max", max);
      XmlUtil.createAttribute(field, "minlength", minlength);
      XmlUtil.createAttribute(field, "maxlength", maxlength);
      XmlUtil.createAttribute(field, "pattern", datapattern);

      if (!options.isEmpty()) {
         Element optionlist = XmlUtil.createChild(field, "optionlist");
         for (Option option : options) {
            Element optionElement = XmlUtil.createChild(optionlist, "option");
            XmlUtil.createAttribute(optionElement, "value", option.value);
            XmlUtil.createText(optionElement, option.description);
         }
      }
   }

   public void portLoad() {
      // nothing yet
   }

}
