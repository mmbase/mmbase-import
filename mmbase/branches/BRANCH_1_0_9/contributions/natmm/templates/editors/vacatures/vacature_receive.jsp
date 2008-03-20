<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page import="java.util.Enumeration,nl.leocms.applications.NatMMConfig"%>
<%@include file="/taglibs.jsp" %>
<html>
<head>
<title></title>
<link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
<link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/wizard.css" type="text/css" rel="stylesheet"/>
</head>
<body style="overflow:auto;">
<% Enumeration eParamsNames = request.getParameterNames(); 
	//String sBronValue = request.getParameter("bron");
	boolean bNewNode = true;
	if(eParamsNames != null){ %>
		<mm:cloud logon="admin" pwd="<%= (String) com.finalist.mmbase.util.CloudFactory.getAdminUserCredentials().get("password") %>" method="pagelogon" jspvar="cloud">
			<% String vacatureConstraint = "vacature.bron = '" + request.getParameter("bron") + "'"; %>
			<mm:listnodes type="vacature" constraints="<%= vacatureConstraint %>">
			<%	bNewNode = false;
				while (eParamsNames.hasMoreElements()) { 
					String sFieldName = (String) eParamsNames.nextElement(); 
               if(!sFieldName.equals("ref")) {   %>
					   <mm:setfield name="<%= sFieldName %>"><%= request.getParameter(sFieldName)%></mm:setfield>
			      <%	
               }
            } 
         %>
			</mm:listnodes>
			<% if (bNewNode) {
					eParamsNames = request.getParameterNames(); %>
					<mm:node number="vacatures" id="vacature_page" />
					<mm:createnode type="vacature" id="this_vacature">
					<%	while (eParamsNames.hasMoreElements()) { 
							String sFieldName = (String) eParamsNames.nextElement();
                     if(!sFieldName.equals("ref")) { %>
					      <mm:setfield name="<%= sFieldName %>"><%= request.getParameter(sFieldName)%></mm:setfield>
			            <%	
                  }
            	} %>
					</mm:createnode>
					<mm:createrelation source="vacature_page" destination="this_vacature" role="contentrel" />
			<% }%>
		</mm:cloud>
      <h3>Uw vacature is op www.natuurmonumenten.nl geplaatst</h3>
      Vergeet niet:
		<ul>
			<li>eventueele bijlagen handmatig toe te voegen</li>
			<li>de vacature pagina te publiceren (klik op het gekleurde blokje rechtsbovenin de pagina hieronder)</li>
		</ul>
      <a href="<%= request.getParameter("ref") %>">Terug naar het overzicht vacatures</a>
      <br/><br/>
      <iframe src="<%= NatMMConfig.getLiveUrl() + "vacatures.jsp?p=vacatures&preview=on" %>" style="padding:0px;width:780px;height:400px;"></iframe>
<%	} else { %>
		<h3 color="red"><b>Uw vacature is niet gepubliceerd op www.natuurmonumenten.nl!</h3>
      <br/>
      <br/>
      <a href="<%= request.getParameter("ref") %>">Terug naar het overzicht vacatures</a>
<%	}%>
</body>
</html>
