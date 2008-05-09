package com.finalist.cmsc.security.forms;

import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.struts.MMBaseAction;
import org.apache.struts.action.*;
import org.apache.struts.util.LabelValueBean;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * GroupAction
 * 
 * @author Nico Klasens
 */
public class GroupAction extends MMBaseAction {

   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {
      if (!isCancelled(request)) {
         GroupForm groupForm = (GroupForm) form;
         List<LabelValueBean> membersList = new ArrayList<LabelValueBean>();
         List<LabelValueBean> usersList = new ArrayList<LabelValueBean>();
         // get all users
         NodeList users = SecurityUtil.getUsers(cloud);

         for (Iterator<Node> iter = users.iterator(); iter.hasNext();) {
            Node user = iter.next();
            String label = getLabel(user);
            LabelValueBean bean = new LabelValueBean(label, String.valueOf(user.getNumber()));
            usersList.add(bean);

         }
         // get members and remove them from users
         for (String memberNumber : groupForm.getMembers()) {
            Node member = cloud.getNode(memberNumber);
            String label = getLabel(member);
            LabelValueBean beanMember = new LabelValueBean(label, String.valueOf(member.getNumber()));
            membersList.add(beanMember);
            usersList.remove(beanMember);
         }

         addToRequest(request, "membersList", membersList);
         addToRequest(request, "usersList", usersList);

         // validate
         ActionMessages errors = new ActionMessages();

         Node groupNode = getOrCreateNode(groupForm, cloud, SecurityUtil.GROUP);
         if (groupForm.getId() == -1) {
            if (groupForm.getName() == null || groupForm.getName().length() < 3) {
               errors.add("groupname", new ActionMessage("error.groupname.invalid"));
               saveErrors(request, errors);
               return mapping.getInputForward();
            }
            else {
               String name = groupForm.getName();
               NodeList list = MMBaseAction.getCloudFromSession(request).getNodeManager("mmbasegroups").getList(
                     "name='" + name + "'", null, null);
               if (list.size() != 0) {
                  errors.add("groupname", new ActionMessage("error.groupname.alreadyexists"));
                  saveErrors(request, errors);
                  return mapping.getInputForward();
               }
            }

            groupNode.setStringValue("name", groupForm.getName());
         }

         groupNode.setStringValue("description", groupForm.getDescription());
         groupNode.commit();
         SecurityUtil.setGroupMembers(cloud, groupNode, groupForm.getMembers());
      }
      removeFromSession(request, form);
      return mapping.findForward(SUCCESS);
   }


   private String getLabel(Node user) {
      String label = user.getStringValue("username") + " {" + SecurityUtil.getFullname(user) + ")";
      return label;
   }
}
