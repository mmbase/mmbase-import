<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@include file="parts/headers.jsp"%>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"  prefix="mm" %>
<%@include file="parts/basics.jsp"%>
<mm:cloud jspvar="wolk" method="asis" >
<mm:import externid="node" jspvar="node" vartype="String"><mm:node number="kbase.root"><mm:field name="number"/></mm:node></mm:import>
<mm:import externid="qnode" jspvar="qnode" vartype="String"/>
<mm:import externid="expanded" jspvar="expanded" vartype="String"/>

<html>
<head>
  <%@include file="parts/httpHeadIncludes.jsp"%>
</head>
<!-- als de node aanwezig is moet die de (javascript) currentNode worden -->
<body onload="<mm:present referid="node"> setCurrentFolder(<mm:write referid="node"/>);</mm:present>setEditor(<%=isEditor(wolk)%>)">
<table width="100%" height="80%" border="0" cellpadding="0" cellspacing="0" bordercolor="red">
  <tr>
    <td colspan="2" height="50" style="border-bottom:1px solid #797868"><h1>MMBase Knowledge Base</h1></td>
  </tr>
  <tr>
    <td valign="top" style="width:400px; overflow:auto">
      <%@include file="parts/tree.jsp"%>
    </td>
    <td valign="top">
      <table width="100%" cellpadding="0" cellspacine="0">
        <tr>
          <td>
            <%@include file="parts/toolbar.jsp"%>
          </td>
        </tr>
      </table>

      <mm:present referid="qnode">
        <%@include file="parts/showquestion.jsp"%>
      </mm:present>
    </td>
  </tr>
</mm:cloud>
</body>
</html>

