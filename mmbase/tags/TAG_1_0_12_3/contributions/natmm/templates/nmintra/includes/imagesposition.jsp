<mm:field name="posrel.pos" jspvar="posrel_pos" vartype="String" write="false"
><% if(posrel_pos.equals("1")||posrel_pos.equals("3")||posrel_pos.equals("5")||posrel_pos.equals("7")){ 
	%> style="width:80;margin:0 0 5px 5px;" align="right" <%
} else {
	%> style="width:80;margin:0 5px 5px 0;" align="left" <%
} %></mm:field>