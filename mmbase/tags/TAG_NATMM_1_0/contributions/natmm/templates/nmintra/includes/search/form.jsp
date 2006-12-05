<% 
// **************** people finder: right bar with the form *****************
%><%@include file="../whiteline.jsp" %>
<form method="POST" name="advsearchform" action="<%= ph.createPaginaUrl(paginaID,request.getContextPath()) %>" onSubmit="return postIt('submit');">
<table cellpadding="0" cellspacing="0"  align="center">
  <tr>
     <td class="bold"><input type="text" name="search" value="<%= (searchIsOn ? defaultSearchText : searchId )
       %>" style="width:170px;" onClick="if(this.value=='<%= defaultSearchText %>') { this.value=''; }" /></td>
  </tr>
  <tr>
     <td class="bold"><span class="light">Rubrieken</span></td>
  </tr>
  <tr>
    <td>
        <select name="categorie" style="width:172px;" />
              <option value="">Alles</option>
              <mm:list nodes="<%= subsiteID %>" path="rubriek1,parent,rubriek2" orderby="parent.pos" constraints="rubriek2.issearchable = '1'">
                 <mm:field name="rubriek2.number" jspvar="sCategoryNumber" vartype="String">
                 <mm:field name="rubriek2.naam" jspvar="sCategoryName" vartype="String">
                    <option value="<%= sCategoryNumber %>" <%
                       if((sCategory != null) && (sCategory.equals(sCategoryNumber))) { %> selected="selected" <% } 
                       %>><%= sCategoryName.toUpperCase() %></option>
                 </mm:field>
                 </mm:field>
              </mm:list>
           </select>
    </td>
  </tr>
  <tr>
    <td class="bold"><span class="light">Categorie</span></td>
  </tr>
  <tr>
    <td>
      <select name="pool" style="width:172px;">
        <option value="">Alles</option>
        <mm:list path="pools" orderby="pools.name" directions="UP">
            <mm:field name="pools.number" jspvar="thispool" vartype="String" write="false">
               <option value="<%= thispool %>"  <% if(sPool.equals(thispool)){ %>SELECTED<% } %>><mm:field name="pools.name" />
            </mm:field>
        </mm:list>
    </td>
  </tr>
  <tr>
    <td class="bold"><span class="light">Doorzoek archief</span></td>
  </tr>
  <tr>
    <td>
      <select name="archive" style="width:172px;">
        <option value="ja" <% if (sArchive.equals("ja")) {%> SELECTED <%} %>>ja</option>
        <option value="nee" <% if (sArchive.equals("nee")) {%> SELECTED <%} %>>nee</option>
    </td>
  </tr>
  <tr>
    <td>
       <table cellspacing="0" cellpadding="0" border="0">
       <tr>
          <td colspan="5" class="bold"><span class="light">Vanaf</span></td>
       </tr>
       <tr>
          <td><select name="from_day"><option value="00">...<%
               for(int i=1; i<=31; i++) { 
                   %><option value="<% if(i<10){ %>0<% } %><%= i %>" <% 
                    if(fromDay==i) { %>SELECTED<% } %>><%= i %><% 
                } %></select></td>
          <td><img src="media/spacer.gif" alt="" border="0" width="2" height="1"></td>
          <td><select name="from_month"><option value="00">...<%
                for(int i=1; i<=12; i++) { 
                   %><option value="<% if(i<10){ %>0<% } %><%= i %>" <% 
                    if(fromMonth==i) { %>SELECTED<% } %>><%= months_lcase[i-1] %><% 
              } %></select></td>
          <td><img src="media/spacer.gif" alt="" border="0" width="2" height="1"></td>
          <td><select name="from_year"><option value="00">...<%
                 for(int i=startYear; i<=thisYear; i++) { 
                    %><option value="<%= i %>" <% 
                   if(fromYear==i) { %>SELECTED<% } %>><%=  i %><% 
               } %></select></td>
       </tr>
     </table>
     </td>
  </tr>
  <tr>
    <td>
      <table cellspacing="0" cellpadding="0" border="0">
       <tr>
          <td colspan="5" class="bold"><span class="light">Tot en met</span></td>
       </tr>
       <tr>
          <td><select name="to_day"><option value="00">...<%
                 for(int i=1; i<31; i++) { 
                    %><option value="<% if(i<10){ %>0<% } %><%= i %>" <% 
                      if(toDay==i) { %>SELECTED<% } %>><%= i %><% 
                 } %></select></td>
          <td><img src="media/spacer.gif" alt="" border="0" width="2" height="1"></td>
          <td><select name="to_month"><option value="0000">...<%
                 for(int i=1; i<=12; i++) { 
                    %><option value="<% if(i<10){ %>0<% } %><%= i %>" <% 
                      if(toMonth==i) { %>SELECTED<% } %>><%= months_lcase[i-1] %><% 
                 } %></select></td>
          <td><img src="media/spacer.gif" alt="" border="0" width="2" height="1"></td>
          <td><select name="to_year"><option value="0000">...<%
                 for(int i=startYear; i<=thisYear; i++) { 
                    %><option value="<%= i %>" <% 
                     if(toYear==i) { %>SELECTED<% } %>><%= i %><% 
                  } %></select></td>
         </tr>
     </table>
     </td>
  </tr>
  <tr><td><img src="media/spacer.gif" width="1" height="20"></td></tr>
  <tr>
    <td>
      <table cellspacing="0" cellpadding="0" border="0" width="100%">
        <tr>
          <td>
            <input type="reset" name="clear" value="Wis" style="text-align:center;font-weight:bold;width:42px;" onClick="postIt('clear');">
          </td>
          <td>
             <div align="right"><input type="submit" name="submit" value="Zoek" style="text-align:center;font-weight:bold;width:42px;">&nbsp;</div>
          </td>
        </tr>
     </table>
    </td>
  </tr>
