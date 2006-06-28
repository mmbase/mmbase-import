<%
String styleSheet = request.getParameter("rs");
if(styleSheet==null) { styleSheet = "hoofdsite/themas/default.css"; }
int iRubriekStyle = NatMMConfig.DEFAULT_STYLE;
for(int s = 0; s< NatMMConfig.style1.length; s++) {
   if(styleSheet.indexOf(NatMMConfig.style1[s])>-1) { iRubriekStyle = s; } 
}
%>