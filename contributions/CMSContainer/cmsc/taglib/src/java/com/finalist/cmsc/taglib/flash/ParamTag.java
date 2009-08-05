package com.finalist.cmsc.taglib.flash;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;

/**
 * Handles the &lt;flashvar&gt; tag. You can use this tag with either a name and
 * a value attribute or you can only specify a name attribute and specify the
 * value in the body of this tag. If you use both a {@link JspException} will be
 * thrown.
 *
 * @author Auke van Leeuwen
 */
public class ParamTag extends NameValueTag {

   /**
    * Based on: <a
    * href="http://www.adobe.com/cfusion/knowledgebase/index.cfm?id=tn_12701"
    * >http://www.adobe.com/cfusion/knowledgebase/index.cfm?id=tn_12701</a>
    *
    * @see NameValueTag#initAllowedValues()
    *
    * @return the map with allowed values for all parameters, or
    *         <code>null</code> if no parameter checking is needed.
    */
   @Override
   protected Map<String, List<String>> initAllowedValues() {
      Map<String, List<String>> map = new HashMap<String, List<String>>();
      List<String> booleanList = Arrays.asList("true", "false");

      map.put("play", booleanList);
      map.put("loop", booleanList);
      map.put("menu", booleanList);
      map.put("quality", Arrays.asList("low", "autolow", "autohigh", "medium", "high", "best"));
      map.put("scale", Arrays.asList("default", "noorder", "exactfit"));
      map.put("salign", Arrays.asList("t", "r", "b", "l", "tl", "tb", "bl", "br"));
      map.put("wmode", Arrays.asList("window", "opaque", "transparent"));
      map.put("bgcolor", null);
      map.put("base", null);
      map.put("swliveconnect", null);
      map.put("devicefont", booleanList);
      map.put("allowscriptaccess", Arrays.asList("always", "never"));
      map.put("seamlesstabbing", booleanList);
      map.put("allowfullscreen", booleanList);
      map.put("allownetworking", Arrays.asList("all", "internal", "none"));

      return map;
   }

   /** {@inheritDoc} */
   @Override
   public void doTag() throws JspException, IOException {
      super.doTag();

      FlashTag flashTag = (FlashTag) findAncestorWithClass(this, FlashTag.class);
      if (flashTag == null) {
         throw new JspException("A flashparam tag should be nested inside a flash tag!");
      }

      if (!isAllowedValue(getName(), getValue())) {
         throw new JspException(String.format("Invalid value '%s' for '%s'!", getValue(), getName()));
      }

      flashTag.addParam(getName(), getValue());
   }
}