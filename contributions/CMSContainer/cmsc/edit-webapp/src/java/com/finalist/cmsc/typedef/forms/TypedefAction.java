/**
 * 
 */
package com.finalist.cmsc.typedef.forms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.NodeManager;

import com.finalist.cmsc.repository.AssetElementUtil;
import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.struts.MMBaseAction;


/**
 * @author Billy
 *
 */
public class TypedefAction extends MMBaseAction {

   /* (non-Javadoc)
    * @see com.finalist.cmsc.struts.MMBaseAction#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.mmbase.bridge.Cloud)
    */
   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {
      
      String cmd = request.getParameter("cmd");
      String relationOriginNode = request.getParameter("objectnumber");
      String searchvalue = request.getParameter("searchvalue");
      List<Typedef> typeList = new ArrayList<Typedef>();
      List<NodeManager> types = ContentElementUtil.getContentTypes(cloud);
      List<String> hiddenTypes = ContentElementUtil.getHiddenTypes();
      for (NodeManager manager : types) {
         String name = manager.getName();
         int number = manager.getNumber();
         if (!hiddenTypes.contains(name)) {
            if(StringUtils.isEmpty(searchvalue)){
               Typedef bean = new Typedef(number, name);
               typeList.add(bean);
            } else if(name.contains(searchvalue)) {
               Typedef bean = new Typedef(number, name);
               typeList.add(bean);               
            }
         }
      }
      
      List<NodeManager> assetTypes = AssetElementUtil.getAssetTypes(cloud);
      List<String> hiddenAssetTypes = AssetElementUtil.getHiddenAssetTypes();
      for (NodeManager manager : assetTypes) {
         String name = manager.getName();
         int number = manager.getNumber();
         if (!hiddenAssetTypes.contains(name)) {
            if(StringUtils.isEmpty(searchvalue)){
               Typedef bean = new Typedef(number, name);
               typeList.add(bean);
            } else if(name.contains(searchvalue)) {
               Typedef bean = new Typedef(number, name);
               typeList.add(bean);               
            }
         }
      }

      Collections.sort(typeList);
      addToRequest(request, "cmd", cmd);
      addToRequest(request, "typeList", typeList);
      addToRequest(request, "relationOriginNode", relationOriginNode);
      addToRequest(request, "counttypes", "" + typeList.size());
      
      return mapping.findForward(SUCCESS);
   }

}
