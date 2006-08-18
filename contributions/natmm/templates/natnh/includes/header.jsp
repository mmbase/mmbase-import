<%@include file="calendar.jsp" %>
<html>
<head><title><% if(isPreview) { %>PREVIEW: <% } 
    %><mm:node number="<%= rootId %>"><mm:field name="naam" /></mm:node
    ><mm:node number="<%= rubriekId %>" notfound="skipbody"> - <mm:field name="naam" /></mm:node></title>
<link rel="stylesheet" type="text/css" href="css/website.css">
<mm:listnodes type="artikel" constraints="artikel.titel='Keywords voor portal'"
    ><meta name="description" content="<mm:field name="artikel.intro" />">
</mm:listnodes
><mm:listnodes type="artikel" constraints="artikel.titel='Description voor portal'"
    ><meta name="keywords" content="<mm:field name="artikel.intro" />">
</mm:listnodes
><meta name="author" content="MMatch - MMBase consultancy and implementation / www.mmatch.nl">
<meta http-equiv="imagetoolbar" content="no">
<script language="JavaScript" src="scripts/launchcenter.js"></script>
</head>
<body background="media/bg_top.gif"><a name="top"></a>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr><td colspan="2">
     <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td width="274" valign="top"><%@include file="/editors/paginamanagement/flushlink.jsp" %>
              <% 
              if(!isPreview) {
                %><a href="http://www.natuurherstel.nl"><img src="media/topgraphic.gif" border="0"  title="www.natuurherstel.nl"></a><%
              } else {
                %><img src="media/topgraphic.gif" border="0"  title="www.natuurherstel.nl"><%
              } %>
              </td>
            <td valign="top" class="topgradient" background="media/bg_topgradient.gif"><img src="media/spacer.gif" width="1" height="7"><br>
                <div align="right">
						 <%
							String breadCrumClass = "class=\"contrast\""; 
							boolean showEndLeaf = true;
						 %>
						 <%@include file="breadcrums.jsp" %>
					</div>
            </td>
        </tr>
    </table>
    <td width="73"><img src="media/spacer.gif" width="73" height="1"></td>
</tr>
<tr>
    <td width="73"><img src="media/spacer.gif" width="73" height="1"></td>
    <td width="100%">
    <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr><td><%@include file="nav.jsp" %></td></tr>
        <tr><td><img src="media/spacer.gif" width="1" height="20"></td></tr>
        <tr><td>
