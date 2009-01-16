<mm:relatednodes type="images" max="1">
   <mm:field name="number" jspvar="images_number" vartype="String" write="false">
	<table style="width:1%;" border="0" cellspacing="0" cellpadding="0">
      <tr>
         <td style="padding:0px;margin:0px;text-align:right;">
         <mm:field name="reageer" jspvar="showpopup" vartype="String" write="false"><%
            if(showpopup.equals("1")) {
               String requestURI = request.getRequestURI();
               requestURI = requestURI.substring(0,requestURI.lastIndexOf("/"));
               String readmoreUrl = "javascript:launchCenter('" + requestURI + "/includes/fotopopup.jsp?i=" + images_number + "&rs=" + styleSheet + "','foto',600,600,'location=no,directories=no,status=no,toolbars=no,scrollbars=no,resizable=yes');setTimeout('newwin.focus();',250);"; 
               %><div style="position:relative;left:-17px;top:7px;"><div style="visibility:visible;position:absolute;top:0px;left:0px;"><a href="javascript:void(0);" onClick="<%= readmoreURL %>"><img src="media/zoom.gif" border="0" alt="klik voor vergroting" /></a></div></div>
               <a href="javascript:void(0);" onClick="<%= readmoreUrl %>"><% 
            } 
            %><img src="<mm:image template="s(76)" />" border="0"><%
            if(showpopup.equals("1")) {
               %></a><% 
            } 
            %>
         </mm:field>
         </td>
      </tr>
   </table>
   </mm:field>
</mm:relatednodes>	 