<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<mm:content>
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
  <mm:import externid="object_n">-1</mm:import>
  <mm:node referid="object_n" notfound="skip">
    <mm:nodeinfo id="type_of_node" type="type" jspvar="dummy" vartype="String">
<%
      if ("goals".equals(dummy) || "problems".equals(dummy)) {
%>
        <mm:related path="posrel,people" constraints="people.number=$user">
          <mm:deletenode number="$object_n" deleterelations="true"/>
        </mm:related>
<%
      }
%>

    </mm:nodeinfo>
  </mm:node>
  <mm:redirect page="/assessment/index.jsp" referids="$referids"/>

</mm:cloud>
</mm:content>