</table>
</form>
<% 
String sPageRefMinOne = (String) session.getAttribute("pagerefminone");
if(sPageRefMinOne!=null) {
  %>
  <mm:list nodes="<%= sPageRefMinOne %>" path="pagina,gebruikt,paginatemplate">
    <a href="<mm:field name="paginatemplate.url"/>?p=<%= sPageRefMinOne %>" style="color:#FFFFFF;margin-left:20px;">
      Terug naar vorige pagina
    </a>
  </mm:list>
  <% 
} %>

<script language="JavaScript" type="text/javascript">
<!--
 function postIt(action) {
    var href = document.advsearchform.action;
    if(action!='clear') {
    var termsearch = document.advsearchform.elements["search"].value;
    href += "?search=" + termsearch;
    var pool = document.advsearchform.elements["pool"].value;
    if(pool != '') href += "&pool=" + pool;
    var categorie = document.advsearchform.elements["categorie"].value;
    if(categorie != '') href += "&categorie=" + categorie;
    var archive = document.advsearchform.elements["archive"].value;
    if(archive != '') href += "&archive=" + archive;
    var period = "";
    var v = document.advsearchform.elements["from_day"].value;
    if(v != '') { period += v; } else { period += '00'; }
    v = document.advsearchform.elements["from_month"].value;
    if(v != '') { period += v; } else { period += '00'; }
    v = document.advsearchform.elements["from_year"].value;
    if(v != '') { period += v; } else { period += '0000'; }
    v = document.advsearchform.elements["to_day"].value;
    if(v != '') { period += v; } else { period += '00'; }
    v = document.advsearchform.elements["to_month"].value;
    if(v != '') { period += v; } else { period += '00'; }
    v = document.advsearchform.elements["to_year"].value;
    if(v != '') { period += v; } else { period += '0000'; }
    if(period != '0000000000000000') href += "&d=" + period;
   }	else {
    href += "?search=ik%20zoek%20op%20...";
   }
   href += "&t=adv_search";
     document.location = href;
     return false;
 }
//-->
 </script>