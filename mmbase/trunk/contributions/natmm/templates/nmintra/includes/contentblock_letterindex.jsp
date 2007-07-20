<%
String [] letters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
int nextLineBreak = 9;

out.print("<h3>Zoek woord beginnend met:</h3>");
out.print("<p style='font-size: 15px;letter-spacing: 3px;'>");
for (int i=0; i<letters.length; i++) {
	out.print("<a href='begrippenlijst.jsp?k=" + letters[i] + "'>" + letters[i] + "</a> ");
	if (((i+1) % nextLineBreak) == 0) {	
		out.print("<br/>");
	}
}
out.print("</p><br/>");
%>
