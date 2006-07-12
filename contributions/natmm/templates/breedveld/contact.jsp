<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.opensymphony.com/oscache" prefix="cache" %>

<%@include file="include/inc_language.jsp" %>
<%@include file="include/inc_initdate.jsp" %>


<% String pageId = request.getParameter("page") ;
   if (pageId == null ) pageId = "";
   if (!pageId.equals("")){
%>

<%-- following piece of code depends on page and language --%>
<% String cacheKey = "contact_" + pageId + "_" + language; %>
<% int expireTime =  3600*24*365; if(cacheKey.indexOf("homepage")>-1) { expireTime = 1800; } %><cache:cache key="<%= cacheKey %>" time="<%= expireTime %>" scope="application" ><!-- <%= new java.util.Date() %> -->

<mm:cloud>
							
<table width="600" cellpadding="0" cellspacing="0" border="0">
 <tr>
  <td valign="top" align="center">
  <table width="415" cellspacing="0" border="0">
		<%-- select list from the right language --%>
		<% //String listConstraint = "poslang.pos='1' AND poslang.language='"+ language + "'"; %>
		<mm:list nodes="<%= pageId %>" path="pagina1,readmore,pagina2"
			fields="pagina2.number">
		<form action="page.jsp?page=<mm:field name="pagina2.number" />" method="POST">
                <tr> 
                  <td><img src="media/spacer.gif" width="16" height="8"></td>
				  <td><img src="media/spacer.gif" width="128" height="8"></td>
				  <td><img src="media/spacer.gif" width="255" height="8"></td>
				  <td><img src="media/spacer.gif" width="16" height="8"></td>
                </tr>
                <tr> 
                  <td width="144" valign="top" class="background" colspan="2">
				  	<%= lan(language,"Uw vraag of reactie") %> 
                  </td>
                  <td width="255" valign="top" class="background" colspan="2">
                    <textarea rows="7" cols="40" name="reactie" style="FONT-FAMILY: Courier New, Courier, mono; FONT-SIZE: 11px" wrap></textarea>
				  </td>
                </tr>
		  	    <tr> 
                  <td width="144" valign="top" class="background" colspan="2">
				  	<%= lan(language,"Naam") %>:
				  </td>
                  <td width="255" valign="top" class="background" colspan="2"> 
                    <textarea rows="1" cols="40" name="naam" style="FONT-FAMILY: Courier New, Courier, mono; FONT-SIZE: 11px" wrap></textarea>
                  </td>
                </tr>
                <tr> 
                  <td width="144" valign="top" class="background" colspan="2">
				  	<%= lan(language,"Email adres") %>:
				  </td>
                  <td width="255" valign="top" class="background" colspan="2">
				    <textarea rows="1" cols="40" name="email" style="FONT-FAMILY: Courier New, Courier, mono; FONT-SIZE: 11px" wrap></textarea>
                  </td>
				</tr>
				<tr> 
                  <td width="144" valign="top" class="background" colspan="2">
				  	<img src="media/spacer.gif" width="144" height="8">
				  </td>
                  <td width="255" valign="top" class="background"> 
                    <img src="media/spacer.gif" width="255" height="8">
				  </td>
                  <td width="16">
				  	<input type="image" src="media/double_arrow_right_dg.gif" alt="<%= lan(language,"Verstuur") %>">
				  </td>
                </tr>
		  </form>
		  </mm:list>
		  <tr> 
		  	<td valign="top" class="background" colspan="4" align="center">
		      <img src="media/spacer.gif" width="1" height="30">
			</td>
          </tr>
		  <tr> 
			<td valign="top" class="background" colspan="4" align="center">
				  <a target="_blank" href="http://www.mmatch.nl/index.jsp?page=contact" class="light_boldlink"><%= lan(language,"Vragen over de technische support van deze site?") %></a>
		  	</td>
           </tr>
	</table>
	</td></tr>
</table>

</mm:cloud>

</cache:cache>

<% } %>

