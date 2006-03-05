<%
String styleSheet = request.getParameter("rs");
if(styleSheet==null) { styleSheet = "hoofdsite/themas/default.css"; }
int iRubriekStyle = DEFAULT_STYLE;
for(int s = 0; s< style1.length; s++) {
   if(styleSheet.indexOf(style1[s])>-1) { iRubriekStyle = s; } 
}
%>