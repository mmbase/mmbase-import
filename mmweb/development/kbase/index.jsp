<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@page language="java" contentType="text/html; charset=utf-8"%>
<%@include file="parts/basics.jsp"%>
<mm:cloud method="asis" jspvar="wolk">
<mm:import externid="node" jspvar="node" vartype="String">
  <mm:node number="kbase.root"><mm:field name="number"/></mm:node>
</mm:import>


<h3>Under construction</h3>
<html>
<head>
  <link rel="stylesheet" type="text/css" href="<%=getRealPath(request)%>/css/mmbase.css" />
  <link rel="stylesheet" type="text/css" href="<%=getRealPath(request)%>/css/treeview.css" />
  <link rel="stylesheet" type="text/css" href="<%=getRealPath(request)%>/css/kbase-form.css" />
  <script type="text/javascript" language="javascript" src="<%=getRealPath(request)%>/js/callbacks.js"></script>
  <script type="text/javascript" language="javascript" src="<%=getRealPath(request)%>/js/treeview.js"></script>
</head>
<body onload="setImageDir('<%=getRealPath(request)%>/img/') ; setCurrentFolder(<%=node.trim()%>) ; setEditor('<%=isEditor(wolk)%>') ">
<%--; setExtraParams('<%=getParamsFormatted(request,"url",getExtraParams(request))%>') --%>
<%
  Set extraParams=new HashSet();
  extraParams.add("koek");
  extraParams.add("zopie"); 
  setExtraParams(request, extraParams);
%>
<mm:include page="index_real.jsp"/>
</body>
</html>
</mm:cloud>
