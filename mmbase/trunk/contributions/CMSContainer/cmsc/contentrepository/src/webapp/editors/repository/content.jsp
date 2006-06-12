<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="globals.jsp" %>
<%@page import="com.finalist.cmsc.repository.RepositoryUtil" %>
<%@page import="com.finalist.cmsc.security.*" %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
	<head>
		<title><fmt:message key="content.title" /></title>
		<link rel="stylesheet" type="text/css" href="../style.css" />
		<script src="content.js" type="text/javascript"></script>
		<script src="../utils/window.js" type="text/javascript"></script>
		<script src="../utils/rowhover.js" type="text/javascript"></script>
	</head>
	<body>
<mm:cloud jspvar="cloud" rank="basic user" loginpage="../login.jsp">
	  <mm:import externid="parentchannel" jspvar="parentchannel" vartype="Integer" from="parameters" required="true"/>
	  <mm:import jspvar="returnurl" id="returnurl">/editors/repository/content.jsp?parentchannel=<mm:write referid="parentchannel"/></mm:import>

<mm:node number="$parentchannel" jspvar="parentchannelnode">
<% UserRole role = RepositoryUtil.getRoleForUser(cloud, parentchannelnode, false); %>
   <table style="width: 100%;">
	  <tr>
		 <td>
		 	 <fmt:message key="content.channel" >
			 	<fmt:param ><mm:field name="name"/></fmt:param>
			 </fmt:message>
			 <br />
		<% if (role != null && SecurityUtil.isWriter(role)) { %>
			<ul>
			   <li>
				  <form action="../WizardInitAction.do" method="post">
					 <input type="hidden" name="action" value="create" />
					 <input type="hidden" name="creation" value="<mm:write referid="parentchannel" />" />
					 <input type="hidden" name="returnurl" value="<%= returnurl %>" />
					 <fmt:message key="content.new" />
					 <select name="contenttype">
						<mm:listnodes type="editwizards">
						   <mm:field name="nodepath" jspvar="nodepath" id="nodepath" vartype="String">
							  <% if (com.finalist.cmsc.repository.ContentElementUtil.isContentType(nodepath)) { %>
								 <option value="<mm:write />"><mm:nodeinfo nodetype="$nodepath" type="guitype"/></option>
							  <% } %>
						   </mm:field>
						</mm:listnodes>
					 </select>
					 <input type="submit" name="submitButton" value="<fmt:message key="content.create" />" />
				  </form>
			   </li>
			   <li>
				  <a href="<mm:url page="SearchInitAction.do">
							  <mm:param name="linktochannel" value="$parentchannel" />
							  <mm:param name="returnurl" value="${returnurl}" />
						   </mm:url>">
					 <fmt:message key="content.existing" /> <img src="../img/add_content.gif" width="15" height="15" alt="Up"/>
				  </a>
				  <br />&nbsp;
			   </li>
				<% if (SecurityUtil.isEditor(role)) { %>
			   <li>
				  <a href="<mm:url page="ReorderAction.do">
							  <mm:param name="parent" value="$parentchannel" />
						   </mm:url>">
					 <fmt:message key="content.reorder" /> <img src="../img/reorder.gif" width="15" height="15" alt="Up"/>
				  </a>
			   </li>
				<% } %>
			</ul>
			<% } %>
		 </td>
	  </tr>
   </table>
   
   <hr/>
	  <fmt:message key="content.content" /><br/>
	  <mm:import id="lastotype"/>
	 

<mm:relatednodescontainer path="contentrel,contentelement" searchdirs="destination" element="contentelement">
	<mm:sortorder field="contentelement.otype" direction="up" />
	<mm:sortorder field="contentrel.pos" direction="up" />
	
	<mm:listnodes jspvar="node">
		<mm:field name="otype" write="false" id="otype"/>
		<mm:field name="number" write="false" id="number"/>
		<mm:field name="number" write="false" id="relnumber"/>

		<mm:compare referid="lastotype" value="" inverse="true">
		   </tr>
		</mm:compare>
		<mm:compare referid="otype" referid2="lastotype" inverse="true">
		   <mm:compare referid="lastotype" value="" inverse="true">
			  </table>
		   </mm:compare>

		   <mm:node referid="otype">
			  <br/>
			  <fmt:message key="content.type" >
			 	<fmt:param><mm:field name="name" id="nodename"><mm:nodeinfo nodetype="$nodename" type="guitype"/></mm:field></fmt:param>
			 </fmt:message>
		   </mm:node>
		   <mm:import id="lastotype" reset="true"><mm:write referid="otype"/></mm:import>
		   <mm:import id="newotype">true</mm:import>

		   <table class="listcontent">
		</mm:compare>

		<mm:url page="../WizardInitAction.do" id="url" write="false" >
		   <mm:param name="objectnumber" value="$number"/>
		   <mm:param name="returnurl" value="$returnurl" />
		</mm:url>
		<tr class="itemrow" onMouseOver="objMouseOver(this);"
					onMouseOut="objMouseOut(this);"
					href="<mm:write referid="url"/>"><td onMouseDown="objClick(this);">
		   <mm:field name="number"/>
		</td>
		<td onMouseDown="objClick(this);" width="100%">
		   <mm:field name="title"/>
		</td>
		<td style="padding:0px" align="right">
			  <a href="#" onClick="showChannels(<mm:field name="number"/>);" title="<fmt:message key="content.showchannels" />">
				 <img src="../img/tree.gif" alt="<fmt:message key="content.showchannels" />" />
			  </a>
		</td>
		<td style="padding:0px">
			<a href="javascript:callEditWizard('<mm:field name="number" />');"  title="<fmt:message key="content.edit" />">
				<img src="../img/edit.gif" width="15" height="15" alt="<fmt:message key="content.edit" />"/>
			</a>
		</td>
		<td style="padding:0px">
			<% if (role != null && SecurityUtil.isWriter(role)) { %>

			<a href="javascript:unpublish('<mm:write referid="parentchannel" />','<mm:field name="number" />');"
				title="<fmt:message key="content.unlink" />">
				<img src="../img/remove.gif" width="15" height="15" alt="<fmt:message key="content.unlink" />"/>
			</a>
			<% } %>
		</td>
		<mm:present referid="newotype">
		   <td></td>
		</mm:present>

		<mm:import id="lastnumber" reset="true"><mm:write referid="number"/></mm:import>
		<mm:import id="lastrelnumber" reset="true"><mm:write referid="relnumber"/></mm:import>

		<mm:remove referid="newotype"/>

		<mm:last>
		   <mm:compare referid="lastotype" value="" inverse="true">
			  </tr></table>
		   </mm:compare>
		</mm:last>

	</mm:listnodes>
</mm:relatednodescontainer>
</mm:node>
</mm:cloud>
	</body>
</html:html>
</mm:content>