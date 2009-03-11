package com.finalist.cmsc.taglib.flash;

import static org.apache.commons.lang.StringEscapeUtils.escapeJavaScript;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Writes all the needed HTML/JS for a flash movie (*.swf) using <a
 * href="http://code.google.com/p/swfobject/">SWFObject</a>. This uses
 * javascript - by design - to make sure that the content of the SWF is already
 * activated in Internet Explorer and Opera. It also provides us with a way to
 * provide alternative content in case flash is not available.
 *
 * @author Auke van Leeuwen
 */
public class FlashTag extends SimpleTagSupport {
   private final static Log log = LogFactory.getLog(FlashTag.class);

   /** Holds the flashvars */
   private Map<String, String> flashVars = new HashMap<String, String>();

   /**
    * Holds the flash params. See also:
    * (http://www.adobe.com/cfusion/knowledgebase/index.cfm?id=tn_12701)
    */
   private Map<String, String> params = new HashMap<String, String>();

   // REQUIRED ATTRIBUTES
   private String swfUrl;
   private String width;
   private String height;

   // OPTIONAL ATTRIBUTES
   private String version = "9.0.0";
   private String expressInstallSwfUrl;
   private String id;
   private String name;
   private String styleClass;
   private String align;

   /** {@inheritDoc} */
   @Override
   public void doTag() throws JspException, IOException {
      JspWriter out = getJspContext().getOut();

      out.print(constructAlternativeContent());
      out.print(constructJavaScript());
   }

   /**
    * Adds a flashvar - name/value pair - to the map of flashvars. Multiple
    * calls with the same flashvar name will result in a comma separated list of
    * values for this variable name. A <code>null</code> value or empty string
    * for name will not be stored as a flashvar.
    *
    * The parameters added are URLEncoded using the encoding scheme given.
    *
    * @param name
    *           the name of the flashvar
    * @param value
    *           the value of the flashvar
    * @param urlEncode
    *           boolean determining whether or not to use urlEncoding to store
    * @param urlEncodingScheme
    *           the urlEncoding scheme to use when encoding this flashvar
    * @throws UnsupportedEncodingException
    *            if the encoding scheme is not supported.
    */
   protected void addFlashvar(String name, String value, boolean urlEncode, String urlEncodingScheme) throws UnsupportedEncodingException {
      String encodedValue = (value == null ? "" : value);

      if (urlEncode) {
         encodedValue = URLEncoder.encode(encodedValue, urlEncodingScheme);
      }

      if ((name != null) && (name.length() > 0)) {
         if (flashVars.containsKey(name)) {
            flashVars.put(name, String.format("%s,%s", flashVars.get(name), encodedValue));
         } else {
            flashVars.put(name, encodedValue);
         }
      } else {
         log.warn(String.format("Ignoring flashvar with empty name (value: '%s')", value));
      }
   }

   /**
    * Adds a flash param - name/value pair - to the map of flash parameters.
    * Multiple calls with the same parameter name will result in overriding the
    * earlier value. A <code>null</code> value or empty string for name will
    * not be stored as a parameter.
    *
    * @param name
    *           the name of the param
    * @param value
    *           the value of the param
    */
   protected void addParam(String name, String value) {
      if ((name != null) && (name.length() > 0)) {
         params.put(name, value);
      } else {
         log.warn(String.format("Ignoring param with empty name (value: '%s')", value));
      }
   }

   private String constructAlternativeContent() throws JspException, IOException {
      // output the alternative content
      StringBuilder builder = new StringBuilder();

      builder.append("<div");
      builder.append(" id=\"").append(getId()).append("\"");
      builder.append(" class=\"flashcontent\"");
      builder.append(">");
      JspFragment jspBody = getJspBody();
      if (jspBody != null) {
         StringWriter bodyWriter = new StringWriter();
         jspBody.invoke(bodyWriter);
         builder.append(bodyWriter.toString());
      }
      builder.append("</div>\n");

      return builder.toString();
   }

   private String constructJavaScript() {
      // output the javascript
      StringBuilder builder = new StringBuilder();
      builder.append("<script type=\"text/javascript\">");
      builder.append("//<![CDATA[\n");
      builder.append("swfobject.embedSWF(");

      // required
      builder.append("'").append(escapeJavaScript(getSwfUrl())).append("'");
      builder.append(", '").append(escapeJavaScript(getId())).append("'");
      builder.append(", '").append(escapeJavaScript(getWidth())).append("'");
      builder.append(", '").append(escapeJavaScript(getHeight())).append("'");
      builder.append(", '").append(escapeJavaScript(getVersion())).append("'");

      // optional, but we have to keep the order intact
      String installSwfUrl = getExpressInstallSwfUrl();
      if (installSwfUrl == null) {
         builder.append(", ").append(false);
      } else {
         builder.append(", '").append(escapeJavaScript(installSwfUrl)).append("'");
      }
      builder.append(", ").append(toObjectNotation(flashVars));
      builder.append(", ").append(toObjectNotation(params));
      builder.append(", ").append(toObjectNotation(createAttributeMap()));
      builder.append(");");
      builder.append("\n//]]>");
      builder.append("</script>");

      return builder.toString();
   }

