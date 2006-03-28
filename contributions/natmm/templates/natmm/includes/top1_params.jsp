<%
// *** ID can be used as a generic reference to nodes of different nodetypes
// *** if ID is set, use it to set the ID of the related nodetype
if(!ID.equals("-1")){
   %><mm:node number="<%= ID %>" notfound="skipbody">
	   <mm:nodeinfo type="type" write="false" jspvar="nType" vartype="String"><%
		if(nType.equals("rubriek")){ rubriekID=ID; }
		if(nType.equals("pagina")){ paginaID=ID; }
		if(nType.equals("dossier")){ dossierID=ID; }
		if(nType.equals("artikel")){ artikelID=ID; }
		if(nType.equals("evenement")){ evenementID=ID; }
		if(nType.equals("natuurgebieden")){ natuurgebiedID=ID; }
		if(nType.equals("provincies")){ provID=ID; }
		if(nType.equals("images")){ imgID=ID; }
		if(nType.equals("persoon")){ personID=ID; }
      %></mm:nodeinfo>
   </mm:node>
<% 
}

// *** if ID is not set, then set it
if(ID.equals("-1")){
   if(!rubriekID.equals("-1")){ ID = rubriekID;
   } else if(!paginaID.equals("-1")){ ID = paginaID;
   } else if(!dossierID.equals("-1")){ ID = dossierID;
   } else if(!artikelID.equals("-1")){ ID = artikelID;
   } else if(!evenementID.equals("-1")){ ID = evenementID;
   } else if(!natuurgebiedID.equals("-1")){ ID = natuurgebiedID;
   } else if(!provID.equals("-1")){ ID = provID;
   } else if(!imgID.equals("-1")){ ID = imgID;
   } else if(!personID.equals("-1")){ ID = personID;
   }
}

// *** if the ID is still empty, it means that NO ID is provided in the URL
// *** set paginaID, rubriekID and ID from the template
String path =  request.getRequestURI();
if(path.indexOf("/") > -1){
	path = path.substring(path.lastIndexOf("/")+1,path.length());
}
if(path.indexOf(".jsp") > -1){
	path = path.substring(0,path.lastIndexOf(".jsp")+4);
}

if(ID.equals("-1")){ // *** get page from url
	%>
	<mm:listcontainer path="template,gebruikt,pagina,posrel,rubriek">
		<mm:constraint field="template.url" operator="EQUAL" value="<%= path %>" />
		<mm:list fields="rubriek.number,pagina.number"  max="1">
			<mm:field name="rubriek.number" write="false" jspvar="rubriek_number" vartype="String">
			<mm:field name="pagina.number" write="false" jspvar="pagina_number" vartype="String">
			<% 
			paginaID = pagina_number;
			rubriekID = rubriek_number;
			ID = rubriek_number;
			%>
			</mm:field>
			</mm:field>
  		</mm:list>
	</mm:listcontainer><% 

}

