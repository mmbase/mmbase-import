<% (new SimpleStats()).pageCounter(cloud,application,paginaID,request); %>
<%@include file="../includes/getresponse.jsp" %>
<html>
  <head>
		<link rel="stylesheet" type="text/css" href="css/main.css">
	   <link rel="stylesheet" type="text/css" href="<%= styleSheet %>" />
		<title><mm:node number="<%= subsiteID %>" notfound="skipbody"><mm:field name="naam" /></mm:node
			 > - <mm:node number="<%= paginaID %>" notfound="skipbody"><mm:field name="titel" /></mm:node></title>
		<meta http-equiv="imagetoolbar" content="no">
		<script language="javascript" src="scripts/launchcenter.js"></script>
		<script language="javascript" src="scripts/cookies.js"></script>
		<script language="javaScript" src="scripts/screensize.js"></script>
      <script language="javaScript">
      function resizeBlocks() {	
      var MZ=(document.getElementById?true:false); 
      var IE=(document.all?true:false);
      var windowHeight = 0;
      var infoPageDiff = 87;
      var navListDiff = 62;
      var smoelenBoekDiff = 378;
      var linkListDiff = 511;
      var rightColumnDiff = 109;
      var minHeight = 300;
      if(IE){ 
        windowHeight = document.body.clientHeight;
        if(windowHeight>minHeight) {
          if(document.all['infopage']!=null) { document.all['infopage'].style.height = windowHeight - infoPageDiff; }
          if(document.all['navlist']!=null) { document.all['navlist'].style.height = windowHeight - navListDiff; }
          if(document.all['smoelenboeklist']!=null) { document.all['smoelenboeklist'].style.height = windowHeight - smoelenBoekDiff; }
          if(document.all['rightcolumn']!=null) { document.all['rightcolumn'].style.height = windowHeight - rightColumnDiff; }
          if(document.all['linklist']!=null) { document.all['linklist'].style.height = windowHeight - linkListDiff; }
        }
      } else if(MZ){
        windowHeight = window.innerHeight;
        if(windowHeight>minHeight) {
          if(document.getElementById('infopage')!=null) { document.getElementById('infopage').style.height= windowHeight - infoPageDiff; }
          if(document.getElementById('navlist')!=null) { document.getElementById('navlist').style.height= windowHeight - navListDiff; } 
          if(document.getElementById('smoelenboeklist')!=null) { document.getElementById('smoelenboeklist').style.height= windowHeight - smoelenBoekDiff; } 
          if(document.getElementById('rightcolumn')!=null) { document.getElementById('rightcolumn').style.height= windowHeight - rightColumnDiff; } 
          if(document.getElementById('linklist')!=null) { document.getElementById('linklist').style.height= windowHeight - linkListDiff; } 
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
  <body <% if(!printPage) { %>onLoad="javascript:resizeBlocks();" onResize="javascript:resizeBlocks();"<% } %> <%-- onUnLoad="javascript:setScreenSize()" --%>>
  	<%@include file="/editors/paginamanagement/flushlink.jsp" %>
	<table background="media/styles/<%= NMIntraConfig.style1[iRubriekStyle] %>.jpg" cellspacing="0" cellpadding="0" border="0">
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