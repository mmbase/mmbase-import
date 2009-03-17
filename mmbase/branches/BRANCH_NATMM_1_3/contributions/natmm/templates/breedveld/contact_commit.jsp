<%@page import="nl.leocms.applications.*" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@include file="include/inc_language.jsp" %>
<%@include file="include/inc_emailvalidator.jsp"%>

<mm:cloud method="anonymous">

<% String pageId = request.getParameter("page") ;
   if (pageId == null ) pageId = "";
   if (!pageId.equals("")){
%>

<% 
String visitorsReactie = (String) session.getAttribute("reactie");
if(visitorsReactie==null){ visitorsReactie="";}
String visitorsName = (String) session.getAttribute("naam");
if(visitorsName==null){ visitorsName="";}
String visitorsEmail = (String) session.getAttribute("email");
if(visitorsEmail==null){ visitorsEmail="";}
String visitorsPrivacy = (String) session.getAttribute("privacy");
if(visitorsPrivacy==null){ visitorsPrivacy= lan(language,"Nee, ik wil geen nieuws per email ontvangen");}
%>

<% boolean validQuestion =
    !visitorsReactie.equals("")&&
    !visitorsName.equals("")&&
    isValidEmailAddr(visitorsEmail);
%>

<% if(validQuestion) { %>

<%@include file="include/inc_initdate.jsp" %>
<% timeStamp = "now"; %>
<%@ include file="include/inc_date.jsp" %> 
			
<% String visitorsText = "Mijn reactie of vraag: " + visitorsReactie + "\n"
						 + "Ingestuurd op: " + thisDay + " " + monthsStr[thisMonth] + " " + thisYear + "\n" 
						 + "Nieuws per e-mail: " + visitorsPrivacy + "\n";
%>

<mm:transaction id="post_contact" name="my_trans" commitonclose="true">
	<mm:node number="bezoeker" id="all_visitors" />
	<mm:node number="verborgen" id="dummypage" />
	<mm:createnode type="organisatie" id="this_visitor">
		<mm:setfield name="omschrijving"><%= visitorsText %></mm:setfield>
		<mm:setfield name="naam"><%= visitorsName %></mm:setfield>
		<mm:setfield name="email"><%= visitorsEmail %></mm:setfield>
	</mm:createnode>
	<mm:createrelation role="posrel" source="all_visitors" destination="this_visitor" />
	<mm:createrelation role="contentrel" source="dummypage" destination="this_visitor" />
	<mm:remove referid="this_visitor" />
	<mm:remove referid="all_visitors" />
	<mm:remove referid="dummypage" />
</mm:transaction>
<mm:remove referid="post_contact" />

<mm:createnode type="email" id="thisemail">
     <mm:setfield name="subject">Email van de website : <%= visitorsName %></mm:setfield>
     <mm:setfield name="from"><%= visitorsEmail %></mm:setfield>
     <mm:setfield name="to"><%= BreedveldConfig.toEmailAddress %></mm:setfield>
     <mm:setfield name="replyto"><%= visitorsEmail %></mm:setfield>
     <mm:setfield name="body"><%= visitorsText %></mm:setfield>
 </mm:createnode>
<mm:node referid="thisemail">
	<mm:field name="mail(oneshot)" />
</mm:node>
<% } %>
<table width="415" cellpadding="0" cellspacing="0">
  <tr>
  <td>
	<img src="media/spacer.gif" width="415" height="8"></td>
  </tr>
    <% if(validQuestion) { %>
		<tr>
			<td width="415" valign="top" class="background">
				<%= lan(language,"Bedankt voor uw reactie of vraag.") %><br><br>
				<%= lan(language,"U kunt binnenkort een antwoord tegemoet zien.") %>
			</td>
		</tr>
		<tr>
			<td align="right" width="415" valign="top" class="background">
				<a class="dark_boldlink" href="index.jsp" target="_top">
					<IMG height=12 alt="" src="media/arrow_right_dg.gif" width=8 align=absMiddle border=0>
					<%= lan(language,"naar de homepage") %>
				</a>
			</td>
		</tr>
	<% } else { %>
	  	<tr>
			<td width="415" valign="top" class="background">
				<%= lan(language,"Uw reactie of vraag kan niet worden verstuurd.") %><br><br>
				<%= lan(language,"Voor het versturen is het nodig dat:<ul><li>u alle velden invult en</li><li>uw e-mail adres een @ en een . bevat</li></ul>") %>
			</td>
		</tr>
		<tr>
			<td align="right" width="415" valign="top" class="background">
				<a class="dark_boldlink" href="javascript:history.go(-1);">
				<IMG height=12 alt="" src="media/arrow_right_dg.gif" width=8 align=absMiddle border=0>
				<%= lan(language,"terug naar het formulier") %>
				</a>
			</td>
		</tr>
	<% } %>
  <tr>
	<td><img src="media/spacer.gif" width="415" height="8"></td>
  </tr>
</table>

<% } %>


</mm:cloud>
