<%
int windowWidth = 600;
try{
	windowWidth = Integer.parseInt((String) session.getAttribute("width")); 
}catch(java.lang.NumberFormatException exp){
	windowWidth	= 600;
}
%>
<!-- windowWidth in inc_windowwidth <%= windowWidth %> -->