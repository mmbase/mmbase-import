package com.finalist.cmsc.security.forms;

import java.util.*;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import org.apache.struts.util.LabelValueBean;
import org.mmbase.bridge.*;

import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.struts.MMBaseAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Nico Klasens
 */
public class GroupInitAction extends MMBaseAction {

   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {
      String id = request.getParameter("id");
      GroupForm groupForm = (GroupForm) form;

      List<LabelValueBean> usersList = new ArrayList<LabelValueBean>();
      List<LabelValueBean> membersList = new ArrayList<LabelValueBean>();

      NodeList users = SecurityUtil.getUsers(cloud);
      if (id != null) {
         Node node = cloud.getNode(id);
         groupForm.setName(node.getStringValue("name"));
         groupForm.setDescription(node.getStringValue("description"));
         groupForm.setId(Integer.parseInt(id));
         NodeList members = SecurityUtil.getMembers(node);
         for (Iterator<Node> iter = users.iterator(); iter.hasNext();) {
            Node user = iter.next();
            String label = getLabel(user);
            LabelValueBean bean = new LabelValueBean(label, String.valueOf(user.getNumber()));
            if (members.contains(user)) {
               membersList.add(bean);
            }
            else {
               usersList.add(bean);
            }
         }
      }
      else {
         // new
         groupForm.reset(mapping, request);
         groupForm.setId(-1);
         for (Iterator<Node> iter = users.iterator(); iter.hasNext();) {
            Node user = iter.next();
            String label = getLabel(user);
            LabelValueBean bean = new LabelValueBean(label, String.valueOf(user.getNumber()));
            usersList.add(bean);
         }
      }
      addToRequest(request, "membersList", membersList);
      addToRequest(request, "usersList", usersList);

      return mapping.findForward(SUCCESS);
   }


   private String getLabel(Node user) {
      String label = user.getStringValue("username") + " {" + SecurityUtil.getFullname(user) + ")";
      return label;
   }
}
