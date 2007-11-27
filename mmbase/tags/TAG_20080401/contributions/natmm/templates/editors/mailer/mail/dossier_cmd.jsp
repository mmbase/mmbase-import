<%@include file="/taglibs.jsp" %>
<mm:cloud logon="admin" pwd="<%= (String) com.finalist.mmbase.util.CloudFactory.getAdminUserCredentials().get("password") %>" method="pagelogon" jspvar="cloud">
<mm:import externid="user">-1</mm:import>
<mm:node number="dossiers" notfound="skip">
  <mm:relatednodes type="dossier" path="posrel,dossier" orderby="posrel.pos">
    <mm:field name="number" jspvar="dossier_number" vartype="String" write="false">
<%
      String sSelectFullName = "dossier" + dossier_number;
%>
      <mm:related path="posrel,persoon" constraints="persoon.number='$user'">
        <mm:node element="posrel">
          <mm:deletenode deleterelations="true" />
        </mm:node>
      </mm:related>
      <mm:import externid="<%= sSelectFullName %>">-1</mm:import>
      <mm:compare referid="<%= sSelectFullName %>" value="on">
        <mm:import id="this_dossier" reset="true"><%=dossier_number%></mm:import>
        <mm:createrelation role="posrel" source="this_dossier" destination="user"/>
      </mm:compare>
    </mm:field>
  </mm:relatednodes>
</mm:node>

<jsp:forward page="dossier.jsp"/>

</mm:cloud>
