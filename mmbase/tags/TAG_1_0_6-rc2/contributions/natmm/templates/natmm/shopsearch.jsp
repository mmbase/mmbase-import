<%@include file="includes/top0.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="includes/top1_params.jsp" %>
<%@include file="includes/shoppingcart/update.jsp" %>
<%@include file="includes/top2_cacheparams.jsp" %>
<!-- cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application" -->
<mm:locale language="nl">
<%@include file="includes/top3_nav.jsp" %>
<%@include file="includes/top4_head.jsp" %>
<%@include file="includes/top5_breadcrumbs_and_pano.jsp" %>
<%@include file="includes/shop/header.jsp" %>
<%@include file="includes/shop/getsearchresults.jsp" %>
  <td colspan="5" style="text-align:right;"> 
  <table width="90%" cellspacing="0" cellpadding="0"><tr>
  <td><img src="media/trans.gif" width="1" height="11" border="0" alt=""><br/>
  <table width="100%" cellspacing="0" cellpadding="0">
    <tr><td colspan="2" style="padding:4px;">
    <%
    if(!searchId.equals("")||!keyId.equals("")||!poolId.equals("")) {
      %>U heeft gezocht <% if(!searchId.equals("")) { %>op <strong><%= searchId %></strong><% }
        if(!poolId.equals("")) { 
          if(keyId.equals("")) { %> en <% } else { %>, <% }
          %>in de categorie <strong><mm:node number="<%= poolId %>"
            ><mm:field name="titel" jspvar="pools_name" vartype="String" write="false"><%=
              pools_name.toLowerCase()
            %></mm:field
          ></mm:node></strong><%
        } 
        if(!keyId.equals("")) { 
          %> en op het trefwoord <strong><mm:node number="<%= keyId %>"
          ><mm:field name="word" jspvar="keywords_word" vartype="String" write="false"><%=
              keywords_word.toLowerCase()
            %></mm:field
          ></mm:node></strong><% 
        } %>.<%
    } else {
      %>Vul in de onderstaande tabel uw zoekopdracht in en klik op de "ZOEK" knop
      om een artikel te zoeken in de Natuurmonumenten winkel.<br/><br/><%
    } %>
    </td></tr>
    <tr><td colspan="2"><img src="media/trans.gif" height="7" width="1" border="0" alt=""></td></tr>
    <tr><form name="searchform" method="post" action="javascript:searchOn();">
      <td class="maincolor" style="width:40%;vertical-align:middle;padding-left:4px;background-color:#5D5D5D;">Zoekterm</td>
      <td class="maincolor" style="width:60%;vertical-align:middle;text-align:right;padding-right:1px;background-color:#5D5D5D;">
        <input type="text" name="search" style="width:100%;height:15px;border:0px;font-size:0.9em" value="<%= searchId %>"></td>
    </tr>
    <tr><td colspan="2"><img src="media/trans.gif" height="7" width="1" border="0" alt=""></td></tr>
    <tr>
      <td class="maincolor" style="width:40%;ertical-align:middle;padding-left:4px;background-color:#5D5D5D;">Categorie</td>
      <td class="maincolor" style="width:60%;vertical-align:middle;text-align:right;padding-right:1px;background-color:#5D5D5D;">
      <select name="pool" style="width:100%;height:15px;font-size:0.9em">
        <option value="">
        <mm:list path="pagina" orderby="pagina.titel" directions="UP" 
          ><mm:field name="pagina.number" jspvar="pagina_number" vartype="String" write="false"
            ><mm:list nodes="<%= pagina_number %>" path="pagina,posrel,items"
              ><mm:first
                ><mm:field name="pagina.titel" jspvar="pagina_titel" vartype="String" write="false"
                  ><option value="<mm:field name="pagina.number" />" <% if(poolId.equals(pagina_number)) { %>SELECTED<% } 
                  %>><%= pagina_titel.toLowerCase() %>
                </mm:field
              ></mm:first
            ></mm:list
          ></mm:field
        ></mm:list		
        ></select></td>
    </tr>
    <tr><td colspan="2"><img src="media/trans.gif" height="7" width="1" border="0" alt=""></td></tr>
    <tr>
      <td class="maincolor" style="width:40%;vertical-align:middle;padding-left:4px;background-color:#5D5D5D;">Trefwoord</td>
      <td class="maincolor" style="width:60%;vertical-align:middle;text-align:right;padding-right:1px;background-color:#5D5D5D;">
        <select name="key" style="width:100%;height:15px;font-size:0.9em">
          <option value="">
          <mm:list path="keywords" orderby="keywords.word" directions="UP"
          ><mm:field name="keywords.number" jspvar="keywords_number" vartype="String" write="false"
          ><mm:field name="keywords.word" jspvar="keywords_word" vartype="String" write="false"
            ><option value="<mm:field name="keywords.number" />" <% if(keyId.equals(keywords_number)) { %>SELECTED<% } %>><%= keywords_word.toLowerCase() %>
          </mm:field
          ></mm:field
          ></mm:list
          ></select></td>
    </tr>
    <tr><td colspan="2"><img src="media/trans.gif" height="7" width="1" border="0" alt=""></td></tr>
    <tr><td style="width:40%;"><img src="media/trans.gif" height="1" width="1" border="0" alt=""></td>
      <td style="width:60%;">
      <table width="100%" cellspacing="0" cellpadding="0" align="right">
        <tr>
        <td class="maincolor" style="vertical-align:middle;padding-left:4px;padding-right:2px;" width="100%">
          <nowrap><a href="javascript:searchOn();document.searchform.target='';document.searchform.submit();" class="klikpad"><b>OPNIEUW ZOEKEN</b></a></td>
        <td class="maincolor" style="padding:2px;" width="100%">
          <a href="javascript:searchOn();document.searchform.target='';document.searchform.submit();"><img src="media/shop/pijl_wit_op_oranje.gif" border="0" alt=""></a></td>
        </tr></form>
      </table>
      <script language="JavaScript" type="text/javascript">
      <%= "<!--" %>
      function searchOn() {
        var href = "<mm:url page="<%= ph.createPaginaUrl("zoek",request.getContextPath()) %>" />";
        var search = document.searchform.elements["search"].value;
        var hasQuote = search.indexOf('\'');
        if (hasQuote>-1){
          search = search.substring(0,hasQuote);
          alert("Error: Uw zoekopdracht mag geen \' bevatten.");
        }
        href += "?s=" +escape(search);
        href += "&k=" + document.searchform.elements["key"].value;
        href += "&c=" + document.searchform.elements["pool"].value;
        document.location = href;
      }
      <%= "//-->" %>
      </script>
    </td></tr>
  <tr><td colspan="2"><img src="media/trans.gif" height="7" width="1" border="0" alt=""></td></tr>
  <tr><td colspan="2">
  <img src="media/trans.gif" width="1" height="11" border="0" alt=""><br/>
  <%	
    if(!searchResults.equals("")) { 
      %>De volgende artikelen zijn gevonden<br/><br/><%
    } else {
      %><strong>Er zijn geen artikelen gevonden, die voldoen aan uw zoekopdracht.</strong><br/><br/><%
    } 
  %></td></tr>
  </table>
  </td>
  <td width="8"><img src="media/trans.gif" height="1" width="8" border="0" alt=""></td>
  <td width="180" valign="top"><img src="media/trans.gif" height="1" width="180" border="0" alt=""></td>
  </tr></table>
  <%	
  if(!searchResults.equals("")) {
    %><mm:list nodes="<%= searchResults %>" path="items" orderby="items.titel" directions="UP"
      ><mm:first><table width="100%" cellspacing="0" cellpadding="0">
      <tr><td class="maincolor" colspan="4"><img src="media/trans.gif" border="0" alt="" width="1" height="1"></td></tr>
      </mm:first
      ><mm:field name="items.number" jspvar="shop_itemID" vartype="String" write="false"
        ><jsp:include page="includes/shop/relatedfoundproduct.jsp">
          <jsp:param name="u" value="<%= shop_itemID %>" />
        </jsp:include
        ></mm:field
      ><mm:last></table><br/><br/></mm:last
    ></mm:list><%
  } 
  %></td>
<%@include file="includes/shop/footer.jsp" %>
<%@include file="includes/footer.jsp" %>
</mm:locale>
<!-- /cache:cache-->
</mm:cloud>