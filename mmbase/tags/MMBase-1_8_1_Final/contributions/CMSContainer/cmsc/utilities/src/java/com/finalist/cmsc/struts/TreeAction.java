/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.struts;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.mmapps.commons.util.StringUtil;

import org.apache.struts.action.*;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.mmbase.TreeUtil;
import com.finalist.tree.TreeInfo;
import com.finalist.tree.ajax.AjaxTree;


public abstract class TreeAction extends MMBaseAction {

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response, Cloud cloud) throws Exception {

        TreeInfo info = getTreeInfo(cloud);
        
        String action = request.getParameter("action");
        if(!StringUtil.isEmpty(action)) {
            response.setContentType("text/xml");
            if ("expand".equals(action)) {
                String persistentid = request.getParameter("persistentid");
                info.expand(Integer.parseInt(persistentid));
            }
            if ("collapse".equals(action)) {
                String persistentid = request.getParameter("persistentid");
                info.collapse(Integer.parseInt(persistentid));
            }
            if ("inittree".equals(action)) {
                PrintWriter out = response.getWriter();    
                String persistentid = request.getParameter("persistentid");
                AjaxTree t = getTree(request, cloud, info, persistentid);
                t.render(out);
            }
            if ("loadchildren".equals(action)) {
                PrintWriter out = response.getWriter();    
                String persistentid = request.getParameter("persistentid");
                AjaxTree t = getTree(request, cloud, info, persistentid);
                t.renderChildren(out, persistentid);
            }
            if ("autocomplete".equals(action)) {
                String path = request.getParameter("path");
                if (path != null && path.indexOf(TreeUtil.PATH_SEPARATOR) < 0){
                    path = null;
                }
                List children = getChildren(cloud, path);
                PrintWriter out = response.getWriter();    
                out.write("<options>");
                for (Iterator iter = children.iterator(); iter.hasNext();) {
                    String element = (String) iter.next();
                    out.write("<option>"+element+"</option>");                    
                }
                out.write("</options>");
            }
            return null;
        }
        else {
            String channel = getChannelId(request, cloud);
            if (!StringUtil.isEmpty(channel)) {
                Node channelNode = cloud.getNode(channel);
                List openChannels = getOpenChannels(channelNode);
                if (openChannels != null) {
                    for (Iterator iter = openChannels.iterator(); iter.hasNext();) {
                        Node node = (Node) iter.next();
                        info.expand(node.getNumber());
                    }
                    addToRequest(request, "channel", channelNode);
                }
            }
            else {
                Node rootNode = getRootNode(cloud);
                if (rootNode != null) {
                    addToRequest(request, "channel", rootNode);
                }
            }
            
            ActionForward ret = mapping.findForward(SUCCESS);
            return ret;
        }
    }

    protected String getChannelId(HttpServletRequest request, Cloud cloud) {
        return request.getParameter("channel");
    }

    protected abstract TreeInfo getTreeInfo(Cloud cloud);
    protected abstract Node getRootNode(Cloud cloud);
    protected abstract List getOpenChannels(Node channelNode);
    protected abstract AjaxTree getTree(HttpServletRequest request, Cloud cloud, TreeInfo info, String persistentid);
    protected abstract List getChildren(Cloud cloud, String path);

}
