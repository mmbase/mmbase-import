<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="globals.jsp"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<head>
  <title><fmt:message key="dashboard.title" /></title>
  <link rel="stylesheet" type="text/css" href="style.css" />
  <script src="utils/rowhover.js" type="text/javascript"></script>
</head>
<body>
<mm:cloud jspvar="cloud" loginpage="login.jsp">

	<mm:haspage page="/editors/repository/">
		<h2><fmt:message key="repository.lastelements" /></h2>

		<mm:cloudinfo type="user" id="cloudusername" write="false" />

		<mm:listnodescontainer type="contentelement">
			<mm:constraint field="lastmodifier" operator="EQUAL" referid="cloudusername" />
			<mm:maxnumber value="10" />
			<mm:sortorder field="lastmodifieddate" direction="down" />

		<table class="listcontent" style="width: 50%">
		<mm:listnodes>
	        <mm:field name="number" write="false" id="number"/>
		<tr class="itemrow" onMouseOver="objMouseOver(this);"
			onMouseOut="objMouseOut(this);"
			href="javascript:window.top.openRepositoryWithContent('<mm:write referid="number"/>');">

			<td onMouseDown="objClick(this);">
				<mm:nodeinfo type="guitype"/>
			</td>
			<td onMouseDown="objClick(this);">
				<mm:field name="number"/>
			</td>
			<td onMouseDown="objClick(this);">
				<mm:field name="title"/>
			</td>
			<td onMouseDown="objClick(this);">
				<mm:field name="lastmodifieddate"><cmsc:dateformat displaytime="true" /></mm:field>
			</td>
		</tr>
		</mm:listnodes>
		</table>
		</mm:listnodescontainer>
	</mm:haspage>

	<mm:haspage page="/editors/admin/">
		<mm:hasrank minvalue="administrator">
			<h2><fmt:message key="admin.title" /></h2>
			<table cellpadding=1 cellspacing=0>
				<tr>
					<td><a href="usermanagement/userlist.jsp" 
						class="leftmenu"><fmt:message key="admin.users" /></a></td>
				</tr>
				<tr>
					<td><a href="WizardListAction.do?nodetype=properties"
						class="leftmenu"><fmt:message key="admin.settings" /></a></td>
				</tr>
				<tr>
					<td><a href="WizardListAction.do?nodetype=layout" 
						class="leftmenu"><fmt:message key="admin.layouts" /></a></td>
				</tr>
				<tr>
					<td><a href="WizardListAction.do?nodetype=view" 
						class="leftmenu"> <fmt:message key="admin.views" /></a></td>
				</tr>
				<tr>
					<td><a href="WizardListAction.do?nodetype=stylesheet"
						class="leftmenu"><fmt:message key="admin.stylesheets" /></a></td>
				</tr>
				<tr>
					<td><a href="WizardListAction.do?nodetype=portletdefinition"
						class="leftmenu"><fmt:message key="admin.portletdefinitions" /></a>
					</td>
				</tr>
			</table>
		</mm:hasrank>
	</mm:haspage>
</mm:cloud>
</body>
</html:html>
</mm:content>