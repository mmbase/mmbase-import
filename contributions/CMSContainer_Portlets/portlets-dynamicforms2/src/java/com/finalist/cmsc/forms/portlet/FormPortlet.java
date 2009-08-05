package com.finalist.cmsc.forms.portlet;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import javax.activation.DataSource;
import javax.portlet.*;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.portlet.PortletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

import com.finalist.cmsc.forms.*;
import com.finalist.cmsc.forms.definition.FormDefinition;
import com.finalist.cmsc.forms.stepprocessors.ProcessorResult;
import com.finalist.cmsc.forms.value.ValueForm;
import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.portalImpl.PortalConstants;
import com.finalist.cmsc.portlets.XsltPortlet;
import com.finalist.cmsc.util.XmlUtil;

/**
 * Portlet to process forms based on a form definition
 */
public class FormPortlet extends XsltPortlet {

   private static final String DYNAMICFORMS_LOCATION = "/WEB-INF/dynamicforms/";

   private final static Log logger = LogFactory.getLog(FormPortlet.class);

   public static final String PORTLET_FORM_POSTFIX = "postfix";

   public static final Object PARAM_BACK = "backStep";
   public static final String PARAM_CURRENTSTEP = "currentStep";
   public static final String PARAM_SEQUENCE = "sequence";

   public static final String KEY_ACTIVESTEP = "activeStep";
   public static final String KEY_EDITPATH = "editpath";
   public static final String KEY_SEQUENCE = "renderSequence";
   public static final String KEY_VALIDATIONERROR = "validation_error";

   private static final int DEFAULT_MAXFILESIZE = 2; // default file size in Meg
   private static final long MEGABYTE = 1024 * 1024; // 1 Meg
   private static final String ENCODING_UTF8 = "UTF-8";
   
   /**
    * Key for FormDefintion on the request
    */
   public static final String KEY_FORMKEY = "formResource";

   /** If the final form process has a failure then we store it under this names */
   public final static String KEY_ERROR = "error";

   private String formPostfix;

   @Override
   public void init(PortletConfig config) throws PortletException {
      super.init(config);
      formPostfix = config.getInitParameter(PORTLET_FORM_POSTFIX);
   }

