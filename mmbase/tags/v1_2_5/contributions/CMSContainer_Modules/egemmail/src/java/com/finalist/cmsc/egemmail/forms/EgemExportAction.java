package com.finalist.cmsc.egemmail.forms;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.mmbase.ResourcesUtil;
import com.finalist.cmsc.services.publish.Publish;
import com.finalist.cmsc.services.search.Search;
import com.finalist.cmsc.struts.MMBaseFormlessAction;
import com.finalist.util.http.HttpUtil;

public class EgemExportAction extends MMBaseFormlessAction {

    private static final String EGEMMAIL_URL = "egemmail.url";
	private static final String EGEMMAIL_ADMIN_USER = "egemmail.admin.user";
	private static final String EGEMMAIL_ADMIN_PASSWORD = "egemmail.admin.password";
//	private static final String EGEMMAIL_BEHEER_URL = "egemmail.beheer.url";
	private static final String EGEMMAIL_LIVEPATH = "egemmail.livepath";
	
	
	private static Logger log = Logging.getLoggerInstance(EgemExportAction.class.getName());

	@SuppressWarnings("unchecked")
	public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {

      String egemmailUrl = PropertiesUtil.getProperty(EGEMMAIL_URL);
      String egemmailUser = PropertiesUtil.getProperty(EGEMMAIL_ADMIN_USER);
      String egemmailPassword = PropertiesUtil.getProperty(EGEMMAIL_ADMIN_PASSWORD);
      
		int good = 0;
		int wrong = 0;
      int notOnLive = 0;

		Map<String,String[]> params = request.getParameterMap();
		for (String key : params.keySet()) {
			if(key.startsWith("export_")) {
				Map<String, Object> postParams = new HashMap<String, Object>();
				
				String number = key.substring(key.indexOf("_")+1);
				Node node = cloud.getNode(number);
            String liveUrl = getContentUrl(cloud, node);
            if(liveUrl != null) {
               postParams.put("url", liveUrl);
               
               postParams.put("user", egemmailUser);
               postParams.put("password", egemmailPassword);

               postParams.put("title", node.getStringValue("title"));
   				postParams.put("teaser", buildTeaser(node));
               
					String response = HttpUtil.doPost(egemmailUrl, postParams).trim();
					
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
               notOnLive++;
				}
			}
		}
		
		request.setAttribute("good", good);
      request.setAttribute("wrong", wrong);
      request.setAttribute("notOnLive", notOnLive);
		return mapping.findForward(SUCCESS);
	}

    private String getContentUrl(Cloud cloud, Node node) {
        if (Publish.isPublished(node) && Search.hasContentPages(node)) {
             NodeList remoteNodes = cloud.getNodeManager("remotenodes").getList("sourcenumber = "+node.getNumber(), null, null);
             int remoteNumber = remoteNodes.getNode(0).getIntValue("destinationnumber");
             String appPath = ResourcesUtil.getServletPathWithAssociation("content", "/content/*", 
                     String.valueOf(remoteNumber), node.getStringValue("title"));
             String livePath = PropertiesUtil.getProperty(EGEMMAIL_LIVEPATH);
             return livePath + appPath;
    	}
        return null;
    }
    
	private String buildTeaser(Node node) {
		if(node.getNodeManager().hasField("intro")) {
			String intro = node.getStringValue("intro");
			if(intro != null && intro.length() > 0) {
				return replaceHtml(intro);
			}
		}
		
		if(node.getNodeManager().hasField("body")) {
			String body = node.getStringValue("body");
			if(body != null && body.length() > 0) {
				String messageBody = replaceHtml(body);
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

    private String replaceHtml(String str) {
        String message = str.replaceAll("<.*?>","");
         message = message.replace("&quot;", "\"");
         message = message.replace("&#039;", "\'");
        return message;
    }
}
