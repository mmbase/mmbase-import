/**
 * 
 */
package com.finalist.cmsc.contenttype.forms;

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

import com.finalist.cmsc.beans.MMBaseNodeMapper;
import com.finalist.cmsc.beans.SortedNodetypeBean;
import com.finalist.cmsc.repository.AssetElementUtil;
import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.struts.MMBaseAction;


/**
 * @author Billy
 *
 */
public class ContentTypeAction extends MMBaseAction {
   
   public List<SortedNodetypeBean> getContentTypes(Cloud cloud, String searchword) {
      List<SortedNodetypeBean> result = new ArrayList<SortedNodetypeBean>();
      
      List<NodeManager> contentTypes = ContentElementUtil.getContentTypes(cloud);
      List<String> hiddenTypes = ContentElementUtil.getHiddenTypes();
      for (NodeManager nm : contentTypes) {
         if(isAsked(hiddenTypes, nm.getName(), searchword)){
            SortedNodetypeBean ct = MMBaseNodeMapper.copyNode(nm, SortedNodetypeBean.class);
            result.add(ct);
         }
      }
      
      List<NodeManager> assetTypes = AssetElementUtil.getAssetTypes(cloud);
      List<String> hiddenAssetTypes = AssetElementUtil.getHiddenAssetTypes();
      for (NodeManager nm : assetTypes) {
         if(isAsked(hiddenAssetTypes, nm.getName(), searchword)) {
            SortedNodetypeBean ct = MMBaseNodeMapper.copyNode(nm, SortedNodetypeBean.class);
            result.add(ct);
         }
      }
      
      return result;
   }
   
   private boolean isAsked(List<String> hiddenTypes, String name, String searchword){
      if (!hiddenTypes.contains(name)) {
         if(StringUtils.isEmpty(searchword)){
            return true;
         } else if(name.contains(searchword)) {
            return true;
         }
      }
      return false;
   }
   
   /* (non-Javadoc)
    * @see com.finalist.cmsc.struts.MMBaseAction#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.mmbase.bridge.Cloud)
    */
   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {
      
      String cmd = request.getParameter("cmd");
      String relationOriginNode = request.getParameter("objectnumber");
      String searchword = request.getParameter("searchvalue");
      
      List<SortedNodetypeBean> contentTypes = getContentTypes(cloud, searchword);
      
      Collections.sort(contentTypes);
      addToRequest(request, "cmd", cmd);
      addToRequest(request, "contentTypes", contentTypes);
      addToRequest(request, "relationOriginNode", relationOriginNode);
      addToRequest(request, "counttypes", "" + contentTypes.size());
      
      return mapping.findForward(SUCCESS);
   }

}
