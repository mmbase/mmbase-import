<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="globals.jsp"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
	<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
	<html:html xhtml="true">
	<head>
	<title><fmt:message key="tasks.title" /></title>
	<link rel="stylesheet" type="text/css" href="../style.css" />
	<script src="../utils/rowhover.js" type="text/javascript"></script>
	</head>
	<body>
	<mm:cloud jspvar="cloud" loginpage="../login.jsp">

		<h2><fmt:message key="tasks.title" /></h2>

		<mm:cloudinfo type="user" id="cloudusername" write="false" />
		<mm:listnodescontainer type="user">
			<mm:constraint field="user.username" operator="EQUAL" referid="cloudusername" />
			<mm:listnodes>

				<mm:relatednodescontainer type="task" role="assignedrel" searchdirs="source">
					<mm:sortorder field="deadline" direction="down" />

					<table class="listcontent" style="width: 50%">
						<mm:relatednodes>
							<mm:field name="number" write="false" id="number" />
							<tr class="itemrow" onMouseOver="objMouseOver(this);" onMouseOut="objMouseOut(this);"
								href="TaskInitAction.do?id=<mm:field name='number'/>">

								<td onMouseDown="objClick(this);"><mm:field name="deadline"><cmsc:dateformat displaytime="true" /></mm:field></td>
								<td onMouseDown="objClick(this);"><mm:field name="title" /></td>
								<td onMouseDown="objClick(this);"><mm:field name="description" /></td>
								<td onMouseDown="objClick(this);"><mm:field name="status" /></td>
								<td onMouseDown="objClick(this);"><mm:field name="type" /></td>
								<td onMouseDown="objClick(this);"><mm:field name="creationdate"><cmsc:dateformat displaytime="true" /></mm:field></td>
								<td onMouseDown="objClick(this);"><mm:field name="nodetype" id="nodetype"><mm:isnotempty>
									<mm:nodeinfo nodetype="${nodetype}" type="guitype"/>
									</mm:isnotempty>
									</mm:field></td>
								<td><mm:maydelete>
									<a href="DeleteTaskAction.do?id=<mm:field name='number'/>"> <img src="../img/remove.gif"
										border='0' title="<fmt:message key="tasks.remove" />"
										onclick="return confirm('<fmt:message key="tasks.remove" />')" /> </a>
								</mm:maydelete></td>
								<td><mm:maydelete>
									<a href="TaskDoneAction.do?id=<mm:field name='number'/>"> <img src="../img/done.gif"
										border='0' title="<fmt:message key="tasks.done" />" /> </a>
								</mm:maydelete></td>
							</tr>
						</mm:relatednodes>
					</table>
					<a href="TaskInitAction.do"><img src="../editwizards/media/new.gif" border='0' align='middle'
						title="<fmt:message key="tasks.new" />" /></a>
					<br />
				</mm:relatednodescontainer>
			</mm:listnodes>
		</mm:listnodescontainer>
	</mm:cloud>
	</body>
	</html:html>
</mm:content>
