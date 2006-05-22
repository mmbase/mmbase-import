<%@include file="../includes/pagecounter.jsp" %>
<%@include file="../includes/getresponse.jsp" %>
<%--@include file="../includes/shoppingcartupdate.jsp" --%>
<% 
String sPageRef = (String) session.getAttribute("pageref");
if(sPageRef!=null&&!sPageRef.equals(paginaID)) { // set pagerefminone to sPagRef, set pageref to paginaID
	session.setAttribute("pagerefminone",sPageRef);
}
session.setAttribute("pageref",paginaID);
%>
<html>
  <head>
		<link rel="stylesheet" type="text/css" href="css/main.css">
	   <link rel="stylesheet" type="text/css" href="<%= styleSheet %>" />
		<title><mm:node number="<%= rootId %>" notfound="skipbody"><mm:field name="naam" /></mm:node
			 > - <mm:node number="<%= paginaID %>" notfound="skipbody"><mm:field name="titel" /></mm:node></title>
		<meta http-equiv="imagetoolbar" content="no">
		<script language="javascript" src="scripts/launchcenter.js"></script>
		<script language="javascript" src="scripts/cookies.js"></script>
		<script language="javaScript" src="scripts/screensize.js"></script>
  </head>
  <body scroll="auto" onUnLoad="javascript:setScreenSize()">
  	<%@include file="/editors/paginamanagement/flushlink.jsp" %>
	<table background="media/styles/<%= NMIntraConfig.style1[iRubriekStyle] %>.jpg" cellspacing="0" cellpadding="0">
	<%@include file="../includes/searchbar.jsp" %>
	<tr>
		<td class="black"><img src="media/spacer.gif" width="195" height="1"></td>
		<td class="black" style="width:70%;"><img src="media/spacer.gif" width="1" height="1"></td>
		<td class="black"><img src="media/spacer.gif" width="251" height="1"></td>
	</tr>
	<tr>
		<td rowspan="2"><%@include file="../includes/nav.jsp" %></td>