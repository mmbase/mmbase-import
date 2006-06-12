package com.finalist.cmsc.repository;

import javax.servlet.http.HttpServletRequest;

import org.mmbase.bridge.Node;

import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.security.UserRole;
import com.finalist.cmsc.struts.JstlUtil;
import com.finalist.tree.*;

/**
 * Renderer of the Repository tree.
 * 
 * @author Nico Klasens (Finalist IT Group)
 */
public abstract class RepositoryRenderer implements TreeCellRenderer {

    private String target;
    private HttpServletRequest request;

    public RepositoryRenderer(HttpServletRequest request, String target) {
        this.request = request;
        this.target = target;
    }

    /**
     * @see com.finalist.tree.TreeCellRenderer#getElement(TreeModel, Object, String)
     */
    public TreeElement getElement(TreeModel model, Object node, String id) {
        Node parentNode = (Node) node;
        if (id == null) {
            id = String.valueOf(parentNode.getNumber());
        }
        UserRole role = RepositoryUtil.getRoleForUser(parentNode.getCloud(), parentNode, false);

        String name = parentNode.getStringValue("name");
        String fragment = parentNode.getStringValue( RepositoryUtil.getFragmentFieldname(parentNode) );

        String action = "content.jsp?parentchannel=" + parentNode.getNumber();
        
        TreeElement element = createElement(getIcon(node), id, name, fragment, action, target);

        int level = parentNode.getIntValue("level");

        if (role != null && SecurityUtil.isWriter(role)) {
            addWriterOptions(parentNode, element);
            if (SecurityUtil.isEditor(role)) {    
                addEditorOptions(parentNode, element, model, level);
                if (SecurityUtil.isChiefEditor(role)) {    
                    addChiefEditorOptions(parentNode, element, level);
                    if (SecurityUtil.isWebmaster(role)) {    
                        addWebmasterOptions(parentNode, element);
                    }
                }
            }
        }

        return element;
    }

    private void addWriterOptions(Node parentNode, TreeElement element) {
//        String label = JstlUtil.getMessage(request, "repository.content.edit");
//        element.addOption(createOption("new_content.gif", label,
//                "content.jsp?parentchannel=" + parentNode.getNumber(), target));
    }

    private void addEditorOptions(Node parentNode, TreeElement element, TreeModel model, int level) {
        String labelEdit = JstlUtil.getMessage(request, "repository.channel.edit");
        element.addOption(createOption("existing.gif", labelEdit, 
                "ChannelEdit.do?number=" + parentNode.getNumber(), target));
        
        if (RepositoryUtil.countLinkedContent(parentNode) >= 2) {
            String label = JstlUtil.getMessage(request, "repository.content.reorder");
            element.addOption(createOption("reorder.gif", label,
                    "ReorderAction.do?parent=" + parentNode.getNumber(), target));
        }
   
        if (level > 1) {
            if ((model.getChildCount(parentNode) == 0)) {
                String label = JstlUtil.getMessage(request, "repository.channel.remove");
                element.addOption(createOption("remove.gif", label,
                        "ChannelDelete.do?number=" + parentNode.getNumber(), target));
            }
        }
        String labelNew = JstlUtil.getMessage(request, "repository.channel.new");
        element.addOption(createOption("new_contentchannel.gif", labelNew,
                        "ChannelCreate.do?parentchannel=" + parentNode.getNumber(), target));
    }

    private void addChiefEditorOptions(Node parentNode, TreeElement element, int level) {
        if (level > 1) {
            String labelCut = JstlUtil.getMessage(request, "repository.channel.cut");
            element.addOption(createOption("cut.gif", labelCut, "javascript:cut('"
                    + parentNode.getNumber() + "');", null));
            String labelCopy = JstlUtil.getMessage(request, "repository.channel.copy");
            element.addOption(createOption("copy.gif", labelCopy, "javascript:copy('"
                    + parentNode.getNumber() + "');", null));
        }
        String labelPaste = JstlUtil.getMessage(request, "repository.channel.paste");
        element.addOption(createOption("paste.gif", labelPaste, "javascript:paste('"
                + parentNode.getNumber() + "');", null));
    }

    private void addWebmasterOptions(Node parentNode, TreeElement element) {
        String label = JstlUtil.getMessage(request, "repository.channel.xml");
        // Only show the xml for admins, because we don't want
        // to bother editors with this kind of information.
        element.addOption(createOption("xml.gif", label,
              "xmlview/index.jsp?number=" + parentNode.getNumber(), target));
    }
    
    public String getIcon(Object node) {
        Node n = (Node) node;
        return "type/" + n.getNodeManager().getName() + ".gif";
    }

    protected abstract TreeOption createOption(String icon, String label, String action, String target);

    protected abstract TreeElement createElement(String icon, String id, String name, String fragment, String action, String target);
    
}