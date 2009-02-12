/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.taglib.stats;

import java.io.IOException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import net.sf.mmapps.commons.util.EncodingUtil;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.commons.lang.StringUtils;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.navigation.ServerUtil;
import com.finalist.cmsc.repository.RepositoryUtil;

public class GoogleAnalyticsTag extends SimpleTagSupport {
	/** MMbase logging system */
	private static Logger log = Logging
			.getLoggerInstance(GoogleAnalyticsTag.class.getName());

	private final static String TYPE_BASIC = "basic"; // init and page counter code (default)
	private final static String TYPE_EVENT = "event"; // event code, category and action are required

	private String accountParameter;
	private String categoryParameter;
	private String actionParameter;
	private String nodeNumberParameter;
	private String labelParameter;
	private String valueParameter;
	private String typeParameter = TYPE_BASIC;

	private static String contextAccount;
	static {
		InitialContext context;
		try {
			context = new InitialContext();
			Context env = (Context) context.lookup("java:comp/env");
			contextAccount = (String) env.lookup("googleAnalytics/account");
		} catch (NamingException e) {
			log.error("problems getting google context account", e);
		}
	}

	@Override
	public void doTag() throws IOException {

		/*
		 * Find out where to get our account from, search order: 1) The
		 * "account"-parameter passed to the tag (only when available, live and
		 * production) 2) The "googleAnalytics/account" setting in the context
		 * XML (only when available) 3) The "googleanalytics.account" system
		 * property, from the system properties (only when available, live and
		 * production)
		 */
		String account = null;
		boolean isLiveProduction = (ServerUtil.isLive() && ServerUtil
				.isProduction());
		if (StringUtils.isNotBlank(accountParameter) && isLiveProduction) {
			account = accountParameter;
		} else if (contextAccount != null) {
			account = contextAccount;
		} else if (isLiveProduction) {
			account = PropertiesUtil.getProperty("googleanalytics.account");
		}

		// Include the google analytics code
		if (StringUtils.isNotBlank(account)) {

			StringBuilder javascript = new StringBuilder();
			javascript.append("<script type=\"text/javascript\">");
			if (typeParameter.equals(TYPE_BASIC)) {
				javascript.append("\r\nvar gaJsHost = ((\"https:\" == document.location.protocol) ? \"https://ssl.\" : \"http://www.\");\r\n");
				javascript.append("document.write(unescape(\"%3Cscript src='\" + gaJsHost + \"google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E\"));\r\n");
				javascript.append("</script>\r\n");
				javascript.append("<script type=\"text/javascript\">\r\n");
				javascript.append("try{\r\n");
				javascript.append("var pageTracker = _gat._getTracker(\"");
				javascript.append(account);
				javascript.append("\");\r\n");
				javascript.append("pageTracker._trackPageview();\r\n");
				javascript.append("} catch(err) {}\r\n");
			}

			if (typeParameter.equals(TYPE_EVENT)) {
				if (StringUtils.isNotBlank(nodeNumberParameter)) {
					actionParameter = getActionFromNodeNumber(nodeNumberParameter);
				}

				if (StringUtils.isBlank(categoryParameter)
						|| StringUtils.isBlank(actionParameter)) {
					throw new IllegalArgumentException(
							"Both category and (action or nodeNumber) parameters are required when using type "
									+ TYPE_EVENT);
				}
				javascript.append("pageTracker._trackEvent('");
				javascript.append(escapeParameter(categoryParameter));
				javascript.append("','");
				javascript.append(escapeParameter(actionParameter));
				if (StringUtils.isNotBlank(labelParameter)) {
					javascript.append("','");
					javascript.append(escapeParameter(labelParameter));
					if (StringUtils.isNotBlank(valueParameter)) {
						javascript.append("','");
						javascript.append(valueParameter);
					}
				}
				javascript.append("');\r\n");
			}

			javascript.append("</script>\r\n");

			PageContext ctx = (PageContext) getJspContext();
			ctx.getOut().write(javascript.toString());
		}
	}

	private String escapeParameter(String parameter) {
		return parameter.replace("'", "\\'");
	}

	private String getActionFromNodeNumber(String nodeNumber) {
        Cloud cloud = CloudProviderFactory.getCloudProvider().getAnonymousCloud();
        Node node = cloud.getNode(nodeNumber);
		Node creationchannel = RepositoryUtil.getCreationChannel(node);

		String prefix = node.getNodeManager().getName();

		String fullpath = creationchannel.getStringValue("path");
		String path = StringUtils.removeStart(fullpath, "Repository/");
		String title = EncodingUtil.convertNonAscii(node.getStringValue("title"));
		title = filterTitle(title);

		StringBuilder contentCounterName = new StringBuilder();
		contentCounterName.append(prefix);
		contentCounterName.append("/");
		contentCounterName.append(path);
		contentCounterName.append("/");
		contentCounterName.append(nodeNumber);
		contentCounterName.append("_");
		contentCounterName.append(title);
		return contentCounterName.toString();
	}

	private String filterTitle(String title) {
		// make sure the title will not break into different path nodes
		return title.replace('/', '_');
	}

	public void setAccount(String account) {
		this.accountParameter = account;
	}

	public void setType(String type) {
		if (type.equals(TYPE_BASIC) || type.equals(TYPE_EVENT)) {
			this.typeParameter = type;
		} else {
			throw new IllegalArgumentException(
					"type parameter should be empty, \"" + TYPE_BASIC
							+ "\", \"" + TYPE_EVENT + "\"");
		}
	}

	public void setCategory(String categoryParameter) {
		this.categoryParameter = categoryParameter;
	}

	public void setNodeNumber(String nodeNumberParameter) {
		this.nodeNumberParameter = nodeNumberParameter;
	}

	public void setAction(String actionParameter) {
		this.actionParameter = actionParameter;
	}

	public void setLabel(String labelParameter) {
		this.labelParameter = labelParameter;
	}

	public void setValue(String valueParameter) {
		this.valueParameter = valueParameter;
	}

}
