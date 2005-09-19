<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@ include file="/shared/globalLang.jsp" %>
   <%

      String bundleMMBob = null;

   %>

   <mm:write referid="lang_code" jspvar="sLangCode" vartype="String" write="false">

      <%

         bundleMMBob = "nl.didactor.component.mmbob.MMBobMessageBundle_" + sLangCode;

      %>

   </mm:write>

<fmt:bundle basename="<%= bundleMMBob %>">
<mm:import externid="search_component"/>
<mm:compare referid="search_component" value="mmbob">
    <option value="mmbob" selected="selected"><fmt:message key="Forum" /></option>
</mm:compare>
<mm:compare referid="search_component" value="mmbob" inverse="true">
    <option value="mmbob"><fmt:message key="Forum" /></option>
</mm:compare>
</fmt:bundle>

