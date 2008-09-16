<%

breadcrumbs.add(0,paginaID);
if(!provID.equals("-1")){ breadcrumbs.add(0,provID); }
if(!natuurgebiedID.equals("-1")&&natuurgebiedID.indexOf(",")==-1){ breadcrumbs.add(0,natuurgebiedID); }
if(!dossierID.equals("-1")){ breadcrumbs.add(0,dossierID); }
if(!artikelID.equals("-1")&&natuurgebiedID.equals("-1")){ breadcrumbs.add(0,artikelID); }
if(!imgID.equals("-1")){ breadcrumbs.add(0,imgID); }

String siteStatUrl = "http://nl.sitestat.com/natuurmonumenten/natuurmonumenten/s?";
for(int r=breadcrumbs.size()-2; r>=0; r--) { 
   %><mm:node number="<%= (String) breadcrumbs.get(r) %>" jspvar="thisNode"><%
      String name = thisNode.getStringValue("naam");
      if(name==null || name.equals("")) { 
         name = thisNode.getStringValue("titel");
      }
      if(r<breadcrumbs.size()-2) { siteStatUrl += "."; }
      siteStatUrl += HtmlCleaner.stripText(name);
   %></mm:node><% 
} 

if(request.getParameter("actie")!=null&&!request.getParameter("actie").equals("")) {
  siteStatUrl += "." + request.getParameter("actie");
}
%> 
<!-- Begin Sitestat4 code -->
<script language="JavaScript1.1">
<!--
function sitestat(ns_l){
	ns_l+="&ns__t="+new Date().getTime();ns_pixelUrl=ns_l;
	if(document.images){ns_1=new Image();ns_1.src=ns_l;}else
	document.write("<img src="+ns_l+" width=1 height=1>");
}
sitestat("<%= siteStatUrl %>");
//-->
</script>
<noscript>
<img src="<%= siteStatUrl %>" width=1 height=1>
</noscript>
<!-- End Sitestat4 code -->