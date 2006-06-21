<%@page language="java" contentType="text/html;charset=utf-8" import="com.finalist.tree.*" %>
<%@include file="globals.jsp"  %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<head>
  <title><fmt:message key="channelreorder.title" /></title>
  <link rel="stylesheet" type="text/css" href="../style.css" />
  <script type="text/javascript" src="reorder.js"></script>
  <style type="text/css">
    input.button { width : 100; }
  </style>
</head>
<body onload="fillHidden()">
<mm:cloud jspvar="cloud" rank="basic user" loginpage="../login.jsp">
   <mm:import externid="parent" jspvar="parent" from="parameters"/>
<mm:node referid="parent" jspvar="node">
<h2><fmt:message key="channelreorder.reorder" /> <mm:field name="path" /></h2>

<mm:relatednodescontainer path="contentrel,contentelement"
  searchdirs="destination" element="contentelement">
  <mm:sortorder field="contentrel.pos" direction="up" />
  
  <script type='text/javascript'>
      var values = new Array(<mm:size />);
  <mm:listnodes>
		values[<mm:index/> - 1] = <mm:field name="number"/>;
  </mm:listnodes>
  </script>

  <html:form action="/editors/repository/ReorderAction">
   
   <mm:import externid="returnurl" from="parameters"/>
   <mm:present referid="returnurl">
      <input type="hidden" name="returnurl" value="<mm:write referid="returnurl"/>"/>
   </mm:present>
   
	<table>
	  <tr>
	    <td width='200'>
	<select size='10' style="width:100%" name="channels">
  <mm:listnodes>
		<option><mm:field name="title"/></option>
  </mm:listnodes>
	</select>
	    </td>
	    <td> 
		  <img src="../img/up.gif" onClick="moveUp()" onDblClick="moveUp()" alt="" /><br />
		  <img src="../img/down.gif" onClick="moveDown()" onDblClick="moveDown()" alt="" />
	    </td>
	  </tr>
	</table>
	<input type="hidden" name="ids"/>
	<input type="hidden" name="action" value="reorder"/>
	<input type="hidden" name="parent" value="<mm:write referid="parent" />"/>
	<html:submit styleClass="button"><fmt:message key="channelreorder.submit" /></html:submit>
	<html:cancel styleClass="button"><fmt:message key="channelreorder.cancel" /></html:cancel>
  </html:form>
  
</mm:relatednodescontainer>
</mm:node>
</mm:cloud>

</body>
</html:html>
</mm:content>