// *** try to find paginaID, if not set
if(paginaID.equals("-1")) {

   // *** use rubriekID to set paginaID 
   if(paginaID.equals("-1")&&!rubriekID.equals("-1")) {
      
      RubriekHelper h = new RubriekHelper(cloud);
      paginaID =  h.getFirstPage(rubriekID);
      if(paginaID.equals("-1")) { paginaID = "nm_pagina"; }
      
   }

   // *** use dossierID to set paginaID 
   if(paginaID.equals("-1")&&!dossierID.equals("-1")){  %>
   	<mm:node number="<%= dossierID %>" notfound="skipbody">
   		<mm:relatednodes type="pagina" max="1">
   			<mm:field name="number" jspvar="pagina_number" vartype="String" write="false">
   				<% paginaID = pagina_number; %>
   			</mm:field>
   		</mm:relatednodes>
   	</mm:node><% 
   } 
   
   // *** use natuurgebiedID to set paginaID
   // *** include path because both page natuurgebieden and page routes link to natuurgebieden
   if(paginaID.equals("-1")&&!natuurgebiedID.equals("-1")){  %>
      <mm:listcontainer path="natuurgebieden,pos4rel,provincies,contentrel,pagina,gebruikt,template">
         <mm:constraint field="template.url" operator="EQUAL" value="<%= path %>" />
   	   <mm:list nodes="<%=natuurgebiedID%>" fields="pagina.number" max="1">
      		<mm:field name="pagina.number" jspvar="pagina_number" vartype="String" write="false">
      			<% paginaID = pagina_number; %>
      		</mm:field>
      	</mm:list>
   	</mm:listcontainer>
   	<%
   } 

   // *** use provID to set paginaID
   // *** include path because both page natuurgebieden and page routes link to provincies
   if(paginaID.equals("-1")&&!provID.equals("-1")){  %>
      <mm:listcontainer path="provincies,contentrel,pagina,gebruikt,template">
         <mm:constraint field="template.url" operator="EQUAL" value="<%= path %>" />
   	   <mm:list nodes="<%=provID%>" fields="pagina.number" max="1">
      		<mm:field name="pagina.number" jspvar="pagina_number" vartype="String" write="false">
      			<% paginaID = pagina_number; %>
      		</mm:field>
      	</mm:list>
   	</mm:listcontainer>
   	<%
   } 
   
   // *** use artikelID to set paginaID, direct path
   if(paginaID.equals("-1")&&!artikelID.equals("-1")){  %>
   	<mm:list nodes="<%=artikelID%>" path="artikel,contentrel,pagina" fields="pagina.number" max="1">
   		<mm:field name="pagina.number" jspvar="pagina_number" vartype="String" write="false">
   			<% paginaID = pagina_number; %>
   		</mm:field>
   	</mm:list><%
   } 
   
   // *** use artikelID to set paginaID, path via dossier
   if(paginaID.equals("-1")&&!artikelID.equals("-1")){  %>
   	<mm:list nodes="<%=artikelID%>" path="artikel,posrel,dossier,posrel,pagina" fields="pagina.number" max="1">
   		<mm:field name="pagina.number" jspvar="pagina_number" vartype="String" write="false">
   			<% paginaID = pagina_number; %>
   		</mm:field>
   	</mm:list><%
   } 
   
   // *** use imgID to set paginaID, path via dossier
   if(paginaID.equals("-1")&&!imgID.equals("-1")){  %>
   	<mm:list nodes="<%=imgID%>" path="images,posrel,dossier,posrel,pagina" fields="pagina.number,dossier.number" max="1">
   		<mm:field name="pagina.number" jspvar="pagina_number" vartype="String" write="false">
   			<% paginaID = pagina_number; %>
   		</mm:field>
   		<mm:field name="dossier.number" jspvar="dossier_number" vartype="String" write="false">
   		   <% dossierID = dossier_number; %>
   		</mm:field>
   	</mm:list><%
   }    
}
// *** check wheter paginaID points to a pagina
boolean pageExists = false;
if(!paginaID.equals("-1")) {
   %><mm:node number="<%= paginaID %>" notfound="skipbody" jspvar="thisPage">
      <mm:nodeinfo type="type" write="false" jspvar="nType" vartype="String"><%
   	   if(nType.equals("pagina")){ 
            pageExists = true; 
            paginaID = thisPage.getStringValue("number");
         }
      %></mm:nodeinfo>
   </mm:node><%
}

// *** try to find rubriekID, if not set
if(pageExists&&rubriekID.equals("-1")) { 
   // *** set the rubriek on basis of the paginaID
   %>
   <mm:list nodes="<%= paginaID %>" path="pagina,posrel,rubriek" fields="rubriek.number,rubriek.style,rubriek.naam_fra" max="1">
      <mm:field name="rubriek.number" jspvar="rubriek_number" vartype="String" write="false">
         <% rubriekID = rubriek_number; %>
      </mm:field>
   </mm:list>
   <%
}

