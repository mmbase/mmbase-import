/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.richtext;

import org.mmbase.bridge.Field;
import org.mmbase.bridge.Node;
import org.mmbase.datatypes.processors.ParameterizedProcessorFactory;
import org.mmbase.datatypes.processors.Processor;
import org.mmbase.util.functions.Parameter;
import org.mmbase.util.functions.Parameters;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

@SuppressWarnings("serial")
public class RichTextSetProcessor implements ParameterizedProcessorFactory {

   /** MMbase logging system */
   private static Logger log = Logging.getLoggerInstance(RichTextSetProcessor.class.getName());

   protected static final Parameter[] PARAMS = new Parameter[] { 
      new Parameter("replaceHeaders", String.class,"false"), 
      new Parameter("replaceParagraphs", String.class,"true")
   };

   public Parameters createParameters() {
       return new Parameters(PARAMS);
    }

   public Processor createProcessor(Parameters parameters) {
       final boolean replaceHeaders = Boolean.parseBoolean(parameters.get("replaceHeaders").toString());
       final boolean replaceParagraphs = Boolean.parseBoolean(parameters.get("replaceParagraphs").toString());
       return new Processor() {

           public Object process(Node node, Field field, Object value) {
              if (value instanceof String) {
                 String in = (String) value;
                 String out = RichText.cleanRichText(in, replaceHeaders, replaceParagraphs);
                 if (log.isDebugEnabled() && !out.equals(in)) {
                    log.debug("Replaced " + field.getName() + " value \"" + in + "\"\n \t by \n\"" + out + "\"");
                 }
                 return out;
              }
              return value;
           }
       };
   }

}
