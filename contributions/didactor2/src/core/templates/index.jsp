<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<mm:content postprocessor="reducespace" expires="0" type="text/html"
            escaper="entities">
  <mm:cloud authenticate="login">
    <jsp:directive.include file="/shared/setImports.jsp" />
    <mm:hasnode number="component.portal">
      <mm:redirect page="/portal" />
    </mm:hasnode>
    <mm:hasnode number="component.portal" inverse="true">
      <mm:treefile page="cockpit.jsp" objectlist="$includePath" referids="$referids">
        <mm:redirect page="${_}" />
      </mm:treefile>
    </mm:hasnode>
  </mm:cloud>
</mm:content>
