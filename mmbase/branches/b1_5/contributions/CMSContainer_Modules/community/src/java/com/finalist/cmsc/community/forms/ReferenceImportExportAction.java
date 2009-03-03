package com.finalist.cmsc.community.forms;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import org.supercsv.cellprocessor.ConvertNullTo;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.constraint.StrRegEx;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCSVException;
import org.supercsv.exception.SuperCSVReflectionException;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.finalist.cmsc.services.community.domain.CommunityExport;
import com.finalist.cmsc.services.community.domain.CommunityExportForCsvVO;
import com.finalist.cmsc.services.community.domain.PersonExportImportVO;
import com.finalist.cmsc.services.community.person.PersonService;
import com.finalist.cmsc.services.community.preferences.Preference;
import com.finalist.cmsc.services.community.security.Authentication;
import com.finalist.cmsc.services.community.security.Authority;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * @author nikko
 * 
 */
public class ReferenceImportExportAction extends DispatchAction {
   private static Log log = LogFactory.getLog(ReferenceImportExportAction.class);
   private PersonService personService;
   static final CellProcessor[] userProcessors = new CellProcessor[] { null, new NotNull(), new NotNull(), null, null,
         null, null, null, null, null, new NotNull(new StrRegEx("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")),
         null };

   public ActionForward listGroups(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response) throws IOException {
      List<Authority> groups = personService.getAllAuthorities();
      request.setAttribute("groups", groups);
      return mapping.findForward("show");
   }

   public ActionForward showImportPage(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response) throws IOException {
      String groupId = request.getParameter("imGroupId");
      request.setAttribute("groupId", groupId);
      return mapping.findForward("failed");
   }

   /**
    * @param mapping
    *           do nothing
    * @param form
    *           do nothing
    * @param request
    *           do nothing
    * @param response
    *           setContentType
    * @return ActionForward do nothing
    * @throws IOException
    *            toXML
    */
   public ActionForward export(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response) throws IOException {
      log.debug("Export Susbscriptions");
      ReferenceImportUploadForm groupForm = (ReferenceImportUploadForm) form;
      String groupId = groupForm.getGroups();
      CommunityExport communityExport;
      if (StringUtils.equalsIgnoreCase("0", groupId)) {
         communityExport = new CommunityExport(personService.getPersonExportImportVO());
      } else {
         communityExport = new CommunityExport(personService.getPersonExportImportVO(groupId));
      }
      String fileType = request.getParameter("fileType");
      if (StringUtils.equalsIgnoreCase("xml", fileType)) {
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
      } else if (StringUtils.equalsIgnoreCase("csv", fileType)) {
         response.setContentType("text/csv");
         response.setHeader("Content-Disposition", "attachment; filename=subscriptions.csv");
         response.setHeader("Cache-Control", "no-store");
         ICsvBeanWriter writer = new CsvBeanWriter(response.getWriter(), CsvPreference.EXCEL_PREFERENCE);
         String[] header = new String[] { "authenticationId", "authenticationUserId", "authenticationPassword",
               "preferenceId", "preferenceModule", "preferenceKey", "preferenceValue", "firstName", "lastName",
               "infix", "email", "active" };
         ConvertNullTo constraint = new ConvertNullTo("");
         CellProcessor[] processor = new CellProcessor[header.length];
         for (int i = 0; i < processor.length; i++) {
            processor[i] = constraint;
         }
         List<CommunityExportForCsvVO> communityCsvVos = fillCommunityVO(communityExport);
         try {
            writer.writeHeader(header);
            for (CommunityExportForCsvVO communityCsvVo : communityCsvVos) {
               writer.write(communityCsvVo, header, processor);
            }
         } catch (SuperCSVReflectionException e) {
            e.printStackTrace();
         } finally {
            writer.close();
         }
      }
      return mapping.findForward(null);
   }

