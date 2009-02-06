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
      <form method="post">
         <input type="hidden" name="action" value="add"/>
         <strong>1) Choose how you want the relations to be made.</strong>
         <ul>
            <li>If 'Relate to root channel' is chosen, the asset elements will be related to the channel node which is filled in the input text. If it's empty, the root of the repository will be used.</li>
            <li>If 'Self-determination' is chosen, the asset elements will be related to the channel node which relates to an article and the article relates to the asset.</li>
         </ul>
         <select name="type">
            <option value="root">Relate to root channel</option>
            <option value="selfselect">Self-determination</option>
         </select><br/><br/>
         <strong>2) If you have chosen 'Self-determination', you can select here how you want it to work. Otherwise skip this step.</strong><br/><br/>
         <select name="selfselectLogic">
            <option value="firstElem">Always relate to the first possible channel</option>
            <option value="rootWhenUndetermined">Pick ROOT when more then one channel to choose from</option>
         </select><br/><br/>
         <strong>3) Choose your root channel. If you leave this field blank repository.root will be chosen.</strong><br/><br/>
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
      <%-- firstElem --%>
      <c:if test="${param.selfselectLogic== 'firstElem'}">

         <mm:write referid="number" jspvar="number" vartype="Integer">
         <%
            if( !RepositoryUtil.isChannel(String.valueOf(number))) {
                     new CreateRelationsForSecondaryContent(cloud,pageContext).execute(number,"selfselect","firstElem");
                     out.println("Total Asset count : "+pageContext.getAttribute("totalCount")+"  added creationrel count : "+pageContext.getAttribute("addedRelationCount"));
            }
            else {
                  out.println("The number should be a channel number!");
               }
         %>
         </mm:write>
      </c:if>
      
      <%-- rootWhenUndetermined --%>
      <c:if test="${param.selfselectLogic=='rootWhenUndetermined'}">
         <mm:write referid="number" jspvar="number" vartype="Integer">
         <%
            if (!RepositoryUtil.isChannel(String.valueOf(number))) {
               new CreateRelationsForSecondaryContent(cloud,pageContext).execute(number,"selfselect","rootWhenUndetermined");
               out.println("Total Asset count : "+pageContext.getAttribute("totalCount")+"  added creationrel count : "+pageContext.getAttribute("addedRelationCount"));
            }
            else {
               out.println("The number should be a channel number!");
            }
         %>
      </mm:write>
      </c:if>
   </c:if>
</mm:present>

</mm:log>
</mm:cloud>
</body>
