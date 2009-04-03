<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<mm:content postprocessor="reducespace">
  <mm:cloud method="delegate">
    <mm:import externid="learnobject" required="true"/>
    <!-- TODO Need this page? -->

    <jsp:directive.include file="/shared/setImports.jsp" />
    <mm:node number="$learnobject">
      <mm:field name="showtitle">
        <mm:compare value="1">
          <h2><mm:field name="name"/></h2>
        </mm:compare>
      </mm:field>
      <mm:field name="intro"/>

      <p/><%-- empty paragraph? --%>

      <mm:nodeinfo type="type">
        <a href="${_}.jsp">Test</a>
      </mm:nodeinfo>
    </mm:node>
  </mm:cloud>
</mm:content>

