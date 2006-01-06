<%@ page import="java.util.*"%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page language="java" contentType="text/html; charset=utf-8" session="true"%>
<mm:cloud >
<%@include file="/includes/getids.jsp" %>
<%@include file="/includes/header.jsp"%>

<td class="white" colspan="2" valign="top">
<% Map orgs = (Map) session.getAttribute("organisations");
   if(orgs == null || orgs.isEmpty()) {
%> <!-- no orgs found --> <%
 %>

<mm:node referid="page">
<mm:related path="posrel,articles" orderby="posrel.pos" directions="UP" searchdir="destination">
<mm:node element="articles">
<%@include file="/includes/article.jsp"%>
</mm:node>
</mm:related>
</mm:node>

<% } else {
 %> <h1> Add a new MMBase site </h1> <%
      String message = request.getParameter("message");
      if(message != null && message.length() > 0) {
         %> <h3> <%= message %> </h3> <%
      }
      // there are organisations, present 'Add Website' page
%> <form method="post" action="<mm:url page="/dopartner.jsp"/>"> <table> <%
      // if there is more than one organisation, present selectbox,
      // else use hiddenfield
      Iterator it = orgs.entrySet().iterator();
      if(orgs.size() > 1) {
%> <tr> <td> Organisation </td> <td> <select name="organisation"> <%
         while(it.hasNext()) {
            Map.Entry me = (Map.Entry) it.next();
%> <option value="<%= me.getKey() %>"> <%= me.getValue() %> </option> <%
         }
%> </select> </td> </tr> <%
      } else {
%> <input type="hidden" name="organisation" value="<%= ((Map.Entry) it.next()).getKey() %>"> <%
      }
%>
   <tr> <td> Title </td> <td>
   <input class="mmbase" type="text" name="title" size="28">
   </td> </tr>
   <tr> <td> Subtitle </td> <td>
   <input class="mmbase" type="text" name="subtitle" size="28">
   </td> </tr>
   <tr> <td> Intro </td> <td>
   <textarea name="intro"></textarea>
   </td> </tr>
   <tr> <td> Body </td> <td>
   <textarea name="body"></textarea>
   </td> </tr>
   <tr> <td> Url Name </td> <td>
   <input class="mmbase" type="text" name="name" size="28">
   </td> </tr>
   <tr> <td> Url </td> <td>
   <input class="mmbase" type="text" name="url" size="28">
   </td> </tr>
   <tr> <td> Description </td> <td>
   <textarea name="description"></textarea>
   </td> </tr> 
   <tr><td>&nbsp;</td><td><input type="submit" name="addproject" value="add"></td></tr>
   </table> </form> <%
   }

 %>


</td>

<%@include file="/includes/footer.jsp"%>
</mm:cloud>
