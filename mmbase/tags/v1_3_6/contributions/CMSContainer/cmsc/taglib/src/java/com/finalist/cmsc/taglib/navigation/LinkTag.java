/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.taglib.navigation;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspFragment;

import org.apache.commons.lang.StringUtils;

import com.finalist.cmsc.beans.om.*;
import com.finalist.cmsc.navigation.ServerUtil;
import com.finalist.cmsc.services.search.Search;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;
import com.finalist.cmsc.taglib.CmscTag;
import com.finalist.pluto.portalImpl.core.PortalURL;

/**
 * A tag to make URLs to other locations in the website.
 * 
 * @author Wouter Heijke
 * @author R.W. van 't Veer
 */
public class LinkTag extends CmscTag {

    
	/**
	 * JSP variable name.
	 */
	public String var;
	
    private String element;
    private String window;
    private String urlfragment;
    private String portletdefinition;

	/**
	 * Parameters added by nested param tag
	 */
	private Map<String, Object> params = new HashMap<String, Object>();
	private Page page;
    private Page defaultPage;
	
	@Override
    public void doTag() throws JspException, IOException {
		PageContext ctx = (PageContext) getJspContext();
		HttpServletRequest request = (HttpServletRequest) ctx.getRequest();

		if (page == null) {
		    if (!StringUtils.isBlank(urlfragment)) {
		      String path = getPath();
		      page = getPageWithUrlFragement(path, urlfragment);
		    }
		    else {
	            if (!StringUtils.isBlank(portletdefinition)) {
	                if (window != null) {
	                    throw new IllegalArgumentException("portletdefinition and window can not be set both");
	                }
	                String path = getPath();
	                setPageAndWindowBasedOnPortletDefinition(path, portletdefinition);      
	            }
		    }
		}
		
		if (page == null) {
		    page = defaultPage;
		}
		
		if (page != null) {
            String newlink = null;
            
            String externalurl = page.getExternalurl();
            if (!StringUtils.isBlank(externalurl)) {
                if (externalurl.indexOf("://") > -1) {
                    newlink = externalurl;
                }
                else {
                    Page internalPage = SiteManagement.getPageFromPath(externalurl);
                    if (internalPage != null) {
                        String link = SiteManagement.getPath(internalPage, !ServerUtil.useServerName());
                        newlink = getPageUrl(request, link);
                    }
                }
            }
            else {
    			String link = SiteManagement.getPath(page, !ServerUtil.useServerName());
                if (link != null) {
                    newlink = getPageUrl(request, link);
                }
            }
			// handle result
			if (var != null) {
				// put in variable
				if (newlink != null) {
					request.setAttribute(var, newlink);
				} else {
					request.removeAttribute(var);
				}
			} else {
                if (newlink != null) {
                    // write
    				ctx.getOut().print(newlink);
                }
			}
			page = null;
			defaultPage = null;
			params.clear();
		} else {
			// log.warn("NO PAGE");
		}
	}

    private void setPageAndWindowBasedOnPortletDefinition(String path, String portletdefinition) {
        List<Page> pages = SiteManagement.getListFromPath(path);
        int lastIndexOfPages = pages.size() - 1;
        for (int i = lastIndexOfPages; i >= 0; i--) {
            Page currentPage = pages.get(i);
            String portletPosition = getPortletPositionWithDefinition(currentPage, portletdefinition);
            if (portletPosition != null) {
                window = portletPosition;
                page =  currentPage;
                return;
            }
            else {
                List<Page> childPages = SiteManagement.getPages(currentPage);
                // already examined the child which is on the path;
                Page childPageOfPath = (i == lastIndexOfPages) ? null : pages.get(i + 1);
                childPages.remove(childPageOfPath);
                for (Page childPage : childPages) {
                    String childPortletPosition = getPortletPositionWithDefinition(childPage, portletdefinition);
                    if (childPortletPosition != null) {
                        window = childPortletPosition;
                        page =  childPage;
                        return;
                    }
                }
            }
        }
    }

    private String getPortletPositionWithDefinition(Page currentPage, String portletdefinition) {
        Map<String,Integer> portlets = currentPage.getPortletsWithNames();
        for (Map.Entry<String,Integer> portletEntry : portlets.entrySet()) {
            Portlet portlet = SiteManagement.getPortlet(portletEntry.getValue());
            if (portlet != null) {
                PortletDefinition definition = SiteManagement.getPortletDefinition(portlet.getDefinition());
                if (definition != null && definition.getDefinition().equalsIgnoreCase(portletdefinition)) {
                    return portletEntry.getKey();
                }
            }
        }
        return null;
    }

