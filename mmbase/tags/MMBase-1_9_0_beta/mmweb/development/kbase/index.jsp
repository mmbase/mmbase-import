<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@page language="java" contentType="text/html; charset=utf-8"%>
<%
  response.setHeader("Cache-Controll","no-cache");
  response.setHeader("Cache-Controll","no-store");
  response.setHeader("Cache-Controll","must-revalidate");
  response.setDateHeader("Expires",new Date().getTime());
%>
<%
  Set extraParams=new HashSet();
  extraParams.add("portal");
  extraParams.add("page");
  setExtraParams(request,extraParams);
%>
<%@include file="parts/basics.jsp"%>
<mm:cloud jspvar="wolk" method="asis" >

<mm:import externid="node" jspvar="node" vartype="String">
  <mm:node number="kbase.root"><mm:field name="number"/></mm:node>
</mm:import>
<mm:import externid="qnode" jspvar="qnode" vartype="String"/>
<mm:import externid="expanded" jspvar="expanded" vartype="String"/>
	
  <%@include file="/includes/getids.jsp"%>
 <%
   //linkjes voor javascript toevoegen
        String realpath=getRealPath(request);
 	 List l=new ArrayList();
	 l.add(realpath+"/js/callbacks.js");
 	 l.add(realpath+"/js/treeview.js");
	 pageContext.setAttribute("jScripts",l);
	 
   //body onload event toevoegen
	 List l1=new ArrayList();
         l1.add("setImageDir('"+realpath+"/img/')");
	 l1.add("setCurrentFolder("+node.trim()+")");
	 l1.add("setEditor("+isEditor(wolk)+")");
         l1.add("setExtraParamsJs('" + getParamsFormatted("url", getExtraParams(request)) + "')");
	 pageContext.setAttribute("bLoadEvs",l1);

  //linkjes voor css
	List l2 = new ArrayList();
	l2.add("css/kbase-form.css");
	l2.add("css/treeview.css");
	pageContext.setAttribute("cssScripts", l2);
%>
	
<%@include file="/includes/header.jsp" %>

<td colspan="2" valign="top">
  <mm:import id="title">MMBase Knowledge Base</mm:import>
  <mm:include page="index_real.jsp" referids="title" />
</td>
<%@include file="/includes/footer.jsp"
%></mm:cloud>



