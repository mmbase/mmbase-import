<%@include file="/taglibs.jsp" %>
<mm:content type="text/html" escaper="none">
<%@page import="nl.leocms.util.PropertiesUtil,nl.leocms.util.ApplicationHelper,org.mmbase.bridge.*,java.net.*" %>
<% 
// make sure website_user is not used as editor account
String account = null; 
%>
<mm:cloud method="http" rank="anonymous" jspvar="cloud">
  <% account = cloud.getUser().getIdentifier(); %>
</mm:cloud>
<% 
if("website_user".equals(account)) {
  request.getSession().invalidate(); 
  %><mm:redirect page="/editors/topmenu.jsp" /><%
} %>
<mm:cloud jspvar="cloud" rank="basic user">
<%
account = cloud.getUser().getIdentifier();
String unused_items = cloud.getNodeManager("users").getList("users.account = '" + account + "'",null,null).getNode(0).getStringValue("unused_items");

int iTotalNotUsed = 0;
if (unused_items!=null&&(!unused_items.equals(""))){
  String contentElementConstraint = " contentelement.number IN (0, " + unused_items + ") ";
  NodeList nlObjects = cloud.getList("",
                               "contentelement",
                               "contentelement.number",
                               contentElementConstraint,
                               null,null,null,true);
  iTotalNotUsed = nlObjects.size();
  application.setAttribute("unused_items",unused_items);
}

String unusedItemsLink = "";
if (iTotalNotUsed>0) {
  unusedItemsLink = "<td><a href='beheerbibliotheek/view_unused_items.jsp' target='bottompane' title='bekijk niet gebruikte contentelementen uit de door u beheerde rubrieken'>"
    + "<img src='img/delete.gif' style='vertical-align:bottom;'>(" + iTotalNotUsed + ")</a><td>";
}
%>
<cache:cache groups="<%= account %>" key="<%= account + "_topmenu_" + iTotalNotUsed %>" time="<%= 3600*24*7 %>" scope="application">
<html>
<head>
    <link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
    <link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/wizard.css" type="text/css" rel="stylesheet"/>
    <title>Menu beheeromgeving</title>
    <style>
        td.fieldname {
            padding-left: 5px;
            padding-right: 5px;
            font-size: 14px;
        }
    </style>
</head>
<body style="background-color:#E4F0F7;">
<mm:import externid="action"/>
<!-- We are going to set the referrer explicitely, because we don't wont to depend on the 'Referer' header (which is not mandatory) -->
<mm:import externid="language">nl</mm:import>
<mm:import id="referrer"><%=new java.io.File(request.getServletPath())%>?language=<mm:write  referid="language" /></mm:import>
<mm:import id="jsps"><%= editwizard_location %>/jsp/</mm:import>
<mm:import id="debug">false</mm:import>
<%
RubriekHelper rh = new RubriekHelper(cloud);
PaginaHelper ph = new PaginaHelper(cloud);

String contentModusProperty = PropertiesUtil.getProperty("content.modus");
if ((contentModusProperty != null) && (contentModusProperty.equals("on"))) {
  session.setAttribute("contentmodus", contentModusProperty);
}
else {
  session.removeAttribute("contentmodus.contentnodenumber");
  session.setAttribute("contentmodus", "off");
}
boolean isAdmin = cloud.getUser().getRank().equals("administrator");
boolean isChiefEditor = cloud.getUser().getRank().equals("chiefeditor");
String rubriekID = "";
boolean hasEditwizards = false;

TreeSet tsRubrieks = new TreeSet();
boolean isEventUser = false;
String sNatuurinNumber = "";
%>
<mm:node number="natuurin_rubriek" notfound="skipbody" jspvar="natmmEventsRubriek">
 <% sNatuurinNumber = natmmEventsRubriek.getStringValue("number"); %>
</mm:node>
<mm:listnodes type="users" constraints="<%= "[account]='" + cloud.getUser().getIdentifier() + "'" %>" max="1" id="thisuser">
   <mm:related path="rolerel,rubriek">
      <mm:field name="rubriek.number" jspvar="rubriek_number" vartype="String" write="false">
			<mm:field name="rubriek.level" jspvar="rubriek_level" vartype="String" write="false">
				<%
				if(rubriek_number.equals(sNatuurinNumber)){ // special purpose rubriek, do not use for general functionality
					isEventUser = true;
				} else {
					if(rubriek_level.equals("0")) { // put children of the root rubriek in list 
						%>
						<mm:list nodes="<%= rubriek_number %>" path="rubriek1,parent,rubriek2">
							<mm:field name="rubriek2.number" jspvar="rubriek2_number" vartype="String" write="false">
								<% tsRubrieks.add(rubriek2_number); %>
							</mm:field>
						</mm:list>
						<% 
					} else if (rubriek_level.equals("1")) { // this is a subsite rubriek
						tsRubrieks.add(rubriek_number);
					} else { // add the subsite rubriek of this rubriek
						tsRubrieks.add(PaginaHelper.getSubsiteRubriek(cloud,rubriek_number));
					}
				} 
				%>
			</mm:field>	
      </mm:field>
   </mm:related>
   <mm:related path="gebruikt,editwizards" max="1">
      <% hasEditwizards = true; %>
   </mm:related>
