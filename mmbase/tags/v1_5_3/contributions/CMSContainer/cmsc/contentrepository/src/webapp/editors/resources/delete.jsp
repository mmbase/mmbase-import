<%@page language="java" contentType="text/html;charset=utf-8"
%><%@include file="globals.jsp" 
%><mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="Delete">
   <style type="text/css">
   input { width: 100px;}
   </style>
</cmscedit:head>

<mm:import externid="objectnumber" required="true"/>
<mm:import externid="object_type" jspvar="object_type"  required="true"/>
<mm:cloud jspvar="cloud" rank="basic user" loginpage="../login.jsp">
<body>
   <mm:node referid="objectnumber">
   <mm:nodeinfo type="type" jspvar="nodeType" write="false" />
   <cmscedit:sideblock title="secondarydelete.${nodeType}.title" titleClass="side_block_green">
   <p><c:choose> 
         <c:when test="${nodeType eq 'images'}">
             <c:set var="object_type" scope="request">
                <c:out value="${(object_type != '' && object_type == 'imagesupload')?'imagesupload':'images' }"/>
             </c:set>
             <fmt:message key="secondarydelete.field.title" />: <b><mm:field name="title"/></b><br/>
            <fmt:message key="secondarydelete.field.filename" />: <b><mm:field name="filename"/></b>
         </c:when>
         <c:when test="${nodeType eq 'attachments'}">
            <c:set var="object_type" scope="request">
                <c:out value="${(object_type != '' && object_type == 'attachmentsupload')?'attachmentsupload':'attachments' }"/>
             </c:set>
             <fmt:message key="secondarydelete.field.title" />: <b><mm:field name="title"/></b><br/>
            <fmt:message key="secondarydelete.field.filename" />: <b><mm:field name="filename"/></b>
         </c:when>
         <c:when test="${nodeType eq 'urls'}">
             <c:set var="object_type" value="urls" scope="request"/>
             <fmt:message key="secondarydelete.field.name" />: <b><mm:field name="name"/></b><br/>
            <fmt:message key="secondarydelete.field.url" />: <b><mm:field name="url"/></b>
         </c:when>
       </c:choose>
      
   </p>
   <mm:relatednodes type="contentelement">
      <mm:first>
         <p><fmt:message key="secondarydelete.${nodeType}.linkedto" /><br/><br/>
      </mm:first>
      
      <img src="<cmsc:staticurl page='../gfx/bullet_menu_active.gif'/>"/>
      <mm:nodeinfo type="guitype"/>:<mm:field jspvar="title" name="title"/><br/>
      
      <mm:last>
         </p>
      </mm:last>
   </mm:relatednodes>
   
   <p>
      <fmt:message key="secondarydelete.${nodeType}.message" />
   </p>
   
   <form action="?">
      <html:hidden property="objectnumber" value="${objectnumber}" />
        <html:hidden property="object_type" value="${object_type}" />
         <html:submit property="remove"><fmt:message key="secondarydelete.yes" /></html:submit>&nbsp;
         <html:submit property="cancel"><fmt:message key="secondarydelete.no" /></html:submit>
   </form>
</cmscedit:sideblock>
</mm:node>
</body>
</mm:cloud>
</html:html>
</mm:content>