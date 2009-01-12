<%@page language="java" contentType="text/html;charset=UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Frameset//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd">
<%@include file="globals.jsp" %>
<mm:import externid="bottomurl" from="parameters">dashboard.jsp</mm:import>

<mm:cloud loginpage="login.jsp" rank="basic user">
   <mm:cloudinfo type="user" id="username" write="false"/>
   <mm:listnodes type="user" constraints="username='${username}'">
      <mm:field name="language" id="language" write="false"/>
      
      <c:if test="${empty language}">
         <c:set var="language" value='<%=request.getHeader ( "Accept-Language" )%>'/>
         <c:if test="${fn:length(language) > 2}">
            <c:set var="language" value="${fn:substring(language,0,2)}"/>
         </c:if>
         <mm:setfield name="language">${language}</mm:setfield>
      </c:if>
   </mm:listnodes>

   <fmt:setLocale value="${language}" scope="session"/>
   <fmt:setBundle basename="cmsc" scope="request" /> <%-- Reload the resource bundle again for JSTL --%>
   <mm:write referid="language" jspvar="lang" vartype="String">
      <% request.getSession().setAttribute("org.apache.struts.action.LOCALE", new Locale(lang));%>
   </mm:write>
</mm:cloud>

<%-- A cloud tag inside a locale tag will set the locale to the user cloud
 A local tag inside a cloud tag will only set the locale for its body
 This code sets the locale for all editors --%>
<mm:locale language="${language}">
   <mm:cloud loginpage="login.jsp" rank="basic user">
      <html:html xhtml="true">
         <head><title><fmt:message key="editors.title" /></title>
            <link rel="icon" href="<cmsc:staticurl page='/favicon.ico' />" type="image/x-icon" />
               <link rel="shortcut icon" href="<cmsc:staticurl page='/favicon.ico' />" type="image/x-icon" />
            <script type="text/javascript" src="editors.js"></script>
         </head>
         <mm:url page="topmenu.jsp" id="toppane" write="false">
            <mm:param name="bottomurl"><mm:write referid="bottomurl"/></mm:param>
         </mm:url>
         <mm:url page="${bottomurl}" id="bottompane" write="false"/>
         <frameset rows="75,*,46" framespacing="0" border="0">
            <frame src="<mm:url referid="toppane"/>" name="toppane" frameborder="0" scrolling="no" noresize="noresize" style="border: 0px" />
            <frame src="<mm:url referid="bottompane"/>" name="bottompane" frameborder="0" scrolling="auto" onload="initMenu();"/>
            <frame src="footer.jsp" name="footerpane" frameborder="0" scrolling="no"/>
         </frameset>
      </html:html>
   </mm:cloud>
</mm:locale>

