<%
PaginaHelper ph = new PaginaHelper(cloud);
RubriekHelper rubriekHelper = new RubriekHelper(cloud);
String path = ph.getTemplate(request);

HashMap ids = new HashMap();
ids.put("object", ID);
ids.put("rubriek", rubriekID);
ids.put("pagina", paginaID);
ids.put("ads", adID);
ids.put("artikel", artikelID);
ids.put("dossier", dossierID);
ids.put("images", imgID);
ids.put("items", shop_itemID);
ids.put("link", linkID);
ids.put("linklijst", linklijstID);
ids.put("natuurgebieden", natuurgebiedID);
ids.put("persoon", personID);
ids.put("provincies", provID);
ids.put("vacature", vacatureID);
ids.put("items", shop_itemID);
ids.put("isNaardermeer", isNaardermeer);

ids = ph.findIDs(ids, path, "nm_pagina");

ID = (String) ids.get("object");
rubriekID = (String) ids.get("rubriek");
paginaID = (String) ids.get("pagina");
adID = (String) ids.get("ads");
artikelID = (String) ids.get("artikel");
dossierID = (String) ids.get("dossier");
imgID = (String) ids.get("images");
shop_itemID = (String) ids.get("items");
linkID = (String) ids.get("link");
linklijstID = (String) ids.get("linklijst");
natuurgebiedID = (String) ids.get("natuurgebieden");
personID = (String) ids.get("persoon");
provID = (String) ids.get("provincies");
vacatureID = (String) ids.get("vacature");
shop_itemID = (String) ids.get("items");
isNaardermeer = (String) ids.get("isNaardermeer");

Vector breadcrumbs = new Vector();
String lnRubriekID = "";
String subsiteID = "";

int iRubriekStyle = NatMMConfig.PARENT_STYLE;
String styleSheet = "hoofdsite/themas/default.css"; 
int iRubriekLayout = NatMMConfig.PARENT_LAYOUT;
String lnLogoID = "-1";
String rnImageID = "-1";

if(!ph.isOfType(rubriekID,"rubriek")||!ph.isOfType(paginaID, "pagina")) {
   // *** makes if(rubriekExists&&pageExists) { in template unnecessary ***
   response.sendRedirect("/404/index.html"); 

} else {

   // *** find the root rubriek the present page is related to
   breadcrumbs = ph.getBreadCrumbs(cloud, paginaID);
   subsiteID = (String) breadcrumbs.get(breadcrumbs.size()-2);
   
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
 
            if(iRubriekLayout==NatMMConfig.PARENT_LAYOUT) {
               try { iRubriekLayout = thisRubriek.getIntValue("naam_fra"); } catch (Exception e) {}
            }
            if(iRubriekStyle==NatMMConfig.PARENT_STYLE){
               styleSheet = thisRubriek.getStringValue("style");
            	for(int s = 0; s< NatMMConfig.style1.length; s++) {
                  if(styleSheet.indexOf(NatMMConfig.style1[s])>-1) { iRubriekStyle = s; } 
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

   if(iRubriekLayout==NatMMConfig.PARENT_LAYOUT) { iRubriekLayout = NatMMConfig.DEFAULT_LAYOUT; }
   if(iRubriekStyle==NatMMConfig.PARENT_STYLE) { iRubriekStyle = NatMMConfig.DEFAULT_STYLE; }
} 

String shortyRol = "0";

boolean isIE = (request.getHeader("User-Agent").toUpperCase().indexOf("MSIE")>-1);

// *** used in EventNotifier to check whether the webapp runs on the production server ***
if(application.getAttribute("request_url")==null) {
   application.setAttribute("request_url", javax.servlet.http.HttpUtils.getRequestURL(request).toString());
}

if(!(new java.io.File( NatMMConfig.getIncomingDir() )).exists()) {
   %><div style="position:absolute;color:red;font-weight:bold;padding:30px;">
         WARNING: The settings in NatMMConfig are incorrect: <%= NatMMConfig.getIncomingDir() %> is not a directory on this server.
         Please change the settings and place a new natmm.jar
   </div><%
}

%><%@include file="../includes/image_vars.jsp" %>