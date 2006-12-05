<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@page import="java.util.*,nl.leocms.evenementen.Evenement" 
%><%@include file="/taglibs.jsp" 
%><mm:import externid="p" jspvar="parentId" id="parentId">-1</mm:import
><mm:import externid="e" jspvar="eventId" id="eventId">-1</mm:import><%

if(eventId.indexOf("-")==-1) { //*** it has to be - because none-saved nodes have number -1,-2,-3, etc

   String ticketIcon = "../img/ticket.gif";
   String altText = "Aanmelden voor activiteit";

   %><mm:cloud method="http" rank="basic user" jspvar="cloud"
      ><mm:node number="$parentId" jspvar="parentEvent"
      ><mm:node number="$eventId" jspvar="thisEvent"
         ><%@include file="event_status.jsp" 
      %></mm:node
      ></mm:node
   ></mm:cloud
   ><a href="SubscribeInitAction.eb?number=<%= eventId %>"><img src='<%= ticketIcon %>' align='absmiddle' border='0' alt='<%= altText %>'></a><%
} 
%>