   @Override
   public void processView(ActionRequest req, ActionResponse res) throws PortletException {
      try {
         String formname = getFormName(req);
         
         // StepProcessors could use this entry to get the ValueForm from the req
         req.setAttribute(KEY_FORMKEY, formname);
         
         Map<String, List<String>> parameters = new HashMap<String, List<String>>();
         Map<String, DataSource> binaries = new HashMap<String, DataSource>();
         processRequest(req, parameters, binaries);
      
         // The activeStep is the step which will be rendered.
         String activeStep = "";
         List<String> aStep = parameters.get(KEY_ACTIVESTEP);
         if (notEmptyValue(aStep)) {
            activeStep = aStep.get(0);
            res.setRenderParameter(KEY_ACTIVESTEP, activeStep);
         }
         else {
            // FormPortlet did not post to itself. Probably another portlet which post to this.
            // IOW it is not a form action.
            // Request should just follow the normal portlet flow.
            // Step DataProvider can then pick up the posted parameters.
            return;
         }
      
         String lastRenderedStep = null;
         // The rendering process puts the name of the rendered step (currentStep) in the html form
         List<String> lastStep = parameters.get(PARAM_CURRENTSTEP);
         if (notEmptyValue(lastStep)) {
            lastRenderedStep = lastStep.get(0);
         }
         else {
            throw new IllegalStateException("currentStep param is not set. It is a required parameter");
         }
      
         int renderSequence = 0;
         // The rendering process puts the sequence number of the rendered step in the html form
         List<String> renderSeq = parameters.get(PARAM_SEQUENCE);
         if (notEmptyValue(renderSeq)) {
            try {
               renderSequence = Integer.parseInt(renderSeq.get(0));
            }
            catch (NumberFormatException e) {
               logger.debug("renderSequence is not a number " + renderSequence + " " + e.getMessage(), e);
            }
         }
         else {
            throw new IllegalStateException("renderSequence param is not set. It is a required parameter");
         }
      
         int expectedRenderSequence = 0;
         String expectedRenderSeq = getStateValue(KEY_SEQUENCE, req, true);
         try {
            expectedRenderSequence = Integer.parseInt(expectedRenderSeq);
         }
         catch (NumberFormatException e) {
            logger.debug("expectedRenderSeq is not a number " + expectedRenderSeq + " "
                  + e.getMessage(), e);
         }
      
         // Back to a step which is already processed. Previous button (guitype = backbutton)
         boolean btfu = false;
         List<String> back = parameters.get(PARAM_BACK);
         if (notEmptyValue(back)) {
            btfu = (Boolean.valueOf(back.get(0))).booleanValue();
         }
         if (!btfu) {
            btfu = renderSequence < expectedRenderSequence;
         }
      
         // Value structure. Contains form definition and user data
         ValueForm valueForm = (ValueForm) req.getPortletSession().getAttribute(formname);
         if (valueForm == null) {
            // stop processing, start rendering
            return;
         }
         // Path of the object in the value structure for modifications
         // format of editpath = basket|wmsitm[3]|connection[1]
         String editPath = parameters.get(KEY_EDITPATH).get(0);
      
         if (btfu) {
            setStateValue(req, res, KEY_EDITPATH, editPath, false);
            if (isEmpty(activeStep)) {
               setStateValue(req, res, KEY_ACTIVESTEP, lastRenderedStep, false);
            }
            removeBadEditPath(parameters);
            // stop processing, start rendering
            return;
         }
         else {
            storeParemetersInFields(parameters, valueForm);
            storeBinariesInFields(binaries, valueForm);
            removeBadEditPath(parameters);
      
            if (lastRenderedStep != null && !activeStep.equals(lastRenderedStep)
                  && !valueForm.isValidStep(lastRenderedStep, editPath)) {
               // Posted data is not valid. Return to the old step
               activeStep = lastRenderedStep;
               setStateValue(req, res, KEY_VALIDATIONERROR, "true", false);
               setStateValue(req, res, KEY_ACTIVESTEP, activeStep, false);
               setStateValue(req, res, KEY_EDITPATH, editPath, false);
               // stop processing, start rendering
               return;
            }
            else {
               ProcessorResult processorResult = null;
      
               if (lastRenderedStep != null) {
                  logger.debug("About to execute the stepprocesser for step : " + lastRenderedStep);
                  processorResult = valueForm.executeStepProcessor(lastRenderedStep, parameters);
      
                  // a StepProcessor can influence the flow using its return value
                  if (processorResult != null) {
                     if (processorResult.getActiveStep() != null) {
                        setStateValue(req, res, KEY_ACTIVESTEP,
                              processorResult.getActiveStep(), false);
                     }
                     if (processorResult.getEditPath() != null) {
                        setStateValue(req, res, KEY_EDITPATH, processorResult.getEditPath(), false);
                     }
                  }
               }
      
               // don't execute the form processor if the active step has been modified by the step
               // processor
               if (valueForm.isFinalStep(lastRenderedStep)) {
                  String result = valueForm.processForm(parameters);
                  if (StringUtils.isNotEmpty(result)) {
                     setStateValue(req, res, KEY_ERROR, result, false);
                  }
                  setStateValue(req, res, KEY_ACTIVESTEP, activeStep, false);
      
                  // now that the form has finished, we need to remove the ValueForm instance from the
                  // session.
                  req.getPortletSession().removeAttribute(formname);
                  // stop processing, start rendering
                  return;
               }
               else {
                  // don't remove editpath from session when step processor has supplied a different
                  // value
                  if (processorResult == null
                        || (processorResult != null && processorResult.getEditPath() != null)) {
                     setStateValue(req, res, KEY_EDITPATH, editPath, false);
                  }
                  // stop processing, start rendering
                  return;
               }
            }
         }
      }
      catch (RuntimeException e) {
         throw new PortletException(e);
      }
   }

   @SuppressWarnings("unchecked")
   private Map<String, String[]> copyRequestParameters(PortletRequest req) {
      Map<String, String[]> parameters = new HashMap<String, String[]>(req.getParameterMap());
      return parameters;
   }

   private void setStateValue(ActionRequest req, ActionResponse res, String name,
         String value, boolean session) {
      if (session) {
         req.getPortletSession().setAttribute(formPostfix + "_" + name, value);
      }
      else {
         res.setRenderParameter(name, value);
      }
   }

   private void setStateValue(RenderRequest req, String name,
         String value, boolean session) {
      if (session) {
         req.getPortletSession().setAttribute(formPostfix + "_" + name, value);
      }
      else {
         req.setAttribute(name, value);
      }
   }

   private String getStateValue(String name, PortletRequest req, boolean session) {
      if (session) {
         return (String) req.getPortletSession().getAttribute(formPostfix + "_" + name);
      }
      else {
         return req.getParameter(name);
      }
   }

   private void storeParemetersInFields(Map<String,List<String>> parameters, ValueForm valueForm) {
      for (Entry<String, List<String>> param : parameters.entrySet()) {
         String formname = param.getKey();
         List<String> formvalue = param.getValue();
         if (formname != null && formvalue != null && formvalue.get(0) != null) {
            // remove trailing spaces from input values
            for (int i = 0; i < formvalue.size(); i++) {
               formvalue.set(i, formvalue.get(i).trim());
            }
            valueForm.storeField(formname, formvalue);
         }
      }
   }

