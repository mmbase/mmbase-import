 <%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
         <p>
          <h1><fmt:message key="WELCOME" /></h1>
          </p>
          <br />
          <p>
            <h3>Bij Didactor, de elektronische leeromgeving.</h3>
          </p>
          <br />
          <p>
            Didactor versie 2.0 Beta
          </p>
</mm:cloud>
</mm:content>
