<%-- Sitestat4 code 
--%><%! public String stripText(String text) {
	// cleans text from &, spaces and special characters
	text = text.toLowerCase();
	int charPos =  text.indexOf("&"); 
	while(charPos>-1){
		text = text.substring(0,charPos) + "en" + text.substring(charPos+1);
		charPos =  text.indexOf("&"); 
	}
	charPos =  text.indexOf(" "); 
	while(charPos>-1){
		text = text.substring(0,charPos) + "_" + text.substring(charPos+1);
		charPos =  text.indexOf(" "); 
	}
	for(charPos = 0; charPos < text.length(); charPos++){
		char c = text.charAt(charPos);
		if	(		!(('a'<=c)&&(c<='z'))
			&&	!(('0'<=c)&&(c<='9'))
			&&	!(c=='_') 
			) { 
				text = text.substring(0,charPos) + "*" + text.substring(charPos+1);
			}
	}
	return text;
}

%><%

String siteStatUrl = "http://nl.sitestat.com/natuurmonumenten/natuurmonumenten/s?natuurherstel";

%><mm:node number="<%= rubriekId %>" notfound="skipbody"
	><mm:field name="naam" jspvar="name" vartype="String" write="false"
	><% siteStatUrl += "." + stripText(name); 
	%></mm:field
></mm:node
><mm:node number="<%= paginaID %>" notfound="skipbody"
		><mm:field name="titel" jspvar="titel" vartype="String" write="false"
		><% siteStatUrl += "." + stripText(titel);  
		%></mm:field
	></mm:node>
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