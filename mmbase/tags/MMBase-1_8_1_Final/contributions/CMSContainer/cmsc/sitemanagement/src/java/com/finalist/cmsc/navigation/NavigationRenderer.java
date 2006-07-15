package com.finalist.cmsc.navigation;

import javax.servlet.http.HttpServletRequest;

import org.mmbase.bridge.Node;

import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.security.UserRole;
import com.finalist.cmsc.struts.JstlUtil;
import com.finalist.tree.*;

/**
 * Renderer of the Site management tree.
 * 
 * @author Nico Klasens (Finalist IT Group)
 */
public abstract class NavigationRenderer implements TreeCellRenderer {

    private String target = null;
    private HttpServletRequest request;

    public NavigationRenderer(HttpServletRequest request, String target) {
        this.request = request;
        this.target = target;
    }

    /**
     * Render een node van de tree.
     * 
     * @see com.finalist.tree.TreeCellRenderer#getElement(TreeModel, Object, String)
     */
    public TreeElement getElement(TreeModel model, Object node, String id) {
        Node parentNode = (Node) node;
        if (id == null) {
            id = String.valueOf(parentNode.getNumber());
        }
        
        UserRole role = NavigationUtil.getRoleForUser(parentNode.getCloud(), parentNode, false);

        boolean isPage = PagesUtil.isPage(parentNode);
        
        String titleFieldName = isPage ? PagesUtil.TITLE_FIELD : SiteUtil.TITLE_FIELD;
        String name = parentNode.getStringValue(titleFieldName);
        
        String fragment = parentNode.getStringValue( NavigationUtil.getFragmentFieldname(parentNode) );
        
        String action = "../../" + NavigationUtil.getPathToRootString(parentNode, !ServerUtil.useServerName());

        TreeElement element = createElement(getIcon(node), id, name, fragment, action, target);
        
        if (role != null && SecurityUtil.isWriter(role)) {
            if (isPage) {
                if (SecurityUtil.isEditor(role)) {
                    String labelPageEdit = JstlUtil.getMessage(request, "site.page.edit");
                    element.addOption(createOption("existing.gif", labelPageEdit,
                        "PageEdit.do?number=" + parentNode.getNumber(), target));

                    if ((model.getChildCount(parentNode) == 0)) {
                        String labelPageRemove = JstlUtil.getMessage(request, "site.page.remove");
                        element.addOption(createOption("remove.gif", labelPageRemove,
                                "PageDelete.do?number=" + parentNode.getNumber(), target));
                    }
                }
            }
            else {
                if (SecurityUtil.isChiefEditor(role)) {
                    String labelSiteEdit = JstlUtil.getMessage(request, "site.site.edit");
                    element.addOption(createOption("existing.gif", labelSiteEdit,
                        "SiteEdit.do?number=" + parentNode.getNumber(), target));

                    if ((model.getChildCount(parentNode) == 0)) {
                        String labelSiteRemove = JstlUtil.getMessage(request, "site.site.remove");
                        element.addOption(createOption("remove.gif", labelSiteRemove,
                                "SiteDelete.do?number=" + parentNode.getNumber(), target));
                    }
                }
            }
            if (SecurityUtil.isEditor(role)) {
                String labelPageNew = JstlUtil.getMessage(request, "site.page.new");
                element.addOption(createOption("new_page.gif", labelPageNew,
                                "PageCreate.do?parentpage=" + parentNode.getNumber(), target));
                if (NavigationUtil.getChildCount(parentNode) >= 2) {
                    String labelPageReorder = JstlUtil.getMessage(request, "site.page.reorder");
                    element.addOption(createOption("reorder.gif", labelPageReorder, "reorder.jsp?parent="
                            + parentNode.getNumber(), target));
                }
                
                if (SecurityUtil.isChiefEditor(role)) {
                    if (isPage) {
                        String labelPageCut = JstlUtil.getMessage(request, "site.page.cut");
                        element.addOption(createOption("cut.gif", labelPageCut, "javascript:cut('"
                                + parentNode.getNumber() + "');", null));
                        String labelPageCopy = JstlUtil.getMessage(request, "site.page.copy");
                        element.addOption(createOption("copy.gif", labelPageCopy, "javascript:copy('"
                                + parentNode.getNumber() + "');", null));
                    }
                    String labelPagePaste = JstlUtil.getMessage(request, "site.page.paste");
                    element.addOption(createOption("paste.gif", labelPagePaste, "javascript:paste('"
                            + parentNode.getNumber() + "');", null));
                }
            }
        }
        return element;
    }

    public String getIcon(Object node) {
        Node n = (Node) node;
        return "type/" + n.getNodeManager().getName() + ".gif";
    }

    protected abstract TreeOption createOption(String icon, String label, String action, String target);

    protected abstract TreeElement createElement(String icon, String id, String name, String fragment, String action, String target);
    
}