package com.finalist.cmsc.taglib.form;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.bridge.Cloud;

import com.finalist.cmsc.editwizard.WizardMaker;
import com.finalist.cmsc.taglib.DumpDefaultsTag;

/**
 * Create a link to a Editwizard
 * 
 * Freek: I marked this class as depricated, because the WizardInitAction should be used
 * and the implementation is incomplete
 * 
 * @deprecated
 * 
 * @author Wouter Heijke
 * @version $Revision: 1.1 $
 */
public class WizardTag extends SimpleTagSupport {
	private static Log log = LogFactory.getLog(DumpDefaultsTag.class);

	private String action;

	private String number;

	private String returnurl;

	private String popup;

	private String creation;

	private String contenttype;


	private String var;

	public void doTag() throws JspException, IOException {
		PageContext ctx = (PageContext) getJspContext();
		HttpServletRequest request = (HttpServletRequest) ctx.getRequest();

		Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();

		WizardMaker w = new WizardMaker(request, cloud);

		w.setObjectNumber(number);
		w.setContentType(contenttype);
		w.setLanguage(cloud.getLocale().getLanguage());
		w.setAction(action);

		String newlink = request.getContextPath() + w.makeWizard();

		// handle result
		if (var != null) {
			// put in variable
			if (newlink != null) {
				request.setAttribute(var, newlink);
			} else {
				request.removeAttribute(var);
			}
		} else {
			// write
			ctx.getOut().print(newlink);
		}

	}

	public void setAction(String action) {
		this.action = action;
	}

	public void setContenttype(String contenttype) {
		this.contenttype = contenttype;
	}

	public void setCreation(String creation) {
		this.creation = creation;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public void setPopup(String popup) {
		this.popup = popup;
	}

	public void setReturnurl(String returnurl) {
		this.returnurl = returnurl;
	}

	public void setVar(String var) {
		this.var = var;
	}

}
