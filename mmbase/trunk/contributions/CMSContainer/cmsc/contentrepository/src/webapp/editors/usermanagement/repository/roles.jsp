<%@page language="java" contentType="text/html;charset=utf-8" %>
<%@page import="com.finalist.tree.html.*"%>
<%@page import="com.finalist.cmsc.repository.*"%>
<%@page import="com.finalist.cmsc.security.forms.*"%>
<%@include file="../globals.jsp"  %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="contentroles.title">
	<style>
		input.select { font-height: 4px;}
	</style>
</cmscedit:head>
<body style="overflow: auto">
<mm:cloud jspvar="cloud" rank='administrator'>

<html:form action="/editors/usermanagement/ContentRolesAction">
<input type="hidden" name="savetree" id="savetree" value="true">
<input type="hidden" name="channel" id="channel">
<input type="hidden" name="expand" id="expand">

<div class="content_block_purple">
	<div class="header">
	   <div class="title">
			<bean:write name="RolesForm" property="name"/>
		</div>
		<div class="header_end"></div>
	</div>
<div style="padding: 5px;">
	
	
	<div id="rollen">

<p><b><fmt:message key="contentroles.description" /></b></p>
<p style="padding: 0px; margin: 0px; bottom: 0px;">

<mm:import externid="channel" from="parameters"><%= RepositoryUtil.ALIAS_ROOT %></mm:import>
<mm:import externid="expand" jspvar="expand">true</mm:import> 
<mm:node number="${channel}" jspvar="channel"> 

<%
	RolesForm form = (RolesForm) session.getAttribute("RolesForm");
	RolesInfo info = form.getRolesInfo();
    List openChannels = RepositoryUtil.getPathToRoot(channel);
    for (Iterator iter = openChannels.iterator(); iter.hasNext();) {
        Node node = (Node) iter.next();
        info.expand(node);
    }
    if (!expand.equals("true")) {
    	info.collapse(channel);
    }

   ContentRolesRenderer chr = new ContentRolesRenderer(request, cloud, form);    

   RepositoryTrashTreeModel trashModel = new RepositoryTrashTreeModel(cloud, true);
   ServerHTMLTree tTrash = new ServerHTMLTree(trashModel, chr, info, "javascript");
   tTrash.setImgBaseUrl("../gfx/");
   tTrash.render(out);   

	RepositoryTreeModel model = new RepositoryTreeModel(cloud, true);
	ServerHTMLTree t = new ServerHTMLTree(model, chr, info, "javascript");
	t.setImgBaseUrl("../gfx/");
	t.render(out);
%>
</mm:node>
</p>
</div>
<br>
<html:submit style="width:90"><fmt:message key="contentroles.submit"/></html:submit>
<html:cancel style="width:90"><fmt:message key="contentroles.cancel"/></html:cancel>
</div>
<div class="side_block_end"></div>
</div>	

</html:form>
</mm:cloud>
</body>
</html:html>
</mm:content>