   private List<CommunityExportForCsvVO> fillCommunityVO(CommunityExport communityExport) {
      List<CommunityExportForCsvVO> communityCsvVos = new ArrayList<CommunityExportForCsvVO>();
      for (PersonExportImportVO personExImVO : communityExport.getUsers()) {
         Authentication auth = personExImVO.getAuthentication();
         if (0 != personExImVO.getPreferences().size()) {
            for (Preference preference : personExImVO.getPreferences()) {
               CommunityExportForCsvVO communityVO = fillBasicCommunityExForVO(personExImVO, auth);
               communityVO.setPreferenceId(preference.getId().toString());
               communityVO.setPreferenceKey(preference.getKey());
               communityVO.setPreferenceModule(preference.getModule());
               communityVO.setPreferenceValue(preference.getValue());
               communityCsvVos.add(communityVO);
            }
         } else {
            CommunityExportForCsvVO communityVO = fillBasicCommunityExForVO(personExImVO, auth);
            communityCsvVos.add(communityVO);
         }
      }
      return communityCsvVos;
   }

   private CommunityExportForCsvVO fillBasicCommunityExForVO(PersonExportImportVO personExImVO, Authentication auth) {
      CommunityExportForCsvVO communityVO = new CommunityExportForCsvVO();
      communityVO.setAuthenticationId(auth.getId().toString());
      communityVO.setAuthenticationPassword(auth.getPassword());
      communityVO.setAuthenticationUserId(auth.getUserId());
      communityVO.setActive(personExImVO.getActive());
      communityVO.setFirstName(personExImVO.getFirstName());
      communityVO.setInfix(personExImVO.getInfix());
      communityVO.setLastName(personExImVO.getLastName());
      communityVO.setEmail(personExImVO.getEmail());
      return communityVO;
   }

   /**
    * @param mapping
    *           goto jsp
    * @param form
    *           get paramate
    * @param request
    *           save massages
    * @param response
    *           do nothing
    * @return ActionForward chose jsp
    * @throws IOException
    *            about treating XML
    */
   public ActionForward importsubscription(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response) throws IOException {
      String groupId = request.getParameter("groupId");
      ReferenceImportUploadForm myForm = (ReferenceImportUploadForm) form;
      FormFile myFile = myForm.getFile();
      byte[] fileData = myFile.getFileData();
      String contentType = myFile.getContentType();
      boolean isXML = "text/xml".equals(contentType);
      boolean isCSV = "text/csv".equals(contentType);
      String level = myForm.getLevel();
      ActionMessages messages = new ActionMessages();
      if (!isXML && !isCSV) {
//         messages.add("invalidMessage", new ActionMessage("datafile.unsupport"));
//         saveMessages(request, messages);
         request.setAttribute("warning", 1);
         request.setAttribute("invalidMessage", "datafile.unsupport");
         return mapping.findForward("failed");
      }

      if (isXML) {
         try {
            int size = importFromFile(fileData, level);
            request.setAttribute("confirm_userNum", size);
         } catch (Exception e) {
            log.error(e);
//            messages.add("invalidMessage", new ActionMessage("datafile.invalid"));
//            saveMessages(request, messages);
            request.setAttribute("warning", 1);
            request.setAttribute("groupId", groupId);
            request.setAttribute("invalidMessage", "datafile.invalid");
            return mapping.findForward("failed");
         }
      } else if (isCSV) {
         InputStreamReader ir = new InputStreamReader(myFile.getInputStream());
         ICsvBeanReader inFile = new CsvBeanReader(ir, CsvPreference.EXCEL_PREFERENCE);
         try {
            String[] header = inFile.getCSVHeader(true);
            if (header.length != userProcessors.length) {
               ir = new InputStreamReader(myFile.getInputStream());
               inFile = new CsvBeanReader(ir, CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE);
               header = inFile.getCSVHeader(true);
            }
            Map<String, PersonExportImportVO> personVOMap = fillPersonVO(groupId, inFile, header);
            if(personVOMap == null) {
//               messages.add("invalidMessage", new ActionMessage("datafile.invalid.data"));
//               saveMessages(request, messages);
               request.setAttribute("warning", 1);
               request.setAttribute("groupId", groupId);
               request.setAttribute("invalidMessage", "datafile.invalid.data");
               return mapping.findForward("failed");
            }
            if ("clean".equals(level)) {
               batchCleanRecord();
            }
            Iterator<String> it = personVOMap.keySet().iterator();
            while (it.hasNext()) {
               personService.importDataFromFileRecord(level, personVOMap.get(it.next()));
            }
            request.setAttribute("confirm_userNum", personVOMap.size());
         } catch (Exception e) {
            log.error(e);
//            messages.add("invalidMessage", new ActionMessage("datafile.invalid"));
//            saveMessages(request, messages);
            request.setAttribute("warning", 1);
            request.setAttribute("groupId", groupId);
            request.setAttribute("invalidMessage", "datafile.invalid");
            return mapping.findForward("failed");
         } finally {
            inFile.close();
         }
      }
      return mapping.findForward("success");
   }

   private Map<String, PersonExportImportVO> fillPersonVO(String groupId, ICsvBeanReader inFile, String[] header)
         throws IOException {
      CommunityExportForCsvVO communityVO;
      Map<String, PersonExportImportVO> personVOMap = new HashMap<String, PersonExportImportVO>();
      while ((communityVO = inFile.read(CommunityExportForCsvVO.class, header, userProcessors)) != null) {
         if(StringUtils.isEmpty(communityVO.getAuthenticationUserId()) || StringUtils.isEmpty(communityVO.getAuthenticationPassword()) || StringUtils.isEmpty(communityVO.getEmail())) {
            return null;
         }
         PersonExportImportVO personExImVO = new PersonExportImportVO();
         Authentication auth = new Authentication();
         List<Preference> preferences = new ArrayList<Preference>();
         String authUserId = communityVO.getAuthenticationUserId();
         if (personVOMap.containsKey(authUserId)) {
            Preference pre = addPreference(communityVO);
            personVOMap.get(authUserId).getPreferences().add(pre);
         } else {
            if (StringUtils.isNotEmpty(communityVO.getAuthenticationId())) {
               auth.setId(new Long(communityVO.getAuthenticationId()));
            }
            auth.setUserId(communityVO.getAuthenticationUserId());
            auth.setPassword(communityVO.getAuthenticationPassword());
            personExImVO.setAuthentication(auth);
            personExImVO.setFirstName(communityVO.getFirstName());
            personExImVO.setLastName(communityVO.getLastName());
            personExImVO.setInfix(communityVO.getInfix());
            personExImVO.setEmail(communityVO.getEmail());
            personExImVO.setRegisterDate(new Date(System.currentTimeMillis()));
            if (StringUtils.isNotBlank(communityVO.getActive())) {
               personExImVO.setActive(communityVO.getActive());
            } else {
               personExImVO.setActive("active");
            }
            Preference pre = addPreference(communityVO);
            preferences.add(pre);
            personExImVO.setPreferences(preferences);
            if (null != groupId && groupId != "0" && groupId != "") {
               personExImVO.setAuthorityId(new Long(groupId));
            }
            personVOMap.put(authUserId, personExImVO);
         }
      }
      return personVOMap;
   }

   private Preference addPreference(CommunityExportForCsvVO communityVO) {
      Preference pre = new Preference();
      if (StringUtils.isNotEmpty(communityVO.getPreferenceId())) {
         pre.setId(new Long(communityVO.getPreferenceId()));
      }
      pre.setKey(communityVO.getPreferenceKey());
      pre.setModule(communityVO.getPreferenceModule());
      pre.setValue(communityVO.getPreferenceValue());
      return pre;
   }

   private int importFromFile(byte[] fileData, String level) throws Exception {
      String xml = new String(fileData);
      CommunityExport communityExport;
      communityExport = (CommunityExport) getXStream().fromXML(xml);
      List<PersonExportImportVO> xpersons = communityExport.getUsers();
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
      return xpersons.size();
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
   private boolean isCorretFile(ICsvBeanReader inFile , String[] header) throws SuperCSVReflectionException, SuperCSVException, IOException{
      CommunityExportForCsvVO communityVO;
      boolean isCorrectFile = true;
      while ((communityVO = inFile.read(CommunityExportForCsvVO.class, header, userProcessors)) != null) {
         if(StringUtils.isEmpty(communityVO.getAuthenticationUserId()) || StringUtils.isEmpty(communityVO.getAuthenticationPassword()) || StringUtils.isEmpty(communityVO.getEmail())) {
            isCorrectFile = false;
            break;
         }
      }
      return isCorrectFile;
   }
}
