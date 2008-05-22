<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@page import="java.util.*" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<mm:cloud method="http" jspvar="cloud" rank="administrator">
<%
TreeMap zipCodeTable = (TreeMap) application.getAttribute("zipCodeTable");
TreeMap houseNumberTable = (TreeMap) application.getAttribute("houseNumberTable");
TreeMap houseExtTable = (TreeMap) application.getAttribute("houseExtTable");
TreeMap lastNameTable = (TreeMap) application.getAttribute("lastNameTable");
if(zipCodeTable==null) {
   %>zipCodeTable is not loaded.<%
} else if(houseNumberTable==null) {
   %>houseNumberTable is not loaded.<% 
} else if(houseExtTable==null) {
   %>houseExtTable is not loaded.<%
} else if(lastNameTable==null) {
   %>lastNameTable is not loaded.<%
} else {

   String thisKey = (String) zipCodeTable.firstKey();
   %><%= thisKey %>,<%= zipCodeTable.get(thisKey) %>,<%= houseNumberTable.get(thisKey) %>,<%= houseExtTable.get(thisKey) %>,<%= lastNameTable.get(thisKey) %><br /><%
   thisKey = (String) zipCodeTable.lastKey();
   %><%= thisKey %>,<%= zipCodeTable.get(thisKey) %>,<%= houseNumberTable.get(thisKey) %>,<%= houseExtTable.get(thisKey) %>,<%= lastNameTable.get(thisKey) %><br /><%

} %>
</mm:cloud>
