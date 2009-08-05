package com.finalist.cmsc.fileupload.actions;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;

import com.finalist.cmsc.struts.MMBaseFormlessAction;

/**
 * Struts action to redirect a particular MMBase node to the correct editwizard.
 *
 * @author Auke van Leeuwen
 */
public class EditFileAction extends MMBaseFormlessAction {

	/** {@inheritDoc} */
	@Override
	public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {
		String action = getParameter(request, "action");

		if (StringUtils.isBlank(action)) {
			String objectnumber = getParameter(request, "id", true);

			ActionForward ret = new ActionForward(mapping.findForward("openwizard").getPath() + "?objectnumber="
					+ objectnumber + "&returnurl=" + mapping.findForward("returnurl").getPath());
			ret.setRedirect(true);
			return ret;
		} else {
			return mapping.findForward(SUCCESS);
		}
	}
}
