<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="globals.jsp"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
	<head>
		<title><fmt:message key="channeldelete.title" /></title>
		<link href="../style.css" type="text/css" rel="stylesheet" />
		<script src="content.js" type="text/javascript"></script>
		<script src="../utils/window.js" type="text/javascript"></script>
		<script src="../utils/rowhover.js" type="text/javascript"></script>
	</head>
	<body>

	<mm:cloud jspvar="cloud" rank="basic user" method='http'>
		<mm:import externid="number" id="parentchannel" jspvar="parentchannel" vartype="Integer" from="parameters" required="true" />

		<table style="width: 100%;">
			<tr>
				<td><fmt:message key="channeldelete.warning" /><br />
				<b><mm:node number="$parentchannel"><mm:field name="name" /></mm:node></b>.<br />
				</td>
			</tr>
		</table>

		<form action="ChannelDelete.do" method="post" onsubmit="return unlinkAll();">
			<input type="hidden" name="remove" value="unlinkall" /> 
			<input type="hidden" name="number" value="<mm:write referid="parentchannel"/>" /> 
			<input type="submit" value="<fmt:message key="channeldelete.removeall" />" />
		</form>
		<hr />

		<mm:import id="lastotype" />

		<mm:node number="$parentchannel">
			<mm:relatednodescontainer path="creationrel,contentelement" searchdirs="source" element="contentelement">
				<mm:sortorder field="contentelement.otype" direction="up" />
				<mm:sortorder field="contentelement.title" direction="up" />

				<mm:listnodes jspvar="node">
					<mm:field name="otype" write="false" id="otype" />
					<mm:field name="number" write="false" id="number" />

					<mm:compare referid="lastotype" value="" inverse="true">
						</tr>
					</mm:compare>
					<mm:compare referid="otype" referid2="lastotype" inverse="true">
						<mm:compare referid="lastotype" value="" inverse="true">
							</table>
						</mm:compare>

						<mm:node referid="otype">
							<br />
							<fmt:message key="recyclebin.type">
								<fmt:param>
									<mm:field name="name" id="nodename">
										<mm:nodeinfo nodetype="$nodename" type="guitype" />
									</mm:field>
								</fmt:param>
							</fmt:message>
						</mm:node>
						<mm:import id="lastotype" reset="true"><mm:write referid="otype" /></mm:import>
						<mm:import id="newotype">true</mm:import>

						<table class="listcontent">
					</mm:compare>

					<mm:url page="../repository/showitem.jsp" id="url" write="false">
						<mm:param name="objectnumber" value="$number" />
					</mm:url>
					<tr class="itemrow" onMouseOver="objMouseOver(this);" onMouseOut="objMouseOut(this);"
						href="<mm:write referid="url"/>">
						<td onMouseDown="objClickPopup(this, 500, 500);"><mm:field name="number" /></td>
						<td onMouseDown="objClickPopup(this, 500, 500);" width="100%"><mm:field name="title" /></td>

						<td style="padding:0px">
							<a href="javascript:unpublish('<mm:write referid="parentchannel" />','<mm:field name="number" />');">
								<img src="../img/remove.gif" width="15" height="15" alt="<fmt:message key="channeldelete.unlink" />" />
							</a>
						</td>
						<mm:present referid="newotype">
							<td></td>
						</mm:present>

						<mm:remove referid="newotype" />
					<mm:last>
					<mm:compare referid="lastotype" value="" inverse="true">
							</tr>
						</table>
					</mm:compare>
					</mm:last>
				</mm:listnodes>
			</mm:relatednodescontainer>
		</mm:node>
	</mm:cloud>

	</body>
	</html:html>
</mm:content>
