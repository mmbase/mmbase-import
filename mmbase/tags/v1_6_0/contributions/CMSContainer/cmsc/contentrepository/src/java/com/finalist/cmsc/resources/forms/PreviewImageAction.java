package com.finalist.cmsc.resources.forms;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeManager;

import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.struts.MMBaseAction;

public class PreviewImageAction extends MMBaseAction {
   
   private final static String IMAGE_ID = "imageId";
   private final static String IMAGE_NAME = "imageBame";
   private final static String IMAGE_TITLE = "imageTitle";
   private final static String ELEMENT = "element";
   private final static String ELEMENT_SIZE = "size";
   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {
      try {
         String imgId=request.getParameter(IMAGE_ID);
         String size=request.getParameter("more");
         int imageId = 0;
         if (!StringUtils.isEmpty(imgId)) {
            imageId = Integer.parseInt(imgId);
         }
         if (StringUtils.isEmpty(size)) {
            size = "3";
         }
         NodeManager imageMaager=cloud.getNodeManager("images");
         Node imageNode=imageMaager.getCloud().getNode(imageId);
         String filename=imageNode.getStringValue("filename");
         String imageTitle=imageNode.getStringValue("title"); 
         List<Node> contentNodes =imageNode.getRelatedNodes("contentelement");
         List contentElements = getContentInfoList(imageNode,Integer.parseInt(size),contentNodes);
         request.setAttribute(IMAGE_ID,imgId);
         request.setAttribute(IMAGE_NAME,filename);
         request.setAttribute(IMAGE_TITLE,imageTitle);
         request.setAttribute(ELEMENT, contentElements);
         request.setAttribute(ELEMENT_SIZE, contentNodes.size()-Integer.parseInt(size));
      } catch (Exception e) {
         return mapping.findForward(CANCEL);
      }
      
      return mapping.findForward(SUCCESS);
   }
   private List getContentInfoList(Node imageNode,int size,List<Node> contentNodes) {
     
      List contentElements=new ArrayList();
      for (int i=0;i<contentNodes.size();i++) {
         if(i<size){
         Node element = (Node) contentNodes.get(i);
         Map<String, Object> contentElement = new HashMap<String, Object>();
         String GUIname= element.getNodeManager().getName();
         String title=element.getStringValue("title");
         int objectnumber=element.getIntValue("number");
         Node channelNode=RepositoryUtil.getCreationChannel(element);
         contentElement.put("tilte", title);
         contentElement.put("number", element.getIntValue("number"));
         contentElement.put("channel",channelNode.getStringValue("name") );
         contentElement.put("type", GUIname);
         contentElement.put("objectnumber", objectnumber);
         contentElements.add(contentElement);   
         }
      }
      return contentElements;
   }

}
