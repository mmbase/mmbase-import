package com.finalist.cmsc.navigation;

import javax.servlet.http.*;

import net.sf.mmapps.commons.util.HttpUtil;

import org.mmbase.bridge.Node;

import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.security.UserRole;
import com.finalist.cmsc.util.bundles.JstlUtil;
import com.finalist.tree.*;

/**
 * Renderer of the Site management tree.
 * 
 * @author Nico Klasens (Finalist IT Group)
 */
public abstract class NavigationRenderer implements TreeCellRenderer {

    private String target = null;
    private HttpServletRequest request;
    private HttpServletResponse response;

    public NavigationRenderer(HttpServletRequest request, HttpServletResponse response, String target) {
        this.request = request;
        this.response = response;
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
        
        boolean secure = parentNode.getBooleanValue(PagesUtil.SECURE_FIELD);
        String action = null;
        if (ServerUtil.useServerName()) {
            String[] pathElements = NavigationUtil.getPathElementsToRoot(parentNode, true);

            action = HttpUtil.getWebappUri(request, pathElements[0], secure);
            for (int i = 1; i < pathElements.length; i++) {
                action += pathElements[i] + "/";
            }
            if (!request.getServerName().equals(pathElements[0])) {
                action = HttpUtil.addSessionId(request, action);
            }
            else {
                action = response.encodeURL(action);
            }
        }
        else {
            String path = NavigationUtil.getPathToRootString(parentNode, true);
            String webappuri = HttpUtil.getWebappUri(request, secure);
            action = response.encodeURL(webappuri + path);
        }
        
        TreeElement element = createElement(getIcon(node), id, name, fragment, action, target);
        
        if (role != null && SecurityUtil.isWriter(role)) {
            if (isPage) {
                if (SecurityUtil.isEditor(role)) {
                    String labelPageEdit = JstlUtil.getMessage(request, "site.page.edit");
                    element.addOption(createOption("edit.png", labelPageEdit,
                        getUrl("PageEdit.do?number=" + parentNode.getNumber()), target));

                    if ((model.getChildCount(parentNode) == 0)) {
                        String labelPageRemove = JstlUtil.getMessage(request, "site.page.remove");
                        element.addOption(createOption("delete.png", labelPageRemove,
                                getUrl("PageDelete.do?number=" + parentNode.getNumber()), target));
                    }
                }
            }
            else {
                if (SecurityUtil.isChiefEditor(role)) {
                    String labelSiteEdit = JstlUtil.getMessage(request, "site.site.edit");
                    element.addOption(createOption("edit.png", labelSiteEdit,
                            getUrl("SiteEdit.do?number=" + parentNode.getNumber()), target));

                    if ((model.getChildCount(parentNode) == 0)) {
                        String labelSiteRemove = JstlUtil.getMessage(request, "site.site.remove");
                        element.addOption(createOption("delete.png", labelSiteRemove,
                            getUrl("SiteDelete.do?number=" + parentNode.getNumber()), target));
                    }
                }
            }
            if (SecurityUtil.isEditor(role)) {
                String labelPageNew = JstlUtil.getMessage(request, "site.page.new");
                element.addOption(createOption("new.png", labelPageNew,
                        getUrl("PageCreate.do?parentpage=" + parentNode.getNumber()), target));
                if (NavigationUtil.getChildCount(parentNode) >= 2) {
                    String labelPageReorder = JstlUtil.getMessage(request, "site.page.reorder");
                    element.addOption(createOption("reorder.png", labelPageReorder, 
                            getUrl("reorder.jsp?parent=" + parentNode.getNumber()), target));
                }
                
                if (SecurityUtil.isChiefEditor(role)) {
                    if (isPage) {
                        String labelPageCut = JstlUtil.getMessage(request, "site.page.cut");
                        element.addOption(createOption("cut.png", labelPageCut, "javascript:cut('"
                                + parentNode.getNumber() + "');", null));
                        String labelPageCopy = JstlUtil.getMessage(request, "site.page.copy");
                        element.addOption(createOption("copy.png", labelPageCopy, "javascript:copy('"
                                + parentNode.getNumber() + "');", null));
                    }
                    String labelPagePaste = JstlUtil.getMessage(request, "site.page.paste");
                    element.addOption(createOption("paste.png", labelPagePaste, "javascript:paste('"
                            + parentNode.getNumber() + "');", null));
                }
            }
        }
        return element;
    }

    private String getUrl(String url) {
        return response.encodeURL(url);
    }

    public String getIcon(Object node) {
        Node n = (Node) node;
        return "type/" + n.getNodeManager().getName() + ".png";
    }

    protected abstract TreeOption createOption(String icon, String label, String action, String target);

    protected abstract TreeElement createElement(String icon, String id, String name, String fragment, String action, String target);
    
}