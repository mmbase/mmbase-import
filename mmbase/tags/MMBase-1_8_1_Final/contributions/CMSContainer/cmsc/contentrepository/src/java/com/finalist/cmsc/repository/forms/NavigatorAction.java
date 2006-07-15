/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.repository.forms;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.mmapps.commons.util.StringUtil;

import org.apache.struts.action.*;
import org.mmbase.bridge.*;

import com.finalist.cmsc.mmbase.TreeUtil;
import com.finalist.cmsc.repository.*;
import com.finalist.cmsc.struts.*;
import com.finalist.tree.TreeInfo;
import com.finalist.tree.ajax.AjaxTree;

public class NavigatorAction extends TreeAction {

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response, Cloud cloud) throws Exception {

        JstlUtil.setResourceBundle(request, "cmsc-repository");
        return super.execute(mapping, form, request, response, cloud);
    }

    protected Node getRootNode(Cloud cloud) {
        return RepositoryUtil.getRootNode(cloud);
    }

    protected TreeInfo getTreeInfo(Cloud cloud) {
        TreeInfo info = RepositoryUtil.getRepositoryInfo(cloud);
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
        RepositoryAjaxRenderer chr = new RepositoryAjaxRenderer(request, "content");
        AjaxTree t = new AjaxTree(model, chr, info);
        t.setImgBaseUrl("../img/");
        return t;
    }

    @Override
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
