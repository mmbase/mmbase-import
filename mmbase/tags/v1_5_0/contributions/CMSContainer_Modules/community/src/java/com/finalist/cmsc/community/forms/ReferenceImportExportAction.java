package com.finalist.cmsc.community.forms;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.actions.DispatchAction;
import org.apache.struts.upload.FormFile;
import com.finalist.cmsc.services.community.domain.CommunityExport;
import com.finalist.cmsc.services.community.domain.PersonExportImportVO;
import com.finalist.cmsc.services.community.person.PersonService;
import com.finalist.cmsc.services.community.preferences.Preference;
import com.finalist.cmsc.services.community.security.Authentication;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * @author nikko
 *
 */
public class ReferenceImportExportAction extends DispatchAction {
   private static Log log = LogFactory.getLog(ReferenceImportExportAction.class);
   private PersonService personService;

   /**
    * @param mapping do nothing 
    * @param form do nothing 
    * @param request do nothing 
    * @param response setContentType
    * @return ActionForward do nothing 
    * @throws IOException toXML
    */
   public ActionForward export(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response) throws IOException {
      log.debug("Export Susbscriptions");
      CommunityExport communityExport = new CommunityExport(personService.getPersonExportImportVO());
      String xml = getXStream().toXML(communityExport);
      byte[] bytes = xml.getBytes();
      response.setContentType("text/xml");
      response.setContentLength(bytes.length);
      response.setHeader("Content-Disposition", "attachment; filename=subscriptions.xml");
      response.setHeader("Cache-Control", "no-store");
      response.flushBuffer();
      OutputStream outStream = response.getOutputStream();
      outStream.write(bytes);
      outStream.flush();
      return mapping.findForward(null);
   }

   /**
    * @param mapping goto jsp
    * @param form get paramate
    * @param request save massages
    * @param response do nothing
    * @return ActionForward chose jsp
    * @throws IOException about treating XML
    */
   public ActionForward importsubscription(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response) throws IOException {
      ReferenceImportUploadForm myForm = (ReferenceImportUploadForm) form;
      FormFile myFile = myForm.getDatafile();
      byte[] fileData = myFile.getFileData();
      String contentType = myFile.getContentType();
      boolean isXML = "text/xml".equals(contentType);
      String level = myForm.getLevel();
      ActionMessages messages = new ActionMessages();
      if (!isXML) {
         messages.add("file", new ActionMessage("datafile.unsupport"));
         saveMessages(request, messages);
         return mapping.findForward("failed");
      }
      try {
         importFromFile(fileData, level);
      } catch (Exception e) {
         log.error(e);
         messages.add("file", new ActionMessage("datafile.invalid"));
         saveMessages(request, messages);
         return mapping.findForward("failed");
      }

      return mapping.findForward("success");
   }

   private void importFromFile(byte[] fileData, String level) throws Exception {
      String xml = new String(fileData);
      CommunityExport communityExport;
      communityExport = (CommunityExport) getXStream().fromXML(xml);
      List < PersonExportImportVO > xpersons = communityExport.getUsers();
      if ("clean".equals(level)) {
         batchCleanRecord();
      }
      for (PersonExportImportVO importPerson : xpersons) {
         Authentication authentication = importPerson.getAuthentication();
         if (null == authentication || StringUtils.isWhitespace(authentication.getUserId())
               || StringUtils.isWhitespace(authentication.getPassword())) {
            continue;
         }
         personService.addRelationRecord(level, importPerson);
      }
   }

   private void batchCleanRecord() throws Exception {
      personService.batchClean();
   }

   private XStream getXStream() {
      XStream xstream = new XStream(new DomDriver());
      xstream.alias("community-export", CommunityExport.class);
      xstream.alias("user", PersonExportImportVO.class);
      xstream.alias("authentication", Authentication.class);
      xstream.alias("preference", Preference.class);
      return xstream;
   }

   public static void setLog(Log log) {
      ReferenceImportExportAction.log = log;
   }

   public void setPersonService(PersonService personService) {
      this.personService = personService;

   }

   public static Log getLog() {
      return log;
   }
}
