<%@page language="java" contentType="text/html;charset=utf-8" %>
<%@page import="com.finalist.tree.html.*"%>
<%@page import="com.finalist.cmsc.navigation.*"%>
<%@page import="com.finalist.cmsc.security.forms.*"%>
<%@include file="../globals.jsp"  %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="siteroles.title">
	<style>
		input.select { font-height: 4px;}
	</style>
</cmscedit:head>
<body style="overflow: auto">
<mm:cloud jspvar="cloud" rank='administrator'>
<html:form action="/editors/usermanagement/SiteRolesAction">
<input type="hidden" name="savetree" id="savetree" value="true">
<input type="hidden" name="channel" id="channel">
<input type="hidden" name="expand" id="expand">

<div class="content_block_pink">
	<div class="header">
	   <div class="title">
			<bean:write name="RolesForm" property="name"/>   	
		</div>
		<div class="header_end"></div>
	</div>
<div style="padding: 5px;">

<div id="rollen">
<p><b><fmt:message key="siteroles.description" /></b></p>
<p style="padding: 0px; margin: 0px; bottom: 0px;">

<mm:import externid="channel" from="parameters"></mm:import>
<mm:import externid="expand" jspvar="expand">true</mm:import> 
<%
RolesForm form = (RolesForm) session.getAttribute("RolesForm");
RolesInfo info = form.getRolesInfo();
%>
<mm:compare referid="channel" value="" inverse="true">
  <mm:node number="${channel}" jspvar="pageNode">
  	<mm:import id="pagepath"><mm:field name="path"/></mm:import>
  <%
	List openChannels = NavigationUtil.getPathToRoot(pageNode);
	for (Iterator iter = openChannels.iterator(); iter.hasNext();) {
	    Node node = (Node) iter.next();
	    info.expand(node);
	}
    if (!expand.equals("true")) {
	    info.collapse(pageNode);
    }
    %>
  </mm:node>
</mm:compare>
<mm:compare referid="channel" value="">
	<mm:import id="pagepath"></mm:import>
</mm:compare>
<%
	for (NodeIterator iter = SiteUtil.getSites(cloud).nodeIterator(); iter.hasNext();) {
	    Node site = iter.nextNode();
	   	NavigationTreeModel model = new NavigationTreeModel(site);
		SiteRolesRenderer chr = new SiteRolesRenderer(request, cloud, form);
	   	ServerHTMLTree t = new ServerHTMLTree(model, chr, info, "javascript");
	   	t.setImgBaseUrl("../gfx/");
	   	t.render(out);
	}
%>
</p>
</div>
<br>
<html:submit style="width:90"><fmt:message key="siteroles.submit"/></html:submit>
<html:cancel style="width:90"><fmt:message key="siteroles.cancel"/></html:cancel>
</div>
<div class="side_block_end"></div>
</div>	

</html:form>
</mm:cloud>
</body>
</html:html>
</mm:content>