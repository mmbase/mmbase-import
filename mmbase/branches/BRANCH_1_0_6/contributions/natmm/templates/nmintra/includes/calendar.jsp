<%@page import="java.util.*" %><%
Calendar cal = Calendar.getInstance();
String [] days = { "ZONDAG","MAANDAG","DINSDAG","WOENSDAG","DONDERDAG","VRIJDAG","ZATERDAG" }; 
String [] days_lcase = { "Zondag","Maandag","Dinsdag","Woensdag","Donderdag","Vrijdag","Zaterdag" }; 
String [] days_abbr = { "Zo.","Ma.","Di.","Wo.","Do.","Vrij.","Za." } ; 
String [] months = { "JANUARI","FEBRUARI","MAART","APRIL","MEI","JUNI","JULI",
					"AUGUSTUS","SEPTEMBER","OKTOBER","NOVEMBER","DECEMBER" };
String [] months_lcase = { "januari","februari","maart","april","mei","juni","juli",
					"augustus","september","oktober","november","december" };
Date ddd = new Date();
int quarterOfAnHour = 60*15;
long nowSec = (ddd.getTime()/1000);
nowSec = (nowSec/(60*15))*(60*15);     // help the query cache by rounding to quarter of an hour
String timestr = "";
%>