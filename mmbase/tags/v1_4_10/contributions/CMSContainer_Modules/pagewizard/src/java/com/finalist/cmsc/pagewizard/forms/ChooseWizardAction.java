package com.finalist.cmsc.pagewizard.forms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import com.finalist.cmsc.struts.MMBaseAction;

public class ChooseWizardAction extends MMBaseAction {

   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {
      PageWizardForm wizardForm = (PageWizardForm) form;
      wizardForm.loadDefinition(cloud);

      return mapping.findForward(SUCCESS);
   }

}
