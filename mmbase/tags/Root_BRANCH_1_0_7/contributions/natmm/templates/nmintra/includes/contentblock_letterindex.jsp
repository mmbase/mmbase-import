<%
String [] letters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
int nextLineBreak = 9;

out.print("<br/><b style='color:white;'>Zoek woord beginnend met:</b><br/><br/>");
out.print("<p style='font-size: 15px;letter-spacing: 3px;'>");
for (int i=0; i<letters.length; i++) {
	out.print("<a style='color:white;' href='begrippenlijst.jsp?k=" + letters[i] + "&rb="+ iRubriekStyle + "&rbid=" + rubriekId + "&pgid=" + paginaID + "&ssid="+ subsiteID + "'>" + letters[i] + "</a> ");
	if (((i+1) % nextLineBreak) == 0) {	
		out.print("<br/>");
	}
}
out.print("</p><br/>");
%>
