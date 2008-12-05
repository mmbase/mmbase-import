package com.finalist.cmsc.util;

import java.io.*;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * @author <a href="mailto:nico@klasens.net"> Nico Klasens </A>
 */
public final class XsltUtil {

   /** Source if of type Reader */
   private static final int SOURCE_READER = 1;

   /** Source if of type InputStream */
   private static final int SOURCE_INPUT_STREAM = 2;

   /** Source if of type String */
   private static final int SOURCE_STRING = 3;

   /** Source if of type Url */
   private static final int SOURCE_URL = 4;

   /** Source if of type File */
   private static final int SOURCE_FILE = 5;

   /** Transformation factory */
   private TransformerFactory factory = null;

   /** Transformer */
   private Transformer transformer = null;

   /** XML source */
   private Object xmlSource = null;

   /** XSL source */
   private Object xslSource = null;

   /** XSL type */
   private int xslSourceType = 0;

   /** XML type */
   private int xmlSourceType = 0;

   /** Mime type */
   private String mimeType = null;


   /**
    * Constuctor XsltUtil.
    *
    * @param xmlSource
    *           Source XML
    * @param xslSource
    *           Source XSL
    * @param mimetype
    *           mime type
    * @param usefop
    *           use apache.org's FOP
    */
   public XsltUtil(Object xmlSource, Object xslSource, String mimetype) {

      setXMLSource(xmlSource);
      setXSLSource(xslSource);
      setOutputMimeType(mimetype);
      factory = TransformerFactory.newInstance();
   }


   /**
    * create XSL Source.
    *
    * @return Source
    * @throws IOException
    *            if IO fails
    */
   private final Source createXSLSource() throws IOException {
      return createSource(xslSource, xslSourceType);
   }


   /**
    * create XML Source.
    *
    * @return Source
    * @throws IOException
    *            if IO fails
    */
   private final Source createXMLSource() throws IOException {
      return createSource(xmlSource, xmlSourceType);
   }


   /**
    * create Source.
    *
    * @param source
    *           data
    * @param sourceType
    *           source type
    * @return Source
    * @throws IOException
    *            if IO fails
    */
   private final Source createSource(Object source, int sourceType) throws IOException {

      switch (sourceType) {
         case SOURCE_FILE: // '\005'
            File file = (File) source;
            StreamSource streamsource4 = new StreamSource(file);
            return streamsource4;

         case SOURCE_URL: // '\004'
            URL url = (URL) source;
            StreamSource streamsource = new StreamSource(url.openStream());
            return streamsource;

         case SOURCE_STRING: // '\003'
            String s = (String) source;
            StreamSource streamsource1 = new StreamSource(new StringReader(s));
            return streamsource1;

         case SOURCE_INPUT_STREAM: // '\002'
            InputStream inputstream = (InputStream) source;
            StreamSource streamsource2 = new StreamSource(inputstream);
            return streamsource2;

         case SOURCE_READER: // '\001'
            Reader reader = (Reader) source;
            StreamSource streamsource3 = new StreamSource(reader);
            return streamsource3;
      }

      return null;
   }


   /**
    * set XSL Source.
    *
    * @param obj
    *           XSL source
    */
   public void setXSLSource(Object obj) {
      if (obj == null) {
         throw new IllegalArgumentException("You cannot have a null XSL source.");
      }

      xslSourceType = getType(obj);
      if (xslSourceType == 0) {
         throw new IllegalArgumentException("The XSL source type '" + obj.getClass().getName() + "' is unsupported.");
      }
      xslSource = obj;
   }


   /**
    * set XML Source.
    *
    * @param obj
    *           XML source
    */
   public void setXMLSource(Object obj) {
      if (obj == null) {
         throw new IllegalArgumentException("You cannot have a null XML source.");
      }

      xmlSourceType = getType(obj);
      if (xmlSourceType == 0) {
         throw new IllegalArgumentException("The XML source type '" + obj.getClass().getName() + "' is unsupported.");
      }
      xmlSource = obj;

   }


   /**
    * get Type.of source
    *
    * @param obj
    *           Source
    * @return int
    */
   private int getType(Object obj) {
      if (obj instanceof String) {
         return SOURCE_STRING;
      }
      if (obj instanceof URL) {
         return SOURCE_URL;
      }
      if (obj instanceof File) {
         return SOURCE_FILE;
      }
      if (obj instanceof Reader) {
         return SOURCE_READER;
      }
      if (obj instanceof InputStream) {
         return SOURCE_INPUT_STREAM;
      }
      return 0;
   }


   /**
    * setURIResolver.
    *
    * @param uriresolver
    *           resolver
    */
   public void setURIResolver(URIResolver uriresolver) {
      factory.setURIResolver(uriresolver);
   }


   /**
    * getURIResolver.
    *
    * @return URIResolver
    */
   public URIResolver getURIResolver() {
      return factory.getURIResolver();
   }


   /**
    * setOutputMimeType.
    *
    * @param s
    *           String
    */
   public void setOutputMimeType(String s) {
      if (s == null) {
         mimeType = "text/xml";
      }
      else {
         mimeType = s;
      }
   }


   /**
    * getOutputMimeType.
    *
    * @return String
    */
   public String getOutputMimeType() {
      return mimeType;
   }


   /**
    * XSL Transform.
    *
    * @param streamresult
    *           result
    * @param params
    *           The params to be placed. Standard name/value pairs.
    * @throws TransformerException
    *            if Transformation fails
    * @throws IOException
    *            if IO fails
    */
   private final void xsltTransform(StreamResult streamresult, Map<String, Object> params) throws TransformerException,
         IOException {

      Source source = createXSLSource();
      Source source1 = createXMLSource();

      transformer = factory.newTransformer(source);
      setStylesheetParams(transformer, params);
      transformer.transform(source1, streamresult);
   }


   /**
    * This method can set the stylesheetparams for a transformer.
    *
    * @param transformer
    *           The transformer.
    * @param params
    *           The params to be placed. Standard name/value pairs.
    */
   private static void setStylesheetParams(Transformer transformer, Map<String, Object> params) {
      if (params == null) {
         return;
      }

      Iterator<Map.Entry<String, Object>> i = params.entrySet().iterator();
      while (i.hasNext()) {
         Map.Entry<String, Object> entry = i.next();
         transformer.setParameter(entry.getKey(), entry.getValue());
      }
   }


   /**
    * Method transformToString.
    *
    * @param params
    *           The params to be placed. Standard name/value pairs.
    * @return String
    * @throws FOPException
    *            if FOP fails
    * @throws IOException
    *            if IO fails
    * @throws TransformerException
    *            if Transformation fails
    */
   public String transformToString(Map<String, Object> params) throws IOException, TransformerException {

      CharArrayWriter caw = new CharArrayWriter();
      xsltTransform(new StreamResult(caw), params);
      return caw.toString();
   }


   /**
    * transform To ServletResponse.
    *
    * @param response
    *           HTTP response
    * @param params
    *           The params to be placed. Standard name/value pairs.
    * @throws FOPException
    *            if FOP fails
    * @throws IOException
    *            if IO fails
    * @throws TransformerException
    *            if Transformation fails
    */
   public void transformToServletResponse(HttpServletResponse response, Map<String, Object> params) throws IOException,
         TransformerException {

      String xml = transformToString(params);
      response.setContentType(mimeType);
      response.setContentLength(xml.length());
      response.getWriter().write(xml);
   }

}