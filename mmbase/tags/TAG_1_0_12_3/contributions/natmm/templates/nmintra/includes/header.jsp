<% (new SimpleStats()).pageCounter(cloud,application,paginaID,request); %>
<%@include file="../includes/getresponse.jsp" %>
<html>
  <head>
      <base href="<%= javax.servlet.http.HttpUtils.getRequestURL(request) %>" />
		<link rel="stylesheet" type="text/css" href="css/main.css">
	  <link rel="stylesheet" type="text/css" href="<%= styleSheet %>" />
		<title><% 
    if(isPreview) { %>PREVIEW: <% } 
    %><mm:node number="<%= subsiteID %>" notfound="skipbody"><mm:field name="naam" /></mm:node
			 > - <mm:node number="<%= paginaID %>" notfound="skipbody"><mm:field name="titel" /></mm:node></title>
		<meta http-equiv="imagetoolbar" content="no">
		<script type="text/javascript" src="scripts/launchcenter.js"></script>
		<script type="text/javascript" src="scripts/cookies.js"></script>
		<script type="text/javascript" src="scripts/screensize.js"></script>
      <script type="text/javascript">
      function resizeBlocks() {	
      var MZ=(document.getElementById?true:false); 
      var IE=(document.all?true:false);
      var wHeight = 0;
      var infoPageDiff = 87;
      var navListDiff = 62;
      <mm:notpresent referid="showprogramselect">
        var smoelenBoekDiff = 378;
      </mm:notpresent>
      <mm:present referid="showprogramselect">
        var smoelenBoekDiff = 414;
      </mm:present>
      var linkListDiff = 511;
      var rightColumnDiff = 109;
      var minHeight = 300;
      if(IE){ 
        wHeight = document.body.clientHeight;
        if(wHeight>minHeight) {
          if(document.all['infopage']!=null) { 
            document.all['infopage'].style.height = (wHeight>infoPageDiff ? wHeight - infoPageDiff : 0); }
          if(document.all['navlist']!=null) { 
            document.all['navlist'].style.height = (wHeight>navListDiff ? wHeight - navListDiff : 0); }
          if(document.all['smoelenboeklist']!=null) {
            document.all['smoelenboeklist'].style.height = (wHeight>smoelenBoekDiff ? wHeight - smoelenBoekDiff : 0); }
          if(document.all['rightcolumn']!=null) {
            document.all['rightcolumn'].style.height = (wHeight>rightColumnDiff ? wHeight - rightColumnDiff : 0); }
          if(document.all['linklist']!=null) {
            document.all['linklist'].style.height = (wHeight>linkListDiff ? wHeight - linkListDiff : 0); }
        }
      } else if(MZ){
        wHeight = window.innerHeight;
        if(wHeight>minHeight) {
          if(document.getElementById('infopage')!=null) {
            document.getElementById('infopage').style.height= (wHeight>infoPageDiff ? wHeight - infoPageDiff : 0); }
          if(document.getElementById('navlist')!=null) {
            document.getElementById('navlist').style.height= (wHeight>navListDiff ? wHeight - navListDiff : 0); } 
          if(document.getElementById('smoelenboeklist')!=null) {
            document.getElementById('smoelenboeklist').style.height= (wHeight>smoelenBoekDiff ? wHeight - smoelenBoekDiff : 0); } 
          if(document.getElementById('rightcolumn')!=null) {
            document.getElementById('rightcolumn').style.height= (wHeight>rightColumnDiff ? wHeight - rightColumnDiff : 0); } 
          if(document.getElementById('linklist')!=null) {
            document.getElementById('linklist').style.height= (wHeight>linkListDiff ? wHeight - linkListDiff : 0); } 
        }
      }
      return false;
      }
      </script>
      <% 
      if(printPage) { 
         %>
         <style>
            body {
               overflow: auto;
               background-color: #FFFFFF
            }
         </style>
         <%
      } %>
  </head>
  <body <% 
        if(!printPage) { 
          %>onLoad="javascript:resizeBlocks();<mm:present referid="extraload"><mm:write referid="extraload" /></mm:present
          >" onResize="javascript:resizeBlocks();" onUnLoad="javascript:setScreenSize()"<%
        } else {
          %>onLoad="self.print();"<% 
        }
        %>>
  	<%@include file="/editors/paginamanagement/flushlink.jsp" %>
	<table <% if(!printPage) {%> background="media/styles/<%= NMIntraConfig.style1[iRubriekStyle] %>.jpg" <% } %> cellspacing="0" cellpadding="0" border="0">
	<% 
	if(!printPage) { 
	   %>
	   <%@include file="../includes/searchbar.jsp" %>
   	<tr>
   		<td class="black"><img src="media/spacer.gif" width="195" height="1"></td>
   		<td class="black" style="width:70%;"><img src="media/spacer.gif" width="1" height="1"></td>
   		<td class="black"><img src="media/spacer.gif" width="251" height="1"></td>
   	</tr>
   	<% 
	} 
	%>
	<tr>
		<% 
	   if(!printPage) { 
	      %><td rowspan="2"><%@include file="../includes/nav.jsp" %></td><% 
	   } 
	   %>