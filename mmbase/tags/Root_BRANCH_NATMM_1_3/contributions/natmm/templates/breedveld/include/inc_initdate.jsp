<%@ page import="java.util.*" %>
<% // global variables needed for conversion from dates in date.jsp
   String timeStamp="";
   Date dd = new Date();
   Calendar cal = Calendar.getInstance(); 
   int thisYear = 0;
   int thisMonth = 0;
   int thisDay = 0;
   String[] monthsStr = new String[] { 
   	lan(language,"januari"),
	lan(language,"februari"),
	lan(language,"maart"),
	lan(language,"april"),
	lan(language,"mei"),
	lan(language,"juni"),
	lan(language,"juli"),
	lan(language,"augustus"),
	lan(language,"september"),
	lan(language,"oktober"),
	lan(language,"november"),
	lan(language,"december")};
%>