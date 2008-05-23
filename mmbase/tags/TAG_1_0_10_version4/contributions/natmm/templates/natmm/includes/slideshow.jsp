<%@include file="/taglibs.jsp" %>
<%@include file="../includes/request_parameters.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="../includes/top1_params.jsp" %>
<%@include file="../includes/top2_cacheparams.jsp" %>
<cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application" >
<!-- <%= new java.util.Date() %> -->
<% String previousImage = "-1";
   String nextImage = "-1";
   String thisImage = "";
   String otherImages = "";
   String justNextImage = "";
   String justPreviousImage = "";
   int totalNumberOfImages = 1;
   int thisImageNumber = 1;
   String imageId = request.getParameter("i");
   String offsetID= request.getParameter("o");
   String pageTitle = " " + NatMMConfig.getCompanyName() + ": ";
   String pageText = "<table cellspacing=\"0\" cellpadding=\"0\"><tr><td width=\"430\">"; %>
<%@include file="../includes/splitimagelist.jsp"%>
<% String pageUrl = "slideshow.jsp?o=" + offsetID+ "&r=" + rubriekID; %>
<mm:node number="<%= rubriekID %>" notfound="skipbody">
   <mm:field name="naam" jspvar="rubriek_name" vartype="String" write="false">
      <% if(!rubriek_name.equals("")){ pageTitle += rubriek_name; } %>
   </mm:field>
</mm:node>
<mm:node number="<%= thisImage %>" notfound="skipbody">
   <mm:field name="title" jspvar="images_title" vartype="String" write="false">
      <% if(!images_title.equals("")){
            pageTitle += " - " + images_title; 
            pageText += "<strong>" + images_title + "</strong>";
          } %>
   </mm:field>
   <% pageText += "<strong> [" + thisImageNumber + "/" + totalNumberOfImages + "]</strong>" ;%>
   <mm:field name="omschrijving" jspvar="images_omschrijving" vartype="String" write="false">
   <% if(!images_omschrijving.equals("")){
         pageText += " " + images_omschrijving;
      } %>
   </mm:field>
   <mm:field name="bron" jspvar="images_bron" vartype="String" write="false">
      <% if(!images_bron.equals("")){
             pageText += " | " + images_bron;
       } %>
   </mm:field>
   <% pageText += "</td><td>"; 
   if((!nextImage.equals("-1"))&&(nextImage.indexOf(",")>-1)){
      justNextImage = nextImage.substring(0,nextImage.indexOf(","));
   }
   if((!previousImage.equals("-1"))&&(previousImage.indexOf(",")>-1)){
      justPreviousImage = previousImage.substring(0,previousImage.indexOf(","));
   }
   if(!previousImage.equals("-1")&&!nextImage.equals("-1")) { 
      pageText += "<td style=\"text-align:center;\"><a href=\"" + 
      pageUrl + "&i=" + previousImage + 
      "\"><img src=\"../media/back_" + NatMMConfig.style1[iRubriekStyle] + ".gif\" border=\"0\" alt=\"\"></a></td><td style=\"text-align:center;\">&nbsp;<a class=\"nav\" href=\"" + 
      pageUrl + "&i=" + previousImage + "\">vorige</a></td>"; 
   }
   pageText += "<td>&nbsp; | &nbsp;</td>";
   if(!previousImage.equals("-1")&&!nextImage.equals("-1")) { 
      pageText += "<td style=\"text-align:center;\"><a class=\"nav\" href=\"" + 
      pageUrl + "&i=" + nextImage + "\">volgende</a>&nbsp;</td>" + "<td style=\"text-align:center;\"><a href=\"" + 
      pageUrl + "&i=" + nextImage + 
      "\"><img src=\"../media/submit_" + NatMMConfig.style1[iRubriekStyle] + ".gif\" border=\"0\" alt=\"\"></a></td>"; 
   }
   pageText += "</td></tr></table>"; %>
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
          if (iHeight - wHeight + 75 != 0){
             window.resizeBy(0, iHeight - wHeight + 75);
          }
      };
    
   </script>
   <link rel="stylesheet" type="text/css" href="../hoofdsite/themas/main.css"  title="default" />
   <% if(styleSheet!=null) { %><link rel="stylesheet" type="text/css" href="../<%= styleSheet %>" /><% } %>
   </head>
   <body style="padding:0px;margin:0px;overflow:no;text-align:center;" class="maincolor" onLoad="javascript:resizeDiv()">
      <a href="javascript:void(0);" onClick="window.close();" title="Klik op de foto om het venster te sluiten"><img src="<mm:image template="s(600x450)" />" style="margin:0px;" border="0"></a><br/>
      <div style="display:block;overflow:auto;width:600px;height:75px;" id="contentblock">
         <table width="600px;" border="0" cellspacing="0" cellpadding="0">
         <tr>
            <td style="padding-left:7px;padding-right:7px;padding-top:9px;padding-bottom:9px;">
               <%= pageText  %> 
            </td>
          </tr>
       </table>
       </div>
   </body>
   </mm:node>  
</html>
</cache:cache>
</mm:cloud>