   private void storeBinariesInFields(Map<String,DataSource> binaries, ValueForm valueForm) {
      for (Entry<String, DataSource> param : binaries.entrySet()) {
         String formname = param.getKey();
         DataSource formvalue = param.getValue();
         if (formname != null && formvalue != null) {
            valueForm.storeField(formname, formvalue);
         }
      }
   }
   
   private boolean notEmptyValue(List<String> value) {
      return value != null && !value.isEmpty() && value.get(0) != null;
   }

   private boolean isEmpty(String value) {
      return value == null || value.length() == 0;
   }

   @Override
   protected void doView(final RenderRequest req, final RenderResponse res) throws PortletException,
   IOException {
      Locale locale = null;
      ResourceBundle bundle = null;

      List<Locale> locales = getLocales(req);
      if (!locales.isEmpty()) {
         locale = locales.get(0);
         
         PortletPreferences preferences = req.getPreferences();
         String template = preferences.getValue(PortalConstants.CMSC_PORTLET_VIEW_TEMPLATE, null);
         String baseName = getResourceBaseName(template);
         bundle = getResourceBundle(locale, baseName);
      }
      
      Localization.localize(locale, bundle, new Localization.Action<Void>() {
         public Void run() throws PortletException, IOException {
            doViewLocalized(req, res);
            return null;
         }
      });
   }

   protected void doViewLocalized(final RenderRequest req, final RenderResponse res)
         throws PortletException, IOException {
      String formname = getFormName(req);
      // DataProviders could use this entry to get the ValueForm
      req.setAttribute(KEY_FORMKEY, formname );

      ValueForm valueForm = (ValueForm) req.getPortletSession().getAttribute(formname);
      if (isFirstTime(valueForm)) {
         valueForm = createValueForm(req, formname);
      }

      String activeStep = getStateValue(KEY_ACTIVESTEP, req, false);
      if (isEmpty(activeStep)) {
         PortletPreferences preferences = req.getPreferences();
         activeStep = preferences.getValue(KEY_ACTIVESTEP, null);
         if (isEmpty(activeStep)) {
            activeStep = valueForm.getDefinition().getForm().getFirstStep().getName();
         }
      }

      String editpath = getStateValue(KEY_EDITPATH, req, false);

      boolean validationError = Boolean.valueOf(getStateValue(KEY_VALIDATIONERROR, req, false))
            .booleanValue();

      // when there were errors during the processing of the form, include the error in the rendered xml
      String formProcessorError = getStateValue(KEY_ERROR, req, false);

      if (!valueForm.isDisplayonly(activeStep)) {
         if (editpath == null) {
            // Create new ValueObjects.
            editpath = valueForm.createDataObjects(activeStep);
         }

         if (!validationError) {
            // data providers
            Map<String, String[]> parameters = copyRequestParameters(req);
            valueForm.createData(activeStep, editpath, parameters);
         }
      }

      int expectedRenderSequence = 0;
      String expectedRenderSeq = getStateValue(KEY_SEQUENCE, req, true);
      if (!isEmpty(expectedRenderSeq)) {
         try {
            expectedRenderSequence = Integer.parseInt(expectedRenderSeq);
         }
         catch (NumberFormatException e) {
            logger.debug("expectedRenderSeq is not a number " + expectedRenderSeq + " "
                  + e.getMessage(), e);
         }
      }
      expectedRenderSequence++;
      setStateValue(req, KEY_SEQUENCE, String.valueOf(expectedRenderSequence), true);

      Document doc = XmlUtil.createDocument();
      valueForm.render(doc, activeStep, editpath, formProcessorError, expectedRenderSequence);
      String xml = XmlUtil.serializeDocument(doc);
      renderXml(req, res, xml);
   }
   
   private String getFormName(PortletRequest req) throws PortletException {
      PortletPreferences preferences = req.getPreferences();
      String formName = preferences.getValue(KEY_FORMKEY, null);
      if (isEmpty(formName)) {
         throw new PortletException("No form configured.");
      }
      return formName;
   }

   private boolean isFirstTime(ValueForm valueForm) {
      return valueForm == null;
   }

   private ValueForm createValueForm(PortletRequest req, String formname) throws PortletException {
      ValueForm valueForm;
      FormDefinition formDefinition = getFormDefinition(formname);
      if (formDefinition != null) {
         valueForm = formDefinition.createValueForm();
         req.getPortletSession().setAttribute(formname, valueForm);
         return valueForm;
      }
      else
         throw new PortletException("Formdefinition is not loaded.");
   }

