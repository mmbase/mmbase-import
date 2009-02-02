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
<%--<script type="text/javascript" language="javaScript" src="scripts/skyscraper_cookie.js"></script> --%>
</head>
<body <%--onLoad="loadSkyscraper()"--%> background="media/bg_top.gif"> 

<%--
   <table width="100%" border="0" cellspacing="0" cellpadding="0" align="center" valign="top">
   <tr>
      <td style="width:50%"></td>
      <td style="width:744px;">

         <div id="bigdiv" style="position:relative;">
         <div id="skyscraper" style="visibility: hidden; position:absolute; left:-1000px; top:-600px; width:2000; height:2000; color:#000000; z-index:101;">
            <div style="position: absolute; left: 1220px; top:722px; z-index:102; display: block;">
               <img src="flash/cross.gif" onclick="closeSkyscraper();"/>
            </div>
            <object classid="clsid:d27cdb6e-ae6d-11cf-96b8-444553540000" codebase="http://fpdownload.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=8,0,0,0" width="2000" height="2000" id="overlayerbanner_pop_600x600" align="middle">
               <param name="allowScriptAccess" value="sameDomain" />
               <param name="movie" value="flash/080923_hart_v1.swf?clickTag=http://www.natuurmonumenten.nl&clickTarget=_blank" />
               <param name="menu" value="false" />
               <param name="quality" value="high" />
               <param name="scale" value="noscale" />
               <param name="wmode" value="transparent" />
               <param name="bgcolor" value="#ffffff" />
               <embed src="flash/080923_hart_v1.swf?clickTag=http://www.natuurmonumenten.nl&clickTarget=_blank" menu="false" quality="high" scale="noscale" wmode="transparent" bgcolor="#ffffff" width="2000" height="2000" name="overlayerbanner_pop_600x600" align="middle" allowScriptAccess="sameDomain" type="application/x-shockwave-flash" pluginspage="http://www.macromedia.com/go/getflashplayer" />
            </object>
         </div>
      </td>
   </tr>
   </table>
--%>
<a name="top"></a>
<table width="100%" border="0" cellspacing="0" cellpadding="0" valign="top">
<tr height="1px"><td colspan="2" valign="top">
     <table width="100%" border="0" cellspacing="0" cellpadding="0" valign="top">
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
    </td>
    <td width="159" rowspan="2" valign="top" class="topgradient"  style="padding-top:29px;">
       
    <img src="media/natmm_logo_rgb1.gif" width="159" height="216">
    </td>
    <td width="73" rowspan="2">
	<img width="73" height="1" src="media/spacer.gif"/>
	</td>
</tr>
<tr height="1px" valign="top">
    <td width="73"><img src="media/spacer.gif" width="73" height="1"></td>
    <td width="100%" height="1px">
    <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr><td><%@include file="nav.jsp" %></td></tr>
        <tr><td><img src="media/spacer.gif" width="1" height="1"></td></tr>
     </table>
	</td>
</tr>
<tr>
    <td width="73"><img src="media/spacer.gif" width="73" height="1"></td>
    <td width="100%" colspan="2">    
    <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr><td>   
