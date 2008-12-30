package com.finalist.cmsc.repository.forms;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;

import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.services.publish.Publish;
import com.finalist.cmsc.services.workflow.Workflow;
import com.finalist.cmsc.struts.MMBaseFormlessAction;

public class ContentMassDeleteAction extends MMBaseFormlessAction {

   @SuppressWarnings("unchecked")
   @Override
   public ActionForward execute(ActionMapping mapping,
         HttpServletRequest request, Cloud cloud) throws Exception {
      
      String action = getParameter(request, "action");
      String channelnumber = getParameter(request, "channelnumber");
      String offset = getParameter(request, "offset");
      String orderBy = getParameter(request, "orderby");
      String direction = getParameter(request, "direction");
      Node channelNode = cloud.getNode(channelnumber);
      List<Integer> numbers = new ArrayList<Integer>();
      
      if(StringUtils.isEmpty(direction)) {
         direction = "down";
      }
      
      if (action == null) {
         Enumeration<String> parameters = request.getParameterNames();
         while (parameters.hasMoreElements()) {
            String parameter = parameters.nextElement();
   
            if (parameter.startsWith("chk_")) {
               String number = request.getParameter(parameter);
               Node contentNode = cloud.getNode(number);
               
               if (RepositoryUtil.isCreationChannel(contentNode, channelNode)) {
                  NodeList contentchannels = RepositoryUtil.getContentChannelsForContent(contentNode);
                  if (contentchannels.size() <= 1) {
                     RepositoryUtil.removeContentFromChannel(contentNode, channelNode);
                     RepositoryUtil.removeCreationRelForContent(contentNode);
                     RepositoryUtil.addContentToChannel(contentNode, RepositoryUtil.getTrash(cloud));
         
                     // unpublish and remove from workflow
                     Publish.remove(contentNode);
                     Workflow.remove(contentNode);
                     Publish.unpublish(contentNode);
                  }
                  else{
                     numbers.add(contentNode.getNumber());
                  }
               }
               else {
                  RepositoryUtil.removeContentFromChannel(contentNode, channelNode);
               }
            }
         }
         if(numbers.size() > 0) {
           request.getSession().setAttribute("objectNumbers",numbers);
         }
      }

         String destinationnumber = getParameter(request, "destionationchannel");
         if (StringUtils.isNotEmpty(destinationnumber)) {
            Node newCreationNode = cloud.getNode(destinationnumber);
            String objectnumber = getParameter(request, "objectnumber");
            Node contentNode = cloud.getNode(objectnumber);
            
            RepositoryUtil.removeContentFromChannel(contentNode, channelNode);
            RepositoryUtil.removeCreationRelForContent(contentNode);

            if (RepositoryUtil.isTrash(newCreationNode)) {
               RepositoryUtil.removeContentFromAllChannels(contentNode);
               RepositoryUtil.addContentToChannel(contentNode, newCreationNode.getStringValue("number"));

               // unpublish and remove from workflow
               Publish.remove(contentNode);
               Publish.unpublish(contentNode);
               Workflow.remove(contentNode);
            }
            else {
               RepositoryUtil.addCreationChannel(contentNode, newCreationNode);
            }
            if(request.getSession().getAttribute("objectNumbers") != null) {
               List<Integer> objectNumbers = (List)request.getSession().getAttribute("objectNumbers");
               objectNumbers.remove(Integer.valueOf(objectnumber));
            }
         }

         if( request.getSession().getAttribute("objectNumbers") != null){
            List<Integer> objectNumbers  =  (List)request.getSession().getAttribute("objectNumbers");
            if(!objectNumbers.isEmpty()) {
               Integer objectNumber = objectNumbers.get(0);
               Node contentNode = cloud.getNode(String.valueOf(objectNumber));
               NodeList contentchannels = RepositoryUtil.getContentChannelsForContent(contentNode);
               addToRequest(request, "content", contentNode);
               addToRequest(request, "creationchannel", channelNode);
               addToRequest(request, "contentchannels", contentchannels);
               addToRequest(request, "trashchannel", RepositoryUtil.getTrashNode(cloud));
               addToRequest(request, "action", "massmove");
               addToRequest(request, "offset", offset);
               addToRequest(request, "orderby", orderBy);
               addToRequest(request, "direction", direction);
               return mapping.findForward("unlinkcreation");
            }

         }
        
         String url = mapping.findForward(SUCCESS).getPath() + "?parentchannel=" + channelnumber;
         if(StringUtils.isNotEmpty(offset)) {
            url += "&offset="+offset;
         }
         if(StringUtils.isNotEmpty(orderBy)) {
            url += "&orderby="+orderBy;
         }
         if(StringUtils.isNotEmpty(direction)) {
            url += "&direction="+direction;
         }
         return new ActionForward(url, true);
   }

}
