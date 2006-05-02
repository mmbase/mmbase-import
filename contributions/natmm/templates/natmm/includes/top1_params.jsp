<%
PaginaHelper ph = new PaginaHelper(cloud);
String path = ph.getTemplate(request);

HashMap ids = new HashMap();
ids.put("object", ID);
ids.put("rubriek", rubriekID);
ids.put("pagina", paginaID);
ids.put("dossier", dossierID);
ids.put("natuurgebieden", natuurgebiedID);
ids.put("provincies", provID);
ids.put("artikel", artikelID);
ids.put("images", imgID);
ids.put("persoon", personID);
ids.put("ads", adID);

ids = ph.findIDs(ids, path, "nm_pagina");

ID = (String) ids.get("object");
rubriekID = (String) ids.get("rubriek");
paginaID = (String) ids.get("pagina");
dossierID = (String) ids.get("dossier");
natuurgebiedID = (String) ids.get("natuurgebieden");
provID = (String) ids.get("provincies");
artikelID = (String) ids.get("artikel");
imgID = (String) ids.get("images");
personID = (String) ids.get("persoon");
adID = (String) ids.get("ads");

Vector breadcrumbs = new Vector();
String lnRubriekID = "";
String rootID = "";

int iRubriekStyle = PARENT_STYLE;
String styleSheet = "hoofdsite/themas/default.css"; 
int iRubriekLayout = PARENT_LAYOUT;
String lnLogoID = "-1";
String rnImageID = "-1";

if(!ph.isOfType(rubriekID,"rubriek")||!ph.isOfType(paginaID, "pagina")) {
   // *** makes if(rubriekExists&&pageExists) { in template unnecessary ***
   response.sendRedirect("/404/index.html"); 

} else {

   // *** find the root rubriek the present page is related to
   breadcrumbs = ph.getBreadCrumbs(cloud, paginaID);
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

if(!(new java.io.File( nl.mmatch.NatMMConfig.incomingDir )).exists()) {
   %><div style="position:absolute;color:red;font-weight:bold;padding:30px;">
         WARNING: The settings in NatMMConfig are incorrect: <%= nl.mmatch.NatMMConfig.incomingDir %> is not a directory on this server.
         Please change the settings and place a new natmm.jar
   </div><%
}

%><%@include file="../includes/image_vars.jsp" %>