    private Page getPageWithUrlFragement(String path, String urlfragment) {
        List<Page> pages = SiteManagement.getListFromPath(path);
        int lastIndexOfPages = pages.size() - 1;
        for (int i = lastIndexOfPages; i >= 0; i--) {
            Page currentPage = pages.get(i);
            if (currentPage.getUrlfragment().equalsIgnoreCase(urlfragment)) {
                return currentPage;
            }
            else {
                List<Page> childPages = SiteManagement.getPages(currentPage);
                // already examined the child which is on the path;
                Page childPageOfPath = (i == lastIndexOfPages) ? null : pages.get(i + 1);
                childPages.remove(childPageOfPath);
                for (Page childPage : childPages) {
                    if (childPage.getUrlfragment().equalsIgnoreCase(urlfragment)) {
                        return childPage;
                    }
                }
            }
        }
        return null;
    }

    private String getPageUrl(HttpServletRequest request, String link)
            throws JspException, IOException {
        String newlink = null;
        if (link != null) {
        	// handle body, call any nested tags
        	JspFragment frag = getJspBody();
        	if (frag != null) {
        		StringWriter buffer = new StringWriter();
        		frag.invoke(buffer);
        	}
   
            String host = null;
            if(ServerUtil.useServerName()) {
               host = SiteManagement.getSite(page);
            }
            PortalURL u = new PortalURL(host, request, link);
            
            if (element != null) {
                int pageId = page.getId();
                if (window == null) {
                    window = Search.getPortletWindow(pageId, element);
                }
                if (window != null) {
                    u.setRenderParameter(window, "elementId", new String[] { element } );
				}
            }
            
            if (window != null) {
                for (Map.Entry<String, Object> paramEntry : params.entrySet()) {
                    u.setRenderParameter(window, paramEntry.getKey(),
                            new String[] { paramEntry.getValue().toString() } );
                }
            }

            newlink = u.toString();
            
            if(newlink != null && newlink.length() == 0) {
            	newlink = "/";
            }
        }
        return newlink;
	}

    /**
	 * Set destination to navigate to.
	 * 
	 * @param dest the destination node, list of nodes or comma or slash
	 *        separated node numbers or aliases
	 */
	public void setDest(Object dest) {
		if (dest != null) {
			if (dest instanceof Page) {
				page = (Page) dest;
			} else if (dest instanceof Integer) {
			    page = getPageInteger((Integer) dest);
			} else if (dest instanceof String) {
			    page = getPageString((String) dest);
			} else {
				throw new IllegalArgumentException("only Page, integer or string allowed: " + dest.getClass());
			}
		}
	}

	public void setUrlfragment(String urlfragment) {
        this.urlfragment = urlfragment;
    }

    public void setPortletdefinition(String portletdefinition) {
        this.portletdefinition = portletdefinition;
    }

    public void setWindow(String window) {
        this.window = window;
    }
    
    public void setDefaultpage(Object dest) {
        if (dest != null) {
            if (dest instanceof Page) {
                defaultPage = (Page) dest;
            } else if (dest instanceof Integer) {
                defaultPage = getPageInteger((Integer) dest);
            } else if (dest instanceof String) {
                defaultPage = getPageString((String) dest);
            } else {
                throw new IllegalArgumentException("only Page, integer or string allowed: " + dest.getClass());
            }
        }
    }

    public void setElement(String element) {
        this.element = element;
    }

	/**
	 * Set destination node number to navigate to.
	 * 
	 * @param n the node number
	 */
	private Page getPageInteger(Integer n) {
		return SiteManagement.getPage(n.intValue());
	}

	/**
	 * Set the destination node path to navigate to.
	 * 
	 * @param s comma, slash or space separated list of node numbers and/or
	 *        aliases
	 */
	private Page getPageString(String s) {
	    Page temp = null;
        if (!StringUtils.isBlank(s)) {
    		if (StringUtils.isNumeric(s)) {
    			temp = SiteManagement.getPage(Integer.parseInt(s));
    		} else {
    			temp = SiteManagement.getPageFromPath(s);
    		}
        }
        return temp;
	}

	public void setVar(String var) {
		this.var = var;
	}

	protected void addParam(String name, Object value) {
		if (name != null && name.length() > 0) {
			params.put(name, value);
		}
	}

}
