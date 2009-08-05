package com.finalist.cmsc.navigation.forms;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.storage.search.Step;

import com.finalist.cmsc.struts.MMBaseAction;
import com.finalist.cmsc.util.HttpUtil;

public class SelectSiteNameAction extends MMBaseAction {
   protected static final String EXIST = "1";
   public ActionForward execute(ActionMapping mapping, ActionForm form,
                                HttpServletRequest request, HttpServletResponse response,
                                Cloud cloud) throws Exception {
	
	    PrintWriter out = HttpUtil.getWriterForXml(response);
	    String siteName = request.getParameter("siteName"); 
	    String number =request.getParameter("number");
		NodeManager siteNodeManager = cloud.getNodeManager("site");
		NodeQuery query= cloud.createNodeQuery();
        Step step = query.addStep(siteNodeManager);
		query.setNodeStep(step);
	    SearchUtil.addEqualConstraint(query, siteNodeManager.getField("stagingfragment"), siteName);
	    NodeList result = query.getList();
	    int i=result.size();
	    if(i>0&&null!=number){
	    	Node n=result.getNode(0);
		    int dataNumber=n.getIntValue("number");
		    if(dataNumber==Integer.parseInt(number)){
		    	i=0;
		    }
	    }
	    if (i==0) {
	        out.print("0");
	    } else {
	        out.print(EXIST);
	    }
      return null;
   }

}
