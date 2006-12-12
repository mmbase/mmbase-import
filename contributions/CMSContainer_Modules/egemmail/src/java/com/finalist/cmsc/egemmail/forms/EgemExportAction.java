package com.finalist.cmsc.egemmail.forms;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.ContextProvider;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.mmbase.ResourcesUtil;
import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.struts.MMBaseFormlessAction;
import com.finalist.util.http.HttpUtil;

public class EgemExportAction extends MMBaseFormlessAction {

    private static final String EGEMMAIL_URL = "egemmail.url";
	private static final String EGEMMAIL_ADMIN_USER = "egemmail.admin.user";
	private static final String EGEMMAIL_ADMIN_PASSWORD = "egemmail.admin.password";
	private static final String EGEMMAIL_BEHEER_URL = "egemmail.beheer.url";
	private static final String EGEMMAIL_LIVEPATH = "egemmail.livepath";
	
	
	private static Logger log = Logging.getLoggerInstance(EgemExportAction.class.getName());

	@SuppressWarnings("unchecked")
	public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {

		int good = 0;
		int wrong = 0;

		Map<String,String[]> params = request.getParameterMap();
		for(Iterator<String> i = params.keySet().iterator();i.hasNext();) {
			String key = i.next();
			if(key.startsWith("export_")) {
				String url = PropertiesUtil.getProperty(EGEMMAIL_URL);
				String user = PropertiesUtil.getProperty(EGEMMAIL_ADMIN_USER);
				String password = PropertiesUtil.getProperty(EGEMMAIL_ADMIN_PASSWORD);

				HashMap postParams = new HashMap();
				postParams.put("user", user);
				postParams.put("password", password);
				
				String number = key.substring(key.indexOf("_")+1);
				Node node = cloud.getNode(number);
				postParams.put("title", node.getStringValue("title"));
				postParams.put("teaser", buildTeaser(node));
				String liveUrl = getContentUrl(cloud, node);
				if(liveUrl != null) {
					postParams.put("url", liveUrl);
	
					String response = HttpUtil.doPost(url, postParams).trim();
					
					if(response.equals("ok")) {
						good++;
					}
					else {
						wrong++;
						log.warn("Received error response:\n"+response);
					}
				}
				else {
					log.warn("Cloud not find live node for: node.number");
				}
			}
		}
		
		request.setAttribute("good", new Integer(good));
		request.setAttribute("wrong", new Integer(wrong));
		return mapping.findForward(SUCCESS);
	}

    private String getContentUrl(Cloud cloud, Node node) {
    	NodeList remoteNodes = cloud.getNodeManager("remotenodes").getList("sourcenumber = "+node.getNumber(), null, null);
    	if(remoteNodes.size() == 0) {
    		return null;
    	}
    	else {
			String livePath = PropertiesUtil.getProperty(EGEMMAIL_LIVEPATH);
    		return livePath + "/content/" + remoteNodes.getNode(0).getStringValue("destinationnumber") + "/" + node.getStringValue("title");
    	}
    }
	
	private String buildTeaser(Node node) {
		if(node.getNodeManager().hasField("intro")) {
			String intro = node.getStringValue("intro");
			if(intro != null && intro.length() > 0) {
				return intro.replaceAll("<.*?>","");
			}
		}
		
		if(node.getNodeManager().hasField("body")) {
			String body = node.getStringValue("body");
			if(body != null && body.length() > 0) {
				String messageBody = body.replaceAll("<.*?>","");
				if(messageBody.length() > 300) {
					int bestIndex = Math.max(messageBody.lastIndexOf(" ", 300), messageBody.lastIndexOf(".", 300)+1); 
					messageBody = messageBody.substring(0,bestIndex);
				}
				return messageBody;
			}
		}
		
		// no field found, just use an empty field
		return "";
	}
}