   /**
    * Creates a map of 'attributes', they are used as attributes to the object
    * tag (as opposed to the &lt;param&gt; tags). And contains the following
    * keys: 'id', 'name', 'styleclass' and 'align'.
    *
    * @return the map of the keys with their corresponding value.
    */
   private Map<String, String> createAttributeMap() {
      Map<String, String> result = new HashMap<String, String>(4);

      result.put("id", getId());
      result.put("name ", getName());
      result.put("styleclass", getStyleClass());
      result.put("align", getAlign());

      return result;
   }

   /**
    * Returns a JavaScript object notation of the given map. Something along the
    * lines of;
    *
    * <pre>
    * {attribute: 'value', attributes2: 'value2', ...}
    * </pre>
    *
    * <code>null</code> values in the map will result in that attribute not
    * being shown.
    *
    * @param map
    *           the map to convert
    * @return a JS object notation of the given map.
    */
   private String toObjectNotation(Map<String, String> map) {
      StringBuilder builder = new StringBuilder();
      String delimeter = ", ";

      builder.append("{");
      if (map != null) {

         Set<Entry<String, String>> entries = map.entrySet();

         for (Entry<String, String> entry : entries) {
            String value = entry.getValue();

            if (value != null) {
               builder.append(entry.getKey()).append(": ");
               builder.append("'").append(escapeJavaScript(value)).append("'");
               builder.append(delimeter);
            }
         }

         // remove the last delimeter if we have one. We do it like this
         // because it's much harder to check on whether or not we are going
         // to have a next value, since the value of the next entry can be
         // null.
         int end = builder.length();
         int start = end - delimeter.length();
         if ((end >= delimeter.length()) && builder.substring(start).equals(delimeter)) {
            builder.replace(start, end, "");
         }
      }
      builder.append("}");

      return builder.toString();
   }

   /**
    * Returns the swfFile.
    *
    * @return the swfFile
    */
   public String getSwfUrl() {
      return swfUrl;
   }

   /**
    * Sets the swfUrl to the specified value.
    *
    * @param swfUrl
    *           the swfUrl to set
    */
   public void setSwfUrl(String swfUrl) {
      this.swfUrl = swfUrl;
   }

   /**
    * Returns the width.
    *
    * @return the width
    */
   public String getWidth() {
      return width;
   }

   /**
    * Sets the width to the specified value.
    *
    * @param width
    *           the width to set
    */
   public void setWidth(String width) {
      this.width = width;
   }

   /**
    * Returns the height.
    *
    * @return the height
    */
   public String getHeight() {
      return height;
   }

   /**
    * Sets the height to the specified value.
    *
    * @param height
    *           the height to set
    */
   public void setHeight(String height) {
      this.height = height;
   }

   /**
    * Returns the version.
    *
    * @return the version
    */
   public String getVersion() {
      return version;
   }

   /**
    * Sets the version to the specified value.
    *
    * @param version
    *           the version to set
    */
   public void setVersion(String version) {
      this.version = version;
   }

   /**
    * Returns the expressInstallSwfUrl.
    *
    * @return the expressInstallSwfUrl
    */
   public String getExpressInstallSwfUrl() {
      return expressInstallSwfUrl;
   }

   /**
    * Sets the expressInstallSwfUrl to the specified value.
    *
    * @param expressInstallSwfUrl
    *           the expressInstallSwfUrl to set
    */
   public void setExpressInstallSwfUrl(String expressInstallSwfUrl) {
      this.expressInstallSwfUrl = expressInstallSwfUrl;
   }

   /**
    * Returns the id.
    *
    * @return the id
    */
   public String getId() {
      if (id == null) {
         id = String.format("f%s", System.currentTimeMillis());
      }
      return id;
   }

   /**
    * Sets the id to the specified value.
    *
    * @param id
    *           the id to set
    */
   public void setId(String id) {
      this.id = id;
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
    * Returns the styleClass.
    *
    * @return the styleClass
    */
   public String getStyleClass() {
      return styleClass;
   }

   /**
    * Sets the styleClass to the specified value.
    *
    * @param styleClass
    *           the styleClass to set
    */
   public void setStyleClass(String styleClass) {
      this.styleClass = styleClass;
   }

   /**
    * Returns the align.
    *
    * @return the align
    */
   public String getAlign() {
      return align;
   }

   /**
    * Sets the align to the specified value.
    *
    * @param align
    *           the align to set
    */
   public void setAlign(String align) {
      this.align = align;
   }
}