package com.finalist.cmsc.repository.xml;

import net.sf.mmapps.commons.util.HttpUtil;
import net.sf.mmapps.commons.util.StringUtil;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.storage.search.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.repository.RepositoryUtil;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The XMLServlet is a basic servlet for retrieving data from MMBase. A remote system can
 * request content and this Servlet will access MMBase, generate
 * the proper XML and send it back.
 *
 * @author Nico Klasens
 */
public class XMLServlet extends HttpServlet {
   /**
    * MMBase logging system
    */
   static Logger log = Logging.getLoggerInstance(XMLServlet.class.getName());

   private static final String CHANNEL = "channel";
   private static final String CONTENT_TYPE = "contentType";
   private static final String PK = "pk";
   private static final String PREVIEWDATE = "previewdate";
   private static final String FROM_INDEX = "listFromIndex";
   private static final String TO_INDEX = "listToIndex";
   private static final String FILTER_NAME = "filterAttributeName";
   private static final String FILTER_VALUE = "filterAttributeValue";
   private static final String SORT_NAME = "sortAttribute";
   private static final String SORT_DIRECTION = "sortDirection";
   private static final String NUMBERS_ONLY = "numbersOnly";
   /**
    * This is the channel separator.
    */
   public static final String CS = "/";

   /**
    * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
    */
   /**
    * This method handles all requests, checks the request to see what data is required
    * and calls the proper methods for creating the xml. After processing the proper XML is
    * written out to the HttpServletResponse.
    *
    * @param request  The HttpServletRequest to this servlet.
    * @param response The HttpServletResponse this servlet will write to.
    */
   protected void service(HttpServletRequest request, HttpServletResponse response) {

      // This cloud can only be read from.
      Cloud cloud = CloudProviderFactory.getCloudProvider().getAnonymousCloud();
      
      // Primary key request
      String pk = request.getParameter(PK);
      if (!StringUtil.isEmptyOrWhitespace(pk)) {
         log.debug("PRIMARY KEY " + pk);
         String xml = "";
         try {
            Node node = cloud.getNode(pk);
            if (RepositoryUtil.isChannel(node)) {
               xml = XMLController.toXml(node);
            }
            else {
               xml = XMLController.toXml(node, RepositoryUtil.CONTENTCHANNEL);
            }
            HttpUtil.sendXml(xml, response);
            return;
         }
         catch (Exception e) {
            sendError("Creating xml failed", response);
            return;
         }
      }

      // Check the contentType
      String contentType = request.getParameter(CONTENT_TYPE);
      log.debug("CONTENTTYPE " + contentType);

      if (StringUtil.isEmptyOrWhitespace(contentType)) {
         sendError("Content type is empty", response);
         return;
      }
      contentType = contentType.toLowerCase();

      // For all remaining content types, a channel must be specified

      // Check the channel
      String channel = request.getParameter(CHANNEL);

      if (StringUtil.isEmptyOrWhitespace(channel)) {
         sendError("Channel is empty", response);
         return;
      }

      log.debug("CHANNEL PATH = " + channel);

      Node channelNode = null;
      try {
         channelNode = RepositoryUtil.getChannelFromPath(cloud, channel);
      }
      catch (NotFoundException nfe) {
         sendError("Channel not found", response);
         return;
      }

      if (channelNode == null) {
         sendError("Channel not found", response);
         return;
      }
      
      // Handle other content types
      if (!ContentElementUtil.isContentType(contentType)) {
         sendError("Unknown content type", response);
         return;
      }

      String fromIndex = request.getParameter(FROM_INDEX);
      String toIndex = request.getParameter(TO_INDEX);
      String filterName = request.getParameter(FILTER_NAME);
      String filterValue = request.getParameter(FILTER_VALUE);
      String sortName = request.getParameter(SORT_NAME);
      String sortDirection = request.getParameter(SORT_DIRECTION);
      String previewdateParam = request.getParameter(PREVIEWDATE);
      String numbersOnly = request.getParameter(NUMBERS_ONLY);

      NodeQuery query = cloud.createNodeQuery();
      Step step1 = query.addStep(channelNode.getNodeManager());
      query.addNode(step1, channelNode);

      NodeManager contentManager = cloud.getNodeManager(contentType);
      RelationStep step2 = query.addRelationStep(contentManager, RepositoryUtil.CONTENTREL, "destination");
      Step step3 = step2.getNext();
      query.setNodeStep(step3); // makes it ready for use as NodeQuery

      if (!StringUtil.isEmptyOrWhitespace(filterName) && !StringUtil.isEmptyOrWhitespace(filterValue)) {
         Field field = contentManager.getField(filterName);
         FieldValueConstraint constraint = query.createConstraint(query.getStepField(field),
               FieldCompareConstraint.LIKE,
               "%" + filterValue + "%");
         query.setConstraint(constraint);
      }

      if (previewdateParam != null) {
         Integer date = null;
         if (previewdateParam.matches("^\\d+$")) {
            date = new Integer(previewdateParam);
            Constraint orginal = query.getConstraint();
            Field field = contentManager.getField("expirydate");
            Constraint expirydate = query.createConstraint(query.getStepField(field), FieldCompareConstraint.GREATER_EQUAL, date);
            field = contentManager.getField("publishdate");
            Constraint publishdate = query.createConstraint(query.getStepField(field), FieldCompareConstraint.LESS_EQUAL, date);
            Constraint composite = query.createConstraint(expirydate, CompositeConstraint.LOGICAL_AND, publishdate);

            if (orginal == null) {
               query.setConstraint(composite);
            }
            else {
               query.setConstraint(query.createConstraint(composite, CompositeConstraint.LOGICAL_AND, orginal));
            }
         }
      }

      if (!StringUtil.isEmptyOrWhitespace(sortName)) {
          StepField sf = query.getStepField(contentManager.getField(sortName));
          int dir = SortOrder.ORDER_ASCENDING;
          if ("DOWN".equalsIgnoreCase(sortDirection)) {
             dir = SortOrder.ORDER_DESCENDING;
          }
          query.addSortOrder(sf, dir);
       }
       else {
           Field field = cloud.getNodeManager("contentrel").getField("pos");
           StepField posSF = query.createStepField(step2, field);
           query.addSortOrder(posSF, SortOrder.ORDER_ASCENDING);
       }

       int contentSize = Queries.count(query);
       if (!StringUtil.isEmptyOrWhitespace(fromIndex) && !StringUtil.isEmptyOrWhitespace(toIndex)) {
          query.setOffset(getInt(fromIndex, 0));
          query.setMaxNumber(Math.max(getInt(toIndex, -1) - getInt(fromIndex, 0), -1));
       }

       NodeList contentlist = query.getNodeManager().getList(query);
       String xml = "";
       try {
         if (Boolean.valueOf(numbersOnly).booleanValue()) {
             xml = XMLController.toXmlNumbersOnly(channelNode, contentlist, contentSize);
         }
         else {
             xml = XMLController.toXml(channelNode, contentlist, contentSize);
         }
       }
       catch (Exception e) {
           if (log.isDebugEnabled()) {
               log.debug(Logging.stackTrace(e));
           }
          sendError("Creating xml failed", response);
          return;
       }
       HttpUtil.sendXml(xml, response);
   }

   /**
    * Retrieves the int-value held in a String. This method will return the default value
    * if the String could not be properly parsed.
    *
    * @param value The String holding the int value.
    * @param dflt  The default int value, which should be returned if the value could not be parsed.
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
    * @param error    The error to output.
    * @param response The HttpServletResponse to write to.
    */
   public static void sendError(String error, HttpServletResponse response) {
      HttpUtil.sendXml("<?xml version=\"1.0\" encoding=\"UTF-8\"?><cmserror>" + error + "</cmserror>", response);
   }

}