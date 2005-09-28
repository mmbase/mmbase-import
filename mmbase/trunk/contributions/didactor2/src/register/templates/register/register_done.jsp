<%@page session="true" language="java" contentType="text/html; charset=UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@page import ="java.util.Locale" %>
<%
  Locale requestLocale = request.getLocale();
  Locale sessionLocale = new Locale(requestLocale.getLanguage(), (requestLocale.getCountry().length()==0 ? (requestLocale.getLanguage().equals("en") ? "GB" : requestLocale.getLanguage().toUpperCase()) : requestLocale.getCountry()));
  String localeString = sessionLocale.getLanguage() + "_" + sessionLocale.getCountry();
%>
<fmt:setLocale value="<%=localeString%>" scope="session" />
<fmt:setBundle basename="nl.didactor.component.core.CoreMessageBundle" scope="session" />
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<mm:content postprocessor="reducespace">
<mm:cloud jspvar="cloud">
  <%@include file="/shared/setImports.jsp" %>
  <mm:treeinclude page="/cockpit/cockpit_intro_header.jsp" objectlist="$includePath" referids="$referids">
   <mm:param name="extraheader"><style>.columns {height: 100%;}</style></mm:param>
  </mm:treeinclude>

  <mm:import externid="uname"/>
  <mm:import externid="password"/>
  <div class="columns">
    <div class="columnLeft">
    </div>
    <div class="columnMiddle">
      <p>
        Thank you for your registration. Your account details are:
        <ul>
          <li>Username: <mm:write referid="uname" /></li>
          <li>Password: <mm:write referid="password" /></li>
        </ul>
      </p>
    </div>
    <div class="columnRight">
    </div>
  </div>
  <mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />
</mm:cloud>
</mm:content>
