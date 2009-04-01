<%@include file="/taglibs.jsp" %>
<%@include file="../includes/request_parameters.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="../includes/getstyle.jsp" %>
<%
String pageTitle = " " + NatMMConfig.getCompanyName() + ": ";
String pageText = "";
boolean bFirst = true; 
%>
<mm:node number="<%= rubriekID %>" notfound="skipbody">
   <mm:field name="naam" jspvar="rubriek_name" vartype="String" write="false">
      <% if(!rubriek_name.equals("")){ pageTitle += rubriek_name; } %>
   </mm:field>
</mm:node>
<mm:node number="<%= imgID %>" notfound="skipbody">
	<%-- NMCMS-639 --%>
    <% bFirst = false; %>
   <title><%= pageTitle %></title>
   <script type="text/javascript" language="Javascript">
   <!-- // 
   function resizeDiv() {
      iHeight = document.images[0].height;
      var IE=(document.all?true:false);
      if(IE){ 
         wHeight = document.body.clientHeight;
         // document.all['contentblock'].style.height = wHeight - iHeight;
      } else {
         wHeight = window.innerHeight;
         // document.getElementById('contentblock').style.height = wHeight - iHeight;
      }
      <% if(!bFirst) { %>
         window.resizeBy(0, iHeight - wHeight + 150);
      <% } else { %>
         window.resizeBy(0, iHeight - wHeight);
      <% } %>        
   };
   if ((navigator.appName == "Microsoft Internet Explorer") && (parseInt(navigator.appVersion) < 4 )) {
      document.write("<link rel=stylesheet href=\"../hoofdsite/themas/ie3_main.css\" type=\"text/css\">"); 
   } else {
      document.write("<link rel=stylesheet href=\"../hoofdsite/themas/main.css\" type=\"text/css\">");
   }
   // -->
   </script>
   <% if(styleSheet!=null) { %><link rel="stylesheet" type="text/css" href="../<%= styleSheet %>" /><% } %>
</head>
<body style="padding:0px;margin:0px;overflow:no;text-align:center;" class="footer" onLoad="javascript:resizeDiv()">
<%-- NMCMS-639 --%>
<a href="javascript:void(0);"><img src="<mm:image />" style="margin:0px;" border="0"></a><br/>
<% if(!bFirst) { 
   %><div style="display:block;overflow:auto;width:600px;height:150px;" id="contentblock">
   <table width="580px;" border="0" cellspacing="0" cellpadding="0">
   <tr>
      <td style="padding-left:7px;padding-right:7px;padding-top:9px;padding-bottom:9px;">
      <%-- NMCMS-639 --%>
        <%@include file="image_metadata.jsp" %>
      </td>
   </tr>
   
   </table>
   </div><%
 } %>
</body>
</html>
</mm:node>
</mm:cloud>