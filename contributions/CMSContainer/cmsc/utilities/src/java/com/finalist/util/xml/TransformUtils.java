package com.finalist.util.xml;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

public class TransformUtils {
   private static Log log = LogFactory.getLog(TransformUtils.class);


   public static StringWriter serializeDocument(Document doc) {
      // XmlUtil serializeDocument(Document doc) will destory UTF-8 charecters.
      StringWriter sw = new StringWriter();

      try {
         TransformerFactory tfactory = TransformerFactory.newInstance();
         Transformer transformer = tfactory.newTransformer();
         transformer.transform(new DOMSource(doc), new StreamResult(sw));
      }
      catch (Exception e) {
         log.error("Error when try to transform document to String", e);
      }
      return sw;
   }
}