</mm:listnodes>
<% String subDir = ""; %>
<mm:list nodes="root" path="rubriek1,parent,rubriek2" orderby="parent.pos" directions="UP" max="1">
   <mm:field name="rubriek2.url_live" jspvar="url_live" vartype="String" write="false">
      <mm:isnotempty>
        <% subDir= "/" + url_live; %>
      </mm:isnotempty>
   </mm:field>
</mm:list>
<div style="position:absolute;left:5px;top:5px;z-index:100;overflow:auto;"><small>
<%
	for (Iterator it = tsRubrieks.iterator(); it.hasNext(); ) { 
		rubriekID = (String) it.next();
		String paginaId = rh.getFirstPage(rubriekID);
		String contextPath = request.getContextPath(); // todo add functionality 
		String sLink = ph.createPaginaUrl(paginaId,contextPath);
		String sRubriekName = cloud.getNode(rubriekID).getStringValue("naam");
		%>
		<li>
    <a href="<%= sLink + (sLink.indexOf("?")==-1 ? "?" : "&") + "preview=off" %>" target="_blank" class="menu" title="bekijk <%= sRubriekName %>">
        <%= sRubriekName %>
    </a><br/>
		<%
	}
%>
</small></div>
<h1 style="text-align:center;width:100%;">Beheeromgeving <mm:node number="root" notfound="skipbody"><mm:field name="naam" /></mm:node></h1>
<table class="formcontent" style="margin-left:180px;background-color:#E4F0F7;width:auto;">
	<tr>
	<%
	if (isEventUser) { 
		%>
		<td class="fieldname"><a href="<mm:url page="<%= subDir + "/events.jsp" %>" />" target="_blank" class="menu" title="bekijk de agenda">Agenda</a></td>
		<%
		if (isAdmin) {
			%>
			<td class="fieldname"><a href="evenementen/frames.jsp" target="bottompane" class="menu" title="beheer activiteiten en boek aanmeldingen">Activiteiten</a></td>
			<%
		} else {
			%>
			<td class="fieldname"><a href="evenementen/evenementen.jsp" target="bottompane" class="menu" title="beheer activiteiten en boek aanmeldingen">Activiteiten</a></td>						
			<%
		} 
	}
	if (!tsRubrieks.isEmpty()) {
		%>	 
		<td class="fieldname"><a href="beheerbibliotheek/index.jsp?refreshFrame=bottompane" target="bottompane" class="menu" title="beheer de contentelementen via de bibliotheek">Bibliotheek</a></td><%= unusedItemsLink %>
		<td class="fieldname"><a href="paginamanagement/frames.jsp" target="bottompane" class="menu" title="beheer rubrieken en paginas">Pagina-editor</a></td>
		<%
	} else if(hasEditwizards) {
		%>
		<td class="fieldname"><a href="paginamanagement/frames.jsp" target="bottompane" class="menu" title="beheer rubrieken en paginas">Pagina-editor</a></td>
		<% 
	}
	%>
	<td class="fieldname"><a href="usermanagement/changepassword.jsp" target="bottompane" class="menu" title="wijzig uw wachtwoord">Wijzig wachtwoord</a></td>	
	<td class="fieldname"><a href="logout.jsp" target="_top" class="menu" title="log uit als gebruiker">Uitloggen</a></td>
	<%	
	if (tsRubrieks.isEmpty()&&!isEventUser) { 
		%>
   	<td class="menu" style="color:red;">
      Er is geen rubriek voor u geselecteerd.
      Neem contact op met de webmasters om u een rol op een van de rubrieken te geven.
      Of log in met een andere gebruikersnaam en password.</td>
		<% 
	} %>
	</tr>
</table>
<div style="position:absolute;right:5px;top:5px;z-index:100">
  <small>
    <li><a class="menu" target="bottompane" href="<mm:url page="<%= subDir + "/doc/index.jsp" %>" />" title="klik hier om de gebruikershandleidingen te bekijken of te downloaden">gebruikershandleiding</a><br/>
    <% String webmasterMail = ""; %>
    <mm:listnodescontainer type="users"
       ><mm:constraint field="rank" operator="=" value="administrator" 
       /><mm:listnodes searchdirs="destination"
          ><mm:first inverse="true"><% webmasterMail += ";"; %></mm:first
          ><mm:field name="emailadres" jspvar="dummy" vartype="String" write="false"
             ><% webmasterMail += dummy; 
          %></mm:field
       ></mm:listnodes
    ></mm:listnodescontainer>
    <li><a class="menu" href="mailto:<%= webmasterMail %>" title="<%= webmasterMail %>">mail&nbsp;de&nbsp;webmasters</a><br/>
    <li><a class="menu" href="usermanagement/changepassword.jsp" target="bottompane" title="wijzig uw wachtwoord"><mm:node number="$thisuser">gebruiker:&nbsp;<mm:field name="voornaam"/>&nbsp;<mm:field name="tussenvoegsel"><mm:isnotempty><mm:write />&nbsp;</mm:isnotempty></mm:field><mm:field name="achternaam"
       /></mm:node></a>
  </small>
  <iframe src="refresh.jsp?account=<%=account %>" style="width:7px;height:7px;" frameborder="0" scrolling="no"></iframe>
</div>
</body>
</html>
</cache:cache>
</mm:cloud>
</mm:content>