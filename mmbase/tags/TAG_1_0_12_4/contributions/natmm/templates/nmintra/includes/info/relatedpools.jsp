<%@include file="../whiteline.jsp" %>
<table cellpadding="0" cellspacing="0"  align="center" border="0">
   <form method="POST" name="infoform" action="<%= javax.servlet.http.HttpUtils.getRequestURL(request) + templateQueryString %>" onSubmit="return postIt();"> 
   <tr>
      <td class="bold"><input type="text" name="termsearch" value="<%= (termSearchId.equals("") ? defaultSearchText : termSearchId )
                %>" onClick="if(this.value=='<%= defaultSearchText %>') { this.value=''; }" style="width:170px;" /></td>
   </tr>
   <%
   String lastpool=""; 
   %><mm:list nodes="<%= paginaID %>" path="pagina,contentrel,artikel,posrel,pools" orderby="pools.name" directions="UP"
        ><mm:first
            ><tr>
            	<td class="bold"><span class="light">Categorie</span></td>
   			</tr>
   			<tr><td>
            <select name="pool" style="width:172px;">
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
   if (lastpool.equals("")){
      %>
      <input type="hidden" name="pool" value=""/>
      <%
   } %>
   <tr><td>
      <table cellspacing="0" cellpadding="0" border="0">
      <tr>
         <td colspan="5" class="bold"><span class="light">Vanaf</span></td>
      </tr>
      <tr>
         <td>
            <select name="from_day">
               <option value="00">...
               <%
               for(int i=1; i<=31; i++) { 
                  %><option value="<% if(i<10){ %>0<% } %><%= i %>" <% 
                  if(fromDay==i) { %>SELECTED<% } %>><%= i %><% 
               } %>
            </select>
         </td>
         <td><img src="media/spacer.gif" alt="" border="0" width="2" height="1"></td>
         <td>
            <select name="from_month">
              <option value="00">...
              <%
              for(int i=1; i<=12; i++) { 
                  %><option value="<% if(i<10){ %>0<% } %><%= i %>" <% 
                  if(fromMonth==i) { %>SELECTED<% } %>><%= months_lcase[i-1] %><% 
              } %>
            </select>
         </td>
         <td><img src="media/spacer.gif" alt="" border="0" width="2" height="1"></td>
         <td>
            <select name="from_year">
               <option value="00">...
               <%
               for(int i=startYear; i<=thisYear; i++) { 
                  %><option value="<%= i %>" <% 
                  if(fromYear==i) { %>SELECTED<% } %>><%=  i %><% 
               } %>
            </select>
         </td>
      </tr>
      </table>
   </td></tr>
   <tr><td>
      <table cellspacing="0" cellpadding="0" border="0">
      <tr>
         <td colspan="5" class="bold"><span class="light">Tot en met</span></td>
      </tr>
      <tr>
         <td>
            <select name="to_day">
               <option value="00">...<%
               for(int i=1; i<31; i++) { 
                  %><option value="<% if(i<10){ %>0<% } %><%= i %>" <% 
                  if(toDay==i) { %>SELECTED<% } %>><%= i %><% 
               } %>
            </select>
         </td>
         <td><img src="media/spacer.gif" alt="" border="0" width="2" height="1"></td>
         <td>
            <select name="to_month">
               <option value="0000">...<%
               for(int i=1; i<=12; i++) { 
                  %><option value="<% if(i<10){ %>0<% } %><%= i %>" <% 
                  if(toMonth==i) { %>SELECTED<% } %>><%= months_lcase[i-1] %><% 
               } %>
            </select>
         </td>
         <td><img src="media/spacer.gif" alt="" border="0" width="2" height="1"></td>
         <td>
            <select name="to_year">
               <option value="0000">...<%
               for(int i=startYear; i<=thisYear; i++) { 
                  %><option value="<%= i %>" <% 
                  if(toYear==i) { %>SELECTED<% } %>><%= i %><% 
               } %>
            </select>
         </td>
      </tr>
      </table>
      <br/>
      <table border="0" cellspacing="0" cellpadding="0" style="width:100%;">
      <tr>
         <td>
            <input type="reset" name="clear" value="Wis" style="text-align:center;font-weight:bold;width:50px;" onClick="postIt('clear');">
         </td>
         <td style="text-align:right;padding-right:10px;">
            <input type="submit" name="submit" value="Zoek" style="text-align:center;font-weight:bold;">
         </td>
      </tr>
      </table>
   </td></tr>
   </form>
</table>
<script language="JavaScript" type="text/javascript">
<%= "<!--" %>
function postIt(action) {
    var href = document.infoform.action;
   if(action!='clear') {
	    var pool = document.infoform.elements["pool"].value;
	    if(pool != '') href += "&pool=" + pool;
	    var termsearch = document.infoform.elements["termsearch"].value;
   	 if(termsearch != '') href += "&termsearch=" + escape(termsearch);
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
   }
    document.location = href;
    return false;
}
<%= "//-->" %>
</script>