   private void removeBadEditPath(Map<String,List<String>> parameters) {
      List<String> editpathparam = parameters.get(KEY_EDITPATH);
      if (editpathparam != null && "null".equals(editpathparam.get(0)))
         parameters.put(KEY_EDITPATH, null);
   }

   public FormDefinition getFormDefinition(String name) {
      DigesterLoader loader = new DigesterLoader();
      InputStream stream = getPortletContext().getResourceAsStream(DYNAMICFORMS_LOCATION + name);
      if (stream != null) {
         return loader.load(stream);         
      }
      return loader.load(name);
   }
   
   @SuppressWarnings("unchecked")
   private void processRequest(ActionRequest request, Map<String, List<String>> parameters,
         Map<String, DataSource> binaries) throws PortletException {
      try {
         if (PortletFileUpload.isMultipartContent(request)) {
            DiskFileItemFactory factory = new DiskFileItemFactory();
            PortletFileUpload upload = new PortletFileUpload(factory);
            upload.setHeaderEncoding(ENCODING_UTF8);
            List<FileItem> fileItems = upload.parseRequest(request);
      
            if (fileItems != null) {
               for (Iterator<FileItem> iterator = fileItems.iterator(); iterator.hasNext();) {
                  FileItem fileItem = iterator.next();
                  String paramName = fileItem.getFieldName();
                  if (fileItem.isFormField()) {
                     try {
                        String paramValue = fileItem.getString(ENCODING_UTF8);
                        addParam(parameters, paramName, paramValue);
                     }
                     catch (UnsupportedEncodingException e) {
                        getLogger().error("UnsupportedEncoding " + ENCODING_UTF8);
                     }
                  }
                  else {
                     if (StringUtils.isNotBlank(fileItem.getName())) {
                        long maxAttachmentSize = getMaxAttachmentSize();
                        if (fileItem.getSize() <= maxAttachmentSize) {
                           DataSource attachment = new FileItemDataSource(fileItem);
                           binaries.put(paramName, attachment);
                        }
                        else {
                           throw new PortletException("Size of file exceeds allowed filesize of " + maxAttachmentSize);
                        }
                     }
                  }
               }
            }
         }
         else {
            Map<String,String[]> reqParams = request.getParameterMap();
            for (Map.Entry<String, String[]> entry : reqParams.entrySet()) {  
               parameters.put(entry.getKey(), Arrays.asList(entry.getValue()));
            }
         }
      }
      catch (FileUploadException e) {
         getLogger().error("error parsing request", e);
         throw new PortletException("Failed to parse request parameters");
      }
   }

   private void addParam(Map<String, List<String>> parameters, String paramName,
         String paramValue) {
      List<String> parameterValues;
      if (parameters.containsKey(paramName)) {
         parameterValues = parameters.get(paramName);
      }
      else {
         parameterValues = new ArrayList<String>();
         parameters.put(paramName, parameterValues);
      }
      parameterValues.add(paramValue);
   }
   
   private long getMaxAttachmentSize() {
      long maxFileSize = DEFAULT_MAXFILESIZE;
      String maxFileSizeValue = PropertiesUtil.getProperty("email.maxattachmentsize");
      if (StringUtils.isNotBlank(maxFileSizeValue)) {
         try {
            maxFileSize = Integer.parseInt(maxFileSizeValue);
         }
         catch (NumberFormatException e) {
            getLogger().info(
                  "incorrect value for email.maxattachmentsize=" + maxFileSizeValue + ", default value="
                        + DEFAULT_MAXFILESIZE + " is used");
         }
      }
      return maxFileSize * MEGABYTE;
   }

   @Override
   protected void doEditDefaults(RenderRequest req, RenderResponse res) throws IOException,
         PortletException {
      addFormResourceInfo(req);
      super.doEditDefaults(req, res);
   }
   
   protected void addFormResourceInfo(RenderRequest req) {
      PortletPreferences preferences = req.getPreferences();

      List<String> formResources = new ArrayList<String>();
      
      Set<String> webInfResources = getPortletContext().getResourcePaths(DYNAMICFORMS_LOCATION);
      for (String resource : webInfResources) {
         String formResource = resource.substring(DYNAMICFORMS_LOCATION.length());
         formResources.add(formResource);
      }
      
      setAttribute(req, "formResources", formResources);

      String viewId = preferences.getValue(KEY_FORMKEY, null);
      if (StringUtils.isNotEmpty(viewId)) {
         setAttribute(req, KEY_FORMKEY, viewId);
      }
   }

   @Override
   protected void saveParameters(ActionRequest request, String portletId) {
      setPortletParameter(portletId, KEY_FORMKEY, request.getParameter(KEY_FORMKEY));
   }
   
}
