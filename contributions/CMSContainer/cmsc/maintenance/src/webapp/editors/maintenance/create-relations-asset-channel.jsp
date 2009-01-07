<%@page language="java" contentType="text/html;charset=UTF-8"
%><%@include file="globals.jsp"
%><%@ page import="com.finalist.cmsc.maintenance.beans.*,com.finalist.cmsc.repository.RepositoryUtil"
%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>Create relations from assets to channels</title>
<meta http-equiv="pragma" value="no-cache" />
<meta http-equiv="expires" value="0" />
<link href="../style.css" type="text/css" rel="stylesheet"/>
</head>
    <body>
       <h2>Create relations from assets to channels.</h2>
<mm:cloud jspvar="cloud" loginpage="../editors/login.jsp" rank="administrator">
<mm:log jspvar="log">
Note:<ul>
<li>if 'Relate to root channel' is choosed,the asset elements will be related to the channel node which you fill in the input text.if it's empty ,the root node will be used.</li>
<li>if 'Self-determination' is choosed,The asset elements will be related to the channel node which relates to a article and the article relates to the asset</li>

</ul>
<br/><br/>
<form method="post">
      <input type="hidden" name="action" value="add"/>
   <select name="type">
      <option value="root">Relate to root channel</option>
      <option value="selfselect">Self-determination</option>
   </select><br/><br/>
   Parent channel Node Number:<input type="text" name="number"/><br/><br/>
      <input type="submit" name="action" value="Create"/>
</form>

<mm:import externid="action"/>
<mm:import externid="number"/>
<mm:present referid="action">
   <c:if test="${param.type == 'root'}">
   <mm:write referid="number" jspvar="number" vartype="Integer">
      <% if(number < 1) {
        new CreateRelationsForSecondaryContent(cloud,pageContext).execute(number,null);
        out.println("Total Asset count : "+pageContext.getAttribute("totalCount")+"  added creationrel count : "+pageContext.getAttribute("addedRelationCount"));
      }
      else {
       if(RepositoryUtil.isChannel(String.valueOf(number))) {
         new CreateRelationsForSecondaryContent(cloud,pageContext).execute(number,null);
         out.println("Total Asset count : "+pageContext.getAttribute("totalCount")+"  added creationrel count : "+pageContext.getAttribute("addedRelationCount"));
       }
       else {
         out.println("The number should be a channel number!");
         }
      }
      %>
      </mm:write>
   </c:if>
   <c:if test="${param.type == 'selfselect'}">
       <mm:write referid="number" jspvar="number" vartype="Integer">
      <%
          new CreateRelationsForSecondaryContent(cloud,pageContext).execute(number,"selfselect");
          out.println("Total Asset count : "+pageContext.getAttribute("totalCount")+"  added creationrel count : "+pageContext.getAttribute("addedRelationCount"));
      %>
            </mm:write>
   </c:if>
</mm:present>

</mm:log>
</mm:cloud>
   </body>
