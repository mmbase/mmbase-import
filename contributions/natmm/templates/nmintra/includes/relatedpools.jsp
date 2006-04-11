<%
if(hasPools||isArchive) { 
   %><%@include file="../includes/whiteline.jsp" %>
   <table cellpadding="0" cellspacing="0"  align="center" border="0">
   <form method="POST" name="infoform" action="<%= sTemplateUrl %><%= templateQueryString %>" onSubmit="return postIt();"><% 
   if(isArchive) { 
      %>
      <tr>
         <td class="bold"><span class="light_<%= cssClassName %>">Zoekterm</span></td>
      </tr>
      <tr>
         <td class="bold"><input type="text" name="termsearch" value="<%= termSearchId %>" class="<%= cssClassName %>" style="width:170px;" /></td>
      </tr>
      <tr>
         <td class="bold"><span class="light_<%= cssClassName %>">Categorie</span></td>
      </tr>
      <%
   }
}
String lastpool=""; 
%><mm:list nodes="<%= pageId %>" path="pagina,contentrel,artikel,posrel,pools" orderby="pools.name" directions="UP"
     ><mm:first
         ><tr><td>
         <select name="pool" class="<%= cssClassName %>" style="width:172px;" <% if(!isArchive) { %>onChange="javascript:postIt();"<% } %>>
     </mm:first
     ><mm:field name="pools.number" jspvar="thispool" vartype="String" write="false"><%
         if(!lastpool.equals(thispool)) { 
             %><option value="<%= thispool %>"  <% if(thisPool.equals(thispool)){ %>SELECTED<% } 
                 %>><mm:field name="pools.name" /><% 
         } 
         lastpool = thispool;
     %></mm:field
     ><mm:last
         ><option value="-1" <% if(thisPool.equals("-1")){ %>SELECTED<% } 
             %>>Alles</select>
          </td></tr>
     </mm:last
></mm:list><% 
if (lastpool.equals("")){ %>
<input type="hidden" name="pool" value=""/>
<% }
if(isArchive) { 
%><tr><td>
   <table cellspacing="0" cellpadding="0" border="0">
   <tr>
      <td colspan="5" class="bold"><span class="light_<%= cssClassName %>">Vanaf</span></td>
   </tr>
   <tr>
      <td><select name="from_day" class="<%= cssClassName %>"><option value="00">...<%
              for(int i=1; i<=31; i++) { 
                  %><option value="<% if(i<10){ %>0<% } %><%= i %>" <% 
                  if(fromDay==i) { %>SELECTED<% } %>><%= i %><% 
              } %></select></td>
      <td><img src="media/spacer.gif" alt="" border="0" width="2" height="1"></td>
      <td><select name="from_month" class="<%= cssClassName %>"><option value="00">...<%
              for(int i=1; i<=12; i++) { 
                  %><option value="<% if(i<10){ %>0<% } %><%= i %>" <% 
                  if(fromMonth==i) { %>SELECTED<% } %>><%= months_lcase[i-1] %><% 
              } %></select></td>
      <td><img src="media/spacer.gif" alt="" border="0" width="2" height="1"></td>
      <td><select name="from_year" class="<%= cssClassName %>"><option value="00">...<%
              for(int i=startYear; i<=thisYear; i++) { 
                  %><option value="<%= i %>" <% 
                  if(fromYear==i) { %>SELECTED<% } %>><%=  i %><% 
              } %></select></td>
   </tr>
   </table>
</td></tr>
<tr><td>
   <table cellspacing="0" cellpadding="0" border="0">
   <tr>
      <td colspan="5" class="bold"><span class="light_<%= cssClassName %>">Tot en met</span></td>
   </tr>
   <tr>
      <td><select name="to_day" class="<%= cssClassName %>"><option value="00">...<%
              for(int i=1; i<31; i++) { 
                  %><option value="<% if(i<10){ %>0<% } %><%= i %>" <% 
                  if(toDay==i) { %>SELECTED<% } %>><%= i %><% 
              } %></select></td>
      <td><img src="media/spacer.gif" alt="" border="0" width="2" height="1"></td>
      <td><select name="to_month" class="<%= cssClassName %>"><option value="0000">...<%
              for(int i=1; i<=12; i++) { 
                  %><option value="<% if(i<10){ %>0<% } %><%= i %>" <% 
                  if(toMonth==i) { %>SELECTED<% } %>><%= months_lcase[i-1] %><% 
              } %></select></td>
      <td><img src="media/spacer.gif" alt="" border="0" width="2" height="1"></td>
      <td><select name="to_year"class="<%= cssClassName %>"><option value="0000">...<%
              for(int i=startYear; i<=thisYear; i++) { 
                  %><option value="<%= i %>" <% 
                  if(toYear==i) { %>SELECTED<% } %>><%= i %><% 
              } %></select></td>
   </tr>
   </table>
   <br>
   <div align="right"><input type="submit" name="submit" value="Zoek" class="<%= cssClassName %>" style="text-align:center;font-weight:bold;"></div>
</td></tr>
<% 
}
if(hasPools||isArchive) { %>
   </form>
   </table>
   <%@include file="../includes/whiteline.jsp" %>
   <script language="JavaScript" type="text/javascript">
   <%= "<!--" %>
   function postIt() {
       var href = document.infoform.action;
		 var pool = document.infoform.elements["pool"].value;
       if(pool != '') href += "&pool=" + pool;
   <% if(isArchive) { %>
       var termsearch = document.infoform.elements["termsearch"].value;
       if(termsearch != '') href += "&termsearch=" + termsearch;
       var period = "";
       var v = document.infoform.elements["from_day"].value;
       if(v != '') { period += v; } else { period += '00'; }
       v = document.infoform.elements["from_month"].value;
       if(v != '') { period += v; } else { period += '00'; }
       v = document.infoform.elements["from_year"].value;
       if(v != '') { period += v; } else { period += '0000'; }
       v = document.infoform.elements["to_day"].value;
       if(v != '') { period += v; } else { period += '00'; }
       v = document.infoform.elements["to_month"].value;
       if(v != '') { period += v; } else { period += '00'; }
       v = document.infoform.elements["to_year"].value;
       if(v != '') { period += v; } else { period += '0000'; }
       if(period != '0000000000000000') href += "&d=" + period;
   <% } %>
       document.location = href;
       return false;
   }
   <%= "//-->" %>
   </script><% 
} %>