// *** check wheter rubriekID points to a rubriek
boolean rubriekExists = false;
if(!rubriekID.equals("-1")) { 
   %><mm:node number="<%= rubriekID %>" notfound="skipbody">
      <mm:nodeinfo type="type" write="false" jspvar="nType" vartype="String"><%
   	   if(nType.equals("rubriek")){ rubriekExists = true; }
      %></mm:nodeinfo>
   </mm:node><%
}

Vector breadcrumbs = new Vector();
String lnRubriekID = "";
String rootID = "";

int iRubriekStyle = PARENT_STYLE;
String styleSheet = "hoofdsite/themas/default.css"; 
int iRubriekLayout = PARENT_LAYOUT;
String lnLogoID = "-1";
String rnImageID = "-1";

if(!rubriekExists||!pageExists) { // *** makes if(rubriekExists&&pageExists) { in template unnecessary ***

   response.sendRedirect("/404/index.html"); 

} else {

   // *** find the root rubriek the present page is related to
   
   breadcrumbs = PaginaHelper.getBreadCrumbs(cloud, paginaID);
   rootID = (String) breadcrumbs.get(breadcrumbs.size()-2);
   
   // for rubrieken of level 3, go one level back for left navigation
   lnRubriekID = rubriekID;
   %><mm:node number="<%= rubriekID %>"
      ><mm:field name="level"
         ><mm:compare value="3"
            ><mm:related path="parent,rubriek" fields="rubriek.number" max="1" searchdir="SOURCE"
               ><mm:field name="rubriek.number" jspvar="rubriek_number" vartype="String" write="false"><%
                  lnRubriekID = rubriek_number;
         	   %></mm:field
         	></mm:related
         ></mm:compare
      ></mm:field
   ></mm:node><%

   // *** determine the rubriek specific settings: layout, style, logo under leftnav, image above search box
   for(int r=0; r<breadcrumbs.size(); r++) {
      %><mm:node number="<%= (String) breadcrumbs.get(r) %>" jspvar="thisRubriek"><%
 
            if(iRubriekLayout==PARENT_LAYOUT) {
               try { iRubriekLayout = thisRubriek.getIntValue("naam_fra"); } catch (Exception e) {}
            }
            if(iRubriekStyle==PARENT_STYLE){
               styleSheet = thisRubriek.getStringValue("style");
            	for(int s = 0; s< style1.length; s++) {
                  if(styleSheet.indexOf(style1[s])>-1) { iRubriekStyle = s; } 
               }
            } 
            if(lnLogoID.equals("-1")) {
               %><mm:related path="contentrel,images" constraints="contentrel.pos='1'"
                  ><mm:field name="images.number" jspvar="images_number" vartype="String" write="false"><%
                     lnLogoID = images_number;
                  %></mm:field
               ></mm:related><%
            }
            if(rnImageID.equals("-1")) {
               %><mm:related path="contentrel,images" constraints="contentrel.pos='2'"
                  ><mm:field name="images.number" jspvar="images_number" vartype="String" write="false"><%
                     rnImageID = images_number;
                  %></mm:field
               ></mm:related><%
            } 
      %></mm:node><%
   }

   if(iRubriekLayout==PARENT_LAYOUT) { iRubriekLayout = DEFAULT_LAYOUT; }
   if(iRubriekStyle==PARENT_STYLE) { iRubriekStyle = DEFAULT_STYLE; }
} 

String shortyRol = "0";

boolean isIE = (request.getHeader("User-Agent").toUpperCase().indexOf("MSIE")>-1);

// *** used in EventNotifier to check whether the webapp runs on the production server ***
if(application.getAttribute("request_url")==null) {
   application.setAttribute("request_url", javax.servlet.http.HttpUtils.getRequestURL(request).toString());
}

%><%@include file="../includes/image_vars.jsp" %>