<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@include file="parts/headers.jsp"%>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"  prefix="mm" %>
<%@include file="parts/basics.jsp"%>
<mm:import externid="node" jspvar="node" vartype="String"><mm:node number="kbase.root"><mm:field name="number"/></mm:node></mm:import>
<mm:import externid="qnode" jspvar="qnode" vartype="String"/>
<mm:import externid="anode" jspvar="anode" vartype="String"/>
<mm:import externid="expanded"/>
<mm:import externid="action"/>
<mm:import externid="type" jspvar="type" vartype="String"/><!-- [question|category|answer] -->
<mm:import externid="newparent" jspvar="newParent" vartype="String"/>
<mm:import externid="submit" />
<mm:cloud jspvar="wolk" rank="basic user" method="http">
<html>
<head>
  <%@include file="parts/httpHeadIncludes.jsp"%>
</head>
<body onload="<mm:present referid="node"> setCurrentFolder(<mm:write referid="node"/>);</mm:present> setEditor(<%=isEditor(wolk)%>)">

<table width="100%" height="80%" border="0" cellpadding="0" cellspacing="0" bordercolor="red">
  <tr>
    <td colspan="2" height="50"><h2>MMBase Knowledge Base</h2></td>
  </tr>
  <tr>
    <td valign="top" style="width:400px; overflow:auto">
      <%@include file="parts/tree.jsp"%>
    </td>
    <td valign="top">
    <mm:present referid="action">
      <mm:compare referid="action" value="add">
        <%@include file="parts/addpart.jsp"%>
      </mm:compare>
      <mm:compare referid="action" value="edit">
        <%@include file="parts/editpart.jsp"%>
      </mm:compare>      
    </mm:present>
    </td>
  </tr>
</mm:cloud>
</body>
</html>