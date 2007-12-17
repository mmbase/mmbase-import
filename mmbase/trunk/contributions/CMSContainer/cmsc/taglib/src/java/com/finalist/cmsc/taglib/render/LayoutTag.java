package com.finalist.cmsc.taglib.render;

import java.io.*;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.finalist.cmsc.beans.om.Block;
import com.finalist.cmsc.beans.om.Layout;
import com.finalist.cmsc.beans.om.Page;
import com.finalist.cmsc.portalImpl.PortalConstants;
import com.finalist.pluto.portalImpl.aggregation.PortletFragment;
import com.finalist.pluto.portalImpl.aggregation.ScreenFragment;
import com.finalist.pluto.portalImpl.servlet.ServletObjectAccess;
import com.finalist.pluto.portalImpl.servlet.ServletResponseImpl;
import com.finalist.cmsc.services.sitemanagement.SiteManagementAdmin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LayoutTag extends SimpleTagSupport {

    private static Log log = LogFactory.getLog(LayoutTag.class);

	@Override
    public void doTag() throws IOException {
        PageContext ctx = (PageContext) getJspContext();
        HttpServletRequest request = (HttpServletRequest) ctx.getRequest();
		HttpServletResponse response = (HttpServletResponse) ctx.getResponse();
		
        ScreenFragment container = getScreenFragment(); 
        if (container != null) {
        	Layout layout = container.getLayout();
        	Page page = container.getPage();
        	//get all the portlets on this page
        	Collection<PortletFragment> portlets = container.getChildFragments();
        	StringBuffer buffer = new StringBuffer();
        	buffer.append("<div id=\""+page.getId()+"\" class=\"portal\">");
        	List<Block> blocks = layout.getBlocks();
        	if(page.getBlocks().isEmpty())
        		for(Block block : blocks) renderBlock(buffer, block,page,portlets,request, response,false);
        	else
        		for(Block block : blocks) renderBlock(buffer, block, page,portlets,request, response,true);
        	
        	buffer.append("</div>");
        	String html = buffer.toString(); 
        	ctx.getOut().print(html);
        }
	}

	private void renderPortlets(StringBuffer buffer,Block block,Collection<PortletFragment> portlets,HttpServletRequest request,HttpServletResponse response){
		Map<String,PortletFragment> sortedPortlets = new TreeMap<String,PortletFragment>();
		String blockName = block.getName();
		for (PortletFragment portletFrag : portlets) {
			String portletBlock = portletFrag.getKey();
			if(portletBlock.startsWith(blockName)){
				sortedPortlets.put(portletBlock, portletFrag);
			}
		}
		for (String portletKey : sortedPortlets.keySet()) {
			PortletFragment portlet = sortedPortlets.get(portletKey);
			try {
	            StringWriter storedWriter = new StringWriter();
	            // create a wrapped response which the Portlet will be rendered to
	            ServletResponseImpl wrappedResponse = (ServletResponseImpl) ServletObjectAccess.getStoredServletResponse(response, new PrintWriter(storedWriter));
	            // let the Portlet do it's thing
				portlet.writeToResponse(request, wrappedResponse, PortletFragment.DYNAMIC);
	          //  ctx.getOut().print(storedWriter.toString());
				buffer.append(storedWriter.toString());
			} catch (IOException e) {
				log.error("Error in portlet");
			}
		}
	}

    protected ScreenFragment getScreenFragment() {
        PageContext ctx = (PageContext) getJspContext();
        HttpServletRequest request = (HttpServletRequest) ctx.getRequest();
        return (ScreenFragment) request.getAttribute(PortalConstants.FRAGMENT);
    }

	
	private void renderBlock(StringBuffer buffer, Block block, Page page,Collection<PortletFragment> portlets,HttpServletRequest request,HttpServletResponse response,boolean flag) {

		boolean mayEditBlock = SiteManagementAdmin.mayEdit(block);
		buffer.append("<div ");
		appendToBuffer(buffer, "id", block.getName());
		String tracker = block.getTracker();
		boolean isHorizontal = "horizontal".equals(tracker);
		if(mayEditBlock){
			if(!block.getStyleClass().startsWith("portal-column")&& !isHorizontal)
				appendToBuffer(buffer, "class","portal-column "+block.getStyleClass());
			else
				appendToBuffer(buffer, "class",block.getStyleClass());
		}
		else
			appendToBuffer(buffer, "class",block.getStyleClass());
		appendStyleToBuffer(buffer, block.getHeight(), block.getWidth());
		buffer.append(">\n");
		
		
		if(flag){
			if (isHorizontal) {
					List<Block> blocks = page.getBlocks();
					for (int i =0; i < blocks.size(); i++) {
			    		Block childBlock = blocks.get(i);
			    		renderBlock(buffer,childBlock,page,portlets,request, response,false);
			    		if (mayEditBlock && i < blocks.size() - 1) {
							buffer.append("<div class=\"portal-tracker\" id=\"tracker-contentrow-"+i+"\"></div>\n");
						}
					}
			}
			else{
		    	List<Block> blocks = block.getblocks();
		    	for (int i =0; i < blocks.size(); i++) {
		    		Block childBlock = blocks.get(i);
		    		renderBlock(buffer,childBlock,page,portlets,request, response,false);
				}
			}
		}else{
			List<Block> blocks = block.getblocks();
			for (int i =0; i < blocks.size(); i++) {
				Block childBlock = blocks.get(i);
	    		renderBlock(buffer,childBlock,page,portlets,request, response,false);
	    		if (mayEditBlock && isHorizontal && i < blocks.size() - 1) {
					buffer.append("<div class=\"portal-tracker\" id=\"tracker-contentrow-"+i+"\"></div>\n");
				}
			}
		}
		renderPortlets(buffer,block,portlets,request,response);
		buffer.append("</div>\n");
	}

	
	private void appendStyleToBuffer(StringBuffer buffer, int height, int width) {
		if(height>0||width>0) {
			buffer.append("style=\"");
			if(height>0){
			buffer.append("height: auto !important;");
			buffer.append("min-height:").append(height).append("px; ");
			buffer.append("height:").append(height).append("px; ");
			}
			if(width>0)
			buffer.append("width:").append(width).append("px;");
			buffer.append("\" ");
		}
	}

	private void appendToBuffer(StringBuffer buffer, String bufferName, String bufferValue) {
		buffer.append(bufferName+"=\"").append(bufferValue).append("\" ");
	}
	
}
