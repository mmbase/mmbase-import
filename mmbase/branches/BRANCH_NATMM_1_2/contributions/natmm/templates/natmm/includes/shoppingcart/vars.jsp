<%@page import="java.text.*" %>
<%
NumberFormat nf = NumberFormat.getInstance(Locale.FRENCH);
nf.setMaximumFractionDigits(2);
nf.setMinimumFractionDigits(2);
String shop_itemHref = "";
String extendedHref = "";
%>