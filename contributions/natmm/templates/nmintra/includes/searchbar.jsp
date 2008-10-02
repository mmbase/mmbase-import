<script type="text/javascript">
   function startSearch() {
       var href = document.searchform.action;
       var search = escape(document.searchform.elements["search"].value);
       href += "?search=" + search;
       var adv = document.searchform.elements["adv"].value;
   	  if(adv != '') {
           href += "&t=" + adv;
       }
   	  document.location =  href;
       return false; 
   }
   function startPhone() {
       var href = document.phoneform.action;
       var name = escape(document.phoneform.elements["name"].value);
       if(name != '') {
           href += "?name=" + name;
       }
       document.location =  href;
       return false; 
   }
</script>
<% // *************************************** logo ******************************* %>
<mm:node number="<%= subsiteID %>">
<tr>
  <td rowspan="3">
    <% if(!isPreview) { %><a href="http://<mm:field name="url" />" target="_blank"><% } %>
      <img src="media/styles/<%= NMIntraConfig.style1[iRubriekStyle] %>_logo.gif" title="<mm:field name="url" />" 
        style="position:absolute;z-index:2;left:2px;top:1px;" border="0">
    <% if(!isPreview) { %></a><% } %>
  </td>
  <td style="width:70%;"><img src="media/spacer.gif" width="1" height="12"></td>
  <% // *************************************** name of intranet ******************************* %>
  <td class="header" style="padding-right:10px;padding-top:5px;text-align:right;width:251px;">
    <nobr>
      <mm:field name="naam_de" />
      <a href="/index.jsp?r=<%= subsiteID %>" class="hover">
         <span class="red"><mm:field name="naam" /></span>
      </a>
    </nobr>
  </td>
</tr>
</mm:node>
<tr>
   <td style="width:70%;">
   <% // *************************************** zoek box ******************************* %>
   <table border="0" cellspacing="0" cellpadding="0" style="width:100%;">
   	<form name="searchform" action="<%= ph.createPaginaUrl("search",request.getContextPath()) %>" onSubmit="return startSearch();">
   	<tr>
   	<td><input type="text" name="search" value="<%= (searchId.equals("")||actionId.equals("adv_search") ? defaultSearchText : searchId )
      %>" onClick="if(this.value=='<%= defaultSearchText %>') { this.value=''; }" style="text-align:left;width:110px;" /></td>
   	<td style="padding-left:3px;padding-top:1px;">
   	  <input type="submit" name="Submit" value="Zoek" style="text-align:center;font-weight:bold;"></td>
   	<td style="padding-left:3px;padding-top:1px;">
   	  <input type="hidden" name="adv">
   	  <input type="submit" name="AdvSubmit" value="Uitgebreid Zoeken" style="text-align:center;font-weight:bold;width:110px;" onClick="document.searchform.adv.value='adv_search';"></td>
   	<td style="width:80%;text-align:right;">
      <%--
      String owners = ph.getOwners(cloud,paginaID,breadcrumbs);
      String ownersEmail = "";
      if(!owners.equals("")) {
        %>
        <mm:list nodes="<%= owners %>" path="users" fields="users.emailadres">
          <mm:field name="users.emailadres" jspvar="users_email" vartype="String" write="false">
            <%
            ownersEmail += (ownersEmail.equals("") ? "" : ";") + users_email;
            %>
          </mm:field>
        </mm:list>
        <%
        if(!ownersEmail.equals("")) { 
          %>
          <a href="mailto:<%= ownersEmail %>?subject=Betreft <% 
                %><mm:node number="<%= subsiteID %>"><mm:field name="naam" /></mm:node
                > - <mm:node number="<%= paginaID %>"><mm:field name="titel" /></mm:node
                >" title="Email de beheerder van deze pagina">
            <img src="media/email.gif" alt="Email de beheerder van deze pagina" border="0">
          </a>
          <%
        }
      }
      --%>
      <a href="<%= ph.createPaginaUrl("feedback",request.getContextPath()) %>">
   				<img src="media/email.gif" alt="Email nieuws of vragen" border="0" style="margin-top:5px;">
   		</a>
   	</td>
   	</tr>
   	</form>
   </table>
   </td>
   <td style="padding-right:10px;width:251px;">
   <% // *************************************** phone box ******************************* %>
   <form name="phoneform" action="<%= ph.createPaginaUrl("wieiswie",request.getContextPath()) %>" onSubmit="return startPhone();">
   <table border=0 cellspacing="0" cellpadding="0" align="right">
    	  <tr>
   	  <td><img src="media/telefoon.gif" alt="Zoeken in het smoelenboek" onclick="startPhone();"></td>
   	  <td><input type="text" name="name" value="<% if(nameId.equals("")){ %><%= nameEntry %><% } else { %><%= nameId %><% } 
   			%>" style="text-align:left;width:166px;" onClick="if(this.value=='<%= nameEntry %>') { this.value=''; }" /></td>
   	  <td><img src="media/spacer.gif" width="7" height="1"></td>
   	  <td><img src="media/spacer.gif" width="1" height="1"><br>
   			<input type="submit" name="phone" value="Zoek"  style="text-align:center;font-weight:bold;"></td>
   	  </tr>
   </table>
   </form>
   </td>
</tr>
<tr>
   <td style="width:70%;"><img src="media/spacer.gif" width="1" height="12"></td>
   <td style="width:251px;"><img src="media/spacer.gif" width="251" height="12"></td>
</tr>
