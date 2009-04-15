package com.finalist.portlets.preferences;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.lang.StringUtils;

import com.finalist.cmsc.portlets.CmscPortlet;
import com.finalist.cmsc.services.community.domain.PreferenceVO;
import com.finalist.preferences.domain.UserProfile;
import com.finalist.preferences.util.ProfileUtil;

public class ProfilePortlet extends CmscPortlet {

   private static List<String> formUrls;

   enum PREFERENCETYPE {
      profile, preference
   };

   @Override
   protected void doEditDefaults(RenderRequest request, RenderResponse response) throws IOException, PortletException {
      doView(request, response);// doEditDefaults(request, response);
   }

   @Override
   protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
      if (!ProfileUtil.isUserLogin()) {
         request.setAttribute("isUserLogin", ProfileUtil.isUserLogin());
         doInclude("view", "/community/introduction.jsp", request, response);
         return;
      }
      String error = request.getParameter("errors");
      if (StringUtils.isNotBlank(error)) {
         String[] errors = error.split(",");
         request.setAttribute("errors", errors);
      }
      UserProfile profile = ProfileUtil.getUserProfile();
      request.setAttribute("profile", profile);
      doInclude("view", "/community/profile/user.jsp", request, response);
      
      if (formUrls == null) {
         fillAggregatedViewFiles(getPortletContext());
      }
      request.setAttribute("preferenceFormUrls", formUrls);
      
      List<PreferenceVO> preferences = ProfileUtil.getPreferences();
      request.setAttribute("preferences", preferences);
      
      doInclude("view", "/community/preferences.jsp", request, response);
   }

   protected static synchronized void fillAggregatedViewFiles(PortletContext portletContext) {
      //Fill formUrls only
      formUrls = new ArrayList<String>();
      Set<String> webInfResources = portletContext.getResourcePaths(getAggregationDir(portletContext) + "view/community/preferences");
      if (webInfResources != null) {
         for (String resource : webInfResources) {
            if (resource.lastIndexOf("/") > 0) {
               resource = "preferences" + resource.substring(resource.lastIndexOf("/"));
               formUrls.add(resource);
            }
         }
      }
   }

   @Override
   public void processView(ActionRequest request, ActionResponse response) throws PortletException, IOException {
      String preferenceType = request.getParameter("action");
      if (StringUtils.isNotEmpty(preferenceType)) {
         if (preferenceType.equalsIgnoreCase(PREFERENCETYPE.profile.toString())) {
            StringBuffer bf = new StringBuffer();
            UserProfile profile = new UserProfile();
            profile.setFirstName(request.getParameter("firstName"));
            profile.setAccount(request.getParameter("account"));
            profile.setPrefix(request.getParameter("prefix"));
            profile.setLastName(request.getParameter("lastName"));
            profile.setEmail(request.getParameter("email"));
            profile.setPasswordText(request.getParameter("passwordText"));
            profile.setPasswordConfirmation(request.getParameter("passwordConfirmation"));
            profile.setEmail(request.getParameter("email"));
            if (StringUtils.isBlank(profile.getEmail())) {
               bf.append("community.profile.email.empty,");
            }
            else if(!ProfileUtil.isEmail(profile.getEmail())) {
               bf.append("community.profile.email.incorrect,");
            }
            if (StringUtils.isNotBlank(profile.getPasswordText()) && StringUtils.isNotBlank(profile.getPasswordConfirmation())) {
               if (!profile.getPasswordText().equals(profile.getPasswordConfirmation())) {
                  bf.append("community.profile.password.not_equal");
               }
            }
            if (bf.length() > 0) {
               String errorKeys = bf.toString();
               response.setRenderParameter("errors", errorKeys.endsWith(",") ? errorKeys.substring(0, errorKeys.length() - 1) : errorKeys);
            }
            else {
               ProfileUtil.updateUserProfile(profile);
            }
         }
         else if (preferenceType.equalsIgnoreCase(PREFERENCETYPE.preference.toString())) {
            ProfileUtil.addPreferences(getPreferences(request));
         }

      }
      super.processView(request, response);

   }

   private static String getAggregationDir(PortletContext portletContext) {
      String aggregationDir = portletContext.getInitParameter("cmsc.portal.aggregation.base.dir");
      if (StringUtils.isEmpty(aggregationDir)) {
         aggregationDir = "/WEB-INF/templates/";
      }
      return aggregationDir;
   }

   @SuppressWarnings("unchecked")
   private List<PreferenceVO> getPreferences(ActionRequest request) {
      Enumeration<String> parameterNames = request.getParameterNames();
      List<PreferenceVO> preferences = new ArrayList<PreferenceVO>();
      while (parameterNames.hasMoreElements()) {
         String key = parameterNames.nextElement();
         int index = key.indexOf("_key_");
         if (index > 0) {
            String module = key.substring(0, index);
            PreferenceVO preference = new PreferenceVO();
            preference.setModule(module);
            preference.setKey(request.getParameter(key));
            preference.setValue(request.getParameter(module + "_value_" + key.substring(index + 5)));
            preference.setAuthenticationId(ProfileUtil.getCurrentUserId().toString());
            preferences.add(preference);
         }
      }
      return preferences;
   }

}
