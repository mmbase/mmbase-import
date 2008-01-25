package com.finalist.cmsc.community.forms;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Remco Bos
 */
public class UserAddInitAction extends AbstractCommunityAction {

    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        UserForm userForm = (UserForm)actionForm;
        userForm.setAction("add");
        return actionMapping.findForward("success");
    }
}