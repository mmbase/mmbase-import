package com.finalist.cmsc.dataconversion.forms;

import java.util.Properties;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.dataconversion.service.ConversionThread;
import com.finalist.cmsc.struts.MMBaseFormlessAction;

public class ConverseAction extends MMBaseFormlessAction {
   
   private static final Logger log = Logging.getLoggerInstance(ConverseAction.class.getName());
   @Override
   public ActionForward execute(ActionMapping mapping,HttpServletRequest request,
          Cloud cloud) throws Exception {
      Properties properties = new Properties();
      copyProperties(properties,request);
      request.setAttribute("uuid",properties.getProperty("uuid"));
      ConversionThread starter = new ConversionThread(properties, getServlet().getServletContext());
      starter.start();
      
      return mapping.findForward(SUCCESS);
   }
   
   private void  copyProperties(Properties properties,HttpServletRequest request) {
   String driver = getParameter(request,"driver");
      String url = getParameter(request,"url");
      String user = getParameter(request, "user");
      String password = getParameter(request, "password");
      String node =  getParameter(request, "node");
      
      properties.put("driverClassName", driver);
      properties.put("url", url);
      properties.put("username", user);
      properties.put("password", password);
      properties.put("node", node);
      properties.put("uuid", UUID.randomUUID().toString());
      if(log.isDebugEnabled()) {
         log.debug("DB connections info.-> dirver:"+driver+" ; url="+url+" ; user="+user+" ;password ="+password);
      }
   }
   
  
}
