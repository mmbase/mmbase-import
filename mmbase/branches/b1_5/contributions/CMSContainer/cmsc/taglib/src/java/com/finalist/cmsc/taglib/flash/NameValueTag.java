package com.finalist.cmsc.taglib.flash;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * Base class for name/value based tag. It allows you to to use one of the two
 * methods to specify a name and a value:
 * <ol>
 * <li>Specify a name attribute, specify a value attribute and have an empty
 * (or closed) body.</li>
 * <li>Specify a name attribute and specify the value in the body of the tag.
 * In this case the value attribute should not be specified.</li>
 * </ol>
 *
 * @author Auke van Leeuwen
 */
public abstract class NameValueTag extends SimpleTagSupport {
   private String name;
   private String value;

   /**
    * Sets the value of 'name' to either the value of the name parameter or the
    * value of the body of this tag. Throws a {@link JspException} when both or
    * neither is supplied.
    *
    * @throws JspException
    *            If the value attribute is supplied when the body is not empty,
    *            or when the value attribute is not supplied and the body is
    *            empty as well.
    */
   @Override
   public void doTag() throws JspException, IOException {
      JspFragment jspBody = getJspBody();
      if (jspBody != null) {
         if (getValue() != null) {
            throw new JspException("You can't supply both a value attribute and a non-empty body.");
         }

         StringWriter bodyWriter = new StringWriter();
         jspBody.invoke(bodyWriter);
         setValue(bodyWriter.toString());
      } else if (getValue() == null) {
         throw new JspException("You must supply either a value or a non-empty body for this tag.");
      }
      // the other case is simply handled by the setValue() when the tag is
      // invoked.
   }

   /**
    * Holds the map of allowed values.
    *
    * @see NameValueTag#initAllowedValues()
    */
   protected final Map<String, List<String>> allowedValues;

   /**
    * Creates a new NameValueTag.
    */
   protected NameValueTag() {
      super();
      allowedValues = initAllowedValues();
   }

   /**
    * Constructs and returns a map of allowed string values for a given name.
    * That is: if a boolean attribute can only hold the values true or false
    * this map should say so. Something like this:
    *
    * 'booleanAttributeName' -&gt; {true, false}
    *
    * The value check (against the list of Strings) will be case insensitive and
    * the key attribute should be stored in lowercase.
    *
    * There are a few special cases: If <code>null</code> is returned this
    * indicates no checking at all, thus everything is allowed. A null value for
    * a specific key means this value does not a have a fixed set of String
    * values that is allowed, thus any values goes.
    *
    * @return the map with allowed values for all parameters.
    */
   protected abstract Map<String, List<String>> initAllowedValues();

   /**
    * Check whether a certain value is a valid value based on the allowed values
    * that were previously initialized.
    *
    * @param name
    *           the name of the pair.
    * @param value
    *           the value of the pair
    * @return <code>true</code> if this is a valid value (case ignored) for
    *         this name, <code>false</code> otherwise.
    *
    */
   protected boolean isAllowedValue(String name, String value) {
      if (allowedValues == null) {
         return true; // no restrictions
      }

      List<String> values = allowedValues.get(name.toLowerCase());

      boolean result = false;
      if (values != null) {
         for (String v : values) {
            if (v != null) {
               result = result || v.equalsIgnoreCase(value);
            }
         }
      } else {
         result = true; // uncheckable value, everything goes
      }

      return result;
   }

   /**
    * Returns the name.
    *
    * @return the name
    */
   public String getName() {
      return name;
   }

   /**
    * Sets the name to the specified value.
    *
    * @param name
    *           the name to set
    */
   public void setName(String name) {
      this.name = name;
   }

   /**
    * Returns the value.
    *
    * @return the value
    */
   public String getValue() {
      return value;
   }

   /**
    * Sets the value to the specified value.
    *
    * @param value
    *           the value to set
    */
   public void setValue(String value) {
      this.value = value;
   }
}
