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
               <td width="399" valign="top" class="background" colspan=4">
  					<%= lan(language,"Uw vraag of reactie") %><br><br>
  					<%= lan(language,"Met deze pagina kunt u uw reactie of vraag versturen. U ontvangt dan zo snel mogelijk antwoord.") %><br><br>
					<%= lan(language,"De door u verstrekte gegevens zullen vertrouwelijk worden behandeld en alleen worden gebruikt voor de toezending van de door u gevraagde informatie. De gegevens zullen in geen geval aan derden worden verstrekt.") %><br><br>
				  </td>
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
              <td width="16">
					<input type="checkbox" name="privacy" value="<%= lan(language,"Ja, ik wens per e-mail van nieuws op de hoogte gehouden te worden.") %>">		
				  </td>
				  <td width="399" valign="center" align="left" class="background" colspan="3"> 
				  		<%= lan(language,"Ja, ik wens per e-mail van nieuws op de hoogte gehouden te worden.") %>
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

