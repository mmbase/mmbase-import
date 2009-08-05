<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="../globals.jsp"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="luceus.title" />
<body>

<mm:cloud jspvar="cloud" loginpage="../editors/login.jsp" rank="administrator">
<mm:import externid="nodenumber"/>
<mm:import externid="action"/>
<mm:import externid="type"/>
<form method="post">
	<input type="text" name="nodenumber" value=""/>
	<select name="action">
		<option value="update">Update</option>
		<option value="delete">Delete</option>
	</select>
	<select name="type">
		<option value="page">Page</option>
		<option value="content">Content</option>
		<option value="contentchannel">ContentChannel</option>
		<option value="secondary">Secondary</option>
	</select>
	<input type="submit" value="index"/>
</form>

<mm:present referid="nodenumber">
<mm:write referid="action" jspvar="action">
<mm:write referid="type" jspvar="type">
<mm:write referid="nodenumber" jspvar="nodenumber" vartype="Integer">
<%
	com.finalist.cmsc.module.luceusmodule.LuceusModule luceusModule 
		= (com.finalist.cmsc.module.luceusmodule.LuceusModule)
		 org.mmbase.module.Module.getModule("luceusmodule");
	if (luceusModule != null) {
		if ("delete".equals(action)) {
			if ("content".equals(type)) {
				luceusModule.deleteContentIndex(nodenumber);
			}
			if ("page".equals(type)) {
				luceusModule.deletePageIndex(nodenumber);
			}
		}
		if ("update".equals(action)) {
			if ("secondary".equals(type)) {
				luceusModule.updateSecondaryContentIndex(nodenumber);
			}
			if ("content".equals(type)) {
				luceusModule.updateContentIndex(nodenumber);
			}
			if ("contentchannel".equals(type)) {
				luceusModule.updateContentChannelIndex(nodenumber);
			}
			if ("page".equals(type)) {
				luceusModule.updatePageIndex(nodenumber);
			}
		}
	}
%>
nodenumber:	<mm:write referid="nodenumber" /><br />
indexed
</mm:write>
</mm:write>
</mm:write>
</mm:present>
</mm:cloud>

</body>
</html:html>
</mm:content>