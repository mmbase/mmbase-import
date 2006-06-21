/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.repository.select.forms;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.mmapps.commons.util.StringUtil;

import org.apache.struts.action.*;
import org.mmbase.bridge.*;

import com.finalist.cmsc.mmbase.TreeUtil;
import com.finalist.cmsc.repository.*;
import com.finalist.cmsc.repository.select.SelectRenderer;
import com.finalist.cmsc.struts.JstlUtil;
import com.finalist.tree.TreeInfo;
import com.finalist.tree.ajax.AjaxTree;
import com.finalist.tree.ajax.SelectAjaxRenderer;


public class SelectorAction extends com.finalist.cmsc.struts.SelectorAction {

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response, Cloud cloud) throws Exception {

        String action = request.getParameter("action");
        if(StringUtil.isEmpty(action)) {
            RepositoryInfo info = new RepositoryInfo(RepositoryUtil.getRepositoryInfo(cloud));
            cloud.setProperty("Selector" + RepositoryInfo.class.getName(), info);
        }
        
        JstlUtil.setResourceBundle(request, "cmsc-repository");
        return super.execute(mapping, form, request, response, cloud);
    }

    @Override
    protected String getChannelId(HttpServletRequest request, Cloud cloud) {
        String path = request.getParameter("path");
        if(!StringUtil.isEmpty(path)) {
            Node parentNode = RepositoryUtil.getChannelFromPath(cloud, path);
            if(parentNode != null) {
                return String.valueOf(parentNode.getNumber());
            }
        }
        return super.getChannelId(request, cloud);
    }
    
    protected Node getRootNode(Cloud cloud) {
        return RepositoryUtil.getRootNode(cloud);
    }

    protected TreeInfo getTreeInfo(Cloud cloud) {
        TreeInfo info = (TreeInfo) cloud.getProperty("Selector" + RepositoryInfo.class.getName());
        return info;
    }

    protected List getOpenChannels(Node channelNode) {
        if (RepositoryUtil.isChannel(channelNode)) {
            return RepositoryUtil.getPathToRoot(channelNode);
        }
        return null;
    }

    protected AjaxTree getTree(HttpServletRequest request, Cloud cloud, TreeInfo info, String persistentid) {
        RepositoryTreeModel model = new RepositoryTreeModel(cloud);
        SelectAjaxRenderer chr = new SelectRenderer(getLinkPattern(), getTarget());
        AjaxTree t = new AjaxTree(model, chr, info);
        t.setImgBaseUrl("../../img/");
        return t;
    }
    
    protected List getChildren(Cloud cloud, String path) {
        List<String> strings = new ArrayList<String>();
        if (StringUtil.isEmpty(path)) {
            Node parentNode = RepositoryUtil.getRootNode(cloud);
            strings.add(parentNode.getStringValue(TreeUtil.PATH_FIELD));
        }
        else {
            Node parentNode = RepositoryUtil.getChannelFromPath(cloud, path);
            if(parentNode != null) {
                NodeList children = RepositoryUtil.getChildren(parentNode);
                for (Iterator iter = children.iterator(); iter.hasNext();) {
                    Node child = (Node) iter.next();
                    strings.add(child.getStringValue(TreeUtil.PATH_FIELD));
                }
            }
        }
        return strings;
    }
    
}
