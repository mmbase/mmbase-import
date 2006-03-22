<%@include file="../includes/top0.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="../includes/top1_params.jsp" %>
<% 
if(rubriekExists&&pageExists) { 
   %>
   <%@include file="../includes/top2_cacheparams.jsp" %>
   <!-- cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application" -->
   <mm:import id="subdir" />
   <%@include file="../includes/top4_head.jsp" %>
   <table cellSpacing=0 cellPadding=0 width="100%" align="center" border=0 valign="top">
     <tr>
       <td style="width:48%"></td>
       <td style="width:744px;height:157px">
         <img src="media/1_header.jpg" alt="???" border="0">
       </td>
       <td style="width:48%"></td>
     </tr>
   </table>
   <table cellSpacing=0 cellPadding=0 width=744 align=center border=0 valign="top">
      <tr>
      <mm:node number="<%= rubriekID %>" notfound="skip">
      <mm:related path="parent,rubriek" orderby="parent.pos" max="4">
         <mm:node element="rubriek" jspvar="thisRubriek">
         <%
         styleSheet = thisRubriek.getStringValue("style");
         for(int s = 0; s< style1.length; s++) {
            if(styleSheet.indexOf(style1[s])>-1) { iRubriekStyle = s; } 
         }
         %>
         <td style="padding-right:5px;padding-left:5px;padding-bottom:10px;vertical-align:top;padding-top:10px">
           <table cellSpacing="0" cellPadding="0" style="vertical-align:top;width:170px;border-color:828282;border-width:1px;border-style:solid">
             <tr><td><img src="media/artikel.gif"></td></tr>
             <tr>
               <td style="padding:10px 10px 10px 10px">  
                 <div style="font-weight:bold;color:#<%=color1[iRubriekStyle]%>">RUSTIG SLAPEN!</div>
                 <div style="padding-bottom: 5px;"><b>Zolang het nog hhh</b></div>
                 Bekende Beverdflsdf sdf  I will send you a new version of designI will send you a new version of design
                 I will send you a new version of designI will send you a new version of design
                 rtsfszxctrertasad 
                 <span style="color:red">></span> <span style="font-weight: bold; color:#<%=color1[iRubriekStyle]%>">Jort Kelder!</span><br>
                 <a href="http://ya.ru" style="color:#<%=color1[iRubriekStyle]%>">Lees verder</a> <span style="color:#<%=color1[iRubriekStyle]%>">></span>
               </td>
             </tr>
           </table>
         </td>
         </mm:node>
      </mm:related>
      </mm:node>
      </tr>
   </table>
   </body>
   <%@include file="../includes/sitestatscript.jsp" %>
   </html>
   <!-- /cache:cache -->
   <% 
} %>
</mm:cloud>
