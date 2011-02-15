<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "DTD/xhtml1-transitional.dtd">
<html><jsp:directive.include file="page_base_functionality.jsp"
/><mm:import externid="userlogon" from="parameters" />
<mm:content language="$config.lang" country="$config.country" type="text/html" expires="0">
<mm:cloud loginpage="login.jsp" logon="$userlogon" sessionname="$config.session" jspvar="cloud" rank="$rank" uri="$config.uri">
<mm:formatter xslt="xslt/framework/head.xslt" escape="none">
  <head>
    <title><%=m.getString("search_node.search")%></title>
    <jsp:directive.include file="head.entries.jsp" />
  </head>
</mm:formatter>
<!-- mm:timer name="search_node"-->
<mm:import externid="node_number" from="parameters" required="true" />
<body class="basic">
<mm:context id="edit">
<p class="crumbpath"><%= toHtml(urlStack, request) %></p>
 <mm:node number="$node_number">

  <table class="edit" summary="node navigator" width="93%"  cellspacing="1" cellpadding="3" border="0">
  <tr><th colspan="2">
  <mm:nodeinfo type="gui" />:
  <%=m.getString("Node")%> <mm:field name="number" /> <%=m.getString("oftype")%> <mm:nodeinfo type="guinodemanager"  />
  ( <mm:nodeinfo type="nodemanager" /> )
    <a href="<mm:url page="change_node.jsp" referids="node_number" />">
      <span class="change"><!-- needed for IE --></span><span class="alt">[change]</span>
     </a>
  </th></tr>
    <tr><th width="50%">Destination</th><th width="50%">Source</th></tr>
    <tr align="left">
      <td>
        <mm:context>
          <mm:import id="searchdir">destination</mm:import>
          <%@include file="tree.jsp" %>
        </mm:context>
    	</td>
      <td>
        <mm:context>
          <mm:import id="searchdir">source</mm:import>
          <%@include file="tree.jsp" %>
        </mm:context>
      </td>
    </table>
 </mm:node>

</mm:context>

<%@ include file="foot.jsp"  %>
<!-- mm:timer -->
</mm:cloud>
</mm:content>
