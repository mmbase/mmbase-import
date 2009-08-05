<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">
  <mm:present referid="education">
    <mm:node referid="education" jspvar="node">
      <mm:relatednodes type="licensetexts" max="1">
        <%
        if (session.getAttribute(cloud.getUser().getIdentifier()+"_has_agreed_to_education_licence_" + node.getNumber()) == null) {
        %>
        <mm:redirect page="/drm/showlicense.jsp" referids="$referids "/>
        <%
        }
        %>
    </mm:relatednodes>
    </mm:node>
  </mm:present>
</mm:cloud>
</mm:content>
