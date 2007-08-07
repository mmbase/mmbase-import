<mm:field name="posrel.pos" jspvar="posrel_pos" vartype="String" write="false"
><% if(posrel_pos.equals("1")||posrel_pos.equals("3")||posrel_pos.equals("5")||posrel_pos.equals("7")){ 
	%> right <%
} else {
	%> left <%
} %></mm:field>