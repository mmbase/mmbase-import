<%@page import="java.util.*" %><%
Calendar cal = Calendar.getInstance();
String [] days = { "ZONDAG","MAANDAG","DINSDAG","WOENSDAG","DONDERDAG","VRIJDAG","ZATERDAG" }; 
String [] days_lcase = { "Zondag","Maandag","Dinsdag","Woensdag","Donderdag","Vrijdag","Zaterdag" }; 
String [] days_abbr = { "Zo.","Ma.","Di.","Wo.","Do.","Vrij.","Za." } ; 
String [] months = { "JANUARI","FEBRUARI","MAART","APRIL","MEI","JUNI","JULI",
					"AUGUSTUS","SEPTEMBER","OKTOBER","NOVEMBER","DECEMBER" };
String [] months_lcase = { "Januari","Februari","Maart","April","Mei","Juni","Juli",
					"Augustus","September","Oktober","November","December" };

Date ddd = new Date();
long nowSec = (ddd.getTime()/1000);
nowSec = (nowSec/(60*15))*(60*15);     // help the query cache by rounding to quarter of an hour
%>