<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:import externid="type" />
<mm:import externid="scope">none</mm:import>
<%-- mmbob is only valid in the 'education' scope --%>
<mm:compare referid="scope" value="education">
  <mm:cloud loginpage="/login.jsp" jspvar="cloud">
  <%@include file="/shared/setImports.jsp" %>
  <%

    String bundleMMBob = null;

  %>

  <mm:write referid="lang_code" jspvar="sLangCode" vartype="String" write="false">

    <%

      bundleMMBob = "nl.didactor.component.mmbob.MMBobMessageBundle_" + sLangCode;

    %>

  </mm:write>

  <fmt:bundle basename="<%= bundleMMBob %>">
  <%@include file="/mmbob/check.jsp" %>
  <mm:import id="template" reset="true"><mm:treefile page="/mmbob/index.jsp" objectlist="$includePath" referids="$referids" /></mm:import>
  <mm:compare referid="type" value="div">
    <mm:present referid="class">
      <mm:node number="$class" notfound="skip">
        <div class="menuSeperator"> </div>
        <div class="menuItem" id="menuMMBob">
          <a href='<mm:write referid="template" escape="text/plain" />&forumid=<mm:write referid="classforum"/>' class="menubar"><fmt:message key="groupForum"/></a>
        </div>
      </mm:node>
    </mm:present>
    <mm:present referid="education">
      <mm:node number="$education" notfound="skip">
        <div class="menuSeperator"> </div>
        <div class="menuItem" id="menuMMBob">
          <a href='<mm:write referid="template" escape="text/plain" />&forumid=<mm:write referid="educationforum"/>' class="menubar"><fmt:message key="educationForum"/></a>
        </div>
      </mm:node>
    </mm:present>
  </mm:compare>
  
  <mm:compare referid="type" value="option">
    <mm:present referid="class">
      <mm:node number="$class" notfound="skip">
        <option value='<mm:write referid="template" escape="text/plain" />&forumid=<mm:write referid="classforum"/>'>
          <fmt:message key="groupForum"/>
        </option>
      </mm:node>
    </mm:present>
    <mm:present referid="education">
      <mm:node number="$education" notfound="skip">
        <option value='<mm:write referid="template" escape="text/plain" />&forumid=<mm:write referid="educationforum"/>'>
          <fmt:message key="educationForum"/>
        </option>
      </mm:node>
    </mm:present>
  </mm:compare>
  </fmt:bundle>
  </mm:cloud>
</mm:compare>
