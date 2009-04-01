package com.finalist.cmsc.forms.definition;

import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.finalist.cmsc.forms.Localization;
import com.finalist.cmsc.forms.validation.FieldValidator;
import com.finalist.cmsc.forms.validation.ValidationError;
import com.finalist.cmsc.forms.value.*;
import com.finalist.cmsc.util.XmlUtil;

public final class GuiField {

   private final static Log Log = LogFactory.getLog(GuiField.class);

   private int cols = -1;
   private DataField dataField;
   private GuiDescription description;
   private String guiclass;
   private String guitype;
   private String name;
   private int rows = -1;
   private String title;
   private String tooltip;
   private String tooltipname;

   private List<FieldValidator> validators = new ArrayList<FieldValidator>();

   public void addValidator(FieldValidator validator) {
      validators.add(validator);
   }

   private void addValueObject(Element field, ValueObject object) {
      ValueField valueField = object.getField(dataField.getName());
      if (valueField != null) {
         if (valueField.getValue() == null || !valueField.getValue().getClass().isArray()) {
            String stringValue = valueField.getStringValue();
            if (stringValue == null && ("text".equals(guitype) || "html".equals(guitype))) {
               stringValue = "";
            }
            XmlUtil.createAttribute(field, "value", stringValue);

            if (!dataField.getOptions().isEmpty()) {
               Element optionlist = (Element) field.getElementsByTagName("optionlist").item(0);
               if (field.getElementsByTagName("option") != null && valueField.getValue() != null) {
                  Element optionElement = getOptionWithValue(optionlist, stringValue);
                  if (optionElement != null)
                     XmlUtil.createAttribute(optionElement, "selected", "true");
               }
            }
         }
         else {
            if (!dataField.getOptions().isEmpty()) {
               String[] values = (String[]) valueField.getValue();
               Element optionlist = (Element) field.getElementsByTagName("optionlist").item(0);
               for (String value : values) {
                  Element optionElement = getOptionWithValue(optionlist, value);
                  if (optionElement != null)
                     XmlUtil.createAttribute(optionElement, "selected", "true");
               }
            }
         }

         ValidationError validationError = valueField.getValidationError();
         if (validationError != null) {
            ResourceBundle bundle = Localization.getResourceBundle();
            XmlUtil.createAttribute(field, "error", validationError.getErrorMessage(bundle));
         }
      }
   }

   public void postLoad(DataObject dataObject) {
      setDataField(dataObject.getField(name));
   }

   public int getCols() {
      return cols;
   }

   public DataField getDataField() {
      return dataField;
   }

   public String getDatapattern() {
      return dataField.getDatapattern();
   }

   public DataType getDatatype() {
      return dataField.getType();
   }

   public GuiDescription getDescription() {
      return description;
   }

   public String getGuiclass() {
      return guiclass;
   }

   public String getGuitype() {
      return guitype;
   }

   public double getMax() {
      return dataField.getMax();
   }

   public long getMaxlength() {
      return dataField.getMaxlength();
   }

   public double getMin() {
      return dataField.getMin();
   }

   public long getMinlength() {
      return dataField.getMinlength();
   }

   public String getName() {
      return dataField.getName();
   }

   private Element getOptionWithValue(Element optionlist, String value) {
      NodeList options = optionlist.getElementsByTagName("option");
      for (int i = 0; i < options.getLength(); i++) {
         String valuetje = ((Element) options.item(i)).getAttribute("value");
         if (value.equals(valuetje)) {
            return (Element) options.item(i);
         }
      }
      return null;
   }

   public int getRows() {
      return rows;
   }

   public String getTitle() {
      return title;
   }

   public String getTooltip() {
      return tooltip;
   }


   public String getTooltipname() {
      return tooltipname;
   }

   public boolean isRequired() {
      return dataField.isRequired();
   }

   public boolean isValid(ValueObject object) {
      ValueField valueField = object.getField(getName());
      if (valueField == null) {
         Log.error("Object (" + object.getName() + ") does not contain field :" + getName()
               + " - unable to validate object.");
         return false;
      }
      if (valueField.getValidationError() != null) return false;
      if (!valueField.isRequired() && StringUtils.isEmpty(valueField.getStringValue())) return true;

      for (FieldValidator validator : validators) {
         if (!validator.validate(valueField)) {
            ValidationError errorMsg = validator.getErrorMessage(valueField);
            valueField.setValidationError(errorMsg);
            Log.debug("Validation error for field '" + valueField.getName() + "': " + errorMsg);
            return false;
         }
      }
      valueField.setValidationError(null);
      return true;
   }

   public void render(Element root, ValueObject object, String namePath) {
      Element field = toXml(root);
      if (description != null) {
         description.render(field);
      }
      namePath = ValuePathUtil.createNamePath(namePath, dataField.getName());
      XmlUtil.createAttribute(field, "name", namePath);
      addValueObject(field, object);
   }

   public void setCols(int cols) {
      this.cols = cols;
   }

   public void setDataField(DataField dataField) {
      this.dataField = dataField;
      validators.addAll(dataField.getValidators());
   }

   public void setDescription(GuiDescription description) {
      this.description = description;
   }

   public void setGuiclass(String guiclass) {
      this.guiclass = guiclass;
   }

   public void setGuitype(String guitype) {
      this.guitype = guitype;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setRows(int rows) {
      this.rows = rows;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public void setTooltip(String tooltip) {
      this.tooltip = tooltip;
   }

   public void setTooltipname(String tooltipname) {
      this.tooltipname = tooltipname;
   }

   private Element toXml(Element root) {
      Element field = XmlUtil.createChild(root, "field");
      dataField.toXml(field);

      XmlUtil.createAttribute(field, "title", title);
      XmlUtil.createAttribute(field, "guitype", guitype);
      if (StringUtils.isNotBlank(guiclass)) {
         XmlUtil.createAttribute(field, "class", guiclass);
      }
      if (rows > 0) XmlUtil.createChildText(field, "rows", rows);
      if (cols > 0)XmlUtil.createChildText(field, "cols", cols);

      if (StringUtils.isNotBlank(tooltip)) {
         Element tooltipElement = XmlUtil.createChild(field, "tooltip");
         XmlUtil.createAttribute(tooltipElement, "name", tooltipname);
         XmlUtil.createText(tooltipElement, tooltip);
      }

      return field;
   }

}
