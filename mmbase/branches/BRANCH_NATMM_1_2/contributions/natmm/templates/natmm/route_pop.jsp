<%@include file="includes/top0.jsp" %>
<% boolean isIE = (request.getHeader("User-Agent").toUpperCase().indexOf("MSIE")>-1); %>
<mm:cloud jspvar="cloud">
<%@include file="/editors/mailer/util/memberid_get.jsp" %>
<%@include file="includes/top2_cacheparams.jsp" %>
<cache:cache groups="<%= paginaID %>" key="<%= cacheKey + (memberid==null ? "~anonymous" : "~member" ) %>" time="<%= expireTime %>" scope="application">
<!-- <%= new java.util.Date() %> -->
<%@include file="includes/image_vars.jsp" %>
<%@include file="includes/getstyle.jsp" %>
<%

String shortyRol =  ""; imgFormat = "route";
if(!artikelID.equals("-1")) { 
   %>
   <mm:node number="<%= artikelID %>">
   <html>
   <head>
      <link rel="stylesheet" type="text/css" href="hoofdsite/themas/main.css"  title="default" />
      <link rel="stylesheet" type="text/css" href="hoofdsite/themas/natuurin.css" />
      <%-- link rel="stylesheet" type="text/css" href="hoofdsite/themas/fontsize1.0.css" / --%>
      <%-- link rel="alternate stylesheet" type="text/css" href="hoofdsite/themas/fontsize1.2.css" title="groot" / --%>
      <%-- link rel="alternate stylesheet" type="text/css" href="hoofdsite/themas/fontsize1.4.css" title="groter" / --%>
      <style rel="stylesheet" type="text/css">
      @media screen, print {
         table.maintable{
            width:85%;
         }
         td.print_ico {
            background: url(media/images/icons/print.gif) no-repeat ;
         }
         td.close_ico {
            background: url(media/images/icons/close.gif) no-repeat ;
         }
         td.top_ico {
            background: url(media/images/icons/top.gif) no-repeat ;
         }
      }
      @media print {
         body,td{
         color:#000000;
         }
         table.maintable{
            width:95%;
         }
         td.print_ico {
            display: none;
         }
         td.close_ico {
            display: none;
         }
         td.top_ico {
            display: none;
         }
      }
      </style>
      <script>
      function printIt(){  
      if(window.print) {
          window.print() ;  
      } else {
          var WebBrowser = '<OBJECT ID="WebBrowser1" WIDTH=0 HEIGHT=0 CLASSID="CLSID:8856F961-340A-11D0-A96B-00C04FD705A2"></OBJECT>';
         document.body.insertAdjacentHTML('beforeEnd', WebBrowser);
          WebBrowser1.ExecWB(6, 2);//Use a 1 vs. a 2 for a prompting dialog box    
         WebBrowser1.outerHTML = "";  
      }
      }
      </script>
      <title>Natuurmonumenten: <mm:field name="status"/>. <mm:field name="titel" /></title>
   </head>
   <body leftmargin="0" topmargin="0" rightmargin="0" bottommargin="0" marginwidth="0" marginheight="0">
   <a name="top" id="top"></a>
   <table border="0" cellspacing="0" cellpadding="0" align="center" class="maintable">
      <tr>
      <td valign="top"> 
         <table width="100%" border="0" cellspacing="2" cellpadding="2">
            <tr>
                <td></td>
                <td rowspan="3" align="right"><%-- removed old logo --%></td>
            </tr>
            <tr>
                <td rowspan="2"><span style="font-weight: bold;">NATUURROUTES VAN NATUURMONUMENTEN</span></td>
            </tr>
            <tr></tr> <!-- do not delete this <tr></tr>, it will ruin the layout -->
            <tr>
                <td>
                   <span class="colortitle"><mm:field name="status"/>| <mm:field name="titel"/></span>
                   | <mm:field name="type" />
                </td>
                <td align="right" valign="bottom">
                  <table width="100%" cellspacing="0" cellpadding="1">
                  <tr>
                     <td width="100%"></td>
                     <td align="right" class="print_ico"><a href="javascript:printIt()"><img src="media/trans.gif" alt="" width="15" height="15" border="0"></a></td>
                     <td>&nbsp;</td>
                     <td align="right" class="close_ico"><a href="javascript:window.close()"><img src="media/trans.gif" alt="" width="13" height="15" border="0"></a></td>
                  </tr>
                  </table>
               </td>
            </tr>
         </table>

         <table class="dotline"><tr><td height="3"></td></tr></table>
               <mm:field name="intro">
                  <mm:isnotempty>
                  <div style="margin:5px 0px 5px 0px">
                  <strong><mm:write /></strong>
                  <table class="dotline"><tr><td height="3"></td></tr></table>
                  </div>
                  </mm:isnotempty>
               </mm:field>
               
               <mm:field name="text">
                  <mm:isnotempty>
                  <div style="margin:5px 0px 5px 0px">
                  <strong><mm:write /></strong>
                  <table class="dotline"><tr><td height="3"></td></tr></table>
                  </div>
                  </mm:isnotempty>
               </mm:field>
               <mm:related path="posrel,paragraaf" fields="paragraaf.number" orderby="posrel.pos">
                  <mm:node element="paragraaf">
                     <mm:first>
                        <span class="colortitle">Inhoud</span> | 
                     </mm:first>
                     <mm:field name="titel_zichtbaar">
                        <mm:compare value="0" inverse="true">
                           <mm:field name="titel">
                              <mm:isnotempty>
                                 <a href="route_pop.jsp?<%= request.getQueryString() %>#<mm:field name="number" />"><mm:write /></a> | 
                              </mm:isnotempty>
                           </mm:field>
                        </mm:compare>
                     </mm:field>
                  </mm:node>
               </mm:related>  
               <br><br>
               <mm:related path="posrel,paragraaf" fields="paragraaf.number" orderby="posrel.pos">
                  <mm:node element="paragraaf">
                  <mm:first inverse="true">
                     <table class="dotline"><tr><td height="3"></td></tr></table>
                  </mm:first>
                  <a name="<mm:field name="number" />" id="<mm:field name="number" />"></a>
                  <mm:field name="titel_zichtbaar">
                     <mm:compare value="0" inverse="true">
                        <mm:field name="titel">
                           <mm:isnotempty><span class="colortitle"><mm:write /></span><br></mm:isnotempty>
                        </mm:field>
                     </mm:compare>
                  </mm:field>
                  <%@include file="includes/image_logic.jsp" %>
                  <mm:field name="tekst">
                     <mm:isnotempty><mm:write /><br/></mm:isnotempty>
                  </mm:field>
                  <table width="100%" cellspacing="0" cellpadding="0" align="right">
                     <tr>
                        <td width="100%"></td>
                        <td align="right" class="top_ico">
                         <a href="route_pop.jsp?<%= request.getQueryString() %>#top"><img src="media/trans.gif" alt="" width="15" height="15" border="0"></a>
                        </td>
                     </tr>
                  </table>
                  <br />
                  <mm:last>
                     <table class="dotline"><tr><td height="3"></td></tr></table>
                  </mm:last>
                  </mm:node>
               </mm:related>  
               </div>
            </td>
            
            <%-- nieuw huisstijl--%>
            <td valign="top" width="159">
            <img src="media/natmm_logo_rgb1.gif" width="159" height="216" style="padding:0;">
            </td>
            
            </tr>
            <tr>
             <td align="right" valign="bottom" colspan="2">
               <table width="100%" cellspacing="0" cellpadding="1">
               <tr>
                  <td width="100%"></td>
                  <td align="right" class="print_ico"><a href="javascript:printIt()"><img src="media/trans.gif" alt="" width="15" height="15" border="0"></a></td>
                  <td>&nbsp;</td>
                  <td align="right" class="close_ico"><a href="javascript:window.close()"><img src="media/trans.gif" alt="" width="13" height="15" border="0"></a></td>
               </tr>
               </table>
             </td>
         </tr>
         </table>

         </td>         
      </tr>
   </table>
   </body>
   </html>
   </mm:node>
   <% 
} else { 
   %>
   no article selected
   <%
} %>
</cache:cache>
</mm:cloud>