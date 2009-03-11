package com.finalist.cmsc.repository.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.commons.lang.StringUtils;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.storage.search.FieldCompareConstraint;
import org.mmbase.storage.search.FieldValueConstraint;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.xml.sax.SAXException;

import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.util.HttpUtil;
import com.finalist.cmsc.util.XsltUtil;

/**
 * The XMLServlet is a basic servlet for retrieving data from MMBase. A remote
 * system can request content and this Servlet will access MMBase, generate the
 * proper XML and send it back.
 *
 * @author Nico Klasens
 */
@SuppressWarnings("serial")
public class XMLServlet extends HttpServlet {
   /**
    * MMBase logging system
    */
   static final Logger log = Logging.getLoggerInstance(XMLServlet.class.getName());

   private static final String CHANNEL = "channel";
   private static final String CONTENT_TYPE = "contentType";
   private static final String PK = "pk";
   private static final String PREVIEWDATE = "previewdate";
   private static final String FROM_INDEX = "fromIndex";
   private static final String TO_INDEX = "toIndex";
   private static final String FILTER_NAME = "filterName";
   private static final String FILTER_VALUE = "filterValue";
   private static final String SORT_NAME = "sort";
   private static final String SORT_DIRECTION = "sortDirection";
   private static final String NUMBERS_ONLY = "numbersOnly";

   /**
    * This is the channel separator.
    */
   public static final String CS = "/";

   private XMLController xmlController;

   @Override
   public void init() throws ServletException {
      super.init();
      List<String> disallowedTypes = getDisallowedTypes();
      List<String> allowedTypes = getAllowedTypes();

      List<String> disallowedRelationTypes = getDisallowedRelationTypes();
      List<String> allowedRelationTypes = getAllowedRelationTypes();

      List<String> disallowedFields = getDisallowedFields();
      List<String> allowedFields = getAllowedFields();

      xmlController = new XMLController(disallowedRelationTypes, allowedRelationTypes, disallowedTypes, allowedTypes,
            disallowedFields, allowedFields);
   }

   protected List<String> getAllowedFields() {
      return null;
   }


   protected List<String> getDisallowedFields() {
      return null;
   }


   protected List<String> getAllowedRelationTypes() {
      return null;
   }


   protected List<String> getAllowedTypes() {
      return null;
   }


   protected List<String> getDisallowedTypes() {
      return XMLController.defaultDisallowedTypes;
   }


   protected List<String> getDisallowedRelationTypes() {
      return XMLController.defaultDisallowedRelationTypes;
   }


   /**
    * This method handles all requests, checks the request to see what data is
    * required and calls the proper methods for creating the xml. After
    * processing the proper XML is written out to the HttpServletResponse.
    *
    * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest,
    *      javax.servlet.http.HttpServletResponse)
    * @param request
    *           The HttpServletRequest to this servlet.
    * @param response
    *           The HttpServletResponse this servlet will write to.
    */
   @Override
   protected void service(HttpServletRequest request, HttpServletResponse response) {
      Cloud cloud = getCloud();
      String xsl = getXslTemplate(request);

      // Primary key request
      String pk = request.getParameter(PK);
      if (StringUtils.isNotBlank(pk)) {
         processSingle(response, cloud, xsl, pk);
         return;
      }

      // For all remaining parameters, a channel must be specified

      // Check the channel
      String channel = request.getParameter(CHANNEL);
      // Check the contentType
      String[] contentTypes = request.getParameterValues(CONTENT_TYPE);

      String fromIndex = request.getParameter(FROM_INDEX);
      String toIndex = request.getParameter(TO_INDEX);
      String filterName = request.getParameter(FILTER_NAME);
      String filterValue = request.getParameter(FILTER_VALUE);
      String sortName = request.getParameter(SORT_NAME);
      String sortDirection = request.getParameter(SORT_DIRECTION);
      String previewdateParam = request.getParameter(PREVIEWDATE);
      String numbersOnly = request.getParameter(NUMBERS_ONLY);

      if ((contentTypes == null) || (contentTypes.length == 0)) {
         sendError("no content types specified", response);
         return;
      }

      // loop through contentTypes
      for (int i = 0; i < contentTypes.length; i++) {
         log.debug("CONTENTTYPE " + contentTypes[i]);

         if (StringUtils.isBlank(contentTypes[i])) {
            sendError("Content type is empty", response);
            return;
         }

         contentTypes[i] = contentTypes[i].toLowerCase();

         // Handle other content types
         if (!ContentElementUtil.isContentType(contentTypes[i])) {
            sendError("Unknown content type", response);
            return;
         }
      }

      processList(response, xsl, cloud, channel, Arrays.asList(contentTypes), fromIndex, toIndex, filterName,
            filterValue, sortName, sortDirection, previewdateParam, numbersOnly);
   }


   protected void processSingle(HttpServletResponse response, Cloud cloud, String xsl, String pk) {
      log.debug("PRIMARY KEY " + pk);
      String xml = "";
      try {
         Node node = cloud.getNode(pk);
         if (RepositoryUtil.isContentChannel(node)) {
            xml = xmlController.toXml(node);
         }
         else {
            xml = xmlController.toXml(node, RepositoryUtil.CONTENTCHANNEL);
         }
         String transformedXml = transformXml(xsl, xml);
         HttpUtil.sendXml(transformedXml, response);
      }
      catch (IOException e) {
         if (log.isDebugEnabled()) {
            log.debug(Logging.stackTrace(e));
         }
         sendError("IO with xslt failed", response);
         return;
      }
      catch (TransformerException e) {
         if (log.isDebugEnabled()) {
            log.debug(Logging.stackTrace(e));
         }
         sendError("Transformer with xslt failed", response);
         return;
      }
      catch (ParserConfigurationException e) {
         if (log.isDebugEnabled()) {
            log.debug(Logging.stackTrace(e));
         }
         sendError("ParserConfiguration with xslt failed", response);
         return;
      }
      catch (SAXException e) {
         if (log.isDebugEnabled()) {
            log.debug(Logging.stackTrace(e));
         }
         sendError("SAX with xslt failed", response);
         return;
      }
      catch (Exception e) {
         sendError("Creating xml failed", response);
         return;
      }
   }


   protected Cloud getCloud() {
      // This cloud can only be read from.
      return CloudProviderFactory.getCloudProvider().getAnonymousCloud();
   }


   protected void processList(HttpServletResponse response, String xsl, Cloud cloud, String channel,
         List<String> contentTypes, String fromIndex, String toIndex, String filterName, String filterValue,
         String sortName, String sortDirection, String previewdateParam, String numbersOnly) {

      if (StringUtils.isBlank(channel)) {
         sendError("Channel is empty", response);
         return;
      }

      log.debug("CHANNEL PATH = " + channel);

      Node channelNode = null;
      try {
         if (StringUtils.isNumeric(channel)) {
            channelNode = cloud.getNode(channel);
         }
         else {
            channelNode = RepositoryUtil.getChannelFromPath(cloud, channel);
         }
      }
      catch (NotFoundException nfe) {
         sendError("Channel not found", response);
         return;
      }

      if (channelNode == null) {
         sendError("Channel not found", response);
         return;
      }

      // determine offset and max number
      int offSet = -1;
      int maxNumber = -1;

      if (StringUtils.isNotBlank(fromIndex) && StringUtils.isNotBlank(toIndex)) {
         offSet = getInt(fromIndex, 0);
         maxNumber = Math.max(getInt(toIndex, -1) - getInt(fromIndex, 0), -1);
      }

      // create query
      NodeQuery query = RepositoryUtil.createLinkedContentQuery(channelNode, contentTypes, sortName, sortDirection,
            false, null, offSet, maxNumber, -1, -1, -1);

      NodeManager queryNodeManager = query.getNodeManager();

      // add other constraints
      if (StringUtils.isNotBlank(filterName) && StringUtils.isNotBlank(filterValue)) {
         // check if field exists
         if (queryNodeManager.hasField(filterName)) {
            Field field = queryNodeManager.getField(filterName);
            FieldValueConstraint constraint = query.createConstraint(query.getStepField(field),
                  FieldCompareConstraint.LIKE, "%" + filterValue + "%");
            SearchUtil.addConstraint(query, constraint);
         }
         else {
            sendError("Cannot add constraint for field: " + filterName, response);
            return;
         }
      }

      if (previewdateParam != null) {
         Integer date = null;
         if (previewdateParam.matches("^\\d+$")) {
            date = Integer.valueOf(previewdateParam);
            ContentElementUtil.addLifeCycleConstraint(query, date);
         }
      }

      int contentSize = Queries.count(query);

      NodeList contentlist = queryNodeManager.getList(query);
      try {
         String xml = "";
         if (Boolean.parseBoolean(numbersOnly)) {
            xml = xmlController.toXmlNumbersOnly(channelNode, contentlist, contentSize);
         }
         else {
            xml = xmlController.toXml(channelNode, contentlist, contentSize);
         }

         String transformedXml = transformXml(xsl, xml);
         HttpUtil.sendXml(transformedXml, response);
      }
      catch (IOException e) {
         if (log.isDebugEnabled()) {
            log.debug(Logging.stackTrace(e));
         }
         sendError("IO with xslt failed", response);
         return;
      }
      catch (TransformerException e) {
         if (log.isDebugEnabled()) {
            log.debug(Logging.stackTrace(e));
         }
         sendError("Transformer with xslt failed", response);
         return;
      }
      catch (ParserConfigurationException e) {
         if (log.isDebugEnabled()) {
            log.debug(Logging.stackTrace(e));
         }
         sendError("ParserConfiguration with xslt failed", response);
         return;
      }
      catch (SAXException e) {
         if (log.isDebugEnabled()) {
            log.debug(Logging.stackTrace(e));
         }
         sendError("SAX with xslt failed", response);
         return;
      }
      catch (Exception e) {
         if (log.isDebugEnabled()) {
            log.debug(Logging.stackTrace(e));
         }
         sendError("Creating xml failed", response);
         return;
      }
   }


   private String transformXml(String xsl, String xml) throws IOException, TransformerException {
      if (StringUtils.isNotEmpty(xsl)) {
         // get xslt source and xml source
         InputStream xslSrc = Thread.currentThread().getContextClassLoader().getResourceAsStream(xsl);
         // transform
         XsltUtil xsltUtil = new XsltUtil(xml, xslSrc, null);
         xml = xsltUtil.transformToString(null);
      }
      return xml;
   }


   protected String getXslTemplate(HttpServletRequest request) {
      return null;
   }


   /**
    * Retrieves the int-value held in a String. This method will return the
    * default value if the String could not be properly parsed.
    *
    * @param value
    *           The String holding the int value.
    * @param dflt
    *           The default int value, which should be returned if the value
    *           could not be parsed.
    * @return The int-value held in value, or dflt if value could not be parsed.
    */
   public static int getInt(String value, int dflt) {
      try {
         return Integer.parseInt(value);
      }
      catch (NumberFormatException e) {
         return dflt;
      }
   }


   /**
    * This method sends an error-response to the HttpServletResponse.
    *
    * @param error
    *           The error to output.
    * @param response
    *           The HttpServletResponse to write to.
    */
   public static void sendError(String error, HttpServletResponse response) {
      HttpUtil.sendXml("<?xml version=\"1.0\" encoding=\"UTF-8\"?><cmserror>" + error + "</cmserror>", response);